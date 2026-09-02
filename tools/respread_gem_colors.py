"""One-off: boost color separation of the NIGems table in place.

Keeps gem names, table order and family layout; replaces each rgb with a
more separated variant of the same hue: chromatic families get a saturated
three-step / six-step value ladder plus a mild in-family hue stretch, earths
stay muted browns on their own ladder, and the mono/metallic family spreads
value from near-black to near-white. Run from the repo root.
"""
import colorsys
import re

SRC = 'src/main/java/com/nestedinfinity/mod/items/gems/NIGems.java'

# interleaved (saturation, value) ladder: consecutive ranks always differ
# strongly in both channels, and no phase repeats within 12 ranks
LADDER12 = [(0.72, 0.62), (0.88, 0.46), (0.80, 0.67), (0.72, 0.40), (0.88, 0.55), (0.80, 0.50),
            (0.72, 0.67), (0.88, 0.62), (0.80, 0.40), (0.72, 0.55), (0.88, 0.50), (0.80, 0.46)]
EARTH12 = [(0.28, 0.76), (0.40, 0.60), (0.34, 0.52), (0.28, 0.80), (0.40, 0.68), (0.34, 0.60),
           (0.28, 0.52), (0.40, 0.76), (0.34, 0.80), (0.30, 0.64), (0.38, 0.56), (0.32, 0.72)]


def hsv(rgb):
    return colorsys.rgb_to_hsv(*(c / 255 for c in rgb))


def rgb01(h, s, v):
    r, g, b = colorsys.hsv_to_rgb(h % 1.0, min(s, 1.0), min(v, 1.0))
    return tuple(round(c * 255) for c in (r, g, b))


def unwrap(hs):
    """Red-family hues that cross 0: lift 0-60 up by 360 when 300+ present."""
    if any(h > 300 for h in hs):
        return [h + 360 if h < 60 else h for h in hs]
    return list(hs)


def spread(family_name, entries):
    """entries: list of [name, (r,g,b)] in table order; returns new rgb per name."""
    mono = family_name.startswith('grays')
    earth = family_name.startswith('earths')
    out = {}
    if mono:
        chromatic = [e for e in entries if hsv(e[1])[1] >= 0.30]
        plain = [e for e in entries if hsv(e[1])[1] < 0.30]
        for i, (name, rgb) in enumerate(chromatic):
            h, _s, _v = hsv(rgb)
            out[name] = rgb01(h, *LADDER12[(i * 4) % 12])
        plain_sorted = sorted(plain, key=lambda e: hsv(e[1])[2])
        n = max(len(plain_sorted) - 1, 1)
        for rank, (name, rgb) in enumerate(plain_sorted):
            h, s, _v = hsv(rgb)
            out[name] = rgb01(h, min(s, 0.10), 0.14 + (0.95 - 0.14) * rank / n)
        return out
    if earth:
        for i, (name, rgb) in enumerate(entries):
            h, _s, _v = hsv(rgb)
            out[name] = rgb01(h, *EARTH12[i % 12])
        return out
    # chromatic family: ladder assigned by hue rank, mild hue stretch
    keyed = sorted(entries, key=lambda e: hsv(e[1])[0])
    unwrapped = unwrap([hsv(e[1])[0] for e in keyed])
    lo, hi = min(unwrapped), max(unwrapped)
    n = max(len(keyed) - 1, 1)
    margin = (hi - lo) * 0.12 if hi - lo > 12 else 0
    for i, (name, rgb) in enumerate(keyed):
        h, _s, _v = hsv(rgb)
        if margin:
            h = (lo - margin) + ((hi + margin) - (lo - margin)) * i / n
        out[name] = rgb01(h, *LADDER12[i % 12])
    return out


def main():
    src = open(SRC, encoding='utf-8').read()
    row_re = re.compile(r'\{"([a-z_]+)", "(\d+),(\d+),(\d+)"\}')
    fam_re = re.compile(r'// (.+)$')

    # split the table into family blocks of rows
    table_start = src.index('String[][] table = {')
    table_end = src.index('};', table_start)
    table = src[table_start:table_end]

    # collect (family, [(name, rgb)]) in order
    families = []
    for line in table.splitlines():
        fam = fam_re.search(line.strip())
        if fam:
            families.append([fam.group(1), []])
        for m in row_re.finditer(line):
            families[-1][1].append([m.group(1),
                                    (int(m.group(2)), int(m.group(3)), int(m.group(4)))])
    assert sum(len(f[1]) for f in families) == 100, sum(len(f[1]) for f in families)

    new_rgb = {}
    for fam_name, entries in families:
        new_rgb.update(spread(fam_name, entries))

    # cross-family pass: gems from different families can still collide when
    # they share a ladder phase and sit on adjacent hues; nudge later ones
    # (V, then S, then hue) until clear of every earlier gem.
    order = [name for fam, es in families for name, _ in es]
    # resolve in hue order so dense hue bands share the burden fairly
    order.sort(key=lambda n: hsv(new_rgb[n])[0] * (1 if hsv(new_rgb[n])[0] < 0.8 else -1))
    MIN_GAP = 24.0
    for idx, name in enumerate(order):
        h, s, v = hsv(new_rgb[name])
        def clear(hh, ss, vv):
            cand = rgb01(hh, ss, vv)
            return all(sum((x - y) ** 2 for x, y in zip(cand, new_rgb[prev])) ** 0.5 >= MIN_GAP
                       for prev in order[:idx])
        if clear(h, s, v):
            continue
        for dv in (0.07, -0.07, 0.13, -0.13, 0.19, -0.19):
            if clear(h, s, v + dv):
                v += dv
                break
        else:
            for ds in (0.08, -0.06, 0.12, -0.10):
                if clear(h, s + ds, v):
                    s += ds
                    break
            else:
                for dh in (0.02, -0.02, 0.035, -0.035, 0.05, -0.05, 0.07, -0.07):
                    if clear(h + dh, s, v):
                        h += dh
                        break
                else:
                    raise SystemExit(f'could not separate {name}')
        new_rgb[name] = rgb01(h, s, v)

    def repl(m):
        r, g, b = new_rgb[m.group(1)]
        return '{"%s", "%d,%d,%d"}' % (m.group(1), r, g, b)

    new_table = row_re.sub(repl, table)
    open(SRC, 'w', encoding='utf-8', newline='\n').write(
        src[:table_start] + new_table + src[table_end:])

    # report the worst remaining neighbors (same family, consecutive hue rank)
    from itertools import combinations
    colors = [(name, new_rgb[name]) for fam, es in families for name, _ in es]
    worst = []
    for (a, ca), (b, cb) in combinations(colors, 2):
        d = sum((x - y) ** 2 for x, y in zip(ca, cb)) ** 0.5
        worst.append((d, a, b))
    worst.sort()
    print('closest pairs after spread:', worst[:6])


if __name__ == '__main__':
    main()
