package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.lang.reflect.Proxy;
import net.runelite.api.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveEncounterStateTest
{
    @Test public void partyDecoderCountsOccupiedSlotsIncludingDeadAndDisconnected()
    {
        for(int size=1;size<=8;size++) {
            int[] slots=new int[8];for(int i=0;i<size;i++)slots[i]=i%3==0?31:i%3==1?30:15;
            assertEquals(size,ArchiveEncounterState.partySize(1,1,slots));
        }
        assertEquals(0,ArchiveEncounterState.partySize(0,1,new int[]{15,15,0,0,0,0,0,0}));
        assertEquals(0,ArchiveEncounterState.partySize(1,3,new int[]{15,15,0,0,0,0,0,0}));
        assertEquals(0,ArchiveEncounterState.partySize(1,1,new int[]{32,0,0,0,0,0,0,0}));
    }
    @Test public void partyTrackingKeepsDeadPlayersButClearsForANewRaid()
    {
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC boss=ArchiveObservedStateTest.npc(16652,80,101,-1,3,-1);
        EncounterContext five=EncounterContext.UNKNOWN.withArchiveState(new ArchiveEncounterState(1,5,false));
        EncounterContext three=EncounterContext.UNKNOWN.withArchiveState(new ArchiveEncounterState(1,3,false));
        assertEquals(5,tracker.apply(boss,five).archive.partySize);
        assertEquals(5,tracker.apply(boss,three).archive.partySize);
        tracker.apply(null,EncounterContext.UNKNOWN.withArchiveState(new ArchiveEncounterState(0,0,false)));
        assertEquals(3,tracker.apply(boss,three).archive.partySize);
        tracker.clear();assertEquals(0,tracker.apply(boss,EncounterContext.UNKNOWN).archive.partySize);
    }
    @Test public void ezekielEnragePersistsForThatActorUntilRemoved()
    {
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC boss=ArchiveObservedStateTest.npc(16652,20,101,-1,3,-1);
        tracker.apply(boss,EncounterContext.from(boss,EncounterMode.AUTO));
        assertTrue(tracker.apply(boss,EncounterContext.UNKNOWN).archive.ezekielEnraged);
        assertFalse(tracker.apply(ArchiveObservedStateTest.npc(16652,80,101,-1,3,-1),EncounterContext.UNKNOWN).archive.ezekielEnraged);
        tracker.remove(boss);assertFalse(tracker.apply(boss,EncounterContext.UNKNOWN).archive.ezekielEnraged);
    }
    @Test public void impBuffCannotExpireMerelyBecauseItsShortGraphicEnded()
    {
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC elder=ArchiveObservedStateTest.npc(16643,80,101,-1,2,-1);
        tracker.observeImpBuff(true,true);
        tracker.observeImpBuff(true,false);
        EncounterContext c=tracker.apply(elder,EncounterContext.UNKNOWN);
        assertTrue(c.archive.impBuffUncertain);
        assertFalse(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16643,
            TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,c,0).isDpsAvailable());
        tracker.observeImpBuff(false,false);
        assertFalse(tracker.apply(elder,EncounterContext.UNKNOWN).archive.impBuffUncertain);
    }
    @Test public void regionLoadingClearsActorStatesButRetainsStartingPartySize()
    {
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC boss=ArchiveObservedStateTest.npc(16652,20,101,-1,3,-1);
        EncounterContext c=EncounterContext.from(boss,EncounterMode.AUTO).withArchiveState(new ArchiveEncounterState(1,6,true));
        tracker.apply(boss,c);tracker.clearActors();
        EncounterContext after=tracker.apply(boss,EncounterContext.UNKNOWN);
        assertEquals(6,after.archive.partySize);assertFalse(after.archive.ezekielEnraged);
    }
    @Test public void ezekielEnrageCannotPublishTheOrdinaryDamageDistribution()
    {
        NPC actor=ArchiveObservedStateTest.npc(16652,100,101,14696,3,-1);
        EncounterContext c=EncounterContext.from(actor,EncounterMode.AUTO);
        LiveCombatSnapshot s=ArchiveMagicTest.capture(MagicSpell.EARTH_SURGE,0,CombatStyleOverride.MAGIC,
            16652,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,c,0);
        assertFalse(s.isMaxHitAvailable());assertFalse(s.isDpsAvailable());
        assertTrue(s.getMaxHitText().contains("Ezekiel enrage"));
    }
    @Test public void lowEzekielHealthAlsoGuardsLateEntryWhenTheEnrageAnimationWasMissed()
    {
        NPC actor=ArchiveObservedStateTest.npc(16652,20,101,-1,3,-1);
        assertTrue(ArchiveEncounterState.observe(null,actor).ezekielEnraged);
        assertFalse(ArchiveEncounterState.observe(null,ArchiveObservedStateTest.npc(16652,80,101,-1,3,-1)).ezekielEnraged);
    }
}
