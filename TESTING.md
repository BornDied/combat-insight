# Combat Insight 0.4.0 Wave 1A and 1B test checklist

## Start the clean build

1. Stop the RuneLite development window and the Gradle `run` task.
2. Open your local Combat Insight development folder, such as `C:\CombatInsight\combat-insight`.
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
6. Equip any Inquisitor armour and select a Crush attack. Its damage and
   accuracy bonus should apply without an Inquisitor notice. Switch to Stab or
   Slash and confirm the notice appears because the bonus is then inactive.

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

Use ordinary attacks for this section. The basic rows and special rows are
separate, so `Max hit`, `Hit chance`, and `DPS` must continue to describe the
ordinary attack.

## Special attacks: Wave 1A

Use Advanced mode and open `Special attack rows`. Leave `Show spec max`, `Show
spec chance`, `Show average spec damage`, and `Show on-hit effect` enabled. Set
`Target Defence display` to `Movable infobox`. Retain a supported NPC before
comparing accuracy or average damage.

1. Equip each supported special weapon without using the special. Confirm the
   special rows appear automatically while the ordinary basic-attack rows stay
   unchanged.
2. Equip a Dragon warhammer. `Spec max` should be 50% above the ordinary
   single-hit maximum before target flat armour. After a confirmed positive
   special hitsplat, the Defence infobox should fall by 30% of its current
   value.
3. Equip an Elder maul. Its special maximum should equal its ordinary maximum,
   while its accuracy uses a 25% boost. A confirmed positive special should
   reduce current Defence by 35%.
4. Equip a Bandos godsword. Its accuracy roll should be doubled and its damage
   maximum should apply two separate 10% increases with rounding after each
   stage. The Defence infobox should fall by the displayed special damage,
   stopping at any supported target floor.
5. Equip Dragon claws. Advanced `Spec max` should include four split values,
   `Spec chance` should mean at least one of the four accuracy rolls succeeds,
   and `Avg spec dmg` should include the inaccurate fallback damage when all
   four rolls fail. A very high but fallible chance must stop at `99.9%` rather
   than rounding to `100.0%`.
6. Equip a Voidwaker. `Spec chance` should say `Guaranteed`, with damage ranging
   from 50% through 150% of the ordinary melee maximum.
7. Equip Burning claws. Advanced `Spec max` should show three split values and
   `Spec chance` should cover up to three accuracy rolls. Test both Stab and
   Slash styles. `Avg spec dmg` should include the special's expected burn
   contribution, and `On hit` should mention its 15-45% burn rolls.
8. Equip a Dragon dagger or poisoned variant. Advanced `Spec max` should show
   two equal hits, each with 15% extra accuracy and maximum damage. `Spec
   chance` should mean at least one of the two rolls succeeds.
9. Equip a Dark bow with at least two arrows. Non-dragon arrows should use a
   minimum of 5 and 30% extra maximum damage per arrow. Dragon arrows should
   use a minimum of 8 and 50% extra maximum damage, capped at 48 per arrow.
   `On hit` should show `Min 5` for non-dragon arrows and `Min 8, max 48` for
   dragon arrows. Removing the second arrow should produce `Equip 2 arrows`
   instead of a guessed result.
10. With Dizana's quiver equipped, put arrows in the quiver and bolts in the
    normal ammo slot. Dark bow must use the quiver arrows and ignore the bolts.
    Then put compatible arrows in both slots; the normal ammo slot must take
    priority. If the quiver is charged or blessed, verify the hidden +10 Ranged
    accuracy and +1 Ranged Strength affect eligible arrow and bolt attacks.
11. In Standard mode, multi-hit `Spec max` should show only the total. Advanced
   mode should add the split in parentheses.

## Special attacks: Wave 1B

Test these only after the Wave 1A checklist passes. Keep Advanced mode and the
four Special attack row settings enabled.

1. Equip charged Tonalztics of ralos. `Spec max` should show two equal hits,
   each at 75% of the ordinary single-hit maximum. Accuracy receives a 50%
   boost. Each successful hit should reduce Defence by 12.5% of the target's
   current Magic level. The first hit's reduction should improve the second
   hit's calculated accuracy when it lands.
