package com.combatinsight.calculation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OsrsCombatCalculatorTest
{
	@Test
	public void calculatesAStableMeleeResult()
	{
		CombatState state = new CombatState(
			CombatStyle.MELEE,
			AttackStyle.MELEE_AGGRESSIVE,
			new CombatStats(99, 99, 1, 1),
			new CombatStats(0, 0, 0, 0),
			new GearStats(120, 120, 0, 0, 0, 0, 4),
			new PrayerBonuses(1.23, 1.23, 1.0),
			new TargetStats("Training target", 100, 100, 100, 100, 100),
			0);

		CombatResult result = OsrsCombatCalculator.calculate(state);

		assertEquals(38, result.getMaxHit());
		assertTrue(result.getAccuracy() > 0.0 && result.getAccuracy() <= 1.0);
		assertEquals(result.getAverageHit() / 2.4, result.getDamagePerSecond(), 0.000001);
	}

	@Test
	public void returnsNoMoreThanOneAccuracy()
	{
		CombatState state = new CombatState(
			CombatStyle.RANGED,
			AttackStyle.RANGED_RAPID,
			new CombatStats(1, 1, 99, 1),
			new CombatStats(0, 0, 0, 0),
			new GearStats(0, 0, 150, 120, 0, 0, 3),
			new PrayerBonuses(1.0, 1.0, 1.2),
			new TargetStats("Low defence target", 1, 1, 1, 1, 20),
			0);

		assertEquals(1.0, OsrsCombatCalculator.calculate(state).getAccuracy(), 0.02);
	}

	@Test
	public void appliesVisibleMagicDamageBonus()
	{
		CombatState state = new CombatState(
			CombatStyle.MAGIC,
			AttackStyle.MAGIC_ACCURATE,
			new CombatStats(1, 1, 1, 99),
			new CombatStats(0, 0, 0, 0),
			new GearStats(0, 0, 0, 0, 100, 25, 4),
			new PrayerBonuses(1.0, 1.0, 1.0, 4.0),
			new TargetStats("Training target", 100, 100, 100, 100, 100),
			30);

		assertEquals(38, OsrsCombatCalculator.calculate(state).getMaxHit());
	}
}
