package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;

/** Explicitly withhold damage for encounter transforms not yet modelled. */
final class RaidDamageRules
{
	private RaidDamageRules() { }

	static String unavailableReason(int id, AttackType type, boolean fireSpell)
	{
		if (id >= 8369 && id <= 8371 || id >= 10830 && id <= 10832
			|| id >= 10847 && id <= 10849) return "Verzik P1 damage pending";
		if (TargetMechanics.isGuardian(id)) return "Guardian damage pending";
		if (id >= 7540 && id <= 7545 && type == AttackType.MAGIC)
			return "Tekton magic reduction pending";
		if (id == 7568 && type == AttackType.MAGIC) return "Crystal damage reduction pending";
		if ((id == 7551 || id == 7552 || id == 7554 || id == 7555) && type == AttackType.MAGIC)
			return "Olm damage reduction pending";
		if ((id == 7550 || id == 7552 || id == 7553 || id == 7555) && type.isRanged())
			return "Olm damage reduction pending";
		if ((id == 7584 || id == 7585) && !fireSpell) return "Ice demon damage pending";
		return "";
	}
}
