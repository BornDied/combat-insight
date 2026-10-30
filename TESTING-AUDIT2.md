# Combat Insight 0.9.0-alpha4-audit2 — local testing

Includes the destroyed Inert Guardian target-retention fix and audit1 Ocellus accuracy/notices/encounter recording changes. No commit, push or live Plugin Hub change.

Extract the complete source ZIP into a new folder. Open the folder containing build.gradle in IntelliJ. Run `./gradlew.bat test check build` on Windows, then `./gradlew.bat run`. Confirm diagnostic version 0.9.0-alpha4-audit2. Jagex-account developer login still requires the official RuneLite setup; this ZIP contains no account credentials.

Statue regression: attack active Inert Guardian16662, then destroy it. The retained combat identity must not become16663. A late hitsplat on the destroyed form must not select it. Attack the next target and confirm normal selection. Active statue ranged defence is still unverified; this fix does not supply invented accuracy/DPS.

Ocellus: enable Inspector -> Record Ocellus encounter before pulling; after the fight disable it and choose Copy encounter report. Restarting recording clears the previous report. Actor transitions do not end recording. Bounds and completeness limitations are included in the exported report. P4 damage/timing remains unresolved; supported hit chance remains independently available and Notices explains the damage block.

Verification: focused tracker/state/recorder/Inspector tests are recorded in the task response. Prior full Java attempt had16 missing-runtime-dependency failures; this is not a passing full Gradle build. Complete Windows checks before gameplay. No in-game verification performed here.

Detailed review: docs/research/2026-10-28-defence-and-items-audit.md and docs/research/2026-10-28-ocellus-testing.md.
