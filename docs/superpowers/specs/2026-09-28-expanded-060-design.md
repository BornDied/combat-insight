# Combat Insight 0.6.0 — expanded coverage and testing design

Status: written design approved September 28, including the optional held-details shortcut. Local development authorized; release remains separately gated.

## Goal and boundaries

Build one separately testable local update from released commit
`8cbdcf0d028c1c0b4146e798bb1f59890cfe2294`. Improve encounter accuracy,
special-attack coverage and diagnosis without increasing persistent HUD clutter.
Preserve existing mode-specific row toggles, saved settings, custom colours,
BSD 2-Clause licensing and `build=standard`.

No commits, pushes, public PRs, Plugin Hub changes or publication are authorized.
The old 0.5.0 project remains usable. Deliver a full-source local test ZIP,
a cumulative patch against this exact release, and a focused testing checklist.

## Observed baseline

- 30 special-attack weapon families are mapped by SpecialAttackWeapon.
- The general NPC snapshot is dated September 1; raid records September 25.
- The README reports 3,976 IDs and 30 ambiguous IDs. Recount during the audit.
- Shared DT2 encounter records are ambiguous; LiveCombatSnapshot withholds
  their target calculations. Presence tests do not establish functional support.
- RaidScaling consumes manual values; the plugin constructs it each update.
- RaidDamageRules intentionally withholds specific encounter damage estimates.
- Enchanted bolts and some ordinary multi-hit attacks are documented limitations.

## Architecture

Keep game observation separate from deterministic calculation. Introduce a
small encounter context containing boss mode, phase where known, resolved raid
inputs, and the source of each input (observed, manual or unknown).

Resolve an NPC's variant before raid scaling, then apply supported local drains
and encounter transforms in the evidence-backed order. Both basic and special
calculations and the defence tracker consume the same resolved target; they
must not independently choose different variants. Reset tracked state when the
encounter identity or scaling inputs change.

Use immutable calculation inputs and focused rules classes. Avoid further
embedding encounter-specific branches in the large LiveCombatSnapshot class.
Extract only boundaries needed by these features; unrelated refactoring is out
of scope. Retain unavailable results when required context is unresolved.

## A. DT2 normal, Awakened and quest variants

Cover Duke Sucellus, Vardorvis, the Leviathan and the Whisperer in normal and
Awakened encounters. Keep quest variants explicitly separate.

Retain multiple stat records when an NPC ID is reused. Resolve mode from
verified client-visible evidence when possible. Offer an Encounter inputs
setting with Auto, Normal, Awakened and Quest; manual selection is scoped to
DT2 and identified as an assumption. Auto never silently selects Normal.
Invalid or insufficient context produces a specific unavailable reason.

For each boss/mode, research and document base stats, HP-dependent stats,
defence floors and drain interactions, demon modifiers where applicable,
damage reductions or caps, phase-specific vulnerabilities and immunities.
Research determines exact formulas; this design does not assert their values.
Do not infer hidden phase state from guessed timings. HP-derived calculations
must disclose estimated HP and uncertainty near meaningful thresholds.

Acceptance: independent expected-value cases for all eight normal/Awakened
boss combinations, quest separation, unresolved mode, overrides and resets.
Include HP boundaries, floor rounding and relevant special interactions.

## B. NPC data audit

Fetch a fresh complete source snapshot and compare it with the release.
Report newly added IDs, removed IDs requiring investigation, changed stats,
new variants and unresolved ambiguities separately. Include modern bosses,
Slayer monsters, quest instances and transformations in the review.

Preserve the generator's incomplete-response protections. Do not replace the
snapshot after API errors or partial pagination. Keep source dates, named
variant provenance and a reproducible comparison report. A blocked live fetch
must be reported as blocked, not described as a completed current-data sweep.

Upgrade major-boss tests to distinguish record presence, variant resolution
and supported mechanics. Do not erase ambiguity merely to pass a test.

## C. Outstanding raid damage rules

Work through the existing guards: Verzik P1, Guardians, Tekton magic,
Vasa crystal magic, affected Olm part/off-style combinations, and Ice demon
non-fire attacks including Demonbane interactions. Preserve existing Nylo
style restrictions and Entry/Normal/Hard ToB and normal/CM CoX distinctions.

