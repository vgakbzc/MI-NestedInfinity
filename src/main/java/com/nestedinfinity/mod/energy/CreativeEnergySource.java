package com.nestedinfinity.mod.energy;

import com.nestedinfinity.mod.NestedInfinity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import aztech.modern_industrialization.api.energy.EnergyApi;

/** Registration hub for the creative energy source (see the block's javadoc). */
public final class CreativeEnergySource {
    public static final DeferredBlock<CreativeEnergySourceBlock> BLOCK = NestedInfinity.BLOCKS.register(
            "creative_energy_source", () -> new CreativeEnergySourceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD).strength(6.0F, 8.0F).requiresCorrectToolForDrops()
                    .lightLevel(state -> 15)));

    public static final DeferredItem<BlockItem> ITEM =
            NestedInfinity.ITEMS.registerSimpleBlockItem("creative_energy_source", BLOCK);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NestedInfinity.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEnergySourceBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register("creative_energy_source",
                    () -> BlockEntityType.Builder.of(CreativeEnergySourceBlockEntity::new, BLOCK.get()).build(null));

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(EnergyApi.SIDED, TYPE.get(),
                (be, side) -> CreativeEnergySourceBlockEntity.STORAGE);
    }

    /** Wires the BE register and the energy capability to the mod event bus. */
    public static void init(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(CreativeEnergySource::registerCapabilities);
    }

    private CreativeEnergySource() {}
}
