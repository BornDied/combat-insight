package com.combatinsight.live;

import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.MagicSpell;
import org.junit.Test;
import static org.junit.Assert.*;

public class WardenPhaseTest
{
    @Test public void p2AndCoreCannotReportOrdinaryAccuracyOrDamage()
    {
        for (int id : new int[]{11753, 11754, 11755, 11756, 11757, 11758})
        {
            for (CombatStyleOverride style : new CombatStyleOverride[]{CombatStyleOverride.AUTO, CombatStyleOverride.MAGIC})
            {
                LiveCombatSnapshot snapshot = ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE, 0, style, id);
                if(style==CombatStyleOverride.AUTO && TargetMechanics.isWardenP2(id))
                {
                    assertEquals("Immune",snapshot.getMaxHitText());
                    continue;
                }
                assertFalse("Unmodelled Warden distribution " + id, snapshot.isDpsAvailable());
                assertFalse(snapshot.isAccuracyAvailable());
                assertTrue(snapshot.getMaxHitText().contains("Warden"));
                assertFalse(snapshot.hasTargetHealth());
            }
        }
    }

    @Test public void chargingP3IsImmuneAndOrdinaryToaTargetsStayAvailable()
    {
        for (int id : new int[]{11763, 11764})
        {
            LiveCombatSnapshot snapshot = ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE, 0, CombatStyleOverride.MAGIC, id);
            assertEquals("Immune", snapshot.getMaxHitText());
            assertEquals("0.0%", snapshot.getHitChanceText());
            assertEquals("0.00", snapshot.getDpsText());
        }
        assertTrue(ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE, 0, CombatStyleOverride.MAGIC, 11730).isDpsAvailable());
    }
}
