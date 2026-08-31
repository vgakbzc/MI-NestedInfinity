package com.nestedinfinity.mod;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Special items that do not belong to a material group.
 * Material parts (dust/ingot/plate/rod etc.) are registered in bulk via {@link NIMaterials}.
 */
public final class NIItems {
    public static final DeferredItem<Item> NAQUADAH_COMPUTING_UNIT = register("naquadah_computing_unit");
    public static final DeferredItem<Item> NEUTRON_SOURCE = register("neutron_source");
    public static final DeferredItem<Item> HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH = register("high_purity_monocrystalline_naquadah");

    public static final DeferredItem<Item> MONAZITE_RESIDUE = register("monazite_residue");
    public static final DeferredItem<Item> HEAVY_ELEMENT_RESIDUE_OXIDE = register("heavy_element_residue_oxide");
    public static final DeferredItem<Item> HEAVY_ELEMENT_RESIDUE_DUST = register("heavy_element_residue_dust");
    public static final DeferredItem<Item> PLATINIZED_ULTRAHEAVY_RESIDUE_DUST = register("platinized_ultraheavy_residue_dust");

    // Nichrome coil production chain
    public static final DeferredItem<Item> MICA_DUST = register("mica_dust");
    public static final DeferredItem<Item> MICA_INSULATOR_SHEET = register("mica_insulator_sheet");
    public static final DeferredItem<Item> PLASTIC_MICA_MIXTURE = register("plastic_mica_mixture");

    // Epoxy resin production chain
    public static final DeferredItem<Item> EPOXY_PLATE = register("epoxy_plate");
    public static final DeferredItem<Item> ION_EXCHANGE_RESIN = register("ion_exchange_resin");
    public static final DeferredItem<Item> PLATINUM_WIRE_MESH = register("platinum_wire_mesh");
    public static final DeferredItem<Item> ION_EXCHANGE_CATALYST = register("ion_exchange_catalyst");

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    static void init() {}

    private NIItems() {}
}
