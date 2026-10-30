# Combat Insight 0.9.0-alpha4-audit3 — local candidate

This candidate includes audit2 plus a Bork state-resolution correction and a broader Archive test recorder. The audit2 download is unchanged. No commits, pushes or Plugin Hub changes were made.

## What changed

- An unreadable Smorks health bar no longer replaces the existing Bork animation fallback with unknown. A readable, unambiguous Smorks health bracket still takes precedence. This does not invent a bracket when both inputs are missing, retain a stun after it ends, or prove that animation names alone identify every live phase.
- The optional test setting is now labelled **Record raid diagnostics** in Calculation inspector. The stored config key is preserved for compatibility; it remains off by default. As requested, the temporary recorder setting is to be removed from normal configuration after this alpha testing round.
- Recording now includes observed Archive NPCs 16640–16688, including Bork, Smorks, Guardians and Xarth. Raw NPC animation, pose, health-bar ratio/scale, combat level, footprint, overhead sprites and death state are collected each game tick when changed, independently of target selection. It does not capture player names, chat, coordinates or credentials.
- Calculation snapshots, owned hitsplats and observed death/despawn events remain available. The report now contains the build version. Capacity is 12,000 entries or 8,000,000 characters with explicit oldest-entry removal counts; it is not unlimited or a certified complete fight recording. Capture is in memory only and there are no network or gameplay actions.

## Setup and focused testing

1. Extract the source into a separate folder and open its build.gradle project in IntelliJ. Run `.\gradlew.bat test check build`, investigate any failures, then `.\gradlew.bat run`. Confirm **0.9.0-alpha4-audit3** in Copy diagnostic.
2. Before a pull, enable **Record raid diagnostics**. Leave it enabled throughout the encounter. After the kill/wipe, disable it and use **Copy encounter report** in the Inspector. Stopping preserves history; enabling it again or restarting the client clears it. Inspector Freeze affects only the single calculation snapshot.
3. For Bork, include standing, stun, recovery and Smorks health changes in one recording. The room observations should include both NPC16640 and NPC16641 even while attacking only one of them or temporarily selecting neither. Do not automate gameplay. A recording can still contain unknown health-bar values when the client has not received a usable bar.
4. Existing audit2 Guardian transition and Ocellus tests remain relevant. For defence-drain testing use Track my reductions; Off intentionally uses base stats. Do not change a setting merely to make a number appear.

## Limits still present

- The user's Bork audit2 snapshot did not include raw animation or Smorks health-bar values. The reproduced overwrite bug is not proven to be the exact cause of that report. The new recording supplies that missing evidence.
- Active Inert Guardian16662 ranged defence remains unverified; the broad availability guard still hides its max hit as well. The destroyed16663 selection fix is retained. No ranged defensive values were invented.
- Echo/ordinary Venator bounce DPS is not implemented. Xarth's supplied Venator snapshot had max39, accuracy43.5% and an explicit multi-hit DPS block. That report does not establish a Xarth-stat defect.
- Ocellus final-phase damage/timing remains unverified. Supported accuracy is retained independently. No automatic encounter/phase boundary or teammate-hit certification is claimed.

## Verification

See docs/research/2026-10-30-audit3-verification.md for exact results. The cached Java harness is missing the RuneLite runtime class net.runelite.http.api.item.ItemStats in 16 existing tests. A full Windows Gradle build is still required; a focused passing test run is not a passing full build. No in-game verification was performed here.
