# Release submission note

Version metadata is now 0.8.0 for the user-authorized Plugin Hub submission. The following records the earlier local candidate and its validation; no in-game testing is claimed. Full Gradle verification awaits an environment with dependency-download access / Plugin Hub CI.

# Combat Insight 0.8.0-local.1 — local test candidate

This combines the previous ornament candidate with the approved compact HUD and inspector work and confirmed combat fixes. No commit, push, Plugin Hub change or publication was performed. Baseline: GitHub 0.7.0 commit 98e49fd2c18534f376b1004a8adf1b3aec1c4813. These are source files, not an installed plugin or prebuilt runnable JAR.

## What changed

- Optional sidebar calculation inspector; open via toolbar icon or HUD right-click **Inspect calculation**. Closed by default; disable **Enable inspector** to remove it. Expandable inputs, target, special, notices, applied effects and input-source sections. Reads the same immutable snapshot as HUD. Freeze/resume affects inspector only; Copy diagnostic copies the displayed snapshot and capture time. No account name, chat, credentials or location in diagnostics. Applied-effects list is deliberately partial, not a complete algebraic trace.
- Separate Appearance section, original mode sections and saved config keys preserved. Defence displays/colors moved under Defence tracking. Existing Standard rows retain their established behavior in Advanced mode; this is organizational, not a silent semantic change.
- Cyan Spec max label/value for designated supported spec weapons; configurable color/disable. Optional divider and Manual indicator. High-contrast background added alongside existing presets/custom color.
- Explicit PREVIEW values outside combat, honoring mode/row toggles. Preview suppresses for10 seconds following observed combat; turn it off when testing real values. Preview never changes calculation inputs or diagnostic snapshots.
- Zulrah rolls above50 reroll45–50 per hitsplat, affecting maximum and expected damage, including supported multi-hit, bolt and special paths. Aggregate multi-hit totals can legitimately exceed50. Elemental-weakness and ordinary input mechanics still apply before the cap.
- Soulreaper normal and ornamented axe: basic Soul Stack bonus plus current Behead special, using separate unstacked damage base for minimum/maximum;0stacks unavailable. Stack effects and spec result visible in inspector. Spec highlights only when its maximum is available.
- Previous ornament fixes included: Elite/regular Void cosmetic and locked variants, Berserker necklace ornament, DWH ornament and ornamental trident weapon categories. Existing 39 weapon-category equivalence pairs retained.
- ToA target HP and HP-dependent ruby calculations explicitly unavailable until party/path/invocation HP scaling is modeled, rather than using wrong base HP. Non-HP calculations remain distinct.

## Important remaining limitations

- The latest reported PNM pillar discrepancy is NOT reproduced or claimed fixed. Ready pillar IDs9435/9438/9441/9444 already apply double magic damage; known tests pass. Dormant/charged transitions and exact live inputs need evidence. Freeze/copy a snapshot while the discrepancy is visible. Do not add another x2 multiplier.
- No claim all mobs or all weapons are supported. The bounded audit is in docs/research/AUDIT-080-COVERAGE.md and AUDIT-080-COMBAT.md. Some newer NPCs exist but remain ambiguous by phase/delve.
- Bow-specific arrow-tier compatibility is a known remaining input-validity gap (e.g. ordinary shortbow with dragon arrows); use ammunition the game actually permits.
- CoX highest HP/average Mining remain manual. ToB observed roster is not guaranteed original roster after late attach/relog. ToA HP scaling remains incomplete.
- Inspector inputs show equipment slot IDs and aggregate bonuses, not a graphical gear paper doll or exhaustive formula trace.
- Windows/native RuneLite interaction and actual gameplay have not been performed here.

## Verification here

All main/test Java sources compiled with Java11 target against cached RuneLite1.12.39 API/client jars. 228 JUnit tests and8 Python data tests passed. Headless Swing tests exercise freeze/copy/resume and sidebar width; actual HUD renders checked in all three modes. Existing deprecation/unchecked compiler and headless SLF4J warnings remain.

Gradle wrapper `bash gradlew test --no-daemon` could not download Gradle8.10: Network is unreachable. Thus Gradle dependency resolution against latest.release is NOT verified here. Run the Windows build below before live testing.

## Windows setup — keep old working project

1. Extract CombatInsight-0.8.0-local-test.zip into a new folder, such as `D:\CombatInsight-0.8.0-local-test`. Open the extracted project folder containing build.gradle in IntelliJ. Do not overlay an older working folder or import its patch a second time.
2. Wait for Gradle import. Open the project terminal and run `.\gradlew.bat test`.
3. If tests pass, run `.\gradlew.bat run`. Use the test client; avoid loading a second Plugin Hub copy of Combat Insight in that same client.
4. Verify the copied diagnostic header says **0.8.0-local.1**. A running Plugin Hub0.7 client will not contain these changes.

Alternative for Git users: incremental patch is against baseline98e49fd only. Preserve existing work; first use `git apply --check <patch>` in a clean checkout of that baseline. The patch includes previous0.7.1 ornament changes, so do not apply it on top of that candidate.

## Short live test checklist

1. **Settings:** existing selections preserved if using same RuneLite profile. Verify HUD basics / Appearance / Minimal / Standard / Advanced / Special / Calculation / Raid / Defence / Inspector sections. Test Minimal, Standard and Advanced separately.
2. **Preview:** enable while idle; verify PREVIEW label, selected background and row toggles. Begin combat: preview disappears and real values return. Turn preview off after customizing.
3. **Highlight:** equip claws, Voidwaker or ZCB; cyan label+number when supported. Disable highlight, change color, toggle divider. Unsupported or immune results must stay clearly unavailable/immune.
4. **Inspector:** open from HUD and toolbar. Switch gear/prayer/target and verify values update. Freeze, switch again: inspector stays fixed but HUD updates. Copy and verify frozen report; resume. Close sidebar. Disable/re-enable inspector; restart plugin. Confirm no extra sidebar when disabled.
5. **Elite Void:** use the ornamented ranged set with correct blowpipe dart selection. At104Ranged, Rapid, +58Ranged strength, no damage prayer, regression example is24 rather than21. Different inputs legitimately change this.
6. **Soulreaper:** normal/ornamented,0–5stacks, basic and special values.0stacks should not invent a usable special. Observe stack decay and weapon changes. Compare with a trusted calculator using identical stats/gear/prayers.
7. **Zulrah:** Shadow/tbow basic hits capped at50; lower max stays lower. Supported bolts/specials need live comparison. Multiple hits can sum above50. More raw damage above50 can still improve expected DPS.
8. **PNM:** Shadow on an active totem. Freeze/copy exact target ID, current gear/stats, displayed max and observed hit. Compare boss vs totem targeting and transitions. This is diagnostic follow-up, not confirmation the outstanding report is fixed.
9. **Raids:** check manual/observed inputs in inspector; confirm ToA HP-dependent ruby values say unavailable rather than guessing. Sample CM Guardian/Vasa/Ice Demon and ToB Nylo transitions before release.

No public release until the Windows build and targeted live checks pass.
