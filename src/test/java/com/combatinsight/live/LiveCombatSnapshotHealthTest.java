package com.combatinsight.live;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
