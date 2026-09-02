#!/usr/bin/env python3
"""Microverse projector assets: the neutronium machine casing, the twelve
coreflame blocks, the nine time dilation units, the projector controller,
the heart / twelve singularities / nine universe matters, both GUI
backgrounds, plus models, blockstates, loot tables and bilingual lang.

Mirrors the registration in com.nestedinfinity.mod.microverse.

Run from the repo root:  python tools/gen_microverse_assets.py
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from gen_algae_assets import A, MODID, ROOT, write_json, write_png

DATA = os.path.join(ROOT, 'src/main/resources/data', MODID)
TX = os.path.join(A, 'textures')
BLOCK_TX = os.path.join(TX, 'block')
ITEM_TX = os.path.join(TX, 'item')
GUI_TX = os.path.join(TX, 'gui')

# 12 coreflames: block suffix, singularity key, en, zh, flame color
COREFLAMES = [
    ("chrysalis_of_gold", "gold", "Chrysalis of Gold", "黄金之茧", (240, 196, 80)),
    ("bough_of_rift", "rift", "Bough of Rift", "裂隙之枝", (120, 80, 200)),
    ("hand_of_shadow", "shadow", "Hand of Shadow", "暗影之手", (70, 56, 88)),
    ("scale_of_justice", "justice", "Scale of Justice", "正义之秤", (210, 214, 220)),
    ("coin_of_whimsy", "whimsy", "Coin of Whimsy", "狂想之币", (250, 140, 190)),
    ("chalice_of_plenty", "plenty", "Chalice of Plenty", "丰饶之杯", (110, 210, 120)),
    ("eye_of_twilight", "twilight", "Eye of Twilight", "暮光之眼", (160, 110, 220)),
    ("throne_of_worlds", "worlds", "Throne of Worlds", "世界王座", (90, 140, 220)),
    ("lance_of_fury", "fury", "Lance of Fury", "狂怒之枪", (230, 80, 60)),
    ("pillar_of_stone", "stone", "Pillar of Stone", "磐石之柱", (150, 150, 140)),
    ("veil_of_evernight", "evernight", "Veil of Evernight", "永夜之幕", (50, 60, 110)),
    ("gate_of_infinity", "infinity", "Gate of Infinity", "无限之门", (80, 220, 200)),
]

# singularity display names (en, zh) in the same order
SINGULARITY_NAMES = [
    ("Singularity of Gold", "黄金奇点"),
    ("Singularity of Rift", "裂隙奇点"),
    ("Singularity of Shadow", "暗影奇点"),
    ("Singularity of Justice", "正义奇点"),
    ("Singularity of Whimsy", "狂想奇点"),
    ("Singularity of Plenty", "丰饶奇点"),
    ("Singularity of Twilight", "暮光奇点"),
    ("Singularity of Worlds", "世界奇点"),
    ("Singularity of Fury", "狂怒奇点"),
    ("Singularity of Stone", "磐石奇点"),
    ("Singularity of Evernight", "永夜奇点"),
    ("Singularity of Infinity", "无限奇点"),
]

# 9 universe matters: id, en, zh, color
MATTERS = [
    ("quark_gluon_plasma", "Quark-Gluon Plasma", "夸克胶子等离子体", (255, 150, 60)),
    ("hadronic_matter", "Hadronic Matter", "强子物质", (255, 120, 100)),
    ("primordial_hydrogen_helium", "Primordial Hydrogen-Helium", "原初氢氦", (250, 200, 120)),
    ("recombined_atomic_gas", "Recombined Atomic Gas", "复合原子气体", (210, 230, 150)),
    ("dark_matter_halo", "Dark Matter Halo", "暗物质晕", (120, 130, 160)),
    ("population_iii_stellar_matter", "Population III Stellar Matter", "第三族恒星物质", (150, 200, 255)),
    ("early_galactic_matter", "Early Galactic Matter", "早期星系物质", (170, 150, 240)),
    ("supernova_heavy_elements", "Supernova Heavy Elements", "超新星重元素", (240, 120, 180)),
    ("kilonova_ejecta", "Kilonova Ejecta", "千新星抛射物", (200, 140, 255)),
]


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c)


class Canvas:
    """A flat RGBA byte canvas sized for one texture."""

    def __init__(self, w, h):
        self.w, self.h = w, h
        self.buf = bytearray(w * h * 4)

    def px(self, x, y, c, a=255):
        if 0 <= x < self.w and 0 <= y < self.h:
            i = (y * self.w + x) * 4
            self.buf[i], self.buf[i + 1], self.buf[i + 2], self.buf[i + 3] = \
                c[0], c[1], c[2], a

    def fill(self, c):
        for y in range(self.h):
            for x in range(self.w):
                self.px(x, y, c)

    def border(self, c):
        for i in range(self.w):
            self.px(i, 0, shade(c, 1.25))
            self.px(i, self.h - 1, shade(c, 0.6))
        for i in range(self.h):
            self.px(0, i, shade(c, 1.15))
            self.px(self.w - 1, i, shade(c, 0.7))

    def save(self, path):
        write_png(path, self.w, self.h, self.buf)


def make_casing(canvas):
    """Neutronium machine casing: violet-gray plate, seams, rivets."""
    base = (96, 90, 108)
    canvas.fill(base)
    for x in range(canvas.w):
        f = 1.0 + (0.04 if x % 4 < 2 else -0.04)
        for y in range(canvas.h):
            i = (y * canvas.w + x) * 4
            c = shade(base, f)
            canvas.buf[i], canvas.buf[i + 1], canvas.buf[i + 2] = c
    for i in range(2, 14):
        canvas.px(i, 2, shade(base, 0.55))
        canvas.px(i, 13, shade(base, 0.55))
        canvas.px(2, i, shade(base, 0.55))
        canvas.px(13, i, shade(base, 0.55))
    for rx, ry in ((4, 4), (11, 4), (4, 11), (11, 11)):
        canvas.px(rx, ry, shade(base, 1.6))
        canvas.px(rx + 1, ry, shade(base, 1.3))
        canvas.px(rx, ry + 1, shade(base, 1.3))
        canvas.px(rx + 1, ry + 1, shade(base, 0.5))
    canvas.border(base)


def make_coreflame(canvas, color):
    """Dark frame with a burning diamond core of the flame's color."""
    frame = (52, 46, 62)
    canvas.fill(frame)
    canvas.border(frame)
    for r, col in ((5, shade(color, 0.35)), (4, shade(color, 0.6)), (3, shade(color, 0.85)),
                   (2, color), (1, shade(color, 1.3))):
        for dy in range(-r, r + 1):
            for dx in range(-r, r + 1):
                if abs(dx) + abs(dy) <= r:
                    canvas.px(8 + dx, 8 + dy, col)
    canvas.px(8, 8, (255, 255, 255))
    canvas.px(7, 8, shade(color, 1.8))
    canvas.px(8, 7, shade(color, 1.8))


