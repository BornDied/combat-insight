package com.combatinsight;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TargetMagicInfoBoxTest
{
	private static final Color DEFAULT_TEXT = new Color(255, 220, 80);
	private static final Color DEFAULT_FLASH = new Color(100, 235, 130);

	@Test
	public void showsTheTrackedMagicDefenceBonus()
	{
		TargetMagicInfoBox infoBox = new TargetMagicInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());
		Object target = new Object();

		infoBox.update(target, "Scurrius", 10, 10, "Magic Defence bonus",
			true, false, true, DEFAULT_TEXT, DEFAULT_FLASH, 1_000L);
		infoBox.update(target, "Scurrius", 0, 10, "Magic Defence bonus",
			true, false, true, DEFAULT_TEXT, DEFAULT_FLASH, 2_000L);

		assertEquals("0", infoBox.getText());
		assertNotEquals(DEFAULT_TEXT, infoBox.getTextColor(2_000L));
		assertEquals(DEFAULT_TEXT, infoBox.getTextColor(3_200L));
		assertTrue(infoBox.getTooltip().contains("Magic Defence bonus: +0 / +10"));
	}

	@Test
	public void showsMagicLevelForMagicLevelDrains()
	{
		TargetMagicInfoBox infoBox = new TargetMagicInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());

		infoBox.update(new Object(), "Target", 72, 80, "Magic level",
			false, true, false, DEFAULT_TEXT, DEFAULT_FLASH, 1_000L);

		assertEquals("72", infoBox.getText());
		assertTrue(infoBox.getTooltip().contains("Magic level: 72 / 80"));
		assertTrue(infoBox.getTooltip().contains("estimated recovery"));
	}

	@Test
	public void switchingNpcDoesNotTriggerAFlash()
	{
		TargetMagicInfoBox infoBox = new TargetMagicInfoBox(
			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			new CombatInsightPlugin());

		infoBox.update(new Object(), "First", 10, 10, "Magic Defence bonus",
			true, false, true, DEFAULT_TEXT, DEFAULT_FLASH, 1_000L);
		infoBox.update(new Object(), "Second", 0, 0, "Magic Defence bonus",
			true, false, true, DEFAULT_TEXT, DEFAULT_FLASH, 2_000L);

		assertEquals(DEFAULT_TEXT, infoBox.getTextColor(2_000L));
	}
}
