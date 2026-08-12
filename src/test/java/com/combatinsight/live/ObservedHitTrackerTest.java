package com.combatinsight.live;

import java.lang.reflect.Proxy;
import net.runelite.api.NPC;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObservedHitTrackerTest
{
	@Test
	public void averagesDamageIncludingZeroHits()
	{
		ObservedHitTracker tracker = new ObservedHitTracker();
		NPC target = npc("Target");

		tracker.selectTarget(target);
		tracker.record(target, 12);
		tracker.record(target, 0);
		tracker.record(target, 6);

		assertTrue(tracker.hasSamples());
		assertEquals(3, tracker.getHitCount());
		assertEquals(6.0, tracker.getAverageHit(), 0.0000001);
	}

	@Test
	public void keepsSeparateSamplesForSeparateNpcActors()
	{
		ObservedHitTracker tracker = new ObservedHitTracker();
		NPC boss = npc("Boss");
		NPC minion = npc("Minion");

		tracker.selectTarget(boss);
		tracker.record(boss, 20);
		tracker.selectTarget(minion);
		tracker.record(minion, 5);
		assertEquals(5.0, tracker.getAverageHit(), 0.0000001);

		tracker.selectTarget(boss);
		assertEquals(20.0, tracker.getAverageHit(), 0.0000001);
		assertFalse(tracker.record(npc("Never selected"), 99));
	}

	@Test
	public void clearsAllSamplesOnLogoutOrShutdown()
	{
		ObservedHitTracker tracker = new ObservedHitTracker();
		NPC target = npc("Target");

		tracker.selectTarget(target);
		tracker.record(target, 10);
		tracker.clear();

		assertFalse(tracker.hasSamples());
		assertEquals(0, tracker.getHitCount());
	}

	private static NPC npc(String name)
	{
		return (NPC) Proxy.newProxyInstance(
			NPC.class.getClassLoader(),
			new Class<?>[]{NPC.class},
			(proxy, method, args) ->
			{
				switch (method.getName())
				{
					case "getName":
						return name;
					case "hashCode":
						return System.identityHashCode(proxy);
					case "equals":
						return proxy == args[0];
					case "toString":
						return name;
					default:
						return defaultValue(method.getReturnType());
				}
			});
	}

	private static Object defaultValue(Class<?> type)
	{
		if (!type.isPrimitive())
		{
			return null;
		}
		if (type == boolean.class)
		{
			return false;
		}
		if (type == char.class)
		{
			return '\0';
		}
		if (type == long.class)
		{
			return 0L;
		}
		if (type == float.class)
		{
			return 0.0f;
		}
		if (type == double.class)
		{
			return 0.0d;
		}
		if (type == byte.class)
		{
			return (byte) 0;
		}
		if (type == short.class)
		{
			return (short) 0;
		}
		return 0;
	}
}
