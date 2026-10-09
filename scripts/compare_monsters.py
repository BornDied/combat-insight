"""Compare two complete Combat Insight snapshots without changing either."""
import argparse
import json
from pathlib import Path

def compare_snapshots(before, after):
    return {'added': sorted(after.keys() - before.keys()),
            'removed': sorted(before.keys() - after.keys()),
            'changed': sorted(k for k in before.keys() & after.keys() if before[k] != after[k]),
            'ambiguous': sorted(k for k, row in after.items() if row[-1] == '1')}

def read_snapshot(path):
    result = {}
    for line in Path(path).read_text().splitlines():
        if not line or line.startswith('#'): continue
        fields = line.split('\t')
        if len(fields) != 19 or int(fields[0]) in result:
            raise ValueError('Malformed or duplicate NPC row')
        result[int(fields[0])] = tuple(fields[1:])
    if not result: raise ValueError('Empty snapshot')
    return result

if __name__ == '__main__':
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('before'); p.add_argument('after'); args = p.parse_args()
    print(json.dumps(compare_snapshots(read_snapshot(args.before), read_snapshot(args.after)), indent=2))
