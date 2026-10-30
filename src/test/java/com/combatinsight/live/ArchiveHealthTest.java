package com.combatinsight.live;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveHealthTest
{
    @Test public void publishedPartyHpReachesTheSnapshotButUnknownPartyDoesNot()
    {
        EncounterContext c=EncounterContext.UNKNOWN.withArchiveState(new ArchiveEncounterState(1,3,false));
        assertFalse("A partial observed roster cannot establish starting HP",capture(16652,c).hasTargetHealth());
        c=c.withArchivePartySize(3);
        assertTrue(capture(16652,c).hasTargetHealth());
        assertEquals("4,350 / 4,350",capture(16652,c).getTargetHealthText());
        assertFalse(capture(16652,EncounterContext.UNKNOWN).hasTargetHealth());
        assertFalse(capture(16652,EncounterContext.UNKNOWN.withArchiveState(new ArchiveEncounterState(1,8,false))).hasTargetHealth());
        assertFalse(capture(16643,c).hasTargetHealth());
        assertFalse(capture(16652,c.withArchivePartySize(0)).hasTargetHealth());
        assertFalse(capture(16652,c.withArchivePartySize(9)).hasTargetHealth());
    }
    private static LiveCombatSnapshot capture(int id,EncounterContext c)
    {
        return ArchiveMagicTest.capture(com.combatinsight.calculation.MagicSpell.FIRE_SURGE,0,
            com.combatinsight.calculation.CombatStyleOverride.MAGIC,id,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,c,0);
    }
    @Test public void publishedPartyRowsAreUsedWithoutExtrapolatingBlankCells()
    {
        int[][] rows={
            {16640,1500,1500,2180,2810,3440,4070,4700,5330},
            {16641,600,600,870,1120,1370,1630,1880,2130},
            {16652,3000,3000,4350,5610,6870,8130,9390,0},
            {16659,550,550,800,0,1260,0,0,1950},
            {16660,1100,1100,1600,2060,2520,2980,3440,3910}
        };
        for(int[] row:rows)for(int size=1;size<=8;size++)
            assertEquals(row[0]+" party="+size,row[size],ArchiveHealth.maximum(row[0],size));
        for(int size:new int[]{-1,0,9,100})assertEquals(0,ArchiveHealth.maximum(16652,size));
        for(int id:new int[]{16643,16644,16650,16653,16662,16667,16674,16676,16684,16688})
            assertEquals("Unverified HP table "+id,0,ArchiveHealth.maximum(id,5));
    }
}
