package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.MagicSpell;

/** Launch coverage: never infer shield, protection, or scaled HP from a name. */
final class ArchiveEncounterRules
{
    private ArchiveEncounterRules() { }

    static boolean isArchive(int id)
    {
        return id >= 16640 && id <= 16688;
    }

    static String unresolved(int id, AttackType type)
    {
        if (id == 16640) return "Bork stun state pending";
        if (id >= 16643 && id <= 16648) return "Trio shield/buff pending";
        if (id == 16667) return "Moia phase pending";
        if (id == 16674 && type.isMelee()) return "Ocellus protection pending";
        if (id == 16675 || id == 16676) return "Blade phase data pending";
        if (id >= 16678 && id <= 16681) return "Ocellus add mechanics pending";
        return "";
    }

    static boolean ancientImmune(int id, boolean spellbookMagic, MagicSpell spell)
    {
        return (id == 16659 || id == 16660) && spellbookMagic
            && spell.getSpellbook() == MagicSpell.Spellbook.ANCIENT;
    }
}
