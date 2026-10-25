package com.combatinsight.live;

import com.combatinsight.calculation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import net.runelite.api.*;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Exercises the equipped-item reader, prayer detection and actual snapshot together. */
public class LiveEquipmentCaptureTest
{
    @Test public void rapidBlowpipeUsesInternalDartsAndLightDefenceAtArchiveTargets() throws Exception
    {
        for(int target:new int[]{16641,16684,16677})
        {
            LiveCombatSnapshot s=blowpipe(12926,BlowpipeDart.DRAGON,1,target,MagicSpell.FIRE_SURGE);
            assertTrue(s.isDpsAvailable());assertEquals(AttackType.RANGED_LIGHT,s.getAttackType());
            assertEquals(65,strength(s));assertEquals(2,s.getAttackSpeedTicks());
            // 99 Ranged, Rigour, 30 blowpipe strength + 35 dragon darts => max 26.
            int armour=target==16641?2:0;
            assertEquals(Integer.toString(26-armour),s.getMaxHitText());
            int roll=target==16641?32976:target==16684?33831:81326;
            double chance=15624.0/(2*(roll+1));
            double mean=target==16641?300.0/27:352.0/27;
            assertEquals(String.format(Locale.ROOT,"%.2f",chance*mean/1.2),s.getDpsText());
        }
    }
    @Test public void blowpipeOrnamentAndSavedSpellCannotChangeDartsOrAttackSpeed() throws Exception
    {
        LiveCombatSnapshot base=blowpipe(12926,BlowpipeDart.AMETHYST,1,16684,MagicSpell.NOT_SET);
        LiveCombatSnapshot ornament=blowpipe(28688,BlowpipeDart.AMETHYST,1,16684,MagicSpell.FIRE_SURGE);
        assertEquals(base.getMaxHitText(),ornament.getMaxHitText());assertEquals(base.getDpsText(),ornament.getDpsText());
        assertEquals(58,strength(ornament));
        assertEquals(3,blowpipe(12926,BlowpipeDart.DRAGON,0,16684,MagicSpell.NOT_SET).getAttackSpeedTicks());
        assertFalse(blowpipe(12926,BlowpipeDart.NOT_SET,1,16684,MagicSpell.FIRE_SURGE).isDpsAvailable());
    }
    @Test public void dousedXarthCorpseReceivesGuaranteedMaximumFromLightRanged() throws Exception
    {
        LiveCombatSnapshot normal=blowpipe(12926,BlowpipeDart.DRAGON,1,16687,MagicSpell.NOT_SET);
        LiveCombatSnapshot doused=blowpipe(12926,BlowpipeDart.DRAGON,1,16688,MagicSpell.NOT_SET);
        assertNotEquals("100.0%",normal.getHitChanceText());
        assertEquals("100.0%",doused.getHitChanceText());assertEquals("26",doused.getMaxHitText());
        assertEquals("21.67",doused.getDpsText());
    }
    @Test public void twistedBowOnStandingBorkUsesKnownSmorksQuartile() throws Exception
    {
        EncounterContext context=EncounterContext.UNKNOWN.withBorkQuartile(0);
        LiveCombatSnapshot s=equipped(20997,"Twisted bow",ItemEquipmentStats.builder().slot(3).arange(70).rstr(20).aspeed(6).build(),
            BlowpipeDart.NOT_SET,1,16640,MagicSpell.FIRE_SURGE,context);
        assertTrue(s.isDpsAvailable());assertEquals("28",s.getMaxHitText());assertEquals(5,s.getAttackSpeedTicks());
        assertEquals("15,026 / 35,226",s.getRollText());
        double expected=15026.0/(2*35227)*406/37/3;
        assertEquals(String.format(Locale.ROOT,"%.2f",expected),s.getDpsText());
    }

