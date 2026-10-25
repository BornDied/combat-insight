# Fractured Archive: coverage and research, 23 October 2026

**Local alpha4 continuation:** [current changes, complete combat roster, party HP inputs and unresolved mechanics](research/2026-10-24-archive-completion.md). Unpublished; no new push or Plugin Hub update. The historical sections below are superseded where that note differs.

**Alpha3 follow-up:** [current behavior, sources, limitations and tests](research/2026-10-24-alpha3-follow-up.md). The alpha2 material below is retained as history; its Bork/Warden/HP and submission-status statements are superseded where described in that follow-up.

**October 24 continuation:** see the [current encounter audit](research/2026-10-24-encounter-audit.md) and [verification](research/2026-10-24-verification.md). The sections below preserve the supplied alpha2 research history. New work adds eight Archive research/combat records (4,001 total IDs), keeps incomplete Ocellus adds unavailable, applies reduced demonbane effectiveness, and corrects Obligator mode selection. Corpse exclusions below are superseded: 16687/16688 are now represented as distinct target states. Bork remains unavailable. The audit also records ToA/ToB uncertainty guards.

Local 0.9.0-alpha2, based on origin/main a0f2e060973d1e00a835bcd2afa1e95cfa298bca (0.8.0). No publishing performed. Uses launch inputs plus controlled GearScape UI comparisons. No live raid observation was performed. See GEARSCAPE-COMPARISONS.md for exact setups and results.

## Implemented

- Active Zeal varbit 16577: 1.25 Attack and 1.28 Strength. Piety remains 1.20 / 1.23. The unlocked flag 16578 is deliberately not treated as activation. Stable RuneLite 1.13.3 lacks the Prayer.ZEAL enum present on upstream master, so the active gameval number is used.
- Elemental fragment unlocks: Earth 16434, Air 16435, Fire 16436, Water 16437. +2 base damage before equipment and elemental weakness. Only the matching standard spell; powered weapons and saved spells during melee do not inherit it. Inspector applied-effects text identifies the bonus.
- Ascension crossbows (34681): heavy ranged, 3 ticks / 2 rapid through existing style logic; compatible Ascension ammunition only, including compatible quiver selection. Plain bolts (34684) +53 strength; diamond/onyx variants +58. No duplicate simultaneous hit is assumed for dual wielding. Diamond enchanted bolts use 5% activation and +15% damage; onyx uses 5.5% and +20%; diary multiplies activation by 1.1. GearScape high/low-accuracy fixtures match.
- Zorya's tome (34725): built-in Magic, base floor(Magic/3)-10, 3-tick base speed, ignores saved spell. Uncharged variant (34727) withholds damage. Special accuracy x2.5 and maximum x1.6 after normal modifiers. HUD shows the initial hit; follow-up attacks are conditional. Live varp 5926 drives empowered basic damage/accuracy and 2-tick speed. A separate special maximum prevents double enhancement while buffed. Existing optional blue spec accent includes the Tome.
- Obligator (34715): Crush on both styles, Smash 3 ticks / ordinary roll, Thrust 5 ticks / best of 2–4 damage rolls. Thrust size bonus 20% for size 2 or 40% for size >=3, before rolling. Equal defences do not add rerolls. One accuracy check and one hitsplat; armour and encounter transforms apply to that hitsplat. Uncharged 34717 withholds damage. Automatic and forced melee paths have regression coverage.
- Fallback offensive bonuses for new crossbows, bolts, tome, Obligator and Rondache when RuneLite item data is missing; loaded RuneLite stats take priority.
- Rondache charge varbit 16438 drives an Inspector-only base maximum: floor(charges*(100+charges)/100), before target mitigation. Mainhand special remains intact; shield accuracy/DPS are not claimed.
- Magic specials bypass non-Magic flat armour (Tome, Eye of Ayak, Accursed sceptre and Voidwaker).
- 17 new combat IDs, bringing bundled database to 3,993 IDs (IDs are not unique species). Scenery, intro/cutscene, corpse IDs are excluded.
- Defence floor = 60% of base for Guardian, Bork, Ezekiel, Moia, Xarth, Ocellus P1/P3. No floor for Dreadnoth, Smorks, Trio, Blade. This follows the launch hotfix, not older Wiki phase-floor text.
- Nex IDs 11278–11282: crush defence bonus 35, formerly 60.

## Raid coverage

| Encounter | IDs | State of calculation |
|---|---|---|
| Archive Guardian | 16659 / 16660 | Published flying/grounded defensive variants, fire weakness 80% / 40%, Ancient spell immunity; Magic-drain interaction unresolved; a tracked Magic change withholds calculations and Magic display |
| Bork | 16640 | Record retained for research; damage and accuracy withheld: normal armour 10 and offensive Magic bonus 1 matched GearScape; live stun/damage state still requires resolution |
| Smorks | 16641 | Published combat inputs and flat armour 2 |
| Dagon'hai Trio | 16643–16648 | Published records; calculations withheld pending shield and imp-buff mechanics |
| Ezekiel | 16652 | Published combat inputs |
| Dreadnoth | 16665 | Published combat inputs, demon attribute and fire weakness |
| Moia | 16667 | Published record; calculations withheld pending phase/shield state |
| Ocellus P1 | 16674 | Ranged/Magic base calculations; melee withheld pending protection state |
| Blade / Ocellus P2 | 16676 | Partial published record; calculations withheld; offensive Magic/HP still missing |
| Ocellus P3 | 16677 | Published combat inputs; no new reset on quadrant transition |
| Xarth | 16684 | Published combat inputs, demon attribute, water weakness; does not inherit ordinary tormented-demon shield logic |

