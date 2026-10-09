# Combat Insight 0.6.0 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans for native execution or superpowers:subagent-driven-development if selected. Steps use checkbox syntax for tracking.

**Goal:** Deliver the approved encounter, weapon, diagnostic and optional HUD improvements as one local test build.

**Architecture:** Resolve encounter identity and input provenance before calculation. Keep pure mechanics separate from client observation; use the same resolved target for ordinary attacks, specials and tracked drains. Split the work into independently verified batches below.

**Tech Stack:** Java 11, RuneLite public API, JUnit 4, Gradle wrapper, Python NPC generator.

**Spec:** `docs/superpowers/specs/2026-09-28-expanded-060-design.md`

## Global Constraints

- Baseline: `8cbdcf0d028c1c0b4146e798bb1f59890cfe2294`.
- No commits, pushes, public PRs, Plugin Hub changes or publication.
- Preserve BSD 2-Clause licensing and `build=standard`.
- Preserve existing mode-specific row toggles, saved settings, custom colours.
- Keep unknown inputs explicit; never fabricate stats or client observability.
- Hold-to-show details is disabled by default and changes no saved display mode.
- Full-source ZIP plus cumulative baseline patch and Windows test checklist.
- All commands below run at the isolated project root; Windows equivalents use `gradlew.bat`.

## Review Focus

1. Reused NPC IDs across encounters: no stale mode or drain state (tasks 2–3).
2. Uncertain HP near a cap/threshold: no false exact result (tasks 3, 6).
3. Party members die or leave visibility: starting party size remains valid (task 4).
4. Held key loses focus: normal HUD restores without changing config (task 8).
5. Report requested during logout/update: one internally consistent, privacy-safe snapshot (task 7).

## Structure and test conventions

Java paths below are relative to `src/main/java/com/combatinsight/`; tests use
the corresponding package under `src/test/java/com/combatinsight/`.
New boundaries are intentionally small. Do not add a general fight simulator.
For each calculation task, first write independent expected-value tests, run
them and record the expected failure, implement, then rerun the focused suite.
Do not replace those tests with comparisons against the same implementation.

The approved design requires mechanics research. Exact numeric fixtures and
client event identifiers must be established in task 1 before dependent code
is written. Document conflicts as blocked mechanics rather than guessing.

### Task 1: Source evidence and reproducible NPC comparison

**Files:** `scripts/generate_monsters.py`, `scripts/test_generate_monsters.py`,
new `scripts/compare_monsters.py`, `scripts/test_compare_monsters.py`,
`docs/research/060-mechanics.md`, `docs/research/060-npc-audit.md`,
`src/main/resources/com/combatinsight/data/monsters.tsv`.

**Interfaces:** Python `compare_snapshots(before: dict, after: dict) -> dict`
returns `added`, `removed`, `changed`, `ambiguous` sorted ID lists.

- [ ] Recount baseline rows and ambiguities; preserve a baseline copy for comparison.
- [ ] Retrieve current Wiki/Jagex mechanics and RuneLite APIs. Record dated source URLs, formulas, rounding, caps, mode IDs and independently worked examples for every planned mechanic.
- [ ] Write `test_identifies_changed_and_removed_ids` with before IDs {1,2}, after {2,3}, changed stats for 2; assert added=[3], removed=[1], changed=[2]. Add ambiguity and malformed/partial fetch cases.
- [ ] Run `python3 -m unittest discover -s scripts -p 'test_*.py'`; confirm new failures before implementation.
- [ ] Implement comparison and retain source variant identities needed for DT2; fetch into a candidate file, never overwrite live data on incomplete responses.
- [ ] Run generator tests; review additions/removals/stat changes and write the named coverage report. If live access fails, report the block and continue independent tasks without claiming a fresh sweep.

### Task 2: Shared encounter resolution

**Files:** new `live/EncounterMode.java`, `live/EncounterContext.java`,
`live/TargetVariantDatabase.java`, `live/ResolvedTarget.java`,
`live/EncounterResolver.java`; modify `live/LiveCombatSnapshot.java`,
`live/TargetEffectTracker.java`, `live/SpecialAttackTracker.java`,
`CombatInsightPlugin.java`, `CombatInsightConfig.java`; test
`live/EncounterResolverTest.java`.

**Interfaces:** `EncounterMode { AUTO, NORMAL, AWAKENED, QUEST }`;
`EncounterResolver.resolve(int npcId, EncounterContext context): ResolvedTarget`;
resolved target holds nullable TargetProfile, status, and source label.
Context records selected/observed mode and optional current HP. Variant table
maps NPC ID plus explicit mode to TargetProfile; ordinary IDs use TargetDatabase.

