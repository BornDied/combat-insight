package com.combatinsight.calculation;

/** Controls where locally tracked target Defence is displayed. */
public enum TargetDefenceDisplay
{
	INFOBOX,
	HUD_ROW,
	BOTH,
	HIDDEN;

	public boolean showsInfoBox()
	{
		return this == INFOBOX || this == BOTH;
	}

	public boolean showsHudRow()
	{
		return this == HUD_ROW || this == BOTH;
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case INFOBOX:
				return "Movable infobox";
			case HUD_ROW:
				return "Advanced HUD row";
			case BOTH:
				return "Infobox and HUD row";
			default:
				return "Hidden";
		}
	}
}
