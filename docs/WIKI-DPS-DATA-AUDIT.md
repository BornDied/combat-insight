# Wiki DPS data audit — 24 October 2026

**Local alpha4 continuation:** [current changes, complete combat roster, party HP inputs and unresolved mechanics](research/2026-10-24-archive-completion.md). Unpublished; no new push or Plugin Hub update. The historical sections below are superseded where that note differs.

**Alpha3 follow-up:** [current behavior, sources, limitations and tests](research/2026-10-24-alpha3-follow-up.md). The alpha2 material below is retained as history; its Bork/Warden/HP and submission-status statements are superseded where described in that follow-up.

## Continuation against current data

The original comparison below is retained as history. Current reference is `98e4fac38b580901cc3f8d485bbaa48bbb3277ed`: 2,918 variants / 2,874 distinct IDs. After eight curated Archive additions, the local snapshot has 4,001 IDs. The new read-only `scripts/audit_wiki_variants.py` reports **29 unmatched live-ID variant records, two calculator-only Yama records, and 46 numeric-difference candidates**. It preserves duplicated IDs instead of picking a last row. [Generated report](research/2026-10-24-wiki-variants.json) and [all-candidate disposition / raid audit](research/2026-10-24-encounter-audit.md).

Ocellus P1's Wiki 16673 is an intro ID in RuneLite; retain combat 16674. The new P2 record 16675 names a body/controller rather than establishing Blade hitbox 16676 mechanics. Grounded Guardian uses live 16660. None of those apparent mismatches is fixed by a bulk import. Disputed Verzik coloured-add stats are now explicitly unavailable.

## Original alpha2 comparison

Pinned upstream revision: `d50c49c589793dc2ee691592015ba63ff9c49fab`.
Source: https://github.com/weirdgloop/osrs-dps-calc/tree/main/cdn/json

2917 Wiki variant records; 3993 local NPC IDs. 38 Wiki records have no exact local ID; 46 matched records differ in compared numeric fields. These are audit candidates, not confirmed bugs: scaled/phase variants may deliberately differ. IDs are not unique species.

Do not bulk import duplicate IDs. Live phase resolution and raid scaling must be compared separately.

## Missing exact IDs

| ID | Wiki name / variant |
|---|---|
| 16598 | Blink (Combo Eating) |
| 16625 | Blink (Special Attacks) |
| 16688 | Corpse (Xarth) (Doused) |
| 16687 | Corpse (Xarth) (Normal) |
| 16357 | Giant Mole (LANScape) (Bandos) |
| 16356 | Giant Mole (LANScape) (Guthix) |
| 16355 | Giant Mole (LANScape) (Saradomin) |
| 16354 | Giant Mole (LANScape) (Zamorak) |
| 14424 | Giant Rock Crab (Tonali Cavern) |
| 16666 | Jelly (Dreadnoth) () |
| 16673 | Ocellus (Phase 1) |
| 16528 | Outlaw (A Ruff Situation) (Level 22) |
| 16527 | Outlaw (A Ruff Situation) (Level 23) |
| 16579 | Pete Kayer (Final Challenge) |
| 16588 | Pete Kayer (Penultimate Challenge) |
| 16607 | Sparring partner (1) |
| 16616 | Sparring partner (10) |
| 16617 | Sparring partner (11) |
| 16608 | Sparring partner (2) |
| 16609 | Sparring partner (3) |
| 16610 | Sparring partner (4) |
| 16611 | Sparring partner (5) |
| 16612 | Sparring partner (6) |
| 16613 | Sparring partner (7) |
| 16614 | Sparring partner (8) |
| 16615 | Sparring partner (9) |
| 16670 | Undead binder () |
| 16213 | Venator (The Blood Moon Rises) (1) |
| 16214 | Venator (The Blood Moon Rises) (2) |
| 16215 | Venator (The Blood Moon Rises) (3) |
| 16216 | Venator (The Blood Moon Rises) (4) |
| 16217 | Venator (The Blood Moon Rises) (5) |
| 16680 | Void Augmenter () |
| 16678 | Void Rift () |
| 16679 | Void Siphon () |
| 16681 | Void Vine () |
| 100001 | Yama (Glyphic Attenuation) |
| 100001 | Yama (Glyphic Attenuation, Phase 3) |

## Numeric differences

