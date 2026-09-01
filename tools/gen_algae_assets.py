#!/usr/bin/env python3
"""Generate assets for the 12 color-wheel algae fluids and their compound petri dishes.

Run from the repo root:  python tools/gen_algae_assets.py

Everything (fluid tints, registry ids, display names) derives from the single
ALGAE table below, which mirrors com.nestedinfinity.mod.items.algae.NIAlgae / com.nestedinfinity.mod.fluids.NIFluids in the mod:

  index  color    root        taxon (root+phyta)   etymology
  0      red      erythro     Erythrophyta         Greek  erythros  "red"
  1      orange   aurantio    Aurantiophyta        Latin  aurantium "orange (fruit)"
  2      yellow   xantho      Xanthophyta          Greek  xanthos   "yellow"        (real division)
  3      lime     prasino     Prasinophyta         Greek  prasinos  "leek-green"
  4      green    chloro      Chlorophyta          Greek  chloros   "green"         (real division)
  5      teal     glauco      Glaucophyta          Greek  glaukos   "blue-green"    (real division)
  6      cyan     cyano       Cyanophyta           Greek  kyanos    "blue"          (real division)
  7      azure    azuro       Azureophyta          Latin  azureus   "sky blue" (botanical Latin)
  8      blue     caeruleo    Caeruleophyta        Latin  caeruleus "deep blue"
  9      purple   purpureo    Purpureophyta        Latin  purpureus "purple"
  10     magenta  magento     Magentophyta         modern "magenta" (1859)
  11     pink     rhodo       Rhodophyta           Greek  rhodon    "rose"          (real division)

A petri dish hosts a set of algae in which any two members are at least 3 steps
apart on the 12-color wheel (circular distance), so dishes have 1..4 strains.
Compound names concatenate the color roots: red+cyan -> Erythrocyanophyta.
"""
import colorsys
import json
import math
import os
import struct
import zlib

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
A = os.path.join(ROOT, "src/main/resources/assets/mi_nested_infinity")

MODID = "mi_nested_infinity"

# index, english color, compound root, chinese color word
ALGAE = [
    ("red",     "erythro",   "红"),
    ("orange",  "aurantio",  "橙"),
    ("yellow",  "xantho",    "黄"),
    ("lime",    "prasino",   "青柠"),
    ("green",   "chloro",    "绿"),
    ("teal",    "glauco",    "蓝绿"),
    ("cyan",    "cyano",     "青"),
    ("azure",   "azuro",     "蔚蓝"),
    ("blue",    "caeruleo",  "蓝"),
    ("purple",  "purpureo",  "紫"),
    ("magenta", "magento",   "品红"),
    ("pink",    "rhodo",     "粉红"),
]
N = len(ALGAE)
MIN_DISTANCE = 3


def taxon(i):
    return ALGAE[i][1] + "phyta"


def tint(i):
    """Vivid hue wheel: H = i*30deg, S = 0.9, L = 0.6."""
    r, g, b = colorsys.hls_to_rgb(i / N, 0.6, 0.9)
    return (round(r * 255), round(g * 255), round(b * 255))


def argb(rgb):
    return 0xFF000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]


# ---------------------------------------------------------------- dishes

def valid(mask):
    """Any two members must be >= MIN_DISTANCE apart on the 12-cycle."""
    for i in range(N):
        if not mask & (1 << i):
            continue
        for j in range(i + 1, N):
            if not mask & (1 << j):
                continue
            d = abs(i - j)
            d = min(d, N - d)
            if d < MIN_DISTANCE:
                return False
    return True


def members(mask):
    return [i for i in range(N) if mask & (1 << i)]


def dish_word(ms):
    return "".join(ALGAE[i][1] for i in ms[:-1]) + taxon(ms[-1])


def dishes():
    out = []
    for mask in range(1, 1 << N):
        if valid(mask):
            ms = members(mask)
            out.append((ms, dish_word(ms)))
    return out


# ---------------------------------------------------------------- png helpers

def read_png(path):
    data = open(path, "rb").read()
    pos, idat, w = 8, b"", 0
    while pos < len(data):
        ln, typ = struct.unpack(">I", data[pos:pos+4])[0], data[pos+4:pos+8]
        chunk = data[pos+8:pos+8+ln]
        if typ == b"IHDR":
            w, h = struct.unpack(">II", chunk[:8])
        elif typ == b"IDAT":
            idat += chunk
        pos += 12 + ln
    raw = zlib.decompress(idat)
    bpp, stride = 4, w * 4
    out, prev, p = bytearray(), bytearray(stride), 0
    for _ in range(h):
        f = raw[p]; p += 1
        line = bytearray(raw[p:p+stride]); p += stride
        if f == 1:
            for i in range(bpp, stride):
                line[i] = (line[i] + line[i-bpp]) & 255
        elif f == 2:
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 255
        elif f == 3:
            for i in range(stride):
                a = line[i-bpp] if i >= bpp else 0
                line[i] = (line[i] + ((a + prev[i]) >> 1)) & 255
        elif f == 4:
            for i in range(stride):
                a = line[i-bpp] if i >= bpp else 0
                b = prev[i]
                c = prev[i-bpp] if i >= bpp else 0
                pa, pb, pc = abs(b-c), abs(a-c), abs(a+b-2*c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[i] = (line[i] + pr) & 255
        out += line
        prev = line
    return w, h, out


def write_png(path, w, h, rgba):
    def chunk(typ, payload):
        return (struct.pack(">I", len(payload)) + typ + payload
                + struct.pack(">I", zlib.crc32(typ + payload) & 0xFFFFFFFF))

    scanlines = bytearray()
    for y in range(h):
        scanlines.append(0)
        scanlines += rgba[y*w*4:(y+1)*w*4]
    data = (b"\x89PNG\r\n\x1a\n"
            + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(bytes(scanlines), 9))
            + chunk(b"IEND", b""))
    open(path, "wb").write(data)


# ---------------------------------------------------------------- sprites

def make_bucket_texture(dst, rgb):
    """Clone the shared bucket template, repainting the 8 fluid pixels."""
    src = os.path.join(A, "textures/item/xenon_bucket.png")
    w, h, px = read_png(src)
    fluid = (127, 184, 196)  # xenon's tint marks the fluid window
    out = bytearray(px)
    for i in range(0, len(px), 4):
        if (px[i], px[i+1], px[i+2]) == fluid and px[i+3] == 255:
            out[i], out[i+1], out[i+2] = rgb
    write_png(dst, w, h, out)


