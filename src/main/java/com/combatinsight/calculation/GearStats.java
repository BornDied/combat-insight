package com.combatinsight.calculation;

public final class GearStats
{
	private final int meleeAttack;
	private final int meleeStrength;
	private final int rangedAttack;
	private final int rangedStrength;
	private final int magicAttack;
	private final double magicDamagePercent;
	private final int attackSpeedTicks;

	public GearStats(
		int meleeAttack,
		int meleeStrength,
		int rangedAttack,
		int rangedStrength,
		int magicAttack,
		double magicDamagePercent,
		int attackSpeedTicks)
	{
		if (attackSpeedTicks <= 0)
		{
			throw new IllegalArgumentException("Attack speed must be positive");
		}

		this.meleeAttack = meleeAttack;
		this.meleeStrength = meleeStrength;
		this.rangedAttack = rangedAttack;
		this.rangedStrength = rangedStrength;
		this.magicAttack = magicAttack;
		this.magicDamagePercent = magicDamagePercent;
		this.attackSpeedTicks = attackSpeedTicks;
	}

	public int getMeleeAttack()
	{
		return meleeAttack;
	}

	public int getMeleeStrength()
	{
		return meleeStrength;
	}

	public int getRangedAttack()
	{
		return rangedAttack;
	}

	public int getRangedStrength()
	{
		return rangedStrength;
	}

	public int getMagicAttack()
	{
		return magicAttack;
	}

	public double getMagicDamagePercent()
	{
		return magicDamagePercent;
	}

	public int getAttackSpeedTicks()
	{
		return attackSpeedTicks;
	}
}
