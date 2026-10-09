package com.combatinsight.live;
import com.combatinsight.calculation.*;
import net.runelite.api.Client;
import net.runelite.client.game.ItemEquipmentStats;
import java.lang.reflect.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SavedSpellRegressionTest
{
    @Test public void savedSpellDoesNotTurnVoidwakerIntoMagic() throws Exception
    {
        assertEquals(CombatStyle.MELEE, style("voidwaker",5));
    }
    @Test public void savedSpellDoesNotTurnStaffMeleeIntoMagic() throws Exception
    {
        assertEquals(CombatStyle.MELEE, style("ancient staff",15));
    }
    @Test public void poweredStaffStillUsesItsOwnSpell() throws Exception
    {
        assertEquals(CombatStyle.MAGIC, style("tumeken's shadow",35));
    }
    @Test public void voidwakerBaseUsesMeleeStrengthPrayerAndStance() throws Exception
    {
        Class<?> eq=Class.forName(LiveCombatSnapshot.class.getName()+"$EquipmentState");
        Class<?> pr=Class.forName(LiveCombatSnapshot.class.getName()+"$PrayerState");
        Constructor<?> ec=eq.getDeclaredConstructor(), pc=pr.getDeclaredConstructor();ec.setAccessible(true);pc.setAccessible(true);
        Object equipment=ec.newInstance(), prayer=pc.newInstance();
        set(equipment,"weaponNameLower","voidwaker");set(equipment,"meleeStrength",146);
        set(prayer,"meleeStrengthMultiplier",1.23);
        Method method=LiveCombatSnapshot.class.getDeclaredMethod("voidwakerBaseMaximum",eq,pr,int.class,int.class,TargetProfile.class,boolean.class);method.setAccessible(true);
        assertEquals(43,method.invoke(null,equipment,prayer,99,3,TargetDatabase.find(9425),false));
        set(equipment,"magicDamage",100.0f);
        assertEquals(43,method.invoke(null,equipment,prayer,99,3,TargetDatabase.find(9425),false));
    }
    @Test public void savedElementCannotGrantPoweredStavesElementalAccuracy() throws Exception
    {
        Class<?> eq=Class.forName(LiveCombatSnapshot.class.getName()+"$EquipmentState");
        Class<?> st=Class.forName(LiveCombatSnapshot.class.getName()+"$StyleState");
        Constructor<?> ec=eq.getDeclaredConstructor(),sc=st.getDeclaredConstructors()[0];ec.setAccessible(true);sc.setAccessible(true);
        Object equipment=ec.newInstance(),style=sc.newInstance(CombatStyle.MAGIC,AttackType.MAGIC,"Cast",0,0);
        Method method=LiveCombatSnapshot.class.getDeclaredMethod("applyTargetAccuracyModifiers",int.class,st,eq,TargetProfile.class,MagicSpell.class,boolean.class);method.setAccessible(true);
        for(String weapon:new String[]{"tumeken's shadow","trident of the swamp","sanguinesti staff"}) {
            set(equipment,"weaponNameLower",weapon);
            assertEquals(1000,method.invoke(null,1000,style,equipment,TargetDatabase.find(9435),MagicSpell.FIRE_SURGE,false));
        }
        set(equipment,"weaponNameLower","kodai wand");
        assertEquals(1700,method.invoke(null,1000,style,equipment,TargetDatabase.find(9435),MagicSpell.FIRE_SURGE,false));
    }
    @Test public void salamanderBlazeCannotBorrowSavedFireOrDemonbaneExemptions() throws Exception
    {
        Class<?> eq=Class.forName(LiveCombatSnapshot.class.getName()+"$EquipmentState");
        Constructor<?> ec=eq.getDeclaredConstructor();ec.setAccessible(true);Object equipment=ec.newInstance();
        set(equipment,"weaponNameLower","black salamander");set(equipment,"weaponCategory",WeaponCategory.SALAMANDER);
        Method method=eq.getDeclaredMethod("usesSpellbookMagic");method.setAccessible(true);
        boolean spellbook=(boolean)method.invoke(equipment);
        assertFalse(spellbook);
        assertFalse(RaidDamageRules.bypassesIceDemon(CombatStyle.MAGIC,"black salamander",spellbook,spellbook));
    }
    private Object style(String weapon,int magicBonus) throws Exception
    {
        Class<?> equipmentClass=Class.forName(LiveCombatSnapshot.class.getName()+"$EquipmentState");
        Constructor<?> ctor=equipmentClass.getDeclaredConstructor();ctor.setAccessible(true);
        Object equipment=ctor.newInstance();
        set(equipment,"weaponNameLower",weapon);
        set(equipment,"weaponStats",ItemEquipmentStats.builder().amagic(magicBonus).build());
        Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)-> {
            if(m.getName().equals("getEnum")) return Proxy.newProxyInstance(net.runelite.api.EnumComposition.class.getClassLoader(),new Class<?>[]{net.runelite.api.EnumComposition.class},(q,n,b)->n.getName().equals("getIntValue")? 1 : n.getName().equals("getIntVals")?new int[]{1}:null);
            if(m.getName().equals("getStructComposition")) return Proxy.newProxyInstance(net.runelite.api.StructComposition.class.getClassLoader(),new Class<?>[]{net.runelite.api.StructComposition.class},(q,n,b)->"Aggressive");
            if(m.getReturnType()==int.class)return 0;
            return null;
        });
        Method method=LiveCombatSnapshot.class.getDeclaredMethod("getStyleState",Client.class,equipmentClass,MagicSpell.class);method.setAccessible(true);
        Object state=method.invoke(null,client,equipment,MagicSpell.BLOOD_BLITZ);
        Field f=state.getClass().getDeclaredField("combatStyle");f.setAccessible(true);return f.get(state);
    }
    private static void set(Object o,String name,Object value)throws Exception
    {Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,value);}
}
