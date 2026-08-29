package com.combatinsight.live;

import java.util.Locale;

/**
 * Selects the ammunition the equipped weapon can actually fire.
 *
 * Dizana's quiver can provide a second arrow or bolt stack. The ordinary ammo
 * slot wins when it contains compatible ammunition; otherwise the compatible
 * quiver stack is used. An incompatible stack, such as bolts with a dark bow,
 * must never contribute stats merely because it is equipped.
 */
final class AmmunitionRules
{
	enum Source
	{
		NONE,
		WORN,
		QUIVER
	}

	private AmmunitionRules()
	{
	}

	static boolean wornAmmoContributes(String weaponName)
	{
		return weaponName == null
			|| !weaponName.toLowerCase(Locale.ROOT).contains("blowpipe");
	}

	static Source selectSource(
		String weaponName,
		String wornAmmoName,
		String quiverAmmoName)
	{
		if (isCompatible(weaponName, wornAmmoName))
		{
			return Source.WORN;
		}
		if (isCompatible(weaponName, quiverAmmoName))
		{
			return Source.QUIVER;
		}
		return Source.NONE;
	}

	static boolean sunfireBonusApplies(String weaponName, String ammoName)
	{
		String weapon = normalize(weaponName);
		String ammo = normalize(ammoName);
		return isCompatible(weapon, ammo)
			&& (isArrow(ammo) || isBolt(ammo))
			&& !weapon.contains("venator bow");
	}

	private static boolean isCompatible(String weaponName, String ammoName)
	{
		String weapon = normalize(weaponName);
		String ammo = normalize(ammoName);
		if (weapon.isEmpty() || ammo.isEmpty())
		{
			return false;
		}
		if (weapon.contains("blowpipe") || isSelfPoweredBow(weapon))
		{
			return false;
		}
		if (weapon.contains("crossbow"))
		{
			return isBolt(ammo);
		}
		if (weapon.contains("ballista"))
		{
			return ammo.contains("javelin");
		}
		if (weapon.contains("atlatl"))
		{
			return ammo.contains("atlatl dart");
		}
		if (weapon.contains("bow"))
		{
			return isArrow(ammo);
		}
		return false;
	}

	private static boolean isSelfPoweredBow(String weapon)
	{
		return weapon.contains("crystal bow")
			|| weapon.contains("bow of faerdhinen")
			|| weapon.contains("craw's bow")
			|| weapon.contains("webweaver bow");
	}

	private static boolean isArrow(String ammo)
	{
		return ammo.contains("arrow");
	}

	private static boolean isBolt(String ammo)
	{
		return ammo.contains("bolt");
	}

	private static String normalize(String value)
	{
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}
}
