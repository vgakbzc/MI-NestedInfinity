#!/usr/bin/env python3
"""Optical program assets: the hundred-gem collection, its glow tubes, the
tube parts, the super assembler machine and its 10x10 GUI, plus the three new
noble-gas fluids.

The gem table is parsed straight out of NIGems.java (single source of truth);
this script only adds the display names and paints. Run from tools/:
    python gen_optical_assets.py
"""

import json
import os
import re

from gen_algae_assets import (A, MODID, ROOT, make_item_texture, make_bucket_texture,
                              read_png, write_json, write_png, fluid_blockstate, template_recolor)

JAVA = os.path.join(ROOT, 'src/main/java/com/nestedinfinity/mod/items/gems/NIGems.java')
DST = os.path.join(A, 'textures')
DATA = os.path.join(ROOT, 'src/main/resources/data', MODID)

# ---------------------------------------------------------------- the gem table

# display names keyed by the id suffix in NIGems.java
GEM_NAMES = {
    "ruby": ("Ruby", "红宝石"), "garnet": ("Garnet", "石榴石"), "spinel": ("Spinel", "尖晶石"),
    "carnelian": ("Carnelian", "红玉髓"), "bloodstone": ("Bloodstone", "血玉髓"),
    "cuprite": ("Cuprite", "赤铜矿"), "crocoite": ("Crocoite", "红铅矿"),
    "eudialyte": ("Eudialyte", "异性石"), "rhodochrosite": ("Rhodochrosite", "菱锰矿"),
    "morganite": ("Morganite", "摩根石"), "kunzite": ("Kunzite", "紫锂辉石"),
    "rose_quartz": ("Rose Quartz", "芙蓉石"), "rhodonite": ("Rhodonite", "蔷薇辉石"),
    "thulite": ("Thulite", "锰黝帘石"), "poudretteite": ("Poudretteite", "硅硼钾钠石"),
    "sunstone": ("Sunstone", "日长石"), "amber": ("Amber", "琥珀"),
    "fire_opal": ("Fire Opal", "火欧泊"), "hessonite": ("Hessonite", "钙铝榴石"),
    "vanadinite": ("Vanadinite", "钒铅矿"), "sphalerite": ("Sphalerite", "闪锌矿"),
    "imperial_topaz": ("Imperial Topaz", "帝王托帕石"), "citrine": ("Citrine", "黄水晶"),
    "topaz": ("Topaz", "黄玉"), "heliodor": ("Heliodor", "黄绿柱石"), "sulfur": ("Sulfur", "硫磺"),
    "sphene": ("Sphene", "榍石"), "zircon": ("Zircon", "锆石"), "scapolite": ("Scapolite", "方柱石"),
    "tiger_eye": ("Tiger Eye", "虎眼石"), "cassiterite": ("Cassiterite", "锡石"),
    "emerald": ("Emerald", "祖母绿"), "peridot": ("Peridot", "橄榄石"), "jade": ("Jade", "翡翠"),
    "malachite": ("Malachite", "孔雀石"), "chrysoprase": ("Chrysoprase", "绿玉髓"),
    "aventurine": ("Aventurine", "砂金石"), "diopside": ("Diopside", "透辉石"),
    "serpentine": ("Serpentine", "蛇纹石"), "prasiolite": ("Prasiolite", "绿水晶"),
    "prehnite": ("Prehnite", "葡萄石"), "variscite": ("Variscite", "磷铝石"),
    "brazilianite": ("Brazilianite", "巴西石"), "epidote": ("Epidote", "绿帘石"),
    "demantoid": ("Demantoid", "翠榴石"),
    "aquamarine": ("Aquamarine", "海蓝宝石"), "turquoise": ("Turquoise", "绿松石"),
    "chrysocolla": ("Chrysocolla", "硅孔雀石"), "larimar": ("Larimar", "海纹石"),
    "apatite": ("Apatite", "磷灰石"), "fluorite": ("Fluorite", "萤石"),
    "amazonite": ("Amazonite", "天河石"), "hemimorphite": ("Hemimorphite", "异极矿"),
    "alexandrite": ("Alexandrite", "变石"),
    "sapphire": ("Sapphire", "蓝宝石"), "azurite": ("Azurite", "蓝铜矿"),
    "lapis": ("Lapis Lazuli", "青金石"), "benitoite": ("Benitoite", "蓝锥矿"),
    "kyanite": ("Kyanite", "蓝晶石"), "iolite": ("Iolite", "堇青石"),
    "sodalite": ("Sodalite", "方钠石"), "tanzanite": ("Tanzanite", "坦桑石"),
    "lazulite": ("Lazulite", "蓝磷矿"), "celestine": ("Celestine", "天青石"),
    "grandidierite": ("Grandidierite", "蓝硅硼镁铝石"), "jeremejevite": ("Jeremejevite", "硼铝石"),
    "amethyst": ("Amethyst", "紫水晶"), "charoite": ("Charoite", "紫龙晶"),
    "sugilite": ("Sugilite", "苏纪石"), "taaffeite": ("Taaffeite", "塔菲石"),
    "lepidolite": ("Lepidolite", "锂云母"), "purpurite": ("Purpurite", "磷铁锰矿"),
    "axinite": ("Axinite", "斧石"), "afghanite": ("Afghanite", "阿富汗石"),
    "stichtite": ("Stichtite", "斯提克石"),
    "rubellite": ("Rubellite", "红碧玺"), "cobaltoan_calcite": ("Cobaltoan Calcite", "钴方解石"),
    "pezzottaite": ("Pezzottaite", "铯绿柱石"), "bixbite": ("Bixbite", "红绿柱石"),
    "jasper": ("Jasper", "碧玉"), "unakite": ("Unakite", "绿帘花岗岩"),
    "smoky_quartz": ("Smoky Quartz", "烟晶"), "sinhalite": ("Sinhalite", "硅硼镁铝石"),
    "aragonite": ("Aragonite", "文石"), "dolomite": ("Dolomite", "白云石"),
    "staurolite": ("Staurolite", "十字石"), "chromite": ("Chromite", "铬铁矿"),
    "painite": ("Painite", "红硅硼铝钙石"),
    "onyx": ("Onyx", "缟玛瑙"), "hematite": ("Hematite", "赤铁矿"),
    "magnetite": ("Magnetite", "磁铁矿"), "galena": ("Galena", "方铅矿"),
    "pyrite": ("Pyrite", "黄铁矿"), "marcasite": ("Marcasite", "白铁矿"),
    "moonstone": ("Moonstone", "月长石"), "opal": ("Opal", "蛋白石"), "pearl": ("Pearl", "珍珠"),
    "labradorite": ("Labradorite", "拉长石"), "rutile": ("Rutile", "金红石"),
    "molybdenite": ("Molybdenite", "辉钼矿"),
}


