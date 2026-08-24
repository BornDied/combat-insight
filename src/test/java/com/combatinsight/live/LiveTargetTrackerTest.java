package com.combatinsight.live;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.NPC;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LiveTargetTrackerTest
{
	@Test
	public void retainsConfirmedTargetIdentityAfterCombatEnds()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		NPC scurrius = npc(7221, "Scurrius", new AtomicBoolean(false));

		assertTrue(tracker.confirmCombatTarget(scurrius));
		assertTrue(tracker.isEngagedWith(scurrius));
		assertTrue(tracker.endActiveIfSame(scurrius));

		assertFalse(tracker.isEngagedWith(scurrius));
		assertEquals(7221, tracker.getRetainedTargetId());
		assertEquals("Scurrius", tracker.getRetainedTargetName());
	}

	@Test
	public void replacesTargetOnlyAfterAnotherCombatConfirmation()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		NPC first = npc(1, "First", new AtomicBoolean(false));
		NPC second = npc(2, "Second", new AtomicBoolean(false));

		tracker.confirmCombatTarget(first);
		assertFalse(tracker.endActiveIfSame(second));
		assertSame(first, tracker.getActiveTarget());
		assertEquals(1, tracker.getRetainedTargetId());

		tracker.confirmCombatTarget(second);
		assertSame(second, tracker.getActiveTarget());
		assertEquals(2, tracker.getRetainedTargetId());
		assertEquals("Second", tracker.getRetainedTargetName());
	}

	@Test
	public void deathEndsActiveActorButKeepsRetainedTarget()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		AtomicBoolean dead = new AtomicBoolean(false);
		NPC target = npc(3, "Target", dead);

		tracker.confirmCombatTarget(target);
		dead.set(true);

		assertTrue(tracker.endActiveIfDead());
		assertEquals(3, tracker.getRetainedTargetId());
		assertEquals("Target", tracker.getRetainedTargetName());
		assertFalse(tracker.isEngagedWith(target));
	}

	@Test
	public void fullClearRemovesActiveAndRetainedTarget()
	{
		LiveTargetTracker tracker = new LiveTargetTracker();
		NPC target = npc(4, "Target", new AtomicBoolean(false));

		tracker.confirmCombatTarget(target);
		tracker.clear();

		assertEquals(-1, tracker.getRetainedTargetId());
		assertEquals("", tracker.getRetainedTargetName());
		assertFalse(tracker.isEngagedWith(target));
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
