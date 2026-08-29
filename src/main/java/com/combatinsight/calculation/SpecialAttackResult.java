package com.combatinsight.calculation;

import java.util.Arrays;
import java.util.Locale;

/** Immutable HUD-ready summary of one supported special attack. */
public final class SpecialAttackResult
{
	private final SpecialAttackWeapon weapon;
	private final boolean maximumAvailable;
	private final boolean accuracyAvailable;
	private final boolean expectedDamageAvailable;
	private final boolean targetImmune;
	private final boolean guaranteedAccuracy;
	private final int[] maximumHits;
	private final int totalMaximumHit;
	private final double hitChance;
	private final double expectedDamage;
	private final String maximumStatus;
	private final String accuracyStatus;
	private final String expectedDamageStatus;
	private final String onHitText;

	SpecialAttackResult(
		SpecialAttackWeapon weapon,
		boolean maximumAvailable,
		boolean accuracyAvailable,
		boolean expectedDamageAvailable,
		boolean targetImmune,
		boolean guaranteedAccuracy,
		int[] maximumHits,
		double hitChance,
		double expectedDamage,
		String maximumStatus,
		String accuracyStatus,
		String expectedDamageStatus)
	{
		this(
			weapon,
			maximumAvailable,
			accuracyAvailable,
			expectedDamageAvailable,
			targetImmune,
			guaranteedAccuracy,
			maximumHits,
			hitChance,
			expectedDamage,
			maximumStatus,
			accuracyStatus,
			expectedDamageStatus,
			weapon == null ? "" : weapon.getOnHitText());
	}

	SpecialAttackResult(
		SpecialAttackWeapon weapon,
		boolean maximumAvailable,
		boolean accuracyAvailable,
		boolean expectedDamageAvailable,
		boolean targetImmune,
		boolean guaranteedAccuracy,
		int[] maximumHits,
		double hitChance,
		double expectedDamage,
		String maximumStatus,
		String accuracyStatus,
		String expectedDamageStatus,
		String onHitText)
	{
		this.weapon = weapon;
		this.maximumAvailable = maximumAvailable;
		this.accuracyAvailable = accuracyAvailable;
		this.expectedDamageAvailable = expectedDamageAvailable;
		this.targetImmune = targetImmune;
		this.guaranteedAccuracy = guaranteedAccuracy;
		this.maximumHits = Arrays.copyOf(maximumHits, maximumHits.length);
		int total = 0;
		for (int maximumHit : maximumHits)
		{
			total += Math.max(0, maximumHit);
		}
		this.totalMaximumHit = total;
		this.hitChance = Math.max(0.0, Math.min(1.0, hitChance));
		this.expectedDamage = Math.max(0.0, expectedDamage);
		this.maximumStatus = safe(maximumStatus);
		this.accuracyStatus = safe(accuracyStatus);
		this.expectedDamageStatus = safe(expectedDamageStatus);
		this.onHitText = safe(onHitText);
	}

	public static SpecialAttackResult noWeapon()
	{
		return new SpecialAttackResult(
			null, false, false, false, false, false, new int[0], 0.0, 0.0,
			"", "", "");
	}

	static SpecialAttackResult unavailable(SpecialAttackWeapon weapon, String status)
	{
		String safeStatus = safe(status);
		return new SpecialAttackResult(
			weapon, false, false, false, false, false, new int[0], 0.0, 0.0,
			safeStatus, safeStatus, safeStatus);
	}

	static SpecialAttackResult immune(SpecialAttackWeapon weapon)
	{
		return new SpecialAttackResult(
			weapon, false, true, true, true, false, new int[0], 0.0, 0.0,
			"Immune", "0.0%", "0.00");
	}

	public boolean hasSupportedWeapon()
	{
		return weapon != null;
	}

	public SpecialAttackWeapon getWeapon()
	{
		return weapon;
	}

	public boolean isMaximumAvailable()
	{
		return maximumAvailable;
	}

	public boolean isAccuracyAvailable()
	{
		return accuracyAvailable;
	}

	public boolean isExpectedDamageAvailable()
	{
		return expectedDamageAvailable;
	}

	public boolean isTargetImmune()
	{
		return targetImmune;
	}

	public int[] getMaximumHits()
	{
		return Arrays.copyOf(maximumHits, maximumHits.length);
	}

	public int getTotalMaximumHit()
	{
		return totalMaximumHit;
	}

	public double getHitChance()
	{
		return hitChance;
	}

	public double getExpectedDamage()
	{
		return expectedDamage;
	}

	public String getMaximumHitText(boolean includeSplit)
	{
		if (!maximumAvailable)
		{
			return maximumStatus;
		}
		if (!includeSplit || maximumHits.length <= 1)
		{
			return Integer.toString(totalMaximumHit);
		}

		StringBuilder split = new StringBuilder();
		for (int i = 0; i < maximumHits.length; i++)
		{
			if (i > 0)
			{
				split.append('/');
			}
			split.append(maximumHits[i]);
		}
		return totalMaximumHit + " (" + split + ")";
	}

	public String getHitChanceText()
	{
		if (!accuracyAvailable)
		{
			return accuracyStatus;
		}
		if (guaranteedAccuracy)
		{
			return "Guaranteed";
		}

		// A very high multi-roll chance can round to 100.0% even though it can
		// still miss. Reserve guaranteed wording for mechanics that truly are.
		double displayPercent = Math.min(99.9, hitChance * 100.0);
		return String.format(Locale.ROOT, "%.1f%%", displayPercent);
	}

	public String getExpectedDamageText()
	{
		return expectedDamageAvailable
			? String.format(Locale.ROOT, "%.2f", expectedDamage)
			: expectedDamageStatus;
	}

	public String getOnHitText()
	{
		return onHitText;
	}

	private static String safe(String value)
	{
		return value == null ? "" : value;
	}
}
