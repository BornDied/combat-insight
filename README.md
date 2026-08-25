# Combat Insight

Combat Insight is a RuneLite plugin for viewing live combat calculations while playing Old School RuneScape.

## Features

- Maximum hit, hit chance, and estimated basic-attack DPS
- Exact hit splits for supported multi-hit weapons
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
| Standard | Maximum hit, hit chance, DPS, and optional combat-state rows |
| Advanced | Standard information plus hit split, target and estimated health, attack interval, observed hits, and accuracy details |

Every row has its own setting. The configuration sections match the HUD modes, making it clear which rows are available in Minimal, Standard, and Advanced mode.

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

Version 0.3.3 includes complete basic-attack calculations for:

- Scythe of vitur, with one, two, or three hits based on target size
- Dual macuahuitl, including its conditional second accuracy roll
- Torag's hammers
- Sulphur blades
- Dark bow ordinary attacks

Special attacks are not included in these values. Every weapon result currently describes an ordinary basic attack.

## Calculation inputs

Most information is read automatically. Use the **Calculation inputs** section when RuneLite cannot determine a required value, including:

- The dart stored in a toxic blowpipe
- A manually cast spell
- Slayer-task status
- Tombs of Amascut invocation level
- A combat-type override for unusual hybrid weapons

## Current limitations

Combat Insight shows a notice when a mechanic cannot yet be calculated reliably. Current examples include:

- Special attacks and enchanted-bolt effects
- Chinchompa multi-target attacks
- Venator bow bounces
- Tonalztics of ralos attack behavior
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

## Support

Report bugs and request features in [Combat Insight GitHub Issues](https://github.com/BornDied/combat-insight/issues).
