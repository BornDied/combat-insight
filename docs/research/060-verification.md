# Verification — October 1, 2026

Baseline: live0.5.0 commit8cbdcf0d028c1c0b4146e798bb1f59890cfe2294.

- All main and test Java sources compiled with `--release 11`, using the real RuneLite client/API1.12.39 and JUnit4.12 libraries.
- 169 JUnit tests passed. Baseline122; new tests cover DT2 variants, Vardorvis health, overrides and drain reset, Duke demonbane, halberds/fang/scorching, bolt mixtures/Zaryte, raid transforms, CoX input validation, held HUD state/focus, diagnostic shape.
- 8 Python generator/comparison tests passed.
- `git diff --check` passed.
- Fresh independent review found two integration bugs; both fixed and re-reviewed: Ice demon demonbane exemption/effectiveness and Sanguinesti proc ordering through raid transforms.
- Actual RuneLite overlay rendering: configured compact Standard190×54, held Advanced250×118 for the illustrative enabled rows, restored Standard190×54. Preview is in `docs/preview/held-details.png`; it is illustrative, not live combat evidence.
- Gradle wrapper bootstrap could not download due this environment's network restrictions. No claim of a successful standard Gradle build or current-latest RuneLite compatibility beyond the compiled API1.12.39.
- No in-game/Windows validation here. No commits, push, PR, Plugin Hub changes or publication.

See `060-mechanics.md` for incomplete and guarded coverage. Passing unit tests is not equivalent to exhaustive NPC or special-weapon support.

Seeking/rupture follow-up: nine new regressions cover clamped damage distributions, armour/raid ordering, dark-bow accurate hits and misses, bow specials, active-ammo rules, missing equipment data, unchanged current RuneLite entries, live Tbow/Nylo calculation and combined equipment totals. Independent follow-up review found no blocker; final suite count includes the two live-path tests.

October 1 follow-up: user screenshots showed a saved spell substituting 25 basic/37 special for Voidwaker's melee-derived 43/64. New regressions cover saved spells versus actual styles, independent Voidwaker melee inputs, incompatible special inputs, pillar combat/HP resolution and magic multiplication, NPC phase identity, powered-staff elemental leakage, and salamander exemptions. The independent review found the salamander Ice demon leak; it was corrected before packaging. No in-game verification claimed.
