package com.combatinsight.calculation;

import java.util.Locale;

/** Weapons supported by Combat Insight's local special-attack waves. */
public enum SpecialAttackWeapon
{
	DRAGON_WARHAMMER(
		"Dragon warhammer",
		CombatStyle.MELEE,
		AttackType.CRUSH,
		"Defence -30% current",
		true),
	ELDER_MAUL(
		"Elder maul",
		CombatStyle.MELEE,
		AttackType.CRUSH,
		"Defence -35% current",
		true),
	BANDOS_GODSWORD(
		"Bandos godsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"Defence -damage first",
		true),
	ARMADYL_GODSWORD(
		"Armadyl godsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"25% increased damage",
		false),
	SARADOMIN_GODSWORD(
		"Saradomin godsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"HP 50%/10; Prayer 25%/5",
		false),
	ZAMORAK_GODSWORD(
		"Zamorak godsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"Freeze target for 20 sec",
		false),
	ANCIENT_GODSWORD(
		"Ancient godsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"8 ticks: +25 dmg, +25 HP",
		false),
	BURNING_CLAWS(
		"Burning claws",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"3 hits; 15-45% burns",
		false),
	DRAGON_CLAWS(
		"Dragon claws",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"Four-roll hit sequence",
		false),
	VOIDWAKER(
		"Voidwaker",
		CombatStyle.MAGIC,
		AttackType.MAGIC,
		"Guaranteed Magic hit",
		false),
	DRAGON_DAGGER(
		"Dragon dagger",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"Two independent hits",
		false),
	DARK_BOW(
		"Dark bow",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"Minimum depends on arrows",
		false),
	TONALZTICS_OF_RALOS(
		"Tonalztics of ralos",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"Each hit: Defence -12.5% Magic",
		true,
		2),
	EYE_OF_AYAK(
		"Eye of ayak",
		CombatStyle.MAGIC,
		AttackType.MAGIC,
		"Magic defence -damage",
		true),
	ACCURSED_SCEPTRE(
		"Accursed sceptre",
		CombatStyle.MAGIC,
		AttackType.MAGIC,
		"Defence and Magic -15% max",
		true),
	BONE_DAGGER(
		"Bone dagger",
		CombatStyle.MELEE,
		AttackType.STAB,
		"Defence -damage if undrained",
		true),
	ARCLIGHT(
		"Arclight",
		CombatStyle.MELEE,
		AttackType.STAB,
		"Atk/Str/Def drain; stronger on demons",
		true),
	EMBERLIGHT(
		"Emberlight",
		CombatStyle.MELEE,
		AttackType.STAB,
		"Atk/Str/Def drain; stronger on demons",
		true),
	SEERCULL(
		"Seercull",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"Magic -damage if undrained",
		true),
	CRIMSON_KISTEN(
		"Crimson kisten",
		CombatStyle.MELEE,
		AttackType.CRUSH,
		"4 Crush rolls; 70-170% hit",
		false),
	DRAGON_LONGSWORD(
		"Dragon longsword",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"25% increased damage",
		false),
	DRAGON_MACE(
		"Dragon mace",
		CombatStyle.MELEE,
		AttackType.CRUSH,
		"50% increased damage",
		false),
	DRAGON_SWORD(
		"Dragon sword",
		CombatStyle.MELEE,
		AttackType.STAB,
		"25% damage; ignores Protect Melee",
		false),
	ABYSSAL_DAGGER(
		"Abyssal dagger",
		CombatStyle.MELEE,
		AttackType.SLASH,
		"2 hits; one accuracy roll",
		false),
	TOXIC_BLOWPIPE(
		"Toxic blowpipe",
		CombatStyle.RANGED,
		AttackType.RANGED_LIGHT,
		"Heals 50% of damage dealt",
		false),
	WEBWEAVER_BOW(
		"Webweaver bow",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"4 hits; poison not averaged",
		false),
	MAGIC_SHORTBOW(
		"Magic shortbow",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"2 arrows; custom max hit",
		false),
	ROSEWOOD_BLOWPIPE(
		"Rosewood blowpipe",
		CombatStyle.RANGED,
		AttackType.RANGED_LIGHT,
		"2 darts; normal accuracy/damage",
		false),
	DRAGON_KNIFE(
		"Dragon knife",
		CombatStyle.RANGED,
		AttackType.RANGED_LIGHT,
		"2 independent hits",
		false),
	MAGIC_LONGBOW(
		"Magic longbow",
		CombatStyle.RANGED,
		AttackType.RANGED_STANDARD,
		"Guaranteed hit; custom max",
		false);

