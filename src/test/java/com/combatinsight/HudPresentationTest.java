package com.combatinsight;

import com.combatinsight.calculation.SpecialAttackWeapon;
import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;

public class HudPresentationTest
{
    private final Color cyan = new Color(0x42D9FF);

    @Test public void designatedWeaponHighlightSurvivesQuietColors()
    {
        assertEquals(cyan, HudPresentation.specialColor(SpecialAttackWeapon.ZARYTE_CROSSBOW, true, true, cyan, Color.WHITE));
        assertEquals(cyan, HudPresentation.specialColor(SpecialAttackWeapon.DRAGON_CLAWS, true, true, cyan, Color.WHITE));
        assertEquals(Color.WHITE, HudPresentation.specialColor(SpecialAttackWeapon.DARK_BOW, true, true, cyan, Color.WHITE));
    }

    @Test public void unavailableSpecialAndOptOutKeepStatusColor()
    {
        assertEquals(Color.ORANGE, HudPresentation.specialColor(SpecialAttackWeapon.VOIDWAKER, false, true, cyan, Color.ORANGE));
        assertEquals(Color.WHITE, HudPresentation.specialColor(SpecialAttackWeapon.DRAGON_DAGGER, true, false, cyan, Color.WHITE));
        assertEquals(Color.WHITE, HudPresentation.specialColor(null, true, true, cyan, Color.WHITE));
    }

    @Test public void missingTargetUsesDashButImmuneRemainsExplicit()
    {
        assertEquals("—", HudPresentation.value("Select target"));
        assertEquals("—", HudPresentation.value("Select resolved target"));
        assertEquals("Immune", HudPresentation.value("Immune"));
        assertEquals("Ruby: player HP too low", HudPresentation.value("Ruby: player HP too low"));
    }
}
