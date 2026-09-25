package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;

public class RaidCoverageTest
{
	@Test
	public void everyAuditedRaidCombatIdHasAnUnambiguousProfile() throws Exception
	{
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/raid-coverage.tsv"), StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.isEmpty() || line.startsWith("#")) continue;
				int id = Integer.parseInt(line.split("\t")[0]);
				assertNotNull(line, TargetDatabase.find(id));
				assertFalse(line, TargetDatabase.find(id).isAmbiguous());
				count++;
			}
		}
		assertEquals(160, count);
	}

	@Test
	public void challengeModeAndMaidenPhaseIdsHaveUsableProfiles()
	{
		for (int id : new int[]{7544, 7545, 7549, 7553, 7554, 7555, 7570, 7572, 7585,
			10815, 10816, 10817, 10823, 10824, 10825})
		{
			assertNotNull("Missing raid ID " + id, TargetDatabase.find(id));
			assertFalse("Ambiguous raid ID " + id, TargetDatabase.find(id).isAmbiguous());
		}
	}

	@Test
	public void maidenHealthBarAlwaysUsesFullPhaseMaximum()
	{
		assertEquals(3500, TargetDatabase.find(8361).getHitpoints());
		assertEquals(3500, TargetDatabase.find(8362).getHitpoints());
		assertEquals(3500, TargetDatabase.find(8363).getHitpoints());
	}

	@Test
	public void nylocasRejectsWrongCombatStyleInAllModes()
	{
		for (int id : new int[]{8342, 8355, 10774, 10787, 10791, 10804, 10808})
		{
			assertTrue("Nylo melee phase " + id, TargetMechanics.isImmune(id, AttackType.MAGIC));
			assertTrue(TargetMechanics.isImmune(id, AttackType.RANGED_STANDARD));
			assertFalse(TargetMechanics.isImmune(id, AttackType.SLASH));
		}
		assertTrue(TargetMechanics.isImmune(8356, AttackType.SLASH));
		assertFalse(TargetMechanics.isImmune(8356, AttackType.MAGIC));
		assertTrue(TargetMechanics.isImmune(8357, AttackType.MAGIC));
		assertFalse(TargetMechanics.isImmune(8357, AttackType.RANGED_LIGHT));
	}
}