	private final String displayName;
	private final CombatStyle combatStyle;
	private final AttackType attackType;
	private final String onHitText;
	private final boolean trackedTargetEffect;
	private final int effectHitCount;

	SpecialAttackWeapon(
		String displayName,
		CombatStyle combatStyle,
		AttackType attackType,
		String onHitText,
		boolean trackedTargetEffect)
	{
		this(displayName, combatStyle, attackType, onHitText, trackedTargetEffect, 1);
	}

	SpecialAttackWeapon(
		String displayName,
		CombatStyle combatStyle,
		AttackType attackType,
		String onHitText,
		boolean trackedTargetEffect,
		int effectHitCount)
	{
		this.displayName = displayName;
		this.combatStyle = combatStyle;
		this.attackType = attackType;
		this.onHitText = onHitText;
		this.trackedTargetEffect = trackedTargetEffect;
		this.effectHitCount = Math.max(1, effectHitCount);
	}

	public static SpecialAttackWeapon forWeapon(String weaponName)
	{
		if (weaponName == null)
		{
			return null;
		}

		String weapon = weaponName.toLowerCase(Locale.ROOT);
		if (weapon.contains("dragon warhammer"))
		{
			return DRAGON_WARHAMMER;
		}
		if (weapon.contains("elder maul"))
		{
			return ELDER_MAUL;
		}
		if (weapon.contains("bandos godsword"))
		{
			return BANDOS_GODSWORD;
		}
		if (weapon.contains("armadyl godsword"))
		{
			return ARMADYL_GODSWORD;
		}
		if (weapon.contains("saradomin godsword"))
		{
			return SARADOMIN_GODSWORD;
		}
		if (weapon.contains("zamorak godsword"))
		{
			return ZAMORAK_GODSWORD;
		}
		if (weapon.contains("ancient godsword"))
		{
			return ANCIENT_GODSWORD;
		}
		if (weapon.contains("burning claws"))
		{
			return BURNING_CLAWS;
		}
		if (weapon.contains("dragon claws"))
		{
			return DRAGON_CLAWS;
		}
		if (weapon.contains("voidwaker"))
		{
			return VOIDWAKER;
		}
		if (weapon.contains("dragon dagger"))
		{
			return DRAGON_DAGGER;
		}
		if (weapon.contains("dark bow"))
		{
			return DARK_BOW;
		}
		if (weapon.contains("tonalztics of ralos"))
		{
			return TONALZTICS_OF_RALOS;
		}
		if (weapon.contains("eye of ayak"))
		{
			return EYE_OF_AYAK;
		}
		if (weapon.contains("accursed sceptre"))
		{
			return ACCURSED_SCEPTRE;
		}
		if (weapon.contains("bone dagger"))
		{
			return BONE_DAGGER;
		}
		if (weapon.contains("emberlight"))
		{
			return EMBERLIGHT;
		}
		if (weapon.contains("arclight"))
		{
			return ARCLIGHT;
		}
		if (weapon.contains("seercull"))
		{
			return SEERCULL;
		}
		if (weapon.contains("crimson kisten"))
		{
			return CRIMSON_KISTEN;
		}
		if (weapon.contains("dragon longsword"))
		{
			return DRAGON_LONGSWORD;
		}
		if (weapon.contains("dragon mace"))
		{
			return DRAGON_MACE;
		}
		if (weapon.contains("dragon sword"))
		{
			return DRAGON_SWORD;
		}
		if (weapon.contains("abyssal dagger"))
		{
			return ABYSSAL_DAGGER;
		}
		if (weapon.contains("toxic blowpipe") || weapon.contains("blazing blowpipe"))
		{
			return TOXIC_BLOWPIPE;
		}
		if (weapon.contains("webweaver bow"))
		{
			return WEBWEAVER_BOW;
		}
		if (weapon.contains("magic shortbow"))
		{
			return MAGIC_SHORTBOW;
		}
		if (weapon.contains("rosewood blowpipe"))
		{
			return ROSEWOOD_BLOWPIPE;
		}
		if (weapon.contains("dragon knife"))
		{
			return DRAGON_KNIFE;
		}
		if (weapon.contains("magic longbow") || weapon.contains("magic comp bow"))
		{
			return MAGIC_LONGBOW;
		}
		return null;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public CombatStyle getCombatStyle()
	{
		return combatStyle;
	}

	public AttackType getAttackType()
	{
		return attackType;
	}

	public String getOnHitText()
	{
		return onHitText;
	}

	public boolean hasTrackedTargetEffect()
	{
		return trackedTargetEffect;
	}

	public int getEffectHitCount()
	{
		return effectHitCount;
	}
}
