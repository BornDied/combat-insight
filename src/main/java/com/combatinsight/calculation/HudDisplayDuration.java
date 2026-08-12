package com.combatinsight.calculation;

/**
 * Controls how long the live HUD remains visible when the player is not in
 * combat. The permanent option preserves the always-visible max-hit display.
 */
public enum HudDisplayDuration
{
	PERMANENT,
	FIFTEEN_SECONDS,
	THIRTY_SECONDS;

	@Override
	public String toString()
	{
		switch (this)
		{
			case FIFTEEN_SECONDS:
				return "15 seconds after combat";
			case THIRTY_SECONDS:
				return "30 seconds after combat";
		default:
				return "Permanent";
		}
	}
}
