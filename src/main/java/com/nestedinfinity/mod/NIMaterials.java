package com.nestedinfinity.mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aztech.modern_industrialization.materials.MIMaterials;
import net.minecraft.world.level.material.MapColor;

/**
 * All material groups of this mod. Adding a material = adding one field.
 */
public final class NIMaterials {
    public static final HashMap<String, NIMaterial> Materials = new HashMap<>() {};
    static {
        Materials.put("naquadah", new NIMaterial("naquadah").setTier(2));
        Materials.put("uranium_triplatinum", new NIMaterial("uranium_triplatinum").setTier(2));
        Materials.put("nichrome", new NIMaterial("nichrome").setTier(1).generateWire()
                .cancelRecipes(
                        "packer/block", "packer/cable", "packer/dust", "packer/ingot",
                        "craft/block_from_ingot", "craft/cable", "craft/dust_from_tiny_dust",
                        "craft/ingot_from_block", "craft/ingot_from_nugget", "craft/nugget_from_ingot",
                        "craft/tiny_dust_from_dust",
                        "assembler/cable_styrene_rubber", "assembler/cable_synthetic_rubber"));
    }

    public static final NIMaterial NAQUADAH = Materials.get("naquadah");
    public static final NIMaterial URANIUM_TRIPLATINUM = Materials.get("uranium_triplatinum");

    static void init() {
        Materials.values().forEach(NIMaterial::register);
    }

    private NIMaterials() {}
}
