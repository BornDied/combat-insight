package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveWeaponIntegrationTest
{
    @Test public void actualObligatorModesKeepStanceSeparateFromAttackMode() throws Exception
    {
        for (boolean forced : new boolean[]{false,true})
        {
            for (int mode=0;mode<3;mode++)
            {
                final int selectedMode=mode;
                net.runelite.api.Client client=(net.runelite.api.Client)Proxy.newProxyInstance(
                    net.runelite.api.Client.class.getClassLoader(),new Class<?>[]{net.runelite.api.Client.class},(p,m,a)-> {
                        if(m.getName().equals("getVarpValue")) return selectedMode;
                        if(m.getReturnType()==int.class) return 0;
                        if(m.getReturnType()==boolean.class) return false;
                        return null;
                    });
                Object equipment=create("EquipmentState");
                set(equipment,"weaponNameLower","obligator");
                set(equipment,"weaponCategory",WeaponCategory.EXTENDING_CHAIN_MACE);
                set(equipment,"crushAttack",120);
                Object selected=forced ? method("getForcedStyle").invoke(null,client,CombatStyleOverride.MELEE,equipment)
                    : method("getStyleState").invoke(null,client,equipment,MagicSpell.FIRE_SURGE);
                assertEquals("mode "+mode,mode==0?3:0,value(selected,"accuracyStyleBonus"));
                assertEquals(mode==0?0:3,value(selected,"damageStyleBonus"));
                assertEquals(mode==2?3:5,speed(equipment,selected));
                // Upper bound supplied to the distribution already includes the size bonus.
                Object result=basic(equipment,selected,mode==2?17:25,mode==2?3:5);
                assertEquals(mode==2?7:15,value(result,"maximumHit"));
                assertEquals(mode==2?3.0:5.0,(double)value(result,"expectedAttackSpeedTicks"),0.0);
                if(mode==1) assertEquals(1.30745,(double)value(result,"damagePerSecond"),.000005);
            }
        }
    }
    @Test public void thrustWorksInAutomaticAndForcedMeleeModes() throws Exception
    {
        for (String name : new String[]{"Aggressive", "Aggressive (forced)"})
        {
            Object equipment = create("EquipmentState");
            set(equipment,"weaponNameLower","obligator");
            set(equipment,"obligatorStyleIndex",1);
            set(equipment,"crushAttack",120);
            Object style = style(CombatStyle.MELEE,AttackType.CRUSH,name,0,3);
            assertEquals(5, speed(equipment,style));
            Object result = basic(equipment,style,25,5);
            assertEquals(15,value(result,"maximumHit"));
            assertEquals(1.30745,(double)value(result,"damagePerSecond"),.000005);
            assertEquals("",value(result,"maximumHitSplit"));
        }
    }

    @Test public void tomeEmpowermentChangesBasicAccuracyAndSpeedButNotSpecialTwice() throws Exception
    {
        Object equipment = create("EquipmentState");
        set(equipment,"weaponNameLower","zorya's tome");
        set(equipment,"magicAttack",45);
        set(equipment,"attackSpeedTicks",3);
        Object style = style(CombatStyle.MAGIC,AttackType.MAGIC,"Built-in cast",2,0);
        Object normal = basic(equipment,style,26,3);
        assertEquals(11990,value(normal,"attackRoll"));
        assertEquals(3,speed(equipment,style));
        set(equipment,"tomeEmpowered",true);
        Object buffed = basic(equipment,style,41,2);
        assertEquals(29975,value(buffed,"attackRoll"));
        assertEquals(41,value(buffed,"maximumHit")); // Magic bypasses target's 10 armour.
        assertEquals(2,speed(equipment,style));
        SpecialAttackResult special = (SpecialAttackResult) method("calculateSpecialAttackResult").invoke(null,
            style,equipment,create("PrayerState"),1,"Test",target(),true,TargetEffectSnapshot.empty(),
            99,99,99,MagicSpell.FIRE_SURGE,false,0,41,true,41,false,false,1500,false,99);
        assertArrayEquals(new int[]{41},special.getMaximumHits());
        assertEquals(CombatPrediction.hitChance(29975,CombatPrediction.defenceRoll(target(),AttackType.MAGIC,false)),
            special.getHitChance(),1e-12);
    }

    private static TargetProfile target()
    {
        return new TargetProfile(1,300,90,1500,0,4,10,140,130,20,300,80,50,5,"",0,
            false,Collections.emptySet(),false);
    }
    private static Object basic(Object e,Object style,int max,int speed) throws Exception
    {
        return method("calculateTargetResult").invoke(null,style,e,create("PrayerState"),1,target(),99,99,99,
            MagicSpell.FIRE_SURGE,false,0,0,max,true,"",speed,false,HitTransform.IDENTITY,1500,false,99);
    }
    private static int speed(Object e,Object s) throws Exception
    { return (int)method("getAttackSpeedTicks").invoke(null,e,s,MagicSpell.NOT_SET,1); }
    private static Object style(CombatStyle combat,AttackType type,String name,int accuracy,int damage) throws Exception
    {
        Constructor<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$StyleState").getDeclaredConstructors()[0];
        c.setAccessible(true);return c.newInstance(combat,type,name,accuracy,damage);
    }
    private static Method method(String name)
    {
        for(Method m:LiveCombatSnapshot.class.getDeclaredMethods())
            if(m.getName().equals(name) && Modifier.isStatic(m.getModifiers())){m.setAccessible(true);return m;}
        throw new AssertionError(name);
    }
    private static Object create(String name) throws Exception
    {Constructor<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$"+name).getDeclaredConstructor();c.setAccessible(true);return c.newInstance();}
    private static void set(Object o,String name,Object v) throws Exception
    {Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,v);}
    private static Object value(Object o,String name) throws Exception
    {Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(o);}
}
