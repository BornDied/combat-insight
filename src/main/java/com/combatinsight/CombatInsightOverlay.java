package com.combatinsight;

import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.TargetDefenceDisplay;
import com.combatinsight.calculation.TargetMagicDisplay;
import com.combatinsight.live.LiveCombatSnapshot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

@Singleton
public class CombatInsightOverlay extends OverlayPanel
{
	private static final Color ACCENT = new Color(114, 190, 255);
	private static final Color MUTED = new Color(175, 175, 175);
	private static final Color SUCCESS = new Color(120, 220, 140);
	private static final Color WARNING = new Color(255, 190, 80);

	private final CombatInsightPlugin plugin;
	private final CombatInsightConfig config;

	@Inject
	private CombatInsightOverlay(CombatInsightPlugin plugin, CombatInsightConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGH);
		setMovable(true);
		setResettable(true);
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Copy diagnostic snapshot", "Combat Insight"));
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Clear tracked reductions", "Combat Insight"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();
        getMenuEntries().removeIf(entry -> "Inspect calculation".equals(entry.getOption()));
        if (config.enableInspector())
            getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Inspect calculation", "Combat Insight"));
        boolean preview = plugin.isPreviewVisible();
		if (!config.showHud() || (!preview && !plugin.isHudVisible()))
		{
			return null;
		}

		LiveCombatSnapshot snapshot = plugin.getSnapshot();
		HudDisplayMode mode = plugin.effectiveHudMode(config.displayMode());
		boolean compact = config.compactHud();
		panelComponent.setBackgroundColor(config.hudBackgroundMode().resolve(config.backgroundColor()));
		int width = mode == HudDisplayMode.MINIMAL ? (compact ? 145 : 155)
			: mode == HudDisplayMode.ADVANCED ? (compact ? 250 : 280) : (compact ? 190 : 225);
		// OverlayPanel applies the user's resize, when present, on top of this default.
		panelComponent.setPreferredSize(new Dimension(width, 0));
		panelComponent.setBorder(compact ? new Rectangle(4, 3, 4, 3) : new Rectangle(6, 6, 6, 6));

        if (preview)
        {
            renderPreview(mode);
            return super.render(graphics);
        }

        if (config.showManualIndicator() && snapshot.hasActiveManualOverride())
            addLine("Manual", "", MUTED, MUTED);

		if (config.showTitle())
		{
			addLine("Combat Insight", snapshot.getCombatStyleName(), ACCENT, routineColor(SUCCESS));
		}

		if (config.showMaxHit())
		{
			addLine(
				config.showInputMarker() && snapshot.hasInputNotices() ? "Max hit *" : "Max hit",
				snapshot.getMaxHitText(),
				Color.WHITE,
				snapshot.isTargetImmune() ? WARNING
					: snapshot.isMaxHitAvailable() ? plugin.getMaxHitChangeColor() : WARNING);
		}

		if (mode == HudDisplayMode.ADVANCED
			&& config.showMaxSplit()
			&& snapshot.hasMultiHitSplit())
		{
			addLine("Max split", snapshot.getMultiHitSplitText(), Color.WHITE, routineColor(ACCENT));
		}

		if (mode == HudDisplayMode.MINIMAL)
		{
			return super.render(graphics);
		}

		if (shouldShowDefenceBasedRow(config.showAccuracy(), snapshot.isCombatDummy()))
		{
			addLine(
				"Hit chance",
				snapshot.getHitChanceText(),
				Color.WHITE,
				snapshot.isAccuracyAvailable() ? routineColor(SUCCESS) : snapshot.hasTarget() ? WARNING : MUTED);
		}

		if (shouldShowDefenceBasedRow(config.showDps(), snapshot.isCombatDummy()))
		{
			addLine(
				"DPS",
				snapshot.getDpsText(),
				Color.WHITE,
				snapshot.isDpsAvailable() ? routineColor(SUCCESS) : snapshot.hasTarget() ? WARNING : MUTED);
		}

		if (snapshot.hasSupportedSpecialAttack())
		{
            if (config.specDivider() && (config.showSpecMax()
                || shouldShowDefenceBasedRow(config.showSpecChance(), snapshot.isCombatDummy())))
                panelComponent.getChildren().add(new HudDivider());
			if (config.showSpecMax())
			{
				addLine(
					"Spec max",
					snapshot.getSpecialMaximumText(mode == HudDisplayMode.ADVANCED),
                    HudPresentation.specialColor(snapshot.getSpecialAttackWeapon(), snapshot.isSpecialMaximumAvailable(),
                        config.highlightSpecialAttack(), config.specialHighlightColor(), Color.WHITE),
                    HudPresentation.specialColor(snapshot.getSpecialAttackWeapon(), snapshot.isSpecialMaximumAvailable(),
                        config.highlightSpecialAttack(), config.specialHighlightColor(),
                        snapshot.isSpecialMaximumAvailable() ? routineColor(ACCENT) : WARNING));
			}
			if (shouldShowDefenceBasedRow(config.showSpecChance(), snapshot.isCombatDummy()))
			{
				addLine(
					"Spec chance",
					snapshot.getSpecialHitChanceText(),
					Color.WHITE,
					snapshot.isSpecialAccuracyAvailable() ? routineColor(SUCCESS) : snapshot.hasTarget() ? WARNING : MUTED);
			}
		}