TDU_RAMP = [
    (90, 140, 220), (90, 180, 190), (120, 200, 140), (190, 200, 100),
    (235, 190, 90), (240, 150, 120), (230, 120, 170), (200, 110, 230), (240, 240, 250),
]


def make_tdu(canvas, tier):
    """Casing with a clock dial; hand length and rim hue climb with tier."""
    color = TDU_RAMP[tier - 1]
    base = (88, 82, 100)
    canvas.fill(base)
    canvas.border(base)
    for dy in range(-5, 6):
        for dx in range(-5, 6):
            d = (dx * dx + dy * dy) ** 0.5
            if 4.2 <= d <= 5.4:
                canvas.px(8 + dx, 8 + dy, shade(color, 0.8))
            elif d <= 3.8:
                canvas.px(8 + dx, 8 + dy, (30, 28, 38))
    for step in range(1 + tier // 2):
        canvas.px(8 + step, 8 - step, shade(color, 1.4))
    canvas.px(8, 8, (255, 255, 255))
    for t in range(tier):
        canvas.px(3 + t, 13, shade(color, 1.2))


def make_controller_front(canvas, on):
    base = (96, 90, 108)
    canvas.fill(base)
    canvas.border(base)
    x0, y0, x1, y1 = (3, 3, 12, 12) if on else (4, 4, 11, 11)
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            swirl = ((x * 7 + y * 13) % 5) / 4.0
            if on:
                c = (int(40 + swirl * 180), int(20 + swirl * 60), int(90 + swirl * 150))
            else:
                c = (int(18 + swirl * 30), int(12 + swirl * 18), int(34 + swirl * 44))
            canvas.px(x, y, c)
    stars = ((5, 6), (9, 4), (11, 9), (6, 10), (8, 8)) if on else ((5, 6), (10, 9))
    star_c = (240, 230, 255) if on else (90, 80, 110)
    for sx, sy in stars:
        canvas.px(sx, sy, star_c)


def make_controller_side(canvas):
    make_casing(canvas)
    for y in range(3, 13):
        canvas.px(7, y, (150, 90, 220))
        canvas.px(8, y, (110, 60, 180))


def make_singularity(canvas, color):
    """A dark orb with the flame color's event-horizon ring and white core."""
    for y in range(16):
        for x in range(16):
            d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if d <= 6.5:
                if d <= 2.2:
                    c = (255, 255, 255)
                elif d <= 3.4:
                    c = shade(color, 1.5)
                elif d <= 4.8:
                    c = shade(color, 0.5)
                else:
                    c = shade(color, 0.22)
                canvas.px(x, y, c)
    for sx, sy in ((2, 7), (13, 8), (7, 2), (8, 13)):
        canvas.px(sx, sy, shade(color, 1.8))


def make_heart(canvas):
    """Void-purple heart with the nonexistent star at its core."""
    shape = [
        (4, 3), (5, 3), (8, 3), (9, 3),
        (3, 4), (4, 4), (5, 4), (6, 4), (7, 4), (8, 4), (9, 4), (10, 4),
        (3, 5), (4, 5), (5, 5), (6, 5), (7, 5), (8, 5), (9, 5), (10, 5),
        (3, 6), (4, 6), (5, 6), (6, 6), (7, 6), (8, 6), (9, 6), (10, 6),
        (4, 7), (5, 7), (6, 7), (7, 7), (8, 7), (9, 7),
        (5, 8), (6, 8), (7, 8), (8, 8),
        (6, 9), (7, 9),
        (6, 10), (7, 10),
    ]
    base, edge = (150, 80, 220), (90, 40, 140)
    occupied = set(shape)
    for x, y in shape:
        canvas.px(x, y, base)
    for x, y in shape:
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            if (x + dx, y + dy) not in occupied:
                canvas.px(x + dx, y + dy, edge)
    for sx, sy in ((6, 5), (7, 5), (6, 6), (7, 6), (5, 5), (8, 6)):
        canvas.px(sx, sy, (245, 235, 255))
    canvas.px(5, 4, (210, 190, 240))
    canvas.px(8, 5, (210, 190, 240))


def make_matter(canvas, color, seed):
    """Universe matter: an irregular glowing cluster with star specks."""
    import random
    rng = random.Random(seed)
    blobs = [(8, 8, 4.2), (5, 10, 2.4), (11, 6, 2.2), (10, 11, 1.8), (6, 5, 1.6)]
    for y in range(16):
        for x in range(16):
            d = min(((x - bx) ** 2 + (y - by) ** 2) ** 0.5 / br for bx, by, br in blobs)
            if d <= 1.0:
                canvas.px(x, y, shade(color, 1.5 - d * 1.15))
    for _ in range(6):
        sx, sy = rng.randint(3, 12), rng.randint(3, 12)
        i = (sy * 16 + sx) * 4
        if canvas.buf[i + 3]:
            canvas.px(sx, sy, (255, 255, 255))


def gui_slot_well(canvas, x, y):
    """An 18x18 vanilla-style slot well at (x, y) (one pixel up-left of the 16x16 item)."""
    for yy in range(y, y + 18):
        for xx in range(x, x + 18):
            if xx == x or yy == y:
                canvas.px(xx, yy, (55, 55, 55))
            elif xx == x + 17 or yy == y + 17:
                canvas.px(xx, yy, (255, 255, 255))
            else:
                canvas.px(xx, yy, (139, 139, 139))


def inventory_wells(y_top):
    """Slot wells for the 3x9 player inventory at y_top and the hotbar 4 rows down."""
    return [(7 + c * 18, y_top + r * 18) for r in range(3) for c in range(9)] + \
           [(7 + c * 18, y_top + 58) for c in range(9)]


def make_gui(path, height, slots, tint=None):
    # The 8-arg GuiGraphics.blit samples as if the source texture were
    # 256x256, so the panel must sit in the top-left corner of a 256x256
    # canvas (same as the resonance attuner texture); the rest stays
    # transparent. A tint blends the gray body toward a coreflame color.
    if tint is None:
        body = (198, 198, 198)
    else:
        body = tuple(int(g * 0.6 + t * 0.4) for g, t in zip((198, 198, 198), tint))
    light, dark = shade(body, 1.25), shade(body, 0.6)
    canvas = Canvas(256, 256)
    for y in range(height):
        for x in range(176):
            canvas.px(x, y, body)
    # beveled panel edge: light top/left, dark bottom/right
    for x in range(176):
        canvas.px(x, 0, light)
        canvas.px(x, height - 1, dark)
    for y in range(height):
        canvas.px(0, y, light)
        canvas.px(175, y, dark)
    for x, y in slots:
        gui_slot_well(canvas, x, y)
    canvas.save(path)


def make_creative_source(canvas):
    """Creative energy source: dark housing with a blazing golden core."""
    canvas.fill((52, 48, 60))
    canvas.border((52, 48, 60))
    for y in range(16):
        for x in range(16):
            d = abs(x - 7.5) + abs(y - 7.5)
            if d <= 2:
                canvas.px(x, y, (255, 255, 224))
            elif d <= 4:
                canvas.px(x, y, (255, 214, 90))
            elif d <= 6:
                canvas.px(x, y, (200, 140, 50))
    for x, y in ((2, 7), (13, 7), (7, 2), (7, 13), (4, 4), (11, 11), (4, 11), (11, 4)):
        canvas.px(x, y, (255, 236, 160))


def loot(name):
    write_json(os.path.join(DATA, 'loot_table', 'blocks', name + '.json'), {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1,
            "entries": [{"type": "minecraft:item", "name": MODID + ":" + name}],
            "conditions": [{"condition": "minecraft:survives_explosion"}],
        }],
    })


