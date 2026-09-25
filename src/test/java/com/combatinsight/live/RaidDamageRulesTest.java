package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import org.junit.Test;
import static org.junit.Assert.*;

public class RaidDamageRulesTest
{
	@Test
	public void unsupportedPhaseDamageIsExplicitInsteadOfOrdinaryDps()
	{
		assertFalse(RaidDamageRules.unavailableReason(8369, AttackType.SLASH, false).isEmpty());
		assertFalse(RaidDamageRules.unavailableReason(7545, AttackType.MAGIC, false).isEmpty());
		assertFalse(RaidDamageRules.unavailableReason(7555, AttackType.RANGED_STANDARD, false).isEmpty());
		assertFalse(RaidDamageRules.unavailableReason(7585, AttackType.MAGIC, false).isEmpty());
		assertFalse(RaidDamageRules.unavailableReason(7570, AttackType.CRUSH, false).isEmpty());
	}

	@Test
	public void regularRaidAttacksRemainAvailable()
	{
		assertEquals("", RaidDamageRules.unavailableReason(8374, AttackType.SLASH, false));
		assertEquals("", RaidDamageRules.unavailableReason(7552, AttackType.SLASH, false));
		assertEquals("", RaidDamageRules.unavailableReason(7550, AttackType.MAGIC, false));
		assertEquals("", RaidDamageRules.unavailableReason(7584, AttackType.MAGIC, true));
	}
}
