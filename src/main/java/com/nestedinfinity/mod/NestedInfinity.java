package com.nestedinfinity.mod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.nestedinfinity.mod.datagen.NIDataGen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.blocks.NICoils;
import com.nestedinfinity.mod.blocks.NIMachines;
import com.nestedinfinity.mod.fluids.NIFluids;
import com.nestedinfinity.mod.items.NICircuits;
import com.nestedinfinity.mod.items.NIItems;
import com.nestedinfinity.mod.items.algae.NIPetriDishes;
import com.nestedinfinity.mod.items.gems.NIGems;
import com.nestedinfinity.mod.items.resonance.NINotes;
import com.nestedinfinity.mod.material.NIMaterials;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NestedInfinity.MODID)
public class NestedInfinity {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mi_nested_infinity";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "mi_nested_infinity" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "mi_nested_infinity" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "mi_nested_infinity" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "mi_nested_infinity:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mi_nested_infinity")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> NICircuits.CRYSTAL_CIRCUIT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                NICircuits.ALL_TIERS.forEach(circuit -> output.accept(circuit.get()));
                NIUpgrades.ALL_TIERS.forEach(upgrade -> output.accept(upgrade.get()));
                // material parts + energy cables register into MI's own creative tab via the material API
                output.accept(NIItems.MONAZITE_RESIDUE.get());
                output.accept(NIItems.HEAVY_ELEMENT_RESIDUE_OXIDE.get());
                output.accept(NIItems.HEAVY_ELEMENT_RESIDUE_DUST.get());
                output.accept(NIItems.PLATINIZED_ULTRAHEAVY_RESIDUE_DUST.get());
                output.accept(NIItems.HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH.get());
                output.accept(NIItems.NEUTRON_SOURCE.get());
                output.accept(NIItems.NAQUADAH_COMPUTING_UNIT.get());
                output.accept(NIItems.MICA_DUST.get());
                output.accept(NIItems.MICA_INSULATOR_SHEET.get());
                output.accept(NIItems.PLASTIC_MICA_MIXTURE.get());
                output.accept(NIBlocks.PLASTIC_MICA_BLOCK_ITEM.get());
                NICoils.ALL.forEach(coil -> output.accept(coil.get()));
                NIFluids.ALGAE.forEach(algae -> output.accept(algae.bucket.get()));
                NIPetriDishes.ALL.forEach(dish -> output.accept(dish.item().get()));
                output.accept(NIItems.PROTEIN.get());
                output.accept(NIItems.AGAR.get());
                output.accept(NIFluids.NUTRIENT_AGAR.bucket.get());
                // resonant program: Q8 notes, separation cascade and circuit parts
                NINotes.ALL.forEach(note -> output.accept(note.item.get()));
                output.accept(NIBlocks.TUNING_BLOCK_ITEM.get());
                output.accept(NIBlocks.RESONANCE_ATTUNER_ITEM.get());
                output.accept(NIItems.NAQUIDE.get());
                output.accept(NIItems.ADAMANTIUM_INGOT.get());
                output.accept(NIItems.MITHRIL_INGOT.get());
                output.accept(NIItems.ADAMANTIUM_PLATE.get());
                output.accept(NIItems.MITHRIL_PLATE.get());
                output.accept(NIItems.TELLURIUM_DUST.get());
                output.accept(NIItems.PGM_RESIDUE.get());
                output.accept(NIItems.QUARTZ_OSCILLATOR.get());
                output.accept(NIItems.SAW_RESONATOR.get());
                output.accept(NIItems.POLYIMIDE_PLATE.get());
                output.accept(NIItems.FLUOROELASTOMER_SHEET.get());
                output.accept(NIItems.RESONANT_SUPERCONDUCTOR_TAPE.get());
                output.accept(NIItems.RESONANT_RANDOM_ACCESS_MEMORY.get());
                output.accept(NIItems.RESONANT_MEMORY_MANAGEMENT_UNIT.get());
                output.accept(NIItems.RESONANT_ARITHMETIC_LOGIC_UNIT.get());
                output.accept(NIItems.SASER.get());
                output.accept(NIItems.RESONANCE_CHAMBER.get());
                output.accept(NIItems.PHASE_LOCKED_LOOP.get());
                output.accept(NIFluids.RESONANT_MOTHER_LIQUOR.bucket.get());
                // optical program: the hundred-gem collection, its glow tubes,
                // the tube parts and the finale
                NIGems.ALL.forEach(gem -> output.accept(gem.gem().get()));
                NIGems.ALL.forEach(gem -> output.accept(gem.tube().get()));
                output.accept(NIItems.TRANSURANIC_BATTERY.get());
                output.accept(NIItems.CRYSTAL_DIODE.get());
                output.accept(NIItems.GRAPHENE_ELECTRODE.get());
                output.accept(NIItems.GRAPHENE_OXIDE.get());
                output.accept(NIItems.GRAPHENE.get());
                output.accept(NIItems.GRAPHENE_ROD.get());
                output.accept(NIItems.OPTICAL_QUBIT_COMPONENT.get());
                output.accept(NIFluids.NEON.bucket.get());
                output.accept(NIFluids.ARGON.bucket.get());
                output.accept(NIFluids.KRYPTON.bucket.get());
                output.accept(NIFluids.LIQUID_XENON.bucket.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public NestedInfinity(IEventBus modEventBus, ModContainer modContainer) {
        NICircuits.init();
        NIUpgrades.init();
        NIMaterials.init();
        NIItems.init();
        NIBlocks.init();
        NIFluids.init();
        NIPetriDishes.init();
        NINotes.init();
        NIGems.init();
        NICoils.init();
        NIMachines.init();

        // generate recipe/tag JSONs during runData
        modEventBus.addListener(NIDataGen::gatherData);

        NIFluids.FLUID_TYPES.register(modEventBus);
        NIFluids.FLUIDS.register(modEventBus);
        NIFluids.BLOCKS.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Block entity types (resonance attuner)
        NIBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        // Menus (resonance attuner GUI)
        NIBlocks.MENUS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
