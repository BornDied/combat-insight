package com.combatinsight.live;

final class ArchiveEquipmentRules
{
    private ArchiveEquipmentRules() { }
    static String unresolved(String weapon, String ammo, int tomeStacks)
    {
        if (weapon.contains("obligator") && weapon.contains("uncharged")) return "Charge Obligator";
        if (weapon.contains("ascension crossbows"))
        {
            if (ammo.isEmpty()) return "Ascension bolts required";
            if (ammo.contains("(e)") && !(ammo.contains("diamond") || ammo.contains("onyx")))
                return "Ascension proc data pending";
        }
        if (weapon.contains("zorya's tome") && (tomeStacks < 0 || tomeStacks > 3)) return "Tome stacks unavailable";
        return "";
    }
}
