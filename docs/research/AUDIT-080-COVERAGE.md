# 0.8.0 bounded coverage audit

Reviewed 17 October 2026. This is a source review, not an assertion of complete current-game coverage. No broad mechanical changes were made by this audit.

## Evidence boundary

The local Weird Gloop OSRS DPS calculator checkout is commit `89c3e25b344aea90d0189746e4b5f73dde0f0383` (2 September 2026). It is primary evidence for that project's implemented formulas, not a guarantee of server behavior after that date. The bundled monster table identifies its base snapshot as 1 September, with CoX/ToB refresh on 25 September. Jagex's [22 July gear update](https://secure.runescape.com/m=news/summer-sweep-up-gear--pvm-changes?oldschool=1) was retrieved to verify Soulreaper's five stacks, +12% special accuracy per stack and +6% special minimum per stack. Direct Wiki retrieval was unavailable in this session.

## Findings

| Area | Observed implementation | Coverage or limitation |
| --- | --- | --- |
| Soulreaper basic | `SoulreaperRules` adds floor(boosted Strength × stacks × 6 / 100) after prayer, before stance/Void. Both normal 28338 and ornamental 33335 map to the same weapon category. | Matches local reference's basic-only additive Strength treatment; existing tests cover both names, 0–5 stacks and invalid values. |
| Soulreaper special | New `SoulreaperSpecial` scales attack roll by 1 + .12s; minimum and maximum independently use the unstacked maximum: floor(M × .06s), floor(M × (1 + .06s)). Zero stacks unavailable. | Formula order follows local `PlayerVsNPCCalc.ts`, not an inference that minimum is 30% of the already boosted special maximum. Live caller must exclude the basic stack Strength bonus. |
| Seeking arrows | `AmmunitionRules` selects compatible active ammunition and supplies accurate floor 3; shared hit transforms apply that floor. `EquipmentStatsFallback` covers nine new arrow IDs when RuneLite lacks stats. | Local reference likewise raises accurate rolls to 3, not a uniform reroll from 3. Misses remain misses. Compatibility remains broad name-based bow/arrow matching; individual bow arrow-tier limits are not enforced (e.g. a shortbow with dragon arrows can be treated as usable). This is a concrete pre-existing input-validity gap. |
| Elder-derived necklace | Local reference item 33639 is **Necklace of rupture**, with +20 Ranged accuracy, +8 Ranged Strength and +3 Prayer. Fallback supplies exactly those values when RuneLite has no entry. | No independent item called "elder necklace" was established. No additional proc exists in the inspected reference calculation code. Current RuneLite values win; do not add fallback stats twice. |
| Boosted levels / overload | Snapshot reads `Client.getBoostedSkillLevel` for Attack, Strength, Ranged and Magic before applying prayer/stance/equipment rules. | Active potion/overload levels are already observed; an additional potion multiplier would double count. This review did not simulate server-side overload refresh, drains or client event timing. |
| CoX | Runtime party/mode/highest-combat observations; highest HP and average Mining remain manual. Formula order agrees with inspected upstream scaling paths, including distinct linked Magic and Olm hands. | Partial automation is explicit. Manual team HP/Mining are still necessary and can produce wrong target stats if entered incorrectly. No claim of fully automatic raid context. |
| ToB | HP-only scaling, normal minimum effective party of 3, Entry Mode fractions 10/19/27/34/40 over 40. Largest observed roster is retained. | Matches inspected reference formulas. An attach/relog after members are already absent cannot reconstruct the original roster; code labels it observed roster, verify starting size. |
| ToA | Invocation changes defence rolls. `RaidScaling.apply` has no ToA HP path, while Snapshot uses profile HP for health-ratio inversion. | **Concrete gap:** reference `scaling/TombsOfAmascut.ts` changes HP with invocation, path level and party size, including rounding; local UI has no ToA path/party inputs. Consequently ToA HP estimates and HP-dependent ruby-bolt values cannot be called verified. Basic non-HP-dependent attack/defence roll support is distinct from this gap. |
| New NPC coverage | IDs for Blood-starved venator, Venator variants, Ur-maggot larvae, Maggot King and Doom of Mokhaiotl exist in bundled data. | Maggot King 15742 and Doom 14707 have ambiguous=1, since their reference records vary by phase/delve. Presence of an ID does not mean those encounters are resolved or fully supported. They should continue to fail closed until phase/delve evidence is modeled. |

## Inspector lifecycle / threading review

Inspected `InspectorDocument`, `InspectorSession`, `CalculationInspectorPanel` and their plugin integration. No blocking thread-safety defect found in the reviewed source:

- Plugin publishes a volatile immutable `LiveCombatSnapshot`; document capture receives one snapshot reference and copies text, without querying the RuneLite client from Swing.
- Document clones its section array. Snapshot warnings are defensively copied and unmodifiable.
- Panel construction, session updates, freeze/resume, timer callbacks and cleanup occur on Swing EDT. Session frozen state is isolated from live combat calculation.
- Navigation and timer are removed/stopped on disabling or shutdown; queued callback checks the current panel reference. Volatile running/enabled flags protect pending setup callbacks.
- Clipboard operations run on EDT; chat feedback is dispatched back through the client thread.

This is a source review, not a live client lifecycle stress test. Freeze intentionally retains an old document while new snapshots continue to arrive. The diagnostic copy contains the captured report rather than live client data.

## Verification evidence

Soulreaper's six new tests were observed failing before implementation, then passing. Focused basic + special tests pass (9 tests). One full concurrent suite run passed 216 tests; a later run was temporarily blocked by in-progress HUD test compilation, so final whole-project verification belongs after all agents finish. No commit or push was performed.
