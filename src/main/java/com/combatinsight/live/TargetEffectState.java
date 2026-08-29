package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.SpecialAttackWeapon;
import com.combatinsight.calculation.TargetProfile;

/** Mutable state for one live NPC actor. */
final class TargetEffectState
{
	static final int RECOVERY_INTERVAL_TICKS = 100;

	private int targetId;
	private int baseDefence;
	private int currentDefence;
	private int baseMagic;
	private int currentMagic;
	private int baseMagicDefence;
	private int currentMagicDefence;
	private int defenceFloor;
	private boolean demon;
	private int nextRecoveryTick = -1;
	private boolean estimatedDefenceRecovery;
	private boolean estimatedMagicRecovery;
	private boolean tektonDefenceSpecialAttempted;

	TargetEffectState(TargetProfile target)
	{
		rebase(target);
	}

	void rebase(TargetProfile target)
	{
		if (target == null || target.isAmbiguous())
		{
			return;
		}

		int previousDefenceDrain = Math.max(0, baseDefence - currentDefence);
		int previousMagicDrain = Math.max(0, baseMagic - currentMagic);
		int previousMagicDefenceDrain = Math.max(0, baseMagicDefence - currentMagicDefence);
		targetId = target.getId();
		baseDefence = Math.max(0, target.getDefenceLevel());
		baseMagic = Math.max(0, target.getMagicLevel());
		baseMagicDefence = target.defenceBonus(AttackType.MAGIC);
		defenceFloor = Math.min(baseDefence, Math.max(0, TargetEffectRules.defenceFloor(target)));
		demon = target.hasAttribute("demon");
		currentDefence = Math.max(defenceFloor, baseDefence - previousDefenceDrain);
		currentMagic = Math.max(0, baseMagic - previousMagicDrain);
		currentMagicDefence = baseMagicDefence > 0
			? Math.max(0, baseMagicDefence - previousMagicDefenceDrain)
			: baseMagicDefence;
	}

	boolean apply(
		SpecialAttackWeapon weapon,
		int displayedDamage,
		String targetName,
		int gameTick)
	{
		return apply(weapon, displayedDamage, displayedDamage > 0, targetName, gameTick);
	}

	boolean apply(
		SpecialAttackWeapon weapon,
		int displayedDamage,
		boolean accurate,
		String targetName,
		int gameTick)
	{
		advance(gameTick);
		if (weapon == null || !weapon.hasTrackedTargetEffect())
		{
			return false;
		}

		int beforeDefence = currentDefence;
		int beforeMagic = currentMagic;
		int beforeMagicDefence = currentMagicDefence;
		boolean tekton = TargetEffectRules.isTekton(targetId, targetName);
		if (tekton)
		{
			tektonDefenceSpecialAttempted = true;
		}

		switch (weapon)
		{
			case DRAGON_WARHAMMER:
				if (accurate)
				{
					reduceDefence(currentDefence * 30 / 100);
				}
				else if (tekton)
				{
					reduceDefence(currentDefence * 5 / 100);
				}
				break;
			case ELDER_MAUL:
				if (accurate)
				{
					reduceDefence(currentDefence * 35 / 100);
				}
				else if (tekton)
				{
					reduceDefence(currentDefence * 5 / 100);
				}
				break;
			case BANDOS_GODSWORD:
				int drain = displayedDamage > 0 ? displayedDamage : tekton ? 10 : 0;
				reduceDefence(drain);
				break;
			case TONALZTICS_OF_RALOS:
				if (accurate)
				{
					reduceDefence(currentMagic / 8);
				}
				break;
			case EYE_OF_AYAK:
				if (displayedDamage > 0 && currentMagicDefence > 0)
				{
					currentMagicDefence = Math.max(0, currentMagicDefence - displayedDamage);
				}
				break;
			case ACCURSED_SCEPTRE:
				if (accurate)
				{
					int defenceLimit = Math.max(defenceFloor, baseDefence - baseDefence * 15 / 100);
					int magicLimit = Math.max(0, baseMagic - baseMagic * 15 / 100);
					currentDefence = Math.min(currentDefence, defenceLimit);
					currentMagic = Math.min(currentMagic, magicLimit);
				}
				break;
			case BONE_DAGGER:
				if (displayedDamage > 0 && currentDefence == baseDefence)
				{
					reduceDefence(displayedDamage);
				}
				break;
			case ARCLIGHT:
				if (accurate)
				{
					reduceDefence(baseDefence * (demon ? 10 : 5) / 100 + (demon ? 2 : 1));
				}
				break;
			case EMBERLIGHT:
				if (accurate)
				{
					reduceDefence(baseDefence * (demon ? 15 : 5) / 100 + 1);
				}
				break;
			case SEERCULL:
				if (displayedDamage > 0 && currentMagic == baseMagic)
				{
					currentMagic = Math.max(0, currentMagic - displayedDamage);
				}
				break;
			default:
				break;
		}

		boolean recoverableChanged = currentDefence != beforeDefence || currentMagic != beforeMagic;
		boolean changed = recoverableChanged || currentMagicDefence != beforeMagicDefence;
		if (recoverableChanged && nextRecoveryTick < 0)
		{
			nextRecoveryTick = gameTick + RECOVERY_INTERVAL_TICKS;
		}
		return changed || tekton;
	}

	void advance(int gameTick)
	{
		while (nextRecoveryTick >= 0 && gameTick >= nextRecoveryTick)
		{
			if (currentDefence < baseDefence)
			{
				currentDefence++;
				estimatedDefenceRecovery = true;
			}
			if (currentMagic < baseMagic)
			{
				currentMagic++;
				estimatedMagicRecovery = true;
			}
			if (currentDefence >= baseDefence && currentMagic >= baseMagic)
			{
				nextRecoveryTick = -1;
			}
			else
			{
				nextRecoveryTick += RECOVERY_INTERVAL_TICKS;
			}
		}
	}

	TargetEffectSnapshot snapshot()
	{
		boolean tracked = currentDefence != baseDefence
			|| currentMagic != baseMagic
			|| currentMagicDefence != baseMagicDefence;
		return new TargetEffectSnapshot(
			targetId,
			baseDefence,
			currentDefence,
			baseMagic,
			currentMagic,
			baseMagicDefence,
			currentMagicDefence,
			tracked,
			estimatedDefenceRecovery && currentDefence != baseDefence,
			estimatedMagicRecovery && currentMagic != baseMagic,
			tektonDefenceSpecialAttempted);
	}

	private void reduceDefence(int amount)
	{
		currentDefence = Math.max(defenceFloor, currentDefence - Math.max(0, amount));
	}
}
