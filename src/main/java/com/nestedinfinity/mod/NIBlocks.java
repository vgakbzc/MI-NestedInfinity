package com.nestedinfinity.mod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Standalone blocks that do not belong to a material group.
 */
public final class NIBlocks {
    public static final DeferredBlock<Block> PLASTIC_MICA_BLOCK = NestedInfinity.BLOCKS.register("plastic_mica_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> PLASTIC_MICA_BLOCK_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("plastic_mica_block", PLASTIC_MICA_BLOCK);

    public static final DeferredBlock<Block> CURED_EPOXY_RESIN_BLOCK = NestedInfinity.BLOCKS.register("cured_epoxy_resin_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> CURED_EPOXY_RESIN_BLOCK_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("cured_epoxy_resin_block", CURED_EPOXY_RESIN_BLOCK);

    static void init() {}

    private NIBlocks() {}
}
