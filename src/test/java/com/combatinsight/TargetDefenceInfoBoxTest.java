package com.combatinsight;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TargetDefenceInfoBoxTest
{
	private static final Color DEFAULT_TEXT = new Color(255, 152, 31);
	private static final Color DEFAULT_FLASH = new Color(100, 235, 130);

	@Test
	public void flashesOnlyWhenTheSameTargetsDefenceFalls()
	{
		TargetDefenceInfoBox infoBox = new TargetDefenceInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());
		Object target = new Object();

		infoBox.update(target, "Scurrius", 60, 60, false, false, true,
			DEFAULT_TEXT, DEFAULT_FLASH, 1_000L);
		assertEquals(DEFAULT_TEXT, infoBox.getTextColor(1_000L));

		infoBox.update(target, "Scurrius", 42, 60, true, false, true,
			DEFAULT_TEXT, DEFAULT_FLASH, 2_000L);
		assertEquals("42", infoBox.getText());
		assertNotEquals(DEFAULT_TEXT, infoBox.getTextColor(2_000L));
		assertEquals(DEFAULT_TEXT, infoBox.getTextColor(3_200L));
		assertTrue(infoBox.getTooltip().contains("Defence: 42 / 60"));
	}

	@Test
	public void switchingNpcDoesNotLookLikeADefenceReduction()
	{
		TargetDefenceInfoBox infoBox = new TargetDefenceInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());

		infoBox.update(new Object(), "First", 60, 60, false, false, true,
			DEFAULT_TEXT, DEFAULT_FLASH, 1_000L);
		infoBox.update(new Object(), "Second", 30, 30, false, false, true,
			DEFAULT_TEXT, DEFAULT_FLASH, 2_000L);

		assertEquals(DEFAULT_TEXT, infoBox.getTextColor(2_000L));
	}

	@Test
	public void usesConfiguredStaticAndFlashColors()
	{
		TargetDefenceInfoBox infoBox = new TargetDefenceInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());
		Object target = new Object();
		Color staticColor = new Color(25, 40, 180);
		Color flashColor = new Color(240, 30, 210);

		infoBox.update(target, "Target", 60, 60, false, false, true,
			staticColor, flashColor, 1_000L);
		assertEquals(staticColor, infoBox.getTextColor(1_000L));

		infoBox.update(target, "Target", 30, 60, true, false, true,
			staticColor, flashColor, 2_000L);
		assertEquals(flashColor, infoBox.getTextColor(2_000L));
		assertEquals(staticColor, infoBox.getTextColor(3_200L));
	}
}
