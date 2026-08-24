# Combat Insight 0.3.3 test checklist

## Start the clean build

1. Stop the RuneLite development window and the Gradle `run` task.
2. Open the local development copy, such as `D:\CombatInsight-V3\CombatInsight-0.3.3-dev`.
3. In IntelliJ, choose **File > Close Project**, then **Open** the new folder.
4. Let Gradle finish syncing with the installed Temurin 11 JDK.
5. Open the Gradle panel and double-click `Tasks > other > run`.

Keep the previous working project until this build has launched once. After
that, the older CombatInsight project folders can be deleted.

## First smoke test

1. Enable `Show max hit`, `Show hit chance`, and `Show DPS`, then choose a
   15-second or 30-second HUD duration.
2. Talk to, trade with, or pickpocket a non-combat NPC. The timed HUD must not
   activate and the NPC must not become the calculation target.
3. Attack Scurrius or another ordinary monster. Confirm `Hit chance` becomes a
   percentage and `DPS` becomes a number as soon as Attack is selected.
4. Click the ground, eat, drink a potion, and switch gear. The confirmed combat
   target and both numbers must remain.
5. Kill the target or let it despawn. Its name, hit chance, and DPS must remain
   available between kills. Attacking the next monster must replace the
   retained target and continue updating the HUD.
6. Interact with an unrelated NPC after the kill. It must not replace the last
   combat target or restart the configured HUD timer.

## HUD modes and settings

- Minimal shows only the enabled title and max-hit rows.
- Standard shows the compact live rows, including numeric hit chance and DPS.
- Advanced adds optional effective level, prayer damage, attack interval,
  target, accuracy rolls, target defence type, average hitsplat, and notices.
- The row switches are divided into `Minimal HUD rows`, `Standard HUD rows`,
  and `Advanced HUD rows`. Standard rows should also appear in Advanced mode,
  while Advanced rows should not appear in Standard mode.
- `Show hit chance` is the only hit-chance setting; it applies to Standard and
  Advanced modes.
- `Show accuracy rolls` and `Show target defence` independently control the two
  technical accuracy rows. Both default off to keep the HUD compact.
- Hiding boosted levels or prayer should also hide its extra Advanced detail.
- In Advanced mode, `Show target` should use one compact row such as
  `Target: Vorkath - 401 / 750` while the target's health bar is available.
  The current value is an estimate from the game health-bar ratio. Before a
  health bar is available, or after the NPC dies or despawns, the retained row
  should show only the target name rather than a stale or invented value.

## Melee accuracy

1. On the same target, switch a sword between Stab and Slash. Advanced mode
   should change `Target defence`, and the gear row should name the selected
   attack type.
2. Switch Accurate, Aggressive, Controlled, and Defensive. Accurate should
   improve the attack roll; Aggressive should improve max hit; Controlled
   should affect both.
3. Test separate low-level Attack and Strength prayers if available, then
   Chivalry or Piety. Hit chance and max hit should respond independently.
4. Recheck full Dharok at high and low Hitpoints.
5. If available, test Fang on Stab. Its displayed max is the 85% upper bound,
   its minimum is included in DPS, and its hit chance should be higher than a
   comparable normal weapon.

## Average hitsplat

1. Use Advanced mode and enable `Show average hitsplat`.
2. Select one NPC and land several attacks, including at least one zero if possible.
3. Confirm `Avg hit` or `Avg hitsplat` shows total displayed damage divided by the shown
   hitsplat count; zero-damage hits count as samples.
4. Briefly target a second NPC and confirm it starts a separate average. Return
   to the first living NPC and confirm its earlier samples are retained.
5. Confirm damage from other players in a group encounter is not counted.
6. Log out and back in, then confirm the statistic returns to `Waiting for hits`.

## Scurrius summoned rats

1. Select one of the rats summoned during the Scurrius encounter.
2. Confirm hit chance is `100.0%` and the predicted damage roll uses the current
   maximum hit.
3. Do not use an ordinary Varrock-sewer rat for this test; only Scurrius's
   summoned NPC has the guaranteed-hit mechanic.

## Multi-hit basic attacks

Use Advanced mode with `Show max hit`, `Show hit chance`, `Show DPS`, `Show
attack interval`, and `Show average hitsplat` enabled. Retain a supported NPC
before comparing values. `Max hit` is the total for one complete attack and
`Max split` shows the individual hitsplats after target flat armour.

1. Equip a Scythe of vitur variant and compare targets of size 1, 2, and 3 or
   larger. If its unsplit maximum is `M`, the splits should be `[M]`, then
   `[M, floor(M/2)]`, then `[M, floor(M/2), floor(M/4)]`. DPS should increase
   with target size while the displayed hit chance remains one roll's chance.
2. Equip Dual macuahuitl. The split should be `floor(M/2)` and the remainder.
   Compare DPS with the OSRS Wiki calculator using the same gear, levels,
   prayer, style, and target. The second hit contributes only when the first
   accuracy roll succeeds.
