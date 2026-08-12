package com.combatinsight.live;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.NPC;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LiveTargetTrackerTest
{
	@Test
	public void retainsTargetWhenInteractionIsTemporarilyCleared()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		NPC scurrius = npc(7221, "Scurrius", new AtomicBoolean(false));

		tracker.observe(scurrius);
		tracker.observe(null);

		assertSame(scurrius, tracker.getTarget());
	}

	@Test
	public void replacesTargetWithNewNpcAndOnlyClearsMatchingActor()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		NPC first = npc(1, "First", new AtomicBoolean(false));
		NPC second = npc(2, "Second", new AtomicBoolean(false));

		tracker.observe(first);
		assertFalse(tracker.clearIfSame(second));
		assertSame(first, tracker.getTarget());

		tracker.observe(second);
		assertSame(second, tracker.getTarget());
		assertTrue(tracker.clearIfSame(second));
		assertNull(tracker.getTarget());
	}

	@Test
	public void clearsTargetAfterDeath()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		AtomicBoolean dead = new AtomicBoolean(false);
		NPC target = npc(3, "Target", dead);

		tracker.observe(target);
		dead.set(true);
		tracker.observe(null);

		assertNull(tracker.getTarget());
	}

	private static NPC npc(int id, String name, AtomicBoolean dead)
	{
		return (NPC) Proxy.newProxyInstance(
			NPC.class.getClassLoader(),
			new Class<?>[]{NPC.class},
			(proxy, method, args) ->
			{
				switch (method.getName())
				{
					case "getId":
					case "getIndex":
						return id;
					case "getName":
						return name;
					case "isDead":
						return dead.get();
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