def parse_gems():
    """(name, (r, g, b)) rows from NIGems.java - the same table NIRecipeProvider uses."""
    src = open(JAVA, encoding='utf-8').read()
    rows = re.findall(r'\{"([a-z_]+)", "(\d+),(\d+),(\d+)"\}', src)
    gems = [(name, (int(r), int(g), int(b))) for name, r, g, b in rows]
    assert len(gems) == 100, f'expected 100 gems in NIGems.java, found {len(gems)}'
    assert len({name for name, _ in gems}) == 100, 'duplicate gem ids'
    missing = [name for name, _ in gems if name not in GEM_NAMES]
    assert not missing, f'gems without display names: {missing}'
    return gems


# ---------------------------------------------------------------- painters

# vanilla-diamond-style faceted gem: bright crown and white sparkles on the
# upper left, dark pavilion on the lower right. 1..6 shade dark->bright, W is
# the white sparkle; painted in each gem's hue.
GEM_GRID = [
    "................",
    "................",
    ".....36663......",
    "....3W66641.....",
    "...3W6666641....",
    "...3W6W666641...",
    "..3W666666641...",
    "..3W666666441...",
    "..36666664441...",
    "..36666444441...",
    "..35544444441...",
    "...1544444441...",
    "...1544444461...",
    "....13666641....",
    ".....111111.....",
    "................",
]


