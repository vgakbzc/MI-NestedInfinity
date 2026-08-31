package com.nestedinfinity.mod;

import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public final class NIUpgrades {
    public static final DeferredItem<Item> CRYSTAL_UPGRADE = register("crystal_upgrade");
    public static final DeferredItem<Item> WETWARE_UPGRADE = register("wetware_upgrade");
    public static final DeferredItem<Item> RESONANT_UPGRADE = register("resonant_upgrade");
    public static final DeferredItem<Item> OPTICAL_UPGRADE = register("optical_upgrade");
    public static final DeferredItem<Item> ELECTROMAGNETIC_INTERFERENCE_UPGRADE = register("electromagnetic_interference_upgrade");
    public static final DeferredItem<Item> AWAKENED_DRACONIC_UPGRADE = register("awakened_draconic_upgrade");
    public static final DeferredItem<Item> PARACAUSAL_UPGRADE = register("paracausal_upgrade");
    public static final DeferredItem<Item> MULTIVERSE_PARALLEL_COMPUTATIONAL_UPGRADE = register("multiverse_parallel_computational_upgrade");
    public static final DeferredItem<Item> C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_UPGRADE = register("24d_non_euclidean_space_time_folding_upgrade");
    public static final DeferredItem<Item> AMA_UPGRADE = register("ama_upgrade");

    public static final List<DeferredItem<Item>> ALL_TIERS = List.of(
            CRYSTAL_UPGRADE,
            WETWARE_UPGRADE,
            RESONANT_UPGRADE,
            OPTICAL_UPGRADE,
            ELECTROMAGNETIC_INTERFERENCE_UPGRADE,
            AWAKENED_DRACONIC_UPGRADE,
            PARACAUSAL_UPGRADE,
            MULTIVERSE_PARALLEL_COMPUTATIONAL_UPGRADE,
            C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_UPGRADE,
            AMA_UPGRADE);

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static long extraEu(Item item) {
        if (item == CRYSTAL_UPGRADE.get()) return 8_000_000_000L;
        if (item == WETWARE_UPGRADE.get()) return 64_000_000_000L;
        if (item == RESONANT_UPGRADE.get()) return 512_000_000_000L;
        if (item == OPTICAL_UPGRADE.get()) return 4_096_000_000_000L;
        if (item == ELECTROMAGNETIC_INTERFERENCE_UPGRADE.get()) return 32_768_000_000_000L;
        if (item == AWAKENED_DRACONIC_UPGRADE.get()) return 262_144_000_000_000L;
        if (item == PARACAUSAL_UPGRADE.get()) return 2_097_152_000_000_000L;
        if (item == MULTIVERSE_PARALLEL_COMPUTATIONAL_UPGRADE.get()) return 16_777_216_000_000_000L;
        if (item == C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_UPGRADE.get()) return 134_217_728_000_000_000L;
        if (item == AMA_UPGRADE.get()) return 1_073_741_824_000_000_000L;
        return 0L;
    }

    static void init() {}

    private NIUpgrades() {}
}
