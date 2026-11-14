package com.combatinsight;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;

/** Bounded local testing timeline. No external I/O or gameplay actions. */
final class EncounterRecorder
{
    private final Deque<String> entries=new ArrayDeque<>();
    private final Map<NPC,String> previousActors=new IdentityHashMap<>();
    private final Map<String,String> roomFirst=new LinkedHashMap<>(), roomLast=new LinkedHashMap<>();
    private int characters, dropped, lastRoomTick = -1;
    private boolean enabled;
    private String previous="";
    synchronized void setEnabled(boolean next)
    {
        if(next && !enabled) { entries.clear();roomFirst.clear();roomLast.clear();characters=0;dropped=0;lastRoomTick=-1;previous=""; }
        if(next!=enabled)previousActors.clear();
        enabled=next;
    }
    /** Observe room actors even if no combat target or health bar is selected. */
    synchronized void observeRoom(int tick,Iterable<NPC> actors)
    {
        if(!enabled || actors==null)return;
        if(lastRoomTick>=0 && (tick>lastRoomTick+1 || tick<lastRoomTick))
            marker(tick,"Room observation gap after tick "+lastRoomTick);
        lastRoomTick=tick;
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
            {
                remember(room(actor.getId()),"Tick "+tick+" "+cues);
                append("Tick "+tick+" room observation\n"+cues);
            }
        }
        previousActors.keySet().retainAll(present);
    }
    synchronized void snapshot(int tick,int targetId,String report,String cues)
    {
        if(!enabled || !isArchive(targetId))return;
        String body="NPC "+targetId+"\n"+report+"\nObserved cues: "+cues;
        if(body.equals(previous))return;
        remember(room(targetId),"Tick "+tick+" "+body);
        previous=body; append("Tick "+tick+" snapshot\n"+body);
    }
    synchronized void event(int tick,int targetId,String event)
    {
        if(enabled && isArchive(targetId))
            append("Tick "+tick+" NPC "+targetId+": "+event);
    }
    synchronized void marker(int tick,String event) { if(enabled) append("Tick "+tick+" marker: "+event); }
    private static String room(int id)
    {
        if(id==16659 || id==16660 || id==16662) return "1 Guardian";
        if(id==16640 || id==16641 || id==16652 || id==16653) return "2 Bork or Ezekiel";
        if(id==16665 || id==16666 || id==16667 || id==16670) return "3 Moia or Dreadnoth";
        if(id>=16643 && id<=16650 || id==16684 || id==16687 || id==16688) return "4 Dagon'hai or Xarth";
        if(id>=16674 && id<=16681) return "5 Ocellus";
        return null;
    }
    private void remember(String room,String observation)
    {
        if(room==null)return;
        roomFirst.putIfAbsent(room,observation);
        roomLast.put(room,observation);
    }
    private String summaries()
    {
        StringBuilder out=new StringBuilder("Room summaries (first and latest actor observations or selected-target snapshots; not proof of completion):\n");
        for(String room:roomFirst.keySet()) out.append("\n").append(room).append("\nFirst: ").append(roomFirst.get(room))
            .append("\nLatest: ").append(roomLast.get(room)).append("\n");
        return out.toString();
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
            +summaries()+"\nTimeline:\n"
            +(entries.isEmpty()?"No Archive events captured. Recording must be enabled while Archive NPCs are loaded.":String.join("\n\n",entries));
    }
}
