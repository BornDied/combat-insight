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
import com.combatinsight.live.RaidScaling;
import com.combatinsight.live.EncounterContext;
import com.combatinsight.live.ArchiveStateTracker;
import com.combatinsight.live.RaidInputResolver;
import com.combatinsight.live.TheatrePartyTracker;
import net.runelite.api.gameval.VarbitID;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import java.awt.datatransfer.StringSelection;
import javax.swing.SwingUtilities;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.ChatMessageType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.OverlayMenuClicked;
import com.combatinsight.calculation.HudDisplayMode;
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
	description = "Live max hit, hit chance and DPS for your gear and target, with special attack previews"
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

    @Inject private KeyManager keyManager;
    @Inject private ClientThread clientThread;
    private final HeldDetails heldDetails = new HeldDetails();
    private final HotkeyListener detailsListener = new HotkeyListener(() -> config.holdDetailsKey())
    {
        @Override public void hotkeyPressed() { heldDetails.press(); }
        @Override public void hotkeyReleased() { heldDetails.clear(); }
        @Override public void focusLost() { super.focusLost(); heldDetails.clear(); }
    };

    @Inject private ClientToolbar clientToolbar;
    private CalculationInspectorPanel inspector;
    private NavigationButton inspectorNavigation;
    private javax.swing.Timer inspectorTimer;
    private volatile boolean pluginRunning;
    private volatile boolean inspectorEnabled;

    private void syncInspector()
    {
        inspectorEnabled = config.enableInspector();
        SwingUtilities.invokeLater(() -> {
            if (!pluginRunning || !inspectorEnabled)
            {
                if (inspectorTimer != null) inspectorTimer.stop();
                if (inspectorNavigation != null) clientToolbar.removeNavigation(inspectorNavigation);
                if (inspector != null) inspector.clear();
                inspector=null; inspectorNavigation=null; inspectorTimer=null;
                return;
            }
            if (inspector != null) return;
            inspector=new CalculationInspectorPanel(this::copyDiagnostic);
            BufferedImage icon=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g=icon.createGraphics();
            g.setColor(new Color(66,217,255)); g.drawOval(1,1,9,9); g.drawLine(10,10,15,15); g.dispose();
            inspectorNavigation=NavigationButton.builder().tooltip("Combat Insight inspector")
                .icon(icon).priority(7).panel(inspector).build();
            clientToolbar.addNavigation(inspectorNavigation);
            inspectorTimer=new javax.swing.Timer(600, e -> {
                if (inspector != null && inspector.isActive())
                    inspector.update(InspectorDocument.capture(snapshot));
            });
            inspectorTimer.start();
        });
    }

    private void openInspector()
    {
        if (!config.enableInspector()) return;
        SwingUtilities.invokeLater(() -> {
            if (inspector != null && pluginRunning && inspectorEnabled)
            {
                inspector.update(InspectorDocument.capture(snapshot));
                clientToolbar.openPanel(inspectorNavigation);
            }
        });
    }

    boolean isPreviewVisible()
    {
        return config.previewHud() && snapshot.isLoggedIn()
            && (lastCombatAt == 0L || System.currentTimeMillis()-lastCombatAt > 10_000L);
    }

    HudDisplayMode effectiveHudMode(HudDisplayMode configured) { return heldDetails.resolve(configured); }

    @Subscribe public void onFocusChanged(FocusChanged event)
    {
        if (!event.isFocused()) heldDetails.clear();
    }

    @Subscribe public void onOverlayMenuClicked(OverlayMenuClicked event)
    {
        if (event.getOverlay() != overlay) return;
        if ("Inspect calculation".equals(event.getEntry().getOption()))
        {
            openInspector(); return;
        }
        if ("Clear tracked reductions".equals(event.getEntry().getOption()))
        {
            clientThread.invokeLater(() -> {
                specialAttackTracker.clearTrackedEffects();
                refreshSnapshot();
                diagnosticFeedback("Tracked reductions cleared; target stats reset to base.");
            });
            return;
        }
        if (!"Copy diagnostic snapshot".equals(event.getEntry().getOption())) return;
        // Read exactly one immutable snapshot; do not query the client on the AWT thread.
        copyDiagnostic(snapshot.diagnosticReport());
    }

    private void copyDiagnostic(String report)
    {
        SwingUtilities.invokeLater(() -> {
            try
            {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(report), null);
                diagnosticFeedback("Diagnostic snapshot copied.");
            }
            catch (IllegalStateException | java.awt.HeadlessException | SecurityException ex)
            { log.warn("Combat Insight could not copy the diagnostic snapshot", ex); diagnosticFeedback("Clipboard unavailable; try again."); }
        });
    }

    private void diagnosticFeedback(String message)
    {
        clientThread.invokeLater(() -> {
            if (client.getGameState()==GameState.LOGGED_IN)
                client.addChatMessage(ChatMessageType.GAMEMESSAGE,"","Combat Insight: " + message,"");
        });
    }

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
    private final TheatrePartyTracker theatrePartyTracker = new TheatrePartyTracker();
    private final ArchiveStateTracker archiveStateTracker = new ArchiveStateTracker();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
        pluginRunning=true;
        syncInspector();
        keyManager.registerKeyListener(detailsListener);
		specialAttackTracker.resetEnergy(client.getVarpValue(VarPlayerID.SA_ENERGY));
		refreshSnapshot();
		log.debug("Combat Insight started");
	}

	@Override
	protected void shutDown()
	{
        pluginRunning=false;
        syncInspector();
		removeTargetInfoBoxes();
        keyManager.unregisterKeyListener(detailsListener);
        heldDetails.clear();
		overlayManager.remove(overlay);
		snapshot = LiveCombatSnapshot.empty();
		targetTracker.clear();
		clearCombatDummyTarget();
		observedHitTracker.clear();
		specialAttackTracker.clear();
            archiveStateTracker.clear();
            theatrePartyTracker.clear();
		lastCombatAt = 0L;
		maxHitChangedAt = 0L;
		log.debug("Combat Insight stopped");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
        archiveStateTracker.observeRoom(client);
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
            heldDetails.clear();
			removeTargetInfoBoxes();
			snapshot = LiveCombatSnapshot.empty();
			targetTracker.clear();
			clearCombatDummyTarget();
			observedHitTracker.clear();
			specialAttackTracker.clear();
            if(event.getGameState()==GameState.LOADING)archiveStateTracker.clearActors();
            else archiveStateTracker.clear();
            if (event.getGameState() != GameState.LOADING) theatrePartyTracker.clear();
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
		heldDetails.clear();
        if ("enableInspector".equals(event.getKey())) syncInspector();
        if ("dt2EncounterMode".equals(event.getKey()) || "defenceTrackingMode".equals(event.getKey())
            || "toaPartySize".equals(event.getKey()) || "toaPathLevel".equals(event.getKey())
            || "toaInvocationLevel".equals(event.getKey()) || "archivePartySize".equals(event.getKey()))
            clientThread.invokeLater(this::refreshSnapshot);
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

		specialAttackTracker.setTrackingMode(config.defenceTrackingMode());
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
            archiveStateTracker.onDeath(client,target);
            archiveStateTracker.remove(target);
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
		archiveStateTracker.remove(event.getNpc());
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
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            snapshot = LiveCombatSnapshot.empty();
            return;
        }
		try
		{
			RaidScaling raidScaling = new RaidScaling(config.coxPartySize(), config.coxChallengeMode(),
				config.coxHighestCombat(), config.coxHighestHitpoints(), config.coxAverageMining(), config.tobPartySize())
                .withTombsInputs(config.toaPartySize(),config.toaPathLevel());
            raidScaling = RaidInputResolver.resolve(raidScaling, config.automaticCoxInputs(),
                client.getVarbitValue(VarbitID.RAIDS_CLIENT_INDUNGEON)==1,
                client.getVarbitValue(VarbitID.RAIDS_CLIENT_PARTYSIZE_SCALED),
                client.getVarbitValue(VarbitID.RAIDS_CHALLENGE_MODE),
                client.getVarbitValue(VarbitID.RAIDS_CLIENT_HIGHESTCOMBAT));
			int observedTheatreSize = theatrePartyTracker.observe(config.automaticTobInputs(),
                client.getGameState() == GameState.LOGGED_IN ? client.getVarbitValue(VarbitID.TOB_CLIENT_PARTYSTATUS) : 0,
                new int[]{client.getVarbitValue(VarbitID.TOB_CLIENT_P0), client.getVarbitValue(VarbitID.TOB_CLIENT_P1),
                    client.getVarbitValue(VarbitID.TOB_CLIENT_P2), client.getVarbitValue(VarbitID.TOB_CLIENT_P3),
                    client.getVarbitValue(VarbitID.TOB_CLIENT_P4)});
            raidScaling = raidScaling.withObservedTheatre(observedTheatreSize);
            specialAttackTracker.setTrackingMode(config.defenceTrackingMode());
			specialAttackTracker.setRaidScaling(raidScaling);
            specialAttackTracker.setEncounterMode(config.dt2EncounterMode());
            targetTracker.refreshActiveIdentity();
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
				selectedCombatDummy,
				raidScaling, archiveStateTracker.apply(activeTarget, EncounterContext.from(client, activeTarget, config.dt2EncounterMode())).withArchivePartySize(config.archivePartySize()), config.kandarinHardDiary(), config.defenceTrackingMode());
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
            || captured.getCurrentTargetMagicDefenceRoll() < 0
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
            archiveStateTracker.clear();
            theatrePartyTracker.clear();
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