def hash2(x, y, s):
    return (x * 73856093) ^ (y * 19349663) ^ (s * 83492791)


def make_dish_texture(dst, member_colors):
    """16x16 petri dish: grey rim + agar split into one sector per strain."""
    size = 16
    n = len(member_colors)
    px = bytearray(size * size * 4)
    for y in range(size):
        for x in range(size):
            dx, dy = x - 7.5, y - 7.5
            r = (dx * dx + dy * dy) ** 0.5
            if r > 7.45:
                continue
            o = (y * size + x) * 4
            if r <= 5.5:  # agar
                ang = (math.degrees(math.atan2(dy, dx)) + 90.0) % 360.0
                s = int(ang / (360.0 / n)) if n > 1 else 0
                cr, cg, cb = member_colors[s % n]
                h = hash2(x, y, s) % 97
                if h < 9:  # colonies / mottling
                    cr, cg, cb = int(cr * 0.62), int(cg * 0.62), int(cb * 0.62)
                elif h >= 92:
                    cr, cg, cb = min(255, int(cr * 1.25)), min(255, int(cg * 1.25)), min(255, int(cb * 1.25))
                shade = 1.10 - (y + x) * 0.0075  # gentle top-left lighting
                px[o] = min(255, int(cr * shade))
                px[o+1] = min(255, int(cg * shade))
                px[o+2] = min(255, int(cb * shade))
                px[o+3] = 255
            elif r <= 6.9:  # lid rim
                b = max(138, min(208, int(176 - (dx + dy) * 4.2)))
                px[o], px[o+1], px[o+2] = b, b, b + 6
                px[o+3] = 255
            else:  # dark outer edge, soft alpha
                a = max(0, min(255, int((7.45 - r) / 0.55 * 255)))
                px[o], px[o+1], px[o+2] = 96, 98, 104
                px[o+3] = a
    write_png(dst, size, size, px)


# ---------------------------------------------------------------- generation

def fluid_blockstate(fluid_id):
    """LiquidBlock has a level property (0..15); map every level to the same
    water-parented model so no variant is missing."""
    return {"variants": {f"level={level}": {"model": f"{MODID}:block/{fluid_id}"}
                         for level in range(16)}}


