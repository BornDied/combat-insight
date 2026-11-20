package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.api.*;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveMagicTest
{
    @Test public void unlockedElementAddsTwoBeforeWeaknessAndDoesNotUpgradeOtherElements()
    {
        for (int i=0;i<4;i++)
        {
            MagicSpell[] spells={MagicSpell.EARTH_SURGE,MagicSpell.WIND_SURGE,MagicSpell.FIRE_SURGE,MagicSpell.WATER_SURGE};
            for(int j=0;j<4;j++)
                assertEquals(i==j ? 26 : 24, capture(spells[j],16434+i,CombatStyleOverride.MAGIC).getMagicBaseMaxHit());
        }
    }
    @Test public void archiveElementalCasesProduceSupportedSnapshots()
    {
        int[] targets={16653,16652,16660,16687,16643,16645,16647};
        MagicSpell[] spells={MagicSpell.WIND_SURGE,MagicSpell.EARTH_SURGE,MagicSpell.FIRE_SURGE,MagicSpell.WATER_SURGE};
        for(int id:targets) for(MagicSpell spell:spells) {
            LiveCombatSnapshot result=capture(spell,0,CombatStyleOverride.MAGIC,id);
            assertTrue("max hit unavailable for "+id+" "+spell,result.isMaxHitAvailable());
        }
        assertTrue(capture(MagicSpell.WIND_SURGE,0,CombatStyleOverride.MAGIC,16653).getMaxHit()
            > capture(MagicSpell.WATER_SURGE,0,CombatStyleOverride.MAGIC,16653).getMaxHit());
        assertTrue(capture(MagicSpell.EARTH_SURGE,0,CombatStyleOverride.MAGIC,16652).getMaxHit()
            > capture(MagicSpell.WATER_SURGE,0,CombatStyleOverride.MAGIC,16652).getMaxHit());
        assertTrue(capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16660).getMaxHit()
            > capture(MagicSpell.WATER_SURGE,0,CombatStyleOverride.MAGIC,16660).getMaxHit());
        assertTrue(capture(MagicSpell.WATER_SURGE,0,CombatStyleOverride.MAGIC,16687).getMaxHit()
            > capture(MagicSpell.FIRE_SURGE,0,CombatStyleOverride.MAGIC,16687).getMaxHit());
    }
    @Test public void upgradeDoesNotAffectAncientsOrSavedSpellDuringMelee()
    {
        assertEquals(30,capture(MagicSpell.ICE_BARRAGE,16436,CombatStyleOverride.MAGIC).getMagicBaseMaxHit());
        assertEquals(capture(MagicSpell.NOT_SET,0,CombatStyleOverride.AUTO).getMaxHitText(),
            capture(MagicSpell.FIRE_SURGE,16436,CombatStyleOverride.AUTO).getMaxHitText());
    }
    @Test public void tomeBaseScalesWithMagicAndCannotBorrowSavedSpell() throws Exception
    {
        assertEquals(18, field(base("zorya's tome",85),"baseMaxHit"));
        assertEquals(23, field(base("zorya's tome",99),"baseMaxHit"));
        assertEquals(30, field(base("zorya's tome",120),"baseMaxHit"));
        assertEquals(true,field(base("zorya's tome",99),"poweredWeapon"));
        assertEquals(false,field(base("zorya's tome (uncharged)",99),"supported"));
    }
    private Object base(String name,int level) throws Exception
    {
        Method m=LiveCombatSnapshot.class.getDeclaredMethod("getMagicBase",String.class,int.class,MagicSpell.class,boolean.class,boolean.class);
        m.setAccessible(true);return m.invoke(null,name,level,MagicSpell.FIRE_SURGE,true,false);
    }
    private Object field(Object o,String name) throws Exception
    { Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(o); }
    static LiveCombatSnapshot capture(MagicSpell spell,int unlocked,CombatStyleOverride style)
    { return capture(spell,unlocked,style,1); }
    static LiveCombatSnapshot capture(MagicSpell spell,int unlocked,CombatStyleOverride style,int targetId)
    { return capture(spell,unlocked,style,targetId,TargetEffectSnapshot.empty()); }
    static LiveCombatSnapshot capture(MagicSpell spell,int unlocked,CombatStyleOverride style,int targetId,TargetEffectSnapshot effects)
    {
        return capture(spell,unlocked,style,targetId,effects,RaidScaling.DEFAULT,EncounterContext.UNKNOWN,0);
    }
    static LiveCombatSnapshot capture(MagicSpell spell,int unlocked,CombatStyleOverride style,int targetId,
        TargetEffectSnapshot effects,RaidScaling raids,EncounterContext context,int invocation)
    {
        Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)-> {
            if(m.getName().equals("getGameState"))return GameState.LOGGED_IN;
            if(m.getName().equals("getVarbitValue"))return ((Integer)a[0])==unlocked ? 1 : 0;
            if(m.getName().equals("getBoostedSkillLevel")||m.getName().equals("getRealSkillLevel"))return 99;
            if(m.getReturnType()==boolean.class)return false;
            if(m.getReturnType()==int.class)return 0;
            return null;
        });
        return LiveCombatSnapshot.capture(client,null,style,BlowpipeDart.NOT_SET,spell,false,invocation,targetId,"Test NPC",30,30,
            effects,false,null,raids,context,false,DefenceTrackingMode.LOCAL);
    }
}
