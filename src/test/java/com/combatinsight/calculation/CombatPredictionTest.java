package com.combatinsight.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

public class CombatPredictionTest
{
	@Test
	public void calculatesNormalAccuracyOnBothSidesOfTheDefenceRoll()
	{
		assertEquals(1.0 - 52.0 / 202.0, CombatPrediction.hitChance(100, 50), 0.0000001);
		assertEquals(100.0 / 202.0, CombatPrediction.hitChance(100, 100), 0.0000001);
		assertTrue(CombatPrediction.fangHitChance(100, 100) > CombatPrediction.hitChance(100, 100));
	}

	@Test
	public void appliesFlatArmourToEachSuccessfulDamageRoll()
	{
		CombatPrediction result = CombatPrediction.calculateWithHitChance(
			100,
			50,
			1.0,
			0,
			20,
			4,
			5,
			true);

		assertEquals(120.0 / 21.0, result.getAverageSuccessfulHit(), 0.0000001);
		assertEquals((120.0 / 21.0) / 2.4, result.getDamagePerSecond(), 0.0000001);
	}

	@Test
	public void raisesAccurateZeroesBeforeApplyingNegativeFlatArmour()
	{
		CombatPrediction ordinary = CombatPrediction.calculateWithHitChance(
			100, 50, 1.0, 0, 2, 4, 0, true);
		CombatPrediction negativeArmour = CombatPrediction.calculateWithHitChance(
			100, 50, 1.0, 0, 2, 4, -2, true);

		assertEquals(4.0 / 3.0, ordinary.getAverageSuccessfulHit(), 0.0000001);
		assertEquals(10.0 / 3.0, negativeArmour.getAverageSuccessfulHit(), 0.0000001);
	}

	@Test
	public void doesNotRaiseZeroWhenTheWholeAttackHasNoDamageRoll()
	{
		CombatPrediction result = CombatPrediction.calculateWithHitChance(
			100, 50, 1.0, 0, 0, 4, -2, true);

		assertEquals(0.0, result.getAverageSuccessfulHit(), 0.0000001);
		assertEquals(0.0, result.getDamagePerSecond(), 0.0000001);
	}

	@Test
	public void mapsTargetDefenceToTheSelectedAttackType()
	{
		TargetProfile target = new TargetProfile(
			1, 60, 50, 500, 150, 3, 0,
			20, 30, 40, 10, 5, 15, 25,
			"", 0, false, Collections.emptySet(), false);

		assertEquals((60 + 9) * (30 + 64),
			CombatPrediction.defenceRoll(target, AttackType.SLASH, false));
		assertEquals((50 + 9) * (10 + 64),
			CombatPrediction.defenceRoll(target, AttackType.MAGIC, false));
		assertEquals((60 + 9) * (25 + 64),
			CombatPrediction.defenceRoll(target, AttackType.RANGED_HEAVY, false));
	}
}