		if (config.showStyle())
		{
			addLine(
				"Combat style",
				snapshot.getCombatStyleName() + " | " + snapshot.getStyleName(),
				Color.WHITE,
				Color.WHITE);
		}

		if (config.showWeapon())
		{
			addLine("Weapon / spell", shorten(snapshot.getWeaponOrSpellName(), 25), Color.WHITE, Color.WHITE);
		}

		if (config.showLevels())
		{
			addLine(snapshot.getLevelLabel(), snapshot.getLevelText(), Color.WHITE, Color.WHITE);
		}

		if (config.showGearBonuses())
		{
			addLine(snapshot.getGearBonusLabel(), snapshot.getGearBonusText(), Color.WHITE, Color.WHITE);
		}

		if (config.showPrayer())
		{
			addLine(
				"Prayer",
				snapshot.getPrayerName(),
				Color.WHITE,
				"None".equals(snapshot.getPrayerName()) ? MUTED : Color.WHITE);
		}

		if (mode == HudDisplayMode.ADVANCED)
		{
			if (snapshot.hasSupportedSpecialAttack() && config.showExpectedSpec())
			{
				addLine(
					"Avg spec dmg",
					snapshot.getSpecialExpectedDamageText(),
					Color.WHITE,
					snapshot.isSpecialExpectedDamageAvailable() ? routineColor(SUCCESS) : snapshot.hasTarget() ? WARNING : MUTED);
			}

			if (snapshot.hasSupportedSpecialAttack() && config.showSpecOnHit())
			{
				addLine(
					"On hit",
					shorten(snapshot.getSpecialOnHitText(), 27),
					Color.WHITE,
					routineColor(ACCENT));
			}

			TargetDefenceDisplay targetDefenceDisplay = config.targetDefenceDisplay();
			if (targetDefenceDisplay.showsHudRow() && snapshot.hasLiveTargetEffects())
			{
				addLine(
					"Target Def",
					snapshot.getTargetDefenceText(),
					Color.WHITE,
					snapshot.hasTrackedTargetDefence() ? routineColor(SUCCESS) : MUTED);
			}
			TargetMagicDisplay targetMagicDisplay = config.targetMagicDisplay();
			boolean showMagicRows = snapshot.getCombatStyle() == CombatStyle.MAGIC
				&& targetMagicDisplay.showsHudRows();
			if (showMagicRows && snapshot.hasTrackedTargetMagic())
			{
				addLine(
					"Target Magic",
					snapshot.getTargetMagicText(),
					Color.WHITE,
					routineColor(SUCCESS));
			}
			if (showMagicRows && snapshot.hasTrackedTargetMagicDefence())
			{
				addLine(
					"Magic def bonus",
					snapshot.getTargetMagicDefenceText(),
					Color.WHITE,
					routineColor(SUCCESS));
			}

			if (config.showLevels())
			{
				addLine(
					snapshot.getEffectiveLevelLabel(),
					snapshot.getEffectiveLevelText(),
					Color.WHITE,
					snapshot.isMaxHitAvailable() ? Color.WHITE : WARNING);
			}

			if (config.showPrayer())
			{
				addLine(
					"Prayer damage",
					snapshot.isPrayerAffectsCurrentStyle() ? snapshot.prayerMultiplierText() : "None",
					Color.WHITE,
					snapshot.isPrayerAffectsCurrentStyle() ? Color.WHITE : MUTED);
			}

			if (config.showAttackSpeed())
			{
				addLine("Attack interval", snapshot.getAttackSpeedText(), Color.WHITE, Color.WHITE);
			}

			if (config.showTarget() && snapshot.hasTarget())
			{
				addLine("Target", targetText(snapshot), Color.WHITE, Color.WHITE);
			}

			if (shouldShowDefenceBasedRow(config.showAccuracyRolls(), snapshot.isCombatDummy()))
			{
				addLine(
					"Your / target roll",
					snapshot.getRollText(),
					Color.WHITE,
					snapshot.isAccuracyAvailable() ? Color.WHITE : WARNING);
			}

			if (config.showDefenceType())
			{
				addLine("Target defence", snapshot.getDefenceTypeText(), Color.WHITE, Color.WHITE);
			}

			if (config.showObservedAverage())
			{
				addLine(
					snapshot.getObservedHitLabel(),
					plugin.getObservedAverageHitText(),
					Color.WHITE,
					plugin.hasObservedHits() ? routineColor(SUCCESS) : MUTED);
			}

			if (config.showWarnings())
			{
				List<String> warnings = snapshot.getWarnings();
				int visibleWarnings = Math.min(2, warnings.size());
				for (int i = 0; i < visibleWarnings; i++)
				{
					addLine(i == 0 ? "Notice" : "", shorten(warnings.get(i), 31), WARNING, WARNING);
				}
				if (warnings.size() > visibleWarnings)
				{
					addLine("", "+" + (warnings.size() - visibleWarnings) + " more notices", WARNING, WARNING);
				}
			}

		}

