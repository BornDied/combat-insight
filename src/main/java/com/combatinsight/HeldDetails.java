package com.combatinsight;

import com.combatinsight.calculation.HudDisplayMode;

/** Temporary rendering state; never writes the user's configured mode or row toggles. */
final class HeldDetails
{
    private volatile boolean held;
    void press() { held = true; }
    void clear() { held = false; }
    HudDisplayMode resolve(HudDisplayMode configured)
    {
        return held ? HudDisplayMode.ADVANCED : configured;
    }
}
