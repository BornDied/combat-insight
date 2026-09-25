# Compact HUD Implementation Plan

> Execute locally with the executing-plans workflow. Do not commit, push or publish.

**Goal:** Combine the existing audited 0.5.0 raid/weapon candidate with small HUD refinements approved in chat.

**Architecture:** Keep the existing OverlayPanel, row order, Minimal/Standard/Advanced groups and all saved keys. Add compact sizing, quiet routine colors and background presets. No new combat logic or detail shortcut in this increment.

**Tech Stack:** Java 11, RuneLite 1.12.39, JUnit 4.12.

**Spec:** User-approved small HUD preview and discussion in this thread, September 25, 2026.

## Global constraints
- Local test build only; BSD 2-Clause and build=standard remain.
- Existing row toggles and saved custom background remain valid.
- Routine color changes must preserve warnings and max-hit change flashes.
- Width must stay stable when values change; respect explicit user overlay resizing.
- Existing raid limitations must remain documented.

## Review focus
- Long status text must wrap visibly instead of clipping at compact widths.
- User-resized overlay must retain the requested width.
- Switching background presets must not overwrite the saved custom color.
- Minimal mode must still exclude Standard/Advanced rows.
- Disabled row choices must survive mode changes.

## Task 1: HUD refinements
- [x] Add actual-render regression checks for mode boundaries and compact/wide transitions, using the real RuneLite panel.
- [x] Implement background preset enum; add three config options under HUD, keeping existing config keys.
- [x] Apply narrower preferred widths and small borders for compact mode, keep numbers right aligned and restore classic sizing when disabled.
- [x] Use neutral routine colors when enabled; retain warning and change feedback.
- [x] Compile all Java and run the full JUnit suite; run generator tests.

## Task 2: Review and local delivery
- [x] Obtain a fresh read-only review of the combined changes.
- [x] Address material findings and inspect a rendered overlay image.
- [x] Update documentation; export cumulative patch from c2f0a13 and compare a fresh application against all packaged files.
- [ ] Save one combined source/patch ZIP with setup guidance.

## Execution notes
- Existing isolated checkout reused. No commits: explicit user project restriction.
- Direct javac/JUnit verification uses real RuneLite dependencies; Gradle distribution download was blocked in the previous review environment. Report that limitation if it remains.
- The hold-to-show-details shortcut remains deferred, matching the latest agreed scope.
- Render regression reproduced the constructor width override before its removal.
- Review reported no material runtime defect. Its test-coverage concern was
  addressed by additional actual-render tests and direct inspection of a long
  unavailable message using the bundled RuneLite font. Three core rows rendered
  at 190x54 compact / 225x60 classic; long Verzik P1 status remained readable.
