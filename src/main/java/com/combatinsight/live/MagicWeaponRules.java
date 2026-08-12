package com.combatinsight.live;

import java.util.Locale;

/** Classifies weapons whose Magic attack is not represented by autocast. */
final class MagicWeaponRules
{
	private MagicWeaponRules()
	{
	}

	static boolean hasBuiltInAttack(String weaponName)
	{
		String name = normalize(weaponName);
		return name.contains("trident")
			|| name.contains("sanguinesti staff")
			|| name.contains("tumeken's shadow")
			|| name.contains("warped sceptre")
			|| name.contains("thammaron's sceptre")
			|| name.contains("accursed sceptre")
			|| name.contains("bone staff")
			|| name.contains("eye of ayak")
			|| name.contains("starter staff")
			|| name.contains("dawnbringer")
			|| name.contains("crystal staff")
			|| name.contains("corrupted staff");
	}

	static boolean canCastManualSpell(String weaponName, int magicAttackBonus)
	{
		String name = normalize(weaponName);
		return name.contains("staff")
			|| name.contains("wand")
			|| name.contains("sceptre")
			|| magicAttackBonus > 0;
	}

	private static String normalize(String weaponName)
	{
		return weaponName == null ? "" : weaponName.toLowerCase(Locale.ROOT);
	}
}
