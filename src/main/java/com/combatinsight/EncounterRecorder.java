package com.combatinsight;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;

/** Bounded local testing timeline. No external I/O or gameplay actions. */
final class EncounterRecorder
{
    private final Deque<String> entries=new ArrayDeque<>();
    private final Map<NPC,String> previousActors=new IdentityHashMap<>();
    private int characters, dropped;
    private boolean enabled;
    private String previous="";
    synchronized void setEnabled(boolean next)
    {
        if(next && !enabled) { entries.clear();characters=0;dropped=0;previous=""; }
        if(next!=enabled)previousActors.clear();
        enabled=next;
    }
    /** Observe room actors even if no combat target or health bar is selected. */
    synchronized void observeRoom(int tick,Iterable<NPC> actors)
    {
        if(!enabled || actors==null)return;
        Set<NPC> present=Collections.newSetFromMap(new IdentityHashMap<>());
        for(NPC actor:actors)
        {
            if(actor==null || !isArchive(actor.getId()))continue;
            present.add(actor);
            NPCComposition definition=actor.getTransformedComposition();
            if(definition==null)definition=actor.getComposition();
            String cues="NPC "+actor.getId()+" index="+actor.getIndex()
                +"; animation="+actor.getAnimation()+"; pose="+actor.getPoseAnimation()
                +"; health ratio/scale="+actor.getHealthRatio()+"/"+actor.getHealthScale()
                +"; combat="+actor.getCombatLevel()+"; size="+(definition==null?0:definition.getSize())
                +"; dead="+actor.isDead()
                +"; overhead archives="+Arrays.toString(actor.getOverheadArchiveIds())
                +"; overhead sprites="+Arrays.toString(actor.getOverheadSpriteIds());
            if(!cues.equals(previousActors.put(actor,cues)))
                append("Tick "+tick+" room observation\n"+cues);
        }
        previousActors.keySet().retainAll(present);
    }
    synchronized void snapshot(int tick,int targetId,String report,String cues)
    {
        if(!enabled || !isArchive(targetId))return;
        String body="NPC "+targetId+"\n"+report+"\nObserved cues: "+cues;
        if(body.equals(previous))return;
        previous=body; append("Tick "+tick+" snapshot\n"+body);
    }
    synchronized void event(int tick,int targetId,String event)
    {
        if(enabled && isArchive(targetId))
            append("Tick "+tick+" NPC "+targetId+": "+event);
    }
    private static boolean isArchive(int id) { return id>=16640 && id<=16688; }
    private void append(String text)
    {
        if(text.length()>8000000) { dropped++;return; }
        entries.addLast(text);characters+=text.length();
        while(entries.size()>12000 || characters>8000000)
        { characters-=entries.removeFirst().length();dropped++; }
    }
    synchronized String report()
    {
        return "Combat Insight "+BuildInfo.VERSION+" Archive encounter recording\nRecording: "+enabled
            +"\nCompleteness: unverified; captures only observed events while enabled. Enable before pulling; stop after kill/wipe. Actor changes do not end recording."
            +"\nDropped entries: "+dropped+"\nNo automatic fight/phase boundary certification; multiple attempts may be present.\n\n"
            +"Scope: observed Archive actors, including Bork and Smorks, even without a selected target."
            +"\nLimits: 12,000 entries / 8,000,000 characters; oldest entries are removed at the limit.\n\n"
            +(entries.isEmpty()?"No Archive events captured. Recording must be enabled while Archive NPCs are loaded.":String.join("\n\n",entries));
    }
}
