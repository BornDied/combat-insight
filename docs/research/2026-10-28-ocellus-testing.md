# Ocellus local audit2 testing

Historical audit2 instructions below. The audit3 recorder covers other Archive encounters as well; use [TESTING-AUDIT3.md](../../TESTING-AUDIT3.md) for its control label, bounds and remaining limitations.

Based on alpha4 release branch 377e1a954715de547e2c1aee155505f69a38eac4. Unpublished local source candidate. No commits/pushes or Plugin Hub changes.

## Confirmed fixes

- Unverified final-phase damage/timing no longer discards independently available basic-attack accuracy. Max/DPS remain explicitly unavailable; protection/immunity still override. Notices now includes the final-phase reason.
- Optional Inspector setting **Record Ocellus encounter** (default off). Captures changed calculation snapshots and raw animation, pose, overhead archive/sprite signals each game tick on Ocellus/Blade; logs locally owned hitsplats, observed actor deaths/despawns. It continues across IDs 16674,16676,16677 and does not terminate on temporary disappearance. Independent of Inspector visibility/freeze. No account names, chat, coordinates or network output added.
- **Copy encounter report** exports live recording history, not the frozen Inspector snapshot. In-memory limit: 1200 entries or 2 million characters, with explicit dropped-entry count. No disk persistence. Start before pull, stop after the encounter using the setting, then copy. Stop retains data; restarting clears it. Multiple attempts can be present if you leave recording on. Kill/wipe/phase boundaries are NOT automatically certified; neither are teammate contributions.
- Repeated-drain floor tests cover seven resolved Archive profiles and uncapped Dreadnoth. Existing tracker arithmetic otherwise unchanged pending the rule/lifecycle evidence in the separate defence audit.

## Important correction to phase investigation

RuneLite animation names containing PHASE3 do not prove an early P4 detector: the actor and internal animation family span the enrage section. Current Wiki Ocellus page says the enrage section uses deflect forms, a 50% reduction during void explosions, and a conditional halved attack cooldown after dodging. The report's final-phase flag alone cannot identify every active modifier. The existing final-phase detector is retained, not claimed fixed. A mid-P3 false positive remains unconfirmed until a timeline shows the triggering raw cue.

Sources inspected October 28: https://oldschool.runescape.wiki/w/Ocellus and https://raw.githubusercontent.com/runelite/runelite/master/runelite-api/src/main/java/net/runelite/api/gameval/AnimationID.java . No third-party code copied.

## Test steps

1. Open this source in a separate project. Run Windows Gradle test/check/build before gameplay. Confirm diagnostic version 0.9.0-alpha4-audit2.
2. Settings -> Inspector -> Record Ocellus encounter ON before pulling. Blowpipe dart must match loaded darts. Leave HUD modes unchanged.
3. Play the full encounter normally. Recording does not require the Inspector open. If you die/wipe, stop and copy before starting another attempt if you want separate reports.
4. After kill/wipe, set recording OFF, open Inspector and choose Copy encounter report. Paste/export the text. Recording is preserved across actor transitions; no claim that actor death alone proves a wipe/kill.
5. Check early-phase normal output; later-phase unknown damage should preserve supported hit chance, with explanation in Notices. Capture exact first occurrence of '?' and what was happening in the fight.

## Verification

All sources compiled with Java 11 target against cached RuneLite jars. Full attempt: 340 tests, 16 runtime failures due to missing net.runelite.http.api.item.ItemStats (same dependency problem as baseline); not a successful full build. Focused checks: recorder 2, Inspector panel 2, observed state 7, cap audit 2, target effects 13 passed. Python 12 passed. Whitespace check clean. No in-game verification.

## Still open

Ocellus complete P4 damage/cooldown model and precise boundaries; new offensive-stat cap skill/rounding semantics; party synchronization and robust Ralos attribution; transient actor-state persistence; BGS/Accursed/Seercull rule discrepancies; other raid gaps listed in defence/items audit. These were not silently patched with guesses. This source is for local investigation, not release-ready certification.

## October 29 statue diagnostic follow-up

User's alpha4 snapshot retained destroyed Inert Guardian 16663 with no active actor evidence (combat=-1,size=0). Saved cache roster lists 16662 attackable and 16663 non-attackable. Reproduced tracker overwriting retained16662 with16663; fixed this known transition and delayed hitsplat reselection. Six target-tracker tests pass, including existing ordinary boss ID transitions. This does not invent ranged defence for active16662: its ranged hit chance/DPS remain unavailable pending source data. The audit2 source ZIP includes this fix together with the prior audit1 recorder and reporting changes. Open it in a separate folder rather than overwriting an unknown local checkout. The old audit1 ZIP is unchanged.