All Archive maximum/current HP estimates are withheld until scaling is verified. This prevents base HP from masquerading as the party-scaled total and prevents Ruby bolt calculations based on invented HP. This version does not automatically detect Archive party size. Interruptions, downtime, travel and invulnerable transition animations are not included in sustained DPS.

## Still required before complete support

1. In-game testing of Obligator style transitions, Tome stack lifetime/target changes, and Ascension enchanted effects. Static reference comparisons and automated tests pass; live observations remain required before calling the release ready.
2. Rondache shield-bash accuracy inputs and a complete secondary special result. Charge maximum is available in Inspector, not a replacement for the mainhand special.
3. Bork stun, Moia phases, Trio shields/imp buff, Ocellus protection/P2 data, Guardian Magic-reduction interaction. These require additional authoritative data and live diagnostics. The reported Guardian Magic absorption is not treated as a confirmed formula.
4. Raid HP scaling, room adds (jellies, undead binders, inert guardians, etc.), target phase transitions and party contribution/drain synchronization. Do not claim every raid NPC is supported.
5. Existing limitations remain, including unreproduced PNM pillar report, ToA HP-dependent damage and prior inspector notes.

## Sources

- [GearScape DPS calculator](https://gearscape.net/calculators/dps), controlled public UI comparisons October 23; exact inputs/results in [comparison log](GEARSCAPE-COMPARISONS.md). Moia displayed one base record; this does not establish live shield/phase detection.
- [Jagex launch post and hotfixes](https://secure.runescape.com/m=news/the-fractured-archive---out-today?oldschool=1), inspected October 22: current defence floors, Nex change, elemental rewards, weapon descriptions, Tome hotfix.
- [Jagex reward poll](https://secure.runescape.com/m=news/the-fractured-archive---rewards-poll-blog?oldschool=1): proposal details, used only where launch/current sources agree.
- RuneLite upstream [VarbitID](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/VarbitID.java), [VarPlayerID](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/VarPlayerID.java), [NpcID](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/NpcID.java), [ItemID](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/ItemID.java). Numeric mappings checked October 22.
- OSRS Wiki raw infoboxes retrieved October 22: [Zeal](https://oldschool.runescape.wiki/w/Zeal), [Ascension crossbows](https://oldschool.runescape.wiki/w/Ascension_crossbows), [Obligator](https://oldschool.runescape.wiki/w/Obligator), [Zorya's tome](https://oldschool.runescape.wiki/w/Zorya%27s_tome), [Rondache](https://oldschool.runescape.wiki/w/Rondache), their ammunition pages.
- NPC sources: [Archive Guardian](https://oldschool.runescape.wiki/w/Archive_Guardian), [Bork](https://oldschool.runescape.wiki/w/Bork), [The Smorks](https://oldschool.runescape.wiki/w/The_Smorks), [Ezekiel](https://oldschool.runescape.wiki/w/Ezekiel), [Dreadnoth](https://oldschool.runescape.wiki/w/Dreadnoth_the_Jellied), [Moia](https://oldschool.runescape.wiki/w/Moia), [Dagon'hai Elder](https://oldschool.runescape.wiki/w/Dagon%27hai_Elder), [Dagon'hai Monk](https://oldschool.runescape.wiki/w/Dagon%27hai_Monk), [Xarth](https://oldschool.runescape.wiki/w/Xarth), [Ocellus](https://oldschool.runescape.wiki/w/Ocellus), and [raid strategy notes](https://oldschool.runescape.wiki/w/The_Fractured_Archive/Strategies). Bork and Blade rows remain ambiguous where inputs are missing/conflicting.

Wiki-derived data retains existing project attribution/license obligations. These curated rows are appended to monsters.tsv; a future full generator refresh must re-check this coverage and the launch hotfix, not blindly replace them with older source data. No GearScape implementation was copied. The retrieved WeirdGloop main calculator revision was still from September and contained no Archive mechanics, so it was not an independent oracle for new content.

## October 24 Wiki dataset audit

The updated Wiki DPS dataset is pinned at d50c49c589793dc2ee691592015ba63ff9c49fab. See WIKI-DPS-DATA-AUDIT.md: 38 variant records without an exact local ID and 46 numeric-difference candidates. These counts do not mean 38 missing species or 46 confirmed errors. The source has 27 duplicated IDs across variants.

Bork currently conflicts with GearScape (Wiki: flat armour 0, crush 10, offensive Magic 0; GearScape comparison: armour 10, crush 20, offensive Magic 1). Its live output remains withheld. Wiki labels both Guardian states ID 16659 and Ocellus P1 ID 16673; live IDs must be reconciled with RuneLite rather than blindly overwritten. Xarth corpses and several Archive adds are now explicit audit candidates; the earlier blanket exclusion of corpses must be reviewed because some are attackable encounter targets.
