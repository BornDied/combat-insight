# PNM and saved-spell audit — October 1, 2026

Scope: reported live0.5 Voidwaker and Nightmare pillar failures, plus related input propagation and target-phase transitions in the local0.6 candidate. This is not an exhaustive audit of every game mechanic.

## Confirmed causes and fixes

- A saved manual spell ran before melee-style detection and accepted any weapon with positive Magic attack. Removed that implicit override. Detected casting and an explicit Magic override remain available; manual spellbook casts while a melee stance is selected require the override.
- Voidwaker special damage previously reused whatever maximum the basic HUD calculated. It now independently uses boosted Strength, melee prayer, actual stance, gear strength, applicable Void/Salve/Slayer bonuses. The special remains Magic-typed for target immunity and encounter effects. Other incompatible special/basic input types return unavailable rather than mix formulas.
- Pillar records shared by normal Nightmare and PNM were wholly blocked because their HP differed. Attackable pillar IDs9435/9438/9441/9444 now resolve their identical combat stats. Visible boss evidence supplies200/300HP; absent evidence leaves0 (unknown), never a silently selected variant. Charged pillar IDs remain unresolved/unsupported.
- Magic damage against attackable pillars now doubles per roll; the same multiplier applies once to supported Magic specials, including Voidwaker. Ordinary PNM boss damage is unchanged.
- Saved elemental spells leaked weakness bonuses into powered-staff damage and accuracy. Guards now exclude powered spells. The associated saved demonbane accuracy and salamander/Ice demon exemption paths were also corrected.
- Snapshot refresh now follows the current ID/name of the already-confirmed live actor, preventing stale phase IDs. It does not select new actors or discard between-kill retention.

## Evidence and verification

User screenshots: before removing the saved spell, max25/spec37/hit chance1.4%; afterward max43/spec64/hit chance37.3%. Worn equipment showed+146 melee Strength. A regression reproduces a43 melee maximum using99Strength, Piety and aggressive stance with that bonus; the screenshot does not prove all of those inputs were present.

Pillar mechanics and stats were cross-checked with the OSRS Wiki-maintained calculator snapshot89c3e25b: `cdn/json/monsters.json`, `src/lib/constants.ts` and `src/lib/PlayerVsNPCCalc.ts` (Nightmare Magic multiplier and Voidwaker melee-to-Magic special). Public source: https://github.com/weirdgloop/osrs-dps-calc . Implementation is independent.

169 Java regressions and8 Python tests passed against the available real RuneLite1.12.39 libraries. Clean cumulative patch application and byte comparison are verified during packaging. Gradle bootstrap/network limitations and lack of Windows/in-game testing remain. No commits or publication.

## Known limits retained

Full new-NPC refresh is still blocked by source access;3976 bundled IDs remain. Raid-special transforms, unsupported enchanted bolts, Venator bounce DPS and the other limitations in TESTING-0.6.0.md remain. PNM pillar HP is encounter evidence, not a claim that every unknown same-ID variant is resolved. The defence-tracking on/off feature was not silently added; hiding an infobox still does not disable local tracking.
