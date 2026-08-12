package com.combatinsight.calculation;

/**
 * Core combat formulas kept independent from RuneLite UI and client code.
 *
 * This is deliberately the first, readable baseline. Equipment-specific effects,
 * NPC modifiers, multi-hit weapons, and special attacks will be layered on top
 * of this model rather than embedded in the panel.
 */
public final class OsrsCombatCalculator
{
	private static final double SECONDS_PER_TICK = 0.6;
	private static final int GLOBAL_DAMAGE_CAP = 200;

	private OsrsCombatCalculator()
	{
	}

	public static CombatResult calculate(CombatState state)
	{
		if (state == null || state.getTarget() == null)
		{
			throw new IllegalArgumentException("A target is required");
		}

		int effectiveAttack;
		int attackRoll;
		int defenceRoll;
		int maxHit;

		switch (state.getCombatStyle())
		{
			case MELEE:
				effectiveAttack = effectiveLevel(
					state.getStats().getAttack(),
					state.getBoosts().getAttack(),
					state.getPrayers().getAttackMultiplier(),
					state.getAttackStyle().getAttackStyleBonus());
				attackRoll = effectiveAttack * (state.getGear().getMeleeAttack() + 64);
				defenceRoll = state.getTarget().getDefenceLevel() * (state.getTarget().getMeleeDefence() + 64);
				int effectiveStrength = effectiveLevel(
					state.getStats().getStrength(),
					state.getBoosts().getStrength(),
					state.getPrayers().getStrengthMultiplier(),
					state.getAttackStyle().getStrengthStyleBonus());
				maxHit = (int) Math.floor(0.5
					+ effectiveStrength * (state.getGear().getMeleeStrength() + 64) / 640.0);
				break;

			case RANGED:
				effectiveAttack = effectiveLevel(
					state.getStats().getRanged(),
					state.getBoosts().getRanged(),
					state.getPrayers().getRangedMultiplier(),
					state.getAttackStyle().getAttackStyleBonus());
				attackRoll = effectiveAttack * (state.getGear().getRangedAttack() + 64);
				defenceRoll = state.getTarget().getDefenceLevel() * (state.getTarget().getRangedDefence() + 64);
				maxHit = (int) Math.floor(0.5
					+ effectiveAttack * (state.getGear().getRangedStrength() + 64) / 640.0);
				break;

			case MAGIC:
			default:
				effectiveAttack = effectiveLevel(
					state.getStats().getMagic(),
					state.getBoosts().getMagic(),
					1.0,
					state.getAttackStyle().getAttackStyleBonus());
				attackRoll = effectiveAttack * (state.getGear().getMagicAttack() + 64);
				defenceRoll = state.getTarget().getMagicDefence() * (state.getTarget().getDefenceLevel() + 64);
				maxHit = (int) Math.floor(state.getMagicBaseMaxHit()
					* (1.0 + (state.getGear().getMagicDamagePercent()
						+ state.getPrayers().getMagicDamagePercent()) / 100.0));
				break;
		}

		maxHit = Math.min(GLOBAL_DAMAGE_CAP, Math.max(0, maxHit));

		double accuracy = hitChance(attackRoll, defenceRoll);
		double averageHit = maxHit * 0.5 * accuracy;
		double damagePerSecond = averageHit / (state.getGear().getAttackSpeedTicks() * SECONDS_PER_TICK);

		return new CombatResult(effectiveAttack, attackRoll, defenceRoll, maxHit, accuracy, averageHit, damagePerSecond);
	}

	private static int effectiveLevel(int level, int boost, double prayerMultiplier, int styleBonus)
	{
		return (int) Math.floor((level + boost) * prayerMultiplier) + styleBonus + 8;
	}

	private static double hitChance(int attackRoll, int defenceRoll)
	{
		if (attackRoll > defenceRoll)
		{
			return 1.0 - (double) (defenceRoll + 2) / (2.0 * (attackRoll + 1));
		}

		return (double) attackRoll / (2.0 * (defenceRoll + 1));
	}
}
