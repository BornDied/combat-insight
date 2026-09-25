package com.combatinsight.live;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;

/** Regression coverage for standard-game IDs used by major boss encounters. */
public class MajorBossCoverageTest
{
	private static final int[] LEGACY_AND_GOD_WARS = {
		239, 319, 963, 965, 2054, 2205, 2215, 2642, 3127, 3129, 3162, 4303,
		4304, 5779, 7706, 11278, 11279, 11280, 11281, 11282
	};

	private static final int[] SLAYER_AND_WILDERNESS = {
		494, 496, 499, 5862, 5863, 5866, 5886, 5887, 5888, 5889, 5890, 5891,
		5908, 6609, 6610, 6611, 6612, 6615, 6618, 6619, 7851, 7852, 7853, 7854,
		7855, 7882, 7883, 7884, 7885, 7886, 7887, 7888, 7889, 8615, 8616, 8617,
		8618, 8619, 8620, 8621, 8622, 11992, 11993, 11994, 11998, 13668
	};

	private static final int[] MODERN_SOLO = {
		2042, 2043, 2044, 7221, 7222, 8058, 8059, 8060, 8061, 8713, 9416, 9417,
		9418, 9419, 9420, 9421, 9422, 9424, 9425, 9426, 9427, 9428, 9429, 9430,
		9431, 9432, 9433, 9460, 11153, 11154, 11155, 12077, 12078, 12079, 12080,
		12082, 12191, 12195, 12204, 12205, 12206, 12207, 12214, 12215, 12219,
		12223, 12224, 12228, 12425, 12426, 12596, 12821, 13011, 13012, 13013,
		13685, 13686, 14009, 14010, 14011, 14013, 14014, 14015, 14017, 14147,
		14176, 14707, 15742, 16204
	};

	private static final int[] RAIDS = {
		7530, 7531, 7532, 7540, 7541, 7542, 7543, 7550, 7551, 7552, 7561, 7562,
		7563, 7566, 7567, 8340, 8354, 8355, 8356, 8357, 8359, 8360, 8361, 8362,
		8363, 8369, 8370, 8371, 8372, 8373, 8374, 8375, 8387, 8388, 10768,
		10772, 10786, 10787, 10788, 10789, 10807, 10808, 10809, 10810, 10812,
		10813, 10814, 10822, 10830, 10831, 10832, 10833, 10834, 10835, 10836,
		10847, 10848, 10849, 10850, 10851, 10852, 10853, 10864, 10865, 10867,
		10868, 11719, 11721, 11730, 11732, 11753, 11754, 11755, 11756, 11757,
		11758, 11761, 11762, 11763, 11764, 11778, 11779, 11780, 11789, 11790,
		11791, 11792, 11793, 11794, 11795, 11796
	};

	@Test
	public void containsEveryAuditedMajorBossId()
	{
		assertPresent(LEGACY_AND_GOD_WARS);
		assertPresent(SLAYER_AND_WILDERNESS);
		assertPresent(MODERN_SOLO);
		assertPresent(RAIDS);
	}

	@Test
	public void containsEveryUnambiguousNexPhase()
	{
		for (int npcId = 11278; npcId <= 11282; npcId++)
		{
			TargetProfile nex = TargetDatabase.find(npcId);
			assertNotNull(nex);
			assertFalse(nex.isAmbiguous());
		}
	}

	private static void assertPresent(int[] npcIds)
	{
		for (int npcId : npcIds)
		{
			assertNotNull("Missing major boss NPC ID " + npcId, TargetDatabase.find(npcId));
		}
	}
}
