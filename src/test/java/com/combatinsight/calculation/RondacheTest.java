package com.combatinsight.calculation;

import org.junit.Test;
import static org.junit.Assert.*;

public class RondacheTest
{
    @Test public void gearscapeChargeMaximumFixtures()
    {
        assertEquals(24,Rondache.baseMaximum(20));
        assertEquals(11,Rondache.baseMaximum(10));
        assertEquals(6,HitTransform.IDENTITY.maximum(Rondache.baseMaximum(10),5));
        assertEquals(0,Rondache.baseMaximum(0));
        assertEquals(-1,Rondache.baseMaximum(-1));
    }
}
