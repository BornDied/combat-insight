package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import net.runelite.api.NPC;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpecialHitAttributionTest
{
    @Test public void aConfirmedOcellusPhaseResetClearsOldPhaseUncertainty()
    {
        java.util.concurrent.atomic.AtomicInteger id=new java.util.concurrent.atomic.AtomicInteger(16674);
        NPC original=SpecialAttackTrackerTest.npc(16674,"Ocellus");
        NPC target=(NPC)java.lang.reflect.Proxy.newProxyInstance(NPC.class.getClassLoader(),new Class<?>[]{NPC.class},
            (p,m,a)->m.getName().equals("getId")?id.get():m.invoke(original,a));
        SpecialAttackTracker tracker=new SpecialAttackTracker();tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500,SpecialAttackWeapon.BANDOS_GODSWORD,target,0);
        tracker.onHitsplat(target,20,true,1);tracker.onGameTick(1);
        tracker.onEnergyChanged(0,SpecialAttackWeapon.TONALZTICS_OF_RALOS,target,2);tracker.onGameTick(4);
        assertTrue(tracker.getTargetEffects(target,4).isReductionUncertain());
        assertTrue(tracker.getTargetEffects(target,5).isReductionUncertain());
        id.set(16677);
        TargetEffectSnapshot next=tracker.getTargetEffects(target,6);
        assertEquals(250,next.getCurrentDefence());assertFalse(next.isReductionUncertain());
        assertEquals(100,next.getRemainingDefence());
        tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.TONALZTICS_OF_RALOS,target,7);
        id.set(16674);
        assertFalse(tracker.onHitsplat(target,20,true,9));tracker.onGameTick(9);
        assertEquals(215,tracker.getTargetEffects(target,9).getCurrentDefence());
        assertFalse(tracker.getTargetEffects(target,9).isReductionUncertain());
    }
    @Test public void loadingKeepsOnlyTheSameActorsExistingEstimate()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();NPC target=SpecialAttackTrackerTest.npc(1,"Man");
        tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.DRAGON_WARHAMMER,target,0);
        tracker.onHitsplat(target,10,true,1);tracker.onGameTick(1);tracker.onLoading();
        assertEquals(35,tracker.getTargetEffects(target,1).getCurrentDefence());
        assertEquals(50,tracker.getTargetEffects(SpecialAttackTrackerTest.npc(1,"Man"),1).getCurrentDefence());
    }
    @Test public void unresolvedAttributionCannotFeedAnOrdinaryAccuracyCalculation()
    {
        TargetEffectSnapshot unknown=TargetEffectSnapshot.unmodified(TargetDatabase.find(16674)).withUncertainReductions();
        LiveCombatSnapshot snapshot=ArchiveMagicTest.capture(com.combatinsight.calculation.MagicSpell.FIRE_SURGE,0,
            com.combatinsight.calculation.CombatStyleOverride.RANGED,16674,unknown,RaidScaling.DEFAULT,EncounterContext.UNKNOWN,0);
        assertFalse(snapshot.isAccuracyAvailable());assertFalse(snapshot.isDpsAvailable());
        assertTrue(snapshot.getWarnings().toString().contains("attribution unresolved"));
    }
    @Test public void distanceChangesDelayedSpellTimingAndMissingDistanceIsNotGuessed()
    {
        assertEquals(2,SpecialHitTiming.delay(SpecialAttackWeapon.TONALZTICS_OF_RALOS,1));
        assertEquals(2,SpecialHitTiming.delay(SpecialAttackWeapon.TONALZTICS_OF_RALOS,12));
        assertEquals(2,SpecialHitTiming.delay(SpecialAttackWeapon.ACCURSED_SCEPTRE,1));
        assertEquals(5,SpecialHitTiming.delay(SpecialAttackWeapon.ACCURSED_SCEPTRE,8));
        assertEquals(-1,SpecialHitTiming.delay(SpecialAttackWeapon.ACCURSED_SCEPTRE,-1));
    }
    @Test public void noCapturedTargetCannotBorrowAnotherNpcsHitsplat()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();tracker.resetEnergy(1000);
        assertFalse(tracker.onEnergyChanged(500,SpecialAttackWeapon.DRAGON_WARHAMMER,null,10));
        assertFalse(tracker.onHitsplat(SpecialAttackTrackerTest.npc(1,"Man"),20,11));
    }
    @Test public void ralosUsesACompleteExpectedTickPairBeforeAnyThrallDamage()
    {
        int[][] hits={{0,0,3},{0,2,3},{1,2,3}};
        int[] expected={90,77,64};
        for(int i=0;i<hits.length;i++)
        {
            SpecialAttackTracker tracker=new SpecialAttackTracker();NPC target=SpecialAttackTrackerTest.npc(2,"Ghost");
            tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.TONALZTICS_OF_RALOS,target,10);
            assertFalse(tracker.onHitsplat(target,3,11));
            for(int amount:hits[i])tracker.onHitsplat(target,amount,amount>0,12);
            tracker.onGameTick(12);
            assertEquals(expected[i],tracker.getTargetEffects(target,12).getCurrentDefence());
            assertFalse(tracker.getTargetEffects(target,12).isReductionUncertain());
        }
    }
    @Test public void anIncompleteRalosPairCannotClaimADrainOrAFloor()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();NPC target=SpecialAttackTrackerTest.npc(16674,"Ocellus");
        tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.TONALZTICS_OF_RALOS,target,10);
        tracker.onHitsplat(target,20,true,12);
        assertEquals(215,tracker.getTargetEffects(target,12).getCurrentDefence());
        tracker.onGameTick(12);
        TargetEffectSnapshot snapshot=tracker.getTargetEffects(target,12);
        assertTrue(snapshot.isReductionUncertain());assertFalse(snapshot.isAtDefenceFloor());
        assertEquals("Unresolved",snapshot.getRemainingDefenceText());
    }
    @Test public void instantSpecsUseTheLastOwnedHitOnTheExpectedTick()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();NPC target=SpecialAttackTrackerTest.npc(1,"Man");
        tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.BANDOS_GODSWORD,target,10);
        tracker.onHitsplat(target,3,true,11);tracker.onHitsplat(target,20,true,11);tracker.onGameTick(11);
        assertEquals(30,tracker.getTargetEffects(target,11).getCurrentDefence());
        assertFalse(tracker.onHitsplat(target,20,true,12));
    }
}
