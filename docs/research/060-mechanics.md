# 0.6.0 mechanics evidence and boundaries

Research checked September 28–29, 2026. Local test candidate, not a published release.

## Sources

- Wiki-maintained calculator data and formula cross-check: https://github.com/weirdgloop/osrs-dps-calc/tree/89c3e25b (retrieved checkout). Its GPL implementation is not copied into this BSD project; new arithmetic classes were implemented independently and tested with worked examples.
- DT2 data: `cdn/json/monsters.json` in that revision; pages https://oldschool.runescape.wiki/w/Duke_Sucellus , https://oldschool.runescape.wiki/w/Vardorvis , https://oldschool.runescape.wiki/w/The_Leviathan , https://oldschool.runescape.wiki/w/The_Whisperer . Shared IDs are ambiguous in the bundled database; the resolver uses combat level or the configured override.
- Fang published design and correction: https://oldschool.runescape.wiki/w/Update:Tombs_of_Amascut:_Raid_Rewards_V2 and https://oldschool.runescape.wiki/w/Update:Tombs_of_Amascut_Drop_Mechanics_%26_Osmumten%27s_Fang . Current range/style cross-check: https://oldschool.runescape.wiki/w/Osmumten%27s_fang .
- Halberd: https://oldschool.runescape.wiki/w/Crystal_halberd and https://oldschool.runescape.wiki/w/Dragon_halberd ; calculator's independent size/roll handling corroborates the 110% damage and second 75% attack roll.
- Scorching: https://oldschool.runescape.wiki/w/Scorching_bow . Indexed versions conflict on a purported additional 30% special accuracy bonus; current maintained calculator adds no special bonus, with the 30% being the normal demonbane bonus. This candidate follows that formula, permits only demon targets, and keeps burn/bind in explanatory text outside expected direct damage. This needs in-game verification before publishing.
- Bolts: https://oldschool.runescape.wiki/w/Enchanted_bolts , https://oldschool.runescape.wiki/w/Diamond_bolts_(e) , https://oldschool.runescape.wiki/w/Ruby_bolts_(e) , https://oldschool.runescape.wiki/w/Zaryte_crossbow ; maintained calculator `src/lib/dists/bolts.ts` cross-check.
- Raid per-hit transforms: Wiki-maintained calculator `PlayerVsNPCCalc.ts` and `HitDist.ts`, linked above. Sources for manual testing: https://oldschool.runescape.wiki/w/Verzik_Vitur , https://oldschool.runescape.wiki/w/Guardian_(Chambers_of_Xeric) , https://oldschool.runescape.wiki/w/Great_Olm .
- Client observation: https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/gameval/VarbitID.java and official RaidsPlugin; public `NPC.getCombatLevel`, health ratio/scale; real API/client 1.12.39 compiled for this candidate.

## Worked rules

DT2 normal/Awakened combat levels and HP: Duke 758/1099, 485/1697 HP; Whisperer 791/1146, 900/2700 HP; Leviathan 798/1157, 900/2700 HP; Vardorvis 784/1136, 700/1400 HP. Quest IDs are never promoted to post-quest variants by an override.

Vardorvis Defence = endpoint + floor((full-endpoint) * estimatedCurrentHP / maximumHP). Normal 215→145, Awakened 268→181, quest 180→130. Normal half health gives 180. Health-bar midpoint estimates are reused from the existing live health estimator; missing health is unavailable. Ice demon uses115% demonbane effectiveness and all supported demonbane styles bypass its reduction. Duke's demonbane vulnerability scales the bonus to 70%; a 70% weapon bonus becomes 49%, not a 70% multiplier to total damage.

Halberd base maximum 40 gives 44 per hit; a target larger than one tile gets a second hit. With attack roll 1000, rolls are 1000 and 750. Accuracy is 1-(1-p1)(1-p2), expected direct damage is mean*(p1+p2).

Fang true maximum 40 has a special range 6–40, mean 23 before armour. Attack roll increases 50%. Stab rerolls use the existing outside-ToA fang formula, or 1-(1-p)^2 in ToA. Slash uses ordinary accuracy; its normal damage range is still narrowed.

Raid basic attacks transform every integer damage roll and every hitsplat. Sanguinesti's20% +8 proc is mixed before those transforms, including the resulting maximum. Tekton magic divides by five with minimum one; Vasa crystal magic and off-style Olm damage divide by three. Ice demon non-fire/non-demonbane damage divides by three. Guardian pickaxe damage multiplies by (50+real Mining+pickaxe bonus)/150. Verzik P1 uniformly rolls a cap 0–10 melee or 0–3 otherwise, then uses min(damage,cap); damage 40 has mean 5 under melee cap, not 10.

Ruby proc 6%, diamond 10%, onyx 11%, dragonstone 6%; hard Kandarin multiplies rates by 1.1. Ruby is 20% current target HP capped100, or Zaryte22% capped110. Diamond maximum is115%, Zaryte126%; onyx120%/132%, blocked on undead. Dragonstone adds floor(Ranged*2/10), Zaryte divisor9, blocked on dragons/fiery targets. Ruby/diamond effects bypass ordinary accuracy. Zaryte special doubles attack roll and forces a valid effect on successful accuracy; a failed accuracy roll still allows the ordinary ruby/diamond proc. With accuracy0.5, Zaryte ruby effect probability is0.5+0.5*0.06=0.53.

## Explicit incomplete coverage

- Full fresh Wiki NPC fetch returned403. All3976 existing NPC IDs are retained; representative reference IDs reveal no missing real IDs, which is not proof of exhaustive coverage.
- Special attacks against guarded raid damage transforms remain unavailable; basic attacks are implemented. Complex delayed effects are not newly simulated.
- Only ruby/diamond/onyx/dragonstone enchanted bolts are modeled here. Other effects and enchanted-bolt combinations with raid transforms are unavailable.
- Ruby at player HP≤10 is withheld pending precise current low-HP behaviour confirmation; no fabricated fallback.
- CoX observed inputs are in-raid scaled party size, Challenge Mode, and valid highest combat level. HP/Mining remain manual. ToB starting party size remains manual; do not count only visible/living players.
- Scorching direct damage formula needs in-game validation against the conflicting indexed documentation.
- No claim that every special weapon in the game is supported.
