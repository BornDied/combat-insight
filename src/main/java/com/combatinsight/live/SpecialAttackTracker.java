package com.combatinsight.live;

import com.combatinsight.calculation.SpecialAttackWeapon;
import net.runelite.api.NPC;

/** Detects local special use and binds its next hitsplat to a live target. */
public final class SpecialAttackTracker
{
	private static final int PENDING_TIMEOUT_TICKS = 5;

	private final TargetEffectTracker targetEffects = new TargetEffectTracker();
	private int previousEnergy = -1;
    private DefenceTrackingMode trackingMode = DefenceTrackingMode.LOCAL;

    public void setTrackingMode(DefenceTrackingMode next)
    {
        if (next == null) next = DefenceTrackingMode.LOCAL;
        if (trackingMode != next)
        {
            clearTrackedEffects();
            trackingMode = next;
        }
    }

    /** Reset estimates without losing the current special-energy baseline. */
    public void clearTrackedEffects()
    {
        pendingSpecial = null;
        targetEffects.clearReductions();
    }
	private PendingSpecial pendingSpecial;
	private EncounterMode encounterMode = EncounterMode.AUTO;

    public void setEncounterMode(EncounterMode next)
    {
        if (encounterMode != next)
        {
            pendingSpecial = null;
            encounterMode = next;
            targetEffects.setEncounterMode(next);
        }
    }

	private RaidScaling raidScaling = RaidScaling.DEFAULT;

	public void setRaidScaling(RaidScaling next)
	{
		if (!raidScaling.equals(next))
		{
			pendingSpecial = null;
			raidScaling = next;
			targetEffects.setRaidScaling(next);
		}
	}

	public void resetEnergy(int specialEnergy)
	{
		previousEnergy = specialEnergy;
		pendingSpecial = null;
	}

	public boolean onEnergyChanged(
		int specialEnergy,
		SpecialAttackWeapon weapon,
		NPC target,
		int gameTick)
	{
		if (previousEnergy < 0 || specialEnergy >= previousEnergy)
		{
			previousEnergy = specialEnergy;
			return false;
		}

		previousEnergy = specialEnergy;
        // Attempt history controls conditional spec accuracy, not target reductions.
        targetEffects.recordSpecialAttempt(weapon, target);
		pendingSpecial = trackingMode == DefenceTrackingMode.LOCAL && weapon != null && weapon.hasTrackedTargetEffect()
			? new PendingSpecial(weapon, target, gameTick + PENDING_TIMEOUT_TICKS)
			: null;
		return pendingSpecial != null;
	}

	public boolean onHitsplat(NPC target, int displayedDamage, int gameTick)
	{
		return onHitsplat(target, displayedDamage, displayedDamage > 0, gameTick);
	}

	public boolean onHitsplat(
		NPC target,
		int displayedDamage,
		boolean accurate,
		int gameTick)
	{
		if (pendingSpecial == null || target == null)
		{
			return false;
		}
		if (gameTick > pendingSpecial.expiresAtTick)
		{
			pendingSpecial = null;
			return false;
		}
		if (pendingSpecial.target != null && pendingSpecial.target != target)
		{
			return false;
		}

		SpecialAttackWeapon weapon = pendingSpecial.weapon;
		targetEffects.record(weapon, target, displayedDamage, accurate, gameTick);
		pendingSpecial.remainingHits--;
		if (pendingSpecial.remainingHits <= 0)
		{
			pendingSpecial = null;
		}
		return true;
	}

	public void onGameTick(int gameTick)
	{
		if (pendingSpecial != null && gameTick > pendingSpecial.expiresAtTick)
		{
			pendingSpecial = null;
		}
	}

	public TargetEffectSnapshot getTargetEffects(NPC target, int gameTick)
	{
        TargetEffectSnapshot result = targetEffects.snapshot(target, gameTick);
        return trackingMode == DefenceTrackingMode.OFF
            ? result.withTektonHistory(result.isTektonDefenceSpecialAttempted(), result.isTektonDefenceSpecialAttempted())
            : result;
	}

	public void remove(NPC target)
	{
		if (pendingSpecial != null && pendingSpecial.target == target)
		{
			pendingSpecial = null;
		}
		targetEffects.remove(target);
	}

	public void clear()
	{
		previousEnergy = -1;
		pendingSpecial = null;
		targetEffects.clear();
	}

	private static final class PendingSpecial
	{
		private final SpecialAttackWeapon weapon;
		private final NPC target;
		private final int expiresAtTick;
		private int remainingHits;

		private PendingSpecial(SpecialAttackWeapon weapon, NPC target, int expiresAtTick)
		{
			this.weapon = weapon;
			this.target = target;
			this.expiresAtTick = expiresAtTick;
			this.remainingHits = weapon.getEffectHitCount();
		}
	}
}
