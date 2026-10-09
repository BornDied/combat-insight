package com.combatinsight.live;

import com.combatinsight.calculation.*;
import net.runelite.client.game.ItemEquipmentStats;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Exercises the same target-result path used by capture, without a game client. */
public class SeekingLiveCalculationTest
{
    @Test public void tbowSeekingNyloIncreasesAverageAndDpsButNotMaximum() throws Exception
    {
        Object regular = targetResult("amethyst arrow");
        Object seeking = targetResult("seeking amethyst arrow");
        assertEquals(80, value(seeking,"maximumHit"));
        assertEquals(value(regular,"maximumHit"),value(seeking,"maximumHit"));
        assertEquals((double)value(regular,"averageSuccessfulHit")+5.0/81,
            (double)value(seeking,"averageSuccessfulHit"),1e-12);
        double chance=(double)value(seeking,"hitChance");
        assertEquals((double)value(seeking,"averageSuccessfulHit")*chance/3,
            (double)value(seeking,"damagePerSecond"),1e-12);
        assertTrue((double)value(seeking,"damagePerSecond")>(double)value(regular,"damagePerSecond"));
    }
    @Test public void ruptureAndSeekingBonusesReachGearTotalsExactlyOnce() throws Exception
    {
        Object equipment=construct("EquipmentState");
        Method add=LiveCombatSnapshot.class.getDeclaredMethod("addEquipmentBonuses",equipment.getClass(),ItemEquipmentStats.class);
        add.setAccessible(true);
        add.invoke(null,equipment,EquipmentStatsFallback.resolve(33639,null));
        add.invoke(null,equipment,EquipmentStatsFallback.resolve(33589,null));
        assertEquals(40,value(equipment,"rangedAttack"));
        assertEquals(63,value(equipment,"rangedStrength"));
    }
    private static Object targetResult(String ammo) throws Exception
    {
        Object equipment=construct("EquipmentState"), prayer=construct("PrayerState");
        set(equipment,"weaponNameLower","twisted bow");
        set(equipment,"ammoNameLower",ammo);
        set(equipment,"rangedAttack",ammo.startsWith("seeking")?180:160);
        Class<?> style=Class.forName(LiveCombatSnapshot.class.getName()+"$StyleState");
        Constructor<?> ctor=style.getDeclaredConstructors()[0];ctor.setAccessible(true);
        Object ranged=ctor.newInstance(CombatStyle.RANGED,AttackType.RANGED_STANDARD,"Rapid",0,0);
        for(Method method:LiveCombatSnapshot.class.getDeclaredMethods())
        {
            if(!method.getName().equals("calculateTargetResult"))continue;
            method.setAccessible(true);
            return method.invoke(null,ranged,equipment,prayer,8357,TargetDatabase.find(8357),
                99,112,99,null,false,0,0,80,true,"",5,false,HitTransform.IDENTITY,1875,false,99);
        }
        throw new AssertionError("Missing live calculation");
    }
    private static Object construct(String nested) throws Exception
    {
        Constructor<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$"+nested).getDeclaredConstructor();
        c.setAccessible(true);return c.newInstance();
    }
    private static Object value(Object o,String field) throws Exception
    { Field f=o.getClass().getDeclaredField(field);f.setAccessible(true);return f.get(o); }
    private static void set(Object o,String field,Object value) throws Exception
    { Field f=o.getClass().getDeclaredField(field);f.setAccessible(true);f.set(o,value); }
}
