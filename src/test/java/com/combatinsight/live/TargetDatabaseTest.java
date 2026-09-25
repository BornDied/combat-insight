package com.combatinsight.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;

public class TargetDatabaseTest
{
	@Test
	public void loadsTheBundledMonsterSnapshot()
	{
		assertEquals(3976, TargetDatabase.size());
		TargetProfile scurrius = TargetDatabase.find(7222);
		assertNotNull(scurrius);
		assertFalse(scurrius.isAmbiguous());
		assertEquals(60, scurrius.getDefenceLevel());
		assertEquals(50, scurrius.getMagicLevel());
		assertEquals(20, scurrius.defenceBonus(AttackType.STAB));
		assertEquals(20, scurrius.defenceBonus(AttackType.RANGED_STANDARD));
		assertTrue(scurrius.hasAttribute("rat"));
	}

	@Test
	public void loadsEveryAttackablePhosanisNightmarePhase()
	{
		int[] phaseIds = {
			9416, 9417, 9418, 9419, 9420, 9421, 9422, 9424,
			11153, 11154, 11155
		};

		for (int phaseId : phaseIds)
		{
			TargetProfile phosani = TargetDatabase.find(phaseId);
			assertNotNull(phosani);
			assertFalse(phosani.isAmbiguous());
			assertEquals(150, phosani.getDefenceLevel());
			assertEquals(150, phosani.getMagicLevel());
			assertEquals(3200, phosani.getHitpoints());
			assertEquals(40, phosani.defenceBonus(AttackType.CRUSH));
		}
	}

	@Test
	public void loadsNewAndAlternateNpcIdsFromTheFullWikiIdLists()
	{
		TargetProfile alternateAkkha = TargetDatabase.find(11796);
		TargetProfile vampyreSnail = TargetDatabase.find(16344);

		assertNotNull(alternateAkkha);
		assertNotNull(vampyreSnail);
		assertEquals(300, vampyreSnail.getDefenceLevel());
		assertEquals(500, vampyreSnail.getHitpoints());
	}
}
