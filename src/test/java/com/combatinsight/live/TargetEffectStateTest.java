package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TargetEffectStateTest
{
	@Test
	public void percentageReductionsUseTheCurrentDefenceLevel()
	{
		TargetEffectState warhammer = new TargetEffectState(target(1, 100));
		warhammer.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 1, "Target", 0);
		assertEquals(70, warhammer.snapshot().getCurrentDefence());
		warhammer.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 1, "Target", 1);
		assertEquals(49, warhammer.snapshot().getCurrentDefence());

		TargetEffectState elderMaul = new TargetEffectState(target(1, 100));
		elderMaul.apply(SpecialAttackWeapon.ELDER_MAUL, 1, "Target", 0);
		assertEquals(65, elderMaul.snapshot().getCurrentDefence());
	}

	@Test
	public void bandosGodswordDrainsDisplayedDamageFromDefenceFirst()
	{
		TargetEffectState state = new TargetEffectState(target(1, 100));
		state.apply(SpecialAttackWeapon.BANDOS_GODSWORD, 37, "Target", 0);

		assertEquals(63, state.snapshot().getCurrentDefence());
	}

	@Test
	public void knownTargetFloorsStopFurtherDefenceReduction()
	{
		TargetEffectState nex = new TargetEffectState(target(11278, 300));
		nex.apply(SpecialAttackWeapon.BANDOS_GODSWORD, 100, "Nex", 0);
		assertEquals(250, nex.snapshot().getCurrentDefence());

		TargetEffectState verzik = new TargetEffectState(target(8372, 200));
		verzik.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 1, "Verzik Vitur", 0);
		assertEquals(200, verzik.snapshot().getCurrentDefence());
	}

	@Test
	public void tektonMissesDrainFivePercentAndEndTheFirstSpecState()
	{
		TargetEffectState state = new TargetEffectState(target(7540, 205));
		state.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 0, "Tekton", 0);
		TargetEffectSnapshot snapshot = state.snapshot();

		assertEquals(195, snapshot.getCurrentDefence());
		assertTrue(snapshot.isTektonDefenceSpecialAttempted());
	}

	@Test
	public void naturalLevelRecoveryIsMarkedAsAnEstimate()
	{
		TargetEffectState state = new TargetEffectState(target(1, 100));
		state.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 1, "Target", 0);
		state.advance(TargetEffectState.RECOVERY_INTERVAL_TICKS - 1);
		assertEquals(70, state.snapshot().getCurrentDefence());

		state.advance(TargetEffectState.RECOVERY_INTERVAL_TICKS);
		TargetEffectSnapshot snapshot = state.snapshot();
		assertEquals(71, snapshot.getCurrentDefence());
		assertTrue(snapshot.isEstimatedRecovery());
		assertTrue(snapshot.getDefenceText().endsWith("est."));
	}

	@Test
	public void unmodifiedSnapshotsDoNotClaimAnObservedDrain()
	{
		TargetEffectSnapshot snapshot = TargetEffectSnapshot.unmodified(target(1, 100));

		assertTrue(snapshot.hasTargetStats());
		assertFalse(snapshot.hasTrackedEffect());
		assertTrue(snapshot.displaysMagicDefenceBonus());
		assertEquals("100 / 100", snapshot.getDefenceText());
	}

	@Test
	public void tonalzticsAppliesBothMagicBasedDefenceReductions()
	{
		TargetEffectState state = new TargetEffectState(target(1, 100, 80, 0, false));
		state.apply(SpecialAttackWeapon.TONALZTICS_OF_RALOS, 10, "Target", 0);
		state.apply(SpecialAttackWeapon.TONALZTICS_OF_RALOS, 10, "Target", 0);

		assertEquals(80, state.snapshot().getCurrentDefence());
	}

	@Test
	public void accurateZeroDamageHitsStillApplyNonDamageBasedEffects()
	{
		TargetEffectState tonalztics = new TargetEffectState(target(1, 100, 100, 0, false));
		tonalztics.apply(
			SpecialAttackWeapon.TONALZTICS_OF_RALOS, 0, true, "Target", 0);
		assertEquals(88, tonalztics.snapshot().getCurrentDefence());

		TargetEffectState accursed = new TargetEffectState(target(1, 100, 100, 0, false));
		accursed.apply(SpecialAttackWeapon.ACCURSED_SCEPTRE, 0, true, "Target", 0);
		assertEquals(85, accursed.snapshot().getCurrentDefence());
	}

	@Test
	public void eyeDrainsMagicDefenceWithoutNaturalRecovery()
	{
		TargetEffectState state = new TargetEffectState(target(1, 100, 100, 50, false));
		state.apply(SpecialAttackWeapon.EYE_OF_AYAK, 17, "Target", 0);
		state.advance(TargetEffectState.RECOVERY_INTERVAL_TICKS * 2);

		TargetEffectSnapshot snapshot = state.snapshot();
		assertEquals(33, snapshot.getCurrentMagicDefence());
		assertTrue(snapshot.hasTrackedMagicDefence());
		assertTrue(snapshot.displaysMagicDefenceBonus());
		assertEquals("+33 / +50 tracked", snapshot.getMagicDefenceText());
	}

	@Test
	public void accursedStopsAtFifteenPercentOfBaseDefenceAndMagic()
	{
		TargetEffectState state = new TargetEffectState(target(1, 100, 80, 0, false));
		state.apply(SpecialAttackWeapon.ACCURSED_SCEPTRE, 1, "Target", 0);
		state.apply(SpecialAttackWeapon.ACCURSED_SCEPTRE, 1, "Target", 1);

		TargetEffectSnapshot snapshot = state.snapshot();
		assertEquals(85, snapshot.getCurrentDefence());
		assertEquals(68, snapshot.getCurrentMagic());
		assertTrue(snapshot.hasTrackedMagic());
		assertFalse(snapshot.displaysMagicDefenceBonus());
	}

	@Test
	public void conditionalDamageDrainsDoNotStackAfterTheStatIsLowered()
	{
		TargetEffectState bone = new TargetEffectState(target(1, 100, 100, 0, false));
		bone.apply(SpecialAttackWeapon.BONE_DAGGER, 20, "Target", 0);
		bone.apply(SpecialAttackWeapon.BONE_DAGGER, 20, "Target", 1);
		assertEquals(80, bone.snapshot().getCurrentDefence());

		TargetEffectState seercull = new TargetEffectState(target(1, 100, 100, 0, false));
		seercull.apply(SpecialAttackWeapon.SEERCULL, 35, "Target", 0);
		seercull.apply(SpecialAttackWeapon.SEERCULL, 35, "Target", 1);
		assertEquals(65, seercull.snapshot().getCurrentMagic());
		assertFalse(seercull.snapshot().displaysMagicDefenceBonus());
	}

	@Test
	public void demonbaneSpecialsUseTheirStrongerDemonReductions()
	{
		TargetEffectState normal = new TargetEffectState(target(1, 100, 100, 0, false));
		normal.apply(SpecialAttackWeapon.ARCLIGHT, 1, "Target", 0);
		assertEquals(94, normal.snapshot().getCurrentDefence());

		TargetEffectState arclight = new TargetEffectState(target(1, 100, 100, 0, true));
		arclight.apply(SpecialAttackWeapon.ARCLIGHT, 1, "Demon", 0);
		assertEquals(88, arclight.snapshot().getCurrentDefence());

		TargetEffectState emberlight = new TargetEffectState(target(1, 100, 100, 0, true));
		emberlight.apply(SpecialAttackWeapon.EMBERLIGHT, 1, "Demon", 0);
		assertEquals(84, emberlight.snapshot().getCurrentDefence());
	}

	private static TargetProfile target(int id, int defence)
	{
		return target(id, defence, 100, 0, false);
	}

	private static TargetProfile target(
		int id,
		int defence,
		int magic,
		int magicDefence,
		boolean demon)
	{
		return new TargetProfile(
			id,
			defence,
			magic,
			100,
			0,
			1,
			0,
			0,
			0,
			0,
			magicDefence,
			0,
			0,
			0,
			"",
			0,
			false,
			demon ? Collections.singleton("demon") : Collections.emptySet(),
			false);
	}
}
