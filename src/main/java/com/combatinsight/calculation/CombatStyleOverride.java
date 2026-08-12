package com.combatinsight.calculation;

/**
 * Manual fallback for unusual weapons or manual spell casting that does not
 * change the weapon's selected combat interface.
 */
public enum CombatStyleOverride
{
	AUTO("Automatic"),
	MELEE("Force Melee"),
	RANGED("Force Ranged"),
	MAGIC("Force Magic");

	private final String displayName;

	CombatStyleOverride(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
