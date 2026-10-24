package com.combatinsight.calculation;

/** Tome modifiers, with damage enhancement applied after the normal maximum. */
public final class ZoryaTome
{
    private ZoryaTome() { }
    public static int baseMaximum(int magicLevel) { return Math.max(1, magicLevel / 3 - 10); }
    public static int empoweredMaximum(int normalMaximum) { return normalMaximum * 8 / 5; }
    public static int empoweredAccuracy(int normalRoll) { return (int) ((long) normalRoll * 5 / 2); }
}
