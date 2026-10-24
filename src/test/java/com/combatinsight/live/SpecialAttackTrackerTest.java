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
	public void raidInputChangesRebaseStatsAndClearOldDrains()
	{
		SpecialAttackTracker tracker = new SpecialAttackTracker();
		NPC target = npc(7540, "Tekton");
		tracker.setRaidScaling(new RaidScaling(1, true, 126, 99, 99, 3));
		tracker.resetEnergy(1000);
		assertTrue(tracker.onEnergyChanged(500, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 1));
		assertTrue(tracker.onHitsplat(target, 20, 2));
		assertEquals(173, tracker.getTargetEffects(target, 2).getCurrentDefence());
		tracker.setRaidScaling(new RaidScaling(1, true, 126, 99, 99, 3));
		assertEquals(173, tracker.getTargetEffects(target, 2).getCurrentDefence());
		tracker.setRaidScaling(new RaidScaling(1, false, 126, 99, 99, 3));
		assertEquals(205, tracker.getTargetEffects(target, 2).getCurrentDefence());
	}

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

    @Test public void ralosTracksBorkBeforeStunAndStopsAtHisDefenceFloor()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();NPC bork=npc(16640,"Bork");
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500,SpecialAttackWeapon.TONALZTICS_OF_RALOS,bork,1);
        tracker.onHitsplat(bork,12,true,2);tracker.onHitsplat(bork,15,true,2);
        assertEquals(278,tracker.getTargetEffects(bork,2).getCurrentDefence());
        for(int i=0;i<20;i++)
        {
            tracker.resetEnergy(1000);tracker.onEnergyChanged(500,SpecialAttackWeapon.TONALZTICS_OF_RALOS,bork,3);
            tracker.onHitsplat(bork,12,true,4);tracker.onHitsplat(bork,12,true,4);
        }
        assertEquals(180,tracker.getTargetEffects(bork,4).getCurrentDefence());
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

    @Test public void dt2ModeChangeClearsDrainsAndPendingSpecial()
    {
        SpecialAttackTracker tracker=new SpecialAttackTracker();
        NPC duke=npc(12191,"Duke Sucellus");
        tracker.setEncounterMode(EncounterMode.NORMAL);
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500,SpecialAttackWeapon.DRAGON_WARHAMMER,duke,1);
        assertTrue(tracker.onHitsplat(duke,20,2));
        assertEquals(193,tracker.getTargetEffects(duke,2).getCurrentDefence());
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500,SpecialAttackWeapon.DRAGON_WARHAMMER,duke,3);
        tracker.setEncounterMode(EncounterMode.AWAKENED);
        assertFalse(tracker.onHitsplat(duke,20,4));
        assertEquals(316,tracker.getTargetEffects(duke,4).getCurrentDefence());
        tracker.setEncounterMode(EncounterMode.AUTO);
        assertFalse(tracker.getTargetEffects(duke,5).hasTargetStats());
    }

    @Test public void offCancelsPendingAndExistingReductionsWithoutResurrectingThem()
    {
        SpecialAttackTracker tracker = new SpecialAttackTracker();
        NPC target = npc(1, "Man");
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 1);
        tracker.onHitsplat(target, 20, 2);
        assertEquals(35, tracker.getTargetEffects(target, 2).getCurrentDefence());
        tracker.onEnergyChanged(0, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 3);
        tracker.setTrackingMode(DefenceTrackingMode.OFF);
        assertFalse(tracker.onHitsplat(target, 20, 4));
        assertEquals(50, tracker.getTargetEffects(target, 4).getCurrentDefence());
        tracker.onEnergyChanged(1000, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 5);
        assertFalse(tracker.onEnergyChanged(500, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 6));
        tracker.setTrackingMode(DefenceTrackingMode.LOCAL);
        assertFalse(tracker.onHitsplat(target, 20, 7));
        assertEquals(50, tracker.getTargetEffects(target, 7).getCurrentDefence());
        assertTrue(tracker.onEnergyChanged(0, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 8));
        assertTrue(tracker.onHitsplat(target, 20, 9));
        assertEquals(35, tracker.getTargetEffects(target, 9).getCurrentDefence());
    }

    @Test public void resetClearsPendingButPreservesNextEnergyTransition()
    {
        SpecialAttackTracker tracker = new SpecialAttackTracker();
        NPC target = npc(1, "Man");
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 1);
        tracker.clearTrackedEffects();
        assertFalse(tracker.onHitsplat(target, 20, 2));
        assertTrue(tracker.onEnergyChanged(0, SpecialAttackWeapon.DRAGON_WARHAMMER, target, 3));
        tracker.onHitsplat(target, 20, 4);
        assertEquals(35, tracker.getTargetEffects(target, 4).getCurrentDefence());
        tracker.clearTrackedEffects();
        assertEquals(50, tracker.getTargetEffects(target, 4).getCurrentDefence());
    }

    @Test public void resetAndOffPreserveTektonAttemptHistoryWithoutPreservingDrains()
    {
        SpecialAttackTracker tracker = new SpecialAttackTracker();
        NPC tekton = npc(7540,"Tekton");
        tracker.resetEnergy(1000);
        tracker.onEnergyChanged(500,SpecialAttackWeapon.DRAGON_WARHAMMER,tekton,1);
        tracker.onHitsplat(tekton,20,2);
        tracker.clearTrackedEffects();
        assertTrue(tracker.getTargetEffects(tekton,2).isTektonDefenceSpecialAttempted());
        assertEquals(205,tracker.getTargetEffects(tekton,2).getCurrentDefence());
        tracker.setTrackingMode(DefenceTrackingMode.OFF);
        assertTrue(tracker.getTargetEffects(tekton,3).isTektonDefenceSpecialAttempted());
        tracker.clear();
        tracker.resetEnergy(1000);
        assertFalse(tracker.onEnergyChanged(500,SpecialAttackWeapon.ELDER_MAUL,tekton,4));
        assertTrue(tracker.getTargetEffects(tekton,4).isTektonDefenceSpecialAttempted());
        assertEquals(205,tracker.getTargetEffects(tekton,4).getCurrentDefence());
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