def make_gem_texture(dst, color):
    """A vanilla-diamond-like faceted gem recolored to the given rgb."""
    import colorsys
    h, s, v = colorsys.rgb_to_hsv(*(c / 255 for c in color))
    v = min(max(v, 0.55), 0.90)

    def shade(level, sat_scale=1.0):
        if level == 'W':
            return (250, 250, 252, 255)
        value = {  # dark -> bright
            '1': v * 0.40, '2': v * 0.55, '3': v * 0.70, '4': v * 0.85,
            '5': min(v * 1.05, 1.0), '6': max(v * 1.0, 0.93),
        }[level]
        sat = s * {'1': 1.05, '2': 1.0, '3': 1.0, '4': 0.95, '5': 0.85, '6': 0.55}[level] * sat_scale
        r, g, b = colorsys.hsv_to_rgb(h, min(sat, 1.0), min(value, 1.0))
        return (round(r * 255), round(g * 255), round(b * 255), 255)

    px = bytearray(16 * 16 * 4)
    for y, row in enumerate(GEM_GRID):
        for x, c in enumerate(row):
            o = (y * 16 + x) * 4
            if c == '.':
                continue
            px[o:o + 4] = bytes(shade(c))
    write_png(dst, 16, 16, px)


def make_rod_texture(dst, color):
    """A graphite-style rod: sheen column on the left, shade on the right."""
    px = bytearray(16 * 16 * 4)
    r, g, b = color
    body = (r, g, b)
    sheen = (min(r + 42, 255), min(g + 42, 255), min(b + 46, 255))
    shade = (int(r * 0.6), int(g * 0.6), int(b * 0.62))

    def put(x, y, c):
        o = (y * 16 + x) * 4
        px[o:o + 4] = bytes((c[0], c[1], c[2], 255))

    for y in range(1, 15):
        span = range(6, 10) if y in (1, 14) else range(5, 11)
        for x in span:
            put(x, y, sheen if x <= 6 else (shade if x >= 9 else body))
    write_png(dst, 16, 16, px)


def make_qubit_texture(dst, target_hue_deg):
    """MI's own qubit orb, hue-shifted (yellow for the optical component)."""
    import zipfile
    import colorsys
    jar = os.path.expanduser(os.path.join(
        '~/.gradle/caches/modules-2/files-2.1/maven.modrinth/modern-industrialization',
        'E1nD4PKl/7ef8099ac087509df69408f15807a34d2b82768e',
        'modern-industrialization-E1nD4PKl.jar'))
    with zipfile.ZipFile(jar) as z:
        w, h, px = read_png_stream(z.read('assets/modern_industrialization/textures/item/qubit.png'))
    target = target_hue_deg / 360.0
    out = bytearray(len(px))
    for i in range(w * h):
        o = i * 4
        a = px[o + 3]
        if a == 0:
            continue
        hh, ss, vv = colorsys.rgb_to_hsv(px[o] / 255, px[o + 1] / 255, px[o + 2] / 255)
        r, g, b = colorsys.hsv_to_rgb(target if ss > 0.06 else hh, ss, vv)
        out[o:o + 4] = bytes((round(r * 255), round(g * 255), round(b * 255), a))
    write_png(dst, w, h, out)


