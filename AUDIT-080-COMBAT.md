# Combat calculation audit — local0.8 candidate

Scope: targeted fixes and regression verification, not certification of every encounter or loadout. No live game session was available.

## Changes

- Zulrah IDs 2042/2043/2044 now apply the per-hitsplat rule: raw damage above 50 is replaced by an integer uniformly distributed over 45–50. Damage up to 50 is unchanged. The original roll range is retained when calculating expectation. For an accurate 0–80 roll (accurate zero raised to one), the expectation is `(1 + 1275 + 30 × 47.5) / 81`, not the mean of 0–50.
- The transform reaches basic attacks, supported multi-hit basic attacks, supported special attack distributions, and ruby/diamond/onyx/dragonstone bolt mixtures including Zaryte. Each special hitsplat is transformed before totals are added. Existing identity-transform overloads preserve callers without encounter context.
- Soulreaper special integration consumes the observed Soul Stack varp and an unstacked melee-derived maximum; the passive Strength bonus is not counted a second time. Selected crush defence is retained. The generic special calculator rejects Soulreaper without its required context. Formulas and enum are documented/tested separately by the Soulreaper audit.
- ToA HP is explicitly unavailable because party/path/invocation health scaling is not fully resolved. Ruby basic DPS and Zaryte ruby special damage are withheld with `ToA HP scaling unavailable`; accuracy remains independently available for basics. This prevents plausible but wrong ruby values computed from bundled base HP.
- Snapshot exposes diagnostic inputs, special weapon identity, active manual-input marker and a partial applied-effects list recorded at calculation sites. The list is explicitly partial, not a complete formula trace.

## PNM Shadow finding

The four ready totem IDs 9435/9438/9441/9444 already resolve shared defensive stats and apply x2 magic damage per roll. Existing tests cover all four, including exact doubling of mean and maximum, observed PNM HP 200 versus Nightmare HP 300, unknown encounter HP, and Voidwaker's melee-derived magic special. The multiplier was not increased or duplicated.

RuneLite distinguishes dormant IDs 9434/9437/9440/9443, ready IDs above, and charged IDs 9436/9439/9442/9445. The reference calculator includes dormant and ready IDs for the magic transform but excludes charged IDs. This plugin has no bundled dormant rows; charged rows remain ambiguous. This is a potential transition/selection diagnostic lead, not proof of the reported active-pillar error. No guessed fallback stats were added. The next useful reproduction includes target ID, observed encounter, equipment, boosted Magic, selected style, and the Inspector's raw/target result. A live correction cannot be claimed from the existing report alone.

## Bounded raid and input coverage

| Area | Existing verified scope | Remaining limitation |
|---|---|---|
| CoX / CM | Party/mode/level scaling, Olm hand ordering, size effects, damage reductions, Guardian pickaxe rule; regression suite preserved | Local/observed party inputs are not a complete team-state source; manual HP/Mining fallbacks remain. Some specials intentionally withhold unsupported encounter reductions. |
| ToB / entry / hard | Party HP, variant IDs, Nylo style immunity, Verzik P1 basic cap; regression suite preserved | Starting party size can differ from current roster; P1 special transforms remain deliberately unavailable. |
| ToA | Invocation defensive scaling, Fang/Shadow encounter rules, known target IDs | No complete party/path HP model; ruby blocked as above. This is not full ToA fidelity. |
| Ammunition | Existing compatible worn/quiver source selection, blowpipe manual dart, seeking-arrow floor, equipment fallback, enchanted bolt mixture | Name-family compatibility is not an exhaustive tier-by-tier weapon/ammo legality table. Unsupported bolt types remain unavailable; ruby relies on health-bar estimates outside ToA. |
| Saved spell / style | Existing powered-staff and saved-spell isolation tests; forced style remains explicit | Manual spell and stored dart cannot be inferred perfectly from all client states. |
| Cosmetics | Existing ornament equipment/category tests retained | This audit does not certify every cosmetic item beyond the tested mappings. |

## Verification

`python .superpowers/verification/verify-combat.py` compiles against cached real RuneLite jars with Java 11 target and executes the full JUnit suite. Last full run: **228 tests passed**. Existing compiler deprecation/unchecked notes and missing optional SLF4J binding notice remain; no compilation error or test failure.

New targeted regression cases cover Zulrah threshold 50/51, preserved 0–80 distribution, ruby mixture, guaranteed Zaryte diamond proc, Voidwaker 40–120 distribution, halberd per-hit totals, dark-bow basic arrows, live basic ruby routing, live Voidwaker routing, Soulreaper stack routing/selected crush and ToA ruby rejection for both normal and special attacks. Red failures were observed before the corresponding fixes for missing Zulrah transform/routing, missing Soulreaper live stacks, and ToA ruby erroneously returning damage.

An earlier full run encountered five overlay test failures caused by concurrent HUD preview integration (`config` null); the HUD/root agents corrected them and the subsequent 228-test full run is green. No files committed or pushed by this task.

## Sources

- OSRS Wiki calculator primary implementation: https://github.com/weirdgloop/osrs-dps-calc/blob/main/src/lib/PlayerVsNPCCalc.ts (web accessed 2026-10-17; local reference revision `89c3e25b344aea90d0189746e4b5f73dde0f0383`). Zulrah implementation cites Jagex Ash https://twitter.com/JagexAsh/status/1745852774607183888 and uses `cappedRerollTransformer(50, 5, 45)`.
- Totem ID set and x2 magic transform: same reference repository, `src/lib/constants.ts` and `PlayerVsNPCCalc.ts`.
- RuneLite NPC state IDs: local upstream `runelite-api/src/main/java/net/runelite/api/gameval/NpcID.java`, revision `e1afd4502d3819700374bd045ab4dfcf6f9d82ec`; https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/NpcID.java .
