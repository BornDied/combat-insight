# Windows verification — 24 October 2026

Review build: `0.9.0-alpha2`. No in-game verification performed.

## Recovery provenance

- Supplied source ZIP SHA-256: `ac375853b3bd6e28944349831782a7ec6698ff9823dbaf5acc56c6e48ceeeab6`.
- ZIP extracted into a new `recovered-alpha2/CombatInsight-0.9.0-alpha2` folder; separate `combat-insight-audit` clone contains the review branch.
- Git base and fetched main at recovery: `a0f2e060973d1e00a835bcd2afa1e95cfa298bca`. No upstream divergence to merge at recovery.
- Existing 0.7 checkout and D: work were not edited. No gameplay automation or account login.

## Commands and results

Java: Temurin OpenJDK 11.0.32+9. Gradle wrapper: 8.10 with the repository's distribution checksum. `dependencyInsight` resolved `net.runelite:client:latest.release` to **1.13.4**, not the earlier manually compiled 1.13.3 environment. Dependencies remain dynamically selected by the existing build, so a later build may resolve differently.

```powershell
.\gradlew.bat test --no-daemon --console=plain
```

Recovered alpha2 baseline: **259 Java tests passed**. Baseline Python: **8 passed**.

Final verification:

```powershell
.\gradlew.bat clean test check build shadowJar --no-daemon --console=plain
python -m unittest discover -s scripts -p 'test_*.py'
.\gradlew.bat dependencyInsight --dependency net.runelite:client --configuration testRuntimeClasspath --no-daemon --console=plain
git diff --check
```

- Gradle: **BUILD SUCCESSFUL in 12s; 8 actionable tasks, 8 executed**.
- JUnit XML: **271 tests in 62 suites; 0 failures, 0 errors, 0 skipped**.
- Python unittest: **12 tests; OK**.
- Both `build/libs/combat-insight-0.9.0-alpha2.jar` and `build/libs/combat-insight-0.9.0-alpha2-all.jar` produced. These are local artifacts, excluded from Git and not release uploads.
- `git diff --check`: no whitespace errors.
- Existing warnings remain: deprecated Java/RuneLite APIs, unchecked operations in `CombatInsightPluginTest`, and deprecated Gradle features incompatible with Gradle 9. No warning was promoted to a successful check or silently removed by weakening validation.

The initial sandboxed Gradle invocation could not access the shared wrapper-cache lock; rerunning with approved cache access succeeded. That initial environment denial is not a failing final test. Windows' `python` alias was unavailable in this session, so unittest was run using the installed Codex Python executable with the same arguments above.

## Regression evidence and review

- New Archive add tests first failed for missing records, then passed after curation and uncertainty handling.
- Reduced demonbane bonus test first failed on Dreadnoth, then passed after the shared rule correction. Integration exercises melee/ranged damage and accuracy plus genuine demonbane spell accuracy and saved-spell isolation.
- Warden tests first failed for ordinary P2/core output and charging-P3 damage, then passed after guards/immunity were implemented. Special-attack tests cover the same uncertainty and guaranteed-accuracy bypass risks.
- Disputed Verzik-add test first failed on the trusted local profile, then passed after its uncertainty was made explicit.
- Crumble Undead test first failed because the broad target-dependent spell category granted demonbane accuracy, then passed after explicit spell classification.
- Independent code review found stance-based Obligator mode misclassification. A test using actual style-resolution paths and COM_MODE inputs failed before the fix, then passed for all three configured modes in automatic and forced melee. Reviewer rechecked the correction and found no additional definite defect, while identifying live slot mapping as a remaining validation item.
- Python audit tests cover separate duplicate variants, absent variant-versus-species counts, virtual IDs, immutable inputs and local ambiguity. All earlier data-tool tests also passed.

## Scope of this evidence

These checks validate compilation, regression behavior and local artifact production. They do not validate live raids, server formulas, the GUI import lifecycle, party synchronization or actual combat performance. Use [the encounter audit](2026-10-24-encounter-audit.md) and [testing checklist](../../TESTING-0.9.0.md) before relying on new calculations. Material implementation gaps warrant a **draft PR**, not a release-ready claim.

For local IntelliJ testing, open the new `combat-insight-audit` folder containing `build.gradle`, import Gradle with Java 11 or newer, and run the existing Gradle `run` task. Do not replace the older project or copy a JAR into the published Plugin Hub installation.

Delivery: [draft PR #4](https://github.com/BornDied/combat-insight/pull/4) is open and unmerged. Source commit: `85aaf0bb6ef3a84a562ed16b69e0572ea756e486`; subsequent handoff-only documentation may follow on the same branch. Remote main was rechecked after push and still equals the recovery base.

IntelliJ launcher and Windows Computer Use launch attempts did not expose a running IDE/project window. The source folder and built JARs are ready, but an open/imported IntelliJ session could not be verified. No existing project selection, account or gameplay state was changed.
