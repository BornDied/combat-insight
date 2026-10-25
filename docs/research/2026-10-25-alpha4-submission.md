# Combat Insight 0.9.0-alpha4 submission

The user authorized pushing the new feature branch and submitting a RuneLite Plugin Hub follow-up on October 25. This supersedes the earlier local-only checkpoints; their historical notes remain intact. No direct push to main, merge or release publication is authorized.

## Scope since the published alpha3 commit

Published baseline: `df00806805617dc97a7b2a494d10a2721dd185ae`, accepted by [Plugin Hub PR #19348](https://github.com/runelite/plugin-hub/pull/19348). Alpha4 includes the local Archive checkpoint and ruby-bolt preflight fix. This remains partial encounter support, not a claim that every raid phase or temporary effect has been solved.

- Account for all 28 attackable Archive cache IDs, including the Trio Imp, Animated Sword and active Inert Guardian. Observed footprint sizes reach the actual Scythe/Obligator calculation without clearing unrelated stat uncertainty.
- Enable ordinary calculations for all six Trio forms and normal Moia ranged/Magic attacks; correct weakened Trio and grounded Guardian base HP records.
- Apply style-specific maximum hits to Ocellus adds, reset reductions across Ocellus combat phases, and retain reductions within a phase. Block out-of-reach Voidwaker specials.
- Add explicit starting-party input for published Archive HP cells. Automatic roster stays diagnostic; matching unshared boss HUD HP takes priority. Blank or conflicting data is not extrapolated.
- Withhold unknown Ezekiel enrage, observed imp-buff and positive Moia shield calculations. Enchanted bolts on guaranteed-max targets remain unavailable pending verified proc composition.
- Prevent unresolved enchanted bolts, including ruby bolts with unknown HP, from displaying an ordinary unenchanted maximum or combined accuracy.

## Plugin Hub page wording

The README now leads with what the plugin displays, then explains installation, display modes, optional Inspector, special highlighting and the inputs players need to set. Calculation research stays in linked notes. Both short descriptions use the same direct wording.

RuneLite's [page loader](https://github.com/runelite/runelite.net/blob/master/src/modules/plugin-hub.js) reads README at the manifest's pinned commit. Its Recent updates section uses Plugin Hub PR descriptions. The submission therefore updates both README and a concise, plain-language PR description. The public page changes only after RuneLite accepts the new manifest; no website deployment is performed here.

## Verification

- Java 11.0.32, Gradle 8.10, RuneLite 1.13.4.
- `gradlew.bat clean test check build shadowJar --no-daemon --console=plain`: BUILD SUCCESSFUL in 15s, 8 tasks executed.
- 336 JUnit tests / 73 suites; 0 failures, 0 errors, 0 skipped.
- 12 Python tests passed.
- Packaged developer entry point: `java -ea -jar build/libs/combat-insight-0.9.0-alpha4-all.jar --help` exited 0. This is not a GUI/plugin-injection test.
- Existing deprecation/unchecked warnings remain. Diff whitespace, intended files and secret-pattern review passed. No in-game verification or account access was performed.

## Limits and live checks

Remaining data gaps: imp bonus/consumption, Ezekiel enrage curve, Guardian Magic absorption, Ocellus final/censer effects, some sword/statue defences, missing HP cells, Warden special/core cases and Rondache shield accuracy/DPS. Xarth shield timing, transient invulnerability, late-entry cues and teammate drain synchronization are not complete encounter models.

See [Archive sources, coverage and limits](2026-10-24-archive-completion.md) and [preflight results](2026-10-25-preflight.md). First live checks are plugin startup/off-on/Inspector, all HUD modes, then Trio forms/imp transitions, Ezekiel swords/enrage, Bork/Ralos and Xarth blowpipe/corpses, Ocellus phase resets/protection/adds and party HP. Use diagnostic version 0.9.0-alpha4.
