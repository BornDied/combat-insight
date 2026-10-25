# Archive follow-up: local alpha4

This work is **local and unpublished** on `local/archive-completion`, based on `df00806805617dc97a7b2a494d10a2721dd185ae`. No push, PR update, release, merge or Plugin Hub change was made during this follow-up. It is not a claim of complete encounter support or in-game verification.

## Sources and conflicts

- [Wiki DPS calculator](https://github.com/weirdgloop/osrs-dps-calc/tree/ad3b2539a63fb82058595b8702dcf0d7c1a0c181): latest fetched revision `ad3b2539a63fb82058595b8702dcf0d7c1a0c181`, compared with the preserved alpha3 pin `98e4fac38b580901cc3f8d485bbaa48bbb3277ed`. [Delta](2026-10-24-alpha4-wiki-delta.json) and [variant audit](2026-10-24-alpha4-wiki-variants.json). There are 2,919 variants, 29 unmatched live-ID variant records and 46 numeric candidates. These remain variant comparisons, not counts of missing species or confirmed bugs. The new Imp record contains placeholder zero skills; it is not imported blindly. Other changes in the delta are spell-style capitalization, not new damage formulas.
- [RuneLite game definitions](https://github.com/runelite/runelite/tree/fc8d18969e3028af21a78fae38c9ab6c8c7573b1/runelite-api/src/main/java/net/runelite/api/gameval): pin `fc8d18969e3028af21a78fae38c9ab6c8c7573b1`. NPC IDs, named animations, graphics, items, varps and varbits were checked.
- Local game cache read offline through official `net.runelite:cache:1.13.4`, NPC archive revision `1792425499`. No cache save or gameplay access. SHA256 of the 49-definition extraction: `585ad6eec3fc930e7f439c726601f21300105042c1f709ff944346492544245e`. The sanitized roster fixture records only NPC IDs, names, sizes, Attack options and default base HP. Cache defaults are not proof of server hotfix values: Ezekiel cache Defence200 conflicts with current Wiki130; Moia cache HP1900 conflicts with Wiki785; Jelly cache HP75 conflicts with Wiki61. Existing hotfixed Wiki combat values take priority. Newly researched sword/statue base values are explicitly cache-derived; ranged defences are not fabricated.
- Current [raid strategies](https://oldschool.runescape.wiki/w/The_Fractured_Archive/Strategies), [raid overview](https://oldschool.runescape.wiki/w/The_Fractured_Archive), [Moia](https://oldschool.runescape.wiki/w/Moia), [Ezekiel](https://oldschool.runescape.wiki/w/Ezekiel), [Bork](https://oldschool.runescape.wiki/w/Bork), [Smorks](https://oldschool.runescape.wiki/w/The_Smorks) and [Guardian](https://oldschool.runescape.wiki/w/Archive_Guardian) inspected October24. Practice mode is described as forthcoming; no invented hard/challenge multipliers.
- Existing [GearScape fixtures](../GEARSCAPE-COMPARISONS.md) remain static comparisons. This follow-up adds regression coverage, not new GearScape UI measurements. No GearScape implementation was copied.

## Complete cache combat roster

All 49 Archive definitions16640–16688 were classified by actual Attack options. **28 attackable IDs** now have profiles; the bundled database has **4,004 IDs**. Coverage of an ID does not imply every phase/buff calculation is known.

| Combat IDs | Footprint | Behavior and limits |
|---|---|---|
|16640 Bork /16641 Smorks|4 /3|Existing stun/quartile armour and separate drain tracking retained; physical armour8/10/12/14, stunned0. Ambiguous Smorks quartiles stay unknown.|
|16643–16648 Trio|2|All six ordinary forms calculate. Weakened44/46/48 have cache base HP7, not640. Shared/shield bars do not establish an individual's current HP or shared drain state.|
|16650 Imp|1|Added; successful maximum hits. Cache Magic125 retained for Tbow instead of placeholder Wiki0. Observed imp buff application makes unresolved next-hit calculations unavailable.|
|16652 Ezekiel|3|Ordinary attacks and size-sensitive weapons calculate. Finale animations14694–14697 or the conservative below-one-third HP interval withhold the unknown enrage damage curve, including specials. State persists for the actor until removal.|
|16653 Animated Sword|3|Added cache Defence115/Magic90, melee/Magic defence100; current strategy air weakness100%. Ranged defence unavailable. The80-point charge bar is not used as boss HP.|
|16659/16660 Guardian|5|Flying/grounded variants retained; grounded base HP corrected1100. Ancient immunity and unverified Magic-drain guard retained.|
|16662 active Inert Guardian|2|Added cache Defence/Magic50 and melee/Magic bonuses. Ranged defence and earth-weakness severity remain unavailable. Inactive statue16661 is not attackable.|
|16665 Dreadnoth /16666 Jelly|5 /1|Existing demonbane effectiveness, fire weakness and ordinary combat inputs retained. No assumed add HP scaling.|
|16667 Moia /16670 binder|3 /1|Normal ranged/Magic Moia rolls enabled. All melee weapons, including halberds and Voidwaker specials, are out of reach. Positive shield varbit16418 withholds its unknown modifier. Binder Crumble Undead exclusion retained.|
|16674 Ocellus P1 /16676 Blade /16677 P3|3|Blade geometry resolved; base HP1000 is retained as source data, not a party HP claim. Combat phase changes reset local reduction state; quadrant movement within the same ID preserves it. Existing protection, airborne and final-phase guards retained. Voidwaker cannot bypass physical reach using Magic damage.|
|16678 Rift /16679 Siphon /16680 Augmenter /16681 Vine|1|Sizes resolved. Rift gets maximum melee, Siphon maximum ranged, Augmenter maximum Magic. Vine calculations assume an eligible teammate's target. Bounces, kill-triggered cooldown resets/healing and target eligibility are not continuous single-target DPS.|
|16684 Xarth /16687–16688 corpses|3 /1|Existing demonbane resistance, water weakness, normal/doused corpse rules retained. Initial shield timing/modifier remains a live research item; no ordinary tormented-demon shield formula is borrowed.|

Excluded IDs:16642 HP controller;16649 cauldron;16651 portal;16654–16656 sword effects;16657/16658 pre-fight/dead guardian;16661 inactive statue;16663 destroyed statue;16664 hazard;16668/16669 invisible actors;16671–16673 introduction actors;16675 phase controller;16682 debug;16683 travel Blade;16685/16686 rubble.

Observed transformed composition size overrides bundled size without clearing unrelated stat uncertainty. Scythe tests cover sizes1/2/3 and actual equipped attacks; Obligator tests cover all three modes, the same sizes, every Trio form, Blade and Xarth. Tbow/Shadow/Ralos tests run all six Trio forms. Existing blowpipe dart, ornament, Rapid, saved-spell and corpse tests remain in the full suite.

## Party HP: known cells, explicit input

Clientscript10039 counts every nonzero five-bit roster slot;10034 sums eight slots. Values30/31 still count. Own-slot16395 is one-based. Status0 is no party; status2 prompts entry. The decoder rejects invalid slots/statuses. The largest observed in-combat roster is retained through region loading and cleared at logout/no-party. This count is diagnostic: first observation mid-raid can miss departed members.

Set **Raid inputs → Archive starting party size** to1–8 to use a published HP table cell. Default0 keeps party-dependent HP unknown unless a matching, unshared boss HUD supplies HP. A manual starting roster does not shrink after deaths. Observed boss HUD HP takes priority. Unknown cells stay unknown, even with a valid party input.

Supported tables: Bork and Smorks all sizes; Ezekiel1–7; flying Guardian1/2/3/5/8; grounded Guardian1–8. Solo uses the same published two-player minimum. Blank cells and other bosses/adds are deliberately not interpolated. The strategy page says43.63% per additional player, while several boss tables fit a different sequence; neither a linear extrapolation nor a guessed universal multiplier is safe. Shared Trio shields and Blade stored damage are not individual HP. This prevents speculative ruby-bolt HP damage.

## Remaining evidence gaps

- **Imp-enhanced next attack:** current Wiki explicitly lacks the bonus formula. Graphic4162 has a finite122-cycle animation, not a reliable buff lifetime. Once seen, uncertainty is retained until the Trio room is no longer observed; this can conservatively keep `?` after the buff was consumed. Enabling mid-room may miss the application. Exact consumption, misses, multi-hit/spec interaction and duration need observed evidence before precise support.
- **Guaranteed max with enchanted bolts:** the ordinary proc mixture was incorrectly used for these targets. Basic/special damage is now unavailable on guaranteed-max targets with enchanted crossbows, including Siphon/Imp. Wiki's general always-max transform does not independently establish proc behavior for these new IDs. Plain ammo, bows and blowpipe use supported maximum-hit paths.
- **Ezekiel enrage:** damage increases as HP falls; no verified curve or rounding order in current sources. Ordinary phase is supported; finale is `?`.
- **Inert Guardian / Sword:** ranged bonuses unknown; statue earth severity unknown. Cache numeric defaults require live hotfix comparison. Charging/beam mechanics are not additional automatic player DPS.
- **Moia:** positive shadow-shield state has no verified formula. Normal calculations use current Wiki values. No inferred HP table.
- **Ocellus:** final-phase reduction and dodge-dependent cooldown, censers and their next-hit timing remain unverified. Phase detection requires observed cues. Add cooldown resets are conditional effects, not a new baseline weapon speed. Full fight DPS and Blade's later release are not doubled into direct attack DPS.
- **Guardian Magic absorption**, **Rondache accuracy/secondary DPS**, **Warden P2 specials/non-melee core**, disputed ToB variants and earlier four-raid audit limitations remain open. Latest Wiki calculator revision supplies no new implementation for these gaps. See [prior room/mode audit](2026-10-24-encounter-audit.md).
- **Xarth initial shield** and other transient invulnerability/eligibility windows need diagnostics; baseline DPS assumes the selected target is damageable. No teammate drain synchronization, area/bounce DPS, movement or fight-wide uptime model.

## Review decisions

1. Use exact published HP cells plus an explicit starting size, not extrapolation or a potentially partial automatic roster. Cost: some HP-dependent results remain unavailable.
2. Enable ordinary Trio/Moia attacks separately from unknown buffs/positive shield states. Cost: brief or missed live state cues remain a required test; ordinary baseline is not full encounter DPS.
3. Retain imp-buff uncertainty beyond graphic expiry. Cost: conservative unavailable output for the remainder of that room.
4. Guard enchanted-proc/guaranteed-max combinations rather than invent proc guarantees. Cost: those loadouts remain unavailable on the affected adds.

Independent read-only review found the Voidwaker reach bypass and enchanted-bolt guaranteed-max averaging bug. Regression tests reproduced both before correction. The full verification result is recorded below after final checks. No in-game testing was performed.


## Verification

- `gradlew.bat clean test check build shadowJar --no-daemon --console=plain`: **BUILD SUCCESSFUL in25s;8 tasks executed**.
- JUnit XML: **329 tests /72 suites,0 failures,0 errors,0 skipped**.
- `python -m unittest discover -s scripts -p 'test_*.py' -v`: **12 tests, OK**.
- Built `combat-insight-0.9.0-alpha4-local.jar` and `combat-insight-0.9.0-alpha4-local-all.jar`. Generated artifacts are ignored. Java11.0.32 / Gradle8.10 / RuneLite1.13.4; existing deprecation/unchecked warnings remain.
- Independent review's two important findings were reproduced RED, corrected and included in the green full suite. Safety regressions also cover partial observed roster versus manual starting size, invalid size, blank HP cells, enrage persistence, footprint propagation and conservative buff uncertainty.
- Diff whitespace and intended-file review passed. No credentials, account data, generated cache binaries or unrelated project files are included. No in-game verification, new GitHub push, PR edit or Plugin Hub submission.

## Prioritized live checklist

1. Confirm `0.9.0-alpha4-local` in copied diagnostics. Trio: all Elder/Monk ordinary and weakened forms, Scythe two-hit split, Obligator three modes, Tbow/Shadow/Ralos; then Imp guarantee and buff `?`. Copy state before/after imp death and your next attack, including a miss or special.
2. Ezekiel: ordinary three-hit Scythe/size bonus; charged swords with air spell and ranged `?`; finale animation/low-HP guard; leave/re-enter/death reset. Capture input/output damage pairs needed to establish the enrage curve.
3. Archive starting party size1–8, including solo/two-player equivalence, deaths and loading. Compare published HP cells against matching boss HUD HP; blank cells must remain unknown. Set0 afterward. Automatic roster is diagnostic only.
4. Ocellus: P1→Blade→P3 drain reset, same-phase quadrant preservation, style guarantees on Rift/Siphon/Augmenter; crossbow enchanted cases `?`; protection, airborne melee/Voidwaker block and final-phase guard. Confirm Vine eligibility with a teammate.
5. Recheck Bork Tbow/Ralos through Smorks quartiles/stun/death and floor180; Xarth blowpipe darts/Rapid/ornament and normal/doused corpses. All three HUD modes must remain compact. Current live Plugin Hub code will not contain these local changes.
