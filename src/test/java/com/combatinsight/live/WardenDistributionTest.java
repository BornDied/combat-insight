package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class WardenDistributionTest
{
    @Test public void magicShieldUsesAccuracyToChooseUniformDamageRange() throws Exception
    {
        Object r = basic(11753, CombatStyle.MAGIC, AttackType.MAGIC, 100);
        // Attack 6912, defence 6766; delta 4657 => 17..37%.
        assertEquals(1.0, number(r,"hitChance"),0);
        assertEquals(37.0,number(r,"maximumHit"),0);
        assertEquals(27.0,number(r,"averageSuccessfulHit"),0);
        assertEquals(11.25,number(r,"damagePerSecond"),1e-10);
    }

    @Test public void rangedShieldUsesRangedDefenceAndGuaranteedAccuracy() throws Exception
    {
        Object r = basic(11754, CombatStyle.RANGED, AttackType.RANGED_HEAVY, 100);
        // Attack 6848, defence 14606; delta 1980 => 16..36%.
        assertEquals(1.0,number(r,"hitChance"),0);
        assertEquals(36.0,number(r,"maximumHit"),0);
        assertEquals(26.0,number(r,"averageSuccessfulHit"),0);
    }

    @Test public void protectedStylesCannotDamageActiveWarden() throws Exception
    {
        for(int id : new int[]{11753,11754,11756,11757})
        {
            assertEquals(0.0,number(basic(id,CombatStyle.MELEE,AttackType.STAB,100),"damagePerSecond"),0);
        }
    }

    @Test public void exposedCoreAlwaysReceivesMeleeMaximum() throws Exception
    {
        for(int id : new int[]{11770,11771})
        {
            Object r=basic(id,CombatStyle.MELEE,AttackType.STAB,100);
            assertEquals(1.0,number(r,"hitChance"),0);
            assertEquals(100.0,number(r,"averageSuccessfulHit"),0);
        }
    }

    @Test public void enchantedBoltsKeepTheShieldMinimumBeforeTheirProcTransform() throws Exception
    {
        String[] bolts={"diamond bolts (e)","onyx bolts (e)","dragonstone bolts (e)","ruby bolts (e)"};
        // Heavy ranged inputs give 16..36. Replacement proc rolls still start at zero;
        // dragonstone adds 19 to the actual 16..36 roll; ruby here deals 20.
        double[] expected={.9*26+.1*(862.0/42),.89*26+.11*(947.0/44),26+.06*19,.94*26+.06*20};
        for(int i=0;i<bolts.length;i++)
            assertEquals(bolts[i],expected[i],number(basic(11754,CombatStyle.RANGED,AttackType.RANGED_HEAVY,100,bolts[i]),"averageSuccessfulHit"),1e-10);
    }

    private static Object basic(int id,CombatStyle combat,AttackType type,int max) throws Exception
    { return basic(id,combat,type,max,""); }
    private static Object basic(int id,CombatStyle combat,AttackType type,int max,String ammo) throws Exception
    {
        Class<?> owner=LiveCombatSnapshot.class;
        Constructor<?> ec=Class.forName(owner.getName()+"$EquipmentState").getDeclaredConstructor();ec.setAccessible(true);
        Object equipment=ec.newInstance();
        if(!ammo.isEmpty()) {
            Field weapon=equipment.getClass().getDeclaredField("weaponNameLower");weapon.setAccessible(true);weapon.set(equipment,"rune crossbow");
            Field ammunition=equipment.getClass().getDeclaredField("ammoNameLower");ammunition.setAccessible(true);ammunition.set(equipment,ammo);
        }
        Constructor<?> pc=Class.forName(owner.getName()+"$PrayerState").getDeclaredConstructor();pc.setAccessible(true);
        Constructor<?> sc=Class.forName(owner.getName()+"$StyleState").getDeclaredConstructors()[0];sc.setAccessible(true);
        Object style=sc.newInstance(combat,type,"Test",0,0);
        for(Method m:owner.getDeclaredMethods()) if(m.getName().equals("calculateTargetResult"))
        {
            m.setAccessible(true);
            return m.invoke(null,style,equipment,pc.newInstance(),id,target(id),99,99,99,
                MagicSpell.NOT_SET,false,0,0,max,true,"",4,false,HitTransform.IDENTITY,100,false,99);
        }
        throw new AssertionError("Missing target calculator");
    }
    private static TargetProfile target(int id)
    {
        net.runelite.api.NPCComposition composition=(net.runelite.api.NPCComposition)Proxy.newProxyInstance(
            net.runelite.api.NPCComposition.class.getClassLoader(),new Class<?>[]{net.runelite.api.NPCComposition.class},
            (p,m,a)->m.getReturnType()==int.class?1:null);
        net.runelite.api.NPC npc=(net.runelite.api.NPC)Proxy.newProxyInstance(
            net.runelite.api.NPC.class.getClassLoader(),new Class<?>[]{net.runelite.api.NPC.class},(p,m,a)->{
                if(m.getName().equals("getId")) return id;
                if(m.getName().equals("getComposition")) return composition;
                if(m.getReturnType()==int.class)return 0;
                return null;
            });
        return EncounterResolver.resolve(id,EncounterContext.from(npc,EncounterMode.AUTO));
    }
    private static double number(Object o,String name) throws Exception
    { Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);return ((Number)f.get(o)).doubleValue(); }
}
