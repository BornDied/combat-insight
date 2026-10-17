package com.combatinsight;

import com.combatinsight.live.LiveCombatSnapshot;
import java.time.Instant;

/** Immutable text captured from the same snapshot as the HUD. No client or account access. */
final class InspectorDocument
{
    final String summary;
    final String report;
    private final String[] sections;

    InspectorDocument(String summary, String report, String[] sections)
    {
        this.summary=summary;
        this.report=report;
        this.sections=sections.clone();
    }

    String section(int index) { return sections[index]; }

    static InspectorDocument capture(LiveCombatSnapshot s)
    {
        String captured="Captured: " + Instant.now();
        String summary=s.isLoggedIn()
            ? "Target: " + (s.hasTarget() ? s.getTargetName() : "None selected")
                + "\nWeapon / spell: " + s.getWeaponOrSpellName()
                + "\nMax hit: " + s.getMaxHitText()
                + "\nHit chance: " + s.getHitChanceText() + "\nDPS: " + s.getDpsText()
            : "Log in to inspect a calculation.";
        String inputs=s.getLevelLabel() + ": " + s.getLevelText()
            + "\nStyle: " + s.getCombatStyleName() + " / " + s.getStyleName()
            + "\nPrayer: " + s.getPrayerName()
            + "\n" + s.getGearBonusLabel() + ": " + s.getGearBonusText()
            + "\nEffective levels: " + s.getEffectiveLevelText()
            + "\nAttack interval: " + s.getAttackSpeedText();
        String target="NPC ID: " + s.getTargetId() + "\nHealth estimate: "
            + (s.hasTargetHealth() ? s.getTargetHealthText() : "Unavailable")
            + "\nAttack / defence rolls: " + s.getRollText()
            + "\nTracked Defence: " + s.getTargetDefenceText()
            + "\nTracked Magic: " + s.getTargetMagicText();
        String specials=s.hasSupportedSpecialAttack()
            ? "Maximum: " + s.getSpecialMaximumText(true)
                + "\nHit chance: " + s.getSpecialHitChanceText()
                + "\nExpected damage: " + s.getSpecialExpectedDamageText()
                + "\nOn hit: " + s.getSpecialOnHitText()
            : "No supported special for this weapon.";
        String notices=s.getWarnings().isEmpty() ? "No active notices. This does not guarantee every mechanic is supported."
            : String.join("\n\n",s.getWarnings());
        return new InspectorDocument(summary,captured + "\n" + s.diagnosticReport(),
            new String[]{inputs,target,specials,notices,s.diagnosticReport(),
                String.join("\n",s.getAppliedEffects()) + "\n\nSelected effects only; not a complete formula trace.",
                s.getDiagnosticInputs()});
    }
}