Replace each guard only when its transform and operation order have sourced
tests. Accuracy, per-hit damage and expected damage must agree. Apply caps or
reductions per hit where required rather than to a multi-hit total by default.
Unresolved cases remain explicitly unavailable and appear in the handoff.
This is attack calculation support, not a simulation of movement, healing or
the duration of an entire encounter.

## D. Additional special attacks

Add crystal halberd, dragon halberd, Osmumten's fang and scorching bow.
Cover accuracy rules, defence style, damage ranges, target-size effects,
hit dependence and applicable effects rather than just maximum damage.
Treat persistent effects separately from immediate damage; avoid counting
an effect in average damage unless its eligibility and duration are modelled.

Acceptance includes small/large targets, miss sequences, damage bounds,
rounding, relevant target immunities and regression cases for existing specs.
Multi-target totals are not inferred from a single selected target.

## E. Enchanted bolts and Zaryte crossbow

Build a shared bolt-effect calculation layer for supported ordinary attacks
and crossbow specials. Audit the PvM-applicable enchanted bolt families and
explicitly list supported and deferred effects; do not claim every bolt effect
is covered just because its item is recognized.

Research activation probabilities, eligibility, diary/equipment modifiers,
HP-dependent damage, caps, immunities and Zaryte-specific behaviour. Detect
selected compatible ammo through the existing ammunition/quiver rules.

Represent normal-hit and proc outcomes separately and combine them with the
correct conditional probabilities. Avoid double-counting accuracy or proc
damage. Keep ordinary max hit distinct from conditional proc maximum, with
extra explanations in details rather than permanent HUD rows. Unknown target
HP or an unsupported effect must not yield an apparently exact DPS estimate.

Acceptance includes deterministic proc/miss branches, conditional probability
sums, ruby/diamond cases, relevant equipment modifiers, target HP boundaries,
ammo/quiver switching and the Zaryte special.

## F. Copy calculation details

Provide a user-triggered overlay context-menu action, Copy calculation details.
Capture a consistent current calculation snapshot including plugin version,
target ID and variant, equipment IDs, ammo, levels/boosts/prayers, attack style,
manual inputs, input provenance, supported drains, results and limitations.
Exclude player/account names, chat, credentials and unrelated client state.

Copy to the local clipboard only, without uploading or automatically sending.
Make unknown values explicit. Show a brief success/failure notification; no
permanent HUD row or new always-open panel. Unit-test serialization and verify
clipboard integration on the user's Windows test build.

## G. Automatic raid inputs

Audit public RuneLite API events/values for each existing raid input. Only
populate inputs whose meaning and lifetime can be verified. Do not assume
all party member skills are exposed. Use explicit per-input manual overrides;
when Auto lacks evidence, display the need for an input or a labelled fallback.

Never use the count of currently visible players as starting party size.
Retain starting participants through deaths and room transitions where the
game's scaling requires it. Clear observed context on logout, world change and
raid exit. Reset drains on material scaling changes. No inter-client syncing
or unsupported teammate drain inference is included.

## H. Optional hold-to-show details

Include a disabled-by-default configurable keybind that temporarily displays
the user's Advanced rows, returning to the selected mode on release. Preserve
all saved mode toggles. Do not override combat visibility, alter saved mode,
or make the overlay permanently larger. Clear held state on focus loss,
logout and plugin shutdown; avoid intercepting ordinary typing when unbound.

## Evidence and validation

Before coding a mechanic, record source URL, retrieval date, formula and
known exceptions. Prefer Jagex update notes, OSRS Wiki mechanics and public
RuneLite APIs; use independently maintained calculators as cross-checks,
respecting their licences. Conflicts remain explicit until resolved.

Expected-value tests must come from documented rules or independent worked
examples, not copies of the new implementation. Existing tests remain gates.
Run Java tests, generator tests, a standard Gradle build and actual RuneLite
overlay render checks. Record environmental blocks precisely. Follow with
in-game tests on Windows before any release decision.

## Delivery sequence and review

1. Current source research, data audit and variant resolution design details.
2. DT2 calculations and functional boss tests.
3. Outstanding raid transforms and raid input detection.
4. Halberd/fang/scorching specials.
5. Bolt outcomes and Zaryte integration.
6. Copy details and optional held-details keybind.
7. Combined regressions, local packaging and in-game checklist.

Each batch has independent verification; all feed one local test build.
Research failures or missing observable state narrow claims and are reported
before delivery. They do not justify fabricated stats or silent defaults.

The next stage is a file-level implementation plan after review of this design.
No product code has changed at the design stage.
