package com.combatinsight.calculation;

import java.awt.Color;

/** Presets never replace the separately saved custom background color. */
public enum HudBackgroundMode
{
	CUSTOM("Custom color"),
	SUBTLE("Subtle"),
	TEXT_ONLY("Text only");

	private final String label;

	HudBackgroundMode(String label)
	{
		this.label = label;
	}

	public Color resolve(Color customColor)
	{
		switch (this)
		{
			case SUBTLE: return new Color(20, 20, 20, 120);
			case TEXT_ONLY: return new Color(0, 0, 0, 0);
			default: return customColor;
		}
	}

	@Override public String toString() { return label; }
}