def block_model(name, texture=None):
    write_json(os.path.join(A, 'models', 'block', name + '.json'), {
        "parent": "minecraft:block/cube_all",
        "textures": {"all": MODID + ":block/" + (texture or name)},
    })


def coreflame_block_model(name):
    """The brazier is 0.6 blocks tall (9.6px). Its sides wear the neutronium
    machine casing texture (the structure's skin), sampled from the texture's
    center band to keep proportions; the top face shows the full flame
    diamond of this flame's own texture."""
    casing_uv = [0, 3.2, 16, 12.8]
    write_json(os.path.join(A, 'models', 'block', name + '.json'), {
        "textures": {
            "flame": MODID + ":block/" + name,
            "casing": MODID + ":block/neutronium_machine_casing",
            "particle": MODID + ":block/neutronium_machine_casing",
        },
        "elements": [{
            "from": [0, 0, 0],
            "to": [16, 9.6, 16],
            "faces": {
                "north": {"uv": casing_uv, "texture": "#casing"},
                "south": {"uv": casing_uv, "texture": "#casing"},
                "west": {"uv": casing_uv, "texture": "#casing"},
                "east": {"uv": casing_uv, "texture": "#casing"},
                "up": {"uv": [0, 0, 16, 16], "texture": "#flame"},
                "down": {"uv": [0, 0, 16, 16], "texture": "#casing"},
            },
        }],
    })


