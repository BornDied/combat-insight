package com.combatinsight.live;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;

/** Current-stack bonus for basic attacks, additive with the prayer strength bonus. */
final class SoulreaperRules
{
    private SoulreaperRules() { }

    static int strengthBonus(Client client, String weapon, int boostedStrength)
    {
        if (!weapon.equals("soulreaper axe") && !weapon.equals("soulreaper axe (o)"))
            return 0;
        int stacks = client.getVarpValue(VarPlayerID.SOULREAPER_STACKS);
        if (stacks < 0 || stacks > 5) return 0;
        return Math.max(0, boostedStrength) * stacks * 6 / 100;
    }
}
