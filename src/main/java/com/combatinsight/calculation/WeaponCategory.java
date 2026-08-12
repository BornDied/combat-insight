package com.combatinsight.calculation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Weapon-interface category used to resolve the selected attack type. */
public enum WeaponCategory
{
	NONE(""),
	TWO_HANDED_SWORD("2h Sword"),
	AXE("Axe"),
	BANNER("Banner"),
	BLADED_STAFF("Bladed Staff"),
	BLASTER("Blaster"),
	BLUDGEON("Bludgeon"),
	BLUNT("Blunt"),
	BOW("Bow"),
	BULWARK("Bulwark"),
	CHINCHOMPA("Chinchompas"),
	CLAW("Claw"),
	CROSSBOW("Crossbow"),
	DAGGER("Dagger"),
	FLAIL("Flail"),
	GUN("Gun"),
	MULTI_MELEE("Multi-Melee"),
	PARTISAN("Partisan"),
	PICKAXE("Pickaxe"),
	POLEARM("Polearm"),
	POLESTAFF("Polestaff"),
	POWERED_STAFF("Powered Staff"),
	POWERED_WAND("Powered Wand"),
	SALAMANDER("Salamander"),
	SCYTHE("Scythe"),
	SLASH_SWORD("Slash Sword"),
	SPEAR("Spear"),
	SPIKED("Spiked"),
	STAB_SWORD("Stab Sword"),
	STAFF("Staff"),
	THROWN("Thrown"),
	UNARMED("Unarmed"),
	WHIP("Whip"),
	UNKNOWN("?");

	private static final Map<String, WeaponCategory> BY_DATA_NAME = new HashMap<>();

	static
	{
		for (WeaponCategory category : values())
		{
			BY_DATA_NAME.put(category.dataName.toLowerCase(Locale.ROOT), category);
		}
	}

	private final String dataName;

	WeaponCategory(String dataName)
	{
		this.dataName = dataName;
	}

	public static WeaponCategory fromDataName(String name)
	{
		return name == null
			? UNKNOWN
			: BY_DATA_NAME.getOrDefault(name.toLowerCase(Locale.ROOT), UNKNOWN);
	}

	public AttackType attackType(int styleIndex)
	{
		switch (this)
		{
			case BOW:
				return AttackType.RANGED_STANDARD;
			case CROSSBOW:
			case CHINCHOMPA:
				return AttackType.RANGED_HEAVY;
			case THROWN:
				return AttackType.RANGED_LIGHT;
			case POWERED_STAFF:
			case POWERED_WAND:
				return AttackType.MAGIC;
			case SALAMANDER:
				return styleIndex == 0 ? AttackType.SLASH
					: styleIndex == 1 ? AttackType.RANGED_MIXED
						: styleIndex == 2 ? AttackType.MAGIC : AttackType.UNKNOWN;
			case TWO_HANDED_SWORD:
				return styleIndex == 2 ? AttackType.CRUSH : AttackType.SLASH;
			case AXE:
				return styleIndex == 2 ? AttackType.CRUSH : AttackType.SLASH;
			case BANNER:
				return styleIndex == 0 || styleIndex == 3 ? AttackType.STAB
					: styleIndex == 1 ? AttackType.SLASH : AttackType.CRUSH;
			case BLADED_STAFF:
				if (styleIndex == 3 || styleIndex == 4 || styleIndex == 5)
				{
					return AttackType.MAGIC;
				}
				return styleIndex == 0 ? AttackType.STAB
					: styleIndex == 1 ? AttackType.SLASH : AttackType.CRUSH;
			case BULWARK:
			case BLUDGEON:
			case BLUNT:
			case POLESTAFF:
			case UNARMED:
			case NONE:
				return AttackType.CRUSH;
			case MULTI_MELEE:
				return styleIndex == 0 ? AttackType.STAB
					: styleIndex == 2 ? AttackType.CRUSH : AttackType.SLASH;
			case PARTISAN:
			case PICKAXE:
				return styleIndex == 2 ? AttackType.CRUSH : AttackType.STAB;
			case POLEARM:
				return styleIndex == 1 ? AttackType.SLASH : AttackType.STAB;
			case CLAW:
				return styleIndex == 2 ? AttackType.STAB : AttackType.SLASH;
			case SPIKED:
				return styleIndex == 2 ? AttackType.STAB : AttackType.CRUSH;
			case STAFF:
				return styleIndex == 3 || styleIndex == 4 || styleIndex == 5
					? AttackType.MAGIC : AttackType.CRUSH;
			case SCYTHE:
				return styleIndex == 2 ? AttackType.CRUSH : AttackType.SLASH;
			case SLASH_SWORD:
				return styleIndex == 2 ? AttackType.STAB : AttackType.SLASH;
			case SPEAR:
				return styleIndex == 1 ? AttackType.SLASH
					: styleIndex == 2 ? AttackType.CRUSH : AttackType.STAB;
			case STAB_SWORD:
			case DAGGER:
				return styleIndex == 2 ? AttackType.SLASH : AttackType.STAB;
			case WHIP:
			case FLAIL:
				return AttackType.SLASH;
			case GUN:
				return AttackType.CRUSH;
			case BLASTER:
			default:
				return AttackType.UNKNOWN;
		}
	}
}
