package com.nestedinfinity.mod.material;

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
        Materials.put("naquadah", new NIMaterial("naquadah").setTier(2).generateRotor());
        Materials.put("uranium_triplatinum", new NIMaterial("uranium_triplatinum").setTier(2));
        Materials.put("nichrome", new NIMaterial("nichrome").setTier(1).generateWire()
                .cancelRecipes(
                        "packer/block", "packer/cable", "packer/dust", "packer/ingot",
                        "craft/block_from_ingot", "craft/cable", "craft/dust_from_tiny_dust",
                        "craft/ingot_from_block", "craft/ingot_from_nugget", "craft/nugget_from_ingot",
                        "craft/tiny_dust_from_dust",
                        "assembler/cable_styrene_rubber", "assembler/cable_synthetic_rubber"));
        // Titanium-platinum-vanadium alloy: smelted on the nichrome coil tier, feeds the
        // TPV coil (EBF tier above nichrome) and its 2M EU/t cable.
        Materials.put("tpv", new NIMaterial("tpv").setTier(2).generateWire(2_097_152L)
                .cancelRecipes(
                        "packer/block", "packer/cable", "packer/dust", "packer/ingot",
                        "craft/block_from_ingot", "craft/cable", "craft/dust_from_tiny_dust",
                        "craft/ingot_from_block", "craft/ingot_from_nugget", "craft/nugget_from_ingot",
                        "craft/tiny_dust_from_dust",
                        "assembler/cable_styrene_rubber", "assembler/cable_synthetic_rubber",
                        // replaced by the hand-written recipe (with the 25%-consumed large pump)
                        "assembler/cable"));
        // The advanced superconductor cable: a single cable part, 2^33 EU/t max transfer
        // (MI scales CableTier eu by x8 -> eu 2^30 = eight times MI's own superconductor
        // tier of 128M eu / 2^30 transfer; CableTier eu is a long, so no overflow).
        // Recipe is hand-written in NIRecipeProvider.superconductorChain.
        Materials.put("advanced_superconductor", new NIMaterial("advanced_superconductor")
                .generateCableOnly(8_589_934_592L));
        // Resonant-circuit materials. Trinium: smelted by FUSION (adamantium +
        // mithril), so the auto dust->hot ingot EBF recipe is canceled. Resonite:
        // the ender-eye alloy of the resonant program, smelted on the trinium
        // coil (setTier 4 = NICoils tier index 2, 32768 heat), 2^25 EU/t cable
        // (cable recipe hand-written with PI/FKM). The resonant superconductor:
        // cable-only at 2^36 EU/t, one tier of eight above the advanced one.
        Materials.put("trinium", new NIMaterial("trinium").skipEbfRecipes()
                // hot ingots have no source (fusion casts ingots directly), so the
                // auto vacuum-freezer hot ingot -> ingot recipe would be dead
                .cancelRecipes("vacuum_freezer/hot_ingot"));
        Materials.put("resonite", new NIMaterial("resonite").setTier(4).generateWire(33_554_432L)
                .cancelRecipes(
                        "packer/block", "packer/cable", "packer/dust", "packer/ingot",
                        "craft/block_from_ingot", "craft/cable", "craft/dust_from_tiny_dust",
                        "craft/ingot_from_block", "craft/ingot_from_nugget", "craft/nugget_from_ingot",
                        "craft/tiny_dust_from_dust",
                        "assembler/cable_styrene_rubber", "assembler/cable_synthetic_rubber",
                        "assembler/cable"));
        Materials.put("resonant_superconductor", new NIMaterial("resonant_superconductor")
                .generateCableOnly(68_719_476_736L));
    }

    public static final NIMaterial NAQUADAH = Materials.get("naquadah");
    public static final NIMaterial URANIUM_TRIPLATINUM = Materials.get("uranium_triplatinum");

    public static void init() {
        Materials.values().forEach(NIMaterial::register);
    }

    private NIMaterials() {}
}
