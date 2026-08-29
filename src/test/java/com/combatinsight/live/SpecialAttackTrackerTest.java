package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import java.lang.reflect.Proxy;
import net.runelite.api.NPC;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpecialAttackTrackerTest
{
	@Test
	public void energyDropAndNextMatchingHitsplatApplyTheEffect()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC target = npc(1, "Man");

		tracker.resetEnergy(1000);
		assertTrue(tracker.onEnergyChanged(
			500, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 10));
		assertTrue(tracker.onHitsplat(target, 1, 11));
		assertEquals(35, tracker.getTargetEffects(target, 11).getCurrentDefence());
	}

	@Test
	public void normalEnergyChangesAndNonReductionSpecsDoNotQueueEffects()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC target = npc(1, "Man");

		tracker.resetEnergy(500);
		assertFalse(tracker.onEnergyChanged(
			600, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 1));
		assertFalse(tracker.onEnergyChanged(
			350, SpecialAttackWeapon.DRAGON_CLAWS, target, 2));
		assertFalse(tracker.onHitsplat(target, 10, 3));
	}

	@Test
	public void pendingEffectRequiresTheSameNpcAndExpires()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC target = npc(1, "Man");
		NPC other = npc(2, "Woman");

		tracker.resetEnergy(1000);
		tracker.onEnergyChanged(500, SpecialAttackWeapon.BANDOS_GODSWORD, target, 10);
		assertFalse(tracker.onHitsplat(other, 20, 11));
		assertTrue(tracker.onHitsplat(target, 20, 11));

		tracker.resetEnergy(1000);
		tracker.onEnergyChanged(500, SpecialAttackWeapon.BANDOS_GODSWORD, target, 20);
		tracker.onGameTick(26);
		assertFalse(tracker.onHitsplat(target, 20, 26));
	}

	@Test
	public void separateNpcActorsKeepSeparateDefenceStates()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC first = npc(1, "First man");
		NPC second = npc(1, "Second man");

		tracker.resetEnergy(1000);
		tracker.onEnergyChanged(500, SpecialAttackWeapon.DRAGON_WARHAMMER, first, 0);
		tracker.onHitsplat(first, 1, 1);

		assertEquals(35, tracker.getTargetEffects(first, 1).getCurrentDefence());
		assertEquals(50, tracker.getTargetEffects(second, 1).getCurrentDefence());
		tracker.remove(first);
		assertEquals(50, tracker.getTargetEffects(first, 1).getCurrentDefence());
	}

	@Test
	public void tonalzticsConsumesAndTracksTwoHitsplats()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC target = npc(2, "Ghost");

		tracker.resetEnergy(1000);
		assertTrue(tracker.onEnergyChanged(
			500, SpecialAttackWeapon.TONALZTICS_OF_RALOS, target, 10));
		assertTrue(tracker.onHitsplat(target, 10, 11));
		assertEquals(77, tracker.getTargetEffects(target, 11).getCurrentDefence());
		assertTrue(tracker.onHitsplat(target, 10, 11));
		assertEquals(64, tracker.getTargetEffects(target, 11).getCurrentDefence());
		assertFalse(tracker.onHitsplat(target, 10, 11));
	}

	private static NPC npc(int id, String name)
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
