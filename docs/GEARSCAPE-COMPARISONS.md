# GearScape comparison fixtures — October 23, 2026

October 24: Bork's displayed base inputs were rechecked and still match the GearScape inputs below. They conflict with current Wiki base data and do not resolve its live stun/quartile. These are static numerical fixtures, not permission to enable Bork live calculations. The historical Smash accuracy fixture also does not establish live COM_MODE slots: current mode/stance handling follows the three-style Wiki reference and needs the in-game button check documented in TESTING-0.9.0.md. See the [current reconciliation](research/2026-10-24-encounter-audit.md).

Reference: https://gearscape.net/calculators/dps (public calculator UI). No implementation code copied. Figures below are displayed UI results; tests use a 0.000005 tolerance for five-decimal DPS.

All equipment omitted unless stated. Player stats 99, no prayers or boosts. Custom target: combat/defensive bonuses zero, size 1 unless stated, armour 0, HP 500. GearScape defaults Kandarin diary on; fixture-specific overrides below.

## Obligator

- Thrust, custom target with equal stab/slash/crush defence 0: max 18, DPS 3.99406.
- Increase only stab defence bonus to 1: DPS 4.51178.
- Increase slash defence bonus to 1 as well: DPS 4.82232.
- Custom size 3 with equal defences: max 25, DPS 5.52713.
- Bork: size 4, Defence 300, stab/slash/crush 140/130/20, armour 10. Thrust max 15, accuracy 37.92%, DPS 1.30745. Smash max 7, accuracy 38.99%, DPS 0.33693.
- Inference matching these outputs: Thrust max increases 20% for size 2, 40% for size >=3 before rolling; highest of 2 + (stab > crush) + (slash > crush) rolls. One accuracy check, one hitsplat. Smash has no size bonus or rerolls. Attack periods 5 / 3 ticks.
- Bork UI time-to-kill was anomalous (approximately 1e34 seconds). It is not used as a fixture or copied into Combat Insight.

## Zorya's tome

Tome only: +45 Magic accuracy, +15% damage. Custom target zero stats/defences, size 3, HP 500.

| Magic level | Normal max | Special per-hit max |
|---|---:|---:|
| 85 | — | 32 |
| 93 | — | 38 |
| 99 | 26 | 41 |
| 120 | 34 | — |

At 99, normal DPS 7.06824 and accuracy 97.59%. Special view reports 164 (41 x 4), accuracy 99.04%; this includes conditional follow-ups. Combat Insight shows the initial special hit and describes the next three attacks conditionally, then reads live stack varp 5926 for empowered basic attacks.

Inference matching outputs: base floor(Magic/3)-10, gear modifiers applied normally, then empowered max floor(normal max*1.6). Level 93 distinguishes this order from multiplying/flooring the base first. Special accuracy x2.5. Basic speed 3 ticks; empowered speed 2 ticks. The earlier fixed base 18 was incorrect (it is the level-85 value).

## Ascension enchanted bolts

Ascension crossbows, diamond/onyx bolts +58 Ranged strength, Rapid (2 ticks). Custom target size 3. At 0 Defence, ordinary accuracy 97.24%; at 300 Defence, 26.51%. Defence bonuses remain zero.

| Bolt | Defence | Diary | Normal/proc max | DPS |
|---|---:|---|---|---:|
| Diamond, unenchanted | 0 | on | 20 | 8.14227 |
| Diamond (e) | 0 | on | 20 / 23 | 8.22344 |
| Diamond (e) | 300 | off | 20 / 23 | 2.58965 |
| Onyx (e) | 300 | off | 20 / 24 | 2.24394 |
| Onyx (e) | 300 | on | 20 / 24 | 2.24637 |

Inference matching outputs: diamond 5% proc bypassing accuracy, onyx 5.5% proc requiring accuracy; diary multiplies each by 1.1. Damage bonuses remain +15% / +20%. Ordinary bolt rates remain unchanged. Onyx undead exclusion remains from the existing effect rules; that exclusion was not independently checked in this UI session.

These comparisons verify static calculations, not automatic detection of live raid phases, target changes, or raid HP scaling. Those remaining limitations are listed in FRACTURED_ARCHIVE.md.

## Rondache

Rondache only, custom target zero defences: 20 charges displays Rondache Max 24; 10 charges displays 11; adding 5 flat armour displays 6. The UI reports no separate shield accuracy/DPS. Setting charges to zero displayed the default 20-charge maximum, so that UI fallback is not copied: no charges means base maximum zero in Combat Insight. Inspector explicitly labels the value before target mitigation and keeps mainhand specials separate.
