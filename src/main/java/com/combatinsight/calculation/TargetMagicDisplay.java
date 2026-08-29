package com.combatinsight.calculation;

/** Controls where locally tracked Magic-related target stats are displayed. */
public enum TargetMagicDisplay
{
	INFOBOX,
	HUD_ROWS,
	BOTH,
	HIDDEN;

	public boolean showsInfoBox()
	{
		return this == INFOBOX || this == BOTH;
	}

	public boolean showsHudRows()
	{
		return this == HUD_ROWS || this == BOTH;
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case INFOBOX:
				return "Magic-only infobox";
			case HUD_ROWS:
				return "Magic-only HUD rows";
			case BOTH:
				return "Infobox and HUD rows";
			default:
				return "Hidden";
		}
	}
}
