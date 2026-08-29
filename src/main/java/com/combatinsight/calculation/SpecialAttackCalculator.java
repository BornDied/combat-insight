package com.combatinsight.calculation;

/** Pure formulas for supported special-attack weapon waves. */
public final class SpecialAttackCalculator
{
	private SpecialAttackCalculator()
	{
	}

	public static SpecialAttackResult calculate(
		SpecialAttackWeapon weapon,
		int baseMaximumHit,
		boolean baseMaximumAvailable,
		int baseAttackRoll,
		int targetDefenceRoll,
		boolean targetAvailable,
		int flatArmour,
		boolean targetImmune,
		boolean guaranteedAccuracy,
		boolean guaranteedMaximum,
		boolean darkBowAmmoAvailable,
		boolean dragonArrows,
		boolean seercullAmmoAvailable,
		int targetDefenceLevel,
		int targetMagicLevel,
		int targetDefenceFloor,
		int specialMaximumHitOverride,
		boolean boneDaggerGuaranteed)
	{
		if (weapon == null)
		{
			return SpecialAttackResult.noWeapon();
		}
		if (!baseMaximumAvailable)
		{
			return SpecialAttackResult.unavailable(weapon, "Max hit unavailable");
		}
		if (weapon == SpecialAttackWeapon.DARK_BOW && !darkBowAmmoAvailable)
		{
			return SpecialAttackResult.unavailable(weapon, "Equip 2 arrows");
		}
		if (weapon == SpecialAttackWeapon.SEERCULL && !seercullAmmoAvailable)
		{
			return SpecialAttackResult.unavailable(weapon, "Equip arrows");
		}
		if (targetAvailable && targetImmune)
		{
			return SpecialAttackResult.immune(weapon);
		}

		int specialAttackRoll = applyAccuracyModifier(weapon, baseAttackRoll);
		boolean trulyGuaranteedAccuracy = targetAvailable
			&& (guaranteedAccuracy
				|| weapon == SpecialAttackWeapon.VOIDWAKER
				|| weapon == SpecialAttackWeapon.SEERCULL
				|| weapon == SpecialAttackWeapon.BONE_DAGGER && boneDaggerGuaranteed);
		double perRollChance = targetAvailable
			? trulyGuaranteedAccuracy
				? 1.0
				: CombatPrediction.hitChance(specialAttackRoll, targetDefenceRoll)
			: 0.0;
		int applicableFlatArmour = targetAvailable && weapon != SpecialAttackWeapon.VOIDWAKER
			? flatArmour : 0;
		DamageSummary damage = damageSummary(
			weapon,
			Math.max(0, baseMaximumHit),
			applicableFlatArmour,
			perRollChance,
			dragonArrows,
			specialAttackRoll,
			targetDefenceRoll,
			targetDefenceLevel,
			targetMagicLevel,
			targetDefenceFloor,
			specialMaximumHitOverride);

		double successfulAttackChance = successfulAttackChance(weapon, perRollChance);
		String targetStatus = targetAvailable ? "" : "Select target";
		double expectedDamage = guaranteedMaximum
			? damage.expectedDamageAtMaximum
			: damage.expectedDamage;
		String onHitText = weapon == SpecialAttackWeapon.DARK_BOW
			? dragonArrows ? "Min 8, max 48" : "Min 5"
			: weapon.getOnHitText();
		return new SpecialAttackResult(
			weapon,
			true,
			targetAvailable,
			targetAvailable,
			false,
			trulyGuaranteedAccuracy,
			damage.maximumHits,
			successfulAttackChance,
			targetAvailable ? expectedDamage : 0.0,
			"",
			targetStatus,
			targetStatus,
			onHitText);
	}

	private static int applyAccuracyModifier(SpecialAttackWeapon weapon, int attackRoll)
	{
		switch (weapon)
		{
			case ELDER_MAUL:
				return scale(attackRoll, 5, 4);
			case BANDOS_GODSWORD:
			case ARMADYL_GODSWORD:
			case SARADOMIN_GODSWORD:
			case ZAMORAK_GODSWORD:
			case ANCIENT_GODSWORD:
				return scale(attackRoll, 2, 1);
			case DRAGON_DAGGER:
				return scale(attackRoll, 23, 20);
			case TONALZTICS_OF_RALOS:
			case ACCURSED_SCEPTRE:
				return scale(attackRoll, 3, 2);
			case EYE_OF_AYAK:
				return scale(attackRoll, 2, 1);
			case DRAGON_WARHAMMER:
			case BURNING_CLAWS:
			case DRAGON_CLAWS:
			case VOIDWAKER:
			case DARK_BOW:
			case BONE_DAGGER:
			case ARCLIGHT:
			case EMBERLIGHT:
			case SEERCULL:
			default:
				return attackRoll;
		}
	}

