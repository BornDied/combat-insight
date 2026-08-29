package com.combatinsight.calculation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Immutable combat data for one NPC ID. */
public final class TargetProfile
{
	private final int id;
	private final int defenceLevel;
	private final int magicLevel;
	private final int hitpoints;
	private final int offensiveMagic;
	private final int size;
	private final int flatArmour;
	private final int stabDefence;
	private final int slashDefence;
	private final int crushDefence;
	private final int magicDefence;
	private final int lightRangedDefence;
	private final int standardRangedDefence;
	private final int heavyRangedDefence;
	private final String weaknessElement;
	private final int weaknessSeverity;
	private final boolean slayerMonster;
	private final Set<String> attributes;
	private final boolean ambiguous;

	public TargetProfile(
		int id,
		int defenceLevel,
		int magicLevel,
		int hitpoints,
		int offensiveMagic,
		int size,
		int flatArmour,
		int stabDefence,
		int slashDefence,
		int crushDefence,
		int magicDefence,
		int lightRangedDefence,
		int standardRangedDefence,
		int heavyRangedDefence,
		String weaknessElement,
		int weaknessSeverity,
		boolean slayerMonster,
		Set<String> attributes,
		boolean ambiguous)
	{
		this.id = id;
		this.defenceLevel = defenceLevel;
		this.magicLevel = magicLevel;
		this.hitpoints = hitpoints;
		this.offensiveMagic = offensiveMagic;
		this.size = size;
		this.flatArmour = flatArmour;
		this.stabDefence = stabDefence;
		this.slashDefence = slashDefence;
		this.crushDefence = crushDefence;
		this.magicDefence = magicDefence;
		this.lightRangedDefence = lightRangedDefence;
		this.standardRangedDefence = standardRangedDefence;
		this.heavyRangedDefence = heavyRangedDefence;
		this.weaknessElement = weaknessElement == null ? "" : weaknessElement;
		this.weaknessSeverity = Math.max(0, weaknessSeverity);
		this.slayerMonster = slayerMonster;
		this.attributes = Collections.unmodifiableSet(new HashSet<>(attributes));
		this.ambiguous = ambiguous;
	}

	public int getId()
	{
		return id;
	}

	public int getDefenceLevel()
	{
		return defenceLevel;
	}

	public int getMagicLevel()
	{
		return magicLevel;
	}

	public int getHitpoints()
	{
		return hitpoints;
	}

	public int getOffensiveMagic()
	{
		return offensiveMagic;
	}

	public int getSize()
	{
		return size;
	}

	public int getFlatArmour()
	{
		return flatArmour;
	}

	public String getWeaknessElement()
	{
		return weaknessElement;
	}

	public int getWeaknessSeverity()
	{
		return weaknessSeverity;
	}

	public boolean isSlayerMonster()
	{
		return slayerMonster;
	}

	public boolean hasAttribute(String attribute)
	{
		return attributes.contains(attribute);
	}

	public boolean isAmbiguous()
	{
		return ambiguous;
	}

	/** Returns a derived live profile without mutating the bundled target data. */
	public TargetProfile withCombatStats(int liveDefenceLevel, int liveMagicLevel, int liveMagicDefence)
	{
		return new TargetProfile(
			id,
			Math.max(0, liveDefenceLevel),
			Math.max(0, liveMagicLevel),
			hitpoints,
			offensiveMagic,
			size,
			flatArmour,
			stabDefence,
			slashDefence,
			crushDefence,
			liveMagicDefence,
			lightRangedDefence,
			standardRangedDefence,
			heavyRangedDefence,
			weaknessElement,
			weaknessSeverity,
			slayerMonster,
			attributes,
			ambiguous);
	}

	public int defenceBonus(AttackType attackType)
	{
		switch (attackType)
		{
			case STAB:
				return stabDefence;
			case SLASH:
				return slashDefence;
			case CRUSH:
				return crushDefence;
			case MAGIC:
				return magicDefence;
			case RANGED_LIGHT:
				return lightRangedDefence;
			case RANGED_HEAVY:
				return heavyRangedDefence;
			case RANGED_MIXED:
				return (lightRangedDefence + standardRangedDefence + heavyRangedDefence) / 3;
			case RANGED_STANDARD:
			default:
				return standardRangedDefence;
		}
	}
}
