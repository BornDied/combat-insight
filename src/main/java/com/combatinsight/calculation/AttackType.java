package com.combatinsight.calculation;

/**
 * The exact offensive roll used by the player. Melee types map directly to
 * stab/slash/crush defence, while ranged types use the post-rebalance light,
 * standard, and heavy ranged defences.
 */
public enum AttackType
{
	STAB("Stab"),
	SLASH("Slash"),
	CRUSH("Crush"),
	RANGED_LIGHT("Light ranged"),
	RANGED_STANDARD("Standard ranged"),
	RANGED_HEAVY("Heavy ranged"),
	RANGED_MIXED("Mixed ranged"),
	MAGIC("Magic"),
	UNKNOWN("Unknown");

	private final String displayName;

	AttackType(String displayName)
	{
		this.displayName = displayName;
	}

	public boolean isMelee()
	{
		return this == STAB || this == SLASH || this == CRUSH;
	}

	public boolean isRanged()
	{
		return this == RANGED_LIGHT || this == RANGED_STANDARD
			|| this == RANGED_HEAVY || this == RANGED_MIXED;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
