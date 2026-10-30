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
        // The six Trio actors have the same ordinary combat defences. A replenishing
        // shield bar is not evidence of a damage multiplier. Imp-enhanced attacks
        // are handled separately from ordinary target stats.
        if ((id==16653 || id==16662) && type.isRanged()) return "Add ranged defence unverified";
        return "";
    }

    static boolean ancientImmune(int id, boolean spellbookMagic, MagicSpell spell)
    {
        return (id == 16659 || id == 16660) && spellbookMagic
            && spell.getSpellbook() == MagicSpell.Spellbook.ANCIENT;
    }
}
