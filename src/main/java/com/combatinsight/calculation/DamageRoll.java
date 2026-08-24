package com.combatinsight.calculation;

/**
 * Shared successful-damage-roll transforms.
 *
 * <p>Modern OSRS raises an accurate zero damage roll to one before applying
 * non-Magic flat armour. Flat armour can be negative, in which case it adds
 * damage to every accurate hitsplat.</p>
 */
public final class DamageRoll
{
	private DamageRoll()
	{
	}

	/** Models a damage roll that is already known to be accurate. */
	public static double averageSuccessfulHit(int minimumHit, int maximumHit, int flatArmour)
	{
		int maximum = Math.max(0, maximumHit);
		int minimum = Math.max(0, Math.min(minimumHit, maximum));
		long total = 0L;
		for (int roll = minimum; roll <= maximum; roll++)
		{
			total += transformSuccessfulHit(roll, flatArmour);
		}
		return (double) total / (maximum - minimum + 1);
	}

	/** A zero maximum is valid here when it belongs to an accurate split hitsplat. */
	public static int maximumSuccessfulHit(int maximumHit, int flatArmour)
	{
		return maximumHit < 0 ? 0 : transformSuccessfulHit(maximumHit, flatArmour);
	}

	public static int transformSuccessfulHit(int rolledDamage, int flatArmour)
	{
		long raisedDamage = Math.max(1, rolledDamage);
		long transformed = Math.max(0L, raisedDamage - flatArmour);
		return (int) Math.min(Integer.MAX_VALUE, transformed);
	}
}
