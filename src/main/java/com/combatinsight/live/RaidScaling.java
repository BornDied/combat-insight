package com.combatinsight.live;

import com.combatinsight.calculation.TargetProfile;
import java.util.Objects;

/** Immutable raid inputs. Apply to bundled base stats before any local stat drains. */
public final class RaidScaling
{
	public static final RaidScaling DEFAULT = new RaidScaling(1, false, 126, 99, 99, 5);
	private final int coxSize;
	private final boolean challengeMode;
	private final int highestCombat;
	private final int highestHitpoints;
	private final int averageMining;
	private final int tobSize;

	public RaidScaling(int coxSize, boolean challengeMode, int highestCombat,
		int highestHitpoints, int averageMining, int tobSize)
	{
		this.coxSize = clamp(coxSize, 1, 100);
		this.challengeMode = challengeMode;
		this.highestCombat = clamp(highestCombat, 60, 126);
		this.highestHitpoints = clamp(highestHitpoints, 1, 99);
		this.averageMining = clamp(averageMining, 1, 99);
		this.tobSize = clamp(tobSize, 1, 5);
	}

	public TargetProfile apply(TargetProfile base)
	{
		if (base == null || base.isAmbiguous()) return base;
		if (base.hasAttribute("xerician")) return chambers(base);
		if (!base.hasAttribute("theatre_of_blood")) return base;
		int hp = base.getHitpoints();
		if (isEntryMode(base.getId()))
		{
			int[] fortieths = {0, 10, 19, 27, 34, 40};
			hp = hp * fortieths[tobSize] / 40;
		}
		else hp = hp * (Math.max(3, tobSize) + 3) / 8;
		return base.withRaidStats(base.getDefenceLevel(), base.getMagicLevel(),
			base.getHitpoints() == 0 ? 0 : Math.max(1, hp));
	}

	private TargetProfile chambers(TargetProfile base)
	{
		int id = base.getId();
		boolean cm = challengeMode || base.hasAttribute("cox_cm");
		boolean single = id == 7548 || id == 7549 || id == 7538 || id == 7539;
		boolean hand = id == 7550 || id == 7552 || id == 7553 || id == 7555;
		boolean mageHand = id == 7550 || id == 7553;
		boolean head = id == 7551 || id == 7554;
		boolean tekton = id >= 7540 && id <= 7545;
		boolean magicDefensive = hand || tekton || id == 7559 || id == 7533
			|| id >= 7530 && id <= 7532 || id == 7538 || id == 7539;
		int def = base.getDefenceLevel();
		// Both claws use the same linked defensive stat; mage claw is halved last.
		int magic = hand ? def : base.getMagicLevel();
		int hp = TargetMechanics.isGuardian(id) ? 151 + averageMining : base.getHitpoints();
		if (single)
		{
			int statPercent = Math.max(55, highestHitpoints);
			int combatPercent = highestCombat;
			if (cm)
			{
				statPercent += statPercent / 2;
				combatPercent += combatPercent / 2;
			}
			def = def == 1 ? 1 : Math.max(1, def * statPercent / 99);
			magic = magic == 1 ? 1 : Math.max(1, magic * statPercent / 99);
			hp = Math.max(5, hp * combatPercent / 126);
		}
		else
		{
			int membersBeyondSolo = coxSize - 1;
			int root = (int) Math.sqrt(membersBeyondSolo);
			int levelPercent = 55 + 44 * highestHitpoints / 99;
			int defensivePercent = 100 + root + membersBeyondSolo * 7 / 10;
			int offensivePercent = 100 + root * 7 + membersBeyondSolo;
			int cmDefencePercent = !cm || id == 7568 ? 100 : tekton ? (coxSize < 4 ? 120 : 135) : 150;
			def = scaledSkill(def, levelPercent, defensivePercent, cmDefencePercent, 20000);
			magic = scaledSkill(magic, levelPercent,
				magicDefensive ? defensivePercent : offensivePercent,
				magicDefensive ? cmDefencePercent : cm ? 150 : 100,
				magicDefensive ? 20000 : 5000);
			hp = hp * highestCombat / 126;
			hp *= 1 + coxSize / 2;
			if (cm && id != 7568) hp += hp / 2;
			hp = clamp(hp, 50, 30000);
		}
		if (hand || head)
		{
			int extra = Math.min(coxSize - 1, 50) - 3 * (Math.min(coxSize, 50) / 8);
			hp = hand ? 600 + 300 * extra : 800 + 400 * extra;
			if (mageHand) magic /= 2;
		}
		return base.withRaidStats(def, magic, hp);
	}

	private static int scaledSkill(int base, int levels, int party, int mode, int ceiling)
	{
		if (base == 1) return 1;
		return clamp(base * levels / 99 * party / 100 * mode / 100, 50, ceiling);
	}

	static boolean isEntryMode(int id)
	{
		return id == 10767 || id == 10768 || id >= 10774 && id <= 10789 || id == 10812
			|| id >= 10814 && id <= 10821 || id >= 10830 && id <= 10845
			|| id == 10864 || id == 10865;
	}

	public String notice(TargetProfile target)
	{
		if (target == null) return "";
		if (target.hasAttribute("xerician"))
			return "CoX input: " + coxSize + " players, "
				+ (challengeMode || target.hasAttribute("cox_cm") ? "CM" : "Normal")
				+ "; team stats " + highestCombat + " CB / " + highestHitpoints + " HP";
		if (target.hasAttribute("theatre_of_blood")) return "ToB input: " + tobSize + " players; local drains only";
		return "";
	}

	@Override public boolean equals(Object other)
	{
		if (!(other instanceof RaidScaling)) return false;
		RaidScaling that = (RaidScaling) other;
		return coxSize == that.coxSize && challengeMode == that.challengeMode
			&& highestCombat == that.highestCombat && highestHitpoints == that.highestHitpoints
			&& averageMining == that.averageMining && tobSize == that.tobSize;
	}

	@Override public int hashCode()
	{
		return Objects.hash(coxSize, challengeMode, highestCombat, highestHitpoints, averageMining, tobSize);
	}

	private static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }
}
