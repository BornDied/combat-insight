# Combat Insight

See your max hit, hit chance and estimated DPS while you play. Combat Insight uses your equipped gear, combat style, active prayers, boosted levels and current NPC target. Swap a weapon, change prayers or land a supported defence drain, and the numbers update.

## What it shows

- **Max hit and hit splits** for supported single-hit and multi-hit attacks.
- **Hit chance and basic-attack DPS** against your current target.
- **Special attack previews:** maximum damage, chance to hit, expected damage and on-hit effects for supported weapons.
- **Tracked target stats:** Defence, Magic and Magic defence changes from your own supported special attacks, with optional infoboxes.
- **Combat details:** attack interval, target health when known, gear bonuses, prayers and the average of your observed hitsplats.
- **Calculation Inspector:** a separate panel with inputs, applied effects, notices and a copyable diagnostic report.

Calculations include supported effects for weapons such as the twisted bow, Tumeken's shadow, scythe, fang, blowpipe, Obligator, Ascension crossbows and Zorya's tome. Compatible ammunition, set effects, elemental weaknesses and applicable Salve/Slayer bonuses are included. POH combat dummies can be selected for comparisons.

## Getting started

1. Install **Combat Insight** from RuneLite's **Plugin Hub**, then open its configuration.
2. Choose **Minimal**, **Standard** or **Advanced**.
3. Equip your gear and select your attack style. **Automatic** combat type is recommended.
4. Attack or cast on an NPC. The overlay updates for that target and retains its information between kills.
5. Set any inputs your setup needs under **Calculation inputs** and the raid settings.

Use RuneLite's overlay controls to move or resize the HUD. Each display mode keeps its own row settings.

| Mode | What you can display |
| --- | --- |
| Minimal | Max hit |
| Standard | Max hit, hit chance, DPS, special maximum/chance and optional gear, style and prayer rows |
| Advanced | Standard rows plus hit splits, expected special damage, target stats, health, attack interval and accuracy rolls |

Choose compact spacing, quiet colours, a background style and optional max-hit change flashes. Special maximum highlighting has its own toggle and colour. A configurable hold-details key temporarily opens the detailed view without changing your saved mode.

## Inputs to check

**Blowpipe darts:** select the darts stored inside your blowpipe. The worn ammo slot cannot tell the plugin what is loaded. Update this setting when you change darts.

**Spellbook casting:** select your active spell under **Manual magic spell** when needed. Powered staves use their own attack; a saved spell does not override a detected melee attack.

**Slayer task:** enable **Target is Slayer task** when the current target qualifies for your equipped helmet or mask bonus. Turn it off after the task.

**Raids:** check the party, mode and scaling settings. ToA HP calculations need your invocation, starting party size and the current path's level. Unknown party/path inputs withhold HP-dependent damage; Wardens do not use path level. CoX and ToB use available observed inputs and their configured fallbacks.

**Defence tracking:** the plugin tracks your own confirmed supported specials. It does not automatically know your teammates' drains. Clear tracked reductions from the HUD menu if the encounter resets or the tracked state no longer matches the fight.

## What does `?` mean?

A required input or encounter rule is unavailable. The HUD keeps the value short. Enable the optional Inspector and use **Inspect calculation** from the HUD menu to see the reason, or choose **Copy diagnostic snapshot**. The report includes the plugin version, NPC ID, equipment IDs, style and calculation inputs, making it useful when reporting a mismatch.

**Immune** means a supported rule blocks that attack style. DPS estimates assume continuous basic attacks; movement, eating, missed attack cycles and unmodelled temporary encounter effects can change your actual damage.

## Encounter coverage

Combat Insight includes rules for CoX, ToB, ToA, Fractured Archive, DT2 bosses, Nightmare pillars, Zulrah and other supported encounters. Coverage varies by phase. Bork uses observed stun state and Smorks health to select his armour; Ralos drains can be tracked even while Bork's damage state is unknown. Doused Xarth corpses, Ocellus protection prayers and attackable Archive adds have separate handling.

Some mechanics still show `?`, including unresolved Trio/Moia states, Ocellus's final-phase damage/timing modifiers and Warden P2 special distributions. Rondache's charge-based shield-bash maximum is available in the Inspector; shield accuracy and DPS remain unavailable. See the [current calculation notes and limitations](docs/research/2026-10-24-alpha3-follow-up.md) for details.

## Reporting a problem

Open an issue on [GitHub](https://github.com/BornDied/combat-insight/issues) with the boss/form, weapon and ammo, expected result, and a copied diagnostic snapshot. Include the HUD mode if it is a display issue. Screenshots help with animation or phase mismatches.

For local development, import this folder as a Gradle project with Java 11 or newer. Run `gradlew test` to check the code and `gradlew run` to launch the developer client. The [testing checklist](TESTING-0.9.0.md) lists the live checks still needed.
