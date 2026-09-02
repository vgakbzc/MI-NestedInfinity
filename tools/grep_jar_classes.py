"""Find class files (binary) containing a constant string."""
import os
import sys

needle = sys.argv[1].encode()
root_dir = sys.argv[2] if len(sys.argv) > 2 else '.tmp_micls'
hits = []
for root, _, files in os.walk(root_dir):
    for f in files:
        if f.endswith('.class'):
            p = os.path.join(root, f)
            data = open(p, 'rb').read()
            if needle in data:
                hits.append((data.count(needle), p))
for n, p in sorted(hits, reverse=True)[:15]:
    print(n, os.path.relpath(p, root_dir).replace(os.sep, '/'))
