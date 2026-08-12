package com.combatinsight.calculation;

import java.util.Locale;

/**
 * UI-ready values for the overlay. Keeping formatting here means the future
 * RuneLite overlay can remain focused on layout and drawing.
 */
public final class HudDisplayModel
{
	private final String targetName;
	private final int maxHit;
	private final double accuracy;
	private final double averageHit;
	private final double damagePerSecond;
	private final int attackSpeedTicks;
	private final int attackRoll;
	private final int defenceRoll;

	private HudDisplayModel(
		String targetName,
		int maxHit,
		double accuracy,
		double averageHit,
		double damagePerSecond,
		int attackSpeedTicks,
		int attackRoll,
		int defenceRoll)
	{
		this.targetName = targetName;
		this.maxHit = maxHit;
		this.accuracy = accuracy;
		this.averageHit = averageHit;
		this.damagePerSecond = damagePerSecond;
		this.attackSpeedTicks = attackSpeedTicks;
		this.attackRoll = attackRoll;
		this.defenceRoll = defenceRoll;
	}

	public static HudDisplayModel from(CombatState state, CombatResult result)
	{
		return new HudDisplayModel(
			state.getTarget().getName(),
			result.getMaxHit(),
			result.getAccuracy(),
			result.getAverageHit(),
			result.getDamagePerSecond(),
			state.getGear().getAttackSpeedTicks(),
			result.getAttackRoll(),
			result.getDefenceRoll());
	}

	public String getTargetName()
	{
		return targetName;
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

	public int getAttackSpeedTicks()
	{
		return attackSpeedTicks;
	}

	public int getAttackRoll()
	{
		return attackRoll;
	}

	public int getDefenceRoll()
	{
		return defenceRoll;
	}

	public String accuracyText()
	{
		return String.format(Locale.ROOT, "%.1f%%", accuracy * 100.0);
	}

	public String averageHitText()
	{
		return String.format(Locale.ROOT, "%.1f", averageHit);
	}

	public String dpsText()
	{
		return String.format(Locale.ROOT, "%.1f", damagePerSecond);
	}
}
