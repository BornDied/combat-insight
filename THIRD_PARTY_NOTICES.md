# Third-party notices

The October30 local defence-tracking candidate adapts projectile-delay arithmetic,
same-tick hitsplat ordering and deferred live-input/HP-XP observations from RuneLite's
Special Attack Counter (SpecialCounterPlugin.java and SpecialWeapon.java), copyright
2018 Raqes. Its full BSD2-Clause notice is retained in SpecialHitTiming.java and in
the bundled resource com/combatinsight/third-party/RuneLite-SpecialCounter-LICENSE.txt.
Source: https://github.com/runelite/runelite/tree/master/runelite-client/src/main/java/net/runelite/client/plugins/specialcounter

Better Party Defence's current Plugin Hub pin3d5848574bce1c1a8254c1ea887293b4292eb9b9
was reviewed as a behaviour reference for current-minus-floor display and uncertainty.
No Better Party Defence or OSParty source implementation is copied into this candidate.
https://github.com/Falsary/better-party-defence/tree/3d5848574bce1c1a8254c1ea887293b4292eb9b9

Combat Insight source code is distributed under the BSD 2-Clause License; see
`LICENSE`.

The bundled `monsters.tsv` file is generated directly from structured monster
infobox data provided by the [Old School RuneScape Wiki](https://oldschool.runescape.wiki/)
through its public API. The world snapshot was generated on 2026-09-01; 160
ToB/CoX combat ID records were refreshed on 2026-09-25. The
Wiki describes its site-content licence on its
[copyright page](https://oldschool.runescape.wiki/w/RuneScape:Copyrights).

The bundled `weapon-categories.tsv` file is a compact, transformed mapping
based on the public equipment dataset from the
[OSRS Wiki DPS Calculator](https://github.com/weirdgloop/osrs-dps-calc),
revision `91218d63e71927e99748a50d008975336025a88e` (snapshot dated
2026-08-08). That project is licensed under the GNU General Public License,
version 3. Its source and licence are available in the linked repository.

Combat formulas were implemented for this RuneLite plugin and checked against
public Jagex descriptions, current game behaviour documented by the OSRS Wiki,
and the OSRS Wiki DPS Calculator's public implementation.

The 2026-09-25 raid review cross-checked scaling and rounding against the public
[Chambers scaling reference](https://github.com/weirdgloop/osrs-dps-calc/blob/main/src/lib/scaling/ChambersOfXeric.ts)
(Git blob `faceaa7fba1c6e1ea6e82927e8999ab11e491c42`) and
[Theatre scaling reference](https://github.com/weirdgloop/osrs-dps-calc/blob/main/src/lib/scaling/TheatreOfBlood.ts)
(Git blob `3e506cc546f491616459e0a03cfd28985a1e45e7`). NPC phase IDs were
cross-checked against RuneLite's `gameval.NpcID` definitions. The audited roster
is in `src/test/resources/raid-coverage.tsv`.

Combat Insight is a third-party RuneLite plugin and uses the RuneLite API.

The October 24 alpha2 continuation adds eight curated Fractured Archive records
from the [Wiki DPS structured monster dataset](https://github.com/weirdgloop/osrs-dps-calc/blob/98e4fac38b580901cc3f8d485bbaa48bbb3277ed/cdn/json/monsters.json),
cross-checked with RuneLite gameval definitions at
`fc8d18969e3028af21a78fae38c9ab6c8c7573b1`. The Wiki-derived data retains
the Wiki's site-content attribution/licensing obligations. The comparison
report is data derived from those sources, not copied calculator code.
Source revisions and scope are recorded in
`docs/research/2026-10-24-encounter-audit.md`. No GearScape implementation was copied.

RuneLite is licensed under the BSD 2-Clause License. RuneLite and Old School
RuneScape are separate projects; this plugin is not endorsed by Jagex.
