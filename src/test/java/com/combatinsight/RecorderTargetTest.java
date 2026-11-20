package com.combatinsight;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecorderTargetTest
{
    @Test public void animationUsesActualInteractionAndRetainsInspectorContext()
    {
        NPC retained=npc(16677,20), actual=npc(16678,21);
        AtomicReference<Actor> interaction=new AtomicReference<>(actual);
        EncounterRecorder recorder=new EncounterRecorder();recorder.setEnabled(true);
        recorder.observePlayerAnimation(client(interaction),retained);
        String report=recorder.report();
        assertTrue(report.contains("Tick 100 NPC 16678: local player animation=15123"));
        assertTrue(report.contains("interacting=NPC 16678 index=21"));
        assertTrue(report.contains("retained Inspector target=NPC 16677 index=20"));
        assertTrue(report.contains("equipped weapon=22325"));
        assertTrue(report.contains("game cycle=3000"));
        interaction.set(null);
        recorder.observePlayerAnimation(client(interaction),retained);
        assertTrue(recorder.report().contains("interacting=none; retained Inspector target=NPC 16677 index=20"));
    }

    @Test public void disabledRecorderDoesNotReadLiveState()
    {
        Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},
            (p,m,a)->{throw new AssertionError("Disabled recorder read "+m.getName());});
        new EncounterRecorder().observePlayerAnimation(client,null);
    }

    private static NPC npc(int id,int index)
    {
        return (NPC)Proxy.newProxyInstance(NPC.class.getClassLoader(),new Class<?>[]{NPC.class},
            (p,m,a)->m.getName().equals("getId") ? id : m.getName().equals("getIndex") ? index : zero(m.getReturnType()));
    }

    private static Client client(AtomicReference<Actor> interaction)
    {
        Player player=(Player)Proxy.newProxyInstance(Player.class.getClassLoader(),new Class<?>[]{Player.class},
            (p,m,a)->m.getName().equals("getInteracting") ? interaction.get()
                : m.getName().equals("getAnimation") ? 15123 : zero(m.getReturnType()));
        Item[] items=new Item[14];items[3]=new Item(22325,1);
        ItemContainer equipment=(ItemContainer)Proxy.newProxyInstance(ItemContainer.class.getClassLoader(),new Class<?>[]{ItemContainer.class},
            (p,m,a)->m.getName().equals("getItems") ? items : zero(m.getReturnType()));
        return (Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)->{
            switch(m.getName()) {
                case "getLocalPlayer":return player;
                case "getTickCount":return 100;
                case "getGameCycle":return 3000;
                case "getItemContainer":return equipment;
                default:return zero(m.getReturnType());
            }
        });
    }
    private static Object zero(Class<?> type)
    { return type==int.class ? 0 : type==boolean.class ? false : null; }
}
