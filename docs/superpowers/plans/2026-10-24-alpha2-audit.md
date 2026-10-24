# Alpha2 recovery and encounter audit implementation plan

> Execute inline with superpowers:executing-plans. User authorized autonomous implementation and a new-branch draft PR; no merge or Plugin Hub submission.

**Goal:** Recover unpublished alpha2, reconcile raid inputs against current references, implement defensible corrections, and leave a tested review branch.

**Architecture:** Retain the existing snapshot/calculation pipeline and compact HUD. Curate live NPC mappings separately from Wiki variant records. Keep unresolved states unavailable; do not infer live phase or HP from a calculator default.

**Tech stack:** Java 11, Gradle 8.10, JUnit 4, Python unittest, RuneLite API.

**Spec:** User's October 24 continuation requests and recovered TESTING-0.9.0.md / docs/FRACTURED_ARCHIVE.md.

## Constraints and review focus

- Preserve the 0.7 and D: projects. Work only in the separate audit clone and feature branch.
- Current main equals supplied base a0f2e060973d1e00a835bcd2afa1e95cfa298bca; overlay is an exact-base recovery.
- Never collapse duplicate Wiki IDs or import intro/debug/scenery as combat IDs.
- No invented scaling, shield, stun, protection, or HP-dependent damage.
- Special attacks, saved spells, prayer and ammunition must share the appropriate target rules.
- Preserve Minimal/Standard/Advanced, optional Inspector and configurable special highlighting.
- Audit all raids and DT2/PNM/Zulrah; document unsupported rules without claiming complete support.

## Task 1: Recover and establish baseline

- [x] Extract ZIP into a new archival folder; clone repository into separate folder; create audit/alpha2-encounter-reconciliation.
- [x] Confirm current main equals supplied base; overlay all alpha2 files without touching original projects.
- [x] Run normal Gradle test and Python unittest suite. Baseline: 259 Java, 8 Python, all passing.

## Task 2: Archive mappings, adds and conservative output

Files: monsters.tsv, ArchiveEncounterRules.java, ArchiveTargetTest.java, new ArchiveAddTest.java.

- [x] Pin current Wiki and RuneLite source revisions; record Bork's phase-dependent conflict, Guardian mapping and Ocellus intro versus combat mapping.
- [x] Write failing live snapshot tests for Xarth normal/doused corpses, Dreadnoth jelly and undead binder; assert distinct weaknesses/defences, no invented HP and available supported damage.
- [x] Write failing tests for Ocellus adds with incomplete size/state evidence; require explicit unavailable results and excluded intro/debug IDs.
- [x] Add only curated supported rows; retain uncertainty guards for incomplete adds and boss phases. Run focused tests then full suite.

## Task 3: Demonbane effectiveness

Files: DemonbaneRules.java, DemonbaneRulesTest.java and live integration tests.

- [x] Write failing tests: Dreadnoth/jelly use 25% of ordinary bonus, Xarth/Ocellus 80%, Duke 70%, Ice demon 115%; unrelated demons retain 100%. Preserve reference integer rounding.
- [x] Implement ID-based vulnerability in the existing shared helper, exercised by melee/ranged/Magic accuracy and damage callers.
- [x] Test real snapshot results for affected weapons, including saved spell and non-demon control; run focused and full suite.

## Task 4: Reproducible variant audit and remaining coverage

Files: scripts/audit_wiki_variants.py, scripts/test_audit_wiki_variants.py, docs/research/2026-10-24-encounter-audit.md, updated audit documents.

- [x] Add a read-only report tool with tests for duplicate IDs, virtual IDs, absent IDs and numeric differences; never mutate source data.
- [x] Compare current Wiki records against the recovered/updated snapshot; review all four raids, DT2, PNM, Zulrah and other notable mismatches in context of existing resolver/scaling/transform code.
- [x] Record classification, evidence/revisions, fixes, unresolved mechanics and prioritized live tests. No blanket dataset replacement.

## Task 5: Verification and GitHub delivery

- [x] Run Gradle clean test check build shadowJar and all Python tests; summarize exact counts and warnings.
- [x] Review full diff including recovered alpha2, secrets, unrelated files and resources; independent final review if available.
- [x] Recheck origin/main, commit only intended files, push new branch, open draft PR with limitations and live test checklist.
- [ ] Verify the project is open/imported in IntelliJ. Buildable folder and artifacts are ready; both launcher and Computer Use attempts exposed no targetable IDE window. Older projects were not changed.

## Execution ledger

- Baseline checks completed 2026-10-24: normal Gradle test succeeds; 259 tests, zero failures; Python 8 tests, OK.
- Wiki reference: 98e4fac38b580901cc3f8d485bbaa48bbb3277ed. RuneLite gameval reference: fc8d18969e3028af21a78fae38c9ab6c8c7573b1.
- Ruling: keep Bork unavailable. Current Wiki phase code requires Smorks health quartile and stun state; static GearScape fixture cannot establish live detection.
- Ruling: retain Ocellus 16674 and Guardian 16659/16660; pinned RuneLite definitions resolve conflicting Wiki IDs.

- Final verification: Gradle clean test/check/build/shadowJar successful; 271 Java tests, zero failures/errors/skips; Python 12 tests OK. Independent review finding fixed and re-reviewed. Final staged whitespace and credential-pattern checks passed.
- Delivery: source commit 85aaf0bb6ef3a84a562ed16b69e0572ea756e486 pushed to audit/alpha2-encounter-reconciliation; draft PR https://github.com/BornDied/combat-insight/pull/4 is open and unmerged. Main remains a0f2e060973d1e00a835bcd2afa1e95cfa298bca.
- Ruling: live implementation gaps remain explicitly documented; this delivery is a draft review build, not complete encounter support or release-ready.