| ID | Wiki name / variant | Local → Wiki |
|---|---|---|
| 16659 | Archive Guardian (Grounded) | defensive.slash: 110 -> 90; defensive.light: 75 -> 50; defensive.standard: 120 -> 115 |
| 15547 | Big Evil Chicken (Permanent) | skills.hp: 3500 -> 1000 |
| 15025 | Black Eye Bethel (Rune scimitar) | skills.def: 56 -> 85; skills.magic: 45 -> 68 |
| 15025 | Black Eye Bethel (Steel scimitar) | skills.def: 56 -> 100; skills.magic: 45 -> 80 |
| 8367 | Blood spawn (Entry) | skills.hp: 120 -> 7 |
| 16640 | Bork () | offensive.magic: 1 -> 0; defensive.flat_armour: 10 -> 0; defensive.crush: 20 -> 10 |
| 14707 | Doom of Mokhaiotl (Delve 1) | skills.hp: 625 -> 525 |
| 14707 | Doom of Mokhaiotl (Delve 2) | skills.hp: 625 -> 550 |
| 14707 | Doom of Mokhaiotl (Delve 3) | skills.hp: 625 -> 575 |
| 14707 | Doom of Mokhaiotl (Delve 4) | skills.hp: 625 -> 600 |
| 14707 | Doom of Mokhaiotl (Delve 6) | skills.hp: 625 -> 650 |
| 14707 | Doom of Mokhaiotl (Delve 7) | skills.hp: 625 -> 650 |
| 14707 | Doom of Mokhaiotl (Delve 8) | skills.hp: 625 -> 675 |
| 12191 | Duke Sucellus (Post-quest, Awake) | skills.def: 316 -> 275; skills.magic: 465 -> 310; skills.hp: 1697 -> 485; defensive.slash: 65 -> 45 |
| 14420 | Earthen Nagua (Weakened) | defensive.stab: 30 -> 0; defensive.slash: 50 -> 0; defensive.crush: 50 -> 0 |
| 13012 | Eclipse Moon (Regular) | defensive.flat_armour: 4 -> 6 |
| 11761 | Elidinis' Warden (Enraged) | skills.def: 150 -> 180 |
| 4431 | Feral Vampyre (Level 64 (Burgh de Rott/Crombwick Manor)) | skills.def: 60 -> 30 |
| 3707 | Feral Vampyre (Level 64 (Meiyerditch)) | skills.def: 60 -> 30 |
| 12452 | Giant goblin (Apocalypse) | skills.def: 200 -> 250; skills.hp: 3500 -> 750 |
| 12452 | Giant goblin (Armageddon) | skills.hp: 3500 -> 1000 |
| 12452 | Giant goblin (Permanent) | skills.hp: 3500 -> 1000 |
| 15553 | I DSCIM YOU (Permanent) | skills.hp: 1500 -> 750 |
| 12353 | Jhallan () | skills.def: 83 -> 250; skills.magic: 83 -> 250; skills.hp: 1 -> 1500 |
| 15742 | Maggot King (Nearby) | defensive.magic: 150 -> 400 |
| 15742 | Maggot King (Roaring) | skills.def: 200 -> 100 |
| 13663 | Magic Mark (Armageddon) | skills.hp: 3500 -> 1500 |
| 13663 | Magic Mark (Permanent) | skills.hp: 3500 -> 1500 |
| 8383 | Nylocas Hagios (Verzik) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 11 -> 25; offensive.magic: 1073 -> 0 |
| 10843 | Nylocas Hagios (Verzik (Entry Mode)) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 2 -> 3; offensive.magic: 505 -> 0 |
| 8381 | Nylocas Ischyros (Verzik) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 11 -> 25; offensive.magic: 1073 -> 0 |
| 10841 | Nylocas Ischyros (Verzik (Entry Mode)) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 2 -> 3; offensive.magic: 505 -> 0 |
| 8382 | Nylocas Toxobolos (Verzik) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 11 -> 25; offensive.magic: 1073 -> 0 |
| 10842 | Nylocas Toxobolos (Verzik (Entry Mode)) | skills.def: 1 -> 0; skills.magic: 1 -> 0; skills.hp: 2 -> 3; offensive.magic: 505 -> 0 |
| 13664 | Ranging Ro (Armageddon) | skills.hp: 3500 -> 1500 |
| 13664 | Ranging Ro (Permanent) | skills.hp: 3500 -> 1500 |
| 12214 | The Leviathan (Post-quest) | skills.def: 287 -> 250; skills.magic: 280 -> 160; skills.hp: 2700 -> 900 |
| 8363 | The Maiden of Sugadinti (30% Health) | skills.hp: 3500 -> 1050 |
| 8362 | The Maiden of Sugadinti (50% Health) | skills.hp: 3500 -> 1750 |
| 8361 | The Maiden of Sugadinti (70% Health) | skills.hp: 3500 -> 2450 |
| 12204 | The Whisperer (Post-quest) | skills.def: 300 -> 250; skills.magic: 225 -> 180; skills.hp: 2700 -> 900 |
| 9435 | Totem (The Nightmare) () | skills.hp: 200 -> 300 |
| 9436 | Totem (The Nightmare) () | skills.hp: 200 -> 300 |
| 11762 | Tumeken's Warden (Enraged) | skills.def: 150 -> 180 |
| 12223 | Vardorvis (Post-quest) | skills.hp: 1400 -> 700 |
| 14176 | Yama (Phase 3) | defensive.stab: 100 -> 135; defensive.slash: 80 -> 108; defensive.crush: 333 -> 449; defensive.magic: 0 -> 81; defensive.light: 333 -> 449; defensive.standard: 220 -> 297; defensive.heavy: 333 -> 449 |
