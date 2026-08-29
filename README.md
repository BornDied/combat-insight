# Combat Insight

Combat Insight is a RuneLite plugin for viewing live combat calculations while playing Old School RuneScape.

## Features

- Maximum hit, hit chance, and estimated basic-attack DPS
- Exact hit splits for supported multi-hit weapons
- Special maximum, chance, average damage, and on-hit summaries for supported weapons
- Solo tracking of Defence, Magic, and Magic Defence reductions from supported special attacks
- Player-Owned House combat-dummy targeting with variant-aware Salve, Slayer, and other modifiers
- Target-aware calculations using your current gear, combat style, prayers, boosts, and NPC target
- Compact target name and estimated health in Advanced mode
- Attack interval, observed average hitsplat, and optional accuracy details
- Minimal, Standard, and Advanced HUD modes with independent row settings

The displayed values update when your equipment, combat style, prayers, boosts, weapon, or target changes.

## How to use

1. Install Combat Insight from the RuneLite Plugin Hub.
2. Enable the plugin in RuneLite's plugin panel.
3. Open **Configure** and choose a HUD mode.
4. Equip your gear and select your combat style.
5. Attack or cast on an NPC to confirm it as your combat target.
6. View the calculated combat information in the movable overlay.

Combat Insight retains your last confirmed combat target between repeated kills. A new Attack or Cast action, or one of your hitsplats on another NPC, updates the target.

## HUD modes

| Mode | Available information |
| --- | --- |
| Minimal | Maximum hit |
| Standard | Maximum hit, hit chance, DPS, supported special maximum and chance, and optional combat-state rows |
| Advanced | Standard information plus hit splits, average special damage, special on-hit effects, tracked target stats, target health, attack interval, observed hits, and accuracy details |

The configuration sections match the HUD modes, making it clear which rows are available in Minimal, Standard, and Advanced mode. Separate target-display settings control Defence and Magic-related effects. The Magic infobox and Magic HUD rows appear only while the current combat style is Magic.

## Understanding the overlay

### Max hit and Max split

**Max hit** is the highest total damage your complete basic attack can deal. For a supported multi-hit weapon, **Max split** shows how that total is divided across its individual hitsplats.

### Hit chance

The estimated chance that one accuracy roll will successfully hit the current target.

### DPS

Your estimated average basic-attack damage per second, based on accuracy, damage, attack interval, equipment, and target information.

### Attack interval

The time between basic attacks, shown in game ticks. With the full Blood Moon set, Dual macuahuitl calculations include the set's accuracy-weighted timing effect.

### Target

Advanced mode can show the retained target and its estimated current health in a compact row, such as `Target: Vorkath - 401 / 750`.

Current health is estimated from RuneLite's NPC health-bar information, so it can differ slightly from the NPC's exact server-side health.

### Average hitsplat

The observed average of your own hitsplats against the current NPC, including zero-damage hits. Each hitsplat from a multi-hit attack is counted separately. The samples reset when you log out or the plugin stops.

## Supported multi-hit basic attacks

Version 0.4.0 includes complete basic-attack calculations for:

- Scythe of vitur, with one, two, or three hits based on target size
- Dual macuahuitl, including its conditional second accuracy roll
- Torag's hammers
- Sulphur blades
- Dark bow ordinary attacks

The ordinary `Max hit`, `Hit chance`, and `DPS` rows still describe basic attacks. Special attacks use their own rows so the two calculations are not mixed together.

## Supported special attacks

Version 0.4.0 supports:

- Dragon warhammer
- Elder maul
- Bandos godsword
- Burning claws, including their three accuracy rolls and expected burn damage
- Dragon claws
- Voidwaker
- Dragon dagger and its poisoned variants
- Dark bow, including dragon-arrow and other-arrow special behavior


- Tonalztics of ralos, including its two hits and the first hit's effect on the second accuracy roll
- Eye of ayak
- Accursed sceptre and Accursed sceptre (a)
- Bone dagger and its poisoned variants
- Arclight
- Emberlight
- Seercull


- Armadyl godsword
- Saradomin godsword
- Zamorak godsword
- Ancient godsword, including its conditional delayed damage

Advanced mode can show average special damage and a short on-hit summary. `Spec chance` says `Guaranteed` only when the special cannot miss; non-guaranteed multi-roll chances never round up to `100.0%`.

