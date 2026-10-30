# Archive coverage implementation plan

> Execution: superpowers:executing-plans, local task-by-task work.

Goal: complete every evidence-supported Archive combat target/form, party-size input and size-dependent weapon path, including Trio and Ezekiel. Keep missing evidence visible and do not equate a roster entry with mechanic support.
Architecture: keep current snapshot and compact HUD; isolate Archive state/scaling rules in focused helpers and consume observed NPC composition sizes. Separate ordinary attack behavior, special previews, temporary next-hit effects and phase HP.
Tech stack: Java11, RuneLite1.13.4, Gradle8.10, JUnit4, Python audit tooling. Read local game cache through official RuneLite cache parser in a separate research project.
Spec: user requests to finish remaining work locally, all Trio/forms/sizes/use cases, every raid mob including Ezekiel; original audit requirements remain.

## Constraints
- NO PUSH, PR update/submission, release or merge. Branch local/archive-completion begins at df00806805617dc97a7b2a494d10a2721dd185ae. Existing remote PRs are not modified.
- Preserve ZIP, old0.7 and D: projects. Read the game cache only; never save/change it. No gameplay automation/account login.
- Preserve HUD modes, Inspector and highlighting; unknown values stay ?.
- Do not invent phase buffs or infer unavailable values from a name.

## Review focus
- NPC ID mutation must reset genuinely changed phase stats without resetting same-phase quadrant movement.
- Shared Trio health must not imply shared local drain state unless evidence confirms it.
- Cache sizes must reach Scythe/Obligator and size-sensitive specials; do not replace a known damage ambiguity merely because size is known.
- Unknown party inputs must not enable ruby or HP-dependent specials; manual and observed inputs need provenance.
- Next-hit buffs and specials cannot leak across weapons, targets, misses or scene changes.

### 1. Establish the complete roster
- [ ] Compare latest Wiki DPS revision against preserved alpha3 pin; record source changes.
- [ ] Read cache NPC sizes/options/transforms and relevant parameters for all Archive IDs; record provenance.
- [ ] Classify each ID as combat, phase/add, controller, hazard, intro or pet using evidence.

### 2. Trio, Moia and Ezekiel
- [ ] Resolve which shield/health variants actually change player damage; compare Wiki/GearScape and cache signals.
- [ ] Add failing live snapshot/gear fixtures for all six Trio IDs and imp, plus buff/unknown states.
- [ ] Implement supported phase/size/immunity/drain rules; retain explicit guard for genuinely unknown next-hit effects.
- [ ] Audit Ezekiel/swords, Moia/binders, Guardian and other encounter states with actual attack options.

### 3. Party scaling and weapon matrix
- [ ] Reconcile published HP tables before implementing bounded party scaling; test every party size1..8 and edge values.
- [ ] Exercise Scythe/Obligator, Tbow/Shadow, blowpipe/ammo, spells/weakness and supported specs across combat IDs.
- [ ] Check all missing calculations from alpha3 (including Ocellus/Rondache/Wardens); implement what has evidence and record exact missing formulas/signals.

### 4. Review and local verification
- [ ] Independent code review and fix confirmed findings.
- [ ] Clean Java/Python tests, Gradle check/build/shadowJar; diff/secrets/irrelevant-file review.
- [ ] Leave local work for user review with exact supported/pending matrix. Do not push.


## Execution record

- Roster/cache/Wiki reconciliation completed; all28 cache Attack IDs represented. New placeholder Wiki Imp was reconciled with cache skills. Original four audit documents linked to current notes.
- Evidence-supported ordinary Trio/Moia, sword/statue profiles, size propagation, typed add maximums, Ocellus phase reset and Ezekiel guard implemented with regression tests. Exact unresolved buff/phase formulas remain explicitly limited in the source audit; this is not full encounter completion.
- Party HP uses explicit manual starting size1–8 plus exact published cells; default unknown. Observed roster decoded for diagnostics and retained across loading, not trusted as the original scale.
- Tbow/Shadow/Ralos all-six-form matrix; Scythe all footprint classes/combat adds; Obligator modes and sizes; existing darts/prayer/ammo/ornament/saved-spell suite retained.
- Independent reviewer found Voidwaker reach and guaranteed-max enchanted bolt averaging. Both RED→GREEN; full329-test suite passed,12 Python tests passed, clean check/build/shadowJar passed.
- Ruling: retain ordinary baseline separately from unknown event effects; costs and remaining live-state limitations recorded in research/2026-10-24-archive-completion.md.
- Ruling: no HP extrapolation or partial-roster automatic HP; exact missing cells stay unavailable. Ruling: imp-buff uncertainty persists beyond short graphic. Ruling: guaranteed-max enchanted proc composition remains unavailable until verified. Costs and sources are in the audit.
- No push/remote changes. Local branch/checkpoint retained for review. Original0.7 and D: projects untouched.
