package com.combatinsight.calculation;

public enum AttackStyle
{
	MELEE_ACCURATE(3, 0, 0),
	MELEE_AGGRESSIVE(0, 3, 0),
	MELEE_CONTROLLED(1, 1, 1),
	MELEE_DEFENSIVE(0, 0, 3),
	RANGED_ACCURATE(3, 0, 0),
	RANGED_RAPID(0, 0, 0),
	RANGED_LONGRANGE(0, 0, 3),
	MAGIC_ACCURATE(0, 0, 0),
	MAGIC_LONGRANGE(0, 0, 3);

	private final int attackStyleBonus;
	private final int strengthStyleBonus;
	private final int defenceStyleBonus;

	AttackStyle(int attackStyleBonus, int strengthStyleBonus, int defenceStyleBonus)
	{
		this.attackStyleBonus = attackStyleBonus;
		this.strengthStyleBonus = strengthStyleBonus;
		this.defenceStyleBonus = defenceStyleBonus;
	}

	public int getAttackStyleBonus()
	{
		return attackStyleBonus;
	}

	public int getStrengthStyleBonus()
	{
		return strengthStyleBonus;
	}

	public int getDefenceStyleBonus()
	{
		return defenceStyleBonus;
	}
}
