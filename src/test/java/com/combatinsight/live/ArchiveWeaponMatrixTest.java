package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.client.game.ItemEquipmentStats;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveWeaponMatrixTest
{
    @Test public void allSixTrioFormsUseTwoScytheHitsAndTheSameOrdinaryDefences() throws Exception
    {
        for(int id=16643;id<=16648;id++) {
            LiveCombatSnapshot s=scythe(id,EncounterContext.UNKNOWN);
            assertEquals("Trio "+id,"29 / 14",s.getMultiHitSplitText());
            assertEquals("43",s.getMaxHitText());assertEquals(5,s.getAttackSpeedTicks());
            assertTrue(s.isDpsAvailable());
            assertFalse(s.hasTargetHealth()); // shield/shared HP is not an individual health estimate
        }
    }
    @Test public void combatAddsAndBossesUseTheirOwnFootprintsForScythe() throws Exception
    {
        for(int id:new int[]{16650,16666,16670,16678,16679,16680,16681,16687,16688})
            assertEquals("NPC "+id,"29",scythe(id,EncounterContext.UNKNOWN).getMaxHitText());
        for(int id:new int[]{16652,16653,16660,16665,16674,16676,16677,16684})
            assertEquals("NPC "+id,"29 / 14 / 7",scythe(id,EncounterContext.UNKNOWN).getMultiHitSplitText());
        assertEquals("29 / 14",scythe(16662,EncounterContext.UNKNOWN).getMultiHitSplitText());
        assertEquals("Immune",scythe(16659,EncounterContext.UNKNOWN).getMaxHitText());
        assertEquals("Immune",scythe(16667,EncounterContext.UNKNOWN).getMaxHitText());
    }
    @Test public void transformedFootprintReachesTheActualScytheDistribution() throws Exception
    {
        EncounterContext sizeOne=EncounterContext.from(ArchiveObservedStateTest.npc(16652,80,101,-1,1,-1),EncounterMode.AUTO);
        assertEquals("29",scythe(16652,sizeOne).getMaxHitText());
        assertEquals("29 / 14 / 7",scythe(16652,EncounterContext.UNKNOWN).getMultiHitSplitText());
    }
    @Test public void trioTbowShadowAndRalosCalculateAcrossEveryForm() throws Exception
    {
        for(int id=16643;id<=16648;id++) {
            LiveCombatSnapshot tbow=LiveEquipmentCaptureTest.equipped(20997,"Twisted bow",ItemEquipmentStats.builder().slot(3).arange(70).rstr(20).aspeed(6).build(),BlowpipeDart.NOT_SET,1,id,MagicSpell.FIRE_SURGE,EncounterContext.UNKNOWN);
            assertTrue(tbow.isDpsAvailable());assertEquals(AttackType.RANGED_STANDARD,tbow.getAttackType());
            LiveCombatSnapshot ralos=LiveEquipmentCaptureTest.equipped(28919,"Tonalztics of ralos",ItemEquipmentStats.builder().slot(3).arange(100).rstr(0).aspeed(6).build(),BlowpipeDart.NOT_SET,1,id,MagicSpell.FIRE_SURGE,EncounterContext.UNKNOWN);
            assertTrue(ralos.isDpsAvailable());assertTrue(ralos.isSpecialExpectedDamageAvailable());
            LiveCombatSnapshot shadow=LiveEquipmentCaptureTest.equipped(27275,"Tumeken's shadow",ItemEquipmentStats.builder().slot(3).amagic(35).mdmg(0).aspeed(5).build(),BlowpipeDart.NOT_SET,0,id,MagicSpell.FIRE_SURGE,EncounterContext.UNKNOWN,CombatStyleOverride.MAGIC);
            assertTrue(shadow.isDpsAvailable());
            assertEquals(shadow.getMaxHitText(),LiveEquipmentCaptureTest.equipped(27275,"Tumeken's shadow",ItemEquipmentStats.builder().slot(3).amagic(35).mdmg(0).aspeed(5).build(),BlowpipeDart.NOT_SET,0,id,MagicSpell.NOT_SET,EncounterContext.UNKNOWN,CombatStyleOverride.MAGIC).getMaxHitText());
        }
    }
    @Test public void obligatorThrustAndSmashUseSizesOneTwoAndThreeWithoutASavedSpellLeak() throws Exception
    {
        int[] ids={16650,16662,16652,16643,16644,16645,16646,16647,16648,16676,16684};
        int[] thrust={21,25,29,25,25,25,25,25,25,29,29};
        for(int i=0;i<ids.length;i++)for(int mode=0;mode<3;mode++) {
            LiveCombatSnapshot s=LiveEquipmentCaptureTest.equipped(34715,"Obligator",
                ItemEquipmentStats.builder().slot(3).acrush(120).str(40).aspeed(5).build(),
                BlowpipeDart.NOT_SET,mode,ids[i],MagicSpell.FIRE_SURGE,EncounterContext.UNKNOWN,CombatStyleOverride.MELEE);
            assertEquals("NPC "+ids[i]+" mode="+mode,Integer.toString(mode==2?21:thrust[i]),s.getMaxHitText());
            assertEquals(AttackType.CRUSH,s.getAttackType());assertEquals(mode==2?3:5,s.getAttackSpeedTicks());
            assertTrue(s.isDpsAvailable());
        }
    }
    @Test public void magicDamageDoesNotGiveVoidwakerExtraReach() throws Exception
    {
        for(CombatStyleOverride forced:new CombatStyleOverride[]{CombatStyleOverride.MELEE,CombatStyleOverride.MAGIC}) {
            for(int id:new int[]{16667,16659,16677}) {
                EncounterContext c=id==16677?EncounterContext.from(ArchiveObservedStateTest.npc(id,80,101,14841,3,-1),EncounterMode.AUTO):EncounterContext.UNKNOWN;
                LiveCombatSnapshot s=LiveEquipmentCaptureTest.equipped(27690,"Voidwaker",ItemEquipmentStats.builder().slot(3).astab(70).str(80).aspeed(4).build(),
                    BlowpipeDart.NOT_SET,0,id,MagicSpell.FIRE_SURGE,c,forced);
                assertEquals("Voidwaker reach "+id+" "+forced,"Immune",s.getSpecialMaximumText(false));
            }
        }
    }
    @Test public void guaranteedMaximumAndEnchantedBoltProcCompositionMustBeEstablished() throws Exception
    {
        for(int id:new int[]{16650,16679})for(String ammo:new String[]{"Diamond dragon bolts (e)","Onyx dragon bolts (e)"}) {
            LiveCombatSnapshot s=LiveEquipmentCaptureTest.equipped(26374,"Zaryte crossbow",ItemEquipmentStats.builder().slot(3).arange(110).aspeed(6).build(),
                BlowpipeDart.NOT_SET,1,id,MagicSpell.NOT_SET,EncounterContext.UNKNOWN,CombatStyleOverride.RANGED,21948,ammo,122);
            assertFalse(id+" "+ammo,s.isDpsAvailable());assertFalse(s.isMaxHitAvailable());
            assertFalse(s.isSpecialMaximumAvailable());assertFalse(s.isSpecialExpectedDamageAvailable());
        }
    }
    private static LiveCombatSnapshot scythe(int id,EncounterContext context) throws Exception
    {
        return LiveEquipmentCaptureTest.equipped(22325,"Scythe of vitur",ItemEquipmentStats.builder().slot(3).aslash(110).acrush(30).str(75).aspeed(5).build(),BlowpipeDart.NOT_SET,1,id,MagicSpell.FIRE_SURGE,context,CombatStyleOverride.MELEE);
    }
}
