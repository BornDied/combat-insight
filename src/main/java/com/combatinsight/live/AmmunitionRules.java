package com.combatinsight.live;

import java.util.Locale;

/**
 * Rules for ammunition stored separately from the worn ammo slot.
 *
 * Blowpipes carry their darts inside the weapon. Arrows or bolts can still be
 * equipped at the same time, but those items do not contribute to a blowpipe
 * attack and must not be added to its displayed bonuses or maximum hit.
 */
final class AmmunitionRules
{
	private AmmunitionRules()
	{
	}

	static boolean wornAmmoContributes(String weaponName)
	{
		return weaponName == null
			|| !weaponName.toLowerCase(Locale.ROOT).contains("blowpipe");
	}
}
