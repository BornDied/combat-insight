package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.api.NPC;
import org.junit.Test;
import static org.junit.Assert.*;

public class BorkRecoveryTest
{
    @Test public void leavingTheSlamStateRestoresStandingPredictions()
    {
        NPC bork=ArchiveObservedStateTest.npc(16640,5,30,-1,4,-1);
        NPC smorks=ArchiveObservedStateTest.npc(16641,41,101,-1,3,-1);
        EncounterContext context=EncounterContext.from(
            ArchiveObservedStateTest.client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
        LiveCombatSnapshot result=ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,
            CombatStyleOverride.MELEE,16640,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,context,0);
        assertTrue(result.isMaxHitAvailable());
        assertTrue(result.isDpsAvailable());
    }

    @Test public void recordedSlamStatesCannotPublishStandingArmourPredictions()
    {
        // The supplied run recorded owned hits 57/56 with animations 14549/14550,
        // after a standing prediction of 48. Attacks were also initiated in this window.
        for(int animation:new int[]{14549,14550})
        {
            NPC bork=ArchiveObservedStateTest.npc(16640,5,30,animation,4,-1);
            NPC smorks=ArchiveObservedStateTest.npc(16641,41,101,-1,3,-1);
            EncounterContext context=EncounterContext.from(
                ArchiveObservedStateTest.client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
            context=new ArchiveStateTracker().apply(bork,context.withArchivePartySize(4));
            LiveCombatSnapshot result=ArchiveMagicTest.capture(MagicSpell.NOT_SET,0,
                CombatStyleOverride.MELEE,16640,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,context,0);
            assertFalse(result.isMaxHitAvailable());
            assertFalse(result.isDpsAvailable());
            assertTrue(result.getMaxHitText().contains("Bork slam damage unverified"));
        }
    }
}
