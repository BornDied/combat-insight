package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class ZoryaTomeTest
{
    @Test public void gearscapeLevelAndRoundingFixtures()
    {
        int[] levels = {85, 93, 99, 120};
        int[] normal = {20, 24, 26, 34};
        int[] empowered = {32, 38, 41, 54};
        for (int i=0;i<levels.length;i++)
        {
            int max = (int) Math.floor(ZoryaTome.baseMaximum(levels[i]) * 1.15);
            assertEquals(normal[i], max);
            assertEquals(empowered[i], ZoryaTome.empoweredMaximum(max));
        }
    }

    @Test public void specialShowsInitialHitAndConditionalFollowUps()
    {
        SpecialAttackWeapon weapon = SpecialAttackWeapon.forWeapon("Zorya's tome");
        assertNotNull(weapon);
        SpecialAttackResult result = SpecialAttackCalculator.calculate(weapon,26,true,11990,576,
            true,100,false,false,false,false,false,false,0,0,0,-1,false);
        assertArrayEquals(new int[] {41}, result.getMaximumHits());
        assertEquals(CombatPrediction.hitChance(29975,576),result.getHitChance(),1e-12);
        assertTrue(weapon.getOnHitText().contains("3"));
        assertNull(SpecialAttackWeapon.forWeapon("Zorya's tome (uncharged)"));
    }
}
