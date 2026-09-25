#!/usr/bin/env python3
"""Generate Combat Insight's compact NPC combat-stat snapshot.

The OSRS Wiki stores every live NPC ID for a monster variant in its `id`
field. Combat Insight must expand that complete list because bosses often use
different IDs for phases even when their combat stats do not change.
"""

import argparse
import copy
import datetime as dt
import json
import re
import time
import urllib.parse
import urllib.request
from collections import defaultdict
from pathlib import Path


API_URL = "https://oldschool.runescape.wiki/api.php"
USER_AGENT = "Combat Insight data generator (https://github.com/BornDied/combat-insight)"
DEFAULT_OUTPUT = Path("src/main/resources/com/combatinsight/data/monsters.tsv")

FIELDS = (
    "page_name",
    "page_name_sub",
    "name",
    "id",
    "hitpoints",
    "defence_level",
    "magic_level",
    "magic_attack_bonus",
    "size",
    "flat_armour",
    "stab_defence_bonus",
    "slash_defence_bonus",
    "crush_defence_bonus",
    "magic_defence_bonus",
    "light_range_defence_bonus",
    "standard_range_defence_bonus",
    "heavy_range_defence_bonus",
    "elemental_weakness",
    "elemental_weakness_percent",
    "slayer_experience",
    "attribute",
)

PARSER_MARKER = re.compile(r"['\"`]*UNIQ--[A-Za-z0-9]+-[0-9A-F]{8}-QINU['\"`]*")
NAMESPACE_PAGE = re.compile(r"^[A-Za-z]*:")
BARRIER = re.compile(r"^(Strong|Weak|Medium|Overcharged) Barrier$")


def fetch_rows():
    rows = []
    offset = 0
    selected = ",".join(repr(field) for field in FIELDS)
    while True:
        bucket_query = (
            "bucket('infobox_monster')"
            f".select({selected})"
            f".limit(500).offset({offset})"
            ".where(bucket.Not('Category:Discontinued content'))"
            ".orderBy('page_name_sub', 'asc').run()"
        )
        query = urllib.parse.urlencode(
            {"action": "bucket", "format": "json", "query": bucket_query}
        )
        request = urllib.request.Request(
            f"{API_URL}?{query}", headers={"User-Agent": USER_AGENT}
        )
        with urllib.request.urlopen(request, timeout=60) as response:
            payload = json.load(response)
        if not isinstance(payload, dict) or "error" in payload or not isinstance(payload.get("bucket"), list):
            raise ValueError("Wiki API did not return a valid bucket page; existing snapshot kept")
        page = payload["bucket"]
        if offset == 0 and not page:
            raise ValueError("Wiki API returned no monsters; existing snapshot kept")
        rows.extend(page)
        if len(page) < 500:
            return rows
        offset += 500
        time.sleep(0.25)


def first(value, default=None):
    if isinstance(value, list):
        return value[0] if value else default
    return default if value is None else value


def integer(value, default=0):
    value = first(value, default)
    try:
        return int(value)
    except (TypeError, ValueError):
        return default


def text(value, default=""):
    value = first(value, default)
    if value is None:
        return default
    return PARSER_MARKER.sub("", str(value)).strip()


def version(row):
    page_name_sub = text(row.get("page_name_sub"))
    return page_name_sub.split("#", 1)[1] if "#" in page_name_sub else ""


def valid_variant(row):
    page_name = text(row.get("page_name"))
    page_name_sub = text(row.get("page_name_sub"))
    variant = version(row)
    infobox_name = text(row.get("name"))

    if "Challenge Mode" in variant or "Deadman" in page_name_sub:
        return False
    if NAMESPACE_PAGE.match(page_name_sub):
        return False
    if any(label in variant for label in ("Spawn point", "Asleep", "Defeated")):
        return False
    if BARRIER.match(page_name_sub):
        return False
    lowered = page_name.lower()
    if any(
        label in lowered
        for label in (
            "(historical)",
            "(pvm arena)",
            "(deadman: apocalypse)",
            "(echo)",
        )
    ):
        return False
    if page_name == "Doom of Mokhaiotl" and (
        "Shielded" in infobox_name or "Burrowed" in infobox_name
    ):
        return False
    if page_name == "Araxxor" and "In combat" not in variant:
        return False

    hitpoints = 50000 if 14779 in npc_ids(row) else integer(row.get("hitpoints"))
    return (hitpoints > 0 or 10845 in npc_ids(row)) and bool(npc_ids(row))


def npc_ids(row):
    raw_ids = row.get("id") or []
    if not isinstance(raw_ids, list):
        raw_ids = [raw_ids]
    result = []
    for raw_id in raw_ids:
        try:
            npc_id = int(raw_id)
        except (TypeError, ValueError):
            continue
        if npc_id not in result:
            result.append(npc_id)
    return result


def attributes(row):
    values = row.get("attribute") or []
    if not isinstance(values, list):
        values = [values]
    result = []
    for value in values:
        attribute = text(value)
        if attribute and attribute not in result:
            result.append(attribute)
    return tuple(result)


def is_slayer_monster(row):
    result = row.get("slayer_experience") is not None
    if "Awakened" in version(row):
        result = False
    if text(row.get("page_name")) == "Lizardman shaman (Chambers of Xeric)":
        result = True
    return result