# Bio-program items: id -> (english, chinese, rgb, style)
# styles: powder (amino-acid heap), gel, crystal, thallus (algae mass), flake
BIO_ITEMS = [
    ("low_purity_algae", "Low-Purity Algae", "低纯藻类", (118, 138, 78), "thallus"),
    ("wet_rhodophyta", "Wet Rhodophyta Thallus", "湿红藻藻体", (214, 60, 110), "thallus"),
    ("desulfated_rhodophyta", "Desulfated Rhodophyta Thallus", "脱硫酸基红藻藻体", (150, 40, 80), "thallus"),
    ("washed_rhodophyta", "Washed Rhodophyta Thallus", "水洗红藻藻体", (230, 90, 130), "thallus"),
    ("bleached_rhodophyta", "Bleached Rhodophyta Thallus", "漂白红藻藻体", (238, 200, 210), "thallus"),
    ("rhodophyta_residue", "Rhodophyta Cellulose Residue", "红藻纤维渣", (110, 70, 60), "mi_dust"),
    ("frozen_agar_gel", "Frozen Agar Gel", "冷冻琼脂凝胶", (190, 230, 240), "gel"),
    ("agar_gel", "Agar Gel", "琼脂凝胶", (238, 228, 190), "gel"),
    ("agar", "Agar", "琼脂", (245, 238, 205), "mi_dust"),
    ("protein", "Protein", "蛋白质", (232, 176, 120), "mi_dust"),
    ("glycine", "Glycine", "甘氨酸", (220, 220, 235), "mi_dust"),
    ("alanine", "Alanine", "丙氨酸", (200, 225, 200), "mi_dust"),
    ("valine", "Valine", "缬氨酸", (235, 210, 170), "mi_dust"),
    ("leucine", "Leucine", "亮氨酸", (215, 200, 240), "mi_dust"),
    ("isoleucine", "Isoleucine", "异亮氨酸", (170, 210, 220), "mi_dust"),
    ("serine", "Serine", "丝氨酸", (240, 170, 190), "mi_dust"),
    ("threonine", "Threonine", "苏氨酸", (180, 235, 170), "mi_dust"),
    ("aspartic_acid", "Aspartic Acid", "天冬氨酸", (170, 190, 245), "mi_dust"),
    ("glutamic_acid", "Glutamic Acid", "谷氨酸", (245, 200, 160), "mi_dust"),
    ("lysine", "Lysine", "赖氨酸", (200, 180, 235), "mi_dust"),
    ("arginine", "Arginine", "精氨酸", (235, 170, 160), "mi_dust"),
    ("proline", "Proline", "脯氨酸", (230, 225, 155), "mi_dust"),
    ("phenylalanine", "Phenylalanine", "苯丙氨酸", (185, 170, 210), "mi_dust"),
    ("tyrosine", "Tyrosine", "酪氨酸", (240, 220, 245), "mi_dust"),
    ("asparagine", "Asparagine", "天冬酰胺", (205, 235, 245), "mi_dust"),
    ("glutamine", "Glutamine", "谷氨酰胺", (245, 235, 195), "mi_dust"),
    ("ornithine", "Ornithine", "鸟氨酸", (225, 205, 170), "mi_dust"),
    ("cyanamide", "Cyanamide", "氰胺", (210, 240, 240), "mi_dust"),
    ("chloroacetic_acid", "Chloroacetic Acid", "氯乙酸", (190, 225, 190), "mi_dust"),
    ("epsilon_aminocaproic_acid", "Epsilon-Aminocaproic Acid", "ε-氨基己酸", (215, 225, 195), "mi_dust"),
    ("fumaric_acid", "Fumaric Acid", "富马酸", (240, 240, 250), "mi_dust"),
    ("maleic_anhydride", "Maleic Anhydride", "顺丁烯二酸酐", (250, 225, 235), "mi_dust"),
    ("alpha_ketoglutaric_acid", "Alpha-Ketoglutaric Acid", "α-酮戊二酸", (235, 205, 205), "mi_dust"),
    ("glycolaldehyde", "Glycolaldehyde", "乙醇醛", (225, 245, 225), "mi_dust"),
    ("slaked_lime", "Slaked Lime", "氢氧化钙", (235, 240, 245), "mi_dust"),
    ("silicone_rubber_sheet", "Silicone Rubber Sheet", "硅橡胶片", (240, 216, 218), "sheet"),
    ("silicone_mica_insulator_sheet", "Silicone Mica Insulator Sheet", "硅橡胶云母绝缘片", (218, 190, 148), "sheet"),
    ("supercharged_naquadah", "Supercharged Naquadah", "超能硅岩锭", (110, 228, 214), "ingot"),
    ("bio_random_access_memory", "Bio Random Access Memory", "生物随机存取存储器", (126, 204, 128), "bio_ram"),
    ("bio_memory_management_unit", "Bio Memory Management Unit", "生物内存管理单元", (126, 204, 128), "bio_memory"),
    ("bio_arithmetic_logic_unit", "Bio Arithmetic Logic Unit", "生物算术逻辑单元", (126, 204, 128), "bio_arithmetic"),
    # wetware circuit board program: TsOH, the Celazole PBI route, board chassis
    ("naquadah_frame", "Naquadah Frame", "硅岩框架", (107, 142, 90), "frame"),
    ("polybenzimidazole_plate", "Polybenzimidazole Plate", "聚苯并咪唑片", (200, 148, 62), "mi_plate"),
    ("epoxy_plate", "Epoxy Plate", "环氧树脂板", (216, 168, 56), "mi_plate"),
    # elites: MI's current motor/pump sprites with only the blue accents
    # hue-rotated to green (grays/whites/copper untouched)
    ("elite_motor", "Elite Motor", "精英马达", (76, 175, 80), "mi_motor_green"),
    ("elite_pump", "Elite Pump", "精英泵", (76, 175, 80), "mi_pump_green"),
    ("sodium_cyanide", "Sodium Cyanide", "氰化钠", (238, 240, 244), "mi_dust"),
    ("cyanoacetic_acid", "Cyanoacetic Acid", "氰乙酸", (214, 232, 202), "mi_dust"),
    ("poly_methyl_cyanoacrylate", "Poly(Methyl Cyanoacrylate)", "聚氰基丙烯酸甲酯", (228, 214, 178), "mi_dust"),
    ("p_toluenesulfonic_acid", "p-Toluenesulfonic Acid", "对甲苯磺酸", (238, 228, 205), "mi_dust"),
    ("isophthalic_acid", "Isophthalic Acid", "间苯二甲酸", (226, 234, 244), "mi_dust"),
    ("diphenyl_isophthalate", "Diphenyl Isophthalate", "二苯基间苯二甲酸酯", (232, 222, 195), "mi_dust"),
    ("benzidine", "Benzidine", "联苯胺", (198, 192, 186), "mi_dust"),
    ("dinitrobenzidine", "3,3'-Dinitrobenzidine", "3,3'-二硝基联苯胺", (218, 190, 120), "mi_dust"),
    ("diaminobenzidine", "3,3'-Diaminobenzidine", "3,3'-二氨基联苯胺", (192, 176, 166), "mi_dust"),
    # chain catalysts (returned by their recipes)
    ("haber_iron_catalyst", "Haber Iron Catalyst", "铁基合成氨催化剂", (168, 108, 72), "mi_dust"),
    ("reforming_nickel_catalyst", "Nickel Reforming Catalyst", "镍重整催化剂", (150, 160, 172), "mi_dust"),
    ("methanol_synthesis_catalyst", "Methanol Synthesis Catalyst", "甲醇合成催化剂", (172, 148, 120), "mi_dust"),
    ("silver_gauze_catalyst", "Silver Gauze Catalyst", "银网催化剂", (216, 222, 230), "mi_dust"),
    ("platinum_gauze_catalyst", "Platinum Gauze Catalyst", "铂网催化剂", (222, 224, 228), "mi_dust"),
    ("iridium_carbonylation_catalyst", "Iridium Carbonylation Catalyst", "铱羰化催化剂", (226, 214, 232), "mi_dust"),
    ("hydroformylation_catalyst", "Hydroformylation Catalyst", "氢甲酰化催化剂", (206, 212, 226), "mi_dust"),
    ("copper_chloride_catalyst", "Copper Chloride Catalyst", "氯化铜催化剂", (150, 190, 120), "mi_dust"),
    ("immobilized_enzyme", "Immobilized Enzyme", "固定化酶", (196, 226, 178), "mi_dust"),
    # byproducts of the chain
    ("ammonium_chloride", "Ammonium Chloride", "氯化铵", (238, 238, 240), "mi_dust"),
    ("calcium_chloride", "Calcium Chloride", "氯化钙", (245, 245, 240), "mi_dust"),
    ("sodium_sulfate", "Sodium Sulfate", "硫酸钠", (240, 240, 245), "mi_dust"),
    # Strecker intermediates
    ("alanine_aminonitrile", "Alanine Aminonitrile", "丙氨酸氨基腈", (210, 200, 190), "mi_dust"),
    ("valine_aminonitrile", "Valine Aminonitrile", "缬氨酸氨基腈", (220, 196, 170), "mi_dust"),
    ("leucine_aminonitrile", "Leucine Aminonitrile", "亮氨酸氨基腈", (200, 196, 214), "mi_dust"),
    ("isoleucine_aminonitrile", "Isoleucine Aminonitrile", "异亮氨酸氨基腈", (178, 200, 196), "mi_dust"),
    ("serine_aminonitrile", "Serine Aminonitrile", "丝氨酸氨基腈", (228, 176, 200), "mi_dust"),
    ("phenylalanine_aminonitrile", "Phenylalanine Aminonitrile", "苯丙氨酸氨基腈", (206, 208, 176), "mi_dust"),
    # advanced superconductor chain
    ("vanadium_dust", "Vanadium Dust", "钒粉", (128, 138, 160), "mi_dust"),
    ("sodium_vanadate", "Sodium Vanadate", "钒酸钠", (226, 200, 160), "mi_dust"),
    ("cinnabar_dust", "Cinnabar Dust", "朱砂粉", (198, 60, 48), "mi_dust"),
    ("barite_dust", "Barite Dust", "重晶石粉", (226, 224, 218), "mi_dust"),
    ("mercury_oxide_dust", "Mercury Oxide Dust", "氧化汞粉", (216, 88, 72), "mi_dust"),
    ("barium_oxide_dust", "Barium Oxide Dust", "氧化钡粉", (222, 226, 218), "mi_dust"),
    ("mercury_barium_titanium_copper_oxide", "Mercury Barium Titanium Copper Oxide", "汞钡钛铜氧", (72, 96, 168), "mi_dust"),
    ("superconductor_substrate", "Superconductor Substrate", "超导体基底", (56, 72, 130), "chunk"),
    ("superconductor_substrate_wire", "Superconductor Substrate Wire", "超导体基底线", (88, 108, 178), "mi_wire"),
]

