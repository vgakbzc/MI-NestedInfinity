package com.nestedinfinity.mod.microverse;

import com.nestedinfinity.mod.NestedInfinity;
import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Microverse program items: the Heart of a Nonexistent World that opens a
 * projection, the twelve singularities (one per coreflame kind, see
 * {@link MicroverseBlocks#COREFLAMES}), and the nine tiers of universe
 * matter the projector harvests (see the spec doc section 4).
 *
 * <p>The heart and the singularities deliberately have no recipes yet —
 * they are the hooks of the next progression stage (spec doc section 3).
 */
public final class MicroverseItems {
    public static final DeferredItem<Item> HEART_OF_A_NONEXISTENT_WORLD = register("heart_of_a_nonexistent_world");

    /**
     * One singularity per coreflame, in the same order as the coreflames.
     * The color is the flame's signature tint (0xRRGGBB), shared by the
     * coreflame block textures, the coreflame GUI background and the
     * projector's coreflame light strip.
     */
    public record Singularity(String key, String blockSuffix, int color, DeferredItem<Item> item) {}

    public static final List<Singularity> SINGULARITIES = List.of(
            new Singularity("gold", "chrysalis_of_gold", 0xF0C450, register("singularity_gold")),
            new Singularity("rift", "bough_of_rift", 0x8A5FF0, register("singularity_rift")),
            new Singularity("shadow", "hand_of_shadow", 0x8C3C78, register("singularity_shadow")),
            new Singularity("justice", "scale_of_justice", 0xD2D6DC, register("singularity_justice")),
            new Singularity("whimsy", "coin_of_whimsy", 0xFA78C8, register("singularity_whimsy")),
            new Singularity("plenty", "chalice_of_plenty", 0x6ED278, register("singularity_plenty")),
            new Singularity("twilight", "eye_of_twilight", 0xC85FE1, register("singularity_twilight")),
            new Singularity("worlds", "throne_of_worlds", 0x50AAEB, register("singularity_worlds")),
            new Singularity("fury", "lance_of_fury", 0xEB4632, register("singularity_fury")),
            new Singularity("stone", "pillar_of_stone", 0x96968C, register("singularity_stone")),
            new Singularity("evernight", "veil_of_evernight", 0x2D3787, register("singularity_evernight")),
            new Singularity("infinity", "gate_of_infinity", 0x50DCC8, register("singularity_infinity")));

    /**
     * The nine tiers of universe matter, each named after the cosmological
     * epoch that produces it. Index 0 == tier 1 (quark-gluon plasma).
     */
    public static final List<DeferredItem<Item>> MATTERS = List.of(
            register("quark_gluon_plasma"),
            register("hadronic_matter"),
            register("primordial_hydrogen_helium"),
            register("recombined_atomic_gas"),
            register("dark_matter_halo"),
            register("population_iii_stellar_matter"),
            register("early_galactic_matter"),
            register("supernova_heavy_elements"),
            register("kilonova_ejecta"));

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static void init() {}

    private MicroverseItems() {}
}
