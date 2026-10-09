package com.combatinsight;

import com.combatinsight.calculation.TargetDefenceDisplay;
import com.combatinsight.calculation.TargetMagicDisplay;
import com.combatinsight.live.CombatDummy;
import java.awt.Color;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombatInsightPluginTest
{
	@Test
	public void onlyOffensiveNpcOptionsConfirmCombat()
	{
		assertTrue(CombatInsightPlugin.isCombatMenuOption("Attack"));
		assertTrue(CombatInsightPlugin.isCombatMenuOption("cast"));
		assertFalse(CombatInsightPlugin.isCombatMenuOption("Talk-to"));
		assertFalse(CombatInsightPlugin.isCombatMenuOption("Pickpocket"));
		assertFalse(CombatInsightPlugin.isCombatMenuOption("Trade"));
		assertFalse(CombatInsightPlugin.isCombatMenuOption(null));
	}

	@Test
	public void maxSplitDefaultsToVisible()
	{
		assertTrue(new CombatInsightConfig() { }.showMaxSplit());
	}

	@Test
	public void specialAttackRowsDefaultToVisible()
	{
		CombatInsightConfig config = new CombatInsightConfig() { };
		assertTrue(config.showSpecMax());
		assertTrue(config.showSpecChance());
		assertTrue(config.showExpectedSpec());
		assertTrue(config.showSpecOnHit());
		assertTrue(config.targetDefenceDisplay().showsInfoBox());
		assertFalse(config.targetDefenceDisplay().showsHudRow());
		assertTrue(TargetDefenceDisplay.BOTH.showsInfoBox());
		assertTrue(TargetDefenceDisplay.BOTH.showsHudRow());
		assertTrue(config.targetMagicDisplay().showsInfoBox());
		assertFalse(config.targetMagicDisplay().showsHudRows());
		assertTrue(TargetMagicDisplay.BOTH.showsInfoBox());
		assertTrue(TargetMagicDisplay.BOTH.showsHudRows());
		assertEquals(new Color(255, 152, 31), config.targetDefenceTextColor());
		assertEquals(new Color(100, 235, 130), config.targetDefenceFlashColor());
		assertEquals(new Color(255, 220, 80), config.targetMagicTextColor());
		assertEquals(new Color(100, 235, 130), config.targetMagicFlashColor());
	}

	@Test
	public void resolvesDummyNpcsBeforeObjectFallbacks()
	{
		assertEquals(CombatDummy.UNDEAD,
			CombatInsightPlugin.resolveCombatDummy(7413, 9357));
		assertEquals(CombatDummy.ORNATE_KALPHITE,
			CombatInsightPlugin.resolveCombatDummy(-1, 9357));
		assertEquals(CombatDummy.ORNATE_VAMPYRE,
			CombatInsightPlugin.resolveCombatDummy(10511, -1));
		assertEquals(null, CombatInsightPlugin.resolveCombatDummy(-1, -1));
	}

	@Test
	public void dummyTargetsHideDefenceBasedRows()
	{
		assertFalse(CombatInsightOverlay.shouldShowDefenceBasedRow(true, true));
		assertFalse(CombatInsightOverlay.shouldShowDefenceBasedRow(false, false));
		assertTrue(CombatInsightOverlay.shouldShowDefenceBasedRow(true, false));
	}

    @Test public void sceneLoadingKeepsObservedTheatreRosterButLogoutClearsIt() throws Exception
    {
        CombatInsightPlugin plugin = new CombatInsightPlugin();
        java.lang.reflect.Field field = CombatInsightPlugin.class.getDeclaredField("theatrePartyTracker");
        field.setAccessible(true);
        com.combatinsight.live.TheatrePartyTracker tracker = (com.combatinsight.live.TheatrePartyTracker) field.get(plugin);
        tracker.observe(true,2,new int[]{27,27,27,27,27});
        net.runelite.api.events.GameStateChanged event = new net.runelite.api.events.GameStateChanged();
        event.setGameState(net.runelite.api.GameState.LOADING);
        plugin.onGameStateChanged(event);
        assertEquals(5,tracker.observe(true,2,new int[]{27,27,27,0,0}));
        event.setGameState(net.runelite.api.GameState.LOGIN_SCREEN);
        plugin.onGameStateChanged(event);
        assertEquals(0,tracker.observe(true,2,new int[5]));
    }

    @Test public void refreshDuringLoadingDoesNotConsumeUnavailableRaidVarbits() throws Exception
    {
        CombatInsightPlugin plugin = new CombatInsightPlugin();
        java.lang.reflect.Field trackerField = CombatInsightPlugin.class.getDeclaredField("theatrePartyTracker");
        trackerField.setAccessible(true);
        com.combatinsight.live.TheatrePartyTracker tracker = (com.combatinsight.live.TheatrePartyTracker) trackerField.get(plugin);
        tracker.observe(true,2,new int[]{27,27,27,27,27});
        net.runelite.api.Client client = (net.runelite.api.Client)java.lang.reflect.Proxy.newProxyInstance(
            net.runelite.api.Client.class.getClassLoader(),new Class<?>[]{net.runelite.api.Client.class},(p,m,a) -> {
                if(m.getName().equals("getGameState")) return net.runelite.api.GameState.LOADING;
                if(m.getReturnType()==int.class) return 0;
                if(m.getReturnType()==boolean.class) return false;
                return null;
            });
        java.lang.reflect.Field clientField = CombatInsightPlugin.class.getDeclaredField("client");
        clientField.setAccessible(true); clientField.set(plugin,client);
        java.lang.reflect.Field configField = CombatInsightPlugin.class.getDeclaredField("config");
        configField.setAccessible(true);configField.set(plugin,new CombatInsightConfig(){
            @Override public boolean automaticTobInputs(){return true;}
        });
        java.lang.reflect.Method refresh = CombatInsightPlugin.class.getDeclaredMethod("refreshSnapshot");
        refresh.setAccessible(true);refresh.invoke(plugin);
        assertEquals(5,tracker.observe(true,2,new int[5]));
    }

	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CombatInsightPlugin.class);
		RuneLite.main(args);
	}
}
