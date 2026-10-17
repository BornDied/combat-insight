package com.combatinsight.calculation;

/**
 * Behead following the 22 July 2026 gear update.
 * The input maximum MUST exclude the basic-attack Soul Stack Strength bonus.
 * Both damage endpoints derive independently from that unstacked maximum.
 * Formula order matches Weird Gloop DPS calculator revision 89c3e25 (September 2026).
 */
public final class SoulreaperSpecial
{
    private SoulreaperSpecial() { }

    public static SpecialAttackResult calculate(int baseMaximumHit, boolean baseMaximumAvailable,
        int baseAttackRoll, int targetDefenceRoll, boolean targetAvailable, int flatArmour,
        boolean targetImmune, boolean guaranteedAccuracy, boolean guaranteedMaximum, int stacks)
    {
        return calculate(baseMaximumHit, baseMaximumAvailable, baseAttackRoll, targetDefenceRoll,
            targetAvailable, flatArmour, targetImmune, guaranteedAccuracy, guaranteedMaximum, stacks,
            HitTransform.IDENTITY);
    }

    public static SpecialAttackResult calculate(int baseMaximumHit, boolean baseMaximumAvailable,
        int baseAttackRoll, int targetDefenceRoll, boolean targetAvailable, int flatArmour,
        boolean targetImmune, boolean guaranteedAccuracy, boolean guaranteedMaximum, int stacks,
        HitTransform transform)
    {
        SpecialAttackWeapon weapon = SpecialAttackWeapon.SOULREAPER_AXE;
        if (stacks < 0 || stacks > 5) return SpecialAttackResult.unavailable(weapon, "Soul Stacks unavailable");
        if (stacks == 0) return SpecialAttackResult.unavailable(weapon, "Requires Soul Stacks");
        if (!baseMaximumAvailable) return SpecialAttackResult.unavailable(weapon, "Max hit unavailable");
        if (targetAvailable && targetImmune) return SpecialAttackResult.immune(weapon);
        int base = Math.max(0, baseMaximumHit);
        int minimum = (int) ((long) base * 6 * stacks / 100);
        int maximum = (int) ((long) base * (100 + 6 * stacks) / 100);
        int attackRoll = (int) ((long) Math.max(0, baseAttackRoll) * (100 + 12 * stacks) / 100);
        boolean guaranteed = targetAvailable && guaranteedAccuracy;
        double chance = targetAvailable ? guaranteed ? 1 : CombatPrediction.hitChance(attackRoll, targetDefenceRoll) : 0;
        int armour = targetAvailable ? flatArmour : 0;
        int maximumHit = transform.maximum(maximum, armour);
        double average = guaranteedMaximum
            ? transform.meanForHit(DamageRoll.maximumSuccessfulHit(maximum, armour))
            : transform.average(minimum, maximum, armour);
        String targetStatus = targetAvailable ? "" : "Select target";
        return new SpecialAttackResult(weapon, true, targetAvailable, targetAvailable, false, guaranteed,
            new int[]{maximumHit}, chance, chance * average, "", targetStatus, targetStatus);
    }
}
