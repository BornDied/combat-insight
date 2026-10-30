# Audit4 verification — October 30, 2026

Local candidate includes audit3 and one Ocellus phase-detector correction. No commit, push or Plugin Hub change.

## Evidence and regression

The user-supplied audit2 encounter report has 483 snapshots, 479 event entries and zero dropped entries. At tick3555, animation14869 sets final phase true at6191/7610HP. Three later quadrant-destruction sequences precede the first deflect-family animation14863 at tick3924 /1744HP. Thus296 recorded snapshots were prematurely blocked before that deflect cue. Actor death appears at tick4027 and despawn at4043; capture completeness remains unverified.

A replay test was added before the fix. It failed with `Early final-phase flag at animation 14869`. After restricting the existing animation trigger to14863–14868, all11 ArchiveObservedStateTest cases pass, including the replay, the actual deflect latch and actor cleanup. The existing visible-curse trigger remains. Names alone were not treated as evidence of a phase error: the chronological live sequence establishes the early trigger.

## Checks

- All main/test Java sources compiled with --release11 against cached RuneLite1.13.3 API/client and dependencies.
- Focused JUnit: **38 passed** across ArchiveObservedStateTest, EncounterRecorderTest, CalculationInspectorPanelTest, LiveTargetTrackerTest, ArchiveCapAuditTest and TargetEffectStateTest.
- Full cached JUnit: **347 run,16 failures**. Every failure encounters missing runtime class net.runelite.http.api.item.ItemStats, as in the previous baseline. These are not passing full-build results.
- Python data code was unchanged from audit3's12 passing checks.
- git diff --check passed. The source ZIP is checked for integrity, required files, audit4 version and narrowed animation trigger before delivery.
- No in-game retest, Windows Gradle build or deployment was performed here.

The16 cached-suite failures are:

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

## Remaining limitations

Final-phase damage/timing remains withheld; narrowing the trigger is not full P4 support. The five P1/Blade ZCB snapshots have ruby dragon bolts(e) selected but unknown boss health, so no HP values were invented. Defence tracking was Off for the entire supplied recording, preventing validation of drain attribution/caps/recovery. Other audit3 limitations remain.
