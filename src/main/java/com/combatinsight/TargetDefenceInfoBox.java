package com.combatinsight;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Locale;
import net.runelite.client.ui.overlay.infobox.InfoBox;

/** Movable RuneLite infobox for one live NPC's locally tracked Defence. */
final class TargetDefenceInfoBox extends InfoBox
{
	private static final long REDUCTION_FLASH_MILLIS = 1200L;
	private static final Color DEFAULT_TEXT_COLOR = new Color(255, 152, 31);
	private static final Color DEFAULT_FLASH_COLOR = new Color(100, 235, 130);

	private Object targetKey;
	private int currentDefence;
	private int baseDefence;
	private long reductionChangedAt;
	private boolean animateChanges;
	private Color textColor = DEFAULT_TEXT_COLOR;
	private Color flashColor = DEFAULT_FLASH_COLOR;

	TargetDefenceInfoBox(BufferedImage image, CombatInsightPlugin plugin)
	{
		super(image, plugin);
	}

	void update(
		Object newTargetKey,
		String targetName,
		int newCurrentDefence,
		int newBaseDefence,
		boolean tracked,
		boolean estimatedRecovery,
		boolean animate,
		Color newTextColor,
		Color newFlashColor,
		long now)
	{
		boolean sameTarget = targetKey == newTargetKey;
		if (sameTarget && newCurrentDefence < currentDefence)
		{
			reductionChangedAt = now;
		}
		else if (!sameTarget)
		{
			reductionChangedAt = 0L;
		}

		targetKey = newTargetKey;
		currentDefence = Math.max(0, newCurrentDefence);
		baseDefence = Math.max(0, newBaseDefence);
		animateChanges = animate;
		textColor = newTextColor == null ? DEFAULT_TEXT_COLOR : newTextColor;
		flashColor = newFlashColor == null ? DEFAULT_FLASH_COLOR : newFlashColor;

		String safeName = targetName == null || targetName.trim().isEmpty()
			? "Current target"
			: targetName;
		String status = tracked
			? estimatedRecovery ? " (tracked, estimated recovery)" : " (tracked locally)"
			: "";
		setTooltip(String.format(
			Locale.ROOT,
			"%s</br>Defence: %,d / %,d%s",
			safeName,
			currentDefence,
			baseDefence,
			status));
	}

	boolean appliesTo(Object target)
	{
		return target != null && targetKey == target;
	}

	@Override
	public String getText()
	{
		return Integer.toString(currentDefence);
	}

	@Override
	public Color getTextColor()
	{
		return getTextColor(System.currentTimeMillis());
	}

	Color getTextColor(long now)
	{
		if (!animateChanges || reductionChangedAt <= 0L)
		{
			return textColor;
		}

		long elapsed = now - reductionChangedAt;
		if (elapsed < 0L || elapsed >= REDUCTION_FLASH_MILLIS)
		{
			return textColor;
		}

		double intensity = 1.0 - (double) elapsed / REDUCTION_FLASH_MILLIS;
		return blend(textColor, flashColor, intensity);
	}

	private static Color blend(Color from, Color to, double amount)
	{
		double clamped = Math.max(0.0, Math.min(1.0, amount));
		int red = (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * clamped);
		int green = (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * clamped);
		int blue = (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * clamped);
		return new Color(red, green, blue);
	}
}
