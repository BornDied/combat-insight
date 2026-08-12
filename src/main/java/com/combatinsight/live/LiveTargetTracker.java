package com.combatinsight.live;

import net.runelite.api.Actor;
import net.runelite.api.NPC;

/**
 * Retains the last NPC the player interacted with until that NPC dies,
 * despawns, or is replaced by another NPC target.
 *
 * <p>RuneLite briefly clears {@code Player#getInteracting()} when the player
 * eats, moves, changes equipment, or clicks elsewhere. Those actions should
 * not make a boss calculation disappear from the HUD.</p>
 */
public final class LiveTargetTracker
{
	private NPC target;

	/**
	 * Observes the player's current interaction. A missing or non-NPC
	 * interaction does not discard a living NPC target.
	 */
	public void observe(Actor interacting)
	{
		if (interacting instanceof NPC)
		{
			target = (NPC) interacting;
			return;
		}

		if (target != null && target.isDead())
		{
			target = null;
		}
	}

	/**
	 * Clears the target only when the supplied actor is the retained NPC.
	 * Actor identity is intentional because separate NPCs can share an ID.
	 */
	public boolean clearIfSame(Actor actor)
	{
		if (actor != null && actor == target)
		{
			target = null;
			return true;
		}
		return false;
	}

	public void clear()
	{
		target = null;
	}

	public NPC getTarget()
	{
		return target;
	}
}
