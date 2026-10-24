package com.combatinsight.calculation;

/** Charge-only maximum; target mitigation and shield accuracy are separate inputs. */
public final class Rondache
{
    private Rondache() { }
    public static int baseMaximum(int charges)
    {
        if (charges < 0 || charges > 20) return -1;
        return charges * (100 + charges) / 100;
    }
}
