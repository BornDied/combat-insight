# Combat Insight

Combat Insight is a modern RuneLite DPS and hit calculator for Old School RuneScape.

GearScape is being used as a reference point for the user experience and as one of the validation targets for the calculation engine. The plugin will not scrape or embed the GearScape website. GearScape's public site credits the OSRS Wiki and Bitterkoekje DPS Calc for its foundations, while its terms reserve the operator's intellectual property. We can independently implement the public game formulas, validate equivalent inputs against GearScape and the OSRS Wiki calculator, and request permission from the GearScape author if a direct integration is ever desired.

The project is intentionally split into two layers:

1. A pure combat-calculation engine that can be tested without starting RuneLite.
2. A RuneLite adapter and interface that turns the player's live gear, skills, prayers, boosts, and target into a calculation input.

## Product direction

The plugin is intended to be a successor to the archived DPS Calculator plugin, with a live combat snapshot rather than a form that must be repeatedly filled in.

The first usable version will show:

- Current equipment, combat style, boosted levels, and active offensive prayer
- Selected or right-clicked NPC target
- Maximum hit, accuracy, average hit, attack interval, DPS, and estimated time to kill
- A calculation breakdown explaining how each result was produced
- Manual target overrides for NPC variants and mechanics that cannot be inferred from the client alone

## HUD concept

The HUD is the main interface. The side panel is reserved for configuration, target editing, saved setups, and detailed explanations.

The default `Standard` layout is a small movable card that can sit near the bottom-center of the game window or beside the target. The title row and attack-style row are optional, so the card can stay focused on the numbers that matter to you:

| Area | Information |
| --- | --- |
| Header | Combat Insight, target name, and current weapon/style |
| Primary result | Max hit and hit chance |
| Secondary results | Average hit, DPS, attack interval, and estimated time to kill |
| State row | Active offensive prayer, boosts, and notices |
| Optional detail | Attack roll, defence roll, strength bonus, and formula breakdown on hover |

Three display modes are available:

- `Minimal`: one-line max-hit display, similar to the old plugin
- `Standard`: clean card with the live max hit and selected state rows
- `Advanced`: expanded card with effective strength, prayer multiplier, attack interval, and DPS/target status

The overlay should react visually without becoming distracting. When `Animate changes` is enabled, a max-hit increase briefly pulses green and a decrease pulses red. `HUD duration` can keep the card permanent or hide it 15 or 30 seconds after combat ends. Every effect is optional in the configuration.

## Independent switches

Every visible feature will have its own setting, so the plugin can be used as a minimal max-hit display or as a complete combat dashboard:

- HUD visibility and display mode
- Max hit, special-attack max hit, accuracy, average hit, DPS, and time-to-kill rows
- Target name and target-health information
- Prayer, boost, weapon, and attack-style indicators
- Actual maximum-hit marker
- Gear-change pulse and DPS-delta indicator
- Notice rows for missing inputs, unsupported mechanics, and conditional effects
- Target-attached overlay and detailed hover tooltip

The RuneLite settings are grouped into four compact sections:

- `HUD` contains visibility, mode, duration, title, and background color.
- `Show and hide rows` is a collapsed dropdown for every optional HUD row.
- `Calculation inputs` contains the few facts RuneLite cannot read, such as the dart stored inside a blowpipe, a manually cast spell, Slayer-task state, or Tombs of Amascut invocation. It also has a combat-type override for unusual hybrid weapons and manual casting.
- `Effects and advanced details` contains animation and calculation notices.

Later versions can add saved setup comparisons, upgrade suggestions, special attacks, raid modifiers, Slayer effects, and detailed hit distributions.

## Design principles

- Keep RuneLite API code out of the calculation engine.
- Keep combat effects data-driven and modular so new OSRS items can be added without rewriting the UI.
- Prefer transparent calculations over a single unexplained number.
- Treat unsupported mechanics as explicit notices instead of silently giving an inaccurate result.
- Provide a local personal build first, then prepare a Plugin Hub submission after the calculations are validated.

## Current test build

Version `0.3.2-SNAPSHOT` adds the target-stat and accuracy layer plus the first HUD clarity pass. The project now bundles a compact snapshot of more than 2,800 NPC IDs, including defence levels, Magic levels, stab/slash/crush defence, light/standard/heavy ranged defence, elemental weaknesses, flat armour, size, and combat attributes. It does not contact GearScape or the OSRS Wiki while RuneLite is running.

