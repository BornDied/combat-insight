# Bork and Archive diagnostic follow-up

Goal: fix a reproducible loss of Bork enrage evidence and make the existing opt-in test recorder useful for the other Archive encounters. Preserve the audit2 download and prepare a separate local candidate. No commits, pushes or publication.

Design: preserve a directly observed Bork enrage quartile when an unreadable Smorks bar is encountered. Do not invent a quartile when neither signal exists or retain a transient stun indefinitely. Extend the bounded in-memory recorder to all Archive NPCs, including room observations without a selected target. Keep the temporary opt-in config control for this alpha testing round, label it accurately, and remove it from normal configuration after testing as requested. No gameplay actions, networking or credential collection.

- [x] Inspect diagnostic, state resolution and recorder call sites; baseline state tests pass (7).
- [x] Reproduce the enrage/unknown-bar overwrite with a failing test, then fix it without changing damage constants.
- [x] Add regression coverage for non-Ocellus recording and raw room observations independent of target selection; implement bounded, deduplicated history.
- [x] Verify relevant Java tests, attempt the full suite, run Python data checks and inspect the diff.
- [x] Package audit3 separately with exact limitations and testing instructions. Audit2 remains unchanged.

Known limits: the supplied Bork snapshot lacks raw animation and Smorks bar data, so the reproduced bug is not proven to explain that exact capture. Venator bounce DPS and Guardian ranged defence remain unresolved. Recording cannot certify a complete fight and reports truncation honestly.
