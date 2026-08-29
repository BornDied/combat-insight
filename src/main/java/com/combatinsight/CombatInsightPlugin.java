package com.combatinsight;

import com.combatinsight.calculation.HudDisplayDuration;
import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetDefenceDisplay;
import com.combatinsight.calculation.TargetMagicDisplay;
import com.combatinsight.live.LiveCombatSnapshot;
import com.combatinsight.live.LiveTargetTracker;
import com.combatinsight.live.ObservedHitTracker;
import com.combatinsight.live.CombatDummy;
import com.combatinsight.live.SpecialAttackTracker;
import com.combatinsight.live.TargetEffectSnapshot;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Locale;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HitsplatID;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
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

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private SkillIconManager skillIconManager;

	private volatile LiveCombatSnapshot snapshot = LiveCombatSnapshot.empty();
	private volatile long lastCombatAt;
	private volatile long maxHitChangedAt;
	private volatile boolean maxHitIncreased;
	private TargetDefenceInfoBox targetDefenceInfoBox;
	private TargetMagicInfoBox targetMagicInfoBox;
	private boolean captureFailureLogged;
	private CombatDummy selectedCombatDummy;
	private NPC selectedCombatDummyActor;
	private final LiveTargetTracker targetTracker = new LiveTargetTracker();
	private final ObservedHitTracker observedHitTracker = new ObservedHitTracker();
	private final SpecialAttackTracker specialAttackTracker = new SpecialAttackTracker();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		specialAttackTracker.resetEnergy(client.getVarpValue(VarPlayerID.SA_ENERGY));
		refreshSnapshot();
		log.debug("Combat Insight started");
	}

	@Override
	protected void shutDown()
	{
		removeTargetInfoBoxes();
		overlayManager.remove(overlay);
		snapshot = LiveCombatSnapshot.empty();
		targetTracker.clear();
		clearCombatDummyTarget();
		observedHitTracker.clear();
		specialAttackTracker.clear();
		lastCombatAt = 0L;
		maxHitChangedAt = 0L;
		log.debug("Combat Insight stopped");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		specialAttackTracker.onGameTick(client.getTickCount());
		LiveCombatSnapshot previous = snapshot;
		NPC activeTarget = targetTracker.getActiveTarget();
		if (activeTarget != null && activeTarget.isDead())
		{
			removeTargetInfoBoxesIfSame(activeTarget);
			specialAttackTracker.remove(activeTarget);
		}
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
			removeTargetInfoBoxes();
			snapshot = LiveCombatSnapshot.empty();
			targetTracker.clear();
			clearCombatDummyTarget();
			observedHitTracker.clear();
			specialAttackTracker.clear();
			lastCombatAt = 0L;
			return;
		}

		specialAttackTracker.resetEnergy(client.getVarpValue(VarPlayerID.SA_ENERGY));
		refreshSnapshot();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!"combatinsight".equals(event.getGroup()))
		{
			return;
		}
		if ("targetDefenceDisplay".equals(event.getKey())
			|| "targetMagicDisplay".equals(event.getKey())
			|| "animateChanges".equals(event.getKey())
			|| "targetDefenceTextColor".equals(event.getKey())
			|| "targetDefenceFlashColor".equals(event.getKey())
			|| "targetMagicTextColor".equals(event.getKey())
			|| "targetMagicFlashColor".equals(event.getKey()))
		{
			refreshSnapshot();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayerID.SA_ENERGY)
		{
			return;
		}

		NPC target = targetTracker.getActiveTarget();
		Player player = client.getLocalPlayer();
		if (target == null && player != null && player.getInteracting() instanceof NPC)
		{
			target = (NPC) player.getInteracting();
		}
		if (target != null && CombatDummy.forNpcId(target.getId()) != null)
		{
			target = null;
		}
		SpecialAttackWeapon weapon = SpecialAttackWeapon.forWeapon(snapshot.getWeaponOrSpellName());
		specialAttackTracker.onEnergyChanged(
			event.getValue(),
			weapon,
			target,
			client.getTickCount());
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		MenuEntry menuEntry = event.getMenuEntry();
		if (menuEntry == null || !isCombatMenuOption(menuEntry.getOption()))
		{
			return;
		}

		NPC npc = menuEntry.getNpc();
		if (npc != null)
		{
			CombatDummy combatDummy = resolveCombatDummy(npc.getId(), -1);
			if (combatDummy != null)
			{
				selectCombatDummy(combatDummy, npc);
			}
			else
			{
				confirmCombatTarget(npc);
			}
			return;
		}

		if ("Attack".equalsIgnoreCase(menuEntry.getOption().trim()))
		{
			CombatDummy combatDummy = resolveCombatDummy(-1, menuEntry.getIdentifier());
			if (combatDummy != null)
			{
				selectCombatDummy(combatDummy, null);
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() instanceof NPC
			&& event.getHitsplat() != null
			&& event.getHitsplat().isMine())
		{
			NPC combatTarget = (NPC) event.getActor();
			CombatDummy combatDummy = CombatDummy.forNpcId(combatTarget.getId());
			if (combatDummy != null)
			{
				selectCombatDummy(combatDummy, combatTarget);
				return;
			}
			confirmCombatTarget(combatTarget);
			observedHitTracker.record(combatTarget, event.getHitsplat().getAmount());
			if (specialAttackTracker.onHitsplat(
				combatTarget,
				event.getHitsplat().getAmount(),
				event.getHitsplat().getHitsplatType() != HitsplatID.BLOCK_ME,
				client.getTickCount()))
			{
				refreshSnapshot();
			}
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (event.getActor() instanceof NPC)
		{
			NPC target = (NPC) event.getActor();
			removeTargetInfoBoxesIfSame(target);
			specialAttackTracker.remove(target);
		}
		if (targetTracker.endActiveIfSame(event.getActor()))
		{
			markCombatActivity();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc() == selectedCombatDummyActor && clearCombatDummyTarget())
		{
			refreshSnapshot();
		}
		removeTargetInfoBoxesIfSame(event.getNpc());
		specialAttackTracker.remove(event.getNpc());
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
			TargetEffectSnapshot targetEffects = specialAttackTracker.getTargetEffects(
				activeTarget,
				client.getTickCount());
			LiveCombatSnapshot captured = LiveCombatSnapshot.capture(
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
				activeTarget == null ? -1 : activeTarget.getHealthScale(),
				targetEffects,
				observedHitTracker.hasSamples(),
				selectedCombatDummy);
			snapshot = captured;
			updateTargetDefenceInfoBox(activeTarget, targetEffects);
			updateTargetMagicInfoBox(activeTarget, targetEffects, captured);
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

	private void updateTargetDefenceInfoBox(
		NPC activeTarget,
		TargetEffectSnapshot targetEffects)
	{
		TargetDefenceDisplay display = config.targetDefenceDisplay();
		if (display == null || !display.showsInfoBox()
			|| activeTarget == null || !targetEffects.hasTargetStats())
		{
			removeTargetDefenceInfoBox();
			return;
		}

		if (targetDefenceInfoBox == null)
		{
			targetDefenceInfoBox = new TargetDefenceInfoBox(
				skillIconManager.getSkillImage(Skill.DEFENCE),
				this);
			infoBoxManager.addInfoBox(targetDefenceInfoBox);
		}
		targetDefenceInfoBox.update(
			activeTarget,
			activeTarget.getName(),
			targetEffects.getCurrentDefence(),
			targetEffects.getBaseDefence(),
			targetEffects.hasTrackedEffect(),
			targetEffects.isEstimatedRecovery(),
			config.animateChanges(),
			config.targetDefenceTextColor(),
			config.targetDefenceFlashColor(),
			System.currentTimeMillis());
	}

	private void removeTargetDefenceInfoBoxIfSame(NPC target)
	{
		if (targetDefenceInfoBox != null && targetDefenceInfoBox.appliesTo(target))
		{
			removeTargetDefenceInfoBox();
		}
	}

	private void removeTargetDefenceInfoBox()
	{
		if (targetDefenceInfoBox == null)
		{
			return;
		}
		infoBoxManager.removeInfoBox(targetDefenceInfoBox);
		targetDefenceInfoBox = null;
	}

	private void updateTargetMagicInfoBox(
		NPC activeTarget,
		TargetEffectSnapshot targetEffects,
		LiveCombatSnapshot captured)
	{
		TargetMagicDisplay display = config.targetMagicDisplay();
		if (display == null || !display.showsInfoBox()
			|| activeTarget == null || captured.getCombatStyle() != CombatStyle.MAGIC
			|| !targetEffects.hasTargetStats())
		{
			removeTargetMagicInfoBox();
			return;
		}

		if (targetMagicInfoBox == null)
		{
			targetMagicInfoBox = new TargetMagicInfoBox(
				skillIconManager.getSkillImage(Skill.MAGIC),
				this);
			infoBoxManager.addInfoBox(targetMagicInfoBox);
		}
		targetMagicInfoBox.update(
			activeTarget,
			activeTarget.getName(),
			targetEffects.displaysMagicDefenceBonus()
				? targetEffects.getCurrentMagicDefence()
				: targetEffects.getCurrentMagic(),
			targetEffects.displaysMagicDefenceBonus()
				? targetEffects.getBaseMagicDefence()
				: targetEffects.getBaseMagic(),
			targetEffects.displaysMagicDefenceBonus()
				? "Magic Defence bonus"
				: "Magic level",
			targetEffects.displaysMagicDefenceBonus(),
			targetEffects.isEstimatedRecovery(),
			config.animateChanges(),
			config.targetMagicTextColor(),
			config.targetMagicFlashColor(),
			System.currentTimeMillis());
	}

	private void removeTargetInfoBoxesIfSame(NPC target)
	{
		removeTargetDefenceInfoBoxIfSame(target);
		if (targetMagicInfoBox != null && targetMagicInfoBox.appliesTo(target))
		{
			removeTargetMagicInfoBox();
		}
	}

	private void removeTargetInfoBoxes()
	{
		removeTargetDefenceInfoBox();
		removeTargetMagicInfoBox();
	}

	private void removeTargetMagicInfoBox()
	{
		if (targetMagicInfoBox == null)
		{
			return;
		}
		infoBoxManager.removeInfoBox(targetMagicInfoBox);
		targetMagicInfoBox = null;
	}

	private void confirmCombatTarget(NPC target)
	{
		if (target == null)
		{
			return;
		}

		boolean dummyChanged = clearCombatDummyTarget();
		boolean changed = targetTracker.confirmCombatTarget(target);
		observedHitTracker.selectTarget(target);
		markCombatActivity();
		if (changed || dummyChanged)
		{
			refreshSnapshot();
		}
	}

	private void selectCombatDummy(CombatDummy combatDummy, NPC actor)
	{
		if (combatDummy == null)
		{
			return;
		}

		boolean changed = selectedCombatDummy != combatDummy
			|| selectedCombatDummyActor != actor;
		selectedCombatDummy = combatDummy;
		selectedCombatDummyActor = actor;
		targetTracker.clear();
		observedHitTracker.clear();
		specialAttackTracker.clear();
		specialAttackTracker.resetEnergy(client.getVarpValue(VarPlayerID.SA_ENERGY));
		removeTargetInfoBoxes();
		markCombatActivity();
		if (changed)
		{
			refreshSnapshot();
		}
	}

	private boolean clearCombatDummyTarget()
	{
		boolean changed = selectedCombatDummy != null || selectedCombatDummyActor != null;
		selectedCombatDummy = null;
		selectedCombatDummyActor = null;
		return changed;
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

	static CombatDummy resolveCombatDummy(int npcId, int objectId)
	{
		CombatDummy npcDummy = CombatDummy.forNpcId(npcId);
		return npcDummy != null ? npcDummy : CombatDummy.forObjectId(objectId);
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