# TPV material parts recolored from the nichrome textures; the base color matches the
# hand-drawn green TPV coil textures (sampled bright base ~ (90, 194, 108)). Both cable
# items use MI's pipe delegate model, so they need no texture at all.
TPV_COLOR = (90, 194, 108)
TPV_PARTS = ["tiny_dust", "dust", "hot_ingot", "ingot", "nugget", "plate", "rod", "gear", "wire"]

# Extra fluids outside the color wheel: id -> (english, chinese, tint)
BIO_FLUIDS = [
    ("ammonia", "Ammonia", "氨", 0xFFE8E0B0),
    ("carbon_monoxide", "Carbon Monoxide", "一氧化碳", 0xFFD8D8D8),
    ("methanol", "Methanol", "甲醇", 0xFFD0E8E0),
    ("acetic_acid", "Acetic Acid", "乙酸", 0xFFE8F0D0),
    ("formaldehyde", "Formaldehyde", "甲醛", 0xFFE0F0E8),
    ("hydrogen_cyanide", "Hydrogen Cyanide", "氰化氢", 0xFFC8E0D8),
    ("acetaldehyde", "Acetaldehyde", "乙醛", 0xFFF0E8C8),
    ("isobutyraldehyde", "Isobutyraldehyde", "异丁醛", 0xFFF0E0C0),
    ("isovaleraldehyde", "Isovaleraldehyde", "异戊醛", 0xFFF0D8B0),
    ("methyl_butanal", "2-Methylbutanal", "2-甲基丁醛", 0xFFF0DCC0),
    ("phenylacetaldehyde", "Phenylacetaldehyde", "苯乙醛", 0xFFE0D8F0),
    ("crude_agar_solution", "Crude Agar Solution", "琼脂粗提液", 0xFFB89060),
    ("clarified_agar_solution", "Clarified Agar Solution", "澄清琼脂液", 0xFFE8D8A8),
    ("nutrient_agar", "Nutrient Agar", "营养琼脂", 0xFFC8A868),
    ("sulfur_dioxide", "Sulfur Dioxide", "二氧化硫", 0xFFDCE0C0),
    ("advanced_rubber", "Advanced Rubber", "高级橡胶", 0xFF4A3830),
    ("chloromethane", "Chloromethane", "氯甲烷", 0xFFD0E8DC),
    ("dimethyldichlorosilane", "Dimethyldichlorosilane", "二甲基二氯硅烷", 0xFFE0D4C4),
    ("liquid_silicone_rubber", "Liquid Silicone Rubber", "液态硅橡胶", 0xFFF0E0E2),
    ("supercharged_naquadah_solution", "Supercharged Naquadah Solution", "超能硅岩溶液", 0xFF8CE8D8),
    ("m_xylene", "m-Xylene", "间二甲苯", 0xFFE8E0C8),
    ("nitrobenzene", "Nitrobenzene", "硝基苯", 0xFFE8E0A8),
    ("polybenzimidazole", "Polybenzimidazole", "聚苯并咪唑", 0xFFC8943C),
    ("mutagen", "Mutagen", "变异剂", 0xFFB254C4),
    ("methyl_cyanoacetate", "Methyl Cyanoacetate", "氰乙酸甲酯", 0xFFE8ECE8),
    ("cyanoacrylate_glue", "Cyanoacrylate Glue", "氰基丙烯酸酯强力胶", 0xFFE4DCC0),
]

# Real placeable blocks (registered in NIBlocks): id -> (english, chinese, rgb, painter)
BIO_BLOCKS = [
    ("quicklime", "Quicklime", "氧化钙", (240, 238, 230), "rubble"),
    ("silicone_rubber_block", "Silicone Rubber Block", "硅橡胶块", (232, 200, 204), "rubber"),
    ("polybenzimidazole_block", "Polybenzimidazole Block", "聚苯并咪唑块", (196, 146, 60), "rubber"),
]


def shade(color, factor):
    return tuple(max(0, min(255, int(c * factor))) for c in color)


