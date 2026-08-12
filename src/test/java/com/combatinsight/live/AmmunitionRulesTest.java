package com.combatinsight.live;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AmmunitionRulesTest
{
	@Test
	public void ignoresWornAmmoForBlowpipes()
	{
		assertFalse(AmmunitionRules.wornAmmoContributes("Toxic blowpipe"));
		assertFalse(AmmunitionRules.wornAmmoContributes("Blazing blowpipe"));
	}

	@Test
	public void keepsWornAmmoForOrdinaryRangedWeapons()
	{
		assertTrue(AmmunitionRules.wornAmmoContributes("Twisted bow"));
		assertTrue(AmmunitionRules.wornAmmoContributes("Armadyl crossbow"));
	}
}
