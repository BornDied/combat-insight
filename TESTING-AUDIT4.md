# Combat Insight 0.9.0-alpha4-audit4 — Ocellus phase correction

Includes all audit3 changes plus the Ocellus early-final-phase correction. Audit2 and audit3 downloads remain unchanged. No commit, push, publication or Plugin Hub change.

## Evidence and correction

The supplied audit2 recording contains 483 calculation snapshots and 479 event entries, zero dropped entries, and spans ticks3179–4043. It includes P1 target16674, Blade16676 and later Ocellus16677, ending with observed death and despawn events. This is evidence of the observed span, not a certified complete pull.

At tick3555, Ocellus had6191/7610HP and animation14869. The old detector treated the entire14863–14871 animation range as final phase. It therefore latched the final-phase damage/timing guard before three recorded quadrant-destruction sequences. The first deflect-family animation14863 appears at tick3924 with1744/7610HP. In this recording,296 earlier snapshots were caught by the premature guard,221.4seconds before that first deflect cue.

The detector now accepts the deflect family14863–14868 or the existing visible-curse cue. Prayer animations14869–14871 no longer initiate the final-phase latch. The replay regression covers those prayer/quadrant transitions, then confirms deflect activates and retains the guard until actor cleanup. This change does not invent final-phase damage multipliers or attack-cooldown formulas.

## Setup and test

1. Extract into a separate folder. Open build.gradle in IntelliJ and run `.\gradlew.bat test check build`. Investigate any failing tests. Then run `.\gradlew.bat run` and confirm diagnostic version **0.9.0-alpha4-audit4**.
2. Enable **Record raid diagnostics** before Ocellus. P1 and Blade ordinary attacks should continue calculating when their required inputs are known. During the earlier16677 quadrants, the prayer/spin sequence must not change `final phase` to true or permanently hide max/DPS.
3. When the final quadrant/deflect sequence starts, the final-phase guard remains intentional. Its damage and timing are still unverified; supported hit chance remains independently available. This candidate does not claim full P4 calculation support.
4. Stop recording after kill/wipe and choose **Copy encounter report**. A stop preserves data; starting another recording or restarting the client clears it. Do not automate gameplay. Bork/Smorks and room-observation tests described in TESTING-AUDIT3.md also apply.

## Other findings from this recording

- Selected Dragon darts are detected and normal blowpipe calculations are available in P1. The earlier Set dart blocker is absent from this recording.
- Zaryte crossbow shows five snapshots with the existing bolt/HP availability block during P1/Blade. Target HP was unavailable then; later16677 boss-HUD health is available. The report does not justify inventing earlier HP or claiming those ruby-bolt calculations are fixed.
- Defence tracking is Off (base stats) throughout. This recording cannot validate local/party defence-drain attribution, caps or recovery.
- The report does not capture every player attack launch/projectile or certify which owned hitsplat belongs to which attack. Numerical availability is not proof that every formula or DPS value matches actual combat.
- No airborne flag was observed. That is not proof the airborne detector worked or failed; the selected-target timeline does not establish every airborne interval.

## Verification and remaining work

See docs/research/2026-10-30-audit4-verification.md. Current limitations from audit3 remain: active Guardian ranged defence, Venator bounce DPS, exact Bork state evidence and final-phase Ocellus damage/timing. The temporary config recorder is for this alpha testing round and is still intended to be removed from normal config afterward.

The cached Java test harness lacks RuneLite's runtime ItemStats class in16 existing cases. Windows Gradle checks and an in-game retest are still required. Nothing has been deployed.
