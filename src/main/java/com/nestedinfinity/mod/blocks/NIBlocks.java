package com.nestedinfinity.mod.blocks;
import com.nestedinfinity.mod.NestedInfinity;

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

    // Bulk solids that are real placeable blocks rather than plain items
    // (no requiresCorrectToolForDrops: they are not in any mineable tag, so that
    // flag would make them never drop).
    public static final DeferredBlock<Block> QUICKLIME = NestedInfinity.BLOCKS.register("quicklime",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 4.0F)));
    public static final DeferredItem<BlockItem> QUICKLIME_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("quicklime", QUICKLIME);

    public static final DeferredBlock<Block> SILICONE_RUBBER_BLOCK = NestedInfinity.BLOCKS.register("silicone_rubber_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 4.0F)));
    public static final DeferredItem<BlockItem> SILICONE_RUBBER_BLOCK_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("silicone_rubber_block", SILICONE_RUBBER_BLOCK);

    // Cured Celazole-type PBI block (see NIFluids.POLYBENZIMIDAZOLE), same handling
    // as the bulk solids above: no mineable tag, so no requiresCorrectToolForDrops
    public static final DeferredBlock<Block> POLYBENZIMIDAZOLE_BLOCK = NestedInfinity.BLOCKS.register("polybenzimidazole_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0F, 6.0F)));
    public static final DeferredItem<BlockItem> POLYBENZIMIDAZOLE_BLOCK_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("polybenzimidazole_block", POLYBENZIMIDAZOLE_BLOCK);

    public static void init() {}

    private NIBlocks() {}
}
