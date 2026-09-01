package com.nestedinfinity.mod.blocks;
import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.blocks.resonance.ResonanceAttunerBlock;
import com.nestedinfinity.mod.blocks.resonance.ResonanceAttunerBlockEntity;
import com.nestedinfinity.mod.blocks.resonance.ResonanceAttunerMenu;
import com.nestedinfinity.mod.blocks.resonance.TuningBlock;
import com.nestedinfinity.mod.blocks.superassembler.SuperAssemblerBlock;
import com.nestedinfinity.mod.blocks.superassembler.SuperAssemblerBlockEntity;
import com.nestedinfinity.mod.blocks.superassembler.SuperAssemblerMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

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

    // Resonance program (see blocks/resonance): the attuner machine and the
    // eight-state tuning register placed directly above it. Same no-mineable-tag
    // handling as the bulk solids.
    public static final DeferredBlock<TuningBlock> TUNING_BLOCK = NestedInfinity.BLOCKS.register("tuning_block",
            () -> new TuningBlock(BlockBehaviour.Properties.of().strength(5.0F, 6.0F)));
    public static final DeferredItem<BlockItem> TUNING_BLOCK_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("tuning_block", TUNING_BLOCK);

    public static final DeferredBlock<ResonanceAttunerBlock> RESONANCE_ATTUNER = NestedInfinity.BLOCKS.register("resonance_attuner",
            () -> new ResonanceAttunerBlock(BlockBehaviour.Properties.of().strength(6.0F, 8.0F)));
    public static final DeferredItem<BlockItem> RESONANCE_ATTUNER_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("resonance_attuner", RESONANCE_ATTUNER);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NestedInfinity.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonanceAttunerBlockEntity>> RESONANCE_ATTUNER_TYPE =
            BLOCK_ENTITY_TYPES.register("resonance_attuner",
                    () -> BlockEntityType.Builder.of(ResonanceAttunerBlockEntity::new, RESONANCE_ATTUNER.get()).build(null));

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, NestedInfinity.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ResonanceAttunerMenu>> RESONANCE_ATTUNER_MENU =
            MENUS.register("resonance_attuner", () -> IMenuTypeExtension.create(ResonanceAttunerMenu::new));

    // Optical program finale (see blocks/superassembler): the 10x10 grid
    // machine that fuses the hundred glow tubes into the optical qubit.
    public static final DeferredBlock<SuperAssemblerBlock> SUPER_ASSEMBLER = NestedInfinity.BLOCKS.register("super_assembler",
            () -> new SuperAssemblerBlock(BlockBehaviour.Properties.of().strength(6.0F, 8.0F)));
    public static final DeferredItem<BlockItem> SUPER_ASSEMBLER_ITEM = NestedInfinity.ITEMS.registerSimpleBlockItem("super_assembler", SUPER_ASSEMBLER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperAssemblerBlockEntity>> SUPER_ASSEMBLER_TYPE =
            BLOCK_ENTITY_TYPES.register("super_assembler",
                    () -> BlockEntityType.Builder.of(SuperAssemblerBlockEntity::new, SUPER_ASSEMBLER.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<SuperAssemblerMenu>> SUPER_ASSEMBLER_MENU =
            MENUS.register("super_assembler", () -> IMenuTypeExtension.create(SuperAssemblerMenu::new));

    public static void init() {}

    private NIBlocks() {}
}
