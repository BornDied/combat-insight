package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class TombsHealthScalingTest
{
    @Test public void invocationPathAndPartyScaleInWikiOrder()
    {
        assertEquals(4030,hp(11730,580,300,3,2));
        assertEquals(1900,hp(11730,580,150,2,1));
        assertEquals(86,hp(11721,80,0,1,1));
        assertEquals(11120,hp(11770,4500,300,2,-1));
    }
    @Test public void roundingChangesAtOneHundredAndThreeHundred()
    {
        assertEquals(100,hp(11730,100,0,1,0));
        assertEquals(100,hp(11730,101,0,1,0));
        assertEquals(105,hp(11730,103,0,1,0));
        assertEquals(300,hp(11730,300,0,1,0));
        assertEquals(300,hp(11730,301,0,1,0));
        assertEquals(310,hp(11730,305,0,1,0));
    }
    @Test public void unknownInputsNeverSupplyRubyHitpoints()
    {
        assertEquals(0,hp(11730,580,300,0,0));
        assertEquals(0,hp(11730,580,300,1,-1));
        assertEquals(0,hp(11730,580,300,9,0));
        assertEquals(0,hp(11730,580,300,1,7));
        // Path is irrelevant to Wardens, so only party size is needed there.
        assertEquals(310,hp(11753,140,300,1,-1));
    }
    @Test public void nonTombsTargetsAreNotScaled()
    { assertEquals(580,hp(11278,580,300,8,6)); }

    @Test public void actualSnapshotPublishesScaledHealthOnlyWhenInputsAreKnown()
    {
        LiveCombatSnapshot known=ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,
            11730,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT.withTombsInputs(1,0),EncounterContext.UNKNOWN,300);
        assertEquals("1,280 / 1,280",known.getTargetHealthText());
        assertFalse(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,11730).hasTargetHealth());
    }
    @Test public void observedOtherRaidInputsPreserveTombsInputs()
    {
        RaidScaling r=RaidScaling.DEFAULT.withTombsInputs(3,2).withObservedChambers(2,false,126).withObservedTheatre(4);
        assertEquals(4030,r.applyTombs(TargetDatabase.find(11730),300).getHitpoints());
    }
    private static int hp(int id,int hp,int invo,int party,int path)
    {
        TargetProfile target=new TargetProfile(id,100,100,hp,0,1,0,0,0,0,0,0,0,0,"",0,false,Collections.emptySet(),false);
        return RaidScaling.DEFAULT.withTombsInputs(party,path).applyTombs(target,invo).getHitpoints();
    }
}
