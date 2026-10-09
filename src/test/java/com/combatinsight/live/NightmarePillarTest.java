package com.combatinsight.live;
import com.combatinsight.calculation.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class NightmarePillarTest
{
    @Test public void allFourPillarsHaveResolvedCombatStatsEvenWithoutHpContext()
    {
        for(int id:new int[]{9435,9438,9441,9444}) {
            TargetProfile p=EncounterResolver.resolve(id,EncounterContext.UNKNOWN);
            assertFalse(p.isAmbiguous());
            assertEquals(0,p.getDefenceLevel());
            assertEquals(1,p.getMagicLevel());
            assertEquals(0,p.getHitpoints()); // unknown HP, not a guessed PNM variant
        }
    }
    @Test public void pillarMagicDoublesEveryDamageRollButMeleeDoesNot()
    {
        for(int id:new int[]{9435,9438,9441,9444}) {
            HitTransform magic=RaidDamageRules.transform(id,AttackType.MAGIC,false,"tumeken's shadow",99);
            assertEquals(100,magic.maximum(50,0));
            assertEquals(2*HitTransform.IDENTITY.average(0,50,0),magic.average(0,50,0),1e-12);
            assertEquals(50,RaidDamageRules.transform(id,AttackType.SLASH,false,"voidwaker",99).maximum(50,0));
        }
        assertEquals(50,RaidDamageRules.transform(9425,AttackType.MAGIC,false,"tumeken's shadow",99).maximum(50,0));
    }
    @Test public void currentBossEvidenceSelectsHpAndDoesNotPersistAcrossInstances()
    {
        net.runelite.api.NPC pillar=npc(9435,"Totem");
        java.util.List<net.runelite.api.NPC> actors=new java.util.ArrayList<>();
        net.runelite.api.Client client=(net.runelite.api.Client)java.lang.reflect.Proxy.newProxyInstance(
            net.runelite.api.Client.class.getClassLoader(),new Class<?>[]{net.runelite.api.Client.class},
            (p,m,a)->m.getName().equals("getNpcs")?actors:null);
        actors.add(npc(9425,"Phosani's Nightmare"));
        assertEquals(200,EncounterResolver.resolve(9435,EncounterContext.from(client,pillar,EncounterMode.AUTO)).getHitpoints());
        actors.clear();actors.add(npc(9416,"The Nightmare"));
        assertEquals(300,EncounterResolver.resolve(9435,EncounterContext.from(client,pillar,EncounterMode.AUTO)).getHitpoints());
        actors.clear();
        assertEquals(0,EncounterResolver.resolve(9435,EncounterContext.from(client,pillar,EncounterMode.AUTO)).getHitpoints());
    }
    private static net.runelite.api.NPC npc(int id,String name)
    {
        return (net.runelite.api.NPC)java.lang.reflect.Proxy.newProxyInstance(net.runelite.api.NPC.class.getClassLoader(),
            new Class<?>[]{net.runelite.api.NPC.class},(p,m,a)-> {
                if(m.getName().equals("getId"))return id;
                if(m.getName().equals("getName"))return name;
                if(m.getReturnType()==int.class)return 0;
                return null;
            });
    }
}
