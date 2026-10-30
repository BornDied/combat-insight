package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.HitTransform;

/** Explicitly withhold damage for encounter transforms not yet modelled. */
final class RaidDamageRules
{
	private RaidDamageRules() { }

    /** These distributions are not ordinary accuracy rolls followed by uniform damage. */
    static String unresolvedEncounter(int id)
    {
        if (id == 11755 || id == 11758) return "Attack the exposed Warden core";
        if (id >= 8381 && id <= 8383 || id >= 10841 && id <= 10843 || id >= 10858 && id <= 10860)
            return "Verzik add stats pending";
        return "";
    }

    static boolean bypassesIceDemon(com.combatinsight.calculation.CombatStyle style, String weapon, boolean fire, boolean demonbaneSpell)
    {
        if (style==com.combatinsight.calculation.CombatStyle.MAGIC) return fire || demonbaneSpell;
        if (style==com.combatinsight.calculation.CombatStyle.RANGED) return weapon.contains("scorching bow");
        return weapon.contains("silverlight") || weapon.contains("darklight") || weapon.contains("arclight")
            || weapon.contains("emberlight") || weapon.contains("bone claws") || weapon.contains("burning claws");
    }

    static HitTransform transform(int id, AttackType type, boolean fireOrDemonbane, String weapon, int mining)
    {
        if (TargetMechanics.isZulrah(id)) return HitTransform.zulrah();
        if (EncounterResolver.isNightmarePillar(id) && type == AttackType.MAGIC)
            return HitTransform.multiplier(2,1);
        if ((id >= 8369 && id <= 8371 || id >= 10830 && id <= 10832 || id >= 10847 && id <= 10849)
            && !weapon.contains("dawnbringer")) return HitTransform.randomCap(type.isMelee() ? 10 : 3);
        if (TargetMechanics.isGuardian(id))
        {
            int bonus = pickaxeBonus(weapon);
            return type.isMelee() && bonus > 0 ? HitTransform.multiplier(50+Math.max(1,mining)+bonus,150)
                : HitTransform.multiplier(0,1);
        }
        if (id >= 7540 && id <= 7545 && type == AttackType.MAGIC) return HitTransform.division(5,1);
        if (id == 7568 && type == AttackType.MAGIC) return HitTransform.division(3,0);
        if ((id == 7551 || id == 7552 || id == 7554 || id == 7555) && type == AttackType.MAGIC) return HitTransform.division(3,0);
        if ((id == 7550 || id == 7552 || id == 7553 || id == 7555) && type.isRanged()) return HitTransform.division(3,0);
        if ((id == 7584 || id == 7585) && !fireOrDemonbane) return HitTransform.division(3,0);
        return HitTransform.IDENTITY;
    }

    private static int pickaxeBonus(String weapon)
    {
        if (!weapon.contains("pickaxe")) return 0;
        if (weapon.contains("crystal") || weapon.contains("dragon") || weapon.contains("infernal") || weapon.contains("3rd age")) return 61;
        if (weapon.contains("rune") || weapon.contains("gilded")) return 41;
        if (weapon.contains("adamant")) return 31;
        if (weapon.contains("mithril")) return 21;
        if (weapon.contains("black")) return 11;
        if (weapon.contains("steel")) return 6;
        return 1;
    }

	static String unavailableReason(int id, AttackType type, boolean fireSpell)
	{
		if (id >= 8369 && id <= 8371 || id >= 10830 && id <= 10832
			|| id >= 10847 && id <= 10849) return "Verzik P1 damage pending";
		if (TargetMechanics.isGuardian(id)) return "Guardian damage pending";
		if (id >= 7540 && id <= 7545 && type == AttackType.MAGIC)
			return "Tekton magic reduction pending";
		if (id == 7568 && type == AttackType.MAGIC) return "Crystal damage reduction pending";
		if ((id == 7551 || id == 7552 || id == 7554 || id == 7555) && type == AttackType.MAGIC)
			return "Olm damage reduction pending";
		if ((id == 7550 || id == 7552 || id == 7553 || id == 7555) && type.isRanged())
			return "Olm damage reduction pending";
		if ((id == 7584 || id == 7585) && !fireSpell) return "Ice demon damage pending";
		return "";
	}
}
