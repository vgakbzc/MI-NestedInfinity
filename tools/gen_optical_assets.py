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


def make_machine_textures(dst_dir):
    """Super assembler faces: steel casing with amber conduits feeding a
    10x10 socket grid on the top."""
    os.makedirs(dst_dir, exist_ok=True)

    def base(px):
        import random
        rng = random.Random(0x50A)
        for y in range(16):
            for x in range(16):
                o = (y * 16 + x) * 4
                frame = x == 0 or y == 0 or x == 15 or y == 15
                v = rng.random()
                if frame:
                    c = (52, 48, 44)
                elif v < 0.10:
                    c = (106, 100, 92)
                else:
                    c = (88, 82, 74)
                px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], 255

    def put(px, x, y, c):
        o = (y * 16 + x) * 4
        px[o], px[o+1], px[o+2] = c

    amber, amber_dark, amber_light = (240, 170, 60), (156, 104, 30), (255, 220, 140)
    # side: three amber energy conduits with junction dots
    side = bytearray(16 * 16 * 4)
    base(side)
    for cx in (4, 8, 12):
        for y in range(2, 15):
            put(side, cx, y, amber if (y // 2) % 2 == 0 else amber_dark)
        put(side, cx, 2, amber_light)
    write_png(os.path.join(dst_dir, "super_assembler_side.png"), 16, 16, side)
    # top: a miniature 3x3-preview of the tube grid, amber framed
    top = bytearray(16 * 16 * 4)
    base(top)
    for y in range(3, 13):
        for x in range(3, 13):
            edge = x in (3, 12) or y in (3, 12) or x in (7, 8) or y in (7, 8)
            put(top, x, y, amber if edge else (34, 32, 30))
    for x, y in [(1, 1), (14, 1), (1, 14), (14, 14)]:
        put(top, x, y, amber_light)
    write_png(os.path.join(dst_dir, "super_assembler_top.png"), 16, 16, top)
    # bottom: vents
    bottom = bytearray(16 * 16 * 4)
    base(bottom)
    for x in range(3, 13):
        for y in (5, 8, 11):
            put(bottom, x, y, (40, 38, 34))
    write_png(os.path.join(dst_dir, "super_assembler_bottom.png"), 16, 16, bottom)


def make_super_assembler_gui(dst):
    """256x320 sheet holding a 244x302 panel: title row, the 10x10 tube grid,
    an arrow into the output slot, and the player inventory. Coordinates mirror
    SuperAssemblerMenu."""
    SHEET_W, SHEET_H = 256, 320
    W, H = 244, 302
    px = bytearray(SHEET_W * SHEET_H * 4)

    def put(x, y, rgb):
        if 0 <= x < W and 0 <= y < H:
            i = (y * SHEET_W + x) * 4
            px[i], px[i+1], px[i+2], px[i+3] = rgb[0], rgb[1], rgb[2], 255

    for y in range(H):
        for x in range(W):
            put(x, y, (198, 198, 198))
    for i in range(W):
        put(i, 0, (85, 85, 85))
        put(i, H - 1, (85, 85, 85))
    for j in range(H):
        put(0, j, (85, 85, 85))
        put(W - 1, j, (85, 85, 85))

    def slot_inset(x, y, w=18, h=18):
        for yy in range(y, y + h):
            for xx in range(x, x + w):
                put(xx, yy, (139, 139, 139))
        for k in range(w):
            put(x + k, y, (55, 55, 55))
            put(x + k, y + h - 1, (255, 255, 255))
        for k in range(h):
            put(x, y + k, (55, 55, 55))
            put(x + w - 1, y + k, (255, 255, 255))

    # the 10x10 glow-tube grid
    for row in range(10):
        for col in range(10):
            slot_inset(8 + col * 18, 18 + row * 18)
    # amber arrow into the output slot
    for dx in range(16):
        half = max(0, 6 - dx // 2)
        for dy in range(-half, half + 1):
            put(194 + dx, 107 + dy, (196, 132, 40))
    slot_inset(214, 99)
    # player inventory, centered under the grid
    for row in range(3):
        for col in range(9):
            slot_inset(41 + col * 18, 216 + row * 18)
    for col in range(9):
        slot_inset(41 + col * 18, 278)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    write_png(dst, SHEET_W, SHEET_H, px)


# ---------------------------------------------------------------- main


def main():
    gems = parse_gems()
    print('gems:', len(gems))

    # 1. per-gem assets: textures, models, blockstates, loot tables
    os.makedirs(os.path.join(DST, 'item'), exist_ok=True)
    os.makedirs(os.path.join(DST, 'block'), exist_ok=True)
    loot_dir = os.path.join(DATA, 'loot_table/blocks')
    os.makedirs(loot_dir, exist_ok=True)
    for name, color in gems:
        make_item_texture(os.path.join(DST, f"item/gem_{name}.png"), "crystal", color)
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
    make_item_texture(os.path.join(DST, 'item/optical_qubit_component.png'), "crystal", (250, 215, 120))
    for item in ("transuranic_battery", "crystal_diode", "graphene_electrode", "optical_qubit_component"):
        write_json(os.path.join(A, f"models/item/{item}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{item}"}})
    make_machine_textures(os.path.join(DST, 'block'))
    write_json(os.path.join(A, 'blockstates/super_assembler.json'),
               {"variants": {"": {"model": f"{MODID}:block/super_assembler"}}})
    write_json(os.path.join(A, 'models/block/super_assembler.json'),
               {"parent": "minecraft:block/cube_bottom_top",
                "textures": {"top": f"{MODID}:block/super_assembler_top",
                             "bottom": f"{MODID}:block/super_assembler_bottom",
                             "side": f"{MODID}:block/super_assembler_side"}})
    write_json(os.path.join(A, 'models/item/super_assembler.json'),
               {"parent": f"{MODID}:block/super_assembler"})
    write_json(os.path.join(loot_dir, 'super_assembler.json'),
               {"type": "minecraft:block",
                "pools": [{"rolls": 1,
                           "entries": [{"type": "minecraft:item", "name": f"{MODID}:super_assembler"}],
                           "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    make_super_assembler_gui(os.path.join(A, 'textures/gui/super_assembler.png'))

    # 3. noble gases: the fluid four-piece
    gases = [("neon", "Neon", "氖", 0xFFFF5F42),
             ("argon", "Argon", "氩", 0xFFAA8CFF),
             ("krypton", "Krypton", "氪", 0xFF96C8FF)]
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
             ("optical_qubit_component", "Optical Qubit Component", "光学量子比特组件")]
    for item_id, item_en, item_zh in parts:
        en[f"item.{MODID}.{item_id}"] = item_en
        zh[f"item.{MODID}.{item_id}"] = item_zh
    en[f"block.{MODID}.super_assembler"] = "Super Assembler"
    zh[f"block.{MODID}.super_assembler"] = "超级组装机"
    for fluid_id, fluid_en, fluid_zh, _tint in gases:
        en[f"fluid.{MODID}.{fluid_id}"] = fluid_en
        en[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_en} Bucket"
        zh[f"fluid.{MODID}.{fluid_id}"] = fluid_zh
        zh[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_zh}桶"
    en[f"emi.category.{MODID}.super_assembler"] = "Super Assembler"
    zh[f"emi.category.{MODID}.super_assembler"] = "超级组装机"
    write_json(en_path, en)
    write_json(zh_path, zh)
    print('optical assets done: 100 gems, 3 gases, super assembler')


if __name__ == '__main__':
    main()
