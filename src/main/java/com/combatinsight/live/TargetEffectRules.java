package com.combatinsight.live;

import com.combatinsight.calculation.TargetProfile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Target-specific defence floors and special-attack exceptions. */
final class TargetEffectRules
{
	private static final Set<Integer> BASE_DEFENCE_FLOOR = ids(
		10830, 10831, 10832, 8369, 8370, 8371, 10847, 10848, 10849,
		10833, 10834, 10835, 8372, 8373, 8374, 10850, 10851, 10852,
		12223, 12224, 12228, 12425, 12426, 13656);
	private static final Set<Integer> SOTETSEG = ids(8387, 8388, 10867, 10868);
	private static final Set<Integer> NIGHTMARE = ids(
		378, 9425, 9426, 9427, 9428, 9429, 9430, 9431, 9432, 9433, 9460,
		377, 9423, 9416, 9417, 9418, 9419, 9420, 9421, 9422, 9424, 11153, 11154, 11155);
	private static final Set<Integer> AKKHA = ids(11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796);
	private static final Set<Integer> BABA = ids(11778, 11779, 11780);
	private static final Set<Integer> KEPHRI = ids(11719, 11721);
	private static final Set<Integer> ZEBAK = ids(11730, 11732, 11733);
	private static final Set<Integer> P3_WARDEN = ids(11761, 11763, 11762, 11764);
	private static final Set<Integer> TOA_OBELISK = ids(11751, 11750, 11752);
	private static final Set<Integer> NEX = ids(11278, 11279, 11280, 11281, 11282);
	private static final Set<Integer> ARAXXOR = ids(13668);
	private static final Set<Integer> HUEYCOATL = ids(14009, 14010, 14013, 14017, 14014);
	private static final Set<Integer> YAMA = ids(14176);
	private static final Set<Integer> TEKTON = ids(7540, 7541, 7542, 7543, 7544, 7545, 7546, 7547);

	private TargetEffectRules()
	{
	}

	static int defenceFloor(TargetProfile target)
	{
		if (target == null)
		{
			return 0;
		}
		int id = target.getId();
		if (BASE_DEFENCE_FLOOR.contains(id))
		{
			return target.getDefenceLevel();
		}
		if (SOTETSEG.contains(id))
		{
			return 100;
		}
		if (NIGHTMARE.contains(id) || P3_WARDEN.contains(id) || HUEYCOATL.contains(id))
		{
			return 120;
		}
		if (AKKHA.contains(id))
		{
			return 70;
		}
		if (BABA.contains(id) || KEPHRI.contains(id) || TOA_OBELISK.contains(id))
		{
			return 60;
		}
		if (ZEBAK.contains(id))
		{
			return 50;
		}
		if (NEX.contains(id))
		{
			return 250;
		}
		if (ARAXXOR.contains(id))
		{
			return 90;
		}
		if (YAMA.contains(id))
		{
			return 145;
		}
		return 0;
	}

	static boolean isTekton(int npcId, String npcName)
	{
		if (TEKTON.contains(npcId))
		{
			return true;
		}
		return npcName != null
			&& npcName.toLowerCase(Locale.ROOT).contains("tekton");
	}

	private static Set<Integer> ids(Integer... values)
	{
		return new HashSet<>(Arrays.asList(values));
	}
}