- [ ] Write `autoWithoutEvidenceKeepsAmbiguity`, `awakenedOverrideSelectsAwakened`, `questDoesNotResolveAsNormal`, and `newEncounterClearsOldContext`; assert unresolved auto remains unavailable and non-DT2 stats are unchanged.
- [ ] Run `./gradlew test --tests '*EncounterResolverTest'` and confirm failures.
- [ ] Add mode setting under Encounter inputs, implement resolution from evidence established in task 1, and share its result through snapshots and drain tracking.
- [ ] Test changing mode/raid resets relevant drains and logout/world change clears observed context. Run resolver, existing target and effect tests.

### Task 3: DT2 and raid damage transforms

**Files:** new `live/Dt2CombatRules.java`, `calculation/EncounterDamageRules.java`;
modify `live/RaidDamageRules.java`, `live/TargetMechanics.java`,
`live/TargetEffectRules.java`, `live/LiveCombatSnapshot.java`,
`calculation/SpecialAttackCalculator.java`; tests `live/Dt2CombatRulesTest.java`,
`calculation/EncounterDamageRulesTest.java`, existing raid tests.

**Interfaces:** `Dt2CombatRules.apply(TargetProfile base, EncounterContext context): ResolvedTarget`;
damage rules expose `transformAccurateHit(int rolledDamage, AttackType type, EncounterContext context): int`
and `unavailableReason(AttackType type, EncounterContext context): String`.
Add evidence-required weapon/spell flags to immutable context rather than guessing from attack type.

- [ ] Populate source-backed table tests for all four bosses in both modes, quest separation, HP boundaries, defence floors, immunity and modifier order.
- [ ] Add table tests for each existing raid guard, including correct style, off-style, multi-hit, relevant weapon exceptions and uncertain context.
- [ ] Run `./gradlew test --tests '*Dt2CombatRulesTest' --tests '*EncounterDamageRulesTest'`; confirm failures.
- [ ] Implement resolved stats and per-hit transforms. Apply the same transforms to maxima and enumerated expected outcomes. Never apply an average-damage multiplier when rounding per hit changes the answer.
- [ ] Remove only guards whose supported cases pass their independent fixtures. Keep unsupported states explicit. Run all raid/scaling/effect tests.

### Task 4: Observed raid inputs with manual overrides

**Files:** new `live/RaidInputTracker.java`, `live/ResolvedRaidInputs.java`;
modify `live/RaidScaling.java`, `CombatInsightPlugin.java`, `CombatInsightConfig.java`;
test `live/RaidInputTrackerTest.java`.

**Interfaces:** tracker `reset(): void`, `snapshot(): ResolvedRaidInputs`;
resolved inputs `toScaling(): RaidScaling`, `getSourceLabels(): Map<String,String>`.
Implement concrete client event adapters only for verified public APIs from task 1.

- [ ] Test `deadMemberDoesNotShrinkStartingParty`, `manualOverrideWins`, `unknownTeamSkillsRemainLabelled`, `worldHopClearsRaid`, and `scalingChangeClearsDrains`.
- [ ] Run `./gradlew test --tests '*RaidInputTrackerTest'`; confirm failures.
- [ ] Implement observed values, per-input manual precedence and lifecycle resets without deriving party size from visible actors.
- [ ] Run tracker/scaling tests; document which inputs are observed and which still require manual values.

### Task 5: Halberd, fang and scorching specials

**Files:** `calculation/SpecialAttackWeapon.java`, `calculation/SpecialAttackCalculator.java`,
new `calculation/SpecialAttackContext.java`, `live/LiveCombatSnapshot.java`,
`calculation/SpecialAttackCalculatorTest.java`.

**Interfaces:** preserve the existing calculate overload for regressions;
new overload accepts existing arguments plus `SpecialAttackContext` containing
target size, encounter context and evidence-required effect eligibility.

- [ ] Add mapping cases and independent numeric fixtures from task 1 for both halberds, fang and scorching bow; test hit dependence, small/large targets, miss branches, immunity and caps.
- [ ] Run `./gradlew test --tests '*SpecialAttackCalculatorTest'`; confirm new failures.
- [ ] Implement immediate damage and accuracy; identify conditional effects separately. Do not assume a second target for area attacks.
- [ ] Run all existing special and multi-hit tests to retain 0.5.0 behaviour.

### Task 6: Enchanted bolt outcomes and Zaryte

**Files:** new `calculation/BoltEffect.java`, `calculation/BoltContext.java`,
`calculation/BoltResult.java`, `calculation/BoltCalculator.java`;
modify `live/AmmunitionRules.java`, `live/LiveCombatSnapshot.java`,
`calculation/SpecialAttackWeapon.java`, `calculation/SpecialAttackCalculator.java`;
tests `calculation/BoltCalculatorTest.java`, existing ammunition tests.

