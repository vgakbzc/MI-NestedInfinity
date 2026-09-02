package com.nestedinfinity.mod.microverse;

import aztech.modern_industrialization.api.energy.EnergyApi;
import com.nestedinfinity.mod.NestedInfinity;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registration hub for the microverse program (spec doc section 3): the
 * neutronium machine casing, the twelve coreflame kinds, the nine time
 * dilation unit tiers, the projector controller, their block entities and
 * menus, and the MI energy capability hook on the controller.
 */
public final class MicroverseBlocks {
    public static final DeferredBlock<Block> NEUTRONIUM_MACHINE_CASING = NestedInfinity.BLOCKS.register(
            "neutronium_machine_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(8.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> NEUTRONIUM_MACHINE_CASING_ITEM =
            NestedInfinity.ITEMS.registerSimpleBlockItem("neutronium_machine_casing", NEUTRONIUM_MACHINE_CASING);

    /** The twelve coreflame kinds, in the same order as the singularities. */
    public static final List<DeferredBlock<CoreflameBlock>> COREFLAMES = MicroverseItems.SINGULARITIES.stream()
            .map(s -> NestedInfinity.BLOCKS.register("coreflame_" + s.blockSuffix(),
                    () -> new CoreflameBlock(
                            BlockBehaviour.Properties.of().strength(6.0F, 8.0F).requiresCorrectToolForDrops())))
            .toList();
    public static final List<DeferredItem<BlockItem>> COREFLAME_ITEMS = java.util.stream.IntStream
            .range(0, MicroverseItems.SINGULARITIES.size())
            .mapToObj(i -> NestedInfinity.ITEMS.registerSimpleBlockItem(
                    "coreflame_" + MicroverseItems.SINGULARITIES.get(i).blockSuffix(), COREFLAMES.get(i)))
            .toList();

    /** The nine time dilation unit tiers, index 0 == tier 1. */
    public static final List<DeferredBlock<TimeDilationUnitBlock>> TDUS = java.util.stream.IntStream.rangeClosed(1, 9)
            .mapToObj(tier -> NestedInfinity.BLOCKS.register("time_dilation_unit_t" + tier,
                    () -> new TimeDilationUnitBlock(tier,
                            BlockBehaviour.Properties.of().strength(6.0F, 8.0F).requiresCorrectToolForDrops())))
            .toList();
    public static final List<DeferredItem<BlockItem>> TDU_ITEMS = java.util.stream.IntStream.rangeClosed(1, 9)
            .mapToObj(tier -> NestedInfinity.ITEMS.registerSimpleBlockItem("time_dilation_unit_t" + tier,
                    TDUS.get(tier - 1)))
            .toList();

    public static final DeferredBlock<MicroverseProjectorBlock> MICROVERSE_PROJECTOR = NestedInfinity.BLOCKS.register(
            "microverse_projector",
            () -> new MicroverseProjectorBlock(BlockBehaviour.Properties.of().strength(8.0F, 10.0F)
                    .requiresCorrectToolForDrops().lightLevel(state -> state.getValue(MicroverseProjectorBlock.RUNNING)
                            ? 7 : 0)));
    public static final DeferredItem<BlockItem> MICROVERSE_PROJECTOR_ITEM =
            NestedInfinity.ITEMS.registerSimpleBlockItem("microverse_projector", MICROVERSE_PROJECTOR);

    /** The standalone no-energy machine that condenses matter into singularities. */
    public static final DeferredBlock<SingularityCatalyzerBlock> SINGULARITY_CATALYZER = NestedInfinity.BLOCKS.register(
            "singularity_catalyzer",
            () -> new SingularityCatalyzerBlock(BlockBehaviour.Properties.of().strength(6.0F, 8.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> SINGULARITY_CATALYZER_ITEM =
            NestedInfinity.ITEMS.registerSimpleBlockItem("singularity_catalyzer", SINGULARITY_CATALYZER);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NestedInfinity.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoreflameBlockEntity>> COREFLAME_TYPE =
            BLOCK_ENTITY_TYPES.register("coreflame", () -> BlockEntityType.Builder
                    .of(CoreflameBlockEntity::new, coreflameBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MicroverseProjectorBlockEntity>> PROJECTOR_TYPE =
            BLOCK_ENTITY_TYPES.register("microverse_projector",
                    () -> BlockEntityType.Builder.of(MicroverseProjectorBlockEntity::new,
                            MICROVERSE_PROJECTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SingularityCatalyzerBlockEntity>> CATALYZER_TYPE =
            BLOCK_ENTITY_TYPES.register("singularity_catalyzer",
                    () -> BlockEntityType.Builder.of(SingularityCatalyzerBlockEntity::new,
                            SINGULARITY_CATALYZER.get()).build(null));

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, NestedInfinity.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CoreflameMenu>> COREFLAME_MENU =
            MENUS.register("coreflame", () -> IMenuTypeExtension.create(CoreflameMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MicroverseMenu>> PROJECTOR_MENU =
            MENUS.register("microverse_projector", () -> IMenuTypeExtension.create(MicroverseMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SingularityCatalyzerMenu>> CATALYZER_MENU =
            MENUS.register("singularity_catalyzer", () -> IMenuTypeExtension.create(SingularityCatalyzerMenu::new));

    private static CoreflameBlock[] coreflameBlocks() {
        return COREFLAMES.stream().map(DeferredBlock::get).toArray(CoreflameBlock[]::new);
    }

    /** The coreflame kind index of a block, or -1. */
    public static int coreflameIndex(Block block) {
        for (int i = 0; i < COREFLAMES.size(); i++) {
            if (block == COREFLAMES.get(i).get()) {
                return i;
            }
        }
        return -1;
    }

    /** MI energy cables push EU into the controller through this capability. */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(EnergyApi.SIDED, PROJECTOR_TYPE.get(),
                (be, side) -> be.getEnergyStorage());
    }

    /** Wires the BE and menu registers to the mod event bus; call from the mod constructor. */
    public static void init(net.neoforged.bus.api.IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENUS.register(modEventBus);
        modEventBus.addListener(MicroverseBlocks::registerCapabilities);
    }

    private MicroverseBlocks() {}
}
