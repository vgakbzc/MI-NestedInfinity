package com.nestedinfinity.mod.material;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialBuilder;
import aztech.modern_industrialization.materials.MaterialRegistry;
import aztech.modern_industrialization.materials.part.MIParts;
import aztech.modern_industrialization.materials.property.MaterialHardness;
import aztech.modern_industrialization.materials.property.MaterialProperty;
import aztech.modern_industrialization.materials.recipe.StandardRecipes;
import aztech.modern_industrialization.materials.recipe.builder.MIRecipeBuilder;
import aztech.modern_industrialization.materials.set.MaterialBlockSet;
import aztech.modern_industrialization.materials.set.MaterialSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import com.nestedinfinity.mod.blocks.NICoils;

/**
 * A material group registered through MI's public material API
 * ({@link MaterialRegistry}): one call registers the full standard part set
 * (dust/ingot/plate/rod/gear/storage block...), auto c: tags, and MI's
 * StandardRecipes pipeline. Part items live in the modern_industrialization
 * namespace as {@code modern_industrialization:<name>_<part>}.
 *
 * <p>{@link #setTier} gates the EBF smelting step behind a coil tier
 * (MI's StandardRecipes has no blast furnace step, so we add our own),
 * and {@link #generateWire} adds the wire + energy cable parts.
 * Adding a material is one line in {@link NIMaterials}.
 */
public final class NIMaterial {
    /** Default cable transfer rate: 262144 EU/t (MI scales CableTier eu by x8). */
    public static final long DEFAULT_CABLE_TRANSFER = 262_144L;

    private final String name;
    /** Coil tier required to smelt the hot ingot: 0=cupronickel, 1=kanthal, 2=nichrome, then in NICoils order. */
    private int blastFurnaceTier = 1;
    private boolean wire;
    /** Adds the rotor part with a simplified plate+rod recipe (MI's own rotor needs blade/ring/bolt parts). */
    private boolean rotor;
    /** Cable-only material: registers just {@code <name>_cable} (e.g. the advanced superconductor cable). */
    private boolean cableOnly;
    private long cableTransfer = DEFAULT_CABLE_TRANSFER;
    private Material material;
    /** Auto-generated material recipe ids to suppress, e.g. "packer/block" (applied after StandardRecipes). */
    private final List<String> canceledRecipes = new ArrayList<>();

    public NIMaterial(String name) {
        this.name = name;
    }

    /**
     * Sets the coil tier required to smelt the hot ingot: 0=cupronickel, 1=kanthal, 2=nichrome,
     * then in NICoils order. Defaults to 1.
     * The EBF recipe is registered with that tier's EU; MI's EBF bans a recipe unless
     * {@code recipe.eu <= active coil tier EU}, so lower coils are locked out.
     */
    public NIMaterial setTier(int tier) {
        tierEu(tier); // fail fast on out-of-range tiers or EUs beyond the recipe int limit
        this.blastFurnaceTier = tier;
        return this;
    }

    /** Suppresses auto-generated material recipes, e.g. cancelRecipes("packer/block", "craft/cable"). */
    public NIMaterial cancelRecipes(String... recipeIds) {
        canceledRecipes.addAll(List.of(recipeIds));
        return this;
    }

    /** Adds the wire part plus an MI energy cable (262144 EU/t, modern_industrialization:&lt;name&gt;_cable). */
    public NIMaterial generateWire() {
        return generateWire(DEFAULT_CABLE_TRANSFER);
    }

    public NIMaterial generateWire(long maxTransfer) {
        this.wire = true;
        this.cableTransfer = maxTransfer;
        return this;
    }

    /**
     * Registers only an energy cable part ({@code modern_industrialization:<name>_cable}) with the
     * given max transfer — no dusts/ingots/wire. The cable's own recipe comes from the chain
     * recipes (NIRecipeProvider), not from the material system.
     */
    public NIMaterial generateCableOnly(long maxTransfer) {
        this.cableOnly = true;
        this.cableTransfer = maxTransfer;
        return this;
    }

    /**
     * Adds the rotor part ({@code modern_industrialization:<name>_rotor}). MI's auto-generated
     * rotor recipe is canceled and replaced by a simplified machined one (plate + rod), because
     * pulling in MI's blade/ring/bolt parts would cascade into four more part registrations.
     */
    public NIMaterial generateRotor() {
        this.rotor = true;
        return cancelRecipes("assembler/rotor", "craft/rotor");
    }

    /** Registers through MI's material API. Called once from {@link NIMaterials#init()}, after all options. */
    void register() {
        MaterialBuilder builder = new MaterialBuilder(englishDisplayName(), name)
                .set(MaterialProperty.SET, MaterialSet.METALLIC)
                .set(MaterialProperty.MEAN_RGB, meanRgb())
                .set(MaterialProperty.HARDNESS, MaterialHardness.AVERAGE);
        if (cableOnly) {
            builder.addParts(MIParts.CABLE.of(cableTier()));
        } else {
            builder.addParts(MIParts.TINY_DUST, MIParts.DUST, MIParts.HOT_INGOT, MIParts.INGOT,
                    MIParts.NUGGET, MIParts.PLATE, MIParts.ROD, MIParts.GEAR,
                    MIParts.BLOCK.of(MaterialBlockSet.IRON));
            if (rotor) {
                builder.addParts(MIParts.ROTOR);
            }
            if (wire) {
                builder.addParts(MIParts.WIRE);
                builder.addParts(MIParts.CABLE.of(cableTier()));
            }
        }
        builder.addRecipes(StandardRecipes::apply);
        builder.addRecipes(this::customRecipes);
        if (!canceledRecipes.isEmpty()) {
            // queued last so the ids exist when the cancel action runs
            builder.cancelRecipes(canceledRecipes.toArray(new String[0]));
        }
        this.material = MaterialRegistry.addMaterial(builder);
    }

