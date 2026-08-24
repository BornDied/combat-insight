package com.combatinsight.calculation;

import java.util.Locale;

/** Basic-attack distributions for the first supported multi-hit weapons. */
public enum MultiHitWeapon
{
	SCYTHE_OF_VITUR,
	DUAL_MACUAHUITL,
	TORAGS_HAMMERS,
	SULPHUR_BLADES,
	DARK_BOW;

	public static MultiHitWeapon forWeapon(String weaponName)
	{
		if (weaponName == null)
		{
			return null;
		}

		String weapon = weaponName.toLowerCase(Locale.ROOT);
		if (weapon.contains("scythe of vitur"))
		{
			return SCYTHE_OF_VITUR;
		}
		if (weapon.contains("dual macuahuitl"))
		{
			return DUAL_MACUAHUITL;
		}
		if (weapon.contains("torag's hammers"))
		{
			return TORAGS_HAMMERS;
		}
		if (weapon.contains("sulphur blades"))
		{
			return SULPHUR_BLADES;
		}
		if (weapon.contains("dark bow"))
		{
			return DARK_BOW;
		}
		return null;
	}

	public MultiHitResult calculate(
		int minimumHit,
		int maximumHit,
		int flatArmour,
		double hitChance,
		int targetSize,
		int attackSpeedTicks,
		boolean guaranteedMaximum,
		boolean fullBloodMoonSet)
	{
		int maximum = Math.max(0, maximumHit);
		int minimum = Math.max(0, Math.min(minimumHit, maximum));
		double chance = Math.max(0.0, Math.min(1.0, hitChance));
		int[] rollMaximums = rollMaximums(maximum, targetSize);
		if (maximum == 0)
		{
			return new MultiHitResult(new int[rollMaximums.length], 0.0, 0.0, attackSpeedTicks);
		}

		int[] transformedMaximums = new int[rollMaximums.length];
		double[] averageHits = new double[rollMaximums.length];

		for (int i = 0; i < rollMaximums.length; i++)
		{
			int rollMaximum = Math.max(minimum, rollMaximums[i]);
			transformedMaximums[i] = DamageRoll.maximumSuccessfulHit(rollMaximum, flatArmour);
			averageHits[i] = DamageRoll.averageSuccessfulHit(minimum, rollMaximum, flatArmour);
		}

		double expectedDamage;
		double successfulAttackChance;
		if (guaranteedMaximum)
		{
			expectedDamage = sum(transformedMaximums);
			successfulAttackChance = 1.0;
			chance = 1.0;
		}
		else
		{
			switch (this)
			{
				case DUAL_MACUAHUITL:
					expectedDamage = chance * averageHits[0]
						+ chance * chance * averageHits[1];
					successfulAttackChance = chance;
					break;
				case SCYTHE_OF_VITUR:
					expectedDamage = chance * sum(averageHits);
					successfulAttackChance = 1.0 - Math.pow(1.0 - chance, averageHits.length);
					break;
				case TORAGS_HAMMERS:
				case SULPHUR_BLADES:
				case DARK_BOW:
				default:
					expectedDamage = chance * sum(averageHits);
					successfulAttackChance = 1.0 - Math.pow(1.0 - chance, averageHits.length);
					break;
			}
		}

		double expectedSpeed = Math.max(1, attackSpeedTicks);
		if (this == DUAL_MACUAHUITL && fullBloodMoonSet)
		{
			// Each accurate hitsplat has a one-third chance to shorten the next
			// attack by one tick. The second hit can only be accurate after the first.
			double speedupChance = chance / 3.0 + 2.0 * chance * chance / 9.0;
			expectedSpeed = Math.max(1.0, expectedSpeed - speedupChance);
		}

		return new MultiHitResult(
			transformedMaximums,
			expectedDamage,
			successfulAttackChance,
			expectedSpeed);
	}

	private int[] rollMaximums(int maximumHit, int targetSize)
	{
		switch (this)
		{
			case SCYTHE_OF_VITUR:
				int hitCount = Math.min(3, Math.max(1, targetSize));
				int[] scytheHits = new int[hitCount];
				for (int i = 0; i < hitCount; i++)
				{
					scytheHits[i] = maximumHit / (1 << i);
				}
				return scytheHits;
			case DUAL_MACUAHUITL:
			case TORAGS_HAMMERS:
			case SULPHUR_BLADES:
				int firstMaximum = maximumHit / 2;
				return new int[]{firstMaximum, maximumHit - firstMaximum};
			case DARK_BOW:
			default:
				return new int[]{maximumHit, maximumHit};
		}
	}

	private static int sum(int[] values)
	{
		int total = 0;
		for (int value : values)
		{
			total += value;
		}
		return total;
	}

	private static double sum(double[] values)
	{
		double total = 0.0;
		for (double value : values)
		{
			total += value;
		}
		return total;
	}
}
