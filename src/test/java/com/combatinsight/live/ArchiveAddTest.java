package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveAddTest
{
    @Test public void unknownAddRangedDefenceDoesNotEraseDefenceIndependentMaximum()
    {
        for(int id:new int[]{16653,16662})
        {
            LiveCombatSnapshot s=ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,CombatStyleOverride.RANGED,id);
            assertTrue(s.isMaxHitAvailable());
            assertFalse(s.isAccuracyAvailable());assertFalse(s.isDpsAvailable());
        }
    }
    @Test public void dousingACorpseChangesWeaknessAndMeleeDefence()
    {
        TargetProfile normal = TargetDatabase.find(16687);
        TargetProfile doused = TargetDatabase.find(16688);
        assertNotNull(normal);
        assertNotNull(doused);
        assertTrue(normal.hasAttribute("undead"));
        assertTrue(doused.hasAttribute("undead"));
        assertEquals(100, normal.defenceBonus(AttackType.CRUSH));
        assertEquals(-20, doused.defenceBonus(AttackType.CRUSH));
        assertEquals("33", cast(16687, MagicSpell.WATER_SURGE).getMaxHitText());
        assertEquals("24", cast(16687, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertEquals("48", cast(16688, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertEquals("24", cast(16688, MagicSpell.WATER_SURGE).getMaxHitText());
    }

    @Test public void supportedAddsCalculateWithoutInventingScaledHealth()
    {
        for (int id : new int[]{16666, 16670, 16687, 16688})
        {
            LiveCombatSnapshot snapshot = cast(id, MagicSpell.FIRE_SURGE);
            assertTrue("Missing add calculation " + id, snapshot.isDpsAvailable());
            assertTrue(snapshot.isAccuracyAvailable());
            assertFalse(snapshot.hasTargetHealth());
        }
        assertEquals("38", cast(16666, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertTrue(TargetDatabase.find(16666).hasAttribute("demon"));
        // The Wiki record does not establish the undead attribute for binders.
        assertFalse(TargetDatabase.find(16670).hasAttribute("undead"));
        assertEquals("Immune",cast(16670,MagicSpell.CRUMBLE_UNDEAD).getMaxHitText());
    }

    @Test public void combatOcellusAddsUseVerifiedSizesAndStyleSpecificGuaranteedMaximums()
    {
        int[] ids={16678,16679,16680};
        AttackType[] styles={AttackType.CRUSH,AttackType.RANGED_LIGHT,AttackType.MAGIC};
        for(int i=0;i<ids.length;i++) {
            for(AttackType type:styles)assertEquals(ids[i]+" "+type,type==styles[i],TargetMechanics.isGuaranteedMaximum(ids[i],type));
            assertEquals(1,TargetDatabase.find(ids[i]).getSize());
            assertTrue(cast(ids[i],MagicSpell.FIRE_SURGE).isDpsAvailable());
        }
        assertEquals("100.0%",cast(16680,MagicSpell.FIRE_SURGE).getHitChanceText());
        assertEquals("8.00",cast(16680,MagicSpell.FIRE_SURGE).getDpsText());
        assertTrue(cast(16681,MagicSpell.FIRE_SURGE).isDpsAvailable());
        for(int id:new int[]{16671,16672,16673,16675,16682,16683})assertNull(TargetDatabase.find(id));
    }
    @Test public void moiaRangedAndMagicDoNotRequireAnInventedShieldMultiplier()
    {
        assertTrue(cast(16667,MagicSpell.FIRE_SURGE).isDpsAvailable());
        assertTrue(ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,CombatStyleOverride.RANGED,16667).isDpsAvailable());
        assertEquals("Immune",ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,CombatStyleOverride.MELEE,16667).getMaxHitText());
    }

    @Test public void unknownStatueEarthWeaknessCannotShowAnOrdinaryEarthMaximum()
    {
        assertFalse(cast(16662,MagicSpell.EARTH_SURGE).isDpsAvailable());
        assertTrue(cast(16662,MagicSpell.FIRE_SURGE).isDpsAvailable());
        assertFalse(ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,CombatStyleOverride.RANGED,16662).isDpsAvailable());
    }
    @Test public void aPositiveMoiaShieldSignalCannotAssumeAnUnknownDamageFormula()
    {
        assertFalse(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,16418,CombatStyleOverride.MAGIC,16667).isDpsAvailable());
    }

    private static LiveCombatSnapshot cast(int id, MagicSpell spell)
    {
        return ArchiveMagicTest.capture(spell, 0, CombatStyleOverride.MAGIC, id);
    }
}