	private static double successfulAttackChance(SpecialAttackWeapon weapon, double perRollChance)
	{
		double chance = Math.max(0.0, Math.min(1.0, perRollChance));
		switch (weapon)
		{
			case BURNING_CLAWS:
				return 1.0 - Math.pow(1.0 - chance, 3);
			case DRAGON_CLAWS:
				return 1.0 - Math.pow(1.0 - chance, 4);
			case DRAGON_DAGGER:
			case DARK_BOW:
			case TONALZTICS_OF_RALOS:
				return 1.0 - Math.pow(1.0 - chance, 2);
			case VOIDWAKER:
			case SEERCULL:
				return 1.0;
			case DRAGON_WARHAMMER:
			case ELDER_MAUL:
			case BANDOS_GODSWORD:
			case ARMADYL_GODSWORD:
			case SARADOMIN_GODSWORD:
			case ZAMORAK_GODSWORD:
			case ANCIENT_GODSWORD:
			case EYE_OF_AYAK:
			case ACCURSED_SCEPTRE:
			case BONE_DAGGER:
			case ARCLIGHT:
			case EMBERLIGHT:
			default:
				return chance;
		}
	}

	private static DamageSummary damageSummary(
		SpecialAttackWeapon weapon,
		int baseMaximumHit,
		int flatArmour,
		double perRollChance,
		boolean dragonArrows,
		int specialAttackRoll,
		int targetDefenceRoll,
		int targetDefenceLevel,
		int targetMagicLevel,
		int targetDefenceFloor,
		int specialMaximumHitOverride)
	{
		switch (weapon)
		{
			case DRAGON_WARHAMMER:
				return singleHit(scale(baseMaximumHit, 3, 2), 0, flatArmour, perRollChance);
			case ELDER_MAUL:
				return singleHit(baseMaximumHit, 0, flatArmour, perRollChance);
			case BANDOS_GODSWORD:
				int godswordMaximum = scale(baseMaximumHit, 11, 10);
				godswordMaximum = scale(godswordMaximum, 11, 10);
				return singleHit(godswordMaximum, 0, flatArmour, perRollChance);
			case ARMADYL_GODSWORD:
				return singleHit(scale(baseMaximumHit, 5, 4), 0, flatArmour, perRollChance);
			case SARADOMIN_GODSWORD:
			case ZAMORAK_GODSWORD:
				return singleHit(scale(baseMaximumHit, 11, 10), 0, flatArmour, perRollChance);
			case ANCIENT_GODSWORD:
				return ancientGodsword(baseMaximumHit, flatArmour, perRollChance);
			case BURNING_CLAWS:
				return burningClaws(baseMaximumHit, flatArmour, perRollChance);
			case DRAGON_CLAWS:
				return dragonClaws(baseMaximumHit, flatArmour, perRollChance);
			case VOIDWAKER:
				int voidwakerMinimum = baseMaximumHit / 2;
				return singleHit(
					baseMaximumHit + voidwakerMinimum,
					voidwakerMinimum,
					0,
					1.0);
			case DRAGON_DAGGER:
				return independentHits(
					2,
					scale(baseMaximumHit, 23, 20),
					0,
					flatArmour,
					perRollChance);
			case DARK_BOW:
				int darkBowMinimum = dragonArrows ? 8 : 5;
				int darkBowMaximum = scale(baseMaximumHit, dragonArrows ? 15 : 13, 10);
				if (dragonArrows)
				{
					darkBowMaximum = Math.min(48, darkBowMaximum);
				}
				darkBowMaximum = Math.max(darkBowMinimum, darkBowMaximum);
				return independentHits(
					2,
					darkBowMaximum,
					darkBowMinimum,
					flatArmour,
					perRollChance);
			case TONALZTICS_OF_RALOS:
				return tonalztics(
					baseMaximumHit,
					flatArmour,
					perRollChance,
					specialAttackRoll,
					targetDefenceRoll,
					targetDefenceLevel,
					targetMagicLevel,
					targetDefenceFloor);
			case EYE_OF_AYAK:
				return singleHit(
					overrideOrScale(specialMaximumHitOverride, baseMaximumHit, 13, 10),
					0,
					flatArmour,
					perRollChance);
			case ACCURSED_SCEPTRE:
				return singleHit(
					overrideOrScale(specialMaximumHitOverride, baseMaximumHit, 3, 2),
					0,
					flatArmour,
					perRollChance);
			case SEERCULL:
				return singleHit(
					Math.max(0, specialMaximumHitOverride),
					0,
					flatArmour,
					1.0);
			case BONE_DAGGER:
			case ARCLIGHT:
			case EMBERLIGHT:
			default:
				return singleHit(baseMaximumHit, 0, flatArmour, perRollChance);
		}
	}

