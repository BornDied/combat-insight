package com.combatinsight.live;

import com.combatinsight.calculation.BlowpipeDart;
import com.combatinsight.calculation.CombatStyle;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.MagicSpell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Explain relevant selections without treating a stored selection as live evidence. */
public final class CalculationInputNotes
{
    private CalculationInputNotes() { }

    public static List<String> forSelection(CombatStyleOverride override, MagicSpell spell,
        CombatStyle style, boolean spellbookMagic, boolean blowpipe, BlowpipeDart dart)
    {
        List<String> notes = new ArrayList<>();
        if (override != CombatStyleOverride.AUTO)
            notes.add("Combat type forced: " + override + "; choose Automatic for weapon switches");
        if (spell.isSet())
        {
            if (style == CombatStyle.MAGIC && spellbookMagic)
                notes.add("Manual spell in use: " + spell + "; match it to the spell you cast");
            else
                notes.add("Saved spell ignored: " + spell + "; current weapon/style uses its own calculation");
        }
        if (blowpipe && style == CombatStyle.RANGED)
            notes.add("Manual blowpipe dart: " + dart);
        return Collections.unmodifiableList(notes);
    }
}
