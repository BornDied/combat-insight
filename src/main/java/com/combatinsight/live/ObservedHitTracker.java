package com.combatinsight.live;

import java.util.IdentityHashMap;
import java.util.Map;
import net.runelite.api.Actor;

/**
 * Tracks the local player's observed damage against NPC actors that have been
 * selected as Combat Insight targets.
 *
 * <p>Each hitsplat is one sample, including zero-damage hits. Keeping samples
 * per actor prevents another target, or another player's damage in a group
 * encounter, from changing the displayed average. The most recently selected
 * actor remains visible after it dies so the finished-fight result can still
 * be read.</p>
 */
public final class ObservedHitTracker
{
	private static final int MAX_TRACKED_TARGETS = 64;

	private final Map<Actor, HitStats> targetStats = new IdentityHashMap<>();
	private Actor selectedTarget;

	public void selectTarget(Actor target)
	{
		if (target == null)
		{
			return;
		}

		if (!targetStats.containsKey(target) && targetStats.size() >= MAX_TRACKED_TARGETS)
		{
			targetStats.clear();
		}

		selectedTarget = target;
		targetStats.computeIfAbsent(target, ignored -> new HitStats());
	}

	/** Records one local-player hitsplat for an actor already selected as a target. */
	public boolean record(Actor actor, int amount)
	{
		HitStats stats = targetStats.get(actor);
		if (stats == null)
		{
			return false;
		}

		stats.totalDamage += Math.max(0, amount);
		stats.hitCount++;
		return true;
	}

	public boolean hasSamples()
	{
		HitStats stats = targetStats.get(selectedTarget);
		return stats != null && stats.hitCount > 0;
	}

	public double getAverageHit()
	{
		HitStats stats = targetStats.get(selectedTarget);
		return stats == null || stats.hitCount == 0
			? 0.0
			: (double) stats.totalDamage / stats.hitCount;
	}

	public int getHitCount()
	{
		HitStats stats = targetStats.get(selectedTarget);
		return stats == null ? 0 : stats.hitCount;
	}

	public void clear()
	{
		targetStats.clear();
		selectedTarget = null;
	}

	private static final class HitStats
	{
		private long totalDamage;
		private int hitCount;
	}
}