Burning claws average damage includes expected burn damage from that special when the target has room below the five-burn cap. Existing burns can lower the damage that is actually added.

Combat Insight tracks the local player's confirmed reductions to Defence, Magic level, and Magic Defence bonus when those values affect outgoing calculations. Arclight and Emberlight also describe their Attack and Strength effects, but only Defence is stored because that is the affected stat Combat Insight uses for outgoing accuracy. The tracker is solo only and does not read reductions caused by party members. Bone dagger's untouched-target guarantee is based on locally observed hits.

The default Defence display is a skill infobox showing the target's current level. Hold RuneLite's overlay-drag hotkey, normally Alt, to move it. Its static number and reduction-flash colors are configurable, and the box is removed when that NPC dies or despawns.

Tracked Magic level and Magic Defence bonus changes use a separate Magic-skill infobox by default. It appears for supported targets only while Magic is the current combat style, beginning with the target's base Magic Defence bonus before any reduction. Eye of ayak then updates that value, such as Scurrius changing from `10` toward `0`. Seercull and Accursed sceptre instead display the target's current Magic level after they reduce it. The complete Magic defence roll remains internal to hit-chance and DPS calculations. The Magic box has its own static and flash colors and can instead use Advanced HUD rows, both displays, or be hidden.

Natural NPC Defence and Magic recovery is estimated at one level per 100 game ticks. The infobox tooltip and HUD rows mark estimated recovery because the exact server-side recovery timer is not exposed to the client. Eye of ayak's Magic Defence reduction does not use this recovery estimate.

Ancient godsword `Spec max` and `Avg spec dmg` include its delayed 25 damage. Those values assume the target remains within five tiles until the eight-tick timer finishes. The on-hit row identifies the delay and matching 25 Hitpoint heal.

Dizana's quiver ammunition is read from RuneLite's live second-slot item and quantity values. Combat Insight selects ammo compatible with the current weapon, prioritises compatible ammo in the normal slot, and applies the charged or blessed quiver's hidden accuracy and Ranged Strength bonuses only to eligible arrows and bolts.

## Player-Owned House combat dummies

Clicking **Attack** on a supported POH combat dummy selects it as Combat Insight's target. Dummy targets show `Max hit` and, for supported special attacks, `Spec max`, `Avg spec dmg`, and `On hit`. Normal hit chance, DPS, attack rolls, and target Defence or Magic tracking are hidden because those values would be misleading for a dummy.

Normal dummies do not receive target-specific modifiers. Supported undead, Wilderness, kalphite, kurask, vampyre, and dragon variants apply only the modifiers represented by that form. Attacking a real NPC replaces the dummy target. Leaving the house, logging out, or changing worlds clears it.

## Calculation inputs

Most information is read automatically. Use the **Calculation inputs** section when RuneLite cannot determine a required value, including:

- The dart stored in a toxic blowpipe
- A manually cast spell
- Slayer-task status
- Tombs of Amascut invocation level
- A combat-type override for unusual hybrid weapons

## Current limitations

Combat Insight shows a notice when a mechanic cannot yet be calculated reliably. Current examples include:

- Special attacks outside the supported list above, and enchanted-bolt effects
- Chinchompa multi-target attacks
- Venator bow bounces
- Tonalztics of ralos ordinary multi-hit attacks; its special attack is supported
- Some location, phase, charge, stack, raid, or NPC-variant effects

## Troubleshooting

If a displayed value seems incorrect:

1. Confirm that your equipment and ammunition are correct.
2. Check your combat style.
3. Check your active prayers and boosts.
4. Confirm that the correct NPC is retained as the target.
5. Review any calculation notice in Advanced mode.
6. Reload the plugin if RuneLite has not updated the displayed information.

If the problem continues, include the following in your report:

- Equipment and ammunition
- Combat style
- Active prayers and boosts
- Target NPC
- The value you expected
- The value Combat Insight displayed
- Relevant screenshots or logs

For a special-attack report, also include the special weapon, ammunition, displayed special rows, visible hitsplats, and any tracked Target Def, Target Magic, or Magic def bonus values before and after the hit.

## Support

Report bugs and request features in [Combat Insight GitHub Issues](https://github.com/BornDied/combat-insight/issues).
