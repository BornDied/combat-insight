package com.combatinsight.live;

/** Calculation policy, independent of the visibility of target stat rows/infoboxes. */
public enum DefenceTrackingMode
{
    OFF("Off (base stats)"),
    LOCAL("Track my reductions");

    private final String label;
    DefenceTrackingMode(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
