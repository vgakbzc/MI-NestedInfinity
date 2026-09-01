package com.nestedinfinity.mod.items;
import com.nestedinfinity.mod.NestedInfinity;

import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public final class NICircuits {
    public static final DeferredItem<Item> CRYSTAL_CIRCUIT = register("crystal_circuit");
    public static final DeferredItem<Item> WETWARE_CIRCUIT = register("wetware_circuit");
    public static final DeferredItem<Item> RESONANT_CIRCUIT = register("resonant_circuit");
    public static final DeferredItem<Item> OPTICAL_CIRCUIT = register("optical_circuit");
    public static final DeferredItem<Item> ELECTROMAGNETIC_INTERFERENCE_CIRCUIT = register("electromagnetic_interference_circuit");
    public static final DeferredItem<Item> AWAKENED_DRACONIC_CIRCUIT = register("awakened_draconic_circuit");
    public static final DeferredItem<Item> PARACAUSAL_CIRCUIT = register("paracausal_circuit");
    public static final DeferredItem<Item> MULTIVERSE_PARALLEL_COMPUTATIONAL_CIRCUIT = register("multiverse_parallel_computational_circuit");
    public static final DeferredItem<Item> C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_CIRCUIT = register("24d_non_euclidean_space_time_folding_circuit");
    public static final DeferredItem<Item> AMA_CIRCUIT = register("ama_circuit");

    public static final DeferredItem<Item> CRYSTAL_CIRCUIT_BOARD = register("crystal_circuit_board");
    public static final DeferredItem<Item> WETWARE_CIRCUIT_BOARD = register("wetware_circuit_board");
    public static final DeferredItem<Item> RESONANT_CIRCUIT_BOARD = register("resonant_circuit_board");
    public static final DeferredItem<Item> OPTICAL_CIRCUIT_BOARD = register("optical_circuit_board");
    public static final DeferredItem<Item> ELECTROMAGNETIC_INTERFERENCE_CIRCUIT_BOARD = register("electromagnetic_interference_circuit_board");
    public static final DeferredItem<Item> AWAKENED_DRACONIC_CIRCUIT_BOARD = register("awakened_draconic_circuit_board");
    public static final DeferredItem<Item> PARACAUSAL_CIRCUIT_BOARD = register("paracausal_circuit_board");
    public static final DeferredItem<Item> MULTIVERSE_PARALLEL_COMPUTATIONAL_CIRCUIT_BOARD = register("multiverse_parallel_computational_circuit_board");
    public static final DeferredItem<Item> C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_CIRCUIT_BOARD = register("24d_non_euclidean_space_time_folding_circuit_board");
    public static final DeferredItem<Item> AMA_CIRCUIT_BOARD = register("ama_circuit_board");

    public static final List<DeferredItem<Item>> ALL_TIERS = List.of(
            CRYSTAL_CIRCUIT, CRYSTAL_CIRCUIT_BOARD,
            WETWARE_CIRCUIT, WETWARE_CIRCUIT_BOARD,
            RESONANT_CIRCUIT, RESONANT_CIRCUIT_BOARD,
            OPTICAL_CIRCUIT, OPTICAL_CIRCUIT_BOARD,
            ELECTROMAGNETIC_INTERFERENCE_CIRCUIT, ELECTROMAGNETIC_INTERFERENCE_CIRCUIT_BOARD,
            AWAKENED_DRACONIC_CIRCUIT, AWAKENED_DRACONIC_CIRCUIT_BOARD,
            PARACAUSAL_CIRCUIT, PARACAUSAL_CIRCUIT_BOARD,
            MULTIVERSE_PARALLEL_COMPUTATIONAL_CIRCUIT, MULTIVERSE_PARALLEL_COMPUTATIONAL_CIRCUIT_BOARD,
            C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_CIRCUIT, C24D_NON_EUCLIDEAN_SPACE_TIME_FOLDING_CIRCUIT_BOARD,
            AMA_CIRCUIT, AMA_CIRCUIT_BOARD);

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static void init() {}

    private NICircuits() {}
}