def blockstate(name, model=None):
    write_json(os.path.join(A, 'blockstates', name + '.json'), {
        "variants": {"": {"model": MODID + ":block/" + (model or name)}},
    })


def block_item_model(name):
    write_json(os.path.join(A, 'models', 'item', name + '.json'), {
        "parent": MODID + ":block/" + name,
    })


def item_model(name):
    write_json(os.path.join(A, 'models', 'item', name + '.json'), {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": MODID + ":item/" + name},
    })


def save_block_texture(name, painter, *args):
    canvas = Canvas(16, 16)
    painter(canvas, *args)
    canvas.save(os.path.join(BLOCK_TX, name + '.png'))


def save_item_texture(name, painter, *args):
    canvas = Canvas(16, 16)
    painter(canvas, *args)
    canvas.save(os.path.join(ITEM_TX, name + '.png'))


def main():
    for d in (BLOCK_TX, ITEM_TX, GUI_TX, os.path.join(A, 'models', 'block'),
              os.path.join(A, 'models', 'item'), os.path.join(A, 'blockstates'),
              os.path.join(DATA, 'loot_table', 'blocks')):
        os.makedirs(d, exist_ok=True)

    # casing
    save_block_texture('neutronium_machine_casing', make_casing)
    block_model('neutronium_machine_casing')
    blockstate('neutronium_machine_casing')
    block_item_model('neutronium_machine_casing')
    loot('neutronium_machine_casing')

    # coreflames + singularities
    for i, (suffix, key, _en, _zh, color) in enumerate(COREFLAMES):
        name = 'coreflame_' + suffix
        save_block_texture(name, make_coreflame, color)
        coreflame_block_model(name)
        blockstate(name)
        block_item_model(name)
        loot(name)
        save_item_texture('singularity_' + key, make_singularity, color)
        item_model('singularity_' + key)

    # time dilation units
    for tier in range(1, 10):
        name = 'time_dilation_unit_t%d' % tier
        save_block_texture(name, make_tdu, tier)
        block_model(name)
        blockstate(name)
        block_item_model(name)
        loot(name)

    # projector controller: all four sides share one texture, the distinct
    # "eye" texture sits on the top face (running variant swaps it for the
    # lit one)
    save_block_texture('microverse_projector_front', make_controller_front, False)
    save_block_texture('microverse_projector_front_on', make_controller_front, True)
    save_block_texture('microverse_projector_side', make_controller_side)
    for variant, front in (('', 'front'), ('_on', 'front_on')):
        write_json(os.path.join(A, 'models', 'block', 'microverse_projector' + variant + '.json'), {
            "parent": "minecraft:block/cube",
            "textures": {
                "particle": MODID + ":block/microverse_projector_side",
                "down": MODID + ":block/neutronium_machine_casing",
                "up": MODID + ":block/microverse_projector_" + front,
                "north": MODID + ":block/microverse_projector_side",
                "east": MODID + ":block/microverse_projector_side",
                "south": MODID + ":block/microverse_projector_side",
                "west": MODID + ":block/microverse_projector_side",
            },
        })
    write_json(os.path.join(A, 'blockstates', 'microverse_projector.json'), {
        "variants": {
            "running=false": {"model": MODID + ":block/microverse_projector"},
            "running=true": {"model": MODID + ":block/microverse_projector_on"},
        },
    })
    block_item_model('microverse_projector')
    loot('microverse_projector')

    # items: heart + matters
    save_item_texture('heart_of_a_nonexistent_world', make_heart)
    item_model('heart_of_a_nonexistent_world')
    for i, (mid, _en, _zh, color) in enumerate(MATTERS):
        save_item_texture(mid, make_matter, color, i)
        item_model(mid)

    # GUI backgrounds (slot positions mirror the two menus: coreflame input
    # slot (44,35) and return slot (116,35) -> wells (43,34)/(115,34); player
    # inventory starts at y=84 for the coreflame and y=102 for the projector).
    # Each coreflame also gets its own flame-tinted background (CoreflameScreen
    # picks textures/gui/coreflame_<suffix>.png).
    coreflame_slots = [(43, 34), (115, 34)] + inventory_wells(83)
    make_gui(os.path.join(GUI_TX, 'coreflame.png'), 166, coreflame_slots)
    for suffix, _key, _en, _zh, color in COREFLAMES:
        make_gui(os.path.join(GUI_TX, 'coreflame_' + suffix + '.png'), 166,
                 coreflame_slots, tint=color)
    # projector: only the heart slot (8,26) is exposed; balls come from item
    # input hatches, the matter leaves through item output hatches
    make_gui(os.path.join(GUI_TX, 'microverse_projector.png'), 184,
             [(7, 25)] + inventory_wells(101))

    # creative energy source (creative-only, no recipe)
    save_block_texture('creative_energy_source', make_creative_source)
    block_model('creative_energy_source')
    blockstate('creative_energy_source')
    block_item_model('creative_energy_source')
    loot('creative_energy_source')

    write_lang()
    print('microverse assets written')


