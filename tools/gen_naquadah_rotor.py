"""Generates the naquadah_rotor texture and model from MI's stainless-steel
rotor template, mapped onto the same green-teal ramp the other naquadah parts
use (shadow #1F7B5C -> light #87E8C8, sampled from naquadah_gear)."""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from gen_algae_assets import ROOT, read_png, write_png  # noqa: E402

MI = os.path.join(ROOT, "src/main/resources/assets/modern_industrialization")
SHADOW = (31, 123, 92)
LIGHT = (135, 232, 200)

w, h, px = read_png(os.path.join(ROOT, "tools/template_rotor.png"))
for i in range(0, len(px), 4):
    if not px[i + 3]:
        continue
    t = (px[i] * 299 + px[i + 1] * 587 + px[i + 2] * 114) / 255000
    for c in range(3):
        px[i + c] = round(SHADOW[c] + (LIGHT[c] - SHADOW[c]) * t)
write_png(os.path.join(MI, "textures/item/naquadah_rotor.png"), w, h, px)

model = os.path.join(MI, "models/item/naquadah_rotor.json")
with open(model, "w", encoding="utf-8") as f:
    f.write('{\n  "parent": "minecraft:item/generated",\n  "textures": {\n'
            '    "layer0": "modern_industrialization:item/naquadah_rotor"\n  }\n}\n')
print("wrote naquadah_rotor texture + model")