	private static DamageSummary tonalztics(
		int baseMaximumHit,
		int flatArmour,
		double firstHitChance,
		int attackRoll,
		int defenceRoll,
		int targetDefenceLevel,
		int targetMagicLevel,
		int targetDefenceFloor)
	{
		int perHitMaximum = scale(baseMaximumHit, 3, 4);
		DamageSummary firstHit = singleHit(perHitMaximum, 0, flatArmour, firstHitChance);
		int defenceDrain = Math.max(0, targetMagicLevel) / 8;
		int reducedDefence = Math.max(
			Math.max(0, targetDefenceFloor),
			targetDefenceLevel - defenceDrain);
		int reducedDefenceRoll = targetDefenceLevel <= 0
			? defenceRoll
			: scale(defenceRoll, reducedDefence + 9, targetDefenceLevel + 9);
		double secondChanceAfterHit = CombatPrediction.hitChance(attackRoll, reducedDefenceRoll);
		double secondHitChance = firstHitChance * secondChanceAfterHit
			+ (1.0 - firstHitChance) * firstHitChance;
		DamageSummary secondHit = singleHit(perHitMaximum, 0, flatArmour, secondHitChance);
		return new DamageSummary(
			new int[]{firstHit.maximumHits[0], secondHit.maximumHits[0]},
			firstHit.expectedDamage + secondHit.expectedDamage);
	}

	private static DamageSummary ancientGodsword(
		int baseMaximumHit,
		int flatArmour,
		double hitChance)
	{
		DamageSummary direct = singleHit(
			scale(baseMaximumHit, 11, 10),
			0,
			flatArmour,
			hitChance);
		return new DamageSummary(
			new int[]{direct.maximumHits[0], 25},
			direct.expectedDamage + 25.0 * hitChance);
	}

	private static DamageSummary singleHit(
		int maximumHit,
		int minimumHit,
		int flatArmour,
		double hitChance)
	{
		int maximum = Math.max(0, maximumHit);
		int minimum = Math.max(0, Math.min(minimumHit, maximum));
		int transformedMaximum = maximum <= 0
			? 0
			: DamageRoll.maximumSuccessfulHit(maximum, flatArmour);
		double average = maximum <= 0
			? 0.0
			: DamageRoll.averageSuccessfulHit(minimum, maximum, flatArmour);
		return new DamageSummary(new int[]{transformedMaximum}, average * hitChance);
	}

	private static DamageSummary independentHits(
		int hitCount,
		int maximumHit,
		int minimumHit,
		int flatArmour,
		double hitChance)
	{
		DamageSummary oneHit = singleHit(maximumHit, minimumHit, flatArmour, hitChance);
		int[] maximumHits = new int[hitCount];
		for (int i = 0; i < hitCount; i++)
		{
			maximumHits[i] = oneHit.maximumHits[0];
		}
		return new DamageSummary(maximumHits, oneHit.expectedDamage * hitCount);
	}

	private static DamageSummary dragonClaws(
		int maximumHit,
		int flatArmour,
		double accuracy)
	{
		double chance = Math.max(0.0, Math.min(1.0, accuracy));
		double expectedDamage = 0.0;
		int[] maximumSplit = new int[]{0, 0, 0, 0};
		int maximumTotal = -1;

		for (int accuracyRoll = 0; accuracyRoll < 4; accuracyRoll++)
		{
			int low = maximumHit * (4 - accuracyRoll) / 4;
			int high = maximumHit + low - 1;
			if (high < low)
			{
				continue;
			}

			double branchChance = Math.pow(1.0 - chance, accuracyRoll) * chance;
			double chancePerDamage = branchChance / (high - low + 1.0);
			for (int damage = low; damage <= high; damage++)
			{
				int[] split = clawSplit(accuracyRoll, damage);
				int accurateHits = 4 - accuracyRoll;
				int total = 0;
				for (int i = 0; i < accurateHits; i++)
				{
					split[i] = transformClawHit(split[i], flatArmour);
					total += split[i];
				}
				expectedDamage += chancePerDamage * total;
				if (total > maximumTotal)
				{
					maximumTotal = total;
					maximumSplit = split;
				}
			}
		}

		// When all four accuracy rolls fail, claws have a two-thirds chance
		// to deal two inaccurate 1-damage splats. Flat armour does not alter
		// those inaccurate fallback splats.
		double allRollsFail = Math.pow(1.0 - chance, 4);
		expectedDamage += allRollsFail * 4.0 / 3.0;
		return new DamageSummary(maximumSplit, expectedDamage);
	}

