# Alpha3 follow-up: live Archive reports

This update follows published source commit `a556564e111b32582f14aa2a0c4aa3d5b3e4ecb4`, accepted through Plugin Hub PR [19333](https://github.com/runelite/plugin-hub/pull/19333). It addresses reported Bork/Ralos/Twisted bow, Xarth, Ocellus and oversized-HUD problems. Automated verification is recorded below; no gameplay was automated or independently verified.

## Changes

- Unavailable calculation values now show `?`. Notices occupy one compact row; detailed reasons remain in the optional Inspector and copied report. Minimal/Standard/Advanced settings, optional Inspector and special highlighting remain separate.
- Bork damage uses his observed stun or Smorks' unambiguous health quartile. Standing armour is 8/10/12/14 and Magic resistance is 0/2/4/6; stunned damage reduction is zero and melee accuracy is guaranteed. Ranged/Magic keep normal accuracy. Bork's current base crush defence is 10 and offensive Magic bonus is 0. The historical GearScape armour 10 describes one quartile, not every state; its crush 20/offensive Magic 1 are superseded by the current Wiki page. Missing or threshold-straddling Smorks bars still withhold Bork damage. Confirmed Smorks death is retained for that Bork actor only.
- Verified Bork Defence/Magic inputs can be tracked independently of damage-state uncertainty. Charged Ralos has two independent basic hits, and its basic/special per-hit maximum uses `M - floor(M/4)`. Its second special hit predicts accuracy after the first drain, bounded by the floor derived from original Defence. Uncharged specials remain unavailable.
- Equipped blowpipe tests exposed a forced-Ranged Rapid speed bug: `Rapid (forced)` previously used the ordinary interval. It now receives the same one-tick reduction as detected Rapid. Dragon and amethyst darts, ornamented blowpipe, ignored worn arrows, saved spells, light ranged defence and Smorks armour are covered through actual snapshot capture.
- Xarth corpses use distinct normal/doused IDs. The current strategy says doused corpses take guaranteed maximum melee/light-ranged hits; this is modelled as guaranteed accuracy and maximum, including the blowpipe. Water/ice's shorter projectile delay is not treated as a one-tick attack interval. Undead binders explicitly reject Crumble Undead without inferring a general undead attribute.
- Ocellus P1 melee is allowed when not protected; observed protection/deflect icons block the matching damage style. A named high idle identifies unreachable airborne melee. Blade hitbox 16676 and adds 16678–16681 combine published stats with observed cache size; a missing size leaves them unavailable. Intro/controller/debug actors remain excluded.
- Ocellus's final phase is recognised from curse icons or named final-phase animation sequences and retained per actor. Its damage/timing modifiers are not yet established: max, accuracy, DPS and specials show `?` outside observed blocked-style results. This avoids presenting ordinary phase-3 DPS during that phase.
- Active Warden P2 ranged/Magic attacks use the pinned Wiki accuracy-dependent damage range and guaranteed accuracy, gated by observed protection. Twisted bow gets the second accuracy scaling at P2. Enchanted bolts preserve the nonzero normal minimum before their respective replacement/additive proc transforms. Real core IDs 11770/11771 map to the published core variant, with observed size, guaranteed melee accuracy and maximum. Exposed body IDs are not substituted for the core.
- ToA manual party size/current path inputs resolve scaled HP with invocation/path/party/rounding order and separate core scaling. Unknown inputs retain no guessed HP. Identity-matched boss HUD HP can supply observed current/maximum health; shared extra bars and mismatched actors are excluded. Ruby effects require resolved current HP.
- Charged matching elemental tomes add 10% spell damage. Water tome PvM accuracy follows the current item-page 10% rather than the DPS source's older 20% TODO. Charge reads its active varp and requires the matching god cape. Saved spells do not apply these effects to powered or melee attacks.
- README now describes features, setup, inputs, modes, Inspector and reporting in plain language. Build diagnostics identify `0.9.0-alpha3`.

## Sources and reconciliation

- Wiki DPS source/data pinned to [`98e4fac38b580901cc3f8d485bbaa48bbb3277ed`](https://github.com/weirdgloop/osrs-dps-calc/tree/98e4fac38b580901cc3f8d485bbaa48bbb3277ed): `PlayerVsNPCCalc.ts`, `dists/bolts.ts`, `scaling/{Phases,TombsOfAmascut,DefenceReduction}.ts`, constants and monster data. Do not treat its phase TODOs as verified live detection.
- RuneLite definitions pinned to [`fc8d18969e3028af21a78fae38c9ab6c8c7573b1`](https://github.com/runelite/runelite/tree/fc8d18969e3028af21a78fae38c9ab6c8c7573b1): NPC/item/varbit/varp/animation definitions. Build resolved RuneLite **1.13.4**. Animation names establish candidate live signals; their full transition coverage still needs client testing.
- Current [Bork page](https://oldschool.runescape.wiki/w/Bork), modified October 24 at 10:34 UTC; [Magic resistance](https://oldschool.runescape.wiki/w/Magic_resistance), 03:01 UTC; [Archive strategies](https://oldschool.runescape.wiki/w/The_Fractured_Archive/Strategies), 19:13 UTC; [Xarth corpse](https://oldschool.runescape.wiki/w/Corpse_(Xarth)), 12:29 UTC. Read through the browser because direct retrieval failed. These resolve the earlier Bork conflict and establish the new observed-state guards; some strategy claims remain explicitly unconfirmed.
- [Tome of water](https://oldschool.runescape.wiki/w/Tome_of_water) / [Magic damage](https://oldschool.runescape.wiki/w/Magic_damage) current PvM bonuses override the conflicting DPS TODO for Water accuracy. Other tome bonuses follow matching element and charged item ID.
- GearScape's preserved controlled fixtures remain in [GEARSCAPE-COMPARISONS.md](../GEARSCAPE-COMPARISONS.md). They verify supplied numerical inputs, not universal Bork armour or current live state.
- Repeated the variant comparison against the same dataset after Bork's base correction: **29 unmatched live-ID variant records, 2 calculator-only records, 45 numeric candidates**. [Delta report](2026-10-24-alpha3-wiki-delta.json) identifies the removed Bork candidate. The older 38/46 and alpha2 29/46 counts are historical audit results, not missing-species or confirmed-bug counts.

## Defence floors checked

These floors follow the [launch hotfix](https://secure.runescape.com/m=news/the-fractured-archive---out-today?oldschool=1), current Wiki reduction source and strategy table. Predictions and local tracking must use original base Defence. Ocellus phase changes reset stats; quadrant movement within P3 does not reset drains.

| Target/form | Base | Lowest Defence |
| --- | ---: | ---: |
| Archive Guardian | 260 | 156 |
| Bork | 300 | 180 |
| Ezekiel | 130 | 78 |
| Moia | 195 | 117 |
| Xarth | 170 | 102 |
| Ocellus P1 | 215 | 129 |
| Ocellus P3/final | 250 | 150 |
| Smorks / Dreadnoth / Trio / Blade | 220 / 125 / 100 / 250 | 0 |

## Remaining limitations

- Bork damage can remain `?` before Smorks has a usable health bar, near quartile boundaries, or after missed death/stun observations. This does not disable separately verified local drain tracking.
- Trio shield/imp-next-hit effects and Moia shield/phase calculations remain unavailable. The strategy still describes the imp bonus as unknown. Guardian output remains unavailable after tracked Magic changes until its unusual absorption interaction is established.
- Ocellus final-phase damage reduction, dodge-dependent attack cooldown and censer effects remain unverified. Final-phase detection requires an observed curse/sequence; live transitions, late entry and airborne animation coverage need testing. Add kill effects are not added to continuous basic DPS; ordinary stat boosts already appear in the client's boosted levels.
- Blade DPS describes direct damage to the hitbox; it does not count the stored release as a second ordinary hit or infer phase-3 HP. Adds use published stats and observed size, not guaranteed universal mechanic coverage.
- Archive party HP is used only when a matching unshared boss HUD identifies the target's HP. No generic party formula is invented. Local drains do not include teammate reductions.
- Warden P2 special distributions and non-melee core data remain unavailable. Ambiguous shared P3/enrage variants remain guarded. Disputed Verzik-colour add records, other ambiguous variants and the remaining audit candidates retain their documented limitations.
- Rondache shield accuracy and DPS remain unavailable; the Inspector's charge maximum is not a full shield attack calculation. Bouncing/area multi-target DPS remains unavailable where geometry/timing is not modelled.
- No in-game confirmation has been performed by this development session. Automated fixtures validate inputs/formulas, not server behavior or all raid phases.

## Verification

Commands run on Windows with Temurin Java 11.0.32 and Gradle 8.10:

```powershell
.\gradlew.bat clean test check build shadowJar --no-daemon --console=plain
python -m unittest discover -s scripts -p 'test_*.py'
git diff --check
```

- Clean Gradle run: **BUILD SUCCESSFUL in 14s; 8 actionable tasks, 8 executed**.
- JUnit XML: **305 tests, 68 suites; 0 failures/errors/skips**.
- Python: **12 tests, OK** (installed Python executable used because the Windows alias is unavailable).
- Built `combat-insight-0.9.0-alpha3.jar` and `combat-insight-0.9.0-alpha3-all.jar`; generated artifacts remain ignored.
- Existing deprecation/unchecked warnings remain. No checks were disabled.
- New regression failures were observed before the corresponding fixes for Warden distributions, bolt minima, Bork/Ralos tracking and rounding, forced Rapid speed, compact HUD sizing, corpse/binder rules, and Ocellus final/airborne guards. A reviewer raised a Magic-armour concern and then withdrew it after checking the calculator's existing exclusion; it is not counted as a confirmed bug.
- Independent read-only code review covered drains/floors, HP identity, actor-state lifecycle and equipped blowpipe paths. The corpse accuracy interpretation above is a specific live-test item. Review is not in-game verification.

## Prioritized live checklist

1. Confirm diagnostic version alpha3. Bork: Twisted bow and charged Ralos before/during/after stun; Smorks at all four HP quartiles and after death. Verify two local Ralos drains stop at 180 and don't carry to a new actor.
2. Blowpipe: dragon/amethyst darts, Rapid vs Accurate, Automatic vs forced Ranged, ornamented variant, worn arrows and saved Fire Surge. Compare Xarth/Smorks/Ocellus with copied actual gear, boosts and prayer.
3. Xarth corpse: normal to doused, melee/light maximum and accuracy, Magic weaknesses; confirm no invented HP. Ocellus: P1 protection, P2 Blade, adds, grounded/airborne P3, final-phase `?` and blocked-style `Immune`, death/reset/re-entry.
4. Verify max drain floors for the table above and phase resets. Check Emberlight/Scorching bow/Shadow/Twisted bow, Obligator modes, Tome follow-ups, Ascension ammo, Zeal/Piety and fragment matching.
5. ToA P2 overhead swaps, enchanted bolt procs and real cores; change party/path/invocation inputs and check ruby HP. Recheck CoX/ToB modes, DT2 normal/awakened, PNM pillar and Zulrah per-hit cap.
6. Check all three HUD modes, `?` widths, optional Inspector and special highlighting. Copy a report with the NPC ID and input state whenever a value differs.
