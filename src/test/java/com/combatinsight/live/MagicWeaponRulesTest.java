package com.combatinsight.live;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MagicWeaponRulesTest
{
	@Test
	public void detectsPoweredAndBuiltInMagicWeaponsWithoutAutocast()
	{
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Trident of the swamp"));
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Sanguinesti staff"));
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Tumeken's shadow"));
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Bone staff"));
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Eye of ayak"));
		assertTrue(MagicWeaponRules.hasBuiltInAttack("Crystal staff (perfected)"));
		assertFalse(MagicWeaponRules.hasBuiltInAttack("Iban's staff"));
	}

	@Test
	public void recognizesManualCastingWeapons()
	{
		assertTrue(MagicWeaponRules.canCastManualSpell("Iban's staff", 10));
		assertTrue(MagicWeaponRules.canCastManualSpell("Kodai wand", 28));
		assertFalse(MagicWeaponRules.canCastManualSpell("Twisted bow", 0));
	}
}
