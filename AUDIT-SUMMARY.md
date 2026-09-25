# Combat Insight 0.5.0 local review — September 25, 2026

This candidate combines the untested Wave 2A/2B update, the ToB/CoX review, and
the approved small HUD refinements. It is prepared for local testing, not released.

## Findings addressed

- The NPC generator excluded unique CoX CM IDs. Nine CM IDs now use normal base
  stats and receive runtime scaling once. Eleven ToB phase/add IDs were also added.
- Maiden phase thresholds were being used as full HP-bar maxima. They now retain
  the full base maximum, followed by party-size scaling.
- Raid stats needed scaling before locally tracked reductions. Manual raid inputs
  now feed both the calculation snapshot and reduction tracker consistently.
- Nylocas colour/style restrictions now apply across the supported ToB modes.
- API errors and incomplete cache files could produce empty or zero-stat snapshots.
  The generator now rejects these inputs before replacing the output.
- OverlayPanel's constructor-level width overrode mode-specific defaults. Automatic
  widths now follow the HUD mode, with explicit user resizing retaining precedence.

## Verification

- All main/test Java sources compile targeting Java 11 against RuneLite 1.12.39.
- 122 JUnit tests and 6 Python generator tests pass, including actual overlay
  rendering, mode boundaries, custom-background retention, and warning colors.
- Real RuneLite rendering with its bundled font checked compact/classic spacing
  and a long unavailable status. An illustrative image is included in the ZIP.
- Cumulative patch checked on a clean c2f0a13 checkout and compared with packaged source.
- A separate read-only reviewer found no material runtime defect in the HUD
  changes or targeted raid integration. Its rendering-coverage concern was addressed.

## Remaining validation

The environment cannot download the Gradle distribution (`Network is unreachable`),
so the standard Gradle task and actual client launch need your PC. No in-game raid
testing has been performed here. Data coverage is 3,976 IDs, including 160 audited
ToB/CoX IDs; 30 world IDs remain explicitly ambiguous. Data presence is not a claim
that all encounter mechanics work. README.md lists the withheld damage cases and
TESTING.md gives the focused in-game checklist. Team-caused drains remain untracked.

The hold-to-show-details shortcut and larger HUD card designs are not included.
No commits, pushes, Plugin Hub updates, or publication were performed.
