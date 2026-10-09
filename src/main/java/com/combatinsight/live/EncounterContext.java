package com.combatinsight.live;

import net.runelite.api.NPC;
import net.runelite.api.Client;

/** Evidence belonging to the currently observed actor, never another retained target. */
public final class EncounterContext
{
    public static final EncounterContext UNKNOWN = new EncounterContext(EncounterMode.AUTO, -1, -1, -1);
    final EncounterMode mode;
    final int combatLevel, healthRatio, healthScale, nightmarePillarHp;

    public EncounterContext(EncounterMode mode, int combatLevel, int healthRatio, int healthScale)
    {
        this(mode,combatLevel,healthRatio,healthScale,0);
    }

    EncounterContext(EncounterMode mode, int combatLevel, int healthRatio, int healthScale, int pillarHp)
    {
        this.nightmarePillarHp = pillarHp;
        this.mode = mode == null ? EncounterMode.AUTO : mode;
        this.combatLevel = combatLevel;
        this.healthRatio = healthRatio;
        this.healthScale = healthScale;
    }

    public static EncounterContext from(NPC npc, EncounterMode mode)
    {
        return npc == null ? new EncounterContext(mode, -1, -1, -1)
            : new EncounterContext(mode, npc.getCombatLevel(), npc.getHealthRatio(), npc.getHealthScale());
    }
    /** Encounter evidence is read afresh; never retain another instance's HP variant. */
    public static EncounterContext from(Client client, NPC npc, EncounterMode mode)
    {
        EncounterContext context=from(npc,mode);
        if (npc == null || !EncounterResolver.isNightmarePillar(npc.getId())) return context;
        int hp=0;
        for (NPC actor : client.getNpcs())
        {
            if (actor == null) continue;
            if ("Phosani's Nightmare".equals(actor.getName())) { hp=200; break; }
            if ("The Nightmare".equals(actor.getName())) hp=300;
        }
        return new EncounterContext(context.mode,context.combatLevel,context.healthRatio,context.healthScale,hp);
    }
}