**Interfaces:** `BoltCalculator.calculate(BoltContext context): BoltResult`.
Context stores bolt family, normal accuracy/max, player/target HP with known flags,
crossbow type, special flag, modifiers and resolved encounter. Result stores
availability/reason, ordinary maximum, conditional proc maximum and expected damage.

- [ ] Define the supported PvM bolt-family matrix from researched evidence; identify unsupported effects explicitly.
- [ ] Add fixtures for normal/proc/miss outcomes, ruby HP boundaries, diamond accuracy interaction, Zaryte special, modifiers, immunity and unknown HP. Assert outcome probability sums within 1e-12.
- [ ] Run `./gradlew test --tests '*BoltCalculatorTest'`; confirm failures.
- [ ] Implement probability branches without double-counting accuracy; integrate ammo/quiver selection and encounter transforms. Keep proc maxima distinguished from ordinary damage.
- [ ] Run bolt, ammo, special and snapshot tests; add equipment-switch regression cases.

### Task 7: Copy calculation details

**Files:** new `diagnostics/CalculationReport.java`, `diagnostics/CalculationReportFormatter.java`;
modify `CombatInsightOverlay.java`, `CombatInsightPlugin.java`, `live/LiveCombatSnapshot.java`;
test `diagnostics/CalculationReportFormatterTest.java`.

**Interfaces:** `CalculationReportFormatter.format(CalculationReport report): String`.
Immutable report is assembled alongside the calculation and contains only the
explicitly permitted fields from spec section F; no raw Client or player object.

- [ ] Test a stable report fixture, unknown fields, newline-safe labels and a logout snapshot; verify report contains version, IDs, assumptions and results, but no account name/chat fields.
- [ ] Run `./gradlew test --tests '*CalculationReportFormatterTest'`; confirm failures.
- [ ] Add the overlay menu action `Copy calculation details`; copy the captured report locally, report failure briefly and never transmit it.
- [ ] Run formatter tests; add Windows clipboard verification to the manual checklist.

### Task 8: Hold-to-show details without a redesign

**Files:** new `HeldDetailsController.java`; modify `CombatInsightConfig.java`,
`CombatInsightPlugin.java`, `CombatInsightOverlay.java`;
tests `HeldDetailsControllerTest.java`, `CombatInsightOverlayTest.java`.

**Interfaces:** controller `setHeld(boolean held): void`, `reset(): void`,
`effectiveMode(HudDisplayMode configured): HudDisplayMode`.
Use RuneLite key management APIs; default keybind is unbound.

- [ ] Test that held=false preserves all three modes, held=true yields Advanced, and reset restores configured mode without a config write.
- [ ] Test focus loss, logout, disabled plugin and keybind changes clear held state; verify a hidden HUD stays hidden.
- [ ] Run `./gradlew test --tests '*HeldDetailsControllerTest' --tests '*CombatInsightOverlayTest'`; confirm new failures.
- [ ] Implement key lifecycle and use effective mode at render time. Reuse Advanced toggles and existing colour/background/spacing rules.
- [ ] Render normal and held states with the real RuneLite font, including a narrow user-resized overlay and long warnings. Package the preview image for the user.

### Task 9: Combined validation and local handoff

**Files:** `build.gradle`, `runelite-plugin.properties`, `README.md`, `TESTING.md`,
`AUDIT-SUMMARY.md`, `THIRD_PARTY_NOTICES.md` if attribution changes.

- [ ] Set local candidate version 0.6.0 and update supported/unavailable matrices from actual implemented results.
- [ ] Run `python3 -m unittest discover -s scripts -p 'test_*.py'` and `./gradlew clean test build`; record counts and any environmental failure precisely.
- [ ] Review final changes against all spec sections and the five review-focus conditions. Retain explicit known limitations.
- [ ] Run `git diff --check`; preserve baseline licence and standard build setting.
- [ ] Generate cumulative patch including new source files, apply to a disposable clean baseline and byte-compare against packaged source.
- [ ] Package a full-source ZIP, patch, audit, rendered HUD preview and Windows launch checklist. Exclude caches, build products and credentials.
- [ ] Provide a prioritised in-game checklist: POH/dummy and gear switches, accessible normal DT2, raids, bolts and optional shortcut; label Awakened live checks untested unless actually observed.
- [ ] Stop before release. Do not claim in-game results or Gradle success without evidence.

## Plan review

All spec sections A–H map to tasks 1–8; delivery/verification maps to task 9.
Shared resolver/context precedes dependent calculations. Numeric mechanics and
client detection remain explicit research work rather than invented constants.
Suggested execution: native, sequential batches in this session, followed by
independent final review if available and authorized by the execution workflow.
