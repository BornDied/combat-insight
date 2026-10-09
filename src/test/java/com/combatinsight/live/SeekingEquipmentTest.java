package com.combatinsight.live;
import net.runelite.client.game.ItemEquipmentStats;
import org.junit.Test;
import static org.junit.Assert.*;

public class SeekingEquipmentTest
{
    @Test public void floorRequiresTheActiveCompatibleAmmo()
    {
        assertEquals(3, AmmunitionRules.accurateDamageFloor("Twisted bow","Seeking amethyst arrow"));
        assertEquals(1, AmmunitionRules.accurateDamageFloor("Twisted bow","Amethyst arrow"));
        assertEquals(1, AmmunitionRules.accurateDamageFloor("Bow of faerdhinen","Seeking amethyst arrow"));
        assertEquals(1, AmmunitionRules.accurateDamageFloor("Dragon crossbow","Seeking amethyst arrow"));
        assertEquals(AmmunitionRules.Source.QUIVER, AmmunitionRules.selectSource("Twisted bow","Ruby bolts (e)","Seeking amethyst arrow"));
        assertEquals(AmmunitionRules.Source.WORN, AmmunitionRules.selectSource("Twisted bow","Amethyst arrow","Seeking amethyst arrow"));
    }
    @Test public void missingNewItemDataHasVerifiedBonuses()
    {
        ItemEquipmentStats necklace = EquipmentStatsFallback.resolve(33639,null);
        assertEquals(20,necklace.getArange());
        assertEquals(8,necklace.getRstr());
        assertEquals(3,necklace.getPrayer());
        ItemEquipmentStats arrow = EquipmentStatsFallback.resolve(33589,null);
        assertEquals(20,arrow.getArange());
        assertEquals(55,arrow.getRstr());
        assertNull(EquipmentStatsFallback.resolve(-1,null));
    }
    @Test public void currentRuneLiteStatsWinWithoutDoubleCounting()
    {
        ItemEquipmentStats current = ItemEquipmentStats.builder().arange(21).rstr(9).build();
        assertSame(current, EquipmentStatsFallback.resolve(33639,current));
        assertSame(current, EquipmentStatsFallback.resolve(33589,current));
    }
}
