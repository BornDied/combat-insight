package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.api.*;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveObservedStateTest
{
    @Test public void unreadableSmorksBarCannotEraseObservedBorkEnrage()
    {
        NPC bork=npc(16640,-1,-1,14546,4,-1),smorks=npc(16641,-1,-1,-1,3,-1);
        EncounterContext c=EncounterContext.from(client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
        assertEquals(3,c.borkQuartile);
        assertFalse(EncounterResolver.resolve(16640,c).isAmbiguous());
        assertTrue(capture(16640,CombatStyleOverride.MELEE,c).isDpsAvailable());
    }

    @Test public void absentBorkSignalsCannotInventAQuartileOrAStun()
    {
        NPC bork=npc(16640,-1,-1,-1,4,-1),smorks=npc(16641,-1,-1,-1,3,-1);
        EncounterContext c=EncounterContext.from(client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
        assertEquals(-1,c.borkQuartile);
        assertFalse(c.borkStunned);
        assertFalse(capture(16640,CombatStyleOverride.MELEE,c).isDpsAvailable());
    }

    @Test public void readableSmorksHealthStillOverridesTheAnimationFallback()
    {
        NPC bork=npc(16640,-1,-1,14546,4,-1),smorks=npc(16641,71,101,-1,3,-1);
        EncounterContext c=EncounterContext.from(client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
        assertEquals(1,c.borkQuartile);
    }

    @Test public void smorksHealthSelectsBorkArmourAndEnablesStandingDamage()
    {
        int[] ratios={101,71,41,11};
        for(int i=0;i<ratios.length;i++)
        {
            NPC bork=npc(16640,-1,-1,-1,4,-1),smorks=npc(16641,ratios[i],101,-1,3,-1);
            EncounterContext c=EncounterContext.from(client(0,0,0,false,bork,smorks),bork,EncounterMode.AUTO);
            TargetProfile t=EncounterResolver.resolve(16640,c);
            assertFalse(t.isAmbiguous());assertEquals(8+i*2,t.getFlatArmour());
            assertEquals(10,t.defenceBonus(AttackType.CRUSH));
            LiveCombatSnapshot s=capture(16640,CombatStyleOverride.RANGED,c);
            assertTrue(s.isDpsAvailable());assertFalse(s.hasTargetHealth());
        }
    }
    @Test public void bossHudHealthMustMatchTheCurrentNpcAndHaveNoSharedExtraBar()
    {
        NPC xarth=npc(16684,50,100,-1,3,-1);
        EncounterContext c=EncounterContext.from(client(16684,500,1000,false,xarth),xarth,EncounterMode.AUTO);
        assertEquals("500 / 1,000",capture(16684,CombatStyleOverride.MAGIC,c).getTargetHealthText());
        c=EncounterContext.from(client(16677,500,1000,false,xarth),xarth,EncounterMode.AUTO);
        assertFalse(capture(16684,CombatStyleOverride.MAGIC,c).hasTargetHealth());
        c=EncounterContext.from(client(16684,500,1000,true,xarth),xarth,EncounterMode.AUTO);
        assertFalse(capture(16684,CombatStyleOverride.MAGIC,c).hasTargetHealth());
    }
    @Test public void observedBladeHitboxAndAddsUseTheirPublishedStatsAndCacheSize()
    {
        for(int id:new int[]{16676,16678,16679,16680,16681})
        {
            NPC actor=npc(id,50,100,-1,1,-1);
            EncounterContext c=EncounterContext.from(actor,EncounterMode.AUTO);
            assertTrue("Live hitbox "+id,capture(id,CombatStyleOverride.MAGIC,c).isDpsAvailable());
        }
    }
    @Test public void ocellusProtectionBlocksMatchingStyleWithoutBlanketMeleeDisable()
    {
        assertTrue(capture(16674,CombatStyleOverride.AUTO,EncounterContext.UNKNOWN).isDpsAvailable());
        EncounterContext c=EncounterContext.from(npc(16677,50,100,-1,3,13),EncounterMode.AUTO);
        assertEquals("Immune",capture(16677,CombatStyleOverride.RANGED,c).getMaxHitText());
        assertFalse(capture(16677,CombatStyleOverride.MAGIC,c).isDpsAvailable());
        assertFalse(capture(16677,CombatStyleOverride.MAGIC,c).isMaxHitAvailable());
        assertTrue(capture(16677,CombatStyleOverride.MAGIC,EncounterContext.UNKNOWN).isDpsAvailable());
    }
    @Test public void ocellusAirborneAndFinalPhaseAreSeparateFromGroundedBaseDamage()
    {
        EncounterContext airborne=EncounterContext.from(npc(16677,50,100,14841,3,-1),EncounterMode.AUTO);
        assertEquals("Immune",capture(16677,CombatStyleOverride.MELEE,airborne).getMaxHitText());
        assertTrue(capture(16677,CombatStyleOverride.RANGED,airborne).isDpsAvailable());
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        NPC boss=npc(16677,50,100,-1,3,-1);
        EncounterContext finalPhase=EncounterContext.from(npc(16677,50,100,14864,3,12),EncounterMode.AUTO);
        tracker.apply(boss,finalPhase);
        assertFalse(capture(16677,CombatStyleOverride.RANGED,tracker.apply(boss,EncounterContext.UNKNOWN)).isDpsAvailable());
        assertTrue(capture(16677,CombatStyleOverride.RANGED,tracker.apply(boss,EncounterContext.UNKNOWN)).isAccuracyAvailable());
        assertTrue(capture(16677,CombatStyleOverride.RANGED,tracker.apply(boss,EncounterContext.UNKNOWN)).getWarnings().toString().contains("final-phase"));
        tracker.remove(boss);assertTrue(capture(16677,CombatStyleOverride.RANGED,tracker.apply(boss,EncounterContext.UNKNOWN)).isDpsAvailable());
    }
    @Test public void liveOcellusPrayerAndQuadrantSequenceCannotLatchFinalPhaseEarly()
    {
        // Audit2 recording: prayer animation14869 at tick3555 /6191of7610 HP;
        // three quadrant-destruction sequences precede deflect14863 at tick3924.
        NPC boss=npc(16677,50,100,-1,3,-1);
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        int[] earlier={14846,14869,14871,14859,14860,14862,
            14869,14870,14871,14861,14860,14862,
            14869,14870,14871,14861,14860,14862};
        for(int animation:earlier)
        {
            EncounterContext frame=EncounterContext.from(npc(16677,50,100,animation,3,-1),EncounterMode.AUTO);
            EncounterContext state=tracker.apply(boss,frame);
            assertFalse("Early final-phase flag at animation "+animation,state.ocellusFinalPhase);
            assertTrue(capture(16677,CombatStyleOverride.MELEE,state).isMaxHitAvailable());
            assertTrue(capture(16677,CombatStyleOverride.MELEE,state).isDpsAvailable());
        }
        EncounterContext deflect=EncounterContext.from(npc(16677,50,100,14863,3,-1),EncounterMode.AUTO);
        assertTrue(tracker.apply(boss,deflect).ocellusFinalPhase);
        EncounterContext later=tracker.apply(boss,EncounterContext.UNKNOWN);
        assertTrue(later.ocellusFinalPhase);
        assertFalse(capture(16677,CombatStyleOverride.MELEE,later).isMaxHitAvailable());
        assertTrue(capture(16677,CombatStyleOverride.MELEE,later).isAccuracyAvailable());
        tracker.remove(boss);
        assertFalse(tracker.apply(boss,EncounterContext.UNKNOWN).ocellusFinalPhase);
    }
    @Test public void confirmedSmorksDeathSurvivesDespawnForThatBorkOnly()
    {
        NPC bork=npc(16640,-1,-1,-1,4,-1),smorks=npc(16641,0,101,-1,3,-1);
        ArchiveStateTracker tracker=new ArchiveStateTracker();
        tracker.onDeath(client(0,0,0,false,bork,smorks),smorks);
        EncounterContext c=EncounterContext.from(client(0,0,0,false,bork),bork,EncounterMode.AUTO);
        assertEquals(3,tracker.apply(bork,c).borkQuartile);
        assertEquals(-1,tracker.apply(npc(16640,-1,-1,-1,4,-1),c).borkQuartile);
        tracker.remove(bork);assertEquals(-1,tracker.apply(bork,c).borkQuartile);
        tracker.onDeath(client(0,0,0,false,bork,smorks),smorks);tracker.clear();
        assertEquals(-1,tracker.apply(bork,c).borkQuartile);
    }
    @Test public void uncertainHealthBarBoundariesCannotChooseAQuartile()
    {
        assertEquals(-1,EncounterContext.healthQuartile(76,101));
        assertEquals(-1,EncounterContext.healthQuartile(51,101));
        assertEquals(-1,EncounterContext.healthQuartile(26,101));
        assertEquals(3,EncounterContext.healthQuartile(0,101));
        assertEquals(-1,EncounterContext.healthQuartile(-1,101));
        assertEquals(-1,EncounterContext.healthQuartile(1,1));
    }
    private static LiveCombatSnapshot capture(int id,CombatStyleOverride style,EncounterContext c)
    {return ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE,0,style,id,TargetEffectSnapshot.empty(),RaidScaling.DEFAULT,c,0);}
    static NPC npc(int id,int ratio,int scale,int animation,int size,int icon)
    {
        NPCComposition definition=(NPCComposition)Proxy.newProxyInstance(NPCComposition.class.getClassLoader(),
            new Class<?>[]{NPCComposition.class},(p,m,a)->m.getName().equals("getId")?id:m.getName().equals("getSize")?size:m.getReturnType()==int.class?0:null);
        return (NPC)Proxy.newProxyInstance(NPC.class.getClassLoader(),new Class<?>[]{NPC.class},(p,m,a)->{
            switch(m.getName())
            {
                case "getId":return id;
                case "getHealthRatio":return ratio;
                case "getHealthScale":return scale;
                case "getAnimation":return animation;
                case "getPoseAnimation":return -1;
                case "getComposition":case "getTransformedComposition":return definition;
                case "getOverheadArchiveIds":return icon<0?null:new int[]{440};
                case "getOverheadSpriteIds":return icon<0?null:new short[]{(short)icon};
                default:return m.getReturnType()==int.class?0:m.getReturnType()==boolean.class?false:null;
            }
        });
    }
    static Client client(int id,int hp,int max,boolean extra,NPC... actors)
    {
        return (Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)->{
            if(m.getName().equals("getNpcs"))return Arrays.asList(actors);
            if(m.getName().equals("getVarpValue"))return (int)a[0]==1683?id:(int)a[0]==5991 && extra?1:0;
            if(m.getName().equals("getVarbitValue"))return (int)a[0]==6099?hp:(int)a[0]==6100?max:0;
            return m.getReturnType()==int.class?0:m.getReturnType()==boolean.class?false:null;
        });
    }
}
