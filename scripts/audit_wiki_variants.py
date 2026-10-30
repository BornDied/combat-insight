"""Read-only exact-ID comparison; variant candidates are not confirmed game bugs.

Usage: python scripts/audit_wiki_variants.py WIKI_MONSTERS_JSON LOCAL_MONSTERS_TSV
Keep the Wiki Git revision alongside the report. No network or snapshot mutation.
"""
import argparse
import json
from collections import defaultdict
from pathlib import Path

from compare_monsters import read_snapshot


# Indices refer to read_snapshot's tuple (which excludes the NPC ID column).
NUMERIC_FIELDS = {
    'skills.def': 0, 'skills.magic': 1, 'skills.hp': 2, 'offensive.magic': 3,
    'defensive.flat_armour': 5, 'defensive.stab': 6, 'defensive.slash': 7,
    'defensive.crush': 8, 'defensive.magic': 9, 'defensive.light': 10,
    'defensive.standard': 11, 'defensive.heavy': 12,
}


def audit(wiki, local):
    result = dict(wiki_variant_records=len(wiki), unique_wiki_ids=len({m['id'] for m in wiki}),
                  local_ids=len(local), duplicate_ids={}, unmatched=[],
                  non_live_records=[], numeric_differences=[], ambiguous_matches=[])
    variants = defaultdict(list)
    for monster in wiki:
        npc_id = monster['id']
        record = {key: monster.get(key, '') for key in ('id', 'name', 'version')}
        label = record['name'] + (f" ({record['version']})" if record['version'] else '')
        variants[npc_id].append(label)
        # Wiki calculator pseudo IDs (e.g. Yama Glyphic Attenuation) are not live IDs.
        if npc_id >= 100000 or npc_id < 0:
            result['non_live_records'].append(record)
            continue
        row = local.get(npc_id)
        if row is None:
            result['unmatched'].append(record)
            continue
        if row[-1] == '1':
            result['ambiguous_matches'].append(record)
        differences = {}
        for field, index in NUMERIC_FIELDS.items():
            group, key = field.split('.')
            value = monster.get(group, {}).get(key)
            if value is not None and int(row[index]) != value:
                differences[field] = {'local': int(row[index]), 'wiki': value}
        if differences:
            result['numeric_differences'].append(dict(record, differences=differences))
    result['duplicate_ids'] = {key: labels for key, labels in sorted(variants.items())
                               if len(labels) > 1}
    return result


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('wiki_json')
    parser.add_argument('local_tsv')
    args = parser.parse_args()
    print(json.dumps(audit(json.loads(Path(args.wiki_json).read_text(encoding='utf-8')),
                           read_snapshot(args.local_tsv)), indent=2))
