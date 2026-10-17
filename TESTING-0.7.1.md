# Combat Insight 0.7.1-local.1: equipment variants

Base: GitHub main `98e49fd2c18534f376b1004a8adf1b3aec1c4813` (0.7.0).
Local testing candidate only; not committed, pushed, or submitted to Plugin Hub.

## Changes

- Recognize ornamented and locked regular/elite Void pieces, including mixed cosmetic variants, for Melee, Ranged, and Magic set effects.
- Recognize ornamented Berserker necklace for the obsidian weapon damage bonus.
- Restore missing weapon categories for Dragon warhammer (or), ID 26710, and Uncharged trident (o), ID 33434.
- Read current Soulreaper stacks from RuneLite `VarPlayerID.SOULREAPER_STACKS` (3784) for basic attacks with Soulreaper axe (28338) and Soulreaper axe (o) (33335). Add the integer-rounded 6% per stack boosted-Strength bonus separately from prayer before stance/Void. No stack effect leaks onto other weapons.
- Keep functional item states such as broken, inactive, enhanced, and imbued distinct. No general stripping of all parenthesized text.

## Audit boundary

39 cosmetic weapon ID/base-ID pairs were checked against the bundled weapon categories using RuneLite's ItemID names. Tests also cover existing ornamented godsword, claw, warhammer, maul, fang, Scythe and Blazing blowpipe recognition. Colored crystal armour keeps the same in-game names; inactive armour is deliberately not normalized to active armour. Equipment bonuses still come from RuneLite's item-stat service; this audit does not independently certify every external stat entry or every combat mechanic.

Soulreaper's Behead special is **not implemented** by this patch. The new support is for current-stack basic max hit/DPS, not a future-stack ramp-up simulation. Soulreaper special rows remain unsupported. No new raid comparison diagnostics or gameplay automation were added.

## Verification

- Compiled all main and test sources for Java 11 against cached RuneLite client/API 1.12.39.
- 198 JUnit tests and 8 Python tests passed.
- Reproduced failures before fixes: missing ornamented/locked Void bonuses, screenshot max 21 instead of 24, missing Berserker bonus, missing warhammer category, missing Soulreaper stack support.
- Existing deprecation/unchecked warnings and headless SLF4J no-binding warning remain.
- Standard Gradle build and live Windows/RuneLite testing still required; cached-API compilation is not a latest-release compatibility guarantee.

## Quick live checks

1. Open the new source copy in a separate folder, or apply the incremental patch to clean 0.7.0 after `git apply --check`. Preserve any local work; do not overwrite a newer repository blindly.
2. In its terminal run `./gradlew.bat test` on Windows, then launch the existing Gradle `run` task. Confirm build 0.7.1-local.1 in copied diagnostics if needed.
3. With amethyst darts, Rapid, +58 total Ranged Strength, 104 boosted Ranged, no prayer, and full elite Void (or): max hit should be 24 rather than 21. Letting the boost decay can change this number. Compare exactly the same stats.
4. Compare ornamented and plain Void at unchanged levels/prayers. Max, accuracy, DPS, and supported specials should agree. Remove gloves/helm: the bonus must disappear. Try melee and mage helms too.
5. Compare Berserker necklace and Berserker necklace (or) with the same obsidian weapon and unchanged stats.
6. Compare normal and ornamented Dragon warhammer with the same combat stance and target. Confirm style and special calculations agree.
7. Compare both Soulreaper axes at equal stacks. Check 0 through 5 stacks, with/without Piety, then switch weapons while stacks remain: the other weapon must receive no soul bonus. Behead calculations are outside this patch.

## Research

- Jagex, July 22 2026 Gear & PvM update: https://secure.runescape.com/m=news/summer-sweep-up-gear--pvm-changes?oldschool=1 . Confirms five Soulreaper stacks, 6% per stack, and newer special rules. Do not use old special-attack documentation as current behavior.
- RuneLite gameval ItemID and VarPlayerID sources: verified cosmetic item names and SOULREAPER_STACKS.
- Existing Wiki calculator reference arithmetic was inspected to confirm basic stack bonus ordering; no implementation code was copied.