2. Equip Eye of ayak. Its special accuracy roll should be doubled and its base
   maximum should increase by 30% before Magic damage bonuses. A successful hit
   should reduce the target's Magic Defence bonus by the displayed damage. With
   a Magic combat style active, a separate Magic infobox should show the target's
   current Magic Defence bonus. On Scurrius this begins at `10` and falls toward
   `0`. Leave the NPC alive and confirm that value does not use the normal
   one-level recovery.
3. Equip an Accursed sceptre or Accursed sceptre (a). The special should use
   50% extra accuracy and maximum damage. One successful hit should reduce both
   `Target Def` and `Target Magic` by at most 15% of their base levels. Reusing
   the special must not push either stat below that weapon's 15% limit.
4. Equip a Bone dagger variant and choose a fresh solo target. Before any local
   hit, `Spec chance` should say `Guaranteed`. A successful special should
   reduce Defence by its displayed damage only if Defence was not already
   lowered. Treat group encounters as outside this wave.
5. Equip Arclight and then Emberlight. Both specials use Stab accuracy and
   should reduce Defence after a successful hit. Compare a normal target with
   a demon: Arclight uses 5% + 1 normally and 10% + 2 on demons; Emberlight
   uses 5% + 1 normally and 15% + 1 on demons.
   When a supported target is selected, neither weapon should display the
   notice `Weapon damage modifier needs the target type`.
6. Equip Seercull with arrows. `Spec chance` should say `Guaranteed`. Its
   maximum must use current Ranged level and the active arrow's Ranged Strength
   only, without gear, prayer, Void, Slayer, or Salve damage bonuses. A
   successful hit should add a `Target Magic` row reduced by displayed damage
   only when Magic was not already lowered. Removing the arrows should show
   `Equip arrows`. Repeat with compatible arrows inside Dizana's quiver.
7. For every weapon, confirm an ordinary attack without an energy drop does
   not change any tracked target stat.

## Special attacks: Wave 1C

Test these after Wave 1B behaves correctly. Use whichever godswords are
available and keep the ordinary basic-attack rows visible for comparison.

1. Equip an Armadyl godsword. The special should use doubled accuracy and 25%
   increased maximum damage. `On hit` should say `25% increased damage`.
2. Equip a Saradomin godsword. The special should use doubled accuracy and 10%
   increased maximum damage. `On hit` should summarize 50% Hitpoint healing
   with a minimum of 10 and 25% Prayer restoration with a minimum of 5.
3. Equip a Zamorak godsword. The special should use doubled accuracy and 10%
   increased maximum damage. `On hit` should say it freezes the target for 20
   seconds.
4. Equip an Ancient godsword. The special should use doubled accuracy and 10%
   increased direct maximum damage. Advanced `Spec max` should show the direct
   hit followed by 25 delayed damage. `Avg spec dmg` includes that 25 damage
   under the assumption that the target remains within five tiles until the
   eight-tick timer finishes. `On hit` should also mention the 25 Hitpoint heal.
5. Confirm none of these four specials changes the Defence infobox, Target
   Magic, or Magic Defence rows.

## Player-Owned House combat dummies

Use Advanced mode with the ordinary and special rows enabled. Enter a
Player-Owned House containing the listed dummy forms. Attacking a dummy should
select it immediately even though it has no normal NPC Defence model.

1. Attack a normal Combat dummy and an Ornate combat dummy. The target row
   should name the selected dummy and `Max hit` should update with gear, level,
   prayer, and attack-style changes.
2. Equip a supported special-attack weapon. Confirm `Spec max`, `Avg spec dmg`,
   and `On hit` appear. The dummy's guaranteed maximum-hit behaviour should be
   reflected in average special damage. `Spec chance` must not appear.
3. Confirm `Hit chance`, `DPS`, and `Your / target roll` are hidden while any
   dummy is selected. A dummy has no normal Defence roll, so these values must
   not be guessed.
4. Set both target-stat displays to an infobox or `Both`. Confirm attacking a
   dummy creates neither a Defence infobox nor a Magic infobox and does not
   show tracked target-stat rows.
5. On a normal dummy, toggle the manual Slayer input and equip Salve or
   demonbane gear. None of those target-specific bonuses should be added.
6. Attack an Undead combat dummy and an Ornate undead combat dummy. These two
   forms should apply undead, demon, and Slayer-target modifiers. Compare Salve,
   demonbane, and eligible Slayer-helmet setups with their ordinary-dummy
   values.
7. Switch the ornate dummy to Wilderness, kalphite, and kurask forms. Confirm
   Wilderness weapons receive their Wilderness damage modifier only on the
   Wilderness form, Keris modifiers apply only to the kalphite form, and the
   kurask form requires an eligible leaf-bladed weapon.
