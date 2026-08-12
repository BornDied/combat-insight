package com.combatinsight.calculation;

public final class CombatState
{
	private final CombatStyle combatStyle;
	private final AttackStyle attackStyle;
	private final CombatStats stats;
	private final CombatStats boosts;
	private final GearStats gear;
	private final PrayerBonuses prayers;
	private final TargetStats target;
	private final int magicBaseMaxHit;

	public CombatState(
		CombatStyle combatStyle,
		AttackStyle attackStyle,
		CombatStats stats,
		CombatStats boosts,
		GearStats gear,
		PrayerBonuses prayers,
		TargetStats target,
		int magicBaseMaxHit)
	{
		this.combatStyle = combatStyle;
		this.attackStyle = attackStyle;
		this.stats = stats;
		this.boosts = boosts;
		this.gear = gear;
		this.prayers = prayers;
		this.target = target;
		this.magicBaseMaxHit = magicBaseMaxHit;
	}

	public CombatStyle getCombatStyle()
	{
		return combatStyle;
	}

	public AttackStyle getAttackStyle()
	{
		return attackStyle;
	}

	public CombatStats getStats()
	{
		return stats;
	}

	public CombatStats getBoosts()
	{
		return boosts;
	}

	public GearStats getGear()
	{
		return gear;
	}

	public PrayerBonuses getPrayers()
	{
		return prayers;
	}

	public TargetStats getTarget()
	{
		return target;
	}

	public int getMagicBaseMaxHit()
	{
		return magicBaseMaxHit;
	}
}
