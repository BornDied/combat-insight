package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class SoulreaperSpecialTest
{
    @Test public void bothVariantsResolveToSpecial()
    {
        assertNotNull(SpecialAttackWeapon.forWeapon("Soulreaper axe"));
        assertSame(SpecialAttackWeapon.forWeapon("Soulreaper axe"), SpecialAttackWeapon.forWeapon("Soulreaper axe (o)"));
    }
    @Test public void fiveStacksUseUnstackedMaximumForBothEndpoints()
    {
        SpecialAttackResult result = calculate(5, 0, true, false, false);
        assertEquals(53, result.getTotalMaximumHit()); // floor(41 * 1.30)
        double chance = CombatPrediction.hitChance(161, 100); // floor(101 * 1.60)
        assertEquals(chance, result.getHitChance(), 1e-12);
        assertEquals(chance * 32.5, result.getExpectedDamage(), 1e-12); // min floor(41 * .30) = 12
    }
    @Test public void oneStackRoundsEachEndpointBeforeFlatArmour()
    {
        SpecialAttackResult result = calculate(1, 3, true, false, false);
        assertEquals(40, result.getTotalMaximumHit());
        assertEquals(CombatPrediction.hitChance(113,100) * DamageRoll.averageSuccessfulHit(2,43,3), result.getExpectedDamage(), 1e-12);
    }
    @Test public void absentStacksAndInvalidStacksAreUnavailable()
    {
        for (int stacks : new int[]{-1,0,6}) assertFalse(calculate(stacks,0,true,false,false).isMaximumAvailable());
    }
    @Test public void missingTargetKeepsMaximumButHidesAccuracy()
    {
        SpecialAttackResult result=calculate(5,0,false,false,false);
        assertTrue(result.isMaximumAvailable()); assertFalse(result.isAccuracyAvailable()); assertFalse(result.isExpectedDamageAvailable());
    }
    @Test public void immuneAndGuaranteedTargetsPreserveEncounterRules()
    {
        assertTrue(calculate(5,0,true,true,false).isTargetImmune());
        SpecialAttackResult result=calculate(5,3,true,false,true);
        assertEquals(50,result.getTotalMaximumHit()); assertEquals(50,result.getExpectedDamage(),0);
    }
    private SpecialAttackResult calculate(int stacks,int armour,boolean target,boolean immune,boolean guaranteed)
    {
        return SoulreaperSpecial.calculate(41,true,101,100,target,armour,immune,guaranteed,guaranteed,stacks);
    }
}
