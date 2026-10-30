# Ocellus diagnostics implementation plan

Goal: retain independently supported accuracy when final-phase damage is unknown; record an opt-in bounded Ocellus timeline across actor changes.
Architecture: reuse immutable snapshot diagnostics and client event callbacks. Keep recorder independent of Inspector freeze and UI visibility. No network or disk recording; explicit clipboard export only.
Constraints: no commits/pushes; no game interaction; no chat/account/location capture; no invented P4 damage coefficient or dodge cooldown.

- [ ] Regression for final-phase accuracy retained and warning included; damage remains unavailable. Preserve protection/immunity overrides.
- [ ] Add bounded synchronized recorder (1200 entries / 2 million characters), opt-in setting default false, copy action in Inspector. Record changed snapshots and locally owned hitsplats. Preserve across target/actor/phase changes; explicit recording stop retains report, restarting clears. Do not infer kill/wipe from transient despawn. Mark complete-fight boundaries unverified and truncation explicit.
- [ ] Include raw NPC animation/pose/overheads with tick in recording; retain existing phase guard until live timeline identifies a wrong cue.
- [ ] Run focused/full Java and Python tests; document source findings and unresolved raid mechanics.