def read_png_stream(data):
    """Minimal PNG decoder for recoloring (mirrors gen_algae_assets.read_png)."""
    import struct
    import zlib
    pos, idat, w, h = 8, b"", 0, 0
    while pos < len(data):
        ln, typ = struct.unpack(">I", data[pos:pos+4])[0], data[pos+4:pos+8]
        chunk = data[pos+8:pos+8+ln]
        if typ == b"IHDR":
            w, h, _depth, color = struct.unpack(">IIBB", chunk[:10])
            channels = {0: 1, 2: 3, 4: 2, 6: 4}[color]
        elif typ == b"IDAT":
            idat += chunk
        pos += 12 + ln
    raw = zlib.decompress(idat)
    px = bytearray(w * h * channels)
    prev = bytearray(w * channels)
    i = 0
    for y in range(h):
        f = raw[i]
        i += 1
        line = bytearray(raw[i:i + w * channels])
        i += w * channels
        if f == 1:
            for x in range(channels, w * channels):
                line[x] = (line[x] + line[x - channels]) & 255
        elif f == 2:
            for x in range(w * channels):
                line[x] = (line[x] + prev[x]) & 255
        elif f == 3:
            for x in range(w * channels):
                left = line[x - channels] if x >= channels else 0
                line[x] = (line[x] + (left + prev[x]) // 2) & 255
        elif f == 4:
            for x in range(w * channels):
                a = line[x - channels] if x >= channels else 0
                b = prev[x]
                c = prev[x - channels] if x >= channels else 0
                p, pa, pb, pc = a + b - c, abs(b - c), abs(a - c), abs(a + b - 2 * c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[x] = (line[x] + pr) & 255
        px[y * w * channels:(y + 1) * w * channels] = line
        prev = line
    return w, h, px


def make_glow_tube(dst, color):
    """16x16 nixie-style tube: glass envelope with a glowing gas core and
    steel caps - the gem color shines through the gas."""
    px = bytearray(16 * 16 * 4)

    def put(x, y, c, a=255):
        o = (y * 16 + x) * 4
        px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], a

    def lighten(c, f):
        return tuple(min(255, int(v * f)) for v in c)

    r, g, b = color
    # steel end caps
    for x in (1, 2, 13, 14):
        for y in range(6, 10):
            put(x, y, (110, 114, 124) if x in (1, 14) else (150, 154, 164))
    # glass envelope, x 3..12
    for x in range(3, 13):
        put(x, 5, (196, 208, 220) if x % 3 else (228, 236, 244), 170)
        put(x, 10, (170, 182, 196), 140)
        for y in range(6, 10):
            edge = y in (6, 9)
            glow = lighten(color, 1.25 if y == 7 else (1.0 if y == 8 else 0.7))
            if edge:
                put(x, y, (206, 216, 228), 200)
            else:
                put(x, y, glow)
    # bright filament line and end seals
    for x in range(3, 13):
        put(x, 7, lighten(color, 1.6))
    put(3, 7, (236, 244, 252))
    put(12, 7, (236, 244, 252))
    # faint halo above/below the tube
    for x in range(5, 11):
        put(x, 4, lighten(color, 0.9), 70)
        put(x, 11, lighten(color, 0.8), 55)
    write_png(dst, 16, 16, px)


def make_battery_texture(dst):
    """16x16 RTG cell: steel case, hazard band, charge window."""
    px = bytearray(16 * 16 * 4)

    def put(x, y, c):
        o = (y * 16 + x) * 4
        px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], 255

    for y in range(2, 15):
        for x in range(4, 12):
            frame = x in (4, 11) or y in (2, 14)
            put(x, y, (70, 74, 84) if frame else (118, 122, 132))
    # terminal on top
    for y in range(0, 2):
        for x in range(6, 10):
            put(x, y, (168, 172, 182) if y else (196, 200, 210))
    # hazard stripes
    for x, y in [(5, 4), (7, 4), (9, 4), (6, 5), (8, 5), (10, 5)]:
        put(x, y, (232, 176, 40))
    # charge window with green bars
    for y in range(7, 13):
        for x in range(6, 10):
            lit = y <= 10
            put(x, y, (72, 200, 120) if lit else (36, 70, 48))
    write_png(dst, 16, 16, px)


def make_super_assembler_overlays(dst_dir):
    """Front overlay of the MI-registered machine: a dark instrument window
    with an amber tube-grid gauge (lit up in the _active variant)."""
    os.makedirs(dst_dir, exist_ok=True)

    def window(active):
        px = bytearray(16 * 16 * 4)
        amber = (255, 198, 92) if active else (176, 124, 46)
        amber_dark = (120, 80, 26)
        for y in range(4, 12):
            for x in range(3, 13):
                o = (y * 16 + x) * 4
                frame = x in (3, 12) or y in (4, 11)
                grid = x in (6, 9) or y in (7, 8)
                if frame:
                    px[o:o + 4] = bytes((56, 58, 66, 255))
                elif grid:
                    px[o:o + 4] = bytes(amber_dark + (255,))
                else:
                    px[o:o + 4] = bytes(amber + (255,))
        # corner rivets on the window frame
        for x, y in [(3, 4), (12, 4), (3, 11), (12, 11)]:
            o = (y * 16 + x) * 4
            px[o:o + 4] = bytes((120, 124, 134, 255))
        if active:
            for x, y in [(5, 6), (8, 9), (10, 5)]:
                o = (y * 16 + x) * 4
                px[o:o + 4] = bytes((255, 236, 170, 255))
        return px

    write_png(os.path.join(dst_dir, "overlay_front.png"), 16, 16, window(False))
    write_png(os.path.join(dst_dir, "overlay_front_active.png"), 16, 16, window(True))





