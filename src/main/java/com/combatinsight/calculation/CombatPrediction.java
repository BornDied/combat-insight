package com.combatinsight.calculation;

/**
 * Target-aware accuracy and expected basic-attack damage. Conditional special
 * attacks and multi-hit distributions are intentionally handled by the live
 * adapter before this class is called.
 */
public final class CombatPrediction
{
	private static final double SECONDS_PER_TICK = 0.6;

	private final int attackRoll;
	private final int defenceRoll;
	private final double hitChance;
	private final double averageSuccessfulHit;
	private final double damagePerSecond;

	private CombatPrediction(
		int attackRoll,
		int defenceRoll,
		double hitChance,
		double averageSuccessfulHit,
		double damagePerSecond)
	{
		this.attackRoll = attackRoll;
		this.defenceRoll = defenceRoll;
		this.hitChance = hitChance;
		this.averageSuccessfulHit = averageSuccessfulHit;
		this.damagePerSecond = damagePerSecond;
	}

	public static CombatPrediction calculate(
		int attackRoll,
		int defenceRoll,
		int minimumHit,
		int maximumHit,
		int attackSpeedTicks,
		int flatArmour,
		boolean applyFlatArmour,
		boolean fangAccuracy)
	{
		double chance = fangAccuracy
			? fangHitChance(attackRoll, defenceRoll)
			: hitChance(attackRoll, defenceRoll);
		return calculateWithHitChance(
			attackRoll,
			defenceRoll,
			chance,
			minimumHit,
			maximumHit,
			attackSpeedTicks,
			flatArmour,
			applyFlatArmour);
	}

	public static CombatPrediction calculateWithHitChance(
		int attackRoll,
		int defenceRoll,
		double hitChance,
		int minimumHit,
		int maximumHit,
		int attackSpeedTicks,
		int flatArmour,
		boolean applyFlatArmour)
	{
		double chance = Math.max(0.0, Math.min(1.0, hitChance));
		double average = maximumHit <= 0
			? 0.0
			: DamageRoll.averageSuccessfulHit(
				minimumHit,
				maximumHit,
				applyFlatArmour ? flatArmour : 0);
		double dps = average * chance / (Math.max(1, attackSpeedTicks) * SECONDS_PER_TICK);
		return new CombatPrediction(attackRoll, defenceRoll, chance, average, dps);
	}

	public static int effectiveAccuracyLevel(
		int boostedLevel,
		double prayerMultiplier,
		int stanceBonus,
		double voidMultiplier,
		int baseOffset)
	{
		int level = (int) Math.floor(boostedLevel * prayerMultiplier) + stanceBonus + baseOffset;
		return voidMultiplier > 1.0 ? (int) Math.floor(level * voidMultiplier) : level;
	}

	public static int defenceRoll(TargetProfile target, AttackType attackType, boolean magicUsesDefenceLevel)
	{
		int level = attackType == AttackType.MAGIC && !magicUsesDefenceLevel
			? target.getMagicLevel()
			: target.getDefenceLevel();
		return (level + 9) * (target.defenceBonus(attackType) + 64);
	}

	/** Complete OSRS accuracy formula, including negative-roll behaviour. */
	public static double hitChance(int attackRoll, int defenceRoll)
	{
		int attack = attackRoll < 0 ? Math.min(0, attackRoll + 2) : attackRoll;
		int defence = defenceRoll < 0 ? Math.min(0, defenceRoll + 2) : defenceRoll;

		if (attack >= 0 && defence >= 0)
		{
			return standardHitChance(attack, defence);
		}
		if (attack >= 0)
		{
			return 1.0 - 1.0 / (-defence + 1.0) / (attack + 1.0);
		}
		if (defence >= 0)
		{
			return 0.0;
		}
		return standardHitChance(-defence, -attack);
	}

	/** Osmumten's fang double-roll formula outside Tombs of Amascut. */
	public static double fangHitChance(int attackRoll, int defenceRoll)
	{
		int attack = attackRoll < 0 ? Math.min(0, attackRoll + 2) : attackRoll;
		int defence = defenceRoll < 0 ? Math.min(0, defenceRoll + 2) : defenceRoll;

		if (attack >= 0 && defence >= 0)
		{
			return fangStandardRoll(attack, defence);
		}
		if (attack >= 0)
		{
			return 1.0 - 1.0 / (-defence + 1.0) / (attack + 1.0);
		}
		if (defence >= 0)
		{
			return 0.0;
		}

		int reversedAttack = -defence;
		int reversedDefence = -attack;
		if (reversedAttack < reversedDefence)
		{
			return reversedAttack * (reversedDefence * 6.0 - 2.0 * reversedAttack + 5.0)
				/ 6.0 / (reversedDefence + 1.0) / (reversedDefence + 1.0);
		}
		return 1.0 - (reversedDefence + 2.0) * (2.0 * reversedDefence + 3.0)
			/ 6.0 / (reversedDefence + 1.0) / (reversedAttack + 1.0);
	}

	private static double fangStandardRoll(int attack, int defence)
	{
		if (attack > defence)
		{
			return 1.0 - (defence + 2.0) * (2.0 * defence + 3.0)
				/ (attack + 1.0) / (attack + 1.0) / 6.0;
		}
		return attack * (4.0 * attack + 5.0)
			/ 6.0 / (attack + 1.0) / (defence + 1.0);
	}

	private static double standardHitChance(int attack, int defence)
	{
		if (attack > defence)
		{
			return 1.0 - (defence + 2.0) / (2.0 * (attack + 1.0));
		}
		return attack / (2.0 * (defence + 1.0));
	}

	public int getAttackRoll()
	{
		return attackRoll;
	}

	public int getDefenceRoll()
	{
		return defenceRoll;
	}

	public double getHitChance()
	{
		return hitChance;
	}

	public double getAverageSuccessfulHit()
	{
		return averageSuccessfulHit;
	}

	public double getDamagePerSecond()
	{
		return damagePerSecond;
	}
}