    private static LiveCombatSnapshot blowpipe(int id,BlowpipeDart dart,int mode,int target,MagicSpell saved) throws Exception
    { return equipped(id,"Toxic blowpipe",ItemEquipmentStats.builder().slot(3).arange(60).rstr(30).aspeed(3).build(),dart,mode,target,saved,EncounterContext.UNKNOWN); }
    static LiveCombatSnapshot equipped(int id,String name,ItemEquipmentStats weaponStats,BlowpipeDart dart,int mode,int target,MagicSpell saved,EncounterContext context) throws Exception
    { return equipped(id,name,weaponStats,dart,mode,target,saved,context,CombatStyleOverride.RANGED); }
    static LiveCombatSnapshot equipped(int id,String name,ItemEquipmentStats weaponStats,BlowpipeDart dart,int mode,int target,MagicSpell saved,EncounterContext context,CombatStyleOverride forced) throws Exception
    { return equipped(id,name,weaponStats,dart,mode,target,saved,context,forced,11212,"Dragon arrow",60); }
    static LiveCombatSnapshot equipped(int id,String name,ItemEquipmentStats weaponStats,BlowpipeDart dart,int mode,int target,MagicSpell saved,EncounterContext context,CombatStyleOverride forced,int ammoId,String ammoName,int ammoStrength) throws Exception
    {
        Map<Integer,String> names=new HashMap<>();names.put(id,name);names.put(ammoId,ammoName);
        Map<Integer,ItemStats> stats=new HashMap<>();
        stats.put(id,new ItemStats(true,0,0,weaponStats));
        stats.put(ammoId,new ItemStats(true,0,0,ItemEquipmentStats.builder().slot(13).rstr(ammoStrength).build()));
        Item[] worn=new Item[14];worn[3]=new Item(id,1);worn[13]=new Item(ammoId,1000);
        Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)->{
            switch(m.getName()) {
                case "getGameState":return GameState.LOGGED_IN;
                case "getBoostedSkillLevel":case "getRealSkillLevel":return 99;
                case "isPrayerActive":return a[0]==(forced==CombatStyleOverride.MELEE?Prayer.PIETY:forced==CombatStyleOverride.MAGIC?Prayer.AUGURY:Prayer.RIGOUR);
                case "getVarpValue":return (int)a[0]==43?mode:0;
                case "getItemContainer":return Proxy.newProxyInstance(ItemContainer.class.getClassLoader(),new Class<?>[]{ItemContainer.class},
                    (q,n,b)->n.getName().equals("getItems")?worn:zero(n.getReturnType()));
                case "getItemDefinition":int item=(int)a[0];return Proxy.newProxyInstance(ItemComposition.class.getClassLoader(),new Class<?>[]{ItemComposition.class},
                    (q,n,b)->n.getName().equals("getName")?names.get(item):n.getName().equals("getId")?item:
                    n.getName().equals("getNote")||n.getName().equals("getPlaceholderTemplateId")?-1:zero(n.getReturnType()));
                default:return zero(m.getReturnType());
            }
        });
        Constructor<?> ctor=ItemManager.class.getDeclaredConstructors()[0];ctor.setAccessible(true);
        ScheduledExecutorService idle=(ScheduledExecutorService)Proxy.newProxyInstance(ScheduledExecutorService.class.getClassLoader(),
            new Class<?>[]{ScheduledExecutorService.class},(p,m,a)->zero(m.getReturnType()));
        RuneLiteConfig config=(RuneLiteConfig)Proxy.newProxyInstance(RuneLiteConfig.class.getClassLoader(),new Class<?>[]{RuneLiteConfig.class},(p,m,a)->zero(m.getReturnType()));
        ItemManager manager=(ItemManager)ctor.newInstance(client,idle,null,new EventBus(),null,config);
        Field map=ItemManager.class.getDeclaredField("itemStats");map.setAccessible(true);map.set(manager,stats);
        return LiveCombatSnapshot.capture(client,manager,forced,dart,saved,false,0,target,"Archive target",-1,-1,
            TargetEffectSnapshot.empty(),false,null,RaidScaling.DEFAULT,context,false,DefenceTrackingMode.LOCAL);
    }
    private static int strength(LiveCombatSnapshot s) throws Exception
    { Field f=LiveCombatSnapshot.class.getDeclaredField("rangedStrengthBonus");f.setAccessible(true);return f.getInt(s); }
    private static Object zero(Class<?> type)
    {if(type==boolean.class)return false;if(type==int.class)return 0;if(type==long.class)return 0L;if(type==double.class)return 0.0;return null;}
}
