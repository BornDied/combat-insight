# Archive defence audit — October 30, 2026

Local candidate based on alpha4 commit377e1a954715de547e2c1aee155505f69a38eac4 plus audit1–audit5 work. Nothing committed or published. Scope: current sources, reduction floors, local attribution, phase/lifecycle handling, and cap presentation.

## Floors and presentation

Jagex's [launch hotfix](https://secure.runescape.com/m=news/the-fractured-archive---out-today?oldschool=1) explicitly distinguishes zero-floor targets from targets that retain60% of their base Defence. It also explicitly removes Ocellus P3 quadrant-by-quadrant Defence restoration. Current bundled inputs produce the following table; this is not a claim that every live base stat is independently verified.

| Target | ID | Bundled base Defence | Defence floor |
|---|---|---:|---:|
| Archive Guardian |16659/16660|260|156, before unresolved dynamic-stat interaction|
| Bork |16640|300|180|
| Smorks |16641|220|0|
| Ezekiel |16652|130|78; see source conflict below|
| Moia |16667|195|117|
| Dreadnoth |16665|125|0|
| Trio |16643–16648|100|0|
| Xarth |16684|170|102|
| Ocellus P1 |16674|215|129|
| Blade |16676|250|0|
| Ocellus P3/P4 |16677|250|150|

Current Plugin Hub Better Party Defence pin is [3d5848574bce1c1a8254c1ea887293b4292eb9b9](https://github.com/Falsary/better-party-defence/tree/3d5848574bce1c1a8254c1ea887293b4292eb9b9), superseding the older saved988f6277 reference. Its DefenceReadout displays current-minus-minimum by default. Combat Insight now offers that same presentation behaviour while retaining absolute levels for accuracy calculations. Zero is a local estimate of no remaining reducible Defence, not proof of the server stat or full-party history. A target already at an immutable floor does not acquire a fabricated successful-drain history.

## Attribution and lifecycle

The previous implementation consumed the next one/two owned hitsplats within five ticks and could bind an uncaptured special to any NPC. The revised tracker requires a captured actor and expected tick, collects a bounded tick batch, and handles complete Ralos pairs together. Missing/incomplete evidence marks the actor/phase unresolved rather than silently inventing a miss, drain or cap. Actual level/floor still exist internally; unresolved estimates are withheld from the displayed cap and target calculations.

The implementation follows primary [RuneLite SpecialCounterPlugin](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/specialcounter/SpecialCounterPlugin.java) and [SpecialWeapon](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/specialcounter/SpecialWeapon.java): Ralos first two hits on the expected tick; delayed ordinary specs first, instant specs last; Elder Maul uses HP-XP evidence. Live weapon/interaction reads are deferred because energy events precede actor updates. Distance affects Accursed/Seercull delays. The RuneLite BSD notice is retained in the source/resource and third-party notices.

This remains a heuristic. Multiple unrelated delayed projectiles on the same actor/tick cannot always be distinguished from a complete special using hitsplats alone. Ralos pairing is not cryptographic proof of provenance. HP-XP attribution can also be ambiguous if independent damage lands in the same client cycle. No teammate attribution or Party Hub import was added.

LOADING preserves identity-bound reduction state while cancelling unresolved pending evidence. It does not reconnect arbitrary replacement actors. Existing death/despawn removal can still discard state during an unmodelled transformation; a complete encounter/attempt-aware rebinding design remains open. A regression review identified uncertainty surviving a known Ocellus phase reset; the corrected tracker clears that old-phase uncertainty while preserving it within an unchanged phase. Pending old-phase hits cannot drain a new Ocellus phase.

## Arithmetic and BGS

Arclight demon reduction is corrected to floor(baseDefence*10%)+1. See the [Jagex Emberlight description](https://secure.runescape.com/m=news/while-guthix-sleeps?oldschool=1), current Wiki/Mod Ash references, and the regression cases. Existing nonstacking Accursed and Seercull semantics were corroborated; differing third-party implementations were not copied over them.

Fresh [Wiki DPS monster data](https://raw.githubusercontent.com/weirdgloop/osrs-dps-calc/main/cdn/json/monsters.json) gives Attack/Strength1 for Smorks16641 and Trio combat IDs16643/16645/16647. Dataset SHA256119b8ef1d5fe3bd6360928f77fbba42bf043f33cc5abf02757c97eaa9b8e961a, checked October30. The [calculator's defence-reduction model](https://github.com/weirdgloop/osrs-dps-calc/blob/main/src/lib/scaling/DefenceReduction.ts) chains NPC BGS damage through Defence, Strength, Attack, Magic, Ranged and stops at a nonzero floor. The implementation now retains the relevant1-point stats for those four verified IDs, including own Arclight/Emberlight changes and estimated recovery, so overflow can reduce Magic. Formula/data were reimplemented; no GPL calculator source was copied.

This narrow model does not establish that every NPC has zero Prayer or that every alternate form shares these offensive stats. Other IDs retain the previous Defence-only BGS model. Dreadnoth's nonzero offensive Strength floor should prevent BGS reaching Magic, but its exact odd-number50% rounding is not implemented. Generic BGS overflow and other weapons' secondary offensive effects remain incomplete.

## Open source/mechanic issues

- [October28 Jagex](https://secure.runescape.com/m=news/halloween-event-2026-tfa-tweaks--more?oldschool=1) adds a50% **offensive-stat** cap to Ocellus, Xarth and Dreadnoth. It does not enumerate affected skills or rounding. No invented Magic floor was added. Combat Insight does not model incoming boss DPS/Attack/Strength and does not claim complete offensive-stat-cap simulation.
- Current [Ezekiel Wiki revision15386021](https://oldschool.runescape.wiki/w/Ezekiel?oldid=15386021), timestamp October30 04:09:50UTC, changed Defence to200/floor120. Previous revision15385495, the current Strategies table and freshly fetched Wiki DPS dataset still say130/78. No silent replacement was made. Ezekiel's bundled numbers remain an unresolved source conflict and should not be represented as verified before release.
- Current [Archive Strategies](https://oldschool.runescape.wiki/w/The_Fractured_Archive/Strategies?action=raw) describes Guardian grounded-damage stat recalculation; fixed260/250 profiles and the existing observed-Magic-drain-only guard do not fully cover it. Exact timing, rounding, grounded-phase HP and floor integration are unresolved. This candidate does not fix or certify Guardian live stats after that mechanic.
- Fixed100-tick natural recovery is an estimate, not proven for every Archive phase; timer alignment and special exceptions remain open.
- P4 Ocellus50% damage mitigation during explosions and conditional shortened cooldowns still lack reliable live state integration. Audit4's earlier-P3 trigger correction is retained; true P4 damage/timing stays withheld.
- Active Guardian add ranged defence, unknown early HP/ruby bolts, Venator bounce DPS and generic actor replacement still require work. Ordinary max hit is now independent of missing add ranged defence; this does not supply missing accuracy data.

## Verification

All main/test Java sources compile with release11 using cached RuneLite1.13.3 API/client and the previously omitted HTTP API runtime dependency. Full JUnit366 passed, Python12 passed, git diff --check clean. Red-first regressions cover cap readout versus actual stats, null/wrong timing, Ralos incomplete/ordered batches, BGS overflow, Arclight, and uncertainty across known phase resets. A partial independent review found the phase-uncertainty issue; final fixes were reviewed and verified locally. No complete independent review approval, Windows Gradle build or in-game correctness certification is claimed.
