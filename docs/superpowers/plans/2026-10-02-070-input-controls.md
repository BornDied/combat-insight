# Combat Insight 0.7 Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans inline.

**Goal:** Make input provenance, local reductions and unsupported calculations clear without enlarging the default HUD.
**Architecture:** Keep calculation snapshots immutable. Put raid observation and input notices in small classes. Configure tracking separately from display.
**Tech Stack:** Java 11, RuneLite, JUnit 4, Python coverage checks.
**Spec:** docs/superpowers/specs/2026-10-02-070-input-controls.md

## Global Constraints
- Preserve all 0.6 fixes. No commits/push/publication.
- Preserve per-mode row settings. HUD indicator opt-in.
- Do not claim exhaustive NPC/mechanic support.

## Review Focus
- Toggling tracking during a pending special must cancel that reduction.
- Reset must not lose the next special-energy transition.
- Dead party members and hidden/invalid orbs must not reduce ToB scale.
- Saved spells on powered staffs/melee must be reported as ignored.
- Diagnostics must describe one immutable calculation and exclude player identity.

### Task 1: Reduction controls
Files: SpecialAttackTracker.java, DefenceTrackingMode.java, CombatInsightConfig.java, CombatInsightPlugin.java, CombatInsightOverlay.java, SpecialAttackTrackerTest.java.
Interface: setTrackingMode(DefenceTrackingMode), clearTrackedEffects().
- [x] Write tests: off cancels pending and existing drains; re-enable does not resurrect drains; reset preserves energy baseline.
- [x] Run verify.py SpecialAttackTrackerTest and observe failure.
- [x] Implement controls and client-thread reset menu; default local mode preserves existing use.
- [x] Run targeted tests; expect PASS.

### Task 2: Conservative raid observation
Files: TheatrePartyTracker.java, RaidScaling.java, CombatInsightPlugin.java, CombatInsightConfig.java, TheatrePartyTrackerTest.java.
Interface: observe(boolean enabled, int status, int[] orbs), clear(); RaidScaling.withObservedTheatre(int).
- [x] Write tests for live/dead/hidden/invalid orbs, party loss, lobby reset and disabled mode.
- [x] Observe missing implementation failure.
- [x] Implement observed highest valid roster for active raid; opt-in setting, manual fallback; source summary.
- [x] Run targeted tests; expect PASS.

### Task 3: Input explanations and quiet HUD
Files: CalculationInputNotes.java, LiveCombatSnapshot.java, CombatInsightConfig.java, CombatInsightOverlay.java, tests.
Interface: immutable notes, summary, needsAttention; snapshot getter for optional label marker.
- [x] Test manual/ignored spell, forced style, unavailable results and diagnostics.
- [x] Observe failure; implement notes and optional marker with no new default rows.
- [x] Update organized settings descriptions and version 0.7.0.
- [x] Run full suite; expect PASS.

### Task 4: Coverage, review and packaging
- [x] Audit source guards and generate coverage document distinguishing NPC rows from mechanics.
- [x] Independent code review; fix important findings with regression tests.
- [x] Verify cumulative patch from live 0.5 and byte-match ZIP source.
- [x] Save new 0.7 artifacts and Windows/in-game checklist; leave 0.6 unchanged.
