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
    private final Set<NPC> ezekielEnraged=Collections.newSetFromMap(new IdentityHashMap<>());
    private int maximumPartySize;
    private boolean impBuffUncertain;
    void observeImpBuff(boolean trioPresent,boolean graphicPresent)
    {
        // The short graphic is an application signal, not a persistent buff indicator.
        // Keep the uncertainty for this room; consumption cannot yet be verified.
        if(!trioPresent)impBuffUncertain=false;
        else if(graphicPresent)impBuffUncertain=true;
    }
    public void observeRoom(Client client)
    {
        if(client.getNpcs()==null)return;
        boolean trio=false;
        for(NPC actor:client.getNpcs())if(actor!=null && actor.getId()>=16643 && actor.getId()<=16648)trio=true;
        net.runelite.api.Player player=client.getLocalPlayer();
        observeImpBuff(trio,player!=null && player.hasSpotAnim(4162));
    }
    public void clearActors() { smorksDefeated.clear();ocellusFinalPhase.clear();ezekielEnraged.clear(); }

    public void onDeath(Client client,NPC npc)
    {
        if(npc==null || npc.getId()!=16641 || client.getNpcs()==null)return;
        for(NPC actor:client.getNpcs())
            if(actor!=null && actor.getId()==16640)smorksDefeated.add(actor);
    }
    public EncounterContext apply(NPC npc,EncounterContext context)
    {
        if(context.archive.partyStatus==0)maximumPartySize=0;
        if(npc!=null && ArchiveEncounterRules.isArchive(npc.getId()) && context.archive.partySize>0)maximumPartySize=Math.max(maximumPartySize,context.archive.partySize);
        context=context.withArchiveState(context.archive.withPartySize(maximumPartySize));
        if(impBuffUncertain)context=context.withArchiveState(context.archive.withImpBuffUncertain());
        if(context.archive.ezekielEnraged && npc!=null)ezekielEnraged.add(npc);
        if(ezekielEnraged.contains(npc))context=context.withArchiveState(context.archive.withEzekielEnrage());
        if(context.ocellusFinalPhase && npc!=null)ocellusFinalPhase.add(npc);
        if(ocellusFinalPhase.contains(npc))context=context.withOcellusFinalPhase();
        return smorksDefeated.contains(npc)?context.withBorkQuartile(3):context;
    }
    public void remove(NPC npc) { smorksDefeated.remove(npc);ocellusFinalPhase.remove(npc);ezekielEnraged.remove(npc); }
    public void clear() { clearActors();maximumPartySize=0;impBuffUncertain=false; }
}
