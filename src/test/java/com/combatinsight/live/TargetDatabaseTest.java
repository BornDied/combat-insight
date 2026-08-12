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
		assertTrue(TargetDatabase.size() > 2800);
		TargetProfile scurrius = TargetDatabase.find(7222);
		assertNotNull(scurrius);
		assertFalse(scurrius.isAmbiguous());
		assertEquals(60, scurrius.getDefenceLevel());
		assertEquals(50, scurrius.getMagicLevel());
		assertEquals(20, scurrius.defenceBonus(AttackType.STAB));
		assertEquals(20, scurrius.defenceBonus(AttackType.RANGED_STANDARD));
		assertTrue(scurrius.hasAttribute("rat"));
	}
}