    /** The built MI material handle. */
    public Material material() {
        return material;
    }

    /** The material name, e.g. "naquadah". */
    public String name() {
        return name;
    }

    /** Whether this material has an energy cable (via {@link #generateWire} or {@link #generateCableOnly}). */
    public boolean hasCable() {
        return wire || cableOnly;
    }

    /** Full item id of a part, e.g. naquadah + "dust" -> modern_industrialization:naquadah_dust */
    public String id(String part) {
        return "modern_industrialization:" + name + "_" + part;
    }

    /** Item id of the energy cable created by {@link #generateWire()}. */
    public String cableId() {
        return "modern_industrialization:" + name + "_cable";
    }

    /** Writes this material's accumulated MI recipes (StandardRecipes + custom) during datagen. */
    public void buildRecipes(RecipeOutput output) {
        material.registerRecipes.accept(output);
    }

    /**
     * c: common tags for every part (EMI/other-mod interop; the wiremill and gear
     * recipes consume c:plates/&lt;name&gt;). MI's own tag datagen does not cover
     * addon materials, so we emit the tag JSONs ourselves. Cable-only materials
     * have no standard parts and no tags.
     */
    public void tags(com.nestedinfinity.mod.datagen.NIRecipes r) {
        if (cableOnly) {
            return;
        }
        r.tag("c:dusts/" + name, id("dust"));
        r.tag("c:tiny_dusts/" + name, id("tiny_dust"));
        r.tag("c:ingots/" + name, id("ingot"));
        r.tag("c:nuggets/" + name, id("nugget"));
        r.tag("c:plates/" + name, id("plate"));
        r.tag("c:rods/" + name, id("rod"));
        if (rotor) {
            r.tag("c:rotors/" + name, id("rotor"));
        }
        r.tag("c:gears/" + name, id("gear"));
        r.tag("c:storage_blocks/" + name, "modern_industrialization:" + name + "_block");
    }

    /** EU of a coil tier: cupronickel=32, kanthal=128, then 8x steps from nichrome per NICoils. */
    private static int tierEu(int tier) {
        if (tier < 0 || tier - 2 >= NICoils.TIER_EUS.size()) {
            throw new IllegalArgumentException("Coil tier out of range: " + tier);
        }
        long eu = switch (tier) {
            case 0 -> 32L;
            case 1 -> 128L;
            default -> NICoils.TIER_EUS.get(tier - 2);
        };
        if (eu > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("EU " + eu + " of coil tier " + tier + " exceeds the MI recipe int limit; it cannot be used as a recipe gate");
        }
        return (int) eu;
    }

    private CableTier cableTier() {
        return new CableTier(name, englishDisplayName(), englishDisplayName(), cableTransfer / 8,
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", name + "_cable"), false);
    }

    /** Recipes MI's StandardRecipes does not cover. */
    private void customRecipes(MaterialBuilder.RecipeContext ctx) {
        // Tier-gated EBF smelting: dust -> hot ingot
        if (!cableOnly) {
            new MIRecipeBuilder(ctx, MIMachineRecipeTypes.BLAST_FURNACE, "dust_to_hot_ingot", tierEu(blastFurnaceTier), 400)
                    .addPartInput(MIParts.DUST, 1)
                    .addPartOutput(MIParts.HOT_INGOT, 1);
        }
        if (wire && !cableOnly) {
            // Cable: wire + mica insulator sheet + styrene rubber + liquid glass
            new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "cable", 8, 200)
                    .addPartInput(MIParts.WIRE, 1)
                    .addItemInput("mi_nested_infinity:mica_insulator_sheet", 1)
                    .addFluidInput("modern_industrialization:styrene_butadiene_rubber", 12)
                    .addFluidInput("mi_nested_infinity:liquid_glass", 72)
                    .addItemOutput(cableId(), 1);
        }
        if (rotor && !cableOnly) {
            // Rotor: a blade disk machined from a plate, mounted on a rod shaft
            new MIRecipeBuilder(ctx, MIMachineRecipeTypes.ASSEMBLER, "rotor_simple", 32, 200)
                    .addPartInput(MIParts.PLATE, 1)
                    .addPartInput(MIParts.ROD, 1)
                    .addPartOutput(MIParts.ROTOR, 1);
        }
    }

    /** "uranium_triplatinum" -> "UraniumTriplatinum"; used as the MI-side English name. */
    private String englishDisplayName() {
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("_")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return sb.toString();
    }

    /** Fallback tint for MI texture colorization; we ship hand-tinted textures instead. */
    private int meanRgb() {
        return switch (name) {
            case "naquadah" -> 0x6B8E5A;
            case "uranium_triplatinum" -> 0x9FC4D4;
            case "nichrome" -> 0xA8B0B8;
            case "tpv" -> 0x5AC26C; // aligned with the green tpv coil texture
            case "advanced_superconductor" -> 0x3A4E8C;
            default -> 0x9E9E9E;
        };
    }
}
