package com.combatinsight.live;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.NPC;

/** Confirmed room state is tied to actor identity, never a reused NPC index. */
public final class ArchiveStateTracker
{
    private final Set<NPC> smorksDefeated=Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<NPC> ocellusFinalPhase=Collections.newSetFromMap(new IdentityHashMap<>());
    public void onDeath(Client client,NPC npc)
    {
        if(npc==null || npc.getId()!=16641 || client.getNpcs()==null)return;
        for(NPC actor:client.getNpcs())
            if(actor!=null && actor.getId()==16640)smorksDefeated.add(actor);
    }
    public EncounterContext apply(NPC npc,EncounterContext context)
    {
        if(context.ocellusFinalPhase && npc!=null)ocellusFinalPhase.add(npc);
        if(ocellusFinalPhase.contains(npc))context=context.withOcellusFinalPhase();
        return smorksDefeated.contains(npc)?context.withBorkQuartile(3):context;
    }
    public void remove(NPC npc) { smorksDefeated.remove(npc);ocellusFinalPhase.remove(npc); }
    public void clear() { smorksDefeated.clear();ocellusFinalPhase.clear(); }
}