def paint_item(style, color):
    """16x16 pixel art for one item, centered 12x12 art area."""
    px = bytearray(16 * 16 * 4)

    def put(x, y, c, a=255):
        if 0 <= x < 16 and 0 <= y < 16:
            o = (y * 16 + x) * 4
            px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], a

    dark = shade(color, 0.55)
    dark2 = shade(color, 0.72)
    light = shade(color, 1.25)
    if style == "powder":  # granular heap
        heap = [(5, 10), (6, 10), (7, 10), (8, 10), (9, 10), (10, 10),
                (4, 11), (5, 11), (6, 11), (7, 11), (8, 11), (9, 11), (10, 11), (11, 11),
                (4, 12), (5, 12), (6, 12), (7, 12), (8, 12), (9, 12), (10, 12), (11, 12),
                (5, 13), (6, 13), (7, 13), (8, 13), (9, 13), (10, 13),
                (6, 9), (7, 9), (8, 9), (9, 9)]
        for x, y in heap:
            put(x, y, dark2 if (x + y) % 3 == 0 else color)
        for x, y in [(6, 10), (8, 11), (10, 12)]:
            put(x, y, light)
    elif style == "crystal":  # faceted diamond
        for dy in range(-4, 5):
            span = 4 - abs(dy)
            for dx in range(-span, span + 1):
                c = light if dy < -1 else (dark2 if dy > 2 else color)
                put(8 + dx, 8 + dy, c)
        put(7, 6, (255, 255, 255))
        put(6, 7, light)
    elif style == "gel":  # translucent rounded blob
        for y in range(16):
            for x in range(16):
                dx, dy = x - 7.5, y - 7.5
                r = (dx * dx + dy * dy) ** 0.5
                if r <= 5.0:
                    a = 210 if r <= 4.2 else 150
                    c = light if (dx + dy) < -2 else (dark if (dx + dy) > 4 else color)
                    put(x, y, c, a)
        for x, y in [(6, 5), (5, 6), (7, 4)]:
            put(x, y, (255, 255, 255), 200)
    elif style == "thallus":  # leafy algal frond
        for x, y in [(8, 3), (7, 4), (8, 4), (9, 4), (6, 5), (7, 5), (8, 5), (9, 5), (10, 5),
                     (5, 6), (6, 6), (7, 6), (8, 6), (9, 6), (10, 6), (11, 6),
                     (5, 7), (6, 7), (7, 7), (8, 7), (9, 7), (10, 7), (11, 7),
                     (6, 8), (7, 8), (8, 8), (9, 8), (10, 8),
                     (6, 9), (7, 9), (8, 9), (9, 9), (7, 10), (8, 10), (9, 10), (8, 11)]:
            put(x, y, dark2 if (x * y) % 5 == 0 else color)
        put(8, 4, light)
        put(8, 8, light)
    elif style == "vial":  # sample vial: glass outline with colored mutagen inside
        for y in range(4, 13):
            for x in range(6, 10):
                edge = x in (6, 9) or y in (4, 12)
                if edge:
                    put(x, y, (208, 214, 222))
                else:
                    c = color if y >= 6 else (150, 190, 200)
                    put(x, y, light if (x + y) == 15 else c)
        put(7, 3, (208, 214, 222)); put(8, 3, (208, 214, 222))
        put(7, 5, (240, 246, 250), 200)
        put(7, 7, light); put(8, 9, dark)
    elif style == "chunk":  # irregular solid lump
        for y in range(5, 13):
            for x in range(4, 12):
                edge = (x in (4, 11) or y in (5, 12))
                put(x, y, dark if edge else (light if x + y < 14 else color))
    elif style == "sheet":  # flat slightly translucent slab, gloss on the top edge
        for y in range(6, 13):
            for x in range(3, 13):
                c = light if y == 6 else (dark if y == 12 else color)
                put(x, y, c, 240)
        put(4, 7, (255, 255, 255), 200)
        put(11, 7, dark2)
    elif style == "frame":  # structural frame: square ring with corner gussets
        for y in range(2, 14):
            for x in range(2, 14):
                if x > 4 and x < 11 and y > 4 and y < 11:
                    continue  # hollow center
                corner = (x < 7 and y < 7) or (x > 8 and y < 7) or (x < 7 and y > 8) or (x > 8 and y > 8)
                mid = x in (5, 10) or y in (5, 10)
                put(x, y, light if corner else (dark if mid else color))
        for i in range(2, 14):  # bevel: bright top/left, dark bottom/right
            put(2, i, (255, 255, 255) if i in (2, 3) else shade(color, 1.3))
            put(i, 2, (255, 255, 255) if i in (2, 3) else shade(color, 1.3))
            put(13, i, dark2)
            put(i, 13, dark2)
        for x, y in [(4, 4), (11, 4), (4, 11), (11, 11)]:  # rivets on the gussets
            put(x, y, (255, 255, 255), 230)
    return px


def make_item_texture(dst, style, color):
    if style == "mi_dust":
        # MI's own dust sprite, tinted: map template luminance onto a dark->light ramp
        # of the item color (mirrors how MI recolors its per-material dust textures)
        template_recolor(dst, 'tools/template_dust.png', color)
        return
    if style == "mi_wire":
        # MI's superconductor wire sprite recolored the same way
        template_recolor(dst, 'tools/template_wire.png', color)
        return
    if style == "mi_plate":
        # MI's own plate sprite (titanium plate), recolored with extra contrast so
        # the isometric top face / side bevels clearly read as 3D
        template_recolor(dst, 'tools/template_plate.png', color, low=0.30, span=1.15)
        return
    if style == "mi_pump":
        template_recolor(dst, 'tools/template_pump.png', color)
        return
    if style == "mi_motor":
        template_recolor(dst, 'tools/template_motor.png', color)
        return
    if style == "mi_pump_green":
        blue_to_green(dst, 'tools/template_pump_mi.png')
        return
    if style == "mi_motor_green":
        blue_to_green(dst, 'tools/template_motor_mi.png')
        return
    if style == "ingot":
        # the mod's own nichrome ingot sprite, recolored (repo-relative path)
        template_recolor(dst, 'src/main/resources/assets/modern_industrialization/textures/item/nichrome_ingot.png', color)
        return
    if style.startswith("bio_"):
        # MI's own circuit part sprites, recolored onto the bio green
        template_recolor(dst, f'tools/template_{style[4:]}.png', color)
        return
    write_png(dst, 16, 16, paint_item(style, color))


def template_recolor(dst, template, color, low=0.45, span=0.85):
    """Recolor a colored MI template onto the luminance ramp of `color`,
    preserving the template's shading (used for the dust/wire templates).
    `low`/`span` tune the ramp: darker floor and wider span = more contrast."""
    w, h, px = read_png(os.path.join(ROOT, template))
    for i in range(0, len(px), 4):
        a = px[i+3]
        if a == 0:
            continue
        lum = (px[i] * 299 + px[i+1] * 587 + px[i+2] * 114) // 1000
        factor = low + lum / 255 * span
        px[i] = min(255, int(color[0] * factor))
        px[i+1] = min(255, int(color[1] * factor))
        px[i+2] = min(255, int(color[2] * factor))
    write_png(dst, w, h, px)


def blue_to_green(dst, template):
    """MI's original sprite with only the blue parts hue-rotated to green:
    blue-dominant pixels swap their G and B channels (same luminance ramp,
    e.g. #396385 -> #398563), while grays, whites and copper stay untouched."""
    w, h, px = read_png(os.path.join(ROOT, template))
    for i in range(0, len(px), 4):
        r, g, b, a = px[i], px[i+1], px[i+2], px[i+3]
        if a and b > r + 12 and b >= g + 8:
            px[i+1], px[i+2] = b, g
    write_png(dst, w, h, px)