The retained NPC target now powers real hit-chance and basic-attack DPS rows in both Standard and Advanced modes. Clicking the ground, eating, drinking a potion, or switching gear does not discard it. Advanced mode can additionally show the player and target accuracy rolls, target defence type, and `Avg hit` from the local player's actual hitsplats. The average includes zero-damage hits, displays its sample count, keeps separate samples for separate NPC actors, and resets when the plugin stops or the player logs out. The two raw accuracy-detail rows default off and have independent visibility switches.

The current live calculation covers:

- Exact selected stab, slash, or crush attack bonus rather than adding each item's strongest melee bonus.
- Light ranged for thrown weapons and blowpipes, standard ranged for bows, heavy ranged for crossbows and chinchompas, and mixed ranged for salamanders.
- Offensive accuracy prayers as well as damage prayers, including separate low-level Attack and Strength prayers, Deadeye, Mystic Vigour, and the currently exposed Ruinous Powers.
- Accurate, Aggressive, Controlled, Rapid, Longrange, manual casting, Void accuracy multipliers, crystal-armour accuracy, target flat armour, and the 0.6-second game tick.
- Fang min/max damage and its normal double-roll accuracy, including the different Tombs of Amascut roll behaviour.
- Twisted bow target-Magic scaling and Tumeken's shadow 3x/4x equipment scaling once a supported target is retained.
- Salve, Slayer helmet or black mask, common dragonbane/demonbane/kalphite/golem/rat modifiers, elemental weaknesses, Dharok HP scaling, and several other target attributes. The manual Slayer input distinguishes unimbued Melee gear from the imbued Ranged and Magic bonuses, including the Eclipse atlatl's hybrid damage rule.
- Automatic powered Magic formulas for tridents, Sanguinesti, warped/Accursed/Thammaron's sceptres, Tumeken's shadow, Bone staff, Eye of ayak, Dawnbringer, and Gauntlet staves.
- Blowpipe arrows or bolts remain ignored; the stored dart is still selected manually because the worn equipment container does not expose it. Ordinary bows and crossbows continue to react to their equipped ammunition.
- Scurrius's summoned rat is treated as a guaranteed max-damage hit rather than using its ordinary defence roll.

Some mechanics still need an explicit status instead of a misleading number:

- Multi-hit weapon DPS distributions such as the Scythe, dual macuahuitl, chinchompas, Venator bow, and Tonalztics. Their single-hit accuracy can still be shown.
- Special attacks, enchanted-bolt procs, raid invocation scaling, phase-specific immunity or caps, Wilderness location bonuses, and effects that depend on charges or stacks the client does not expose here. Every displayed weapon value currently describes an ordinary basic attack.
- Automatic last-cast spell detection. Normal spellbook casting still uses `Calculation inputs > Manual magic spell`; powered weapons are automatic.
- A small number of duplicated NPC IDs whose variants have different stats. These display `Target variant needed` rather than choosing one silently.

These exceptions appear as clear HUD statuses or Advanced-mode notices. See `THIRD_PARTY_NOTICES.md` for the bundled data provenance and `TESTING.md` for the focused validation checklist.

## Opening the starter build on Windows

1. Extract this project to a folder such as `D:\CombatInsight`.
2. In IntelliJ IDEA choose **Open** and select that folder.
3. Allow IntelliJ to load the Gradle project. If it asks for a Gradle JVM, choose the installed **Temurin 11** JDK.
4. Open the Gradle tool window and run `Tasks > other > run`, or run `gradlew.bat run` in IntelliJ's terminal.
5. RuneLite should start in developer mode. Log in to a test world and look for the movable Combat Insight HUD.

The first run downloads the RuneLite and Gradle dependencies, so it can take a few minutes. Start the development client from the Gradle `run` task, log in only when you are ready to test, and use Alt-drag to move the overlay. The clean movable HUD is intentionally read-only: it does not send mouse or keyboard input. The RuneLite config panel still uses the plugin name `Combat Insight`; that is the normal RuneLite settings heading. The HUD title itself is controlled separately by `Show HUD title`.

See `TESTING.md` for a short melee, ranged, Magic, Dharok, blowpipe, and Slayer checklist.
