package com.combatinsight;

/** Owned by Swing EDT. Freezing never changes the game's live calculation. */
final class InspectorSession
{
    private InspectorDocument latest;
    private InspectorDocument frozen;
    void update(InspectorDocument document) { latest=document; }
    InspectorDocument displayed() { return frozen == null ? latest : frozen; }
    boolean isFrozen() { return frozen != null; }
    void toggleFreeze() { frozen = frozen == null ? latest : null; }
    void clear() { latest=null; frozen=null; }
}