def make_rubble_texture(dst, color):
    """Full 16x16 block face: irregular angular chunks with dark gaps (quicklime)."""
    import random
    rng = random.Random(1963)
    px = bytearray(16 * 16 * 4)
    dark = shade(color, 0.55)
    mid = shade(color, 0.82)
    light = shade(color, 1.12)
    # a jittered grid of chunk cells, each one shaded by a top-left light
    for cy in range(4):
        for cx in range(4):
            jitter = rng.randint(-1, 1)
            for y in range(cy * 4, min(cy * 4 + 4, 16)):
                for x in range(cx * 4, min(cx * 4 + 4, 16)):
                    lx, ly = x - cx * 4, y - cy * 4
                    edge = lx == 3 or ly == 3
                    o = (y * 16 + x) * 4
                    if edge:
                        px[o], px[o+1], px[o+2] = dark
                    else:
                        r = rng.random()
                        c = light if (lx + ly + jitter < 2 and r < 0.7) else (mid if r < 0.85 else color)
                        px[o], px[o+1], px[o+2] = c
                    px[o+3] = 255
    write_png(dst, 16, 16, px)


def make_rubber_block_texture(dst, color):
    """Full 16x16 block face: smooth cured rubber with soft rounded bumps."""
    import math
    px = bytearray(16 * 16 * 4)
    light = shade(color, 1.14)
    dark = shade(color, 0.78)
    for y in range(16):
        for x in range(16):
            o = (y * 16 + x) * 4
            lum = 0
            for bx, by, br in ((4, 4, 4), (12, 5, 3.4), (7, 11, 4.4), (13, 12, 3.2), (1, 13, 2.6)):
                d = math.hypot(x - bx, y - by)
                if d < br:
                    lum = max(lum, (br - d) / br)
            c = light if lum > 0.55 else (color if lum > 0.15 else dark)
            px[o], px[o+1], px[o+2] = c
            px[o+3] = 255
    write_png(dst, 16, 16, px)


def make_cultivator_overlays(dst_dir):
    """16x16 MI machine front overlays: casing frame + petri dish motif,
    the active variant adds glowing culture pixels."""
    os.makedirs(dst_dir, exist_ok=True)
    for active in (False, True):
        px = bytearray(16 * 16 * 4)
        def put(x, y, r, g, b, a=255):
            if 0 <= x < 16 and 0 <= y < 16:
                o = (y * 16 + x) * 4
                px[o], px[o+1], px[o+2], px[o+3] = r, g, b, a
        # frame outline around the front face
        for k in range(2, 14):
            put(k, 2, 198, 198, 198); put(k, 13, 120, 120, 120)
            put(2, k, 188, 188, 188); put(13, k, 132, 132, 132)
        # petri dish in the center
        for y in range(16):
            for x in range(16):
                dx, dy = x - 7.5, y - 7.5
                r = (dx * dx + dy * dy) ** 0.5
                if r <= 3.6:
                    if r >= 2.7:
                        put(x, y, 154, 160, 168)          # rim
                    else:
                        shade = 1.1 - (x + y) * 0.012
                        put(x, y, int(96 * shade), int(188 * shade), int(110 * shade))
        # colony specks
        for cx, cy in ((6, 7), (9, 6), (8, 9), (7, 8)):
            put(cx, cy, 46, 120, 60)
        if active:  # culture glow bubbling up
            for cx, cy in ((5, 3), (10, 4), (7, 2)):
                put(cx, cy, 168, 255, 176)
            for cx, cy in ((6, 7), (9, 6), (8, 9), (7, 8)):
                put(cx, cy, 210, 255, 214)
        write_png(os.path.join(dst_dir, "overlay_front_active.png" if active else "overlay_front.png"), 16, 16, px)


def make_super_mixer_overlays(dst_dir):
    """16x16 front overlays: casing frame + stirring vessel motif."""
    os.makedirs(dst_dir, exist_ok=True)
    for active in (False, True):
        px = bytearray(16 * 16 * 4)

        def put(x, y, r, g, b, a=255):
            if 0 <= x < 16 and 0 <= y < 16:
                o = (y * 16 + x) * 4
                px[o], px[o+1], px[o+2], px[o+3] = r, g, b, a

        for k in range(2, 14):
            put(k, 2, 198, 198, 198); put(k, 13, 120, 120, 120)
            put(2, k, 188, 188, 188); put(13, k, 132, 132, 132)
        # stirring vessel: wide vat outline with liquid
        for x in range(4, 12):
            put(x, 4, 150, 156, 164)
            put(x, 11, 110, 116, 124)
        for y in range(5, 11):
            put(4, y, 130, 136, 144); put(11, y, 130, 136, 144)
            for x in range(5, 11):
                put(x, y, 120, 190, 140)
        # stir shaft + blade
        for y in range(4, 10):
            put(7, y, 210, 214, 220)
        put(6, 9, 90, 96, 104); put(8, 9, 90, 96, 104)
        if active:  # churning highlights
            put(5, 6, 190, 255, 200); put(9, 8, 190, 255, 200); put(7, 5, 160, 230, 170)
        write_png(os.path.join(dst_dir, "overlay_front_active.png" if active else "overlay_front.png"), 16, 16, px)


def write_json(path, obj):
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2)
        f.write("\n")


