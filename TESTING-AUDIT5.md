# Combat Insight 0.9.0-alpha4-audit5 — local test candidate

Includes audit4, the Ocellus early-P4 trigger fix, and the defence-tracking work described below. This guide records test scope; GitHub and Plugin Hub pull requests determine submission and approval status. Previous local downloads remain unchanged.

## What changed

- **Count Defence down to cap**, enabled by default in Defence tracking, shows remaining reducible Defence. At Ocellus P1's minimum it shows0 while combat calculations still use129. Disable the option to show the actual level. The tooltip and Inspector retain actual level, baseline and floor. Existing row/infobox visibility choices still apply; no extra HUD row is enabled.
- Specials require a captured target and expected hit tick. The plugin reads live equipment and interaction after the client updates, instead of using the previous calculation snapshot. Ralos collects a complete pair before applying either drain; instant/delayed selection follows RuneLite's hitsplat ordering. Elder Maul uses same-cycle HP-XP evidence, as RuneLite's counter does.
- Incomplete or missing evidence is marked unresolved and cannot display a confident cap. Uncertainty persists within the same phase, but clears at a known Ocellus combat-phase reset. New NPC objects never inherit estimates merely because their names/IDs match.
- Identity-bound estimates survive LOADING, with any pending special cancelled as unresolved. This does not implement arbitrary actor-replacement rebinding. Explicit death/despawn cleanup and known phase resets still apply.
- Corrected Arclight demon reduction from10%base+2 to10%base+1.
- BGS secondary stat chaining now covers the current structured-data combat IDs for Smorks16641 and Trio16643/16645/16647. Their Attack and Strength baselines are1; own Arclight/Emberlight reductions and estimated recovery are retained internally before subsequent BGS overflow reaches Magic. Nonzero Defence floors stop overflow. This is not generic BGS secondary-stat support for every NPC or every alternate form.
- Unknown ranged defence on active Inert Guardian16662 /Animated Sword16653 no longer hides an otherwise available ordinary max hit. Accuracy and DPS remain unavailable without that defence data. Special-distribution guards remain.
- The recorder is still opt-in **Record raid diagnostics** for this alpha round. Its intended removal from normal configuration after testing is unchanged.

## Setup

Extract into a separate folder. Open its build.gradle project in IntelliJ. Run `.\gradlew.bat test check build`; investigate any failures, then launch `.\gradlew.bat run`. Confirm **0.9.0-alpha4-audit5** in Copy diagnostic. Preserve the existing Jagex developer-login setup; no credentials are included in this ZIP. Do not commit, push, publish or automate gameplay during testing.

## Focused in-game checks

1. Select **Track my reductions**. Compare a DWH, Elder Maul and Ralos special with independently visible evidence. Try Ralos with and without a thrall, including zero/one/two successful hits. A missing pair must produce an unresolved estimate, not a fabricated successful drain. Same-tick unrelated delayed attacks remain an attribution limitation.
2. Drain a capped boss. For example, Ocellus P1 current129/floor129 should show0 remaining; Bork current180/floor180 should show0 remaining. Confirm the tooltip and Inspector retain those actual levels. Switch the presentation option off and verify those actual numbers appear. Further specials must not take the calculation below the floor.
3. Confirm Off uses base stats and clears old local estimates; changing visibility alone must not change calculation inputs. Party Hub/Better Party Defence reductions are not imported, so local estimates do not represent the entire team's history.
4. Ocellus: P3 prayer/spin animations must not latch P4 early. Existing drains should persist while moving between P3 quadrants when the same live actor survives. Distinct combat phases reset as documented. A replacement actor or an ordinary logout must not inherit arbitrary old estimates. Actual P4 damage/timing remains unavailable.
5. Smorks/Trio: after reaching0 Defence, BGS overflow should update Magic consistently. For the baseline Smorks1Strength/1Attack case, an additional BGS10 changes185Magic to177. Check earlier Arclight/Emberlight usage too. No equivalent support is claimed for unverified alternate IDs.
6. Include Bork standing/stun/recovery, Guardian forms, active/destroyed Inert Guardian and the remaining raid encounters in the recording. Enable before the pull; stop after the kill/wipe and use **Copy encounter report**. Starting another recording/restarting the client clears the previous in-memory history.

## Verification and limits

Full cached Java suite:366 tests passed; Python data checks:12 passed. The earlier16 runtime failures were due to the omitted cached HTTP API jar, now supplied to the verification harness. All Java sources compile with release11. This is not a Windows Gradle build or an in-game certification.

Read docs/research/2026-10-30-archive-defence-audit.md before any release. Important open items include Guardian's dynamic stats, the new Ezekiel130-vs200 Defence source conflict, exact actor-replacement persistence, generic/alternate-form BGS overflow, the scope/rounding of the offensive-stat cap, and Ocellus P4 timing/damage. Venator bounce DPS is still unsupported. This candidate does not establish that every raid calculation is correct.
