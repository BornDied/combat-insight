package com.combatinsight.calculation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class MultiHitWeaponTest
{
	private static final double EPSILON = 0.0000001;

	@Test
	public void identifiesTheFiveInitialWeaponsAndTheirVariants()
	{
		assertSame(MultiHitWeapon.SCYTHE_OF_VITUR,
			MultiHitWeapon.forWeapon("Holy scythe of vitur (uncharged)"));
		assertSame(MultiHitWeapon.DUAL_MACUAHUITL,
			MultiHitWeapon.forWeapon("Dual macuahuitl"));
		assertSame(MultiHitWeapon.TORAGS_HAMMERS,
			MultiHitWeapon.forWeapon("Torag's hammers 75"));
		assertSame(MultiHitWeapon.SULPHUR_BLADES,
			MultiHitWeapon.forWeapon("Sulphur blades"));
		assertSame(MultiHitWeapon.DARK_BOW,
			MultiHitWeapon.forWeapon("Dark bow (green)"));
		assertNull(MultiHitWeapon.forWeapon("Abyssal whip"));
		assertNull(MultiHitWeapon.forWeapon(null));
	}

	@Test
	public void scytheUsesTargetSizeAndIndependentFullHalfQuarterRolls()
	{
		MultiHitResult sizeOne = calculate(MultiHitWeapon.SCYTHE_OF_VITUR, 10, 0, 0.5, 1);
		MultiHitResult sizeTwo = calculate(MultiHitWeapon.SCYTHE_OF_VITUR, 10, 0, 0.5, 2);
		MultiHitResult sizeThree = calculate(MultiHitWeapon.SCYTHE_OF_VITUR, 10, 0, 0.5, 3);

		assertArrayEquals(new int[]{10}, sizeOne.getMaximumHits());
		assertArrayEquals(new int[]{10, 5}, sizeTwo.getMaximumHits());
		assertArrayEquals(new int[]{10, 5, 2}, sizeThree.getMaximumHits());
		assertEquals(17, sizeThree.getTotalMaximumHit());
		assertEquals(0.5 * (56.0 / 11.0), sizeOne.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.5 * (56.0 / 11.0 + 16.0 / 6.0),
			sizeTwo.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.5 * (56.0 / 11.0 + 16.0 / 6.0 + 4.0 / 3.0),
			sizeThree.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.875, sizeThree.getSuccessfulAttackChance(), EPSILON);
	}

	@Test
	public void dualMacuahuitlOnlyRollsItsSecondHitAfterTheFirstLands()
	{
		MultiHitResult result = calculate(MultiHitWeapon.DUAL_MACUAHUITL, 10, 0, 0.5, 1);

		assertArrayEquals(new int[]{5, 5}, result.getMaximumHits());
		assertEquals(2.0, result.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.5, result.getSuccessfulAttackChance(), EPSILON);
		assertEquals(4.0, result.getAverageDamageOnSuccessfulAttack(), EPSILON);
	}

	@Test
	public void standardTwoHitWeaponsUseIndependentSplitDamageRolls()
	{
		MultiHitResult torags = calculate(MultiHitWeapon.TORAGS_HAMMERS, 11, 0, 0.5, 1);
		MultiHitResult sulphur = calculate(MultiHitWeapon.SULPHUR_BLADES, 11, 0, 0.5, 1);

		assertArrayEquals(new int[]{5, 6}, torags.getMaximumHits());
		assertEquals(0.5 * (16.0 / 6.0 + 22.0 / 7.0),
			torags.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(torags.getExpectedDamagePerAttack(),
			sulphur.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.75, torags.getSuccessfulAttackChance(), EPSILON);
	}

	@Test
	public void darkBowFiresTwoIndependentFullDamageArrows()
	{
		MultiHitResult result = calculate(MultiHitWeapon.DARK_BOW, 10, 0, 0.5, 1);

		assertArrayEquals(new int[]{10, 10}, result.getMaximumHits());
		assertEquals(20, result.getTotalMaximumHit());
		assertEquals(56.0 / 11.0, result.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(0.75, result.getSuccessfulAttackChance(), EPSILON);
	}

	@Test
	public void flatArmourAppliesSeparatelyToEveryAccurateHitsplat()
	{
		MultiHitResult positive = calculate(MultiHitWeapon.SULPHUR_BLADES, 10, 2, 1.0, 1);
		MultiHitResult negative = calculate(MultiHitWeapon.SULPHUR_BLADES, 10, -2, 1.0, 1);

		assertArrayEquals(new int[]{3, 3}, positive.getMaximumHits());
		assertEquals(2.0, positive.getExpectedDamagePerAttack(), EPSILON);
		assertArrayEquals(new int[]{7, 7}, negative.getMaximumHits());
		assertEquals(28.0 / 3.0, negative.getExpectedDamagePerAttack(), EPSILON);
	}

	@Test
	public void raisesAccurateZeroesFromLowMaximumSplitHits()
	{
		MultiHitResult scythe = calculate(MultiHitWeapon.SCYTHE_OF_VITUR, 1, 0, 1.0, 3);
		MultiHitResult torags = calculate(MultiHitWeapon.TORAGS_HAMMERS, 1, 0, 1.0, 1);

		assertArrayEquals(new int[]{1, 1, 1}, scythe.getMaximumHits());
		assertEquals(3.0, scythe.getExpectedDamagePerAttack(), EPSILON);
		assertArrayEquals(new int[]{1, 1}, torags.getMaximumHits());
		assertEquals(2.0, torags.getExpectedDamagePerAttack(), EPSILON);
	}

	@Test
	public void bloodMoonSetUsesAnAccuracyWeightedAttackInterval()
	{
		MultiHitResult withoutSet = MultiHitWeapon.DUAL_MACUAHUITL.calculate(
			0, 10, 0, 0.5, 1, 4, false, false);
		MultiHitResult withSet = MultiHitWeapon.DUAL_MACUAHUITL.calculate(
			0, 10, 0, 0.5, 1, 4, false, true);

		assertEquals(4.0, withoutSet.getExpectedAttackSpeedTicks(), EPSILON);
		assertEquals(4.0 - (0.5 / 3.0 + 2.0 * 0.25 / 9.0),
			withSet.getExpectedAttackSpeedTicks(), EPSILON);
		assertEquals(withSet.getExpectedDamagePerAttack()
			/ (withSet.getExpectedAttackSpeedTicks() * 0.6),
			withSet.getDamagePerSecond(), EPSILON);
	}

	@Test
	public void guaranteedMaximumTargetsUseTheWholeAttacksMaximum()
	{
		MultiHitResult result = MultiHitWeapon.SCYTHE_OF_VITUR.calculate(
			0, 10, 0, 0.2, 3, 5, true, false);

		assertEquals(17.0, result.getExpectedDamagePerAttack(), EPSILON);
		assertEquals(1.0, result.getSuccessfulAttackChance(), EPSILON);
	}

	private static MultiHitResult calculate(
		MultiHitWeapon weapon,
		int maximumHit,
		int flatArmour,
		double hitChance,
		int targetSize)
	{
		return weapon.calculate(
			0,
			maximumHit,
			flatArmour,
			hitChance,
			targetSize,
			4,
			false,
			false);
	}
}
