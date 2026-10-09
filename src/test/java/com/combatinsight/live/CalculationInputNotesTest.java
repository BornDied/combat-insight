package com.combatinsight.live;

import com.combatinsight.BuildInfo;
import com.combatinsight.calculation.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalculationInputNotesTest
{
    @Test public void savedSpellIsExplicitlyIgnoredOnMeleeAndPoweredMagic()
    {
        String melee = String.join("; ", CalculationInputNotes.forSelection(CombatStyleOverride.AUTO,
            MagicSpell.FIRE_SURGE, CombatStyle.MELEE, true, false, BlowpipeDart.ADAMANT));
        assertTrue(melee.contains("Saved spell ignored"));
        String shadow = String.join("; ", CalculationInputNotes.forSelection(CombatStyleOverride.AUTO,
            MagicSpell.FIRE_SURGE, CombatStyle.MAGIC, false, false, BlowpipeDart.ADAMANT));
        assertTrue(shadow.contains("Saved spell ignored"));
        assertFalse(shadow.contains("Manual spell in use"));
    }
    @Test public void manualCastingExplainsStyleAndSpellWhileOrdinaryAutomaticMeleeIsQuiet()
    {
        String forced = String.join("; ", CalculationInputNotes.forSelection(CombatStyleOverride.MAGIC,
            MagicSpell.FIRE_SURGE, CombatStyle.MAGIC, true, false, BlowpipeDart.ADAMANT));
        assertTrue(forced.contains("Combat type forced"));
        assertTrue(forced.contains("Manual spell in use: Fire Surge"));
        assertTrue(CalculationInputNotes.forSelection(CombatStyleOverride.AUTO,
            MagicSpell.NOT_SET, CombatStyle.MELEE, true, false, BlowpipeDart.ADAMANT).isEmpty());
    }
    @Test public void dartInputOnlyAppearsWhileFiringBlowpipe()
    {
        assertEquals(1, CalculationInputNotes.forSelection(CombatStyleOverride.AUTO,
            MagicSpell.NOT_SET, CombatStyle.RANGED, true, true, BlowpipeDart.ADAMANT).size());
        assertTrue(CalculationInputNotes.forSelection(CombatStyleOverride.AUTO,
            MagicSpell.NOT_SET, CombatStyle.MELEE, true, true, BlowpipeDart.ADAMANT).isEmpty());
    }
    @Test public void emptyDiagnosticIdentifiesBuildAndDoesNotShowHudMarker()
    {
        assertTrue(LiveCombatSnapshot.empty().diagnosticReport().startsWith("Combat Insight " + BuildInfo.VERSION + " diagnostic\n"));
        assertFalse(LiveCombatSnapshot.empty().hasInputNotices());
    }

    @Test public void liveCaptureIgnoresSavedSpellOnMeleeAndReportsItsApplicability()
    {
        net.runelite.api.Client client = client(new int[]{99});
        LiveCombatSnapshot automatic = capture(client, MagicSpell.NOT_SET, TargetEffectSnapshot.empty(), DefenceTrackingMode.LOCAL);
        LiveCombatSnapshot saved = capture(client, MagicSpell.FIRE_SURGE, TargetEffectSnapshot.empty(), DefenceTrackingMode.LOCAL);
        assertEquals(automatic.getMaxHitText(), saved.getMaxHitText());
        assertEquals(automatic.getHitChanceText(), saved.getHitChanceText());
        assertEquals(automatic.getDpsText(), saved.getDpsText());
        assertTrue(saved.hasInputNotices());
        assertTrue(saved.diagnosticReport().contains("Saved spell ignored: Fire Surge"));
    }

    @Test public void trackingOffRejectsSuppliedDrainsAtCalculationBoundary()
    {
        net.runelite.api.Client client = client(new int[]{99});
        TargetEffectSnapshot drained = new TargetEffectSnapshot(1,50,1,0,0,30,0,true,false,false,false);
        LiveCombatSnapshot base = capture(client, MagicSpell.NOT_SET, TargetEffectSnapshot.empty(), DefenceTrackingMode.LOCAL);
        LiveCombatSnapshot local = capture(client, MagicSpell.NOT_SET, drained, DefenceTrackingMode.LOCAL);
        LiveCombatSnapshot off = capture(client, MagicSpell.NOT_SET, drained, DefenceTrackingMode.OFF);
        assertFalse(base.getHitChanceText().equals(local.getHitChanceText()));
        assertEquals(base.getHitChanceText(), off.getHitChanceText());
        assertEquals(base.getDpsText(), off.getDpsText());
        assertTrue(off.diagnosticReport().contains("defence tracking=Off (base stats)"));
        assertTrue(off.diagnosticReport().contains("Party Hub import=none"));
    }

    @Test public void copiedDiagnosticKeepsCapturedBoostsAfterClientChanges()
    {
        int[] level = {99};
        net.runelite.api.Client client = client(level);
        LiveCombatSnapshot snapshot = capture(client,MagicSpell.NOT_SET,TargetEffectSnapshot.empty(),DefenceTrackingMode.LOCAL);
        String before = snapshot.diagnosticReport();
        level[0] = 118;
        assertEquals(before,snapshot.diagnosticReport());
        assertTrue(capture(client,MagicSpell.NOT_SET,TargetEffectSnapshot.empty(),DefenceTrackingMode.LOCAL)
            .diagnosticReport().contains("118/118/118/118"));
    }

    private static LiveCombatSnapshot capture(net.runelite.api.Client client, MagicSpell spell,
        TargetEffectSnapshot effects, DefenceTrackingMode mode)
    {
        return LiveCombatSnapshot.capture(client,null,CombatStyleOverride.AUTO,BlowpipeDart.NOT_SET,
            spell,false,0,1,"Man",30,30,effects,false,null,RaidScaling.DEFAULT,EncounterContext.UNKNOWN,false,mode);
    }

    private static net.runelite.api.Client client(int[] level)
    {
        return (net.runelite.api.Client) java.lang.reflect.Proxy.newProxyInstance(
            net.runelite.api.Client.class.getClassLoader(), new Class<?>[]{net.runelite.api.Client.class}, (p,m,a) -> {
                if (m.getName().equals("getGameState")) return net.runelite.api.GameState.LOGGED_IN;
                if (m.getName().equals("getBoostedSkillLevel")) return level[0];
                if (m.getName().equals("getRealSkillLevel")) return 99;
                if (m.getReturnType()==boolean.class) return false;
                if (m.getReturnType()==int.class) return 0;
                return null;
            });
    }
}
