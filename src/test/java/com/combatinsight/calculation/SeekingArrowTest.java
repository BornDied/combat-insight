package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class SeekingArrowTest
{
    @Test public void clampsLowAccurateRollsWithoutRerollingOrIncreasingNormalMaximum()
    {
        HitTransform seeking = HitTransform.IDENTITY.withAccurateFloor(3);
        // 0..5 becomes 3,3,3,3,4,5, not a uniform 3..5 distribution.
        assertEquals(3.5, seeking.average(0,5,0), 1e-12);
        assertEquals(5, seeking.maximum(5,0));
        assertEquals(3, seeking.maximum(1,0));
        assertEquals(3.0, seeking.average(0,0,0), 1e-12);
    }
    @Test public void floorPrecedesArmourAndRaidReduction()
    {
        assertEquals(2.5, HitTransform.IDENTITY.withAccurateFloor(3).average(0,5,1), 1e-12);
        assertEquals(8.0/7, HitTransform.division(3,0).withAccurateFloor(3).average(0,6,0), 1e-12);
        assertEquals(0, HitTransform.IDENTITY.withAccurateFloor(3).maximum(2,4));
    }
    @Test public void darkBowAppliesFloorPerAccurateArrowAndLeavesMissesAlone()
    {
        MultiHitResult result = MultiHitWeapon.DARK_BOW.calculate(0,5,0,.5,1,8,false,false,
            HitTransform.IDENTITY.withAccurateFloor(3));
        assertEquals(3.5, result.getExpectedDamagePerAttack(), 1e-12);
        assertEquals(10, result.getTotalMaximumHit());
        assertEquals(0, MultiHitWeapon.DARK_BOW.calculate(0,5,0,0,1,8,false,false,
            HitTransform.IDENTITY.withAccurateFloor(3)).getExpectedDamagePerAttack(), 1e-12);
    }
    @Test public void arrowSpecialsUseClampedDistribution()
    {
        SpecialAttackResult result = SpecialAttackCalculator.calculate(SpecialAttackWeapon.MAGIC_LONGBOW,
            5,true,10000,100,true,0,false,false,false,true,false,true,50,50,0,5,false,true);
        assertEquals(3.5,result.getExpectedDamage(),1e-12);
        assertEquals(5,result.getTotalMaximumHit());
        SpecialAttackResult scorching = ContextSpecialAttack.calculate(SpecialAttackWeapon.SCORCHING_BOW,
            5,10000,100,1,0,false,false,true,false,true);
        assertEquals(3.5 * CombatPrediction.hitChance(10000,100),scorching.getExpectedDamage(),1e-12);
    }
}
