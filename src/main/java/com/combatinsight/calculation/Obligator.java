package com.combatinsight.calculation;

/** One accuracy check and one hitsplat; Thrust keeps the highest damage roll. */
public final class Obligator
{
    private Obligator() { }

    public static int maximum(int baseMaximum, int size, boolean thrust)
    {
        int multiplier = !thrust || size <= 1 ? 10 : size == 2 ? 12 : 14;
        return (int) ((long) Math.max(0, baseMaximum) * multiplier / 10);
    }

    /** maximum already includes the size bonus and other damage modifiers. */
    public static MultiHitResult calculate(int maximum, int armour, double chance,
        int stabDefence, int slashDefence, int crushDefence, boolean thrust,
        boolean guaranteedMaximum, HitTransform transform)
    {
        int upper = Math.max(0, maximum);
        int rolls = thrust ? 2 + (stabDefence > crushDefence ? 1 : 0)
            + (slashDefence > crushDefence ? 1 : 0) : 1;
        double mean = 0;
        for (int roll = guaranteedMaximum ? upper : 0; roll <= upper; roll++)
        {
            // CDF difference for the maximum of independent uniform damage rolls.
            double probability = guaranteedMaximum ? 1.0
                : Math.pow((roll + 1.0) / (upper + 1.0), rolls)
                - Math.pow(roll / (upper + 1.0), rolls);
            mean += probability * transform.meanForHit(DamageRoll.transformSuccessfulHit(roll, armour));
        }
        double accuracy = Math.max(0, Math.min(1, chance));
        return new MultiHitResult(new int[] { transform.maximum(upper, armour) },
            mean * accuracy, accuracy, thrust ? 5 : 3);
    }
}
