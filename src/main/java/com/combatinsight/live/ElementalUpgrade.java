package com.combatinsight.live;

import com.combatinsight.calculation.MagicSpell;
import net.runelite.api.Client;

/** Permanent Fractured Archive elemental-fragment unlocks, applied before damage bonuses. */
final class ElementalUpgrade
{
    private ElementalUpgrade() { }

    static int bonus(Client client, MagicSpell spell)
    {
        if (spell.getSpellbook() != MagicSpell.Spellbook.STANDARD) return 0;
        int varbit;
        switch (spell.getElementName())
        {
            case "earth": varbit = 16434; break;
            case "air": varbit = 16435; break;
            case "fire": varbit = 16436; break;
            case "water": varbit = 16437; break;
            default: return 0;
        }
        return client.getVarbitValue(varbit) == 1 ? 2 : 0;
    }
}
