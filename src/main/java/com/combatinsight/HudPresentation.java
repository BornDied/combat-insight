package com.combatinsight;

import com.combatinsight.calculation.SpecialAttackWeapon;
import java.awt.Color;

/** Display-only policy; special accents never express a combat recommendation. */
final class HudPresentation
{
    private HudPresentation() { }

    static Color specialColor(SpecialAttackWeapon weapon, boolean available,
        boolean enabled, Color accent, Color fallback)
    {
        if (!available || !enabled || weapon == null) return fallback;
        switch (weapon.name())
        {
            case "ZARYTE_CROSSBOW":
            case "DRAGON_CLAWS":
            case "VOIDWAKER":
            case "DRAGON_WARHAMMER":
            case "ELDER_MAUL":
            case "ARMADYL_GODSWORD":
            case "BANDOS_GODSWORD":
            case "DRAGON_DAGGER":
            case "BURNING_CLAWS":
            case "SOULREAPER_AXE":
            case "ZORYAS_TOME":
                return accent;
            default: return fallback;
        }
    }

    static String calculation(String text, boolean available)
    { return "Immune".equals(text) ? text : available ? value(text) : "?"; }

    static String value(String text)
    {
        return text == null || text.isEmpty() || text.startsWith("Select ") ? "—" : text;
    }
}
