# NPC audit — September 28, 2026

Released snapshot: 3,976 IDs; 30 ambiguous IDs (general data September 1,
raid refresh September 25). Complete Wiki API fetch was attempted and returned
HTTP 403; it did not replace the snapshot. Therefore a full current-data
sweep is BLOCKED, not complete.

Cross-check: Weird Gloop's Wiki-derived monster JSON at commit 89c3e25b,
retrieved September 28. Every real reference NPC ID is already present.
The two absent reference rows use synthetic ID 100001 for Yama's Glyphic
Attenuation calculator scenario and must not be added as a live NPC ID.
This reference stores representative IDs, so this check cannot establish
complete phase/alternate ID coverage or that every statistic is current.

DT2 variant records are resolved explicitly in this update rather than
removing ambiguous flags from the general snapshot. The bundled snapshot
remains dated honestly. Run the generator and compare_monsters.py against a
complete fresh Wiki response in an environment with API access before claiming
an exhaustive sweep.
