package com.combatinsight.calculation;

/**
 * RuneLite can see the equipped blowpipe, but the darts stored inside it are
 * not represented by the worn ammo slot. This small manual input prevents the
 * calculator from silently assuming the wrong ammunition.
 */
public enum BlowpipeDart
{
	NOT_SET("Not set", 0),
	BRONZE("Bronze dart", 1),
	IRON("Iron dart", 2),
	STEEL("Steel dart", 3),
	BLACK("Black dart", 6),
	MITHRIL("Mithril dart", 9),
	ADAMANT("Adamant dart", 17),
	RUNE("Rune dart", 26),
	AMETHYST("Amethyst dart", 28),
	DRAGON("Dragon dart", 35);

	private final String displayName;
	private final int rangedStrength;

	BlowpipeDart(String displayName, int rangedStrength)
	{
		this.displayName = displayName;
		this.rangedStrength = rangedStrength;
	}

	public int getRangedStrength()
	{
		return rangedStrength;
	}

	public boolean isSet()
	{
		return this != NOT_SET;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
