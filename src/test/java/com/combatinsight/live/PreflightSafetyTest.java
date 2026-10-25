package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.api.NPC;
import net.runelite.client.game.ItemEquipmentStats;
import org.junit.Test;
import static org.junit.Assert.*;

/** Exercise complete equipped snapshots at the boundary between supported and unknown inputs. */
public class PreflightSafetyTest
{
    @Test public void unknownRubyHitpointsCannotPublishAnUnenchantedMaximum() throws Exception
    {
        LiveCombatSnapshot s=ruby(EncounterContext.UNKNOWN.withBorkQuartile(0));
        assertFalse(s.hasTargetHealth());
        assertFalse(s.isDpsAvailable());assertFalse(s.isSpecialMaximumAvailable());
        assertFalse("The displayed maximum must include a resolved ruby effect",s.isMaxHitAvailable());
        assertFalse("Ordinary accuracy is not the enchanted proc's combined hit chance",s.isAccuracyAvailable());
    }
    @Test public void plainBoltsStillCalculateWithoutTargetHitpoints() throws Exception
    {
        LiveCombatSnapshot s=LiveEquipmentCaptureTest.equipped(26374,"Zaryte crossbow",ItemEquipmentStats.builder().slot(3).arange(110).aspeed(6).build(),
            BlowpipeDart.NOT_SET,1,16640,MagicSpell.NOT_SET,EncounterContext.UNKNOWN.withBorkQuartile(0),
            CombatStyleOverride.RANGED,21905,"Dragon bolts",122);
        assertFalse(s.hasTargetHealth());assertTrue(s.isMaxHitAvailable());
        assertTrue(s.isAccuracyAvailable());assertTrue(s.isDpsAvailable());
    }
    @Test public void matchingBossHudOverridesManualPartySizeForRubyDamage() throws Exception
    {
        NPC boss=ArchiveObservedStateTest.npc(16640,100,101,-1,4,-1);
        EncounterContext c=EncounterContext.from(ArchiveObservedStateTest.client(16640,100,1000,false,boss),boss,EncounterMode.AUTO)
            .withBorkQuartile(0).withArchivePartySize(8);
        LiveCombatSnapshot s=ruby(c);
        assertEquals("100 / 1,000",s.getTargetHealthText());
        assertTrue(s.isDpsAvailable());assertTrue(s.isSpecialMaximumAvailable());
        // Zaryte ruby: floor(100 * .22)=22, then Bork's observed physical armour8.
        assertEquals("14",s.getSpecialMaximumText(false));
    }
    @Test public void aSharedBossBarCannotSupplyRubyHitpoints() throws Exception
    {
        NPC boss=ArchiveObservedStateTest.npc(16640,-1,-1,-1,4,-1);
        EncounterContext c=EncounterContext.from(ArchiveObservedStateTest.client(16640,100,1000,true,boss),boss,EncounterMode.AUTO)
            .withBorkQuartile(0);
        LiveCombatSnapshot s=ruby(c);
        assertFalse(s.hasTargetHealth());assertFalse(s.isSpecialMaximumAvailable());
        assertFalse(s.isMaxHitAvailable());
    }
    @Test public void latePhaseUncertaintyBlocksSpecialsIncludingGuaranteedAccuracy() throws Exception
    {
        for(int target:new int[]{16652,16643}) {
            EncounterContext context=target==16652?
                EncounterContext.from(ArchiveObservedStateTest.npc(target,20,101,14696,3,-1),EncounterMode.AUTO):
                EncounterContext.UNKNOWN.withArchiveState(ArchiveEncounterState.UNKNOWN.withImpBuffUncertain());
            for(int weapon=0;weapon<4;weapon++) {
                String[] names={"Voidwaker","Dragon claws","Tonalztics of ralos","Zaryte crossbow"};
                int[] ids={27690,13652,28919,26374};
                CombatStyleOverride style=weapon<2?CombatStyleOverride.MELEE:CombatStyleOverride.RANGED;
                LiveCombatSnapshot s=LiveEquipmentCaptureTest.equipped(ids[weapon],names[weapon],
                    ItemEquipmentStats.builder().slot(3).astab(70).aslash(70).str(80).arange(100).aspeed(5).build(),
                    BlowpipeDart.NOT_SET,1,target,MagicSpell.FIRE_SURGE,context,style,21948,"Diamond dragon bolts (e)",122);
                assertFalse(names[weapon]+" target="+target,s.isMaxHitAvailable());
                assertFalse(s.isDpsAvailable());assertFalse(s.isSpecialMaximumAvailable());
                assertFalse(s.isSpecialExpectedDamageAvailable());
            }
        }
    }
    @Test public void enrageStateDoesNotLeakIntoANewBossActor()
    {
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC old=ArchiveObservedStateTest.npc(16652,20,101,14696,3,-1);
        tracker.apply(old,EncounterContext.from(old,EncounterMode.AUTO));
        NPC fresh=ArchiveObservedStateTest.npc(16652,100,101,-1,3,-1);
        EncounterContext c=tracker.apply(fresh,EncounterContext.from(fresh,EncounterMode.AUTO));
        assertTrue(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16652,
            TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,c,0).isDpsAvailable());
    }
    private static LiveCombatSnapshot ruby(EncounterContext context) throws Exception
    {
        return LiveEquipmentCaptureTest.equipped(26374,"Zaryte crossbow",ItemEquipmentStats.builder().slot(3).arange(110).aspeed(6).build(),
            BlowpipeDart.NOT_SET,1,16640,MagicSpell.NOT_SET,context,CombatStyleOverride.RANGED,21946,"Ruby dragon bolts (e)",122);
    }
}
