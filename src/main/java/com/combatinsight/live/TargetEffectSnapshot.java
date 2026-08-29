package com.combatinsight.live;

import com.combatinsight.calculation.TargetProfile;
import java.util.Locale;

/** Read-only live target levels derived from locally observed special attacks. */
public final class TargetEffectSnapshot
{
	private static final TargetEffectSnapshot EMPTY = new TargetEffectSnapshot(
		-1, 0, 0, 0, 0, 0, 0, false, false, false, false);

	private final int targetId;
	private final int baseDefence;
	private final int currentDefence;
	private final int baseMagic;
	private final int currentMagic;
	private final int baseMagicDefence;
	private final int currentMagicDefence;
	private final boolean trackedEffect;
	private final boolean estimatedDefenceRecovery;
	private final boolean estimatedMagicRecovery;
	private final boolean tektonDefenceSpecialAttempted;

	TargetEffectSnapshot(
		int targetId,
		int baseDefence,
		int currentDefence,
		int baseMagic,
		int currentMagic,
		int baseMagicDefence,
		int currentMagicDefence,
		boolean trackedEffect,
		boolean estimatedDefenceRecovery,
		boolean estimatedMagicRecovery,
		boolean tektonDefenceSpecialAttempted)
	{
		this.targetId = targetId;
		this.baseDefence = Math.max(0, baseDefence);
		this.currentDefence = Math.max(0, currentDefence);
		this.baseMagic = Math.max(0, baseMagic);
		this.currentMagic = Math.max(0, currentMagic);
		this.baseMagicDefence = baseMagicDefence;
		this.currentMagicDefence = currentMagicDefence;
		this.trackedEffect = trackedEffect;
		this.estimatedDefenceRecovery = estimatedDefenceRecovery;
		this.estimatedMagicRecovery = estimatedMagicRecovery;
		this.tektonDefenceSpecialAttempted = tektonDefenceSpecialAttempted;
	}

	public static TargetEffectSnapshot empty()
	{
		return EMPTY;
	}

	static TargetEffectSnapshot unmodified(TargetProfile target)
	{
		if (target == null || target.isAmbiguous())
		{
			return EMPTY;
		}
		int magicDefence = target.defenceBonus(com.combatinsight.calculation.AttackType.MAGIC);
		return new TargetEffectSnapshot(
			target.getId(),
			target.getDefenceLevel(),
			target.getDefenceLevel(),
			target.getMagicLevel(),
			target.getMagicLevel(),
			magicDefence,
			magicDefence,
			false,
			false,
			false,
			false);
	}

	public boolean hasTargetStats()
	{
		return targetId >= 0;
	}

	public boolean appliesTo(int npcId)
	{
		return hasTargetStats() && targetId == npcId;
	}

	public TargetProfile applyTo(TargetProfile target)
	{
		if (target == null || !appliesTo(target.getId()))
		{
			return target;
		}
		return target.withCombatStats(currentDefence, currentMagic, currentMagicDefence);
	}

	public int getBaseDefence()
	{
		return baseDefence;
	}

	public int getCurrentDefence()
	{
		return currentDefence;
	}

	public int getBaseMagic()
	{
		return baseMagic;
	}

	public int getCurrentMagic()
	{
		return currentMagic;
	}

	public int getBaseMagicDefence()
	{
		return baseMagicDefence;
	}

	public int getCurrentMagicDefence()
	{
		return currentMagicDefence;
	}

	public boolean hasTrackedEffect()
	{
		return trackedEffect;
	}

	public boolean hasTrackedDefence()
	{
		return currentDefence != baseDefence;
	}

	public boolean hasTrackedMagic()
	{
		return currentMagic != baseMagic;
	}

	public boolean hasTrackedMagicDefence()
	{
		return currentMagicDefence != baseMagicDefence;
	}

	/**
	 * Before any Magic-related drain, the infobox introduces the target using
	 * its Magic Defence bonus. A tracked Magic-level drain takes priority only
	 * when no Magic Defence bonus reduction is active.
	 */
	public boolean displaysMagicDefenceBonus()
	{
		return hasTrackedMagicDefence() || !hasTrackedMagic();
	}

	public boolean isEstimatedRecovery()
	{
		return estimatedDefenceRecovery || estimatedMagicRecovery;
	}

	public boolean isTektonDefenceSpecialAttempted()
	{
		return tektonDefenceSpecialAttempted;
	}

	public String getDefenceText()
	{
		if (!hasTargetStats())
		{
			return "Unavailable";
		}
		String suffix = hasTrackedDefence() ? estimatedDefenceRecovery ? " est." : " tracked" : "";
		return String.format(Locale.ROOT, "%,d / %,d%s", currentDefence, baseDefence, suffix);
	}

	public String getMagicText()
	{
		if (!hasTargetStats())
		{
			return "Unavailable";
		}
		String suffix = hasTrackedMagic() ? estimatedMagicRecovery ? " est." : " tracked" : "";
		return String.format(Locale.ROOT, "%,d / %,d%s", currentMagic, baseMagic, suffix);
	}

	public String getMagicDefenceText()
	{
		if (!hasTargetStats())
		{
			return "Unavailable";
		}
		String suffix = hasTrackedMagicDefence() ? " tracked" : "";
		return String.format(Locale.ROOT, "%+,d / %+,d%s",
			currentMagicDefence, baseMagicDefence, suffix);
	}
}
