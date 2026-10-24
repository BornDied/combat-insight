package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class AscensionBoltTest
{
    private BoltEffect.Result result(String gem, double chance, boolean diary, boolean undead)
    {
        return BoltEffect.calculate(gem + " ascension bolts (e)",20,99,chance,500,0,
            false,false,diary,undead,false);
    }
    @Test public void gearscapeDiamondHighAndLowAccuracy()
    {
        double high = CombatPrediction.hitChance(10486,576);
        double low = CombatPrediction.hitChance(10486,19776);
        assertEquals(8.22344,result("diamond",high,true,false).expected/1.2,.000005);
        assertEquals(2.58965,result("diamond",low,false,false).expected/1.2,.000005);
        assertEquals(23,result("diamond",low,false,false).maximum);
        assertTrue(result("diamond",low,false,false).chance > low);
    }
    @Test public void gearscapeOnyxWithAndWithoutDiary()
    {
        double low = CombatPrediction.hitChance(10486,19776);
        assertEquals(2.24394,result("onyx",low,false,false).expected/1.2,.000005);
        assertEquals(2.24637,result("onyx",low,true,false).expected/1.2,.000005);
        assertEquals(24,result("onyx",low,false,false).maximum);
        assertEquals(low,result("onyx",low,false,false).chance,0);
        assertEquals(20,result("onyx",low,false,true).maximum);
    }
}
