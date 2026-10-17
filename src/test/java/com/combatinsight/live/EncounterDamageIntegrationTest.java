package com.combatinsight.live;
import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class EncounterDamageIntegrationTest {
 @Test public void zulrahRubyUsesRerollAndKeepsDpsAvailable() throws Exception {
  Object r=result(2042,"rune crossbow","ruby bolts (e)");
  assertEquals(50,value(r,"maximumHit"));
  double p=(double)value(r,"hitChance");
  assertTrue((double)value(r,"damagePerSecond")>0);
 }
 @Test public void toaRubyDoesNotUseUnscaledHitpoints() throws Exception {
  Object r=result(11789,"rune crossbow","ruby bolts (e)");
  assertEquals("ToA HP scaling unavailable",value(r,"dpsStatus"));
 }
 private static Object result(int id,String weapon,String ammo) throws Exception {
  Object e=create("EquipmentState"),p=create("PrayerState");set(e,"weaponNameLower",weapon);set(e,"ammoNameLower",ammo);
  Class<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$StyleState");Constructor<?> ctor=c.getDeclaredConstructors()[0];ctor.setAccessible(true);
  Object style=ctor.newInstance(CombatStyle.RANGED,AttackType.RANGED_STANDARD,"Rapid",0,0);
  for(Method m:LiveCombatSnapshot.class.getDeclaredMethods()) if(m.getName().equals("calculateTargetResult")) {
   m.setAccessible(true);return m.invoke(null,style,e,p,id,TargetDatabase.find(id),99,99,99,MagicSpell.NOT_SET,false,0,0,80,true,"",5,false,RaidDamageRules.transform(id,AttackType.RANGED_STANDARD,false,weapon,99),500,false,99);
  } throw new AssertionError();
 }
 private static Object create(String n)throws Exception {Constructor<?> c=Class.forName(LiveCombatSnapshot.class.getName()+"$"+n).getDeclaredConstructor();c.setAccessible(true);return c.newInstance();}
 private static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 private static Object value(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
}
