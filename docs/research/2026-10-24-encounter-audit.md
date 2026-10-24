# Encounter audit and alpha2 recovery — 24 October 2026

This is a review branch for local testing, not a release or proof of in-game correctness. The ZIP was recovered against its exact base `a0f2e060973d1e00a835bcd2afa1e95cfa298bca`; freshly cloned GitHub main was identical. No older local project was modified. The recovered source is retained separately from the Git checkout.

## Evidence and reproducibility

- Wiki DPS source/data: [`98e4fac38b580901cc3f8d485bbaa48bbb3277ed`](https://github.com/weirdgloop/osrs-dps-calc/tree/98e4fac38b580901cc3f8d485bbaa48bbb3277ed), October 24 automated JSON update. Reviewed `cdn/json/monsters.json`, `BaseCalc.ts`, `PlayerVsNPCCalc.ts`, `constants.ts`, and `scaling/{ChambersOfXeric,TheatreOfBlood,TombsOfAmascut,DefenceReduction,Phases}.ts` under `src/lib/`.
- RuneLite gamevals: [`fc8d18969e3028af21a78fae38c9ab6c8c7573b1`](https://github.com/runelite/runelite/tree/fc8d18969e3028af21a78fae38c9ab6c8c7573b1/runelite-api/src/main/java/net/runelite/api/gameval), `NpcID`, `ItemID`, `VarbitID`, `VarPlayerID`. Names identify intended roles; they do not establish server damage formulas.
- GearScape public calculator: [DPS UI](https://gearscape.net/calculators/dps), rechecked October 24. Selecting Bork still displays Defence 300, Magic 90, size 4, crush defence +20, offensive Magic +1 and armour 10. Earlier controlled numerical fixtures remain recorded in [GEARSCAPE-COMPARISONS.md](../GEARSCAPE-COMPARISONS.md); they were rerun as regression tests, not re-measured in-game.
- [Jagex launch/hotfix post](https://secure.runescape.com/m=news/the-fractured-archive---out-today?oldschool=1), rechecked October 24: specified Archive bosses retain at least 60% base Defence; Dreadnoth, Smorks, Trio and Blade can reach zero. P3 Ocellus quadrant movement does not reset drains.
- Direct retrieval of current individual Wiki pages for new adds/Bork was unavailable. Use the pinned structured dataset as provisional base-stat evidence, not an independent confirmation of every add mechanic. No gameplay, account login or party-state capture was performed.

Recreate the read-only comparison from repository root (with Python and the reference checkout available):

```powershell
python scripts/audit_wiki_variants.py ../wiki-dps-reference/cdn/json/monsters.json src/main/resources/com/combatinsight/data/monsters.tsv
python -m unittest discover -s scripts -p 'test_*.py'
```

The checked-in [JSON report](2026-10-24-wiki-variants.json) records individual variants, duplicate IDs, ambiguous local matches and calculator-only IDs. Its numeric scope is Defence/Magic/HP/offensive Magic, armour and defence bonuses. It does not compare every attribute, size, weakness, mode, formula or live phase. Counts are candidates, never bug/species counts.

## Fractured Archive

| Target | Resolution and behavior |
|---|---|
| Guardian 16659 / 16660 | RuneLite `TFA_GUARDIAN` / `TFA_GUARDIAN_UNSHIELDED` supports the existing separate flying/grounded mapping. Wiki assigns both variants 16659; retain grounded values under 16660. Fire weakness 80/40, Ancient-spell immunity and flying restrictions remain. Any tracked Magic change still withholds output pending the absorption/drain interaction. |
| Bork 16640 / Smorks 16641 | Do not choose one fixed Bork armour as a universal answer. Wiki `Phases.ts` derives armour 8/10/12/14 and Magic resistance 0/2/4/6 from Smorks HP quartile; stunned uses base data, with full-accuracy handling explicitly incomplete in that reference. Wiki base crush 10/offensive Magic 0 also differs from GearScape 20/1. Live stun/quartile and the remaining base conflict are unresolved: Bork stays unavailable. Smorks retains its own armour 2 and earth weakness 85. |
| Trio 16643–16648 | RuneLite distinguishes shielded/unshielded IDs for Elder and both Monks. Existing base records remain; shield, imp buff and effectiveness are not safely resolved, so calculations stay unavailable for all six. Imp 16650 and rift/cauldron mechanics lack curated combat evidence. |
| Ezekiel 16652 | Base calculations remain; earth weakness 25, 60% Defence floor. Animated/death swords 16653–16656 require separate attackability/stat evidence; no invented rows. |
| Dreadnoth 16665 / jelly 16666 | Add jelly with published stats, fire weakness 60 and demon attribute. Boss retains fire weakness 85. Both use **25% of the ordinary demonbane bonus**; percentage truncation occurs before application, as in Wiki `demonbaneFactor`. No boss Defence floor. |
| Moia 16667 / binder 16670 | Moia remains unavailable pending phase/shield handling. Varbit 16418 names a shadow shield amount, but its range/semantics and formula are not verified. Add binder base stats with Magic defence -20. Do not infer an undead attribute merely from its name: upstream attributes are empty. |
| Ocellus P1 | RuneLite 16673 is `INTRO_MET`; **16674** is combat P1. Retain 16674 and leave 16673 excluded. P1 melee still unavailable pending protection; ranged/Magic base calculations remain. Demonbane bonus effectiveness is 80%. |
| Ocellus P2 / Blade | 16675 is the controller/body; 16676 is sword hitbox. Current Wiki now supplies P2 HP 1000 under 16675, but that does not establish transferable hitbox stats, attackability, or transformations. Keep 16675 excluded and 16676 unavailable. |
| Ocellus P3 16677 | Existing base calculations, 60% floor, demonbane bonus effectiveness 80%; no new reset on quadrants. Timed transitions and invulnerability windows remain outside steady-state DPS. |
| Ocellus adds 16678–16681 | Record Rift, Siphon, Augmenter and Vine for research. Rift/Augmenter have size 0 upstream; this is not a verified live footprint. All four stay unavailable pending add mechanics/size confirmation. Intro, debug 16682 and vault Blade 16683 are excluded. |
| Xarth 16684 / corpses 16687–16688 | Boss demonbane bonus effectiveness 80%; no ordinary tormented-demon shield is inferred. **Corpses are attackable candidates**, not scenery. Add normal water-weak corpse (40%, melee defence 100) and doused fire-weak corpse (100%, melee defence -20), both undead. NPC ID selects the published state. No guessed dousing duration or boss-phase transition formula. |

All Archive maximum/current HP remains unavailable, including new adds: party/mode scaling has not been established. HP-dependent ruby effects cannot borrow the base HP column. Published ordinary attack calculations do not include room downtime, mechanics uptime or movement. Archive normal/challenge-mode differences are not verified; no mode/scaling formula is invented. Party varbit names 16386–16395 are research leads, not a validated party-size decoder.

## Other raids: room and mode audit

The existing 160-ID CoX/ToB roster is a data-coverage fixture, not proof of complete mechanics. Nine disputed Verzik adds are now explicitly uncertain. The following review compared current scaling/transform sources with `RaidScaling`, `TargetMechanics`, `RaidDamageRules`, `TargetEffectRules` and their live callers.

| Raid/room | Mode, scaling, adds and rules checked | Limitations |
|---|---|---|
| CoX Tekton | Normal/CM and enraged IDs; small/large CM Defence factors; ranged immunity; Magic one-fifth/minimum transform; DWH/maul first-attempt tracking. | Special damage remains unavailable where the special transform is not implemented. Unknown attempt history is not treated as a fresh first hit. |
| CoX Guardians | Both statues and CM IDs; Mining-dependent HP and pickaxe damage scaling; non-pickaxe/non-melee exclusion. | Average party Mining is manual. Guardian specials remain pending. |
| CoX Ice demon | Normal/CM; fire and genuine demonbane exemptions; non-exempt damage divided by three; Magic uses Defence level; demonbane effectiveness 115%. | Crumble Undead no longer inherits a demonbane exemption. Environmental icefiends/initial storm are not modeled. |
| CoX Vanguards | Three styles, linked defensive/offensive scaling. | Healing/reset thresholds and multi-target encounter uptime are not modeled. |
| CoX Vespula | Boss phases, soldiers and portal; flying/polearm exceptions; portal Magic uses Defence. | Poison, healing and portal downtime are outside attack DPS. Vespine soldier singles scaling remains distinct. |
| CoX Vasa | Boss and glowing crystal; crystal ranged immunity and Magic reduction. | Crystal/base invulnerability windows and reset/heal timing are not inferred. |
| CoX Olm | Head and both claws, normal/CM mapping; claw linked Magic scaling once; off-style reductions; party HP special formula. | Portal/healing/crippled-hand phase uptime and head vulnerability timing are not tracked. |
| CoX remaining combat rooms | Mystics, shamans, muttadiles, deathly mage/ranger, scavengers in roster; ordinary attribute/weakness input paths. | Ordinary roll coverage does not establish shield, movement or attackable puzzle scenery rules. |
| CoX inputs | Observed party/mode/highest combat; manual highest HP/average Mining; scaling before local drains. | Manual team stats can be wrong; no complete party-state synchronization. |
| ToB Maiden | Entry/Normal/Hard phase IDs, crabs/blood spawn; party HP scaling. | Retain full phase-bar maximum rather than replacing with 70/50/30% current HP. Wiki Entry Blood spawn uses shared 8367; RuneLite entry is 10821, already bundled with HP 7. No replacement of normal 8367 HP 120. |
| ToB Bloat | Entry/Normal/Hard base stats and HP scaling. | Downed/standing damage state and downtime are not dynamically resolved. Treat ordinary predictions as conditional, not fight-wide DPS. |
| ToB Nylocas room | Small/large and boss colour IDs including hard-mode demi-boss; wrong-style immunity follows live ID. | Spawn wave timing/overkill and simultaneous-target DPS are excluded. |
| ToB Sotetseg | Normal/Hard Defence floor 100; entry variant separate; HP-only party scaling. | Maze invulnerability and partner targeting are not observed. |
| ToB Xarpus | All three modes; hard IDs never take entry fractions; HP-only scaling. | Screech/turning/poison downtime not modeled. |
| ToB Verzik | P1 random damage cap (melee 10, other 3), P2/P3 Magic-uses-Defence and floors; mode-specific HP scaling. | P1 special output remains explicitly pending. Coloured adds 8381–8383/10841–10843 conflict strongly with current Wiki (HP, Magic and offensive Magic); hard equivalents 10858–10860 share the suspect local source. All nine now withhold calculations and HP. Do not substitute zeros as verified replacements. |
| ToB inputs | Minimum effective normal/hard party 3; entry fractions 10/19/27/34/40 over 40; largest observed roster retained. | Mid-raid attachment/relog cannot reconstruct absent starters. |
| ToA paths | Akkha/shadows, Ba-Ba, Kephri/overlords, Zebak; invocation roll multiplier; respective Defence floors 70/60/60/50. Kephri overlords excluded from invocation defence scaling. | Entry/Normal/Expert share inputs rather than a flat mode multiplier. Path level/party HP rounding remains unmodeled; no ToA HP estimates or ruby damage. Akkha protection, Ba-Ba boulder/monkey puzzle, Kephri shields and other timed states are not complete. |
| ToA Wardens | Obelisk floor 60, P3 floor 120; active P3 IDs shared by damaged/enraged variants remain ambiguous. Charging 11763/11764 now immune, including specials. | P2 11753/54/56/57 uses guaranteed accuracy with damage derived from accuracy; core 11755/58 has another distribution. These now withhold ordinary basic/special accuracy and damage instead of pretending to implement them. Do not overwrite shared P3 base Defence 150 with 180 for every phase. |

These are explicit scope limitations, not assertions that generic damage is valid during every transient state. Full live phase recognition across all four raids remains material unfinished work.

## DT2, PNM, Zulrah and unique rules

- DT2 normal/awakened shared IDs are resolved from observed combat level or the existing manual mode override. Duke 12191 uses normal Defence/Magic/HP 275/310/485 versus awakened 316/465/1697; slash bonus 45/65. Whisperer 12204/12205 uses 250/180/900 versus 300/225/2700. Leviathan 12214 uses 250/160/900 versus 287/280/2700 and melee immunity. Vardorvis 12223 uses HP-dependent Defence and withholds unresolved health; 700/1400 HP variants remain distinct. Quest variants remain separate. Unknown mode cannot silently select normal. These numeric candidate differences are already resolver responsibilities, not table replacements. Enrage, stunned and temporary vulnerability effects beyond those paths still require live validation.
- PNM: four pillar combat IDs 9435/9438/9441/9444 share stats; Magic damage is doubled per hitsplat, after the weapon calculation (including Shadow and Magic specials). Encounter evidence distinguishes PNM HP 200 from group Nightmare HP 300; absent context withholds HP. Wiki duplicate HP variants 9435/9436 are not unconditional replacement rows. The previously reported pillar/Shadow issue has automated coverage but no reproduced gameplay evidence.
- Zulrah: form IDs 2042/2043/2044, each hitsplat above 50 rerolls over 45–50; bolt effects and specials share the transform. Current Wiki permits polearm melee, while local blanket melee immunity still does not: **unresolved exception**, not advertised complete support. Damage uptime/movement are excluded.
- Other variant candidates already unavailable: Eclipse Moon clone/regular armour, Doom delve/shield, Maggot King phases, Yama P3/tank state, Jhallan, weakened Earthen Nagua, LANScape rotating bosses and Black Eye Bethel. Two Feral Vampyre IDs 4431/3707 retain an unresolved Defence 60 versus Wiki 30 discrepancy; no blind global refresh was made. Gear tier limits (e.g. low-tier bows with high-tier arrows), god-spell Charge/capes, charged elemental tomes, Mark of Darkness/Purging staff and other conditional spell effects are not complete.

## Numeric-candidate disposition (all 46 records)

| Candidates | Records | Disposition |
|---|---:|---|
| Guardian grounded | 1 | Correct live alias 16660 already has the differing values. |
| Bork | 1 | Base conflict plus phase-dependent armour; unavailable. |
| Duke/Whisperer/Leviathan/Vardorvis | 4 | Existing context resolver; unknown context unavailable. |
| P3 Wardens enraged | 2 | Shared IDs/ambiguous phase; unavailable. |
| Maiden 70/50/30 | 3 | Keep whole-bar maximum; phase percentage is current HP. |
| Blood spawn Entry | 1 | Wiki alias differs from live entry 10821; retain separate entry/normal rows. |
| Nightmare pillar HP variants | 2 | Existing context resolver; unknown HP withheld. |
| Verzik coloured adds | 6 | Disputed stats now unavailable; include three hard equivalents conservatively. |
| Yama P3 | 1 | Unresolved shared phase, already ambiguous. |
| Other ambiguous variants listed above | 23 | Retain unavailable state; no unvalidated replacement. |
| Feral Vampyre | 2 | Deferred source conflict outside raid priority; document 60 versus 30. |

Current dataset contains 2,918 variant records / 2,874 distinct IDs, versus 4,001 bundled IDs after adding eight Archive records. There are 29 exact-ID-unmatched records and two calculator-only Yama records (100001), plus 46 numeric candidates. Compared with the previous 38 unmatched, eight Archive adds are now represented, two virtual records are separated, and a newly published Ocellus P2 record adds one candidate. These are not unique missing-species counts.

Remaining unmatched live-ID records include Ocellus intro/controller aliases (16673/16675), Blink/Pete Kayer/sparring targets, LANScape moles, Outlaws, Venator quest variants and Giant Rock Crab 14424. They require individual attackability/context review. Existing intro exclusions must not be removed to make a comparison count look better.

## Weapon/input checks and next live tests

Recovered tests cover Obligator style/sizes/best-of-roll damage, Tome level scaling and stacks, Ascension plain/enchanted ammunition and diary rates, quiver compatibility, Zeal distinct from Piety/unlocked flag, matching fragment, saved-spell overrides, ornaments and boosted levels. New tests exercise Archive demonbane effectiveness in the actual snapshot modifier paths, Crumble Undead classification, add state transitions and unavailable specials. Boosted levels already come from the client; adding an overload multiplier would count the boost twice. Rondache maximum stays separate from mainhand special; shield accuracy/DPS remain unavailable.

Independent review caught a recovered Obligator integration bug: stance bonus had been used to select Thrust/Smash, so accurate Thrust and aggressive Smash were misclassified. Maximum, distribution and speed now share an explicit selected-mode field, with separate stance bonuses. Tests exercise all three configured indices in automatic/forced resolution. Wiki `utils.ts` defines accurate Thrust, aggressive Thrust and aggressive Smash; the correspondence to live COM_MODE slots 0/1/2 still needs client verification. Invalid indices withhold damage; no fallback silently treats them as Smash.

Prioritized manual tests after Gradle checks:

1. Record live Archive target IDs while Guardian lands, Xarth corpses are doused, jelly/binders spawn and Ocellus changes phase/add type. Verify pending messages and absence of guessed HP.
2. Compare Emberlight/Scorching bow/Demonbane against Dreadnoth/jelly and Xarth/Ocellus with copied gear, boosts, prayer and diagnostic inputs. Observe Bork stun/Smorks quartile without treating a single maximum as proof of a distribution.
3. Exercise Obligator Smash/Thrust, Tome initial special plus three follow-ups and target swaps, Ascension bolt diary/ammo states, Zeal/Piety, fragment matching and Rondache charges.
4. Test CoX normal/CM party sizes, ToB entry/normal/hard colour changes and disputed Verzik adds, ToA charging/P2/core/pending P3 variants. Include local drains and room resets; party-member drains remain unsynchronized.
5. Recheck DT2 normal/awakened identification, Vardorvis health, PNM pillar with Shadow/Voidwaker, Zulrah per-hit cap; verify Minimal/Standard/Advanced, optional Inspector and configured special accent.
