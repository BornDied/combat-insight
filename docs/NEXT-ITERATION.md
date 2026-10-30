# Next iteration: Ezekiel, Dagon'hai and spell coverage

Agreed scope on October30, after submitting the audit5 candidate for review. This is future work, not implemented support or a claim that the current release fixes every raid mechanic.

- Resolve Ezekiel's newly conflicting Defence data (dedicated Wiki200/floor120 versus current Wiki DPS/Strategies130/floor78), phase/enrage transitions, animated swords and defence tracking across actor changes.
- Audit all Dagon'hai Elder/Monk forms, immunities, phase changes, imp enhancements and per-actor reductions; retain separate state for distinct monks.
- Build a weapon/spell matrix for air, earth, fire and water spells across the raid, including elemental weaknesses, unlocked elemental upgrades, powered staves, saved/manual spell interactions and relevant special attacks. Different spellbooks, prayer changes and weapon swaps must not leak inputs between styles.
- Extend passive diagnostics when a missing signal prevents a reliable conclusion. Reproduce failures from recorded timelines before changing formulas; keep unknown damage, scaling and timing separate from verified accuracy inputs.
- Carry forward Guardian dynamic stats, exact actor-replacement persistence, offensive-stat-cap scope/rounding, remaining BGS forms and Ocellus P4 damage/timing. Venator bounce DPS remains a distinct open feature.

Prepare and test this work locally before its own release approval. Preserve the compact HUD and existing Minimal/Standard/Advanced row groups.
