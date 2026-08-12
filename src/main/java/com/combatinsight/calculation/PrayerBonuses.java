package com.combatinsight.calculation;

public final class PrayerBonuses
{
	public static final PrayerBonuses NONE = new PrayerBonuses(1.0, 1.0, 1.0, 0.0);

	private final double attackMultiplier;
	private final double strengthMultiplier;
	private final double rangedMultiplier;
	private final double magicDamagePercent;

	public PrayerBonuses(double attackMultiplier, double strengthMultiplier, double rangedMultiplier)
	{
		this(attackMultiplier, strengthMultiplier, rangedMultiplier, 0.0);
	}

	public PrayerBonuses(
		double attackMultiplier,
		double strengthMultiplier,
		double rangedMultiplier,
		double magicDamagePercent)
	{
		this.attackMultiplier = attackMultiplier;
		this.strengthMultiplier = strengthMultiplier;
		this.rangedMultiplier = rangedMultiplier;
		this.magicDamagePercent = magicDamagePercent;
	}

	public double getAttackMultiplier()
	{
		return attackMultiplier;
	}

	public double getStrengthMultiplier()
	{
		return strengthMultiplier;
	}

	public double getRangedMultiplier()
	{
		return rangedMultiplier;
	}

	public double getMagicDamagePercent()
	{
		return magicDamagePercent;
	}
}