def write_lang():
    en_path = os.path.join(A, 'lang/en_us.json')
    zh_path = os.path.join(A, 'lang/zh_cn.json')
    en = json.load(open(en_path, encoding='utf-8'))
    zh = json.load(open(zh_path, encoding='utf-8'))

    for (suffix, key, en_flame, zh_flame, _c), (en_sing, zh_sing) in zip(COREFLAMES, SINGULARITY_NAMES):
        en['block.%s.coreflame_%s' % (MODID, suffix)] = 'Coreflame: ' + en_flame
        zh['block.%s.coreflame_%s' % (MODID, suffix)] = '创世火种·' + zh_flame
        en['item.%s.singularity_%s' % (MODID, key)] = en_sing
        zh['item.%s.singularity_%s' % (MODID, key)] = zh_sing
    for tier in range(1, 10):
        en['block.%s.time_dilation_unit_t%d' % (MODID, tier)] = 'Time Dilation Unit Mk.%d' % tier
        zh['block.%s.time_dilation_unit_t%d' % (MODID, tier)] = '时间膨胀单元 Mk.%d' % tier
    en['block.%s.neutronium_machine_casing' % MODID] = 'Neutronium Machine Casing'
    zh['block.%s.neutronium_machine_casing' % MODID] = '中子素机器外壳'
    en['block.%s.microverse_projector' % MODID] = 'Microverse Projector'
    zh['block.%s.microverse_projector' % MODID] = '微缩宇宙投影仪'
    en['block.%s.creative_energy_source' % MODID] = 'Creative Energy Source'
    zh['block.%s.creative_energy_source' % MODID] = '创造发电机'
    en['item.%s.heart_of_a_nonexistent_world' % MODID] = 'Heart of a Nonexistent World'
    zh['item.%s.heart_of_a_nonexistent_world' % MODID] = '创世之心'
    for mid, en_name, zh_name, _c in MATTERS:
        en['item.%s.%s' % (MODID, mid)] = en_name
        zh['item.%s.%s' % (MODID, mid)] = zh_name

    gui = 'container.%s.microverse_projector.' % MODID
    en[gui + 'ball_time'] = 'Matter ball: +%ss'
    zh[gui + 'ball_time'] = '物质球延长：+%s 秒'
    en[gui + 'running'] = 'Projecting: %s'
    zh[gui + 'running'] = '投影中：%s'
    en[gui + 'ready'] = 'Structure ready — insert heart + 12 singularities'
    zh[gui + 'ready'] = '结构就绪 — 放入创世之心与 12 奇点'
    en[gui + 'countdown'] = 'Collapse in %ss'
    zh[gui + 'countdown'] = '%s 秒后坍缩'
    en[gui + 'accrued'] = 'Accrued: %s'
    zh[gui + 'accrued'] = '已积累:%s'
    en[gui + 'return_chance'] = 'Singularity return: %s%%'
    zh[gui + 'return_chance'] = '奇点返还率:%s%%'
    en[gui + 'output_full'] = 'Output hatch full — paused (half-speed collapse)'
    zh[gui + 'output_full'] = '输出仓已满——已暂停（坍缩半速）'
    en[gui + 'slot_heart'] = 'Heart of a Nonexistent World goes here'
    zh[gui + 'slot_heart'] = '在此放入创世之心'
    en['container.%s.coreflame.slot_input' % MODID] = 'This flame accepts only its own singularity'
    zh['container.%s.coreflame.slot_input' % MODID] = '本火种只接受自己的奇点'
    en['container.%s.coreflame.slot_return' % MODID] = 'Returned singularities appear here'
    zh['container.%s.coreflame.slot_return' % MODID] = '返还的奇点会出现在这里'
    # keys retired with the extend button and the ball/output slots
    for dead in ('extend', 'slot_balls', 'slot_output'):
        en.pop(gui + dead, None)
        zh.pop(gui + dead, None)
    problems_en = {
        'unchecked': 'structure not checked yet',
        'layer1': 'bottom layer incomplete',
        'layer2_center': 'layer 2 center missing casings',
        'layer3': 'top layer incomplete (or forced casing spots taken)',
        'tdu_missing': 'a time dilation unit is missing',
        'tdu_mixed': 'time dilation units have mixed tiers',
        'coreflame_missing': 'a coreflame is missing',
        'coreflame_duplicate': 'duplicate coreflame kind',
    }
    problems_zh = {
        'unchecked': '结构未校验',
        'layer1': '底层不完整',
        'layer2_center': '中层中心缺外壳',
        'layer3': '顶层不完整(或强制外壳位被占用)',
        'tdu_missing': '时间膨胀单元缺失',
        'tdu_mixed': '时间膨胀单元等级不一致',
        'coreflame_missing': '创世火种缺失',
        'coreflame_duplicate': '创世火种种类重复',
    }
    for key in problems_en:
        en[gui + 'problem_' + key] = 'Structure problem: ' + problems_en[key]
        zh[gui + 'problem_' + key] = '结构问题:' + problems_zh[key]

    json.dump(en, open(en_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=2)
    json.dump(zh, open(zh_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=2)


if __name__ == '__main__':
    main()
