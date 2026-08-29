package com.combatinsight.calculation;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SpecialAttackCalculatorTest
{
	private static final double EPSILON = 0.0000001;

	@Test
	public void identifiesWaveOneWeaponsAndVariants()
	{
		assertSame(SpecialAttackWeapon.DRAGON_WARHAMMER,
			SpecialAttackWeapon.forWeapon("Dragon warhammer"));
		assertSame(SpecialAttackWeapon.ELDER_MAUL,
			SpecialAttackWeapon.forWeapon("Elder maul (or)"));
		assertSame(SpecialAttackWeapon.BANDOS_GODSWORD,
			SpecialAttackWeapon.forWeapon("Bandos godsword (or)"));
		assertSame(SpecialAttackWeapon.ARMADYL_GODSWORD,
			SpecialAttackWeapon.forWeapon("Armadyl godsword (or)"));
		assertSame(SpecialAttackWeapon.SARADOMIN_GODSWORD,
			SpecialAttackWeapon.forWeapon("Saradomin godsword"));
		assertSame(SpecialAttackWeapon.ZAMORAK_GODSWORD,
			SpecialAttackWeapon.forWeapon("Zamorak godsword"));
		assertSame(SpecialAttackWeapon.ANCIENT_GODSWORD,
			SpecialAttackWeapon.forWeapon("Ancient godsword"));
		assertSame(SpecialAttackWeapon.BURNING_CLAWS,
			SpecialAttackWeapon.forWeapon("Burning claws"));
		assertSame(SpecialAttackWeapon.DRAGON_CLAWS,
			SpecialAttackWeapon.forWeapon("Dragon claws"));
		assertSame(SpecialAttackWeapon.VOIDWAKER,
			SpecialAttackWeapon.forWeapon("Voidwaker"));
		assertSame(SpecialAttackWeapon.DRAGON_DAGGER,
			SpecialAttackWeapon.forWeapon("Dragon dagger(p++)"));
		assertSame(SpecialAttackWeapon.DARK_BOW,
			SpecialAttackWeapon.forWeapon("Dark bow (green)"));
		assertSame(SpecialAttackWeapon.TONALZTICS_OF_RALOS,
			SpecialAttackWeapon.forWeapon("Tonalztics of Ralos (uncharged)"));
		assertSame(SpecialAttackWeapon.EYE_OF_AYAK,
			SpecialAttackWeapon.forWeapon("Eye of ayak"));
		assertSame(SpecialAttackWeapon.ACCURSED_SCEPTRE,
			SpecialAttackWeapon.forWeapon("Accursed sceptre (a)"));
		assertSame(SpecialAttackWeapon.BONE_DAGGER,
			SpecialAttackWeapon.forWeapon("Bone dagger(p++)"));
		assertSame(SpecialAttackWeapon.ARCLIGHT,
			SpecialAttackWeapon.forWeapon("Arclight"));
		assertSame(SpecialAttackWeapon.EMBERLIGHT,
			SpecialAttackWeapon.forWeapon("Emberlight"));
		assertSame(SpecialAttackWeapon.SEERCULL,
			SpecialAttackWeapon.forWeapon("Seercull"));
	}

	@Test
	public void warhammerUsesCrushChanceAndFiftyPercentDamage()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.DRAGON_WARHAMMER, 40, 100, 100, false, false, true, false);
		double chance = CombatPrediction.hitChance(100, 100);

		assertEquals(60, result.getTotalMaximumHit());
		assertEquals(chance, result.getHitChance(), EPSILON);
		assertEquals(DamageRoll.averageSuccessfulHit(0, 60, 0) * chance,
			result.getExpectedDamage(), EPSILON);
	}

	@Test
	public void elderMaulUsesTwentyFivePercentAccuracyAndNormalDamage()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.ELDER_MAUL, 40, 100, 100, false, false, true, false);

		assertEquals(40, result.getTotalMaximumHit());
		assertEquals(CombatPrediction.hitChance(125, 100), result.getHitChance(), EPSILON);
	}

	@Test
	public void bandosGodswordUsesTwoSeparateRoundingStages()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.BANDOS_GODSWORD, 50, 100, 100, false, false, true, false);

		assertEquals(60, result.getTotalMaximumHit());
		assertEquals(CombatPrediction.hitChance(200, 100), result.getHitChance(), EPSILON);
	}

	@Test
	public void waveOneCGodswordsUseTheirDamageAndAccuracyModifiers()
	{
		double chance = CombatPrediction.hitChance(200, 100);
		SpecialAttackResult armadyl = calculate(
			SpecialAttackWeapon.ARMADYL_GODSWORD, 50, 100, 100, false, false, true, false);
		SpecialAttackResult saradomin = calculate(
			SpecialAttackWeapon.SARADOMIN_GODSWORD, 50, 100, 100, false, false, true, false);
		SpecialAttackResult zamorak = calculate(
			SpecialAttackWeapon.ZAMORAK_GODSWORD, 50, 100, 100, false, false, true, false);
		SpecialAttackResult ancient = calculate(
			SpecialAttackWeapon.ANCIENT_GODSWORD, 50, 100, 100, false, false, true, false);

		assertEquals(62, armadyl.getTotalMaximumHit());
		assertEquals(55, saradomin.getTotalMaximumHit());
		assertEquals(55, zamorak.getTotalMaximumHit());
		assertArrayEquals(new int[]{55, 25}, ancient.getMaximumHits());
		assertEquals(80, ancient.getTotalMaximumHit());
		assertEquals(chance, armadyl.getHitChance(), EPSILON);
		assertEquals(chance, saradomin.getHitChance(), EPSILON);
		assertEquals(chance, zamorak.getHitChance(), EPSILON);
		assertEquals(chance, ancient.getHitChance(), EPSILON);
		assertEquals(
			(DamageRoll.averageSuccessfulHit(0, 55, 0) + 25.0) * chance,
			ancient.getExpectedDamage(),
			EPSILON);
	}

	@Test
	public void waveOneCGodswordsExplainTheirConditionalEffects()
	{
		assertEquals("25% increased damage", SpecialAttackWeapon.ARMADYL_GODSWORD.getOnHitText());
		assertEquals("HP 50%/10; Prayer 25%/5", SpecialAttackWeapon.SARADOMIN_GODSWORD.getOnHitText());
		assertEquals("Freeze target for 20 sec", SpecialAttackWeapon.ZAMORAK_GODSWORD.getOnHitText());
		assertEquals("8 ticks: +25 dmg, +25 HP", SpecialAttackWeapon.ANCIENT_GODSWORD.getOnHitText());
	}

	@Test
	public void dragonDaggerRollsTwoIndependentBoostedHits()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.DRAGON_DAGGER, 50, 100, 100, false, false, true, false);
		double perHitChance = CombatPrediction.hitChance(115, 100);

		assertArrayEquals(new int[]{57, 57}, result.getMaximumHits());
		assertEquals(114, result.getTotalMaximumHit());
		assertEquals(1.0 - Math.pow(1.0 - perHitChance, 2), result.getHitChance(), EPSILON);
		assertEquals(2.0 * perHitChance * DamageRoll.averageSuccessfulHit(0, 57, 0),
			result.getExpectedDamage(), EPSILON);
	}

	@Test
	public void voidwakerIsGuaranteedFromHalfToOneHundredFiftyPercent()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.VOIDWAKER, 50, 0, 999999, false, false, true, false);

		assertArrayEquals(new int[]{75}, result.getMaximumHits());
		assertEquals(1.0, result.getHitChance(), EPSILON);
		assertEquals("Guaranteed", result.getHitChanceText());
		assertEquals(50.0, result.getExpectedDamage(), EPSILON);
	}

	@Test
	public void burningClawsUseThreeRollsAndIncludeExpectedBurnDamage()
	{
		SpecialAttackResult guaranteed = calculate(
			SpecialAttackWeapon.BURNING_CLAWS, 42, 100, 100, true, false, true, false);
		SpecialAttackResult allAccuracyRollsFail = calculate(
			SpecialAttackWeapon.BURNING_CLAWS, 42, 0, 0, false, false, true, false);

		assertArrayEquals(new int[]{18, 18, 36}, guaranteed.getMaximumHits());
		assertEquals(72, guaranteed.getTotalMaximumHit());
		assertEquals(55.4775, guaranteed.getExpectedDamage(), EPSILON);
		assertEquals("Guaranteed", guaranteed.getHitChanceText());
		assertEquals(0.0, allAccuracyRollsFail.getHitChance(), EPSILON);
		assertEquals(1.2, allAccuracyRollsFail.getExpectedDamage(), EPSILON);
	}

	@Test
	public void dragonClawsUseTheFourRollDistributionAndFallbackDamage()
	{
		SpecialAttackResult guaranteed = calculate(
			SpecialAttackWeapon.DRAGON_CLAWS, 10, 0, 0, true, false, true, false);
		SpecialAttackResult allAccuracyRollsFail = calculate(
			SpecialAttackWeapon.DRAGON_CLAWS, 10, 0, 0, false, false, true, false);

		assertArrayEquals(new int[]{9, 4, 2, 3}, guaranteed.getMaximumHits());
		assertEquals(18, guaranteed.getTotalMaximumHit());
		assertEquals(14.0, guaranteed.getExpectedDamage(), EPSILON);
		assertEquals(0.0, allAccuracyRollsFail.getHitChance(), EPSILON);
		assertEquals(4.0 / 3.0, allAccuracyRollsFail.getExpectedDamage(), EPSILON);
	}

	@Test
	public void darkBowUsesAmmoMinimumsAndOnlyCapsDragonArrows()
	{
		SpecialAttackResult dragon = calculate(
			SpecialAttackWeapon.DARK_BOW, 40, 100, 100, false, false, true, true);
		SpecialAttackResult other = calculate(
			SpecialAttackWeapon.DARK_BOW, 50, 100, 100, false, false, true, false);

		assertArrayEquals(new int[]{48, 48}, dragon.getMaximumHits());
		assertEquals(96, dragon.getTotalMaximumHit());
		assertArrayEquals(new int[]{65, 65}, other.getMaximumHits());
		assertEquals(130, other.getTotalMaximumHit());
		assertEquals("Min 8, max 48", dragon.getOnHitText());
		assertEquals("Min 5", other.getOnHitText());
	}

	@Test
	public void targetAndAmmoAvailabilityAreReportedSeparately()
	{
		SpecialAttackResult noTarget = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.DRAGON_WARHAMMER,
			40, true, 0, 0, false, 0, false, false, false, true, false,
			true, 0, 0, 0, -1, false);
		SpecialAttackResult noArrows = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.DARK_BOW,
			40, true, 100, 100, true, 0, false, false, false, false, false,
			true, 100, 100, 0, -1, false);

		assertTrue(noTarget.isMaximumAvailable());
		assertFalse(noTarget.isAccuracyAvailable());
		assertEquals("Select target", noTarget.getHitChanceText());
		assertFalse(noArrows.isMaximumAvailable());
		assertEquals("Equip 2 arrows", noArrows.getMaximumHitText(false));
	}

	@Test
	public void immunityAndGuaranteedMaximumAreExplicit()
	{
		SpecialAttackResult immune = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.VOIDWAKER,
			50, true, 100, 100, true, 0, true, false, false, true, false,
			true, 100, 100, 0, -1, false);
		SpecialAttackResult guaranteedMaximum = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.DRAGON_DAGGER,
			50, true, 100, 100, true, 0, false, true, true, true, false,
			true, 100, 100, 0, -1, false);

		assertTrue(immune.isTargetImmune());
		assertEquals("Immune", immune.getMaximumHitText(false));
		assertEquals(114.0, guaranteedMaximum.getExpectedDamage(), EPSILON);
	}

	@Test
	public void splitDetailsCanBeHiddenForTheCompactHud()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.DRAGON_DAGGER, 50, 100, 100, false, false, true, false);

		assertEquals("114", result.getMaximumHitText(false));
		assertEquals("114 (57/57)", result.getMaximumHitText(true));
	}

	@Test
	public void tonalzticsUsesTwoReducedHitsAndImprovesTheSecondRollAfterSuccess()
	{
		SpecialAttackResult result = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.TONALZTICS_OF_RALOS,
			40, true, 100, 109, true, 0, false, false, false, true, false,
			true, 100, 100, 0, -1, false);
		double firstChance = CombatPrediction.hitChance(150, 109);
		double secondAfterHit = CombatPrediction.hitChance(150, 97);
		double secondChance = firstChance * secondAfterHit + (1.0 - firstChance) * firstChance;

		assertArrayEquals(new int[]{30, 30}, result.getMaximumHits());
		assertEquals(60, result.getTotalMaximumHit());
		assertEquals(1.0 - Math.pow(1.0 - firstChance, 2), result.getHitChance(), EPSILON);
		assertEquals(DamageRoll.averageSuccessfulHit(0, 30, 0) * (firstChance + secondChance),
			result.getExpectedDamage(), EPSILON);
	}

	@Test
	public void eyeAndAccursedUseTheirMagicOverridesAndAccuracyBoosts()
	{
		SpecialAttackResult eye = calculateWithOverride(
			SpecialAttackWeapon.EYE_OF_AYAK, 40, 100, 100, 52, false);
		SpecialAttackResult accursed = calculateWithOverride(
			SpecialAttackWeapon.ACCURSED_SCEPTRE, 40, 100, 100, 60, false);

		assertEquals(52, eye.getTotalMaximumHit());
		assertEquals(CombatPrediction.hitChance(200, 100), eye.getHitChance(), EPSILON);
		assertEquals(60, accursed.getTotalMaximumHit());
		assertEquals(CombatPrediction.hitChance(150, 100), accursed.getHitChance(), EPSILON);
	}

	@Test
	public void boneDaggerAndSeercullExposeTheirConditionalGuarantees()
	{
		SpecialAttackResult bone = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.BONE_DAGGER,
			25, true, 0, 9999, true, 0, false, false, false, true, false,
			true, 100, 100, 0, -1, true);
		SpecialAttackResult seercull = SpecialAttackCalculator.calculate(
			SpecialAttackWeapon.SEERCULL,
			50, true, 0, 9999, true, 0, false, false, false, true, false,
			true, 100, 100, 0, 17, false);

		assertEquals(1.0, bone.getHitChance(), EPSILON);
		assertEquals("Guaranteed", bone.getHitChanceText());
		assertEquals(17, seercull.getTotalMaximumHit());
		assertEquals(1.0, seercull.getHitChance(), EPSILON);
		assertEquals("Guaranteed", seercull.getHitChanceText());
	}

	@Test
	public void nonGuaranteedMultiRollChanceNeverDisplaysAsOneHundredPercent()
	{
		SpecialAttackResult result = calculate(
			SpecialAttackWeapon.DRAGON_CLAWS,
			50,
			1_000_000,
			0,
			false,
			false,
			true,
			false);

		assertEquals("99.9%", result.getHitChanceText());
	}

	private static SpecialAttackResult calculate(
		SpecialAttackWeapon weapon,
		int maximumHit,
		int attackRoll,
		int defenceRoll,
		boolean guaranteedAccuracy,
		boolean guaranteedMaximum,
		boolean ammoAvailable,
		boolean dragonArrows)
	{
		return SpecialAttackCalculator.calculate(
			weapon,
			maximumHit,
			true,
			attackRoll,
			defenceRoll,
			true,
			0,
			false,
			guaranteedAccuracy,
			guaranteedMaximum,
			ammoAvailable,
			dragonArrows,
			true,
			100,
			100,
			0,
			-1,
			false);
	}

	private static SpecialAttackResult calculateWithOverride(
		SpecialAttackWeapon weapon,
		int maximumHit,
		int attackRoll,
		int defenceRoll,
		int override,
		boolean boneDaggerGuaranteed)
	{
		return SpecialAttackCalculator.calculate(
			weapon,
			maximumHit,
			true,
			attackRoll,
			defenceRoll,
			true,
			0,
			false,
			false,
			false,
			true,
			false,
			true,
			100,
			100,
			0,
			override,
			boneDaggerGuaranteed);
	}
}
