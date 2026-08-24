package com.combatinsight.calculation;

import java.util.Arrays;

/** Immutable summary of one complete multi-hit basic attack. */
public final class MultiHitResult
{
	private final int[] maximumHits;
	private final int totalMaximumHit;
	private final double expectedDamagePerAttack;
	private final double successfulAttackChance;
	private final double expectedAttackSpeedTicks;

	MultiHitResult(
		int[] maximumHits,
		double expectedDamagePerAttack,
		double successfulAttackChance,
		double expectedAttackSpeedTicks)
	{
		this.maximumHits = Arrays.copyOf(maximumHits, maximumHits.length);
		int total = 0;
		for (int hit : maximumHits)
		{
			total += Math.max(0, hit);
		}
		this.totalMaximumHit = total;
		this.expectedDamagePerAttack = Math.max(0.0, expectedDamagePerAttack);
		this.successfulAttackChance = Math.max(0.0, Math.min(1.0, successfulAttackChance));
		this.expectedAttackSpeedTicks = Math.max(1.0, expectedAttackSpeedTicks);
	}

	public int[] getMaximumHits()
	{
		return Arrays.copyOf(maximumHits, maximumHits.length);
	}

	public int getHitCount()
	{
		return maximumHits.length;
	}

	public int getTotalMaximumHit()
	{
		return totalMaximumHit;
	}

	public double getExpectedDamagePerAttack()
	{
		return expectedDamagePerAttack;
	}

	public double getSuccessfulAttackChance()
	{
		return successfulAttackChance;
	}

	public double getAverageDamageOnSuccessfulAttack()
	{
		return successfulAttackChance == 0.0
			? 0.0
			: expectedDamagePerAttack / successfulAttackChance;
	}

	public double getExpectedAttackSpeedTicks()
	{
		return expectedAttackSpeedTicks;
	}

	public double getDamagePerSecond()
	{
		return expectedDamagePerAttack / (expectedAttackSpeedTicks * 0.6);
	}
}