def profile(row):
    ids = npc_ids(row)
    hitpoints = 50000 if 14779 in ids else integer(row.get("hitpoints"))
    weakness = text(row.get("elemental_weakness"), "-").lower() or "-"
    if weakness in ("none", "n/a"):
        weakness = "-"
    values = (
        integer(row.get("defence_level")),
        integer(row.get("magic_level")),
        hitpoints,
        integer(row.get("magic_attack_bonus")),
        integer(row.get("size")),
        integer(row.get("flat_armour")),
        integer(row.get("stab_defence_bonus")),
        integer(row.get("slash_defence_bonus")),
        integer(row.get("crush_defence_bonus")),
        integer(row.get("magic_defence_bonus")),
        integer(row.get("light_range_defence_bonus")),
        integer(row.get("standard_range_defence_bonus")),
        integer(row.get("heavy_range_defence_bonus")),
        weakness,
        integer(row.get("elemental_weakness_percent")),
        1 if is_slayer_monster(row) else 0,
        ",".join(attributes(row)),
    )
    return values


def generate(rows):
    if not isinstance(rows, list) or not rows or any(not isinstance(row, dict) for row in rows):
        raise ValueError("Expected a non-empty list of complete monster records")
    if not any("defence_level" in row and "magic_level" in row for row in rows):
        raise ValueError("Monster records lack combat stats; refusing to generate a partial snapshot")
    by_id = defaultdict(list)
    for row in prepare_raid_rows(rows):
        if not valid_variant(row):
            continue
        values = profile(row)
        source = text(row.get("page_name_sub"))
        for npc_id in npc_ids(row):
            by_id[npc_id].append((values, source))

    output = []
    ambiguous_ids = []
    for npc_id in sorted(by_id):
        candidates = by_id[npc_id]
        unique_profiles = {values for values, _ in candidates}
        ambiguous = len(unique_profiles) > 1
        if ambiguous:
            ambiguous_ids.append(npc_id)
        values, _ = sorted(candidates, key=lambda candidate: (candidate[1], candidate[0]))[0]
        output.append((npc_id, *values, 1 if ambiguous else 0))
    return output, ambiguous_ids


# These CM IDs have the same underlying base stats as their normal counterpart.
# Runtime scaling applies CM exactly once, before locally tracked reductions.
COX_CM_BASE_IDS = {7544: 7543, 7545: 7540, 7549: 7548, 7553: 7550,
                   7554: 7551, 7555: 7552, 7570: 7569, 7572: 7571, 7585: 7584}
TOB_ALIASES = {10814: [10815, 10816, 10817], 10822: [10823, 10824, 10825],
               10774: [10841], 10775: [10842], 10776: [10843], 8384: [10861]}


def prepare_raid_rows(rows):
    """Normalize encounter records, preserving unknown HP as zero, not a guess."""
    result = []
    normal_by_id = {}
    for original in rows:
        row = copy.deepcopy(original)
        page = text(row.get("page_name"))
        variant = version(row)
        if "Challenge Mode" in variant:
            continue  # added from the normal base below, never double-scale CM stats
        ids = [i for i in npc_ids(row) if i not in COX_CM_BASE_IDS]
        if page == "Blood spawn":
            ids = [10821] if variant == "Entry" else [8367, 10829]
        if page == "The Maiden of Sugadinti":
            # Infobox phase HP is the transition threshold, not the health-bar maximum.
            row["hitpoints"] = 2000 if "Entry" in variant else 3500
        if page == "Nylocas Matomenos" and 10845 in ids:
            # Wiki does not currently expose HP for this entry-mode add.
            row["hitpoints"] = 0
        for base, aliases in TOB_ALIASES.items():
            if base in ids:
                ids += [i for i in aliases if i not in ids]
        row["id"] = [str(i) for i in ids]
        attrs = list(attributes(row))
        if ids and any(8338 <= i <= 8388 or 10767 <= i <= 10868 for i in ids):
            attrs.append("theatre_of_blood")
        row["attribute"] = list(dict.fromkeys(attrs))
        result.append(row)
        if "xerician" in attrs:
            for npc_id in ids:
                normal_by_id[npc_id] = row
    for cm_id, base_id in COX_CM_BASE_IDS.items():
        if base_id in normal_by_id:
            row = copy.deepcopy(normal_by_id[base_id])
            row["id"] = [str(cm_id)]
            row["attribute"] = list(attributes(row)) + ["cox_cm"]
            result.append(row)
    return result


def render(rows, source_date):
    header = [
        f"# OSRS Wiki infobox_monster snapshot generated {source_date}",
        "# id\tdef\tmagic\thp\toff_magic\tsize\tflat_armour\tstab\tslash\tcrush\tmagic_def\tranged_light\tranged_standard\tranged_heavy\tweakness\tweakness_pct\tslayer\tattributes\tambiguous",
    ]
    body = ["\t".join(str(value) for value in row) for row in rows]
    return "\n".join(header + body) + "\n"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=Path, help="Use a saved Bucket JSON response")
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--source-date", default=dt.date.today().isoformat())
    args = parser.parse_args()

    rows = (
        json.loads(args.input.read_text(encoding="utf-8"))
        if args.input
        else fetch_rows()
    )
    generated, ambiguous_ids = generate(rows)
    if not generated:
        raise ValueError("Refusing to replace the snapshot with an empty dataset")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(render(generated, args.source_date), encoding="utf-8")
    print(
        f"Wrote {len(generated)} NPC IDs to {args.output} "
        f"({len(ambiguous_ids)} ambiguous IDs)"
    )


if __name__ == "__main__":
    main()