	private static DamageSummary burningClaws(
		int maximumHit,
		int flatArmour,
		double accuracy)
	{
		double chance = Math.max(0.0, Math.min(1.0, accuracy));
		double expectedDamage = 0.0;
		int[] maximumSplit = new int[]{0, 0, 0};
		int maximumTotal = -1;
		double maximumBranchBurn = 0.0;

		for (int accuracyRoll = 0; accuracyRoll < 3; accuracyRoll++)
		{
			int low = maximumHit * (3 - accuracyRoll) / 4;
			int high = maximumHit + low;
			double branchChance = Math.pow(1.0 - chance, accuracyRoll) * chance;
			double chancePerDamage = branchChance / (high - low + 1.0);
			double burnChance = 0.15 * (accuracyRoll + 1);
			for (int damage = low; damage <= high; damage++)
			{
				int[] split = burningClawSplit(accuracyRoll, damage);
				int total = 0;
				for (int i = 0; i < split.length; i++)
				{
					split[i] = transformClawHit(split[i], flatArmour);
					total += split[i];
				}
				expectedDamage += chancePerDamage * total;
				if (total > maximumTotal)
				{
					maximumTotal = total;
					maximumSplit = split;
					maximumBranchBurn = expectedBurnDamage(burnChance);
				}
			}

			// Every accurate hitsplat independently rolls a burn. When the first
			// two both burn, their first damage tick currently overlaps and loses
			// one damage, so the branch expectation is 30p - p^2.
			expectedDamage += branchChance * expectedBurnDamage(burnChance);
		}

		// When all three rolls fail, direct damage is 0 with 20% chance, 1
		// with 40% chance, and 2 with 40% chance. Miss branches cannot burn.
		expectedDamage += Math.pow(1.0 - chance, 3) * 1.2;
		return new DamageSummary(
			maximumSplit,
			expectedDamage,
			Math.max(0, maximumTotal) + maximumBranchBurn);
	}

	private static int[] burningClawSplit(int accuracyRoll, int damage)
	{
		switch (accuracyRoll)
		{
			case 0:
				return new int[]{damage / 4, damage / 4, damage / 2};
			case 1:
				return new int[]{Math.max(0, damage / 2 - 1), Math.max(0, damage / 2 - 1), 2};
			case 2:
			default:
				return new int[]{1, 1, Math.max(0, damage - 2)};
		}
	}

	private static double expectedBurnDamage(double burnChance)
	{
		double chance = Math.max(0.0, Math.min(1.0, burnChance));
		return 30.0 * chance - chance * chance;
	}

	private static int[] clawSplit(int accuracyRoll, int damage)
	{
		switch (accuracyRoll)
		{
			case 0:
				return new int[]{damage / 2, damage / 4, damage / 8, damage / 8 + 1};
			case 1:
				return new int[]{damage / 2, damage / 4, damage / 4 + 1, 0};
			case 2:
				return new int[]{damage / 2, damage / 2 + 1, 0, 0};
			case 3:
			default:
				return new int[]{damage + 1, 0, 0, 0};
		}
	}

	private static int transformClawHit(int rolledDamage, int flatArmour)
	{
		if (rolledDamage <= 0)
		{
			return 0;
		}
		long transformed = Math.max(0L, (long) rolledDamage - flatArmour);
		return (int) Math.min(Integer.MAX_VALUE, transformed);
	}

	private static int scale(int value, int numerator, int denominator)
	{
		return denominator == 0 ? value : (int) ((long) value * numerator / denominator);
	}

	private static int overrideOrScale(int override, int value, int numerator, int denominator)
	{
		return override >= 0 ? override : scale(value, numerator, denominator);
	}

	private static int sum(int[] values)
	{
		int total = 0;
		for (int value : values)
		{
			total += Math.max(0, value);
		}
		return total;
	}

	private static final class DamageSummary
	{
		private final int[] maximumHits;
		private final double expectedDamage;
		private final double expectedDamageAtMaximum;

		private DamageSummary(int[] maximumHits, double expectedDamage)
		{
			this(maximumHits, expectedDamage, sum(maximumHits));
		}

		private DamageSummary(
			int[] maximumHits,
			double expectedDamage,
			double expectedDamageAtMaximum)
		{
			this.maximumHits = maximumHits;
			this.expectedDamage = expectedDamage;
			this.expectedDamageAtMaximum = expectedDamageAtMaximum;
		}
	}
}
