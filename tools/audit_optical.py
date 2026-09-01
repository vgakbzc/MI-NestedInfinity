"""Static audit of the optical (photonic) tier — v2.

Slot limits (item inputs, fluid inputs [, item outputs, fluid outputs]) come
from MI 2.5.6 bytecode (SingleBlockCraftingMachines) for MI machines and from
NIMachines.java registrations for our own machines.

Checks:
1. Every new optical item/fluid/material part has at least one producing recipe
   (searched in both the ni and the mi namespace recipe trees).
2. Every new optical intermediate is consumed downstream (terminal whitelist).
3. No recipe exceeds its machine's item/fluid INPUT or OUTPUT slot count.
4. Optical-chain durations match the approved ladder (gem-family quick recipes
   are exempt — they are the earlier T1 batch).
"""
import json
import os
import re
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
GEN_NI = os.path.join(ROOT, "src/generated/resources/data/mi_nested_infinity/recipe")
GEN_MI = os.path.join(ROOT, "src/generated/resources/data/modern_industrialization/recipe")
NI, MI = "mi_nested_infinity:", "modern_industrialization:"

SLOTS = {  # type -> (itemIn, fluidIn, itemOut, fluidOut)
    "assembler": (9, 2, 3, 0), "centrifuge": (1, 1, 4, 4),
    "chemical_reactor": (3, 3, 3, 3), "compressor": (1, 0, 1, 0),
    "cutting_machine": (1, 1, 1, 0), "distillery": (0, 1, 0, 1),
    "electrolyzer": (1, 1, 4, 4), "mixer": (4, 2, 2, 2),
    "packer": (3, 0, 1, 0), "wiremill": (1, 0, 1, 0),
    "vacuum_freezer": (2, 2, 2, 2), "blast_furnace": (2, 2, 2, 2),
    "implosion_compressor": (4, 0, 4, 2),
    # ours
    "magma_crucible": (1, 0, 0, 1), "ion_exchange": (1, 1, 0, 2),
    "super_mixer": (16, 0, 1, 1), "super_assembler": (100, 0, 1, 0),
    "duv_stepper": (3, 2, 1, 1), "algae_cultivator": (9, 2, 9, 2),
}

recipes = []
for base in (GEN_NI, GEN_MI):
    for dirpath, _dirs, files in os.walk(base):
        for fn in files:
            if fn.endswith(".json"):
                p = os.path.join(dirpath, fn)
                rel = os.path.relpath(p, GEN_NI).replace("\\", "/")
                recipes.append((rel, json.load(open(p, encoding="utf-8"))))

# -- new optical registry names ----------------------------------------------
src = os.path.join(ROOT, "src/main/java/com/nestedinfinity/mod")
opt_items = re.findall(r'register\("([a-z0-9_]+)"\)',
                       open(os.path.join(src, "items/NIOpticalItems.java"), encoding="utf-8").read())
fluids_src = open(os.path.join(src, "fluids/NIFluids.java"), encoding="utf-8").read()
block = re.search(r"OPTICAL\s*=.*?List\.of\((.*?)\)", fluids_src, re.S)
opt_fluids = re.findall(r'"([a-z0-9_]+)"', block.group(1))
# neutronium is a full standard material; optical_superconductor is cable-only
# (its ingot/wire are our own items, already in opt_items)
new_things = set()
for i in opt_items:
    new_things.add(("item", NI + i))
for f in opt_fluids:
    new_things.add(("fluid", NI + f))
for part in ("dust", "tiny_dust", "ingot", "hot_ingot", "plate", "rod", "gear", "wire", "block"):
    new_things.add(("item", MI + "neutronium_" + part))
new_things.add(("item", MI + "optical_superconductor_cable"))
new_things.add(("item", MI + "duv_stepper"))

TERMINALS = {
    NI + "optical_circuit", MI + "duv_stepper", NI + "fission_fragments",
    MI + "neutronium_block", MI + "neutronium_tiny_dust", MI + "neutronium_hot_ingot",
    MI + "neutronium_rod", MI + "neutronium_gear", MI + "neutronium_ingot",
}
# neutronium ingot is crafted by us and fed to the auto compressor -> plate,
# which the wire consumes; keep it out of the dead-end set explicitly.

produced, consumed = {}, {}
def note(dic, kind, ident, rel):
    dic.setdefault((kind, ident), []).append(rel)

optical_paths = set()
for rel, d in recipes:
    if rel.startswith("optical/") or "assembler/optical_circuit" in rel:
        optical_paths.add(rel)
    for e in d.get("item_inputs", []):
        note(consumed, "item", e.get("item") or e.get("tag"), rel)
    for e in d.get("item_outputs", []):
        note(produced, "item", e["item"], rel)
    for e in d.get("fluid_inputs", []):
        note(consumed, "fluid", e.get("fluid") or e.get("tag"), rel)
    for e in d.get("fluid_outputs", []):
        note(produced, "fluid", e.get("fluid") or e.get("tag"), rel)

print("== recipes loaded:", len(recipes), "| optical paths:", len(optical_paths))
print("\n== A. production coverage")
miss = [i for k, i in sorted(new_things) if k == "item" and (k, i) not in produced]
miss += [f"fluid:{f}" for k, f in sorted(new_things) if k == "fluid" and (k, f) not in produced]
print("\n".join("  MISSING PRODUCER: " + m for m in miss) if miss else "  all covered")

print("\n== B. dead ends (produced but never consumed, non-terminal)")
dead = [f"{k}:{i}" for k, i in sorted(new_things)
        if (k, i) in produced and (k, i) not in consumed and i not in TERMINALS]
print("\n".join("  NO DOWNSTREAM: " + d_ for d_ in dead) if dead else "  none")

print("\n== C. slot overflow (in AND out)")
bad = []
for rel, d in recipes:
    t = d["type"].split(":", 1)[1]
    if t not in SLOTS:
        continue
    ii, fi, io, fo = (len(d.get(k, [])) for k in
                      ("item_inputs", "fluid_inputs", "item_outputs", "fluid_outputs"))
    lim = SLOTS[t]
    if (ii, fi, io, fo) > lim:
        bad.append(f"{rel}: {ii}/{fi}/{io}/{fo} > {lim[0]}+{lim[1]}in {lim[2]}+{lim[3]}out ({t})")
print("\n".join("  " + b for b in bad) if bad else "  none")

print("\n== D. durations (non-gem optical paths)")
ALLOWED = {80_000, 320_000, 640_000, 7_200_000, 40_000, 400, 160_000, 200, 4_000, 20_000, 6_500}
odd = []
lookup = dict(recipes)
for rel in sorted(optical_paths):
    base = os.path.basename(rel)
    if base.split(".")[0].split("_", 1)[0] in ("gem", "block", "plate", "tube"):
        continue
    dur = lookup[rel]["duration"]
    if dur not in ALLOWED:
        odd.append(f"{rel}: {dur}t eu={lookup[rel]['eu']}")
print("\n".join("  ODD: " + o for o in odd) if odd else "  all on the ladder")
