package com.combatinsight.live;

import com.combatinsight.calculation.CombatStyleOverride;
import com.combatinsight.calculation.MagicSpell;
import org.junit.Test;
import static org.junit.Assert.*;

public class VerzikAddUncertaintyTest
{
    @Test public void disputedVerzikAddsCannotSupplyHpOrDamageFromUnverifiedMagicStats()
    {
        for (int id : new int[]{8381,8382,8383,10841,10842,10843,10858,10859,10860})
        {
            assertTrue("Stats need reconciliation " + id, TargetDatabase.find(id).isAmbiguous());
            LiveCombatSnapshot snapshot = ArchiveMagicTest.capture(MagicSpell.FIRE_SURGE, 0, CombatStyleOverride.MAGIC, id);
            assertFalse(snapshot.isDpsAvailable());
            assertFalse(snapshot.isAccuracyAvailable());
            assertFalse(snapshot.hasTargetHealth());
            assertTrue(snapshot.getMaxHitText().contains("Verzik add"));
        }
    }
}
