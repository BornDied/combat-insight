package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import net.runelite.api.NPC;

/** Keeps independent locally observed effects for each live NPC actor. */
final class TargetEffectTracker
{
	private final Map<NPC, TargetEffectState> states = new IdentityHashMap<>();
	private final Set<NPC> tektonAttempts = Collections.newSetFromMap(new IdentityHashMap<NPC, Boolean>());

    void recordSpecialAttempt(com.combatinsight.calculation.SpecialAttackWeapon weapon, NPC target)
    {
        if (weapon != null && weapon.hasTrackedTargetEffect() && target != null
            && TargetEffectRules.isTekton(target.getId(), target.getName())) tektonAttempts.add(target);
    }

    void clearReductions() { states.clear(); }

    private EncounterMode encounterMode = EncounterMode.AUTO;

    void setEncounterMode(EncounterMode next)
    {
        if (encounterMode != next) { states.clear(); encounterMode = next; }
    }

	private RaidScaling raidScaling = RaidScaling.DEFAULT;

	void setRaidScaling(RaidScaling next)
	{
		if (!raidScaling.equals(next))
		{
			states.clear();
			raidScaling = next;
		}
	}

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

		TargetProfile baseTarget = raidScaling.apply(EncounterResolver.resolve(target.getId(), EncounterContext.from(target, encounterMode)));
		if (baseTarget == null || baseTarget.isAmbiguous())
		{
			return false;
		}

        recordSpecialAttempt(weapon, target);
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

		TargetProfile baseTarget = raidScaling.apply(EncounterResolver.resolve(target.getId(), EncounterContext.from(target, encounterMode)));
		if (baseTarget == null || baseTarget.isAmbiguous())
		{
			return TargetEffectSnapshot.empty();
		}

		TargetEffectState state = states.get(target);
		if (state == null)
		{
			return TargetEffectSnapshot.unmodified(baseTarget).withTektonHistory(tektonAttempts.contains(target), true);
		}
		state.rebase(baseTarget);
		state.advance(gameTick);
		return state.snapshot().withTektonHistory(tektonAttempts.contains(target), true);
	}

	void remove(NPC target)
	{
		if (target != null)
		{
			states.remove(target);
            tektonAttempts.remove(target);
		}
	}

	void clear()
	{
		states.clear();
        tektonAttempts.clear();
	}
}
