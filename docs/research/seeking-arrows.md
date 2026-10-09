# Seeking arrows and Necklace of rupture — September 30, 2026

## Sources and scope

Verified against the OSRS Wiki-maintained calculator repository, revision `89c3e25b`:

- https://github.com/weirdgloop/osrs-dps-calc/blob/89c3e25b/src/lib/PlayerVsNPCCalc.ts — Seeking accurate-hit transformation.
- https://github.com/weirdgloop/osrs-dps-calc/blob/89c3e25b/cdn/json/equipment.json — item IDs and bonuses.
- Jagex's Blood Moon Rises rewards announcement identifies the Elder venator fang as the component for Necklace of rupture: https://oldschool.runescape.wiki/w/Update:Blood_Moon_Rises_Rewards

Formulas were implemented independently; upstream calculator implementation was not copied.

Seeking arrows clamp each accurate damage roll to at least 3 before armour/encounter adjustments. They do not change misses into hits and do not replace the distribution with a uniform 3-to-max roll. Relative to ordinary accurate-zero-to-one handling, the mean increases by 5/(maximum+1) for an unrestricted 0-to-maximum roll with maximum >=3. Their +20 ranged attack also changes accuracy. Ordinary and Seeking amethyst arrows have the same +55 ranged strength, so Seeking alone does not increase an ordinary maximum above 3.

Necklace of rupture (33639) provides +20 ranged attack, +8 ranged strength and +3 Prayer. The supported calculation treats these as equipment bonuses; no extra damage multiplier is evidenced by the maintained reference.

## Implementation

- Selected active ammo governs the floor. Compatible worn arrows take priority; otherwise compatible quiver ammo is used. Inactive arrows do not affect self-powered bows, crossbows, melee or magic.
- Basic Tbow and other supported arrow-firing bows use clamped accurate-roll means, then hit chance and attack interval for DPS. Dark-bow basic hits each receive the floor.
- Magic shortbow, magic longbow, Seercull and Scorching bow supported direct specials include Seeking. Dark-bow specials already have a higher minimum. Existing unavailable/raid-special guards remain.
- RuneLite equipment data remains authoritative. A null-entry fallback supplies the nine Seeking arrow variants and rupture bonuses. It replaces missing data only; it does not stack duplicate bonuses.
- Venator bounce DPS remains unavailable under the existing multi-hit guard. This change does not claim new bounce support.

## Reproduction and remaining uncertainty

The live target-result regression exercises Tbow with Nylocas Vasilias ranged ID8357. For a supplied 80 maximum, Seeking preserves maximum80 while adding 5/81 to mean successful damage and increasing ranged accuracy. Removing the live Seeking wiring makes the regression fail.

The prior reported low maximum in a three-person ToB is not explained by the Seeking floor alone. A diagnostic snapshot and expected/actual maximum are still needed. Confirm rupture vs anguish, potions, prayer, stance, charged quiver, and ranged boss phase. This is not an in-game verification claim.
