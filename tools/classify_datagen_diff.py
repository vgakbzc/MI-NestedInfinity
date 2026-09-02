"""Classify `git diff` changes under src/generated as reorder-only vs real.

runData re-emits JSON arrays in nondeterministic order, producing large diffs
whose sorted content is identical. This script lists files whose content
actually changed (after recursive key-sort + list-multiset normalization).
"""
import json
import subprocess
import sys


def norm(o):
    if isinstance(o, dict):
        return sorted((k, norm(v)) for k, v in o.items())
    if isinstance(o, list):
        return sorted(json.dumps(norm(x)) for x in o)
    return o


def main():
    changed = subprocess.run(
        ['git', 'diff', '--name-only', '--', 'src/generated'],
        capture_output=True, text=True, check=True).stdout.split()
    real = []
    for p in changed:
        old_raw = subprocess.run(['git', 'show', 'HEAD:' + p],
                                 capture_output=True).stdout
        try:
            old = json.loads(old_raw or b'{}')
            new = json.loads(open(p, encoding='utf-8').read())
        except (json.JSONDecodeError, UnicodeDecodeError):
            real.append(p)  # not JSON (or binary) -> treat as real
            continue
        if norm(old) != norm(new):
            real.append(p)
    print('total modified:', len(changed))
    print('real content changes:', len(real))
    for p in real:
        print(' ', p)
    # also report untracked additions under src/generated
    untracked = subprocess.run(
        ['git', 'ls-files', '--others', '--exclude-standard', 'src/generated'],
        capture_output=True, text=True, check=True).stdout.split()
    print('untracked new files:', len(untracked))
    for p in untracked:
        print('  +', p)


if __name__ == '__main__':
    main()
