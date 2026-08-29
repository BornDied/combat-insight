package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import java.util.IdentityHashMap;
import java.util.Map;
import net.runelite.api.NPC;

/** Keeps independent locally observed effects for each live NPC actor. */
final class TargetEffectTracker
{
	private final Map<NPC, TargetEffectState> states = new IdentityHashMap<>();

	boolean record(
		SpecialAttackWeapon weapon,
		NPC target,
		int displayedDamage,
		boolean accurate,
		int gameTick)
	{
		if (weapon == null || !weapon.hasTrackedTargetEffect() || target == null)
		{
			return false;
		}

		TargetProfile baseTarget = TargetDatabase.find(target.getId());
		if (baseTarget == null || baseTarget.isAmbiguous())
		{
			return false;
		}

		TargetEffectState state = states.get(target);
		if (state == null)
		{
			state = new TargetEffectState(baseTarget);
			states.put(target, state);
		}
		else
		{
			state.rebase(baseTarget);
		}
		return state.apply(
			weapon,
			Math.max(0, displayedDamage),
			accurate,
			target.getName(),
			gameTick);
	}

	TargetEffectSnapshot snapshot(NPC target, int gameTick)
	{
		if (target == null)
		{
			return TargetEffectSnapshot.empty();
		}

		TargetProfile baseTarget = TargetDatabase.find(target.getId());
		if (baseTarget == null || baseTarget.isAmbiguous())
		{
			return TargetEffectSnapshot.empty();
		}

		TargetEffectState state = states.get(target);
		if (state == null)
		{
			return TargetEffectSnapshot.unmodified(baseTarget);
		}
		state.rebase(baseTarget);
		state.advance(gameTick);
		return state.snapshot();
	}

	void remove(NPC target)
	{
		if (target != null)
		{
			states.remove(target);
		}
	}

	void clear()
	{
		states.clear();
	}
}
