#!/usr/bin/env python3
"""Generate assets for the resonant circuit program: Q8 tuning notes, the
eight-state tuning block and the resonance attuner, the Ac..Cn element dusts,
the separation/PI/FKM/superconductor items, the 31 resonant fluids, the
trinium / resonite / resonant_superconductor materials, and lang entries.

Run from the repo root:  python tools/gen_resonant_assets.py

PNG/model helpers are shared with gen_algae_assets.py; the tables below mirror
com.nestedinfinity.mod.items.resonance.NINotes, NIItems (resonant program
section), NIFluids.RESONANT and NIMaterials.
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from gen_algae_assets import (A, MODID, ROOT, BIO_FLUIDS, make_item_texture,
                              make_bucket_texture, read_png, write_json,
                              write_png, fluid_blockstate, template_recolor)

DATA = os.path.join(ROOT, "src/main/resources/data")
MI = os.path.join(ROOT, "src/main/resources/assets/modern_industrialization")

# ---------------------------------------------------------------- Q8 notes

# color name / zh / tint — mirrors NINotes (white=1, red=i, yellow=j, blue=k,
# green=-k, cyan=-i, purple=-j, black=-1)
NOTES = [
    ("white",  "白", (255, 255, 255)),
    ("red",    "红", (245, 61, 61)),
    ("yellow", "黄", (245, 233, 77)),
    ("blue",   "蓝", (77, 109, 245)),
    ("green",  "绿", (77, 245, 109)),
    ("cyan",   "青", (77, 245, 245)),
    ("purple", "紫", (154, 77, 245)),
    ("black",  "黑", (58, 58, 72)),
]

# ---------------------------------------------------------------- element dusts (Ac..Cn minus U/Pu)

# id, en symbol name, zh char, color. U/Pu reuse MI items; the other 22 of the
# last-row elements (89..112) are registered by the mod.
ELEMENTS = [
    ("actinium_dust",      "Actinium",      "锕",   (184, 176, 184)),
    ("thorium_dust",       "Thorium",       "钍",   (186, 190, 178)),
    ("protactinium_dust",  "Protactinium",  "镤",   (154, 168, 178)),
    ("neptunium_dust",     "Neptunium",     "镎",   (152, 162, 172)),
    ("americium_dust",     "Americium",     "镅",   (178, 170, 186)),
    ("curium_dust",        "Curium",        "锔",   (168, 164, 180)),
    ("berkelium_dust",     "Berkelium",     "锫",   (178, 170, 162)),
    ("californium_dust",   "Californium",   "锎",   (186, 174, 154)),
    ("einsteinium_dust",   "Einsteinium",   "锿",   (186, 178, 162)),
    ("fermium_dust",       "Fermium",       "镄",   (194, 186, 170)),
    ("mendelevium_dust",   "Mendelevium",   "钔",   (202, 194, 178)),
    ("nobelium_dust",      "Nobelium",      "锘",   (202, 202, 186)),
    ("lawrencium_dust",    "Lawrencium",    "铹",   (202, 194, 194)),
    ("rutherfordium_dust", "Rutherfordium", "𬬻",  (176, 136, 104)),
    ("dubnium_dust",       "Dubnium",       "𬭊",  (184, 144, 112)),
    ("seaborgium_dust",    "Seaborgium",    "𬭳",  (192, 152, 120)),
    ("bohrium_dust",       "Bohrium",       "𬭛",  (200, 160, 128)),
    ("hassium_dust",       "Hassium",       "𬭶",  (208, 168, 136)),
    ("meitnerium_dust",    "Meitnerium",    "鿏",  (216, 176, 144)),
    ("darmstadtium_dust",  "Darmstadtium",  "𫟼",  (216, 184, 152)),
    ("roentgenium_dust",   "Roentgenium",   "𬬭",  (234, 202, 96)),
    ("copernicium_dust",   "Copernicium",   "鎶",   (198, 214, 232)),
]

# ---------------------------------------------------------------- other resonant items

# id -> (en, zh, rgb, style). mi_* styles recolor MI templates via
# make_item_texture; "note"/"frame"/"crystal"/"sheet"/"chunk" are hand painters.
RESONANT_ITEMS = [
    # separation reagents and catalysts (probability-consumed in recipes)
    ("tributyl_phosphate",    "Tributyl Phosphate",       "磷酸三丁酯",        (222, 214, 168), "mi_dust"),
    ("cmpo_extractant",       "CMPO Extractant",          "CMPO萃取剂",        (232, 200, 120), "mi_dust"),
    ("dtpa_complexant",       "DTPA Complexant",          "DTPA络合剂",        (196, 224, 200), "mi_dust"),
    ("alpha_hiba_eluant",     "Alpha-HIBA Eluant",        "α-HIBA淋洗剂",      (214, 232, 214), "mi_dust"),
    ("sodium_nitrite",        "Sodium Nitrite",           "亚硝酸钠",          (240, 244, 232), "mi_dust"),
    ("sodium_chlorate",       "Sodium Chlorate",          "氯酸钠",            (240, 244, 248), "mi_dust"),
    ("hydrazine",             "Hydrazine",                "肼",                (216, 232, 236), "mi_dust"),
    ("gold_foil",             "Gold Foil",                "金箔",              (250, 214, 74),  "sheet"),
    ("tellurium_dust",        "Tellurium Dust",           "碲粉",              (200, 196, 178), "mi_dust"),
    ("pgm_residue",           "PGM Residue",              "铂族金属残渣",      (226, 222, 228), "mi_dust"),
    # naquide and the fusion-born alloys
    ("crude_naquide_powder",  "Crude Naquide Powder",     "粗轻硅岩粉",        (140, 180, 160), "mi_dust"),
    ("naquide",               "Naquide",                  "轻硅岩",            (170, 220, 190), "crystal"),
    ("adamantium_ingot",      "Adamantium Ingot",         "精金锭",            (184, 56, 56),   "ingot"),
    ("mithril_ingot",         "Mithril Ingot",            "秘银锭",            (184, 216, 232), "ingot"),
    # piezo / resonator parts
    ("lead_titanate_dust",    "Lead Titanate Dust",       "钛酸铅粉",          (172, 140, 90),  "mi_dust"),
    ("lead_titanate_plate",   "Lead Titanate Plate",      "钛酸铅陶瓷板",      (172, 140, 90),  "mi_plate"),
    ("piezo_wafer",           "Piezo Wafer",              "压电晶圆",          (150, 232, 210), "crystal"),
    ("quartz_oscillator",     "Quartz Oscillator",        "石英振荡器",        (200, 220, 240), "crystal"),
    ("saw_resonator",         "SAW Resonator",            "声表面波谐振器",    (230, 200, 160), "crystal"),
    # polyimide chain (Kapton amber)
    ("durene",                "Durene",                   "均四甲苯",          (232, 228, 200), "mi_dust"),
    ("pyromellitic_acid",     "Pyromellitic Acid",        "均苯四甲酸",        (244, 240, 225), "mi_dust"),
    ("pyromellitic_dianhydride", "Pyromellitic Dianhydride", "均苯四甲酸二酐", (240, 232, 210), "mi_dust"),
    ("p_nitrochlorobenzene",  "p-Nitrochlorobenzene",     "对硝基氯苯",        (226, 210, 140), "mi_dust"),
    ("dinitrodiphenyl_ether", "Dinitrodiphenyl Ether",    "二硝基二苯醚",      (226, 196, 120), "mi_dust"),
    ("diaminodiphenyl_ether", "Diaminodiphenyl Ether",    "二氨基二苯醚",      (216, 214, 208), "mi_dust"),
    ("polyimide_dust",        "Polyimide Dust",           "聚酰亚胺粉",        (201, 96, 30),   "mi_dust"),
    ("polyimide_plate",       "Polyimide Plate",          "聚酰亚胺板",        (201, 88, 26),   "mi_plate"),
    # fluoroelastomer
    ("fluoroelastomer_sheet", "Fluoroelastomer Sheet",    "氟橡胶板",          (52, 60, 64),    "sheet"),
    # YBCO superconductor chain
    ("yttrium_oxide",         "Yttrium Oxide",            "氧化钇",            (226, 228, 232), "mi_dust"),
    ("cupric_oxide",          "Cupric Oxide",             "氧化铜",            (64, 52, 48),    "mi_dust"),
    ("ybco_target",           "YBCO Target",              "YBCO靶材",          (60, 80, 160),   "chunk"),
    ("sapphire_substrate",    "Sapphire Substrate",       "蓝宝石基板",        (120, 170, 240), "crystal"),
    ("resonant_superconductor_tape", "Resonant Superconductor Tape", "谐振超导带", (80, 200, 232), "sheet"),
    # resonant circuit parts (cyan family, mirroring the bio green parts)
    ("resonant_random_access_memory", "Resonant Random Access Memory", "谐振随机存取存储器", (80, 200, 232), "bio_ram"),
    ("resonant_memory_management_unit", "Resonant Memory Management Unit", "谐振内存管理单元", (80, 200, 232), "bio_memory"),
    ("resonant_arithmetic_logic_unit", "Resonant Arithmetic Logic Unit", "谐振算术逻辑单元", (80, 200, 232), "bio_arithmetic"),
    ("saser",                 "Saser",                    "声子激光器",        (80, 200, 232),  "mi_motor"),
    ("resonance_chamber",     "Resonance Chamber",        "共振腔",            (80, 200, 232),  "frame"),
    ("phase_locked_loop",     "Phase Locked Loop",        "锁相环",            (80, 190, 225),  "mi_plate"),
]

# ---------------------------------------------------------------- fluids

# id, en, zh, tint — mirrors NIFluids.RESONANT
RESONANT_FLUIDS = [
    ("radon",                    "Radon",                    "氡",            0xFFC8B8E8),
    ("superheavy_fission_solution", "Superheavy Fission Solution", "超重裂化液", 0xFF8C58C8),
    ("valence_adjusted_feed",    "Valence Adjusted Feed",    "调价料液",       0xFFB068D8),
    ("tbp_organic_phase",        "TBP Organic Phase",        "TBP有机相",      0xFFE8D8A8),
    ("hlr_raffinate",            "HLR Raffinate",            "高放萃余液",     0xFFB85838),
    ("uranium_liquor",           "Uranium Liquor",           "铀料液",         0xFFC8E858),
    ("plutonium_liquor",         "Plutonium Liquor",         "钚料液",         0xFF8898B8),
    ("neptunium_liquor",         "Neptunium Liquor",         "镎料液",         0xFF68C8A8),
    ("uranium_neptunium_liquor", "Uranium-Neptunium Liquor", "铀镎混合料液",   0xFFB8D888),
    ("truex_organic",            "TRUEX Organic Phase",      "TRUEX有机相",    0xFFE8C888),
    ("minor_actinide_liquor",    "Minor Actinide Liquor",    "次锕系料液",     0xFFC87858),
    ("early_actinide_group",     "Early Actinide Group",     "前段锕系组",     0xFFD88858),
    ("late_actinide_group",      "Late Actinide Group",      "后段锕系组",     0xFFD8A858),
    ("superheavy_vapor",         "Superheavy Vapor",         "超重元素蒸气",   0xFFD8C8F0),
    ("cn_condensate",            "Copernicium Condensate",   "鎶冷凝液",       0xFFA8C8E8),
    ("rg_liquor",                "Roentgenium Liquor",       "𬬭配位液",       0xFFE8C858),
    ("telluric_acid",            "Telluric Acid",            "碲酸",           0xFFE8E0B8),
    ("aqua_regia",               "Aqua Regia",               "王水",           0xFFE8C840),
    ("molten_gold",              "Molten Gold",              "熔融金",         0xFFE8B830),
    ("molten_silver",            "Molten Silver",            "熔融银",         0xFFD8D8E0),
    ("molten_roentgenium",       "Molten Roentgenium",       "熔融𬬭",         0xFFD8A830),
    ("molten_copernicium",       "Molten Copernicium",       "熔融鎶",         0xFF98B8D8),
    ("molten_adamantium",        "Molten Adamantium",        "熔融精金",       0xFFB83838),
    ("molten_mithril",           "Molten Mithril",           "熔融秘银",       0xFFB8D8E8),
    ("molten_trinium",           "Molten Trinium",           "熔融翠尼特",     0xFF9BC8D8),
    ("resonant_mother_liquor",   "Resonant Mother Liquor",   "谐振母液",       0xFF54C4C4),
    ("polyamic_acid",            "Polyamic Acid",            "聚酰胺酸",       0xFFD8C060),
    ("conductive_epoxy",         "Conductive Epoxy",         "导电银胶",       0xFFB8B8C0),
    ("vinylidene_fluoride",      "Vinylidene Fluoride",      "偏二氟乙烯",     0xFFD0E8E0),
    ("hexafluoropropylene",      "Hexafluoropropylene",      "六氟丙烯",       0xFFC8E0D0),
    ("chloroform",               "Chloroform",               "氯仿",           0xFFD8E8E8),
    ("refrigerant_22",           "Refrigerant R-22",         "制冷剂R-22",     0xFFC8E8E8),
]

# ---------------------------------------------------------------- materials

TRINIUM_COLOR = (155, 200, 216)
RESONITE_COLOR = (140, 232, 192)
TRINIUM_DINAQUADIDE_COLOR = (138, 63, 184)  # the purple of the dinaquide coil
ADAMANTIUM_COLOR = (222, 156, 62)  # gold + roentgenium: a dense noble amber
MITHRIL_COLOR = (198, 214, 232)  # silver + copernicium: lunar silver-blue
# standard part set of NIMaterial (generateWire adds wire+cable on top)
MAT_PARTS = ["tiny_dust", "dust", "hot_ingot", "ingot", "nugget", "plate", "rod", "gear"]
MAT_PART_NAMES = {
    "tiny_dust": ("Tiny Dust", "小撮粉"), "dust": ("Dust", "粉"),
    "hot_ingot": ("Hot Ingot", "热锭"), "ingot": ("Ingot", "锭"),
    "nugget": ("Nugget", "粒"), "plate": ("Plate", "板"),
    "rod": ("Rod", "杆"), "gear": ("Gear", "齿轮"), "wire": ("Wire", "线"),
}

# ---------------------------------------------------------------- painters


def shade(color, factor):
    return tuple(max(0, min(255, int(c * factor))) for c in color)


def make_note_texture(dst, color):
    """16x16 eighth-note glyph on a soft resonance halo."""
    px = bytearray(16 * 16 * 4)

    def put(x, y, c, a=255):
        if 0 <= x < 16 and 0 <= y < 16:
            o = (y * 16 + x) * 4
            px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], a

    dark = shade(color, 0.62)
    light = shade(color, 1.25)
    # resonance halo: two fading arcs to the left
    for x, y, a in [(3, 6, 70), (2, 7, 95), (2, 8, 95), (3, 9, 70),
                    (4, 4, 45), (1, 7, 45), (4, 11, 45), (1, 8, 45)]:
        put(x, y, light, a)
    # stem
    for y in range(3, 12):
        put(9, y, dark if y > 9 else color)
    # flag
    for x, y in [(10, 3), (11, 3), (10, 4), (11, 4), (12, 4), (11, 5), (12, 5), (12, 6), (11, 6)]:
        put(x, y, light if y < 5 else color)
    # note head (slanted ellipse)
    for y in range(11, 14):
        for x in range(6, 10):
            dx, dy = x - 7.5, y - 12
            if dx * dx * 1.35 + dy * dy * 3.2 <= 3.2:
                put(x, y, light if x + y < 19 else (dark if y == 13 else color))
    write_png(dst, 16, 16, px)


def make_tuning_block_texture(dst, color):
    """16x16 tuning register face: dark steel frame, machined plate, and a
    glowing horizontal resonator bar in the register color."""
    import random
    rng = random.Random(0x51)
    px = bytearray(16 * 16 * 4)
    dark = shade(color, 0.5)
    light = shade(color, 1.3)
    for y in range(16):
        for x in range(16):
            o = (y * 16 + x) * 4
            if x == 0 or y == 0 or x == 15 or y == 15:
                c = (52, 54, 62) if x + y != 0 else (66, 68, 78)
            elif 6 <= y <= 9 and 3 <= x <= 12:  # resonator bar
                edge = y in (6, 9) or x in (3, 12)
                c = dark if edge else (light if (x + y) % 5 == 0 else color)
            else:
                v = rng.random()
                c = (96, 100, 110) if v < 0.12 else (78, 82, 92)
            px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], 255
    for x, y in [(2, 2), (13, 2), (2, 13), (13, 13)]:  # bolts
        o = (y * 16 + x) * 4
        px[o], px[o+1], px[o+2] = 130, 134, 144
    write_png(dst, 16, 16, px)


def make_attuner_textures(dst_dir):
    """Resonance attuner block faces: steel casing, cyan sound-wave side,
    socketed top, plain bottom."""
    os.makedirs(dst_dir, exist_ok=True)

    def base(px):
        import random
        rng = random.Random(0xA77)
        for y in range(16):
            for x in range(16):
                o = (y * 16 + x) * 4
                frame = x == 0 or y == 0 or x == 15 or y == 15
                v = rng.random()
                if frame:
                    c = (48, 50, 58)
                elif v < 0.10:
                    c = (102, 106, 116)
                else:
                    c = (84, 88, 98)
                px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], 255

    def put(px, x, y, c):
        o = (y * 16 + x) * 4
        px[o], px[o+1], px[o+2] = c

    cyan, cyan_dark, cyan_light = (80, 200, 232), (46, 128, 156), (168, 240, 255)
    # side: three standing waves
    side = bytearray(16 * 16 * 4)
    base(side)
    for cx in (4, 8, 12):
        for y in range(3, 14):
            import math
            amp = 1.6 * math.sin((y - 3) / 10 * math.pi)
            dx = round(amp * math.sin((y - 3) / 3.2))
            put(side, cx + dx, y, cyan)
            put(side, cx + dx - 1 if amp >= 0 else cx + dx + 1, y, cyan_dark)
        put(side, cx, 3, cyan_light)
    write_png(os.path.join(dst_dir, "resonance_attuner_side.png"), 16, 16, side)
    # top: socket square with a cyan ring where the tuning block sits
    top = bytearray(16 * 16 * 4)
    base(top)
    for y in range(4, 12):
        for x in range(4, 12):
            edge = x in (4, 11) or y in (4, 11)
            put(top, x, y, cyan if edge else (30, 32, 38))
    for x, y in [(2, 2), (13, 2), (2, 13), (13, 13)]:
        put(top, x, y, (130, 134, 144))
    write_png(os.path.join(dst_dir, "resonance_attuner_top.png"), 16, 16, top)
    # bottom: vents
    bottom = bytearray(16 * 16 * 4)
    base(bottom)
    for x in range(3, 13):
        for y in (5, 8, 11):
            put(bottom, x, y, (44, 46, 52))
    write_png(os.path.join(dst_dir, "resonance_attuner_bottom.png"), 16, 16, bottom)


def make_attuner_gui(dst):
    """256x256 GUI background (vanilla blit assumes a 256x256 sheet) holding a
    176x184 vanilla-style container panel for the attuner GUI: the two machine
    slots, an arrow, a strip of eight Q8 swatch insets (the screen overlays the
    live colors) and the player inventory grid. Slot coordinates mirror
    ResonanceAttunerMenu / ResonanceAttunerScreen."""
    SHEET, W, H = 256, 176, 184
    px = bytearray(SHEET * SHEET * 4)

    def put(x, y, rgb):
        if 0 <= x < W and 0 <= y < H:
            i = (y * SHEET + x) * 4
            px[i], px[i + 1], px[i + 2], px[i + 3] = rgb[0], rgb[1], rgb[2], 255

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

    slot_inset(53, 34)   # note input
    slot_inset(107, 34)  # note output
    for dx in range(12):  # arrow between the slots
        half = max(0, 5 - dx // 2)
        for dy in range(-half, half + 1):
            put(78 + dx, 43 + dy, (85, 85, 85))
    for i in range(8):  # Q8 swatch insets; screen fills the inner 14x14
        slot_inset(17 + i * 18, 57, 16, 16)
    for row in range(3):
        for col in range(9):
            slot_inset(8 + col * 18, 106 + row * 18)
    for col in range(9):
        slot_inset(8 + col * 18, 162)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    write_png(dst, SHEET, SHEET, px)


def item_model(item_id):
    return {"parent": "minecraft:item/generated",
            "textures": {"layer0": f"{MODID}:item/{item_id}"}}


# ---------------------------------------------------------------- main


def main():
    # 1. Q8 notes: item model + note texture
    for name, _zh, color in NOTES:
        write_json(os.path.join(A, f"models/item/note_{name}.json"), item_model(f"note_{name}"))
        make_note_texture(os.path.join(A, f"textures/item/note_{name}.png"), tuple(color))

    # 2. tuning block textures (blockstates/models were written in T3) + attuner
    for name, _zh, color in NOTES:
        make_tuning_block_texture(os.path.join(A, f"textures/block/tuning_block_{name}.png"), tuple(color))
    make_attuner_textures(os.path.join(A, "textures/block"))
    for block_id in ("tuning_block", "resonance_attuner"):
        write_json(os.path.join(DATA, f"{MODID}/loot_table/blocks/{block_id}.json"),
                   {"type": "minecraft:block",
                    "pools": [{"rolls": 1,
                               "entries": [{"type": "minecraft:item", "name": f"{MODID}:{block_id}"}],
                               "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    # 3. element dusts + other items
    for item_id, symbol, _zh, color in ELEMENTS:
        write_json(os.path.join(A, f"models/item/{item_id}.json"), item_model(item_id))
        make_item_texture(os.path.join(A, f"textures/item/{item_id}.png"), "mi_dust", tuple(color))
    for item_id, _en, _zh, color, style in RESONANT_ITEMS:
        write_json(os.path.join(A, f"models/item/{item_id}.json"), item_model(item_id))
        make_item_texture(os.path.join(A, f"textures/item/{item_id}.png"), style, tuple(color))

    # 4. fluids: the full four-piece (blockstate + block model + bucket model +
    #    bucket texture) for the 31 resonant fluids, and fill in the two pieces
    #    the original BIO_FLUIDS loop forgot (block + bucket models).
    def fluid_assets(fluid_id, tint):
        write_json(os.path.join(A, f"blockstates/{fluid_id}.json"), fluid_blockstate(fluid_id))
        write_json(os.path.join(A, f"models/block/{fluid_id}.json"),
                   {"parent": "minecraft:block/water"})
        write_json(os.path.join(A, f"models/item/{fluid_id}_bucket.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{fluid_id}_bucket"}})
        rgb = ((tint >> 16) & 255, (tint >> 8) & 255, tint & 255)
        make_bucket_texture(os.path.join(A, f"textures/item/{fluid_id}_bucket.png"), rgb)

    for fluid_id, _en, _zh, tint in RESONANT_FLUIDS:
        fluid_assets(fluid_id, tint)
    for fluid_id, _en, _zh, tint in BIO_FLUIDS:
        # idempotent: blockstate + bucket texture already exist, models get added
        fluid_assets(fluid_id, tint)

    # 5. materials: recolor the nichrome part sprites (same pipeline as TPV)
    def material_parts(mat, color, parts):
        for part in parts:
            src_rel = f"src/main/resources/assets/modern_industrialization/textures/item/nichrome_{part}.png"
            template_recolor(os.path.join(MI, f"textures/item/{mat}_{part}.png"), src_rel, tuple(color))
            write_json(os.path.join(MI, f"models/item/{mat}_{part}.json"),
                       {"parent": "minecraft:item/generated",
                        "textures": {"layer0": f"modern_industrialization:item/{mat}_{part}"}})
        template_recolor(os.path.join(MI, f"textures/block/{mat}_block.png"),
                         "src/main/resources/assets/modern_industrialization/textures/block/nichrome_block.png",
                         tuple(color))
        write_json(os.path.join(MI, f"blockstates/{mat}_block.json"),
                   {"variants": {"": {"model": f"modern_industrialization:block/{mat}_block"}}})
        write_json(os.path.join(MI, f"models/block/{mat}_block.json"),
                   {"parent": "minecraft:block/cube_all",
                    "textures": {"all": f"modern_industrialization:block/{mat}_block"}})
        write_json(os.path.join(MI, f"models/item/{mat}_block.json"),
                   {"parent": f"modern_industrialization:block/{mat}_block"})

    material_parts("trinium", TRINIUM_COLOR, MAT_PARTS)
    material_parts("resonite", RESONITE_COLOR, MAT_PARTS + ["wire"])
    # the coil alloy proper (EBF-cast on the TPV coil tier)
    material_parts("trinium_dinaquadide", TRINIUM_DINAQUADIDE_COLOR, MAT_PARTS)
    # fusion-alloy plates laminated into the resonant circuit casing
    for plate_id, color in (("adamantium", ADAMANTIUM_COLOR), ("mithril", MITHRIL_COLOR)):
        template_recolor(os.path.join(A, f"textures/item/{plate_id}_plate.png"),
                         "src/main/resources/assets/modern_industrialization/textures/item/nichrome_plate.png",
                         tuple(color))
        write_json(os.path.join(A, f"models/item/{plate_id}_plate.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{plate_id}_plate"}})
    make_attuner_gui(os.path.join(A, "textures/gui/resonance_attuner.png"))
    for mat in ("resonite", "resonant_superconductor"):
        # cable items render through MI's pipe delegate model, no texture needed
        write_json(os.path.join(MI, f"models/item/{mat}_cable.json"),
                   {"delegate": "modern_industrialization:block/pipe",
                    "loader": "modern_industrialization:delegate"})

    # 6. lang
    en_path = os.path.join(A, "lang/en_us.json")
    zh_path = os.path.join(A, "lang/zh_cn.json")
    en = json.load(open(en_path, encoding="utf-8"))
    zh = json.load(open(zh_path, encoding="utf-8"))

    for name, zh_color, _c in NOTES:
        en[f"item.{MODID}.note_{name}"] = f"{name.capitalize()} Tuning Note"
        zh[f"item.{MODID}.note_{name}"] = f"{zh_color}色音符"
        en[f"color.{MODID}.{name}"] = name.capitalize()
        zh[f"color.{MODID}.{name}"] = f"{zh_color}色"
    # the visual Cayley-table pages (NIAttuningRecipe) replaced the old
    # text-only info pages; drop their keys
    for lang in (en, zh):
        for key in [k for k in lang if k.startswith(f"emi.{MODID}.note.")]:
            del lang[key]
    en[f"emi.category.{MODID}.resonance_attuner"] = "Resonance Attuner"
    zh[f"emi.category.{MODID}.resonance_attuner"] = "谐振调律机"
    en[f"emi.{MODID}.attuning.becomes"] = "Register becomes %s (50%%)"
    zh[f"emi.{MODID}.attuning.becomes"] = "方块变为%s（50%%）"
    en[f"emi.{MODID}.attuning.drifts"] = "Drifts to %s (50%%)"
    zh[f"emi.{MODID}.attuning.drifts"] = "漂移为%s（50%%）"
    en[f"container.{MODID}.resonance_attuner.register"] = "Register: %s"
    zh[f"container.{MODID}.resonance_attuner.register"] = "当前音色：%s"
    en[f"container.{MODID}.resonance_attuner.no_register"] = "No tuning block above!"
    zh[f"container.{MODID}.resonance_attuner.no_register"] = "上方没有调律方块！"
    en[f"item.{MODID}.adamantium_plate"] = "Adamantium Plate"
    zh[f"item.{MODID}.adamantium_plate"] = "精金板"
    en[f"item.{MODID}.mithril_plate"] = "Mithril Plate"
    zh[f"item.{MODID}.mithril_plate"] = "秘银板"
    en[f"block.{MODID}.tuning_block"] = "Tuning Block"
    zh[f"block.{MODID}.tuning_block"] = "调律方块"
    en[f"block.{MODID}.resonance_attuner"] = "Resonance Attuner"
    zh[f"block.{MODID}.resonance_attuner"] = "谐振调律机"
    for item_id, symbol, zh_char, _c in ELEMENTS:
        en[f"item.{MODID}.{item_id}"] = f"{symbol} Dust"
        zh[f"item.{MODID}.{item_id}"] = f"{zh_char}粉"
    for item_id, item_en, item_zh, _c, _s in RESONANT_ITEMS:
        en[f"item.{MODID}.{item_id}"] = item_en
        zh[f"item.{MODID}.{item_id}"] = item_zh
    for fluid_id, fluid_en, fluid_zh, _t in RESONANT_FLUIDS:
        en[f"fluid.{MODID}.{fluid_id}"] = fluid_en
        en[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_en} Bucket"
        zh[f"fluid.{MODID}.{fluid_id}"] = fluid_zh
        zh[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_zh}桶"
    for mat, mat_en, mat_zh in (("trinium", "Trinium", "翠尼特"),
                                ("resonite", "Resonite", "谐振合金"),
                                ("trinium_dinaquadide", "Trinium Dinaquadide", "翠尼特二硅岩化物")):
        for part, (p_en, p_zh) in MAT_PART_NAMES.items():
            has = (part in MAT_PARTS) or (mat == "resonite" and part == "wire")
            if has:
                en[f"item.modern_industrialization.{mat}_{part}"] = f"{mat_en} {p_en}"
                zh[f"item.modern_industrialization.{mat}_{part}"] = f"{mat_zh}{p_zh}"
        en[f"block.modern_industrialization.{mat}_block"] = f"{mat_en} Block"
        zh[f"block.modern_industrialization.{mat}_block"] = f"{mat_zh}块"
    en["cable_tier_long.modern_industrialization.resonite"] = "Resonite"
    zh["cable_tier_long.modern_industrialization.resonite"] = "谐振合金"
    en["cable_tier_short.modern_industrialization.resonite"] = "Resonite"
    zh["cable_tier_short.modern_industrialization.resonite"] = "谐振合金"
    en["item.modern_industrialization.resonite_cable"] = "Resonite Cable"
    zh["item.modern_industrialization.resonite_cable"] = "谐振合金线缆"
    en["cable_tier_long.modern_industrialization.resonant_superconductor"] = "Resonant Superconductor"
    zh["cable_tier_long.modern_industrialization.resonant_superconductor"] = "谐振超导体"
    en["cable_tier_short.modern_industrialization.resonant_superconductor"] = "RSV"
    zh["cable_tier_short.modern_industrialization.resonant_superconductor"] = "谐振超导"
    en["item.modern_industrialization.resonant_superconductor_cable"] = "Resonant Superconductor Cable"
    zh["item.modern_industrialization.resonant_superconductor_cable"] = "谐振超导体线缆"
    write_json(en_path, en)
    write_json(zh_path, zh)

    # 7. preview sheet (notes + tuning blocks + a spread of items + buckets)
    tiles = []
    tiles += [os.path.join(A, f"textures/item/note_{n}.png") for n, _z, _c in NOTES]
    tiles += [os.path.join(A, f"textures/block/tuning_block_{n}.png") for n, _z, _c in NOTES]
    tiles += [os.path.join(A, "textures/block/resonance_attuner_side.png"),
              os.path.join(A, "textures/block/resonance_attuner_top.png")]
    tiles += [os.path.join(A, f"textures/item/{i}.png") for i, _e, _z, _c, _s in RESONANT_ITEMS]
    tiles += [os.path.join(A, f"textures/item/{i}.png") for i, _s, _z, _c in ELEMENTS]
    tiles += [os.path.join(A, f"textures/item/{f}_bucket.png") for f, _e, _z, _t in RESONANT_FLUIDS]
    cols, scale = 12, 4
    cell = 16 * scale + 8
    rows = (len(tiles) + cols - 1) // cols
    sheet = bytearray(cols * rows * cell * cell * 4)
    for idx, path in enumerate(tiles):
        w, h, p = read_png(path)
        cx, cy = (idx % cols) * cell + 4, (idx // cols) * cell + 4
        for y in range(h):
            for x in range(w):
                o = (y * w + x) * 4
                if p[o+3] == 0:
                    continue
                for sy in range(scale):
                    for sx in range(scale):
                        d = ((cy + y*scale + sy) * cols*cell + cx + x*scale + sx) * 4
                        sheet[d:d+4] = p[o:o+4]
    preview = os.path.join(ROOT, "tools/preview_resonant.png")
    write_png(preview, cols * cell, rows * cell, sheet)
    print(f"notes: {len(NOTES)}, elements: {len(ELEMENTS)}, items: {len(RESONANT_ITEMS)}, "
          f"fluids: {len(RESONANT_FLUIDS)} (+{len(BIO_FLUIDS)} bio four-piece fixed)")
    print(f"wrote preview: {preview}")


if __name__ == "__main__":
    main()
