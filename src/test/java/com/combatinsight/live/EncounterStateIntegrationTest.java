package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.api.*;
import java.lang.reflect.Proxy;
import org.junit.Test;
import static org.junit.Assert.*;

public class EncounterStateIntegrationTest
{
    @Test public void observedWardenProtectionControlsTheActualSnapshot()
    {
        assertEquals("Immune",snapshot(11753,CombatStyleOverride.MAGIC,context(11753,8,-1,5)).getMaxHitText());
        LiveCombatSnapshot open=snapshot(11753,CombatStyleOverride.MAGIC,context(11753,7,-1,5));
        assertTrue(open.isDpsAvailable());
        assertEquals("100.0%",open.getHitChanceText());
        assertFalse(snapshot(11753,CombatStyleOverride.MAGIC,EncounterContext.UNKNOWN).isDpsAvailable());
    }
    @Test public void liveCoreNeedsObservedSizeAndOnlyEnablesSupportedMelee()
    {
        assertTrue(snapshot(11770,CombatStyleOverride.AUTO,context(11770,-1,-1,1)).isDpsAvailable());
        assertFalse(snapshot(11770,CombatStyleOverride.AUTO,EncounterContext.UNKNOWN).isDpsAvailable());
        assertFalse(snapshot(11770,CombatStyleOverride.MAGIC,context(11770,-1,-1,1)).isDpsAvailable());
        assertFalse(snapshot(11758,CombatStyleOverride.AUTO,context(11758,-1,-1,5)).isDpsAvailable());
    }
    @Test public void borkStunEnablesGuaranteedMeleeWithoutChoosingConflictingStandingStats()
    {
        LiveCombatSnapshot stunned=snapshot(16640,CombatStyleOverride.AUTO,context(16640,-1,14538,4));
        assertTrue(stunned.isDpsAvailable());
        assertEquals("100.0%",stunned.getHitChanceText());
        assertFalse(stunned.hasTargetHealth());
        assertFalse(snapshot(16640,CombatStyleOverride.AUTO,context(16640,-1,-1,4)).isDpsAvailable());
        LiveCombatSnapshot magic=snapshot(16640,CombatStyleOverride.MAGIC,context(16640,-1,14538,4));
        assertTrue(magic.isDpsAvailable());
        assertNotEquals("100.0%",magic.getHitChanceText());
    }

    private static LiveCombatSnapshot snapshot(int id,CombatStyleOverride style,EncounterContext context)
    { return ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,style,id,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,context,0); }
    private static EncounterContext context(int id,int overhead,int animation,int size)
    {
        NPCComposition composition=(NPCComposition)Proxy.newProxyInstance(NPCComposition.class.getClassLoader(),
            new Class<?>[]{NPCComposition.class},(p,m,a)->m.getReturnType()==int.class?size:null);
        NPC actor=(NPC)Proxy.newProxyInstance(NPC.class.getClassLoader(),new Class<?>[]{NPC.class},(p,m,a)->{
            if(m.getName().equals("getId")) return id;
            if(m.getName().equals("getComposition")) return composition;
            if(m.getName().equals("getAnimation"))return animation;
            if(m.getName().equals("getPoseAnimation"))return -1;
            if(m.getName().equals("getOverheadArchiveIds"))return overhead<0?null:new int[]{440};
            if(m.getName().equals("getOverheadSpriteIds"))return overhead<0?null:new short[]{(short)overhead};
            if(m.getReturnType()==int.class)return 0;
            return null;
        });
        return EncounterContext.from(actor,EncounterMode.AUTO);
    }
}
