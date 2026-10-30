package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.client.game.ItemEquipmentStats;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveEquipmentTest
{
    @Test public void incompleteNewWeaponMechanicsCannotMasqueradeAsOrdinaryAttacks()
    {
        assertEquals("", ArchiveEquipmentRules.unresolved("obligator", "", 0));
        assertFalse(ArchiveEquipmentRules.unresolved("obligator (uncharged)", "", 0).isEmpty());
        assertEquals(AttackType.CRUSH, WeaponCategoryDatabase.find(34715).attackType(0));
        assertEquals(AttackType.CRUSH, WeaponCategoryDatabase.find(34715).attackType(1));
        assertFalse(ArchiveEquipmentRules.unresolved("ascension crossbows", "", 0).isEmpty());
        assertEquals("",ArchiveEquipmentRules.unresolved("ascension crossbows", "diamond ascension bolts (e)", 0));
        assertEquals("",ArchiveEquipmentRules.unresolved("ascension crossbows", "ascension bolts", 0));
        assertEquals("",ArchiveEquipmentRules.unresolved("zorya's tome", "", 0));
        assertEquals("", ArchiveEquipmentRules.unresolved("zorya's tome", "", 3));
        assertFalse(ArchiveEquipmentRules.unresolved("zorya's tome", "", -1).isEmpty());
        assertEquals("",ArchiveEquipmentRules.unresolved("voidwaker", "", 3));
    }
    @Test public void ascensionCannotFireOrdinaryBoltsAndOtherCrossbowsCannotFireAscension()
    {
        assertEquals(AmmunitionRules.Source.NONE, AmmunitionRules.selectSource("Ascension crossbows", "Ruby dragon bolts (e)", ""));
        assertEquals(AmmunitionRules.Source.NONE, AmmunitionRules.selectSource("Zaryte crossbow", "Diamond ascension bolts (e)", ""));
        assertEquals(AmmunitionRules.Source.WORN, AmmunitionRules.selectSource("Ascension crossbows", "Ascension bolts", ""));
        assertEquals(AmmunitionRules.Source.QUIVER, AmmunitionRules.selectSource("Ascension crossbows", "Amethyst arrows", "Onyx ascension bolts (e)"));
    }
    @Test public void newEquipmentHasFallbackStatsButLiveStatsTakePriority()
    {
        assertNotNull(EquipmentStatsFallback.resolve(34681, null));
        assertEquals(3, EquipmentStatsFallback.resolve(34681, null).getAspeed());
        assertEquals(53, EquipmentStatsFallback.resolve(34684, null).getRstr());
        assertEquals(58, EquipmentStatsFallback.resolve(34696, null).getRstr());
        assertEquals(8, EquipmentStatsFallback.resolve(34719, null).getStr());
        assertEquals(45, EquipmentStatsFallback.resolve(34725, null).getAmagic());
        ItemEquipmentStats live = ItemEquipmentStats.builder().arange(99).build();
        assertSame(live, EquipmentStatsFallback.resolve(34681, live));
    }
    @Test public void tomeUsesItsOwnMagicInsteadOfSavedSpell()
    {
        assertTrue(MagicWeaponRules.hasBuiltInAttack("Zorya's tome"));
        assertEquals(AttackType.RANGED_HEAVY, WeaponCategoryDatabase.find(34681).attackType(1));
    }
    @Test public void ascensionProcRatesDifferFromOrdinaryBolts()
    {
        assertTrue(BoltEffect.calculate("Diamond ascension bolts (e)",30,99,.5,100,0,false,false,false,false,false).expected
            < BoltEffect.calculate("Diamond dragon bolts (e)",30,99,.5,100,0,false,false,false,false,false).expected);
    }
}
