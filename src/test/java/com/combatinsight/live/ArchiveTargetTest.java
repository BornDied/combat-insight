package com.combatinsight.live;
import com.combatinsight.calculation.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveTargetTest
{
    @Test public void guardianMagicDrainDoesNotReportAnUnverifiedReducedMagicLevel()
    {
        TargetEffectSnapshot effects=new TargetEffectSnapshot(16659,260,221,250,212,130,130,true,false,false,false);
        LiveCombatSnapshot snapshot=ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16659,effects);
        assertFalse(snapshot.isDpsAvailable());
        assertFalse(snapshot.isAccuracyAvailable());
        assertFalse(snapshot.hasTrackedTargetMagic());
        assertEquals(-1,snapshot.getCurrentTargetMagicDefenceRoll());
        assertTrue(snapshot.getMaxHitText().contains("Magic drain pending"));
    }
    @Test public void unresolvedStatesWithholdNumbersAndUnknownScalingDoesNotInventHp()
    {
        for(int id:new int[]{16640,16643,16667,16674,16676})
        {
            LiveCombatSnapshot snapshot=ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.AUTO,id);
            assertFalse("State needs resolution: "+id,snapshot.isDpsAvailable());
            assertFalse(snapshot.getMaxHitText().matches("[0-9]+"));
        }
        LiveCombatSnapshot xarth=ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16684);
        assertTrue(xarth.isDpsAvailable());
        assertFalse(xarth.hasTargetHealth());
        assertTrue(xarth.diagnosticReport().contains("Archive HP scaling unavailable"));
    }
    @Test public void guardianAncientImmunityDoesNotBlockElementalSpells()
    {
        LiveCombatSnapshot ancient=ArchiveMagicTest.capture(MagicSpell.ICE_BARRAGE,0,CombatStyleOverride.MAGIC,16659);
        assertEquals("Immune",ancient.getMaxHitText());
        assertEquals("0.0%",ancient.getHitChanceText());
        assertEquals("0.00",ancient.getDpsText());
        assertTrue(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16659).isDpsAvailable());
    }
    @Test public void raidCombatIdsArePresentButIntroAndSceneryAreNot()
    {
        for(int id:new int[]{16640,16641,16643,16644,16645,16646,16647,16648,16652,16659,16660,16665,16667,16674,16676,16677,16684})
            assertNotNull("Missing combat NPC "+id,TargetDatabase.find(id));
        for(int id:new int[]{16657,16658,16671,16672,16673,16683}) assertNull(TargetDatabase.find(id));
    }
    @Test public void currentHotfixFloorsUseSixtyPercentOfBase()
    {
        assertEquals(156,TargetEffectRules.defenceFloor(TargetDatabase.find(16659)));
        assertEquals(180,TargetEffectRules.defenceFloor(TargetDatabase.find(16640)));
        assertEquals(78,TargetEffectRules.defenceFloor(TargetDatabase.find(16652)));
        assertEquals(117,TargetEffectRules.defenceFloor(TargetDatabase.find(16667)));
        assertEquals(102,TargetEffectRules.defenceFloor(TargetDatabase.find(16684)));
        assertEquals(129,TargetEffectRules.defenceFloor(TargetDatabase.find(16674)));
        assertEquals(150,TargetEffectRules.defenceFloor(TargetDatabase.find(16677)));
        for(int id:new int[]{16641,16643,16665,16676}) assertEquals(0,TargetEffectRules.defenceFloor(TargetDatabase.find(id)));
    }
    @Test public void nexCrushDefenceReflectsLaunchHotfix()
    {
        for(int id=11278;id<=11282;id++) assertEquals(35,TargetDatabase.find(id).defenceBonus(AttackType.CRUSH));
    }
    @Test public void flyingAndGroundedGuardianHaveDifferentDefences()
    {
        TargetProfile air=TargetDatabase.find(16659), ground=TargetDatabase.find(16660);
        assertNotNull(air); assertNotNull(ground);
        assertTrue(air.hasAttribute("flying"));assertFalse(ground.hasAttribute("flying"));
        assertEquals(110,air.defenceBonus(AttackType.SLASH));assertEquals(90,ground.defenceBonus(AttackType.SLASH));
        assertEquals(80,air.getWeaknessSeverity());assertEquals(40,ground.getWeaknessSeverity());
    }
}
