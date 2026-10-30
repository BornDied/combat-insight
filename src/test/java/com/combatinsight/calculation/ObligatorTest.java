package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class ObligatorTest
{
    @Test public void gearscapeBorkThrustFixture()
    {
        int maximum = Obligator.maximum(18, 4, true);
        MultiHitResult result = Obligator.calculate(maximum, 10,
            19688.0 / (2 * 25957), 140, 130, 20, true, false, HitTransform.IDENTITY);
        assertEquals(25, maximum);
        assertEquals(15, result.getTotalMaximumHit());
        assertEquals(1.30745, result.getDamagePerSecond(), .000005);
        assertEquals(1, result.getHitCount());
    }

    @Test public void gearscapeCustomDefenceFixtures()
    {
        double chance = CombatPrediction.hitChance(19688, 576);
        assertEquals(3.99406, Obligator.calculate(18, 0, chance, 0, 0, 0,
            true, false, HitTransform.IDENTITY).getDamagePerSecond(), .000005);
        assertEquals(4.51178, Obligator.calculate(18, 0, chance, 1, 0, 0,
            true, false, HitTransform.IDENTITY).getDamagePerSecond(), .000005);
        assertEquals(4.82232, Obligator.calculate(18, 0, chance, 1, 1, 0,
            true, false, HitTransform.IDENTITY).getDamagePerSecond(), .000005);
    }

    @Test public void smashUsesOneUnboostedRollAndThreeTicks()
    {
        assertEquals(17, Obligator.maximum(17, 5, false));
        MultiHitResult result = Obligator.calculate(17, 10,
            20240.0 / (2 * 25957), 140, 130, 20, false, false, HitTransform.IDENTITY);
        assertEquals(7, result.getTotalMaximumHit());
        assertEquals(.33693, result.getDamagePerSecond(), .000005);
        assertEquals(3, result.getExpectedAttackSpeedTicks(), 0);
    }

    @Test public void sizeBonusIsCappedAndRoundedBeforeRolls()
    {
        assertEquals(18, Obligator.maximum(18, 1, true));
        assertEquals(21, Obligator.maximum(18, 2, true));
        assertEquals(25, Obligator.maximum(18, 3, true));
        assertEquals(25, Obligator.maximum(18, 8, true));
    }

    @Test public void missesArmourAndEncounterTransformsRemainSeparate()
    {
        assertEquals(0, Obligator.calculate(25, 10, 0, 1, 1, 0,
            true, false, HitTransform.IDENTITY).getExpectedDamagePerAttack(), 0);
        assertEquals(0, Obligator.calculate(25, 100, 1, 1, 1, 0,
            true, false, HitTransform.IDENTITY).getExpectedDamagePerAttack(), 0);
        MultiHitResult capped = Obligator.calculate(90, 0, 1, 1, 1, 0,
            true, true, HitTransform.zulrah());
        assertEquals(50, capped.getTotalMaximumHit());
        assertEquals(47.5, capped.getExpectedDamagePerAttack(), 0);
    }
}