3. If the full Blood Moon set is available, equip its helm, chestplate, and
   tassets with Dual macuahuitl. The Advanced attack interval should become a
   fractional average below the normal interval, and DPS should increase.
4. Equip Torag's hammers and then Sulphur blades. Each should show two split
   hits with independent accuracy rolls. Odd maximum hits should put the extra
   point in the second hitsplat.
5. Equip a Dark bow with valid arrows and use ordinary attacks. The split
   should contain two identical full maximum hits. On a target with zero flat
   armour, the total should be twice one arrow's maximum.
6. Against a target with flat armour, confirm armour changes each split
   hitsplat separately rather than subtracting once from the whole attack.
7. Land several real attacks and confirm `Avg hitsplat` counts each visible
   hitsplat separately. This row is an observed statistic, not predicted
   damage per complete attack.

Do not use a special attack for these checks. Version 0.3.3 calculates ordinary
basic attacks only.

## Ranged accuracy and speed

1. Test a normal bow, a crossbow, and a blowpipe against the same target.
   Advanced mode should report Standard, Heavy, and Light ranged defence.
2. Switch Accurate, Rapid, and Longrange. Rapid should reduce the displayed
   attack interval by one tick and normally increase DPS without changing max
   hit; Accurate should improve attack roll and max hit.
3. Change ordinary bow/crossbow ammunition and confirm max hit and DPS change.
4. With a blowpipe, equip unrelated arrows or bolts. Nothing in its max hit,
   attack roll, or DPS should change. Change only the configured stored dart
   and confirm damage changes.
5. If available, test crystal armour with Bowfa or a crystal bow.
6. A Twisted bow should calculate only after a supported target is retained;
   test a low-Magic and high-Magic target if possible.

## Magic accuracy and powered weapons

1. Select Iban Blast or an elemental spell under `Manual magic spell`, then
   retain a target. Confirm Magic defence, hit chance, and DPS appear.
2. Test an elemental spell that matches a target weakness and one that does
   not. The matching element should improve both max hit and attack roll.
3. Test trident of the seas/swamp, Sanguinesti, warped sceptre, Bone staff, or
   another available powered staff with no autocast selected. It must remain
   Magic and calculate automatically.
4. If available, test Tumeken's shadow outside Tombs of Amascut. It should use
   the 3x equipment scaling after a target is selected. A Tombs target should
   use 4x. Set `Calculation inputs > ToA invocation` to the raid level before
   comparing its hit chance.
5. Toggle Mystic Lore/Might/Vigour/Augury and confirm both their accuracy and
   current magic-damage effects.

## Target modifiers

- With an applicable target, test Salve against undead.
- Test `Target is Slayer task` with a normal helm/mask for Melee and an imbued
  version for Ranged or Magic. Leave it off outside the actual assignment.
- If available, compare dragon hunter equipment on a dragon, demonbane on a
  demon, and a rat-bone weapon on a rat.
- An unsupported or ambiguous target must show a clear status, not a guessed
  percentage.
- Scythe of vitur, Dual macuahuitl, Torag's hammers, Sulphur blades, and Dark
  bow should show complete basic-attack DPS. Chinchompas, Venator bow, and
  Tonalztics of ralos should still say `Multi-hit DPS unavailable`.

## Slayer task input

The Slayer switch is currently a manual calculation input. It lets you test the
formula without an assignment, but the game's real hits will only receive the
bonus when the retained NPC is actually your assigned target in the correct
task area.

1. Equip a normal black mask or unimbued Slayer helmet and use Melee. Toggle
   `Target is Slayer task`; max hit, hit chance, and DPS should increase.
2. Keep the unimbued mask/helmet and use an ordinary Ranged or Magic weapon.
   The switch must not apply a bonus, and Advanced notices should request an
   eligible imbued mask or helmet.
3. Equip an imbued black mask or Slayer helmet (i). Ranged and Magic max hit,
   hit chance, and DPS should now increase when the switch is enabled.
4. If testing an Eclipse atlatl, an unimbued mask/helmet may increase max hit
   through its Strength-based damage formula, but only an imbued version may
   increase its Ranged accuracy. Advanced mode should explain that split with
   an `Atlatl accuracy needs an imbued helm or mask` notice.
5. Leave the switch off whenever the selected NPC is not your actual task.

## Notices and basic attacks

1. Equip a magic shortbow and use ordinary attacks. It should no longer show a
   permanent bow warning.
2. Equip a blowpipe with `Blowpipe dart` set to `Not set`. Advanced mode should
   show a `Notice` explaining that the stored dart must be selected.
3. Turn off `Show calculation notices`; only Notice rows should disappear.
4. Special attacks are not calculated in this version. All displayed weapon
   values describe ordinary basic attacks.

## Useful bug report details

Record the weapon, ammunition, every worn item, boosted levels, attack style,
offensive prayer, target name, Slayer-task switch, Combat Insight max hit/hit
chance/DPS, and any Advanced-mode `Notice` text. A screenshot of the HUD and
equipment tab is ideal; no account or login details are needed.
