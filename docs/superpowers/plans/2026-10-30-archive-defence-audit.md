# Archive defence tracking follow-up

User scope: research raid-wide issues, compare current Better Party Defence, implement verified local fixes and show zero when no more Defence can be drained. No commits, pushes or live publication.

Design: actual Defence remains the input to combat calculations. A presentation option shows current-minus-floor and base-minus-floor, with zero meaning no remaining drain; tooltip and Inspector disclose actual level and floor. Preserve the Off/local policy and clearly identify local estimates. Current Better Party Defence Hub pin is3d5848574bce1c1a8254c1ea887293b4292eb9b9. Its zero semantics are a reference behaviour, not evidence of server state. Primary RuneLite Special Attack Counter establishes ordered hitsplat timing; record only captured targets and expected ticks, resolving Ralos pairs together. Missing evidence must not create a false cap. Keep existing correct nonstacking Accursed/Seercull rules. Apply only independently corroborated data/formula corrections. The Oct28 offensive-stat cap is distinct from the60%-of-base Defence floors; do not invent an unverified Magic floor from its wording.

Implementation/testing:

- [x] Establish full Java baseline with the missing cached HTTP API dependency supplied in the verification harness only.
- [x] Regression tests for capped presentation versus absolute calculation values, unknown targets and nonzero floors; implement snapshot/UI/Inspector integration.
- [x] Reproduce wrong/null target, timing, Ralos partial pair and thrall ordering failures; replace loose five-tick consumption with bounded exact-tick batches. Read live equipment/interaction after client updates. Document same-tick projectile attribution limits.
- [x] Verify and correct formula/cap/lifecycle issues supported by current sources; keep unknown mechanics explicit rather than fabricate them.
- [x] Full tests, Python data checks, diff review and source ZIP integrity. Add source/licence references for any adapted upstream implementation. Preserve audit4 download. Deliver one combined local candidate with a remaining-issues checklist.

Outcome: audit5 implements the verified scoped corrections and records unresolved mechanics in docs/research/2026-10-30-archive-defence-audit.md. No all-raid correctness claim. Full cached Java366 passed; Python12 passed. Windows Gradle and in-game checks pending.
