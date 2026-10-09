package com.combatinsight.calculation;

/** Single-target specials requiring size, encounter or selected-style evidence. */
public final class ContextSpecialAttack
{
    private ContextSpecialAttack() { }
    public static boolean supports(SpecialAttackWeapon weapon)
    {
        return weapon == SpecialAttackWeapon.CRYSTAL_HALBERD || weapon == SpecialAttackWeapon.DRAGON_HALBERD
            || weapon == SpecialAttackWeapon.OSMUMTENS_FANG || weapon == SpecialAttackWeapon.SCORCHING_BOW;
    }

    public static SpecialAttackResult calculate(SpecialAttackWeapon weapon, int trueMax, int attackRoll,
        int defenceRoll, int targetSize, int armour, boolean stab, boolean toa, boolean demon, boolean guaranteed)
    {
        return calculate(weapon,trueMax,attackRoll,defenceRoll,targetSize,armour,stab,toa,demon,guaranteed,false);
    }

    public static SpecialAttackResult calculate(SpecialAttackWeapon weapon, int trueMax, int attackRoll,
        int defenceRoll, int targetSize, int armour, boolean stab, boolean toa, boolean demon, boolean guaranteed,
        boolean seekingArrows)
    {
        if (!supports(weapon)) return SpecialAttackResult.noWeapon();
        if (weapon == SpecialAttackWeapon.SCORCHING_BOW && !demon)
            return SpecialAttackResult.unavailable(weapon, "Demonic target required");
        int maximum = Math.max(0, trueMax), minimum = 0;
        boolean fang = weapon == SpecialAttackWeapon.OSMUMTENS_FANG;
        boolean halberd = weapon == SpecialAttackWeapon.CRYSTAL_HALBERD || weapon == SpecialAttackWeapon.DRAGON_HALBERD;
        if (fang) { attackRoll = (int) ((long) attackRoll * 3 / 2); minimum = maximum * 3 / 20; }
        if (halberd) maximum = maximum * 11 / 10;
        double first = guaranteed ? 1 : CombatPrediction.hitChance(attackRoll, defenceRoll);
        if (fang && stab && !guaranteed)
            first = toa ? 1-(1-first)*(1-first) : CombatPrediction.fangHitChance(attackRoll, defenceRoll);
        double second = halberd && targetSize > 1
            ? guaranteed ? 1 : CombatPrediction.hitChance((int) ((long) attackRoll*3/4), defenceRoll) : 0;
        HitTransform transform = HitTransform.IDENTITY.withAccurateFloor(
            seekingArrows && weapon == SpecialAttackWeapon.SCORCHING_BOW ? 3 : 1);
        int hit = transform.maximum(maximum, armour);
        int[] hits = halberd && targetSize > 1 ? new int[]{hit, hit} : new int[]{hit};
        double mean = guaranteed ? hit : transform.average(minimum, maximum, armour);
        return new SpecialAttackResult(weapon, true, true, true, false, guaranteed, hits,
            1-(1-first)*(1-second), mean*(first+second), "", "", "");
    }
}
