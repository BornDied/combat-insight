package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;
import static org.junit.Assert.*;

public class RaidScalingTest
{
	@Test
	public void soloNormalAndChallengeModeTektonUseDifferentDefence()
	{
		assertEquals(205, new RaidScaling(1, false, 126, 99, 99, 3).apply(TargetDatabase.find(7540)).getDefenceLevel());
		assertEquals(246, new RaidScaling(1, true, 126, 99, 99, 3).apply(TargetDatabase.find(7540)).getDefenceLevel());
		assertEquals(246, new RaidScaling(1, false, 126, 99, 99, 3).apply(TargetDatabase.find(7545)).getDefenceLevel());
	}

	@Test
	public void challengeModeOlmMageHandIsScaledExactlyOnce()
	{
		TargetProfile olm = new RaidScaling(1, true, 126, 99, 99, 3).apply(TargetDatabase.find(7553));
		assertEquals(262, olm.getDefenceLevel());
		assertEquals(131, olm.getMagicLevel());
		assertEquals(600, olm.getHitpoints());
	}

	@Test
	public void partySizeAndMemberLevelsAffectChambers()
	{
		TargetProfile tekton = new RaidScaling(3, false, 126, 99, 99, 3).apply(TargetDatabase.find(7540));
		assertEquals(209, tekton.getDefenceLevel());
		assertEquals(600, tekton.getHitpoints());
		assertEquals(284, new RaidScaling(4, true, 126, 99, 99, 3).apply(TargetDatabase.find(7540)).getDefenceLevel());
		assertEquals(1600, new RaidScaling(3, false, 126, 99, 99, 3).apply(TargetDatabase.find(7551)).getHitpoints());
		assertTrue(new RaidScaling(1, false, 90, 75, 70, 3).apply(TargetDatabase.find(7540)).getDefenceLevel() < 205);
	}

	@Test
	public void theatreScalesHealthWithoutChangingDefence()
	{
		TargetProfile maiden = new RaidScaling(1, false, 126, 99, 99, 3).apply(TargetDatabase.find(8362));
		assertEquals(2625, maiden.getHitpoints());
		assertEquals(200, maiden.getDefenceLevel());
		assertEquals(500, new RaidScaling(1, false, 126, 99, 99, 1).apply(TargetDatabase.find(10814)).getHitpoints());
		assertEquals(3500, new RaidScaling(1, false, 126, 99, 99, 5).apply(TargetDatabase.find(8360)).getHitpoints());
	}

	@Test
	public void hardModeXarpusNeverUsesEntryScaling()
	{
		for (int id : new int[]{10770, 10771, 10772})
		{
			assertFalse(RaidScaling.isEntryMode(id));
		}
		assertEquals(4500, new RaidScaling(1, false, 126, 99, 99, 3).apply(TargetDatabase.find(10772)).getHitpoints());
		assertEquals(4500, new RaidScaling(1, false, 126, 99, 99, 1).apply(TargetDatabase.find(10772)).getHitpoints());
	}

	@Test
	public void defenceReductionStartsFromScaledStats()
	{
		TargetEffectState state = new TargetEffectState(new RaidScaling(1, true, 126, 99, 99, 3).apply(TargetDatabase.find(7540)));
		state.apply(SpecialAttackWeapon.DRAGON_WARHAMMER, 20, true, "Tekton", 1);
		assertEquals(173, state.snapshot().getCurrentDefence());
	}

	@Test
	public void ordinaryTargetsAreUnaffectedAndInputsAreClamped()
	{
		TargetProfile nex = TargetDatabase.find(11278);
		assertSame(nex, new RaidScaling(1000, true, 1000, 1000, 1000, 1000).apply(nex));
		assertEquals(3500, new RaidScaling(1, false, 126, 99, 99, 999).apply(TargetDatabase.find(8360)).getHitpoints());
	}
}
