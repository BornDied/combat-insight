package com.combatinsight.live;

import net.runelite.api.Actor;
import net.runelite.api.NPC;

/**
 * Retains the last NPC explicitly confirmed by an offensive player action.
 *
 * <p>The live actor is used only while combat is active. Its ID and name remain
 * available after death or despawn so hit chance and DPS can stay visible
 * between repeated kills without retaining a removed RuneLite actor.</p>
 */
public final class LiveTargetTracker
{
	private NPC activeTarget;
	private int retainedTargetId = -1;
	private String retainedTargetName = "";

	/**
	 * Confirms an NPC from an Attack/Cast click or a local-player hitsplat.
	 * Ordinary NPC interactions must never call this method.
	 */
	public boolean confirmCombatTarget(NPC target)
	{
		if (target == null)
		{
			return false;
		}

		int targetId = target.getId();
		String targetName = target.getName();
		boolean changed = activeTarget != target
			|| retainedTargetId != targetId
			|| !sameText(retainedTargetName, targetName);
		activeTarget = target;
		retainedTargetId = targetId;
		retainedTargetName = targetName;
		return changed;
	}

	/**
	 * Ends the live engagement only when the supplied actor is the active NPC.
	 * The retained ID and name intentionally survive for between-kill display.
	 */
	public boolean endActiveIfSame(Actor actor)
	{
		if (actor != null && actor == activeTarget)
		{
			activeTarget = null;
			return true;
		}
		return false;
	}

	public boolean endActiveIfDead()
	{
		if (activeTarget != null && activeTarget.isDead())
		{
			activeTarget = null;
			return true;
		}
		return false;
	}

	public boolean isEngagedWith(Actor interacting)
	{
		return activeTarget != null
			&& interacting == activeTarget
			&& !activeTarget.isDead();
	}

	public void clear()
	{
		activeTarget = null;
		retainedTargetId = -1;
		retainedTargetName = "";
	}

	public int getRetainedTargetId()
	{
		return retainedTargetId;
	}

	public String getRetainedTargetName()
	{
		return retainedTargetName;
	}

	public NPC getActiveTarget()
	{
		return activeTarget;
	}

	private static boolean sameText(String first, String second)
	{
		return first == null ? second == null : first.equals(second);
	}
}
