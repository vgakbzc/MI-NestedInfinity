#!/usr/bin/env python3
"""Photonic (optical-circuit) tier assets: the four material families, the
element-chain items, the photonic parts, the DUV stepper machine, the HNIW /
neutronium program items, the giant matter ball with its oversized GUI model,
25 new fluids and the neutronium / optical_superconductor material parts.

Tables mirror com.nestedinfinity.mod.items.NIOpticalItems, NIFluids.OPTICAL
and NIMaterials (neutronium, optical_superconductor).

Run from the repo root:  python tools/gen_photonic_assets.py
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from gen_algae_assets import (A, MODID, ROOT, make_item_texture, make_bucket_texture,
                              read_png, write_json, write_png, fluid_blockstate, template_recolor)

DATA = os.path.join(ROOT, 'src/main/resources/data', MODID)
MI = os.path.join(ROOT, 'src/main/resources/assets/modern_industrialization')
MI_DUST = 'src/main/resources/assets/modern_industrialization/textures/item/nichrome_%s.png'

# ---------------------------------------------------------------- items
# (id, en, zh, rgb, style)
ITEMS = [
    # FFKM perfluoroelastomer
    ("r22_pyrolysis_catalyst", "R-22 Pyrolysis Catalyst", "R-22 裂解催化剂", (150, 190, 200), "mi_dust"),
    ("dcp_peroxide", "Dicumyl Peroxide", "过氧化二异丙苯", (240, 240, 235), "mi_dust"),
    ("taic_coagent", "TAIC Co-curing Agent", "TAIC 助交联剂", (235, 215, 140), "crystal"),
    ("perfluoro_cure_site_monomer", "Perfluoro Cure-site Monomer", "全氟固化点单体", (170, 225, 230), "crystal"),
    ("ffkm_gum", "FFKM Gum Stock", "FFKM 生胶", (232, 228, 222), "gel"),
    ("ffkm_sheet", "FFKM Sheet", "FFKM 板", (225, 222, 215), "sheet"),
    # PEEK
    ("p_fluorobenzoyl_chloride", "p-Fluorobenzoyl Chloride", "对氟苯甲酰氯", (200, 225, 215), "vial"),
    ("difluorobenzophenone", "4,4'-Difluorobenzophenone", "4,4'-二氟二苯甲酮", (235, 235, 230), "crystal"),
    ("diphenyl_sulfone", "Diphenyl Sulfone", "二苯砜", (230, 228, 220), "crystal"),
    ("potassium_carbonate", "Potassium Carbonate", "碳酸钾", (238, 238, 238), "mi_dust"),
    ("potassium_chloride", "Potassium Chloride", "氯化钾", (228, 228, 232), "mi_dust"),
    ("hydroquinone", "Hydroquinone", "对苯二酚", (232, 232, 232), "crystal"),
    ("peek_powder", "PEEK Powder", "PEEK 粉", (216, 190, 140), "mi_dust"),
    ("peek_plate", "PEEK Plate", "PEEK 板", (218, 192, 142), "mi_plate"),
    ("peek_insulator_sheet", "PEEK Insulator Sheet", "PEEK 绝缘片", (222, 198, 150), "sheet"),
    # semiconductor-grade chemicals
    ("ethylanthraquinone", "2-Ethylanthraquinone", "2-乙基蒽醌", (235, 195, 90), "crystal"),
    ("urea", "Urea", "尿素", (235, 235, 230), "crystal"),
    # photoresist chemistry
    ("polyhydroxystyrene_resin", "Polyhydroxystyrene Resin", "聚对羟基苯乙烯树脂", (225, 175, 100), "chunk"),
    ("alicyclic_acrylate_resin", "Alicyclic Acrylate Resin", "脂环族丙烯酸酯树脂", (235, 170, 200), "chunk"),
    ("triphenylsulfonium_pag", "Triphenylsulfonium PAG", "三苯基硫鎓产酸剂", (240, 238, 225), "crystal"),
    ("uv_photoinitiator", "UV Photoinitiator", "UV 光引发剂", (240, 230, 170), "crystal"),
    # HNIW / CL-20
    ("hbiw_crude", "Crude HBIW", "HBIW 粗品", (175, 145, 90), "mi_dust"),
    ("hbiw_crystal", "Recrystallized HBIW", "HBIW 晶体", (238, 232, 215), "crystal"),
    ("tadbiw", "TADBIW", "TADBIW", (235, 230, 210), "crystal"),
    ("taiw", "TAIW", "TAIW", (240, 238, 225), "crystal"),
    ("hniw_crude", "Crude HNIW Crystals", "HNIW 粗晶", (215, 220, 175), "mi_dust"),
    ("hniw_powder", "epsilon-HNIW Powder", "ε-HNIW 粉末", (248, 248, 242), "mi_dust"),
    ("dinitrogen_pentoxide", "Dinitrogen Pentoxide", "五氧化二氮", (240, 242, 248), "crystal"),
    ("sodium_azide", "Sodium Azide", "叠氮化钠", (235, 238, 240), "mi_dust"),
    ("lead_azide_detonator", "Lead Azide Detonator", "叠氮化铅雷管", (185, 180, 170), "chunk"),
    ("hniw_implosion_lens", "HNIW Implosion Lens", "HNIW 聚爆弹", (70, 68, 78), "chunk"),
    # neutronium program
    ("beryllium_reflector", "Beryllium Neutron Reflector", "铍中子反射层", (135, 145, 155), "sheet"),
    ("californium_initiator", "Californium Neutron Initiator", "锎中子源引发器", (225, 165, 70), "chunk"),
    ("fission_fragments", "Fission Fragments", "裂变碎片", (135, 122, 110), "mi_dust"),
    ("giant_matter_ball", "Giant Matter Ball", "巨型物质球", (168, 178, 196), "giant_ball"),
    # optical superconducting alloy
    ("optical_alloy_mixture", "Optical Superconductor Mixture", "光学超导混合料", (152, 132, 176), "mi_dust"),
    ("optical_superconductor_ingot", "Optical Superconductor Ingot", "光学超导锭", (176, 112, 240), "ingot"),
    ("optical_superconductor_wire", "Optical Superconductor Wire", "光学超导线", (186, 128, 246), "mi_wire"),
    # element chain
    ("zinc_flue_dust", "Zinc Smelter Flue Dust", "锌冶炼烟道灰", (145, 138, 128), "mi_dust"),
    ("germanium_dioxide", "Germanium Dioxide", "二氧化锗", (236, 236, 232), "mi_dust"),
    ("germanium_ingot", "Germanium Ingot", "锗锭", (202, 206, 212), "ingot"),
    ("germanium_wafer", "Germanium Wafer", "锗晶片", (212, 216, 222), "mi_plate"),
    ("coltan_concentrate", "Coltan Concentrate", "钽铌精矿", (78, 74, 84), "mi_dust"),
    ("niobium_pentoxide", "Niobium Pentoxide", "五氧化二铌", (238, 238, 240), "mi_dust"),
    ("niobium_ingot", "Niobium Ingot", "铌锭", (142, 152, 172), "ingot"),
    ("lithium_carbonate", "Lithium Carbonate", "碳酸锂", (240, 240, 238), "mi_dust"),
    ("erbium_oxide", "Erbium Oxide", "氧化铒", (226, 172, 182), "mi_dust"),
    ("europium_dust", "Europium Dust", "铕粉", (240, 202, 202), "mi_dust"),
    ("cerium_oxide", "Cerium Oxide", "氧化铈", (232, 202, 160), "mi_dust"),
    ("ruthenium_dioxide", "Ruthenium Dioxide", "二氧化钌", (62, 62, 82), "mi_dust"),
    ("ruthenium_dust", "Ruthenium Dust", "钌粉", (192, 197, 203), "mi_dust"),
    ("gst_target", "GST Sputter Target", "GST 合金靶", (212, 217, 227), "mi_plate"),
    ("gst_memory_cell", "GST Phase-change Cell", "GST 相变存储单元", (104, 108, 120), "frame"),
    # optics and photonics
    ("fused_silica_ingot", "Fused Silica Ingot", "熔融石英锭", (222, 233, 244), "ingot"),
    ("fused_silica_plate", "Fused Silica Plate", "石英玻璃板", (226, 236, 246), "sheet"),
    ("fiber_preform", "Optical Fiber Preform", "光纤预制棒", (205, 222, 236), "chunk"),
    ("erbium_doped_fiber", "Erbium-doped Fiber", "掺铒光纤", (230, 182, 192), "mi_wire"),
    ("lithium_niobate_wafer", "Lithium Niobate Wafer", "铌酸锂晶片", (182, 222, 192), "mi_plate"),
    ("electrooptic_modulator", "Electro-optic Modulator", "电光调制器", (72, 182, 172), "frame"),
    ("laser_diode", "Laser Diode", "激光二极管", (232, 62, 62), "crystal"),
    ("solid_state_laser", "Solid-state Laser", "固体激光器", (172, 42, 52), "chunk"),
    ("excimer_laser", "ArF Excimer Laser", "ArF 准分子激光器", (152, 92, 232), "chunk"),
    ("caf2_lens_array", "CaF2 Lens Array", "氟化钙透镜组", (192, 230, 235), "crystal"),
    ("optical_bench", "Optical Bench", "光学平台", (72, 77, 87), "mi_plate"),
    ("photomask_blank", "Photomask Blank", "空白光掩模", (222, 230, 240), "sheet"),
    ("photomask", "Photomask", "光掩模", (94, 99, 110), "sheet"),
    ("optical_transceiver", "Optical Transceiver", "光收发模块", (232, 172, 82), "frame"),
    ("single_photon_detector", "Superconducting Nanowire Detector", "超导纳米线单光子探测器", (94, 82, 134), "frame"),
    ("optical_waveguide", "Optical Waveguide", "光波导", (92, 202, 232), "mi_wire"),
    # DUV lithography process stream
    ("litho_substrate", "Lithography Substrate", "光刻基板", (216, 226, 236), "sheet"),
    ("coated_substrate", "Resist-coated Substrate", "涂胶基板", (222, 172, 92), "sheet"),
    ("exposed_substrate", "Exposed Substrate", "曝光基板", (192, 142, 222), "sheet"),
    ("developed_substrate", "Developed Substrate", "显影基板", (122, 162, 232), "sheet"),
    ("etched_substrate", "Etched Substrate", "刻蚀基板", (92, 192, 182), "sheet"),
    ("metallized_wafer", "Metallized Wafer", "金属化晶圆", (222, 142, 82), "mi_plate"),
    ("photonic_chip", "Photonic Chip", "光学芯片", (202, 142, 250), "crystal"),
    # processing units + large elites
    ("optical_random_access_memory", "Optical RAM", "光学随机存取存储器", (192, 132, 242), "bio_ram"),
    ("optical_memory_management_unit", "Optical MMU", "光学内存管理单元", (182, 122, 238), "bio_memory"),
    ("optical_arithmetic_logic_unit", "Optical ALU", "光学算术逻辑单元", (202, 142, 246), "bio_arithmetic"),
    ("large_elite_motor", "Large Elite Motor", "大型精英电机", (72, 182, 172), "mi_motor_green_large"),
    ("large_elite_pump", "Large Elite Pump", "大型精英泵", (64, 172, 164), "mi_pump_green_large"),
]

# ---------------------------------------------------------------- fluids
# (id, en, zh, argb tint) — mirrors NIFluids.OPTICAL
FLUIDS = [
    ("tetrafluoroethylene", "Tetrafluoroethylene", "四氟乙烯", 0xFFE8E8E0),
    ("hexafluoropropylene_oxide", "Hexafluoropropylene Oxide", "六氟环氧丙烷", 0xFFD8E8E0),
    ("perfluoromethyl_vinyl_ether", "Perfluoromethyl Vinyl Ether", "全氟甲基乙烯基醚", 0xFFC0E0DC),
    ("anthraquinone_working_solution", "Anthraquinone Working Solution", "蒽醌工作液", 0xFFD8B850),
    ("hydrogenated_working_solution", "Hydrogenated Working Solution", "氢化工作液", 0xFFB89838),
    ("crude_hydrogen_peroxide", "Crude Hydrogen Peroxide", "粗过氧化氢", 0xFFE0E8E4),
    ("electronic_grade_hydrogen_peroxide", "Electronic-grade Hydrogen Peroxide", "电子级过氧化氢", 0xFFF0F8F8),
    ("electronic_grade_nitric_acid", "Electronic-grade Nitric Acid", "电子级硝酸", 0xFFF0E8C8),
    ("electronic_grade_sulfuric_acid", "Electronic-grade Sulfuric Acid", "电子级硫酸", 0xFFF0E8D0),
    ("tmah_developer", "TMAH Developer", "TMAH 显影液", 0xFFE8E8D8),
    ("pgmea_solvent", "PGMEA Solvent", "PGMEA 溶剂", 0xFFF0E8D8),
    ("krf_photoresist", "KrF Photoresist", "KrF 光刻胶", 0xFFD8A860),
    ("arf_photoresist", "ArF Photoresist", "ArF 光刻胶", 0xFFC898B8),
    ("uv_optical_adhesive", "UV Optical Adhesive", "UV 光学胶", 0xFFE8D8E8),
    ("benzyl_chloride", "Benzyl Chloride", "苄氯", 0xFFE8E8C8),
    ("fluorobenzene", "Fluorobenzene", "氟苯", 0xFFE0F0E8),
    ("benzylamine", "Benzylamine", "苄胺", 0xFFE8E0B8),
    ("glyoxal", "Glyoxal", "乙二醛", 0xFFF0E8C8),
    ("ketene", "Ketene", "乙烯酮", 0xFFE0E0E0),
    ("acetic_anhydride", "Acetic Anhydride", "乙酸酐", 0xFFE8E8D0),
    ("mibk_solvent", "MIBK Solvent", "甲基异丁基酮", 0xFFE8E0D0),
    ("silicon_tetrachloride", "Silicon Tetrachloride", "四氯化硅", 0xFFD8E0E8),
    ("germanium_tetrachloride", "Germanium Tetrachloride", "四氯化锗", 0xFFE0E8D0),
    ("fluoroniobic_solution", "Fluoroniobic Solution", "氟铌酸溶液", 0xFFD8E0D8),
    ("neutron_fluid", "Neutron Fluid", "中子流体", 0xFFB8D8F0),
    ("molten_neutronium", "Molten Neutronium", "熔融中子素", 0xFFD8D8F8),
]

# ---------------------------------------------------------------- materials

NEUTRONIUM_COLOR = (216, 216, 248)
MAT_PARTS = ["tiny_dust", "dust", "hot_ingot", "ingot", "nugget", "plate", "rod", "gear"]
MAT_PART_NAMES = {
    "tiny_dust": ("Tiny Dust", "小撮粉"), "dust": ("Dust", "粉"),
    "hot_ingot": ("Hot Ingot", "热锭"), "ingot": ("Ingot", "锭"),
    "nugget": ("Nugget", "粒"), "plate": ("Plate", "板"),
    "rod": ("Rod", "杆"), "gear": ("Gear", "齿轮"), "wire": ("Wire", "线"),
}


# ---------------------------------------------------------------- painters

def make_giant_ball_texture(dst):
    """The giant matter ball: an over-dense sphere with bright mass-swirl
    bands and dark compression craters, drawn slightly oversized inside the
    16x16 canvas (the item model scales it further in GUI)."""
    px = bytearray(16 * 16 * 4)

    def put(x, y, c, a=255):
        if 0 <= x < 16 and 0 <= y < 16:
            o = (y * 16 + x) * 4
            px[o], px[o+1], px[o+2], px[o+3] = c[0], c[1], c[2], a

    def shade(c, f):
        return tuple(max(0, min(255, int(v * f))) for v in c)

    base = (168, 178, 196)
    light = shade(base, 1.45)
    dark = shade(base, 0.45)
    darker = shade(base, 0.3)
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            r = (dx * dx + dy * dy) ** 0.5
            if r <= 7.7:
                # upper-left illumination
                lum = 1.25 - (dx + dy) / 22 - r / 26
                c = shade(base, max(0.35, min(1.5, lum)))
                # swirling density bands
                band = (int(dx * 2 + dy) % 5)
                if band == 0:
                    c = shade(c, 1.18)
                elif band == 3:
                    c = shade(c, 0.82)
                put(x, y, c)
                # specular highlight
                if (x - 5) ** 2 + (y - 5) ** 2 <= 2:
                    put(x, y, light)
                # dark craters
                for cx, cy, cr in ((10, 9, 1.4), (6, 11, 1.1), (12, 5, 1.0)):
                    if (x - cx) ** 2 + (y - cy) ** 2 <= cr * cr:
                        put(x, y, darker if (x + y) % 2 else dark)
    write_png(dst, 16, 16, px)


def make_duv_stepper_overlays(dst_dir):
    """Front overlay of the DUV stepper: an objective-lens column over a
    wafer stage; the active variant adds the violet 193nm beam."""
    os.makedirs(dst_dir, exist_ok=True)

    def panel(active):
        px = bytearray(16 * 16 * 4)

        def put(x, y, r, g, b, a=255):
            o = (y * 16 + x) * 4
            px[o], px[o+1], px[o+2], px[o+3] = r, g, b, a

        body = (74, 78, 90)
        lens = (150, 210, 220)
        for y in range(3, 13):
            for x in range(3, 13):
                frame = x in (3, 12) or y in (3, 12)
                if frame:
                    put(x, y, 56, 58, 66)
                else:
                    put(x, y, *body)
        # the lens column down the middle
        for y in range(4, 9):
            put(7, y, *lens)
            put(8, y, *lens)
            put(7, y, 190, 235, 240) if y == 4 else None
        # wafer stage with alignment notches
        for x in range(5, 11):
            put(x, 10, 220, 226, 234)
            put(x, 11, 176, 182, 194)
        for gx in (5, 10):
            put(gx, 10, 120, 126, 138)
        if active:
            # the 193nm beam
            for y in range(4, 10):
                put(7, y, 198, 150, 255)
                put(8, y, 226, 190, 255)
            put(7, 10, 240, 210, 255)
            put(8, 10, 240, 210, 255)
            for x, y in [(5, 4), (10, 6), (4, 11)]:
                put(x, y, 230, 200, 255)
        # corner rivets
        for x, y in [(3, 3), (12, 3), (3, 12), (12, 12)]:
            put(x, y, 120, 124, 134)
        return px

    write_png(os.path.join(dst_dir, "overlay_front.png"), 16, 16, panel(False))
    write_png(os.path.join(dst_dir, "overlay_front_active.png"), 16, 16, panel(True))


# ---------------------------------------------------------------- main

def main():
    # 1. plain items
    for item_id, _en, _zh, color, style in ITEMS:
        if style == "giant_ball":
            make_giant_ball_texture(os.path.join(A, f"textures/item/{item_id}.png"))
        else:
            make_item_texture(os.path.join(A, f"textures/item/{item_id}.png"), style, tuple(color))

    # the giant matter ball model: scaled up in every hand so it towers over
    # the inventory slot (user request: it must look enormous in the GUI)
    write_json(os.path.join(A, "models/item/giant_matter_ball.json"), {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"{MODID}:item/giant_matter_ball"},
        "display": {
            "gui": {"rotation": [15, -25, 0], "translation": [0, 4, 0], "scale": [2.4, 2.4, 2.4]},
            "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [1.6, 1.6, 1.6]},
            "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1.8, 1.8, 1.8]},
            "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [1.6, 1.6, 1.6]},
            "firstperson_righthand": {"rotation": [0, -35, 0], "translation": [0, 2, 0], "scale": [1.5, 1.5, 1.5]},
        }})
    for item_id, _en, _zh, _c, _s in ITEMS:
        if item_id == "giant_matter_ball":
            continue
        write_json(os.path.join(A, f"models/item/{item_id}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{item_id}"}})

    # 2. fluids: the four-piece set per fluid
    for fluid_id, _en, _zh, tint in FLUIDS:
        write_json(os.path.join(A, f"blockstates/{fluid_id}.json"), fluid_blockstate(fluid_id))
        write_json(os.path.join(A, f"models/block/{fluid_id}.json"),
                   {"parent": "minecraft:block/water"})
        write_json(os.path.join(A, f"models/item/{fluid_id}_bucket.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"{MODID}:item/{fluid_id}_bucket"}})
        make_bucket_texture(os.path.join(A, f"textures/item/{fluid_id}_bucket.png"),
                            ((tint >> 16) & 255, (tint >> 8) & 255, tint & 255))

    # 3. neutronium material parts (nichrome sprites recolored)
    for part in MAT_PARTS + ["wire"]:
        template_recolor(os.path.join(MI, f"textures/item/neutronium_{part}.png"),
                         MI_DUST % part, NEUTRONIUM_COLOR)
        write_json(os.path.join(MI, f"models/item/neutronium_{part}.json"),
                   {"parent": "minecraft:item/generated",
                    "textures": {"layer0": f"modern_industrialization:item/neutronium_{part}"}})
    template_recolor(os.path.join(MI, "textures/block/neutronium_block.png"),
                     'src/main/resources/assets/modern_industrialization/textures/block/nichrome_block.png',
                     NEUTRONIUM_COLOR)
    write_json(os.path.join(MI, "blockstates/neutronium_block.json"),
               {"variants": {"": {"model": "modern_industrialization:block/neutronium_block"}}})
    write_json(os.path.join(MI, "models/block/neutronium_block.json"),
               {"parent": "minecraft:block/cube_all",
                "textures": {"all": "modern_industrialization:block/neutronium_block"}})
    write_json(os.path.join(MI, "models/item/neutronium_block.json"),
               {"parent": "modern_industrialization:block/neutronium_block"})
    # the optical superconductor cable renders through MI's pipe delegate
    write_json(os.path.join(MI, "models/item/optical_superconductor_cable.json"),
               {"delegate": "modern_industrialization:block/pipe",
                "loader": "modern_industrialization:delegate"})

    # 4. the DUV stepper machine (MI namespace like our other machines)
    make_duv_stepper_overlays(os.path.join(
        ROOT, 'src/main/resources/assets/modern_industrialization/textures/block/machines/duv_stepper'))
    write_json(os.path.join(MI, 'blockstates/duv_stepper.json'),
               {"variants": {"": {"model": "modern_industrialization:block/duv_stepper"}}})
    write_json(os.path.join(MI, 'models/block/duv_stepper.json'),
               {"loader": "modern_industrialization:machine",
                "casing": "superconductor",
                "default_overlays": {
                    "fluid_auto": "modern_industrialization:block/overlays/fluid_auto",
                    "front": "modern_industrialization:block/machines/duv_stepper/overlay_front",
                    "front_active": "modern_industrialization:block/machines/duv_stepper/overlay_front_active",
                    "item_auto": "modern_industrialization:block/overlays/item_auto",
                    "output": "modern_industrialization:block/overlays/output"}})
    write_json(os.path.join(MI, 'models/item/duv_stepper.json'),
               {"parent": "modern_industrialization:block/duv_stepper"})
    loot_mi = os.path.join(ROOT, 'src/main/resources/data/modern_industrialization/loot_table/blocks')
    write_json(os.path.join(loot_mi, "duv_stepper.json"),
               {"type": "minecraft:block",
                "pools": [{"rolls": 1,
                           "entries": [{"type": "minecraft:item",
                                        "name": "modern_industrialization:duv_stepper"}],
                           "conditions": [{"condition": "minecraft:survives_explosion"}]}]})

    # 5. lang
    en_path = os.path.join(A, 'lang/en_us.json')
    zh_path = os.path.join(A, 'lang/zh_cn.json')
    en = json.load(open(en_path, encoding='utf-8'))
    zh = json.load(open(zh_path, encoding='utf-8'))
    for item_id, item_en, item_zh, _c, _s in ITEMS:
        en[f"item.{MODID}.{item_id}"] = item_en
        zh[f"item.{MODID}.{item_id}"] = item_zh
    for fluid_id, fluid_en, fluid_zh, _t in FLUIDS:
        en[f"fluid.{MODID}.{fluid_id}"] = fluid_en
        en[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_en} Bucket"
        zh[f"fluid.{MODID}.{fluid_id}"] = fluid_zh
        zh[f"item.{MODID}.{fluid_id}_bucket"] = f"{fluid_zh}桶"
    for part, (p_en, p_zh) in MAT_PART_NAMES.items():
        en[f"item.modern_industrialization.neutronium_{part}"] = f"Neutronium {p_en}"
        zh[f"item.modern_industrialization.neutronium_{part}"] = f"中子素{p_zh}"
    en["block.modern_industrialization.neutronium_block"] = "Neutronium Block"
    zh["block.modern_industrialization.neutronium_block"] = "中子素块"
    for key in ("block.modern_industrialization.duv_stepper",
                "item.modern_industrialization.duv_stepper",
                "rei_categories.modern_industrialization.duv_stepper"):
        en[key] = "DUV Stepper"
        zh[key] = "深紫外光刻机"
    en["cable_tier_long.modern_industrialization.optical_superconductor"] = "Optical Superconductor"
    zh["cable_tier_long.modern_industrialization.optical_superconductor"] = "光学超导体"
    en["cable_tier_short.modern_industrialization.optical_superconductor"] = "Optical SC"
    zh["cable_tier_short.modern_industrialization.optical_superconductor"] = "光学超导"
    en["item.modern_industrialization.optical_superconductor_cable"] = "Optical Superconductor Cable"
    zh["item.modern_industrialization.optical_superconductor_cable"] = "光学超导线缆"
    json.dump(en, open(en_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=2)
    open(en_path, 'a', encoding='utf-8').write('\n')
    json.dump(zh, open(zh_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=2)
    open(zh_path, 'a', encoding='utf-8').write('\n')

    print('photonic assets written:',
          len(ITEMS), 'items,', len(FLUIDS), 'fluids, neutronium parts, duv_stepper')


if __name__ == '__main__':
    main()
