# DPS reference completion implementation plan

Goal: close implementable DPS gaps against current Wiki revision 98e4fac38b580901cc3f8d485bbaa48bbb3277ed and preserved GearScape fixtures; report actual missing evidence separately.
Architecture: retain the current snapshot, pure calculation helpers and compact HUD. Add deterministic raid transforms and explicit bounded raid inputs; never infer an encounter state from unrelated data.
Tech stack: Java 11, RuneLite 1.13.4, Gradle 8.10, JUnit 4.
Spec: original user requirements and docs/GEARSCAPE-COMPARISONS.md.

## Global constraints
- Preserve recovered ZIP, 0.7 project and D: work. Continue only in isolated combat-insight-audit clone on audit/alpha2-encounter-reconciliation.
- Do not automate gameplay, merge upstream, or claim in-game verification.
- Preserve Minimal/Standard/Advanced, Inspector and highlighting.
- Unsupported mechanics need specific missing evidence, not a generic deferred label.

## Review focus
- Warden NPC IDs distinguish active style and exposed core; protection must override damage formulas.
- Warden accuracy modifies damage, including Twisted bow double accuracy scaling.
- ToA HP rounding order and core invocation scaling differ from ordinary enemies.
- Unknown raid inputs must not silently supply ruby bolt HP.
- Saved spells must not activate equipment effects during a built-in or melee attack.

### Task 1: Warden phase distributions
- [x] Add failing live calculation fixtures for P2 Magic/ranged, protections and melee core maximum.
- [x] Implement bounded accuracy-dependent damage and type-sensitive core maximum, with regression coverage.
- [x] Keep unsupported special distributions explicit; verify whole Java suite.

### Task 2: ToA HP scaling
- [x] Add literal fixtures for invocation, path, party, core and rounding boundaries.
- [x] Add manual party/path settings with an explicit unknown default; apply HP scaling before estimates and ruby effects.
- [x] Test real snapshot and ruby special consumers; verify suite.

### Task 3: Spell/equipment and reference reconciliation
- [x] Check charged tomes and god-spell Charge against current formulas, RuneLite item/varp definitions and saved-spell selection.
- [x] Implement reference-supported paths with failing regression tests.
- [x] Reassess every Archive guard and misleading documentation, including existing Zulrah polearm support. Record exact remaining evidence gaps.

### Task 4: Verify and deliver
- [x] Run clean test/check/build/shadowJar, Python tests, review diff and fresh code review.
- [ ] Commit/push intended changes, update source PR and Plugin Hub pin/description only after checks pass.


## Live feedback added October 24
The previously submitted a556564 Plugin Hub PR #19333 is merged. User tests Bork: both Ralos and Twisted bow blocked; requests all unavailable HUD values as ?, no long HUD explanations; check Smorks/Xarth/Ocellus/blowpipe, form changes and drain floors; rewrite public Plugin Hub README in plain language. Keep existing unpublished follow-up changes and deliver as a new update after tests.

- [x] Separate verified drain levels from uncertain damage phases, especially Bork.
- [x] Resolve Bork armour from observed Smorks HP quartile and named stun sequences; do not infer a standing phase from stun alone.
- [x] Fix Ralos basic support and quarter-rounding, and test both tracked hits to the published floor.
- [x] Render unknown numbers as ? and keep explanatory text in Inspector/diagnostics.
- [x] Verify equipped blowpipe/darts, meta weapons and Ocellus/Blade/add forms through real snapshot calls.
- [x] Read identity-matched boss HP where unambiguous, rather than inventing Archive scaling.
- [ ] Rewrite README for users; update version, checks, source PR and a new Plugin Hub submission. (README/version/checks complete; delivery pending.)

Current verification and remaining evidence gaps: [alpha3 follow-up](../../research/2026-10-24-alpha3-follow-up.md).
