package com.combatinsight.live;

import net.runelite.client.game.ItemEquipmentStats;

/** Verified new-item bonuses when RuneLite has not loaded its item-stat entry yet.
 * Current RuneLite data always wins; these bonuses are never added on top of it.
 * Sources and verification date: docs/research/seeking-arrows.md.
 */
final class EquipmentStatsFallback
{
    private EquipmentStatsFallback() { }
    static ItemEquipmentStats resolve(int itemId, ItemEquipmentStats current)
    {
        if (current != null) return current;
        if (itemId == 33639) // Necklace of rupture
            return ItemEquipmentStats.builder().slot(2).arange(20).rstr(8).prayer(3).build();
        int strength;
        switch (itemId)
        {
            case 33553: strength=7; break;
            case 33559: strength=10; break;
            case 33565: strength=16; break;
            case 33571: strength=22; break;
            case 33577: strength=31; break;
            case 33583: strength=49; break;
            case 33589: strength=55; break;
            case 33595: strength=60; break;
            case 33601: strength=28; break;
            default: return null;
        }
        return ItemEquipmentStats.builder().slot(13).arange(20).rstr(strength).build();
    }
}
