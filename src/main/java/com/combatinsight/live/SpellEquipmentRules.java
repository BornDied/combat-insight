package com.combatinsight.live;

import com.combatinsight.calculation.MagicSpell;
import java.util.Locale;

/** Charged item IDs distinguish a functional tome from its empty equipment bonuses. */
final class SpellEquipmentRules
{
    private SpellEquipmentRules() { }

    private static boolean matches(MagicSpell spell,int shieldId,boolean spellbook)
    {
        if(!spellbook || spell==null || spell.getSpellbook()!=MagicSpell.Spellbook.STANDARD) return false;
        String element=shieldId==20714 ? "fire" : shieldId==25574 ? "water" : shieldId==30064 ? "earth" : "";
        return !element.isEmpty() && element.equals(spell.getElementName());
    }

    static int damage(int maximum,MagicSpell spell,int shieldId,boolean spellbook)
    { return matches(spell,shieldId,spellbook) ? (int)(maximum*11L/10) : maximum; }

    static int accuracy(int roll,MagicSpell spell,int shieldId,boolean spellbook)
    {
        // Current Wiki PvM effects state 10%; the calculator's 20% line conflicts with that page.
        return shieldId==25574 && matches(spell,shieldId,spellbook) ? (int)(roll*11L/10) : roll;
    }

    static int chargeBonus(MagicSpell spell,String cape,boolean active,boolean spellbook)
    {
        if(!active || !spellbook || spell==null || cape==null) return 0;
        String god=spell==MagicSpell.SARADOMIN_STRIKE?"saradomin":spell==MagicSpell.CLAWS_OF_GUTHIX?"guthix"
            :spell==MagicSpell.FLAMES_OF_ZAMORAK?"zamorak":"";
        String name=cape.toLowerCase(Locale.ROOT);
        return !god.isEmpty() && (name.equals(god+" cape") || name.equals("imbued "+god+" cape")
            || name.equals(god+" max cape") || name.equals("imbued "+god+" max cape")) ? 10 : 0;
    }
}
