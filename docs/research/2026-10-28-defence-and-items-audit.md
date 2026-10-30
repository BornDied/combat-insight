# October 28 defence and item audit

## Reviewed baseline and scope

Read-only source review plus new regression tests on local branch `local/audit-2026-10-28`, based on `release/archive-alpha4` commit `377e1a954715de547e2c1aee155505f69a38eac4`. GitHub default main resolved to 0.8.0 `a0f2e060973d1e00a835bcd2afa1e95cfa298bca` when inspected. This does not establish which commit the user's latest merge or Plugin Hub currently runs. Compare the in-client diagnostic version/commit before attributing live behavior to this candidate.

No production code changed, no commits, pushes, merges or Plugin Hub writes. Earlier alpha2 worktree preserved. This is a bounded audit, not certification of all loadouts and encounters.

## Today's authoritative changes

Source: https://secure.runescape.com/m=news/halloween-event-2026-tfa-tweaks--more?oldschool=1 (October 28).

- New 50% OFFENSIVE-stat reduction cap for Ocellus, Xarth and Dreadnoth. It is not a new 50% Defence floor. The exact affected skill list and rounding need corroboration before introducing Magic-floor arithmetic. The current model stores Defence, Magic level and Magic-defence bonus, not Attack/Strength/Ranged, so it cannot claim full offensive-drain tracking.
- Existing Archive Defence floors remain 60% of starting Defence for Guardian, Bork, Ezekiel, Moia, Xarth and Ocellus. Dreadnoth still has no Defence floor. Current rules and focused repeated-drain tests agree.
- Ocellus retains reductions between P3 quadrants. Distinguish same-phase movement from a genuine phase transition or new attempt.
- Rondache incoming flat armour/Magic resistance now applies AFTER other mitigation, including protection prayers. Current charge preview reads an observed charge varbit and does not simulate incoming damage, so do not alter its outgoing base maximum to compensate. Any future incoming-damage/charge forecast must use the new order.
- Ocellus P2 Blade diminishing returns are more forgiving in larger scales. Alpha4 does not model the later Blade release as direct player attack DPS; keep that boundary. Any future stored-damage forecast needs the new curve.
- Obligator model/icon change, shard buy limit, bolt crafting inventory behavior, Folio teleport and Zeal skill-guide placement do not establish changes to outgoing damage formulas. No guessed coefficient changes warranted.

## Findings and priorities

### High: tracking lifecycle is tied to NPC actor lifetime

`CombatInsightPlugin.onGameTick`, death handler and despawn handler remove special-effect state; `TargetEffectTracker` keys it by NPC object identity. `TargetEffectState.rebase` handles same-actor ID changes (and Ocellus phase resets), but cannot preserve state after removal/replacement. Same-actor P3 quadrant movement is retained; a replaced actor or temporary zero-HP phase can lose drains. Need encounter identity with explicit phase/new-attempt reset rules, tested against live events. Do not simply preserve every Ocellus phase or every dead actor indefinitely.

### High: Ralos hitsplat attribution remains heuristic

Special energy decrease binds a five-tick window and the next two owned hitsplats to the target. `isMine()` excludes another player's ordinary damage, but alone does not identify the weapon/projectile or exclude all local secondary damage. Extra/missing/delayed hits can consume the pending count incorrectly. An expired/incomplete pair currently has no explicit uncertainty marker. Add event-order fixtures for two hits, one miss, both misses, interleaved secondary hits, incomplete pairs and target switches. Validate upstream SpecialCounter ordering before relying on it.

### High: Magic/secondary drains need independent rule review

Combat Insight's BGS only drains Defence; it does not spill remaining drain into other skills after Defence reaches zero. Accursed sceptre clamps to a base-derived 15% limit. Seercull applies only while Magic equals base. Better Party Defence differs on all three. These are concrete implementation differences, not proof the reference is always correct. Corroborate current game/Wiki rules, then test order dependence, stacking, capped-stat spill stopping, recovery and effect duration before altering production arithmetic. Magic changes also affect magic defence and Ralos/Tbow inputs.

### High: new offensive cap is not represented fully

Do not label Emberlight's displayed Defence drain as a complete summary of all affected stats. Current state does not model its offensive reductions. Once affected skills and rounding are established, share explicit floor rules between tracking and previews; keep skill level and defensive bonus distinct. Do not apply the cap globally to all Archive NPCs or all skills.

### Medium: local estimate is not team truth

Tracking is LOCAL or OFF. No party drain synchronization exists. Better Party Defence has party event/history synchronization and deduplication. A future opt-in integration should validate encounter/phase/attempt and deduplicate event identifiers; never sum both a local event and its network echo. Avoid scraping another plugin's UI or depending on its internals. Existing manual/off behavior must stay usable with an external tracker.

### Medium: recovery and preview parity

Generic recovery is one level per 100 ticks with estimated markers. This audit does not independently establish every boss's regeneration interval or phase restoration. Add encounter-specific evidence before changing it. Preview drain calculations and actual tracker effects should consume the same verified rule definitions; currently their formulas can diverge.

## Better Party Defence comparison

Resolved from RuneLite Hub descriptor: https://github.com/runelite/plugin-hub/blob/master/plugins/better-party-defence

Repository https://github.com/Falsary/better-party-defence, Hub-pinned revision `988f62772895a5a1486415e856878827e0eb4410`.

Reviewed BossDefence, BossMechanics, DefenceTracker, LocalDefenceSpecDetector, RalosHitResolver, TfaBossDefenceTest and README. Useful concepts: separate encounter lifecycle from actor lifecycle, explicit phase persistence, distinguish current-based versus starting-based drains, capped BGS spill, conservative TFA Magic visibility, and event-history deduplication. Its README also disables TFA Magic tracking pending confirmation; it is not evidence that every Archive interaction is solved. Its Ralos resolver explicitly admits incomplete pairs and limits to arrival-order attribution. No third-party implementation copied or runtime dependency added.

## Verification

- Baseline Java sources compiled with Java 11 target against cached RuneLite 1.13.3 jars. Full JUnit attempt: 336 tests, 16 failures, all reported missing `net.runelite.http.api.item.ItemStats` at ItemManager construction. This environment lacks the matching runtime dependency; this is NOT a passing full build and does not supersede the prior Windows alpha4 build record.
- New ArchiveCapAuditTest: 2 tests passed. Repeated DWH/maul/BGS/Ralos/Emberlight stop at the configured floors on seven resolved Archive profiles; Dreadnoth can reach zero Defence. Bork's dynamic/ambiguous profile is not certified by these tests.
- Python data tools: 12 tests passed.
- No in-game verification performed.

## Next local test priorities

1. Record P3 Ocellus drains across quadrants, actual NPC replacement/despawn and temporary zero HP; verify true new attempt resets.
2. Ralos with 0/1/2 successful glaives, thrall/secondary damage, target switches and long travel distance. Compare observed local estimates with Better Party Defence at the same starting stats.
3. BGS at zero/capped Defence; Accursed before/after DWH; repeated Seercull; identify changes in Magic and Ralos drain amounts.
4. Verify offensive cap on the specifically affected bosses without conflating it with Defence floors. Gather skill-specific authoritative evidence before a numerical fix.
5. Regression: Zeal/Piety switching; Tome stacks/target changes; Ascension bolts/diary; Obligator styles; Rondache observed charges. Retain prior CoX/CM, ToB variants, ToA HP caveats, DT2 modes, PNM pillar and Zulrah cap tests.

Release gate: confirm the user's actual latest merged commit, obtain matching Gradle/runtime dependencies, pass full checks and targeted live tests. This review does not recommend publishing yet.
