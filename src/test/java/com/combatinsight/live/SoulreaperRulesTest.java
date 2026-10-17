package com.combatinsight.live;

import java.lang.reflect.*;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;
import org.junit.Test;
import static org.junit.Assert.*;

public class SoulreaperRulesTest
{
    @Test public void bothAxesReadCurrentStacksAndRoundBoostedStrengthBonus() throws Exception
    {
        int[] expected={0,7,14,21,28,35};
        for(String name:new String[]{"soulreaper axe","soulreaper axe (o)"})
            for(int stacks=0;stacks<=5;stacks++) assertEquals(expected[stacks],bonus(name,118,stacks));
    }
    @Test public void otherWeaponsNeverInheritRetainedStacks() throws Exception
    {assertEquals(0,bonus("dragon axe (or)",118,5));}
    @Test public void invalidStacksDoNotBecomeAnInventedDamageBoost() throws Exception
    {assertEquals(0,bonus("soulreaper axe",118,-1));assertEquals(0,bonus("soulreaper axe",118,6));}
    private static int bonus(String weapon,int strength,int stacks)throws Exception
    {
        Class<?> rules;
        try {rules=Class.forName("com.combatinsight.live.SoulreaperRules");}
        catch(ClassNotFoundException e){throw new AssertionError("Soulreaper stack support is missing");}
        Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)-> {
            if(m.getName().equals("getVarpValue")) {assertEquals(VarPlayerID.SOULREAPER_STACKS,a[0]);return stacks;}
            if(m.getReturnType()==int.class)return 0;
            if(m.getReturnType()==boolean.class)return false;
            return null;
        });
        Method method=rules.getDeclaredMethod("strengthBonus",Client.class,String.class,int.class);method.setAccessible(true);
        return (int)method.invoke(null,client,weapon,strength);
    }
}
