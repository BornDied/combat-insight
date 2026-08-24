package com.combatinsight;

import com.combatinsight.calculation.HudDisplayMode;
import com.combatinsight.live.LiveCombatSnapshot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.OverlayLayer;
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
		setPreferredSize(new Dimension(225, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();
		if (!config.showHud() || !plugin.isHudVisible())
		{
			return null;
		}

		LiveCombatSnapshot snapshot = plugin.getSnapshot();
		HudDisplayMode mode = config.displayMode();
		panelComponent.setBackgroundColor(config.backgroundColor());
		panelComponent.setPreferredSize(mode == HudDisplayMode.MINIMAL
			? new Dimension(155, 0)
			: mode == HudDisplayMode.ADVANCED ? new Dimension(280, 0) : new Dimension(225, 0));

		if (config.showTitle())
		{
			addLine("Combat Insight", snapshot.getCombatStyleName(), ACCENT, SUCCESS);
		}

		if (config.showMaxHit())
		{
			addLine(
				"Max hit",
				snapshot.getMaxHitText(),
				Color.WHITE,
				snapshot.isTargetImmune() ? WARNING
					: snapshot.isMaxHitAvailable() ? plugin.getMaxHitChangeColor() : WARNING);
		}

		if (mode == HudDisplayMode.ADVANCED && snapshot.hasMultiHitSplit())
		{
			addLine("Max split", snapshot.getMultiHitSplitText(), Color.WHITE, ACCENT);
		}

		if (mode == HudDisplayMode.MINIMAL)
		{
			return super.render(graphics);
		}

		if (config.showAccuracy())
		{
			addLine(
				"Hit chance",
				snapshot.getHitChanceText(),
				Color.WHITE,
				snapshot.isAccuracyAvailable() ? SUCCESS : snapshot.hasTarget() ? WARNING : MUTED);
		}

		if (config.showDps())
		{
			addLine(
				"DPS",
				snapshot.getDpsText(),
				Color.WHITE,
				snapshot.isDpsAvailable() ? SUCCESS : snapshot.hasTarget() ? WARNING : MUTED);
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

			if (config.showAccuracyRolls())
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
					plugin.hasObservedHits() ? SUCCESS : MUTED);
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

	private void addLine(String left, String right, Color leftColor, Color rightColor)
	{
		panelComponent.getChildren().add(LineComponent.builder()
			.left(left)
			.right(right)
			.leftColor(leftColor)
			.rightColor(rightColor)
			.build());
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
