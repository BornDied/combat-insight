# Combat Insight

Combat Insight shows your max hit, hit chance and estimated DPS against the NPC you are fighting. It uses your equipped gear, attack style, active prayers and boosted levels, and updates as your setup changes.

Use it to compare weapons, see the effect of your own defence drains, or check a special attack without opening a separate calculator.

## Features

- **Max hit**, including separate hits for supported multi-hit weapons.
- **Hit chance and basic-attack DPS** for your current target.
- **Special attack previews** with maximum damage, hit chance, average damage and on-hit effects for supported weapons.
- **Target stat tracking** for Defence, Magic and Magic defence changes caused by your own supported specials. Optional infoboxes keep those stats visible.
- **Combat details** such as attack speed, known target health, gear bonuses, prayer effects and your average observed hitsplat.
- **Calculation Inspector** with the inputs and effects behind a result, explanations for unavailable values, and a copyable diagnostic report.

Weapon calculations include the twisted bow, Tumeken's shadow, scythe, fang, blowpipe, Obligator, Ascension crossbows and Zorya's tome. The plugin also handles applicable ammunition, set effects, elemental weaknesses, and Salve and Slayer bonuses. POH combat dummies can be selected for comparisons.

## Getting started

1. Install **Combat Insight** from RuneLite's **Plugin Hub** and open its settings.
2. Choose **Minimal**, **Standard** or **Advanced**. Leave combat type on **Automatic** for normal use.
3. Equip your gear, select your attack style, then attack or cast on an NPC.
4. Check the inputs below for your weapon or raid. The HUD updates for the target and retains its information between attacks.

Use RuneLite's overlay controls to move or resize the HUD.

## Choose what stays on screen

| Mode | Available information |
| --- | --- |
| Minimal | Max hit |
| Standard | Max hit, hit chance, DPS and special previews, with optional weapon, style, gear and prayer rows |
| Advanced | Standard information plus hit splits, average special damage, target stats, health, attack speed and accuracy rolls |

Each mode keeps its own row settings. You can adjust spacing, colours and the background, enable max-hit change flashes, and choose whether special maximums are highlighted. A hold-details key temporarily shows Advanced information without changing your saved mode.

The optional Inspector keeps longer explanations away from the combat HUD. Enable it in settings, then **Alt-right-click the HUD → Inspect calculation**. The same menu has **Copy diagnostic snapshot** and **Clear tracked reductions**.

For a longer Archive test, enable **Calculation inspector → Record raid diagnostics** before the encounter. Stop it afterward and use **Copy encounter report** in the Inspector. Recording is off by default, stays in memory and has a size limit shown in the report. Starting another recording or restarting the client clears it. It records observed NPC signals and calculation changes; it does not automatically certify a complete fight or identify every phase.

**Count Defence down to cap** shows how much Defence can still be drained. A displayed **0** means the tracked estimate has reached the minimum, which can be above zero. The tooltip and Inspector retain the actual level and floor. Disable the option to display the full Defence level instead. These remain local estimates; Party Hub reductions are not imported.

## Inputs to check

**Blowpipe:** select the darts loaded inside it. The worn ammo slot does not tell the plugin which darts are loaded. Change this setting when you change darts.

**Spells:** select your active spell under **Manual magic spell** when needed. Powered staves use their own attack. A saved spell does not replace a detected melee or ranged attack.

**Slayer:** enable **Target is Slayer task** when the current target qualifies for your helmet or mask bonus. Turn it off after the task.

**Raids:** open **Raid inputs** and check party size, mode and scaling settings. ToA HP calculations need invocation, starting party size and current path level; Wardens do not use path level. CoX and ToB have observed inputs and manual fallbacks. **Archive starting party size** accepts 1–8; 0 means unknown. Archive HP uses published table entries or matching boss-HUD data, so some targets still have no HP estimate.

**Defence tracking:** reductions come from your own confirmed supported specials. Teammates' drains are not imported. Clear tracked reductions from the HUD menu when an encounter resets or the estimate no longer matches the fight.

## Reading the results

**`?`** means a required input or calculation is unavailable. Open Inspector for the reason. **Immune** means a recognized rule prevents that attack from damaging the target.

DPS assumes continuous basic attacks. Movement, eating, missed attack cycles and encounter downtime affect the damage you actually deal.

There are encounter rules for CoX, ToB, ToA, Fractured Archive, DT2 bosses, Nightmare pillars, Zulrah and other targets. Coverage varies by phase. Some temporary buffs and raid phases still have unavailable calculations, including the Trio's imp-enhanced attack, Ezekiel's enrage and Ocellus's final-phase effects. Rondache's charge-based maximum is shown in Inspector; its shield accuracy and DPS are not available.

See the [calculation notes and remaining limits](docs/research/2026-10-24-archive-completion.md) and [latest testing notes](docs/research/2026-10-25-preflight.md) for details.

## Reporting a problem

[Open a GitHub issue](https://github.com/BornDied/combat-insight/issues) with the boss or form, weapon and ammunition, what you expected, and a copied diagnostic snapshot. Include the HUD mode for a display problem. The report identifies the plugin version, NPC, equipment, style and calculation inputs.

For local development, open this folder as a Gradle project with Java 11 or newer. Run `gradlew test` for the tests and `gradlew run` for the developer client. The [testing checklist](TESTING-0.9.0.md) covers the remaining manual checks.