8. Switch to the vampyre form and confirm vampyre weapon restrictions apply.
   Switch to the dragon form and confirm dragonbane modifiers apply, while
   Salve does not. The dragon dummy deliberately does not count as undead.
9. With a supported multi-hit basic weapon, confirm `Max hit` is the complete
   attack total and `Max split` remains available in Advanced mode. Hit chance
   and DPS must remain hidden.
10. Attack a real NPC after selecting a dummy. The real NPC should immediately
    replace the dummy, restore normal hit chance and DPS, and allow the normal
    Defence or Magic infobox rules.
11. Re-select a dummy, then leave the house. Repeat with logout and a world
    change. In every case the dummy target must clear and must not reappear
    after returning to the game.

## Solo target-effect tracking

1. Attack a supported NPC. A small movable Defence-skill infobox should appear
   with its base Defence. Hold Alt and drag it to confirm RuneLite remembers its
   position.
2. Change `Defence number color` and `Defence flash color`. The resting number
   should use the first selection, and a confirmed reduction should briefly use
   the second selection before returning to the resting color.
3. Use a Dragon warhammer, Elder maul, or Bandos godsword special on that NPC.
   The tracker must update only after special energy falls and your own matching
   hitsplat appears. The number should change and briefly use the configured
   flash color.
4. Land an ordinary attack without using special energy. It must not change the
   infobox value.
5. Switch between two living NPC actors of the same type. Each must retain its
   own tracked value without sharing the other actor's drain.
6. Kill or despawn the drained NPC. The infobox must disappear automatically.
   A newly spawned NPC of the same type must begin at base Defence, with no
   drain carried over.
7. Leave a drained target alive for about 100 game ticks. The displayed value
   should recover by one level and the tooltip should identify estimated
   recovery.
8. Change `Target Defence display` through `Advanced HUD row`, `Infobox and HUD
   row`, and `Hidden`. Confirm each choice shows exactly the selected displays.
9. If available, test a known floor such as Nex. The value must stop at the
   floor instead of continuing to zero.

## Magic-only target infobox

1. In `Special attack rows`, leave `Target Magic display` on
   `Magic-only infobox`.
2. Reduce a supported target's Magic level or Magic Defence bonus using Eye of
   ayak, Accursed sceptre, or Seercull.
3. While using melee or ranged, confirm no Magic infobox is present.
4. Switch the current combat style to Magic against a supported target. A
   Magic-skill infobox should appear before any reduction and show the target's
   base Magic Defence bonus. Scurrius should begin at `10`.
5. Use Eye of ayak. The box should flash and change from Scurrius's `10` toward
   `0`. Seercull or Accursed sceptre should display Magic level after their
   Magic-level reduction lands.
6. Hover it. Confirm the tooltip names the displayed stat and shows its current
   and base values, plus whether recovery is estimated.
7. Switch back to melee or ranged. The Magic box should disappear without
   clearing the tracked reduction. Switching back to Magic should restore it.
8. Change `Magic number color` and `Magic flash color`, then land another
   relevant reduction and confirm both colors are used.
9. Kill or despawn the target. The Magic box must disappear automatically. A
   new instance should begin again at its base Magic Defence bonus.
10. If available, test Tekton. The first Dragon warhammer or Elder maul reducer
   special should use the encounter's guaranteed accuracy rule. A zero-damage
   attempt should still reduce current Defence by 5%. A zero-damage Bandos
   godsword attempt should reduce it by 10.
11. Confirm drains from other players are not added. Party synchronization is
   deliberately outside Wave 1A, Wave 1B, and Wave 1C.

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
4. Equip an unsupported special weapon. Combat Insight should keep the
   ordinary rows available without inventing special rows for that weapon.

## Useful bug report details

Record the weapon, ammunition, every worn item, boosted levels, attack style,
offensive prayer, target name, Slayer-task switch, Combat Insight max hit/hit
chance/DPS, and any Advanced-mode `Notice` text. A screenshot of the HUD and
equipment tab is ideal; no account or login details are needed.

For a Wave 1A or Wave 1B issue, also record special energy before and after
use, `Spec max`, `Spec chance`, `Avg spec dmg`, `On hit`, every visible target
stat, every hitsplat, and whether another player used a reducer on the same NPC.
