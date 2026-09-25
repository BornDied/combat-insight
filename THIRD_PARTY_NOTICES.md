# Third-party notices

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
RuneLite is licensed under the BSD 2-Clause License. RuneLite and Old School
RuneScape are separate projects; this plugin is not endorsed by Jagex.
