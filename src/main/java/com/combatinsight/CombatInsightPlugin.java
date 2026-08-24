package com.combatinsight;

import com.combatinsight.calculation.HudDisplayDuration;
import com.combatinsight.live.LiveCombatSnapshot;
import com.combatinsight.live.LiveTargetTracker;
import com.combatinsight.live.ObservedHitTracker;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Locale;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Combat Insight",
	description = "Live OSRS max hit, hit chance, and target-aware DPS"
)
public class CombatInsightPlugin extends Plugin
{
	private static final long MAX_HIT_ANIMATION_MILLIS = 1200L;
	private static final Logger log = LoggerFactory.getLogger(CombatInsightPlugin.class);

	@Inject
	private Client client;

	@Inject
	private CombatInsightConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CombatInsightOverlay overlay;

	private volatile LiveCombatSnapshot snapshot = LiveCombatSnapshot.empty();
	private volatile long lastCombatAt;
	private volatile long maxHitChangedAt;
	private volatile boolean maxHitIncreased;
	private boolean captureFailureLogged;
	private final LiveTargetTracker targetTracker = new LiveTargetTracker();
	private final ObservedHitTracker observedHitTracker = new ObservedHitTracker();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		refreshSnapshot();
		log.debug("Combat Insight started");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		snapshot = LiveCombatSnapshot.empty();
		targetTracker.clear();
		observedHitTracker.clear();
		lastCombatAt = 0L;
		maxHitChangedAt = 0L;
		log.debug("Combat Insight stopped");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		LiveCombatSnapshot previous = snapshot;
		if (targetTracker.endActiveIfDead())
		{
			markCombatActivity();
		}
		refreshSnapshot();

		LiveCombatSnapshot current = snapshot;
		if (current.isLoggedIn() && isPlayerEngagedWithConfirmedTarget())
		{
			markCombatActivity();
		}

		if (previous.isLoggedIn() && current.isLoggedIn()
			&& previous.isMaxHitAvailable() && current.isMaxHitAvailable()
			&& !previous.isTargetImmune() && !current.isTargetImmune()
			&& current.getMaxHit() != previous.getMaxHit())
		{
			maxHitChangedAt = System.currentTimeMillis();
			maxHitIncreased = current.getMaxHit() > previous.getMaxHit();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			snapshot = LiveCombatSnapshot.empty();
			targetTracker.clear();
			observedHitTracker.clear();
			lastCombatAt = 0L;
			return;
		}

		refreshSnapshot();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		MenuEntry menuEntry = event.getMenuEntry();
		if (menuEntry == null || !isCombatMenuOption(menuEntry.getOption()))
		{
			return;
		}

		confirmCombatTarget(menuEntry.getNpc());
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() instanceof NPC
			&& event.getHitsplat() != null
			&& event.getHitsplat().isMine())
		{
			NPC combatTarget = (NPC) event.getActor();
			confirmCombatTarget(combatTarget);
			observedHitTracker.record(combatTarget, event.getHitsplat().getAmount());
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (targetTracker.endActiveIfSame(event.getActor()))
		{
			markCombatActivity();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (targetTracker.endActiveIfSame(event.getNpc()))
		{
			markCombatActivity();
		}
	}

	LiveCombatSnapshot getSnapshot()
	{
		return snapshot;
	}

	String getObservedAverageHitText()
	{
		if (!observedHitTracker.hasSamples())
		{
			return "Waiting for hits";
		}

		int hitCount = observedHitTracker.getHitCount();
		return String.format(
			Locale.ROOT,
			"%.2f (%d %s)",
			observedHitTracker.getAverageHit(),
			hitCount,
			hitCount == 1 ? "hit" : "hits");
	}

	boolean hasObservedHits()
	{
		return observedHitTracker.hasSamples();
	}

	/**
	 * Returns whether the overlay should remain visible for the configured
	 * duration. Permanent mode is useful for a traditional max-hit HUD, while
	 * the timed modes keep the game screen clear when the player is idle.
	 */
	boolean isHudVisible()
	{
		if (!snapshot.isLoggedIn())
		{
			return false;
		}

		HudDisplayDuration duration = config.displayDuration();
		if (duration == HudDisplayDuration.PERMANENT)
		{
			return true;
		}

		long durationMillis = duration == HudDisplayDuration.FIFTEEN_SECONDS
			? 15_000L
			: 30_000L;
		return lastCombatAt > 0L && System.currentTimeMillis() - lastCombatAt <= durationMillis;
	}

	/**
	 * Supplies a short-lived color pulse for a max-hit change. Returning white
	 * outside the animation window keeps the normal HUD appearance unchanged.
	 */
	Color getMaxHitChangeColor()
	{
		if (!config.animateChanges() || maxHitChangedAt == 0L)
		{
			return Color.WHITE;
		}

		long elapsed = System.currentTimeMillis() - maxHitChangedAt;
		if (elapsed < 0L || elapsed >= MAX_HIT_ANIMATION_MILLIS)
		{
			return Color.WHITE;
		}

		double intensity = 1.0 - (double) elapsed / MAX_HIT_ANIMATION_MILLIS;
		Color target = maxHitIncreased ? new Color(100, 235, 130) : new Color(255, 105, 105);
		return blend(Color.WHITE, target, intensity);
	}

	private void refreshSnapshot()
	{
		try
		{
			NPC activeTarget = targetTracker.getActiveTarget();
			snapshot = LiveCombatSnapshot.capture(
				client,
				itemManager,
				config.combatStyleOverride(),
				config.blowpipeDart(),
				config.magicSpell(),
				config.applySlayerBonus(),
				config.toaInvocationLevel(),
				targetTracker.getRetainedTargetId(),
				targetTracker.getRetainedTargetName(),
				activeTarget == null ? -1 : activeTarget.getHealthRatio(),
				activeTarget == null ? -1 : activeTarget.getHealthScale());
			captureFailureLogged = false;
		}
		catch (RuntimeException ex)
		{
			if (!captureFailureLogged)
			{
				log.warn("Combat Insight could not read the current combat snapshot yet", ex);
				captureFailureLogged = true;
			}
		}
	}

	private void confirmCombatTarget(NPC target)
	{
		if (target == null)
		{
			return;
		}

		boolean changed = targetTracker.confirmCombatTarget(target);
		observedHitTracker.selectTarget(target);
		markCombatActivity();
		if (changed)
		{
			refreshSnapshot();
		}
	}

	private boolean isPlayerEngagedWithConfirmedTarget()
	{
		Player player = client.getLocalPlayer();
		return player != null && targetTracker.isEngagedWith(player.getInteracting());
	}

	private void markCombatActivity()
	{
		lastCombatAt = System.currentTimeMillis();
	}

	static boolean isCombatMenuOption(String option)
	{
		if (option == null)
		{
			return false;
		}

		String normalized = option.trim();
		return "Attack".equalsIgnoreCase(normalized)
			|| "Cast".equalsIgnoreCase(normalized);
	}

	private static Color blend(Color from, Color to, double amount)
	{
		int red = (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * amount);
		int green = (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * amount);
		int blue = (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * amount);
		return new Color(red, green, blue);
	}

	@Provides
	CombatInsightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CombatInsightConfig.class);
	}
}
