package com.combatinsight.live;

/**
 * Conservative local roster observation, not a guarantee of starting party size.
 * RuneLite Varbits documents ToB status 2/3 as inside/spectating and orb healths
 * 1..27 as present, 30 as dead, 0 as hidden. Retain the largest observed roster
 * until leaving the raid; deaths/hidden orbs must not shrink encounter scaling.
 */
public final class TheatrePartyTracker
{
    private int largestObserved;

    public int observe(boolean enabled, int status, int[] orbs)
    {
        if (!enabled || (status != 2 && status != 3))
        {
            clear();
            return 0;
        }
        if (orbs == null || orbs.length != 5) return largestObserved;
        int present = 0;
        for (int value : orbs)
        {
            if (value == 0) continue;
            if ((value < 1 || value > 27) && value != 30) return largestObserved;
            present++;
        }
        largestObserved = Math.max(largestObserved, present);
        return largestObserved;
    }

    public void clear() { largestObserved = 0; }
}
