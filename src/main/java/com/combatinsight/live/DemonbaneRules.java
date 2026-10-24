package com.combatinsight.live;

/** Target vulnerability scales the demonbane bonus, not total damage/accuracy. */
final class DemonbaneRules
{
    private DemonbaneRules() { }
    static int bonusPercent(int id, int ordinaryBonus)
    {
        int vulnerability;
        switch (id)
        {
            case 12191: case 12195: vulnerability = 70; break;
            case 7584: case 7585: vulnerability = 115; break;
            case 16665: case 16666: vulnerability = 25; break;
            case 16674: case 16677: case 16684: vulnerability = 80; break;
            default: vulnerability = 100;
        }
        // Wiki BaseCalc.trackFactor truncates this percentage before applying it.
        return ordinaryBonus * vulnerability / 100;
    }
}