def main():
    gems = parse_gems()
    print('gems:', len(gems))

    # 1. per-gem assets: textures, models, blockstates, loot tables
    os.makedirs(os.path.join(DST, 'item'), exist_ok=True)
    os.makedirs(os.path.join(DST, 'block'), exist_ok=True)
    loot_dir = os.path.join(DATA, 'loot_table/blocks')
    os.makedirs(loot_dir, exist_ok=True)
    for name, color in gems:
        make_gem_texture(os.path.join(DST, f"item/gem_{name}.png"), color)
        make_item_texture(os.path.join(DST, f"item/{name}_plate.png"), "mi_plate", color)
        make_glow_tube(os.path.join(DST, f"item/{name}_glow_tube.png"), color)
        for item in (f"gem_{name}", f"{name}_plate", f"{name}_glow_tube"):
            write_json(os.path.join(A, f"models/item/{item}.json"),
                       {"parent": "minecraft:item/generated",
                        "textures": {"layer0": f"{MODID}:item/{item}"}})
        # the storage block: MI-style recolored block texture + models + loot
        template_recolor(os.path.join(DST, f"block/{name}_block.png"),
                         'src/main/resources/assets/modern_industrialization/textures/block/nichrome_block.png',
                         color)
        write_json(os.path.join(A, f"blockstates/{name}_block.json"),
                   {"variants": {"": {"model": f"{MODID}:block/{name}_block"}}})
        write_json(os.path.join(A, f"models/block/{name}_block.json"),
                   {"parent": "minecraft:block/cube_all",
                    "textures": {"all": f"{MODID}:block/{name}_block"}})
        write_json(os.path.join(A, f"models/item/{name}_block.json"),
                   {"parent": f"{MODID}:block/{name}_block"})
        write_json(os.path.join(loot_dir, f"{name}_block.json"),
                   {"type": "minecraft:block",
                    "pools": [{"rolls": 1,
                               "entries": [{"type": "minecraft:item",
                                            "name": f"{MODID}:{name}_block"}],
                               "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    # 2. tube parts, the qubit and the machine
    make_battery_texture(os.path.join(DST, 'item/transuranic_battery.png'))
    make_item_texture(os.path.join(DST, 'item/crystal_diode.png'), "crystal", (150, 205, 225))
    make_item_texture(os.path.join(DST, 'item/graphene_electrode.png'), "mi_plate", (64, 68, 74))
    # the graphene chemical route's intermediates and its pressed rod
    make_item_texture(os.path.join(DST, 'item/graphene_oxide.png'), "mi_dust", (128, 96, 68))
    make_item_texture(os.path.join(DST, 'item/graphene.png'), "mi_dust", (40, 44, 50))
    make_rod_texture(os.path.join(DST, 'item/graphene_rod.png'), (52, 56, 63))
    # MI's own qubit orb, recolored yellow for the optical component
    make_qubit_texture(os.path.join(DST, 'item/optical_qubit_component.png'), 47)
    items = ("transuranic_battery", "crystal_diode", "graphene_electrode",
             "graphene_oxide", "graphene", "graphene_rod", "optical_qubit_component")
    for item in items:
        write_json(os.path.join(A, f"models/item/{item}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{item}"}})
    # the super assembler: a real MI machine (modern_industrialization ns),
    # so its assets are overlays on the lv casing + machine-model jsons
    make_super_assembler_overlays(os.path.join(ROOT,
        'src/main/resources/assets/modern_industrialization/textures/block/machines/super_assembler'))
    AMI = os.path.join(ROOT, 'src/main/resources/assets/modern_industrialization')
    write_json(os.path.join(AMI, 'blockstates/super_assembler.json'),
               {"variants": {"": {"model": "modern_industrialization:block/super_assembler"}}})
    write_json(os.path.join(AMI, 'models/block/super_assembler.json'),
               {"loader": "modern_industrialization:machine",
                "casing": "lv",
                "default_overlays": {
                    "fluid_auto": "modern_industrialization:block/overlays/fluid_auto",
                    "front": "modern_industrialization:block/machines/super_assembler/overlay_front",
                    "front_active": "modern_industrialization:block/machines/super_assembler/overlay_front_active",
                    "item_auto": "modern_industrialization:block/overlays/item_auto",
                    "output": "modern_industrialization:block/overlays/output"}})
    write_json(os.path.join(AMI, 'models/item/super_assembler.json'),
               {"parent": "modern_industrialization:block/super_assembler"})

    # loot tables: the super assembler plus our other four MI-registered
    # machines, which were missing theirs (they dropped nothing when broken)
    DAMI = os.path.join(ROOT, 'src/main/resources/data/modern_industrialization/loot_table/blocks')
    for machine in ("super_assembler", "algae_cultivator", "ion_exchange", "magma_crucible", "super_mixer"):
        write_json(os.path.join(DAMI, f"{machine}.json"),
                   {"type": "minecraft:block",
                    "pools": [{"rolls": 1,
                               "entries": [{"type": "minecraft:item",
                                            "name": f"modern_industrialization:{machine}"}],
                               "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    # the five spectral assemblies were folded back into the single 10x10
    # recipe; nothing extra to emit here

    # 3. noble gases + condensed xenon: the fluid four-piece
    gases = [("neon", "Neon", "氖", 0xFFFF5F42),
             ("argon", "Argon", "氩", 0xFFAA8CFF),
             ("krypton", "Krypton", "氪", 0xFF96C8FF),
             ("liquid_xenon", "Liquid Xenon", "液态氙", 0xFF64A0D7)]
    for fluid_id, _en, _zh, tint in gases:
        write_json(os.path.join(A, f"blockstates/{fluid_id}.json"), fluid_blockstate(fluid_id))
        write_json(os.path.join(A, f"models/block/{fluid_id}.json"),
                   {"parent": "minecraft:block/water"})
        write_json(os.path.join(A, f"models/item/{fluid_id}_bucket.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{fluid_id}_bucket"}})
        make_bucket_texture(os.path.join(A, f"textures/item/{fluid_id}_bucket.png"),
                            ((tint >> 16) & 255, (tint >> 8) & 255, tint & 255))

    # 4. lang
    en_path = os.path.join(A, 'lang/en_us.json')
    zh_path = os.path.join(A, 'lang/zh_cn.json')
    en = json.load(open(en_path, encoding='utf-8'))
    zh = json.load(open(zh_path, encoding='utf-8'))
    for name, _color in gems:
        gem_en, gem_zh = GEM_NAMES[name]
        en[f"item.{MODID}.gem_{name}"] = gem_en
        zh[f"item.{MODID}.gem_{name}"] = gem_zh
        en[f"block.{MODID}.{name}_block"] = f"Block of {gem_en}"
        zh[f"block.{MODID}.{name}_block"] = f"{gem_zh}块"
        en[f"item.{MODID}.{name}_plate"] = f"{gem_en} Plate"
        zh[f"item.{MODID}.{name}_plate"] = f"{gem_zh}板"
        en[f"item.{MODID}.{name}_glow_tube"] = f"{gem_en} Glow Tube"
        zh[f"item.{MODID}.{name}_glow_tube"] = f"{gem_zh}辉光管"
    parts = [("transuranic_battery", "Transuranic Battery", "超铀电池"),
             ("crystal_diode", "Crystal Diode", "晶体二极管"),
             ("graphene_electrode", "Graphene Electrode", "石墨烯电极"),
             ("graphene_oxide", "Graphene Oxide", "氧化石墨烯"),
             ("graphene", "Graphene", "石墨烯"),
             ("graphene_rod", "Graphene Rod", "石墨烯杆"),
             ("optical_qubit_component", "Optical Qubit Component", "光学量子比特组件")]
    for item_id, item_en, item_zh in parts:
        en[f"item.{MODID}.{item_id}"] = item_en
        zh[f"item.{MODID}.{item_id}"] = item_zh
    # the machine is MI-registered, so its keys live in MI's namespace
    for key in (f"block.modern_industrialization.super_assembler",
                f"item.modern_industrialization.super_assembler",
                f"rei_categories.modern_industrialization.super_assembler"):
        en[key] = "Super Assembler"
        zh[key] = "超级组装机"
    en.pop(f"block.{MODID}.super_assembler", None)
    zh.pop(f"block.{MODID}.super_assembler", None)
    en.pop(f"emi.category.{MODID}.super_assembler", None)
    zh.pop(f"emi.category.{MODID}.super_assembler", None)
    for fluid_id, fluid_en, fluid_zh, _tint in gases:
        en[f"fluid.{MODID}.{fluid_id}"] = fluid_en
        en[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_en} Bucket"
        zh[f"fluid.{MODID}.{fluid_id}"] = fluid_zh
        zh[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_zh}桶"
    write_json(en_path, en)
    write_json(zh_path, zh)
    print('optical assets done: 100 gems, 4 fluids, graphene route, MI super assembler')


if __name__ == '__main__':
    main()
