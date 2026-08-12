package com.combatinsight.calculation;

public final class CombatStats
{
	private final int attack;
	private final int strength;
	private final int ranged;
	private final int magic;

	public CombatStats(int attack, int strength, int ranged, int magic)
	{
		this.attack = attack;
		this.strength = strength;
		this.ranged = ranged;
		this.magic = magic;
	}

	public int getAttack()
	{
		return attack;
	}

	public int getStrength()
	{
		return strength;
	}

	public int getRanged()
	{
		return ranged;
	}

	public int getMagic()
	{
		return magic;
	}
}
