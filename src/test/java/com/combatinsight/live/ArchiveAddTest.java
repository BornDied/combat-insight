package com.combatinsight.live;

import com.combatinsight.calculation.AttackType;
import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.MagicSpell;
import com.combatinsight.calculation.TargetProfile;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArchiveAddTest
{
    @Test public void dousingACorpseChangesWeaknessAndMeleeDefence()
    {
        TargetProfile normal = TargetDatabase.find(16687);
        TargetProfile doused = TargetDatabase.find(16688);
        assertNotNull(normal);
        assertNotNull(doused);
        assertTrue(normal.hasAttribute("undead"));
        assertTrue(doused.hasAttribute("undead"));
        assertEquals(100, normal.defenceBonus(AttackType.CRUSH));
        assertEquals(-20, doused.defenceBonus(AttackType.CRUSH));
        assertEquals("33", cast(16687, MagicSpell.WATER_SURGE).getMaxHitText());
        assertEquals("24", cast(16687, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertEquals("48", cast(16688, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertEquals("24", cast(16688, MagicSpell.WATER_SURGE).getMaxHitText());
    }

    @Test public void supportedAddsCalculateWithoutInventingScaledHealth()
    {
        for (int id : new int[]{16666, 16670, 16687, 16688})
        {
            LiveCombatSnapshot snapshot = cast(id, MagicSpell.FIRE_SURGE);
            assertTrue("Missing add calculation " + id, snapshot.isDpsAvailable());
            assertTrue(snapshot.isAccuracyAvailable());
            assertFalse(snapshot.hasTargetHealth());
        }
        assertEquals("38", cast(16666, MagicSpell.FIRE_SURGE).getMaxHitText());
        assertTrue(TargetDatabase.find(16666).hasAttribute("demon"));
        // The Wiki record does not establish the undead attribute for binders.
        assertFalse(TargetDatabase.find(16670).hasAttribute("undead"));
    }

    @Test public void incompleteOcellusAddsWithholdDamageInsteadOfAssumingOrdinaryTargets()
    {
        for (int id : new int[]{16678, 16679, 16680, 16681})
        {
            assertNotNull("Missing research record " + id, TargetDatabase.find(id));
            LiveCombatSnapshot snapshot = cast(id, MagicSpell.FIRE_SURGE);
            assertFalse(snapshot.isDpsAvailable());
            assertFalse(snapshot.isAccuracyAvailable());
            assertTrue(snapshot.getMaxHitText().contains("Ocellus add"));
            assertFalse(snapshot.hasTargetHealth());
        }
        for (int id : new int[]{16671, 16672, 16673, 16675, 16682, 16683})
            assertNull("Intro/controller/debug is not the combat target " + id, TargetDatabase.find(id));
        assertTrue(cast(16674, MagicSpell.FIRE_SURGE).isDpsAvailable());
    }

    private static LiveCombatSnapshot cast(int id, MagicSpell spell)
    {
        return ArchiveMagicTest.capture(spell, 0, CombatStyleOverride.MAGIC, id);
    }
}
