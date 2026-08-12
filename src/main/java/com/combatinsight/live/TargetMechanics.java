package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Small ID sets for mechanics that cannot be inferred from raw defence stats. */
final class TargetMechanics
{
	private static final int SCURRIUS_SPAWNED_RAT = 7223;

	private static final Set<Integer> TOMBS_OF_AMASCUT = ids(
		11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796,
		11797, 11798, 11799,
		11778, 11779, 11780,
		11719, 11721, 11724, 11725, 11726,
		11730, 11732, 11733,
		11751, 11750, 11752,
		11753, 11754, 11756, 11757,
		11755, 11758,
		11761, 11763, 11762, 11764);

	private static final Set<Integer> MAGIC_USES_DEFENCE_LEVEL = ids(
		7584, 7585,
		10830, 10831, 10832, 8369, 8370, 8371, 10847, 10848, 10849,
		10833, 10834, 10835, 8372, 8373, 8374, 10850, 10851, 10852,
		8917, 8918, 8919, 8920,
		11709, 11712, 9118);

	private static final Set<Integer> KEPHRI_OVERLORDS = ids(11724, 11725, 11726);

	private static final Set<Integer> MELEE_IMMUNE = ids(
		494, 7533, 7706, 7708,
		12214, 12215, 12219,
		2042, 2043, 2044);

	private static final Set<Integer> NON_SALAMANDER_MELEE_IMMUNE = ids(
		3169, 3170, 3171, 3172, 3173, 3174, 3175, 3176, 3177,
		3178, 3179, 3180, 3181, 3182, 3183, 7037);

	private static final Set<Integer> ZULRAH = ids(2042, 2043, 2044);
	private static final Set<Integer> VESPULA = ids(7530, 7531, 7532);
	private static final Set<Integer> GUARDIANS = ids(7569, 7571, 7570, 7572);

	private static final Set<Integer> RANGED_IMMUNE = ids(
		7540, 7543, 7544, 7545,
		7851, 7854, 7855, 7882, 7883, 7886, 7887, 7888, 7889,
		7568,
		2463, 2465, 2467, 2464, 2466, 2468,
		2137, 2138, 2139, 2140, 2141, 2142);

	private static final Set<Integer> MAGIC_IMMUNE = ids(
		7851, 7854, 7855, 7882, 7883, 7886, 7887, 7888, 7889,
		2463, 2465, 2467, 2464, 2466, 2468,
		2137, 2138, 2139, 2140, 2141, 2142);

	private TargetMechanics()
	{
	}

	static boolean isInTombsOfAmascut(int npcId)
	{
		return TOMBS_OF_AMASCUT.contains(npcId);
	}

	static boolean magicUsesDefenceLevel(int npcId)
	{
		return MAGIC_USES_DEFENCE_LEVEL.contains(npcId);
	}

	static boolean scalesWithToaInvocation(int npcId)
	{
		return TOMBS_OF_AMASCUT.contains(npcId) && !KEPHRI_OVERLORDS.contains(npcId);
	}

	/** Scurrius's summoned rats are always hit and always receive the max damage roll. */
	static boolean isGuaranteedMaxHitTarget(int npcId)
	{
		return npcId == SCURRIUS_SPAWNED_RAT;
	}

	static boolean isImmune(int npcId, AttackType attackType)
	{
		if (attackType.isMelee())
		{
			return MELEE_IMMUNE.contains(npcId);
		}
		if (attackType.isRanged())
		{
			return RANGED_IMMUNE.contains(npcId);
		}
		return attackType == AttackType.MAGIC && MAGIC_IMMUNE.contains(npcId);
	}

	static boolean isNonSalamanderMeleeImmune(int npcId)
	{
		return NON_SALAMANDER_MELEE_IMMUNE.contains(npcId);
	}

	static boolean isZulrah(int npcId)
	{
		return ZULRAH.contains(npcId);
	}

	static boolean isVespula(int npcId)
	{
		return VESPULA.contains(npcId);
	}

	static boolean isGuardian(int npcId)
	{
		return GUARDIANS.contains(npcId);
	}

	private static Set<Integer> ids(Integer... values)
	{
		return new HashSet<>(Arrays.asList(values));
	}
}
