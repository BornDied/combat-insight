# Combat Insight 0.9.1-local — unfinished test candidate

Baseline: upstream main 161a3ea, release 0.9.0-alpha4-audit5. This is a separate checkout. No commit, push, merge or Plugin Hub change has been made.

## Verified locally

- November 11 values: Guardian flying light ranged defence 55, grounded magic defence 50 and fire weakness 80%; Xarth stab defence 60 and base HP 2450; Bork and Smorks base HP 1650 and 650; sword air weakness 125%. Trio zero Defence floor already existed and is covered by the suite. Source: https://store.steampowered.com/news/posts/?appgroupname=Amazing+Adventures+The+Lost+Tomb&enddate=1794394190&feed=steam_community_announcements (also see https://whatsnew.fyi/product/old-school-runescape/releases/2026-11-11-quest-month-week-two-the-fractured-archive ).
- Jagex announced solo/duo HP increases of 10%, but the announcement does not specify exact rounding or the new full party tables. Old solo/duo HP table entries are withheld; all Bork and Smorks table entries are withheld because their base HP changed. Other retained cells are pre-update published values and need independent verification. Do not treat the base HP database entries as verified scaled HP. See https://store.steampowered.com/news/posts/?appgroupname=Valve&enddate=1794394012&feed=steam_community_announcements .
- Automated ordinary spell snapshots cover all four elements on sword, Ezekiel, grounded Guardian, Xarth corpse 16687, and the three primary Dagon'hai actors. These tests cover calculations, not actual spell casts or counterspells.
- Recorder controls and synthetic overflow test: first and latest actor observations or selected-target snapshots for each observed room survive timeline eviction. The timeline retains at most 12,000 entries / 8 million characters and reports dropped entries. Game-state transitions flag potential gaps, and discontinuous room-observation ticks are marked. Actor signals, owned hitsplats and local player animations are recorded, but an animation is not certified as an attack.
- `gradlew.bat test check build` passes locally. The normal jar is in build/libs. This is not an in-game validation.

## Important outstanding work

This is not a completed Raid Inspector. The recorder does not reliably mark retries or all gaps, certify phase boundaries, identify crystal targets, or provide per-attack Scythe grouping and totals. Rooms can now be retained even without a selected target. The timeline may still lose early details beyond the first/latest observations. Equipment and other inputs depend on changes in the existing diagnostic snapshot and may miss transient events. It does not provide a confirmed attack timeline or automatically associate hits with attacks. Do not use it to assert observed DPS.

The November 11 Dagon'hai counterspell guaranteed-max and 33% imp buff are not implemented: reliable live state detection and rounding need investigation. Existing ordinary Trio calculations do not represent a successful counterspell. The pre-existing imp uncertainty guard is not proof that every buff is detected. Trio floor behavior requires in-game confirmation.

Bork standing, stun and recovery Scythe individual hits and totals, with Smorks HP bracket, require a recorded in-game run and analysis. Ocellus P4 target identity during crystals/reduced damage, damage and attack timing remain unverified; P4 estimates must remain unavailable. Ezekiel enrage and Echo Venator bounce DPS remain on the verification list. No actual full raid or file-sharing test was performed.

## Using the candidate

Open this separate Gradle project and launch `gradlew.bat run` in a properly configured RuneLite development environment. In Calculation Inspector select Start Raid Recording before Guardian, then Stop Recording and Export Recording after Ocellus. Starting a new recording after stopping clears the previous in-memory record; export first. Export opens a file chooser and writes a local text file that can be attached directly. Review the file for information you do not want to share. Plugin shutdown stops recording, and a client restart loses the in-memory record.

Before relying on this candidate, finish the missing mechanics and recording features, run the full automated suite, then record and inspect real raid evidence. Avoid replacing a known working client build.
