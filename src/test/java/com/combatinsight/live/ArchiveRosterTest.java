package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveRosterTest
{
    @Test public void everyAttackableCacheIdHasAProfileAndTheCorrectFootprint() throws Exception
    {
        int combat=0;
        try(BufferedReader r=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/archive-cache-roster.tsv"),StandardCharsets.UTF_8)))
        {
            String line;while((line=r.readLine())!=null) {
                if(line.startsWith("#"))continue;String[] f=line.split("\t");int id=Integer.parseInt(f[0]);
                TargetProfile t=TargetDatabase.find(id);
                if(f[3].equals("1")) {
                    combat++;assertNotNull("Attackable "+id+" "+f[1],t);
                    assertEquals("Footprint "+id,Integer.parseInt(f[2]),t.getSize());
                } else assertNull("Noncombat/controller "+id,t);
            }
        }
        assertEquals(28,combat);
    }
    @Test public void weakenedTrioAndGroundedGuardianHaveTheirOwnHpBars()
    {
        for(int id:new int[]{16644,16646,16648})assertEquals(7,TargetDatabase.find(id).getHitpoints());
        assertEquals(1100,TargetDatabase.find(16660).getHitpoints());
    }
    @Test public void allTrioFormsHaveOrdinaryMagicCalculationsWithoutAnImpBuff()
    {
        for(int id=16643;id<=16648;id++)
            assertTrue("Trio "+id,ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,id).isDpsAvailable());
    }
    @Test public void observedFootprintOverridesBundledSizeWithoutClearingStatUncertainty()
    {
        EncounterContext seen=EncounterContext.from(ArchiveObservedStateTest.npc(16652,100,100,-1,4,-1),EncounterMode.AUTO);
        assertEquals(4,EncounterResolver.resolve(16652,seen).getSize());
        assertTrue(EncounterResolver.resolve(16640,seen).isAmbiguous());
    }
}
