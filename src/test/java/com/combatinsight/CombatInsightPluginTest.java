package com.combatinsight;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Test;

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

	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CombatInsightPlugin.class);
		RuneLite.main(args);
	}
}