def main():
    ds = dishes()
    by_size = {}
    for ms, _ in ds:
        by_size[len(ms)] = by_size.get(len(ms), 0) + 1
    print("dish count by strain count:", dict(sorted(by_size.items())), "total:", len(ds))

    # fluids: blockstate + block model + bucket model + bucket texture
    for i in range(N):
        t = taxon(i)
        write_json(os.path.join(A, f"blockstates/{t}.json"), fluid_blockstate(t))
        write_json(os.path.join(A, f"models/block/{t}.json"),
                   {"parent": "minecraft:block/water"})
        write_json(os.path.join(A, f"models/item/{t}_bucket.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{t}_bucket"}})
        make_bucket_texture(os.path.join(A, f"textures/item/{t}_bucket.png"), tint(i))

    # petri dishes: item model + sector texture
    for ms, word in ds:
        write_json(os.path.join(A, f"models/item/petri_{word}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/petri_{word}"}})
        make_dish_texture(os.path.join(A, f"textures/item/petri_{word}.png"),
                          [tint(i) for i in ms])

    # bio-program items: item model + pixel-art texture
    for item_id, item_en, item_zh, color, style in BIO_ITEMS:
        write_json(os.path.join(A, f"models/item/{item_id}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{item_id}"}})
        make_item_texture(os.path.join(A, f"textures/item/{item_id}.png"), style, tuple(color))

    # bio-program fluids: blockstate + block model + bucket model + tinted bucket texture
    for fluid_id, _en, _zh, fluid_tint in BIO_FLUIDS:
        write_json(os.path.join(A, f"blockstates/{fluid_id}.json"), fluid_blockstate(fluid_id))
        rgb = ((fluid_tint >> 16) & 255, (fluid_tint >> 8) & 255, fluid_tint & 255)
        make_bucket_texture(os.path.join(A, f"textures/item/{fluid_id}_bucket.png"), rgb)

    # real placeable blocks: blockstate + cube_all model + item model + textured face
    # + loot table; the old plain-item model/texture is replaced/removed
    DATA = os.path.join(ROOT, "src/main/resources/data")
    for block_id, _en, _zh, color, painter in BIO_BLOCKS:
        write_json(os.path.join(A, f"blockstates/{block_id}.json"),
                   {"variants": {"": {"model": f"{MODID}:block/{block_id}"}}})
        write_json(os.path.join(A, f"models/block/{block_id}.json"),
                   {"parent": "minecraft:block/cube_all",
                    "textures": {"all": f"{MODID}:block/{block_id}"}})
        write_json(os.path.join(A, f"models/item/{block_id}.json"),
                   {"parent": f"{MODID}:block/{block_id}"})
        if painter == "rubble":
            make_rubble_texture(os.path.join(A, f"textures/block/{block_id}.png"), tuple(color))
        else:
            make_rubber_block_texture(os.path.join(A, f"textures/block/{block_id}.png"), tuple(color))
        stale = os.path.join(A, f"textures/item/{block_id}.png")
        if os.path.exists(stale):
            os.remove(stale)
        write_json(os.path.join(DATA, f"{MODID}/loot_table/blocks/{block_id}.json"),
                   {"type": "minecraft:block",
                    "pools": [{"rolls": 1,
                               "entries": [{"type": "minecraft:item", "name": f"{MODID}:{block_id}"}],
                               "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    # algae cultivator machine (MI addon machine registered in NIMachines)
    MI = os.path.join(ROOT, "src/main/resources/assets/modern_industrialization")
    write_json(os.path.join(MI, "blockstates/algae_cultivator.json"),
               {"variants": {"": {"model": "modern_industrialization:block/algae_cultivator"}}})
    write_json(os.path.join(MI, "models/block/algae_cultivator.json"),
               {"loader": "modern_industrialization:machine",
                "casing": "lv",
                "default_overlays": {
                    "fluid_auto": "modern_industrialization:block/overlays/fluid_auto",
                    "front": "modern_industrialization:block/machines/algae_cultivator/overlay_front",
                    "front_active": "modern_industrialization:block/machines/algae_cultivator/overlay_front_active",
                    "item_auto": "modern_industrialization:block/overlays/item_auto",
                    "output": "modern_industrialization:block/overlays/output"}})
    write_json(os.path.join(MI, "models/item/algae_cultivator.json"),
               {"parent": "modern_industrialization:block/algae_cultivator"})
    make_cultivator_overlays(os.path.join(MI, "textures/block/machines/algae_cultivator"))

    # super mixer machine (16-item-input mixing, registered in NIMachines)
    write_json(os.path.join(MI, "blockstates/super_mixer.json"),
               {"variants": {"": {"model": "modern_industrialization:block/super_mixer"}}})
    write_json(os.path.join(MI, "models/block/super_mixer.json"),
               {"loader": "modern_industrialization:machine",
                "casing": "lv",
                "default_overlays": {
                    "fluid_auto": "modern_industrialization:block/overlays/fluid_auto",
                    "front": "modern_industrialization:block/machines/super_mixer/overlay_front",
                    "front_active": "modern_industrialization:block/machines/super_mixer/overlay_front_active",
                    "item_auto": "modern_industrialization:block/overlays/item_auto",
                    "output": "modern_industrialization:block/overlays/output"}})
    write_json(os.path.join(MI, "models/item/super_mixer.json"),
               {"parent": "modern_industrialization:block/super_mixer"})
    make_super_mixer_overlays(os.path.join(MI, "textures/block/machines/super_mixer"))

    # TPV material parts: recolor every nichrome part texture onto the TPV tint
    # (items live in the modern_industrialization namespace, like the material registry)
    for part in TPV_PARTS + ["block"]:
        if part == "block":
            src = os.path.join(MI, "textures/block/nichrome_block.png")
            dst = os.path.join(MI, "textures/block/tpv_block.png")
        else:
            src = os.path.join(MI, f"textures/item/nichrome_{part}.png")
            dst = os.path.join(MI, f"textures/item/tpv_{part}.png")
        rel = os.path.relpath(src, ROOT).replace(chr(92), '/')
        template_recolor(dst, rel, TPV_COLOR)
        if part != "block":
            write_json(os.path.join(MI, f"models/item/tpv_{part}.json"),
                       {"parent": "minecraft:item/generated",
                        "textures": {"layer0": f"modern_industrialization:item/tpv_{part}"}})
    write_json(os.path.join(MI, "blockstates/tpv_block.json"),
               {"variants": {"": {"model": "modern_industrialization:block/tpv_block"}}})
    write_json(os.path.join(MI, "models/block/tpv_block.json"),
               {"parent": "minecraft:block/cube_all",
                "textures": {"all": "modern_industrialization:block/tpv_block"}})
    write_json(os.path.join(MI, "models/item/tpv_block.json"),
               {"parent": "modern_industrialization:block/tpv_block"})

    # tpv cable: same delegate model as MI's own cables (this was the missing piece
    # behind the broken tpv cable rendering); no texture needed
    write_json(os.path.join(MI, "models/item/tpv_cable.json"),
               {"delegate": "modern_industrialization:block/pipe",
                "loader": "modern_industrialization:delegate"})

    # advanced superconductor cable: MI's pipe delegate model renders the icon and
    # the placed cable from the cable tier, no custom texture needed
    write_json(os.path.join(MI, "models/item/advanced_superconductor_cable.json"),
               {"delegate": "modern_industrialization:block/pipe",
                "loader": "modern_industrialization:delegate"})

    # lang files
    en_path = os.path.join(A, "lang/en_us.json")
    zh_path = os.path.join(A, "lang/zh_cn.json")
    en = json.load(open(en_path, encoding="utf-8"))
    zh = json.load(open(zh_path, encoding="utf-8"))
    for i in range(N):
        t = taxon(i)
        en[f"fluid.{MODID}.{t}"] = t.capitalize()
        en[f"item.{MODID}.{t}_bucket"] = f"{t.capitalize()} Bucket"
        zh[f"fluid.{MODID}.{t}"] = f"{ALGAE[i][2]}藻"
        zh[f"item.{MODID}.{t}_bucket"] = f"{ALGAE[i][2]}藻桶"
    for ms, word in ds:
        W = word[0].upper() + word[1:]
        en[f"item.{MODID}.petri_{word}"] = f"{W} Petri Dish"
        zh[f"item.{MODID}.petri_{word}"] = "".join(ALGAE[i][2] for i in ms) + "藻培养皿"
    for item_id, item_en, item_zh, _color, _style in BIO_ITEMS:
        en[f"item.{MODID}.{item_id}"] = item_en
        zh[f"item.{MODID}.{item_id}"] = item_zh
    for fluid_id, fluid_en, fluid_zh, _tint in BIO_FLUIDS:
        en[f"fluid.{MODID}.{fluid_id}"] = fluid_en
        en[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_en} Bucket"
        zh[f"fluid.{MODID}.{fluid_id}"] = fluid_zh
        zh[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_zh}桶"
    for block_id, block_en, block_zh, _c, _p in BIO_BLOCKS:
        en[f"block.{MODID}.{block_id}"] = block_en
        zh[f"block.{MODID}.{block_id}"] = block_zh
        en.pop(f"item.{MODID}.{block_id}", None)  # they are blocks now
        zh.pop(f"item.{MODID}.{block_id}", None)
    en["tooltip.mi_nested_infinity.algae_cultivator.repeat.1"] = "Tracks the petri dishes used by its last 20 crafts."
    en["tooltip.mi_nested_infinity.algae_cultivator.repeat.2"] = "Each repeated dish multiplies craft time by 4x; beyond 4 repeats, another 10x."
    zh["tooltip.mi_nested_infinity.algae_cultivator.repeat.1"] = "记录本机最近 20 次合成所用的培养皿。"
    zh["tooltip.mi_nested_infinity.algae_cultivator.repeat.2"] = "每有一个重复培养皿，耗时×4；重复超过 4 个，耗时再×10。"
    for key in ("block", "item", "rei_categories"):
        en[f"{key}.modern_industrialization.algae_cultivator"] = "Algae Cultivator"
        zh[f"{key}.modern_industrialization.algae_cultivator"] = "藻类培养机"
    for key in ("block", "item", "rei_categories"):
        en[f"{key}.modern_industrialization.super_mixer"] = "Super Mixer"
        zh[f"{key}.modern_industrialization.super_mixer"] = "超级搅拌机"

    # TPV material parts + advanced superconductor cable (MI namespace items)
    tpv_names = {
        "tiny_dust": ("TPV Tiny Dust", "小撮TPV粉"), "dust": ("TPV Dust", "TPV粉"),
        "hot_ingot": ("TPV Hot Ingot", "TPV热锭"), "ingot": ("TPV Ingot", "TPV锭"),
        "nugget": ("TPV Nugget", "TPV粒"), "plate": ("TPV Plate", "TPV板"),
        "rod": ("TPV Rod", "TPV杆"), "gear": ("TPV Gear", "TPV齿轮"),
        "wire": ("TPV Wire", "TPV线"), "cable": ("TPV Cable", "TPV线缆"),
    }
    for part, (name_en, name_zh) in tpv_names.items():
        en[f"item.modern_industrialization.tpv_{part}"] = name_en
        zh[f"item.modern_industrialization.tpv_{part}"] = name_zh
    en["block.modern_industrialization.tpv_block"] = "TPV Block"
    zh["block.modern_industrialization.tpv_block"] = "TPV块"
    en["cable_tier_long.modern_industrialization.tpv"] = "TPV"
    zh["cable_tier_long.modern_industrialization.tpv"] = "TPV"
    en["cable_tier_short.modern_industrialization.tpv"] = "TPV"
    zh["cable_tier_short.modern_industrialization.tpv"] = "TPV"
    en["item.modern_industrialization.advanced_superconductor_cable"] = "Advanced Superconductor Cable"
    zh["item.modern_industrialization.advanced_superconductor_cable"] = "高级超导体线缆"
    en["cable_tier_long.modern_industrialization.advanced_superconductor"] = "Advanced Superconductor"
    zh["cable_tier_long.modern_industrialization.advanced_superconductor"] = "高级超导体"
    en["cable_tier_short.modern_industrialization.advanced_superconductor"] = "ASV"
    zh["cable_tier_short.modern_industrialization.advanced_superconductor"] = "ASV"
    write_json(en_path, en)
    write_json(zh_path, zh)

    # java snippet for NIFluids
    print("\n// NIFluids algae entries:")
    for i in range(N):
        t = taxon(i)
        print(f'    public static final Entry {t.upper()} = register("{t}", 0x{argb(tint(i)):08X});')

    # preview sheet: buckets + a few dishes per size, scaled 4x
    quads = [d for d in ds if len(d[0]) == 4]
    sample = [d for d in ds if len(d[0]) == 1] + quads + ds[24:26] + ds[54:56]
    tiles = [os.path.join(A, f"textures/item/{taxon(i)}_bucket.png") for i in range(N)]
    tiles += [os.path.join(A, f"textures/item/petri_{w}.png") for _, w in sample]
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
    preview = os.path.join(ROOT, "tools/preview_algae.png")
    write_png(preview, cols * cell, rows * cell, sheet)
    print(f"\nwrote preview: {preview}")
    print("all words:")
    for _, w in ds:
        print(" ", w)


if __name__ == "__main__":
    main()
