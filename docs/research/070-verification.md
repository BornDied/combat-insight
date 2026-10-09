# Verification — October 2, 2026

Candidate: 0.7.0-local.1. Live baseline: `8cbdcf0d028c1c0b4146e798bb1f59890cfe2294`. Includes latest 0.6 source plus the new controls.

- All production/test Java compiled with `--release 11` against real RuneLite client/API 1.12.39 and JUnit 4.12.
- **187 JUnit tests passed**, including off/re-enable/reset, energy baseline, live capture ignoring saved spells, rejection of supplied drains while off, diagnostic immutability, ToB orb boundaries/deaths/exit and plugin scene-loading lifecycle.
- **8 Python tests passed** for data generator/comparison tools.
- Existing deprecation/unchecked warnings and headless SLF4J no-binding notice are present; no compilation error or test failure remains.
- Independent review identified two important defects: ToB roster reset on scene loading, and reduction reset/off restoring Tekton first-special accuracy. Both reproduced in failing regressions, corrected, then the full suite passed. No separate re-review claimed.
- A stale diagnostic-version test failed after the version bump; it now checks the central candidate version.
- Actual RuneLite overlay renderer preview: default compact Standard 190×54; optional notice marker 190×54. Illustrative values only; screenshot visually inspected.
- Numeric-equivalent manual/observed source changes retain valid reductions; changes to numerical raid inputs still rebase and clear reduction estimates.
- Unknown Tekton first-special history cannot claim guaranteed accuracy; observed special-attempt history is independent of reduction estimates. Tracking off may still observe a local special attempt for that conditional accuracy, but does not apply stat reductions.
- No native Windows or in-game validation performed here. Gradle distribution download remains blocked by this environment's network restriction; compilation against 1.12.39 is not a claim of a completed latest-release Gradle build.
- Packaging checks apply the cumulative patch to a fresh archive of the live 0.5 baseline and byte-compare every packaged file. The full ZIP contains source and tests, not a prebuilt plugin binary.
- No commits, push, PR, Plugin Hub update or publication.

Deferred scope: verified automatic spell mapping, full fresh NPC export/completeness audit, remaining special families, Venator bounces and Party Hub integration. ToB roster observation is experimental and opt-in; reconnecting mid-raid cannot reliably reconstruct departed players. These limits are intentional and documented in the test guide.
