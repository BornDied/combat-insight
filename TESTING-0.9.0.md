# Combat Insight 0.9.0-alpha2: local testing

**Alpha3 follow-up:** [current behavior, sources, limitations and tests](docs/research/2026-10-24-alpha3-follow-up.md). The alpha2 material below is retained as history; its Bork/Warden/HP and submission-status statements are superseded where described in that follow-up.

## October 24 continuation

The source ZIP has been recovered on an isolated review branch. See the [current encounter audit](docs/research/2026-10-24-encounter-audit.md) and [verification record](docs/research/2026-10-24-verification.md) for changes after the original alpha2 handoff below. This remains partial support, with no in-game verification.

- Obligator now reads the combat-mode index independently of stance: accurate Thrust, aggressive Thrust, aggressive Smash. Both automatic and forced melee are covered. Confirm the three live buttons/indices in-game before relying on the mapping.
- Test Xarth corpses before/after dousing, Dreadnoth jelly and undead binders. No Archive HP is assumed. Ocellus adds remain explicitly pending.
- Compare reduced demonbane effectiveness on Dreadnoth/jelly (25%) and Xarth/Ocellus (80%). Saved spells must not change powered attacks. Crumble Undead does not grant demonbane accuracy or an Ice demon exemption.
- Warden P2/core must show pending calculations; charging P3 must show Immune. Disputed Verzik coloured adds must show pending stats and no HP estimate in every mode.
- Bork remains unavailable: phase-dependent armour is now identified, but its live state and conflicting base stats are not resolved. Rondache shield accuracy/DPS also remains pending.

The original development-environment results below are historical; current Windows Gradle results supersede the earlier distribution-download blocker.

This is a partial implementation for local testing, not a Plugin Hub submission. Obligator, Tome and Ascension direct-damage calculations now include GearScape comparison fixtures. Full raid phases and Rondache accuracy/DPS remain incomplete. No GitHub push or RuneLite submission was performed.

## Open and run on Windows

1. Extract the source ZIP into a new folder, for example `D:\CombatInsight-0.9.0-alpha2`. Keep your working released project separate.
2. In IntelliJ, select **Open** and choose the extracted folder containing `build.gradle`. Let Gradle import finish.
3. Open IntelliJ's terminal in that folder. Run:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

4. Test in the developer RuneLite window launched by this project. Your ordinary Plugin Hub client still uses the published version. Use the optional inspector/copy diagnostic action to confirm `0.9.0-alpha2` before reporting results.

If testing by patch instead, the patch is based on commit `a0f2e060973d1e00a835bcd2afa1e95cfa298bca`, the checked 0.8.0 main baseline. Use a clean separate branch, run `git apply --check <patch>` before `git apply <patch>`, and do not force conflicts into an unknown newer checkout. The ZIP avoids needing the exact local baseline. The alpha2 patch replaces alpha1: do not stack it on an already patched alpha1 checkout. Use the complete alpha2 ZIP in a new folder.

## First tests

| Test | Expected |
|---|---|
| Prayer off → Piety → off on an account without Zeal | Existing numbers and name remain correct |
| Prayer off → Zeal → off on an unlocked account | Inspector identifies Zeal; Attack x1.25 / Strength x1.28; no double stacking with Piety |
| Matching unlocked elemental fragment | +2 base damage, then gear/weakness bonuses; inspector lists the upgrade |
| Other element or Ancient spell | No fragment bonus from an unrelated element |
| Saved Fire Surge while using Voidwaker / Shadow | Saved spell does not apply fragment damage to the weapon |
| Ascension crossbows, plain Ascension bolts | Heavy ranged; 3-tick accurate / 2-tick rapid; ammo strength included |
| Ascension crossbows with ordinary bolts, or another crossbow with Ascension bolts | Ascension crossbow reports missing compatible ammo; incompatible ammo is not selected |
| Ascension diamond/onyx enchanted bolts | Proc-aware maximum and DPS; test diary off/on |
| Zorya’s tome, no buff → successful spec → three follow-ups → buff ends | Normal Magic-scaled max → x1.6 max / x2.5 accuracy / 2 ticks; spec preview does not double-boost while empowered |
| Tome uncharged / empowered after special | Uncharged withholds damage; empowered stacks update damage, accuracy and speed |
| Rondache equipped, gain/spend charges | Mainhand spec unchanged; Inspector base shield-bash max 11 at 10 charges / 24 at 20, before target mitigation |
| Obligator Smash → Thrust; small → large targets | 3 → 5 ticks; Thrust size bonus and best-of-roll DPS; uncharged version asks to charge |

## Raid tests

- Guardian flying → grounded: check the target ID changes 16659 → 16660, fire weakness and melee eligibility. Ancient spells should show Immune with zero chance/DPS. Standard spells and powered weapons should remain separate. If a locally tracked Magic drain occurs, calculation and Magic display are withheld pending its unusual interaction; do not use the predicted drain as verified data.
- Smorks, Ezekiel, Dreadnoth, Xarth, Ocellus P3: compare weapon, actual boosts, prayer, attack style and damage against known inputs. Values use published launch stats and need live validation.
- Bork, Trio, Moia, Blade/P2 and Ocellus P1 melee: expect explicit pending state. A numeric damage result for these unresolved cases is a bug.
- Defence drains: the supported hotfix floor is 60% of base for Guardian/Bork/Ezekiel/Moia/Xarth/Ocellus P1/P3. Dreadnoth, Smorks, Trio and Blade have no floor. Party members' drains are not automatically synchronized.
- Archive target HP should be unavailable, not a guessed party-scaled value. Ruby bolt/HP-dependent damage must not use base HP as current party HP.
- Nex: crush defence bonus 35 in bundled input data.

## Regression checks

Use the previous PNM pillar + Shadow, Voidwaker, elite Void ornament + blowpipe, Zulrah, CoX CM, and ToB Nylo cases. Preserve your normal settings for Minimal/Standard/Advanced; no config layout redesign was made. Existing unresolved issues remain unresolved unless specifically listed as implemented in the coverage document.

For a mismatch, copy the inspector diagnostic and include a screenshot of equipped gear, boosts/prayer, target phase, party size and the observed hit. One observed maximum is not proof of the full damage distribution, but it can disprove a maximum that is too low.

## Verification performed in the development environment

- Compiled main and test Java with Java 11 target against RuneLite client/API 1.13.3 and cached supporting dependencies.
- JUnit: **259 tests passed** (Java 11 compile + JUnit), including GearScape numeric fixtures and live-adapter regression tests.
- Python data tools: **8 tests passed** (`python -m unittest discover -s scripts -p 'test_*.py'`).
- `git diff --check`: clean.
- Normal `bash gradlew test --no-daemon`: **not completed**; the environment could not reach the Gradle distribution download (`Network is unreachable`). This is not a successful Gradle build. Run the Windows commands above before gameplay.
- Controlled GearScape comparisons performed for Obligator, Tome, Ascension bolts and Rondache charge maximum; see docs/GEARSCAPE-COMPARISONS.md. No in-game tests performed.

See [coverage, sources and remaining work](docs/FRACTURED_ARCHIVE.md).
