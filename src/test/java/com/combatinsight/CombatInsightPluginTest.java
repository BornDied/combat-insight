package com.combatinsight;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CombatInsightPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CombatInsightPlugin.class);
		RuneLite.main(args);
	}
}
