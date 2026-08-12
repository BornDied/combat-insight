package com.combatinsight.calculation;

public final class CombatResult
{
	private final int effectiveAttack;
	private final int attackRoll;
	private final int defenceRoll;
	private final int maxHit;
	private final double accuracy;
	private final double averageHit;
	private final double damagePerSecond;

	public CombatResult(
		int effectiveAttack,
		int attackRoll,
		int defenceRoll,
		int maxHit,
		double accuracy,
		double averageHit,
		double damagePerSecond)
	{
		this.effectiveAttack = effectiveAttack;
		this.attackRoll = attackRoll;
		this.defenceRoll = defenceRoll;
		this.maxHit = maxHit;
		this.accuracy = accuracy;
		this.averageHit = averageHit;
		this.damagePerSecond = damagePerSecond;
	}

	public int getEffectiveAttack()
	{
		return effectiveAttack;
	}

	public int getAttackRoll()
	{
		return attackRoll;
	}

	public int getDefenceRoll()
	{
		return defenceRoll;
	}

	public int getMaxHit()
	{
		return maxHit;
	}

	public double getAccuracy()
	{
		return accuracy;
	}

	public double getAverageHit()
	{
		return averageHit;
	}

	public double getDamagePerSecond()
	{
		return damagePerSecond;
	}
}
