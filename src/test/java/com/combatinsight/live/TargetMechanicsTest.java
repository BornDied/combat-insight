package com.combatinsight.live;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TargetMechanicsTest
{
	@Test
	public void recognizesOnlyScurriusSpawnedRatAsGuaranteedMaxHit()
	{
		assertTrue(TargetMechanics.isGuaranteedMaxHitTarget(7223));
		assertFalse(TargetMechanics.isGuaranteedMaxHitTarget(7221));
		assertFalse(TargetMechanics.isGuaranteedMaxHitTarget(2854));
	}
}