		return super.render(graphics);
	}


    private void renderPreview(HudDisplayMode mode)
    {
        addLine("PREVIEW", "Illustrative", ACCENT, MUTED);
        if (config.showTitle()) addLine("Combat Insight", "Melee", ACCENT, routineColor(SUCCESS));
        if (config.showMaxHit()) addLine("Max hit", "48", Color.WHITE, Color.WHITE);
        if (mode == HudDisplayMode.MINIMAL) return;
        if (config.showAccuracy()) addLine("Hit chance", "82.5%", Color.WHITE, routineColor(SUCCESS));
        if (config.showDps()) addLine("DPS", "6.42", Color.WHITE, routineColor(SUCCESS));
        if (config.specDivider() && (config.showSpecMax() || config.showSpecChance()))
            panelComponent.getChildren().add(new HudDivider());
        if (config.showSpecMax())
        {
            Color color = HudPresentation.specialColor(SpecialAttackWeapon.DRAGON_DAGGER, true,
                config.highlightSpecialAttack(), config.specialHighlightColor(), Color.WHITE);
            addLine("Spec max", mode == HudDisplayMode.ADVANCED ? "110 (55 + 55)" : "110", color, color);
        }
        if (config.showSpecChance()) addLine("Spec chance", "96.9%", Color.WHITE, routineColor(SUCCESS));
        if (config.showStyle()) addLine("Combat style", "Melee | Aggressive", Color.WHITE, Color.WHITE);
        if (config.showWeapon()) addLine("Weapon / spell", "Dragon dagger", Color.WHITE, Color.WHITE);
        if (config.showLevels()) addLine("Strength", "118", Color.WHITE, Color.WHITE);
        if (config.showGearBonuses()) addLine("Strength bonus", "+120", Color.WHITE, Color.WHITE);
        if (config.showPrayer()) addLine("Prayer", "Piety", Color.WHITE, Color.WHITE);
        if (mode != HudDisplayMode.ADVANCED) return;
        if (config.showExpectedSpec()) addLine("Avg spec dmg", "46.9", Color.WHITE, routineColor(SUCCESS));
        if (config.showSpecOnHit()) addLine("On hit", "Two independent hits", Color.WHITE, routineColor(ACCENT));
        if (config.showLevels()) addLine("Effective strength", "156", Color.WHITE, Color.WHITE);
        if (config.showPrayer()) addLine("Prayer damage", "1.23x", Color.WHITE, Color.WHITE);
        if (config.showAttackSpeed()) addLine("Attack interval", "4 ticks", Color.WHITE, Color.WHITE);
        if (config.showTarget()) addLine("Target", "Example target", Color.WHITE, Color.WHITE);
        if (config.showAccuracyRolls()) addLine("Your / target roll", "24000 / 8400", Color.WHITE, Color.WHITE);
        if (config.showDefenceType()) addLine("Target defence", "Stab", Color.WHITE, Color.WHITE);
        if (config.showObservedAverage()) addLine("Avg hit", "—", Color.WHITE, MUTED);
    }

	private void addLine(String left, String right, Color leftColor, Color rightColor)
	{
		panelComponent.getChildren().add(LineComponent.builder()
			.left(left)
			.right(right.isEmpty() ? "" : HudPresentation.value(right))
			.leftColor(leftColor)
			.rightColor(rightColor)
			.build());
	}

	private Color routineColor(Color classicColor)
	{
		return config.quietHudColors() ? Color.WHITE : classicColor;
	}

	static boolean shouldShowDefenceBasedRow(boolean configured, boolean combatDummy)
	{
		return configured && !combatDummy;
	}

	private static String shorten(String value, int maximumLength)
	{
		if (value == null || value.length() <= maximumLength)
		{
			return value == null ? "" : value;
		}
		return value.substring(0, Math.max(0, maximumLength - 3)) + "...";
	}

	private static String targetText(LiveCombatSnapshot snapshot)
	{
		String health = snapshot.getTargetHealthText();
		if (health.isEmpty())
		{
			return shorten(snapshot.getTargetName(), 24);
		}

		int maximumNameLength = Math.max(4, 31 - health.length() - 3);
		return shorten(snapshot.getTargetName(), maximumNameLength) + " - " + health;
	}
}
