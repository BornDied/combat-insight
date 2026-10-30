# Audit3 verification — October 30, 2026

Local candidate based on alpha4 commit 377e1a954715de547e2c1aee155505f69a38eac4 plus the uncommitted audit1/audit2 work. No commit, push or Plugin Hub change.

All main and test Java sources compiled with --release 11 using the cached RuneLite1.13.3 client/API and cached dependencies. Focused JUnit run: **37 tests passed** across ArchiveObservedStateTest, EncounterRecorderTest, CalculationInspectorPanelTest, LiveTargetTrackerTest, ArchiveCapAuditTest and TargetEffectStateTest. Python: **12 tests passed** (`python -m unittest discover -s scripts -p 'test_*.py'`).

Regression evidence: the new Bork test failed with expected quartile3/actual-1 before the unreadable-bar fix. The bar-precedence test caught an intermediate regression (expected1/actual3); the final implementation preserves usable health-bar precedence. Recorder scope failed before expansion; the room-observation API was absent before implementation. Final focused run passes.

Full cached Java suite: **Tests run: 346,  Failures: 16**. All 16 failures encounter missing runtime class `net.runelite.http.api.item.ItemStats`; this is not a passing Gradle build. The affected cases are:

- `unknownRubyHitpointsCannotPublishAnUnenchantedMaximum(com.combatinsight.live.PreflightSafetyTest)`
- `matchingBossHudOverridesManualPartySizeForRubyDamage(com.combatinsight.live.PreflightSafetyTest)`
- `aSharedBossBarCannotSupplyRubyHitpoints(com.combatinsight.live.PreflightSafetyTest)`
- `latePhaseUncertaintyBlocksSpecialsIncludingGuaranteedAccuracy(com.combatinsight.live.PreflightSafetyTest)`
- `plainBoltsStillCalculateWithoutTargetHitpoints(com.combatinsight.live.PreflightSafetyTest)`
- `combatAddsAndBossesUseTheirOwnFootprintsForScythe(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `obligatorThrustAndSmashUseSizesOneTwoAndThreeWithoutASavedSpellLeak(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `guaranteedMaximumAndEnchantedBoltProcCompositionMustBeEstablished(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `magicDamageDoesNotGiveVoidwakerExtraReach(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `transformedFootprintReachesTheActualScytheDistribution(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `trioTbowShadowAndRalosCalculateAcrossEveryForm(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `allSixTrioFormsUseTwoScytheHitsAndTheSameOrdinaryDefences(com.combatinsight.live.ArchiveWeaponMatrixTest)`
- `rapidBlowpipeUsesInternalDartsAndLightDefenceAtArchiveTargets(com.combatinsight.live.LiveEquipmentCaptureTest)`
- `twistedBowOnStandingBorkUsesKnownSmorksQuartile(com.combatinsight.live.LiveEquipmentCaptureTest)`
- `blowpipeOrnamentAndSavedSpellCannotChangeDartsOrAttackSpeed(com.combatinsight.live.LiveEquipmentCaptureTest)`
- `dousedXarthCorpseReceivesGuaranteedMaximumFromLightRanged(com.combatinsight.live.LiveEquipmentCaptureTest)`

Windows Gradle `test check build` and developer-client startup still need verification. No gameplay was automated and no live fight was independently verified.

Remaining limits: Bork's exact reported capture lacks raw signals, so the fixed overwrite path is not proven to explain it. Existing animation-to-enrage fallback was retained, not independently certified from animation names. Current Wiki raw Bork/strategy pages were checked on October30; they confirm HP-dependent armour and transient stun, without justifying invented health brackets. Guardian ranged-defence data, Venator bounce DPS and Ocellus final-phase damage/timing remain unresolved. The recorder is bounded and cannot certify a complete fight.
