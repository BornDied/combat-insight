package com.combatinsight.calculation;

public final class TargetStats
{
	private final String name;
	private final int defenceLevel;
	private final int meleeDefence;
	private final int rangedDefence;
	private final int magicDefence;
	private final int hitpoints;

	public TargetStats(String name, int defenceLevel, int meleeDefence, int rangedDefence, int magicDefence, int hitpoints)
	{
		this.name = name;
		this.defenceLevel = defenceLevel;
		this.meleeDefence = meleeDefence;
		this.rangedDefence = rangedDefence;
		this.magicDefence = magicDefence;
		this.hitpoints = Math.max(0, hitpoints);
	}

	public String getName()
	{
		return name;
	}

	public int getDefenceLevel()
	{
		return defenceLevel;
	}

	public int getMeleeDefence()
	{
		return meleeDefence;
	}

	public int getRangedDefence()
	{
		return rangedDefence;
	}

	public int getMagicDefence()
	{
		return magicDefence;
	}

	public int getHitpoints()
	{
		return hitpoints;
	}
}
