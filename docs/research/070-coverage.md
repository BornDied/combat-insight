# 0.7 coverage audit — October 2, 2026

This is a source/guard audit of the local candidate, not a fresh complete Wiki import or in-game certification.

## Inventory

- 3,976 bundled NPC IDs; 30 raw rows carry an ambiguous-variant flag. Runtime encounter resolution can resolve some of these using observed evidence. These are IDs, not unique species/boss counts.
- 42 rows tagged Chambers (`xerician`), including 9 `cox_cm` rows; 118 rows tagged Theatre of Blood.
- 35 entries in `SpecialAttackWeapon`. Weapon aliases/charged variants are matched separately. This is not an exhaustive list of every in-game special.
- Header dates: full infobox snapshot September 1; CoX/ToB refresh September 25. No new NPC import in 0.7. A reliable current missing-NPC count requires a fresh authoritative reference export; it cannot be inferred from the bundled row count.

## Mechanics versus presence

| Area | Implemented in this candidate | Remaining qualification |
| --- | --- | --- |
| PNM / Nightmare pillars | Attackable IDs 9435/9438/9441/9444; shared combat stats; magic damage ×2; observed boss distinguishes HP variant | Charged states not certified; hidden boss leaves HP unknown; live phase validation needed |
| Voidwaker / saved spells | Separate melee-derived spec base; saved spell cannot turn a detected melee stance into magic | Forced combat type remains an intentional override; actual spell selection is manual |
| DT2 normal / Awakened | Duke, Whisperer, Leviathan, Vardorvis resolved by observed combat level or explicit override; Vardorvis HP-based Defence | Health-bar approximation; unobserved shared variant/HP stays unavailable; live checks pending |
| CoX normal / CM | Party/mode/highest combat observation; manual HP/Mining; basic Guardian, Ice demon, Tekton, Vasa crystal and Olm transforms | Affected raid-special transforms remain guarded; team stats are not read from Party Hub |
| ToB Entry / Normal / Hard | Bundled phase IDs, party HP scaling, Nylo style immunity, Verzik P1 basic caps | New roster observer is opt-in; reconnect/mid-raid starts can miss original members; guarded specials remain |
| Nex | Records 11278–11286 are present | Row presence does not establish complete phase/mechanic coverage; dedicated phase audit still needed |
| Seeking arrows / rupture necklace | Active compatible ammo, floor on accurate arrow damage, average damage/DPS, equipment fallback | Venator bounce DPS not modelled; max hit need not increase from the floor |
| Enchanted bolts | Ruby, diamond, onyx, dragonstone, including supported Zaryte combinations | Other enchanted types and guarded raid-transform combinations are unavailable |
| Special weapons | 35 enumerated entries including kisten, Eye of ayak, rosewood blowpipe, Fang and halberds | Context conditions matter; burns/delayed effects are not uniformly included in direct expected damage |
| Overloads / boosts | Calculations read current boosted Attack/Strength/Ranged/Magic each capture | No future boost-upkeep simulation; current DPS is not a forecast across potion expiry |
| Local reductions | Off/local controls; reset; attempts separated from reduction estimates | No other-player drains or Party Hub integration; unknown conditional first-hit history must not promise a guarantee |

Source files inspected: `TargetDatabase`, `TargetMechanics`, `RaidScaling`, `RaidDamageRules`, `EncounterResolver`, `SpecialAttackWeapon`, `LiveCombatSnapshot`, `TargetEffectTracker`. Regression files include `NightmarePillarTest`, `SavedSpellRegressionTest`, `SeekingLiveCalculationTest`, `EncounterResolverTest`, `RaidDamageRulesTest`, `SpecialInputRegressionTest`.

## Automatic input evidence

The locally available RuneLite API `Varbits.java` documents ToB party status 2/3 as inside/spectating; orbs 1–27 present, 30 dead, 0 hidden. Current gameval names are `TOB_CLIENT_PARTYSTATUS`, `TOB_CLIENT_P0` through `P4`. The observer rejects invalid values and retains the largest valid roster until raid exit. This is an inference about roster size, not a dedicated starting-size signal, hence opt-in and explicit provenance. Public source lookup was unavailable during this pass; live validation is required.

Automatic spell-ID mapping was not added without verified source mapping. Powered staffs remain automatically detected; manual spellbook casting retains the explicit setting.

## Next research after this candidate

1. Fresh authoritative NPC export and a comparison with the 3,976 bundled IDs.
2. Nex and raid phase-by-phase mechanics, especially currently guarded specials.
3. Verified spell observation, then more automatic inputs.
4. Remaining special families and Venator bounces as separate verified calculations.

Do not present these remaining items as completed by 0.7.
