package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.TargetProfile;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LiveCombatSnapshotHealthTest
{
	@Test
	public void estimatesCurrentHitpointsFromHealthBarRatio()
	{
		assertEquals(401, LiveCombatSnapshot.estimateTargetCurrentHitpoints(16, 30, 750));
		assertEquals(750, LiveCombatSnapshot.estimateTargetCurrentHitpoints(30, 30, 750));
		assertEquals(13, LiveCombatSnapshot.estimateTargetCurrentHitpoints(1, 30, 750));
	}

	@Test
	public void recoversExactHitpointsWhenMaximumFitsHealthScale()
	{
		assertEquals(17, LiveCombatSnapshot.estimateTargetCurrentHitpoints(17, 30, 30));
	}

	@Test
	public void handlesDeadAndUnavailableHealth()
	{
		assertEquals(0, LiveCombatSnapshot.estimateTargetCurrentHitpoints(0, 30, 750));
		assertEquals(-1, LiveCombatSnapshot.estimateTargetCurrentHitpoints(-1, 30, 750));
		assertEquals(-1, LiveCombatSnapshot.estimateTargetCurrentHitpoints(16, -1, 750));
		assertEquals(-1, LiveCombatSnapshot.estimateTargetCurrentHitpoints(31, 30, 750));
		assertEquals(-1, LiveCombatSnapshot.estimateTargetCurrentHitpoints(16, 30, 0));
	}

	@Test
	public void inquisitorNoticeOnlyAppearsOutsideCrush()
	{
		assertFalse(LiveCombatSnapshot.shouldShowInquisitorAttackTypeWarning(1, AttackType.CRUSH));
		assertTrue(LiveCombatSnapshot.shouldShowInquisitorAttackTypeWarning(1, AttackType.STAB));
		assertTrue(LiveCombatSnapshot.shouldShowInquisitorAttackTypeWarning(1, AttackType.SLASH));
		assertFalse(LiveCombatSnapshot.shouldShowInquisitorAttackTypeWarning(0, AttackType.STAB));
	}

	@Test
	public void targetTypeNoticeOnlyAppearsWithoutSupportedTargetData()
	{
		assertFalse(LiveCombatSnapshot.shouldShowTargetTypeWarning("Emberlight", true));
		assertFalse(LiveCombatSnapshot.shouldShowTargetTypeWarning("Arclight", true));
		assertTrue(LiveCombatSnapshot.shouldShowTargetTypeWarning("Emberlight", false));
		assertFalse(LiveCombatSnapshot.shouldShowTargetTypeWarning("Dragon dagger", false));
	}

	@Test
	public void calculatesTheCompleteMagicDefenceRoll()
	{
		TargetProfile scurrius = new TargetProfile(
			7222, 60, 1, 500, 1, 3, 0,
			0, 0, 0, 10, 0, 0, 0,
			"", 0, false, Collections.emptySet(), false);
		assertEquals(740, LiveCombatSnapshot.targetMagicDefenceRoll(scurrius, 7222, 0));
		assertEquals(640, LiveCombatSnapshot.targetMagicDefenceRoll(
			scurrius.withCombatStats(60, 1, 0), 7222, 0));
	}

	@Test
	public void seercullSpecialUsesOnlyRangedLevelAndAmmoStrength()
	{
		assertEquals(21, LiveCombatSnapshot.seercullMaximumHit(99, 60));
		assertEquals(7, LiveCombatSnapshot.seercullMaximumHit(50, 10));
	}
}
