package com.nestedinfinity.mod;

import aztech.modern_industrialization.client.pipes.MIPipesClient;
import aztech.modern_industrialization.client.pipes.api.PipeRenderer;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;
import java.lang.reflect.Field;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.blocks.NIMachines;
import com.nestedinfinity.mod.blocks.resonance.ResonanceAttunerScreen;
import com.nestedinfinity.mod.blocks.superassembler.SuperAssemblerScreen;
import com.nestedinfinity.mod.fluids.NIFluids;
import com.nestedinfinity.mod.material.NIMaterial;
import com.nestedinfinity.mod.material.NIMaterials;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = NestedInfinity.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = NestedInfinity.MODID, value = Dist.CLIENT)
public class NestedInfinityClient {
    public NestedInfinityClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NIMachines.clientInit();
        registerAddonCableRenderers();
    }

    /**
     * MI's MIPipesClient.registerRenderers() snapshots the pipe types known at MI's own
     * constructor time, so cable types registered later (ours) never get a renderer and
     * drawing the pipe dies with an NPE. Re-register MI's own ELECTRICITY_RENDERER factory
     * (same instance, so its sprites are already collected) for each addon cable type.
     */
    private static void registerAddonCableRenderers() {
        try {
            Field field = MIPipesClient.class.getDeclaredField("ELECTRICITY_RENDERER");
            field.setAccessible(true);
            PipeRenderer.Factory factory = (PipeRenderer.Factory) field.get(null);
            for (NIMaterial material : NIMaterials.Materials.values()) {
                if (material.hasCable()) {
                PipeNetworkType type = PipeNetworkType.get(
                        ResourceLocation.fromNamespaceAndPath("modern_industrialization", material.name() + "_cable"));
                    PipeRenderer.register(type, factory);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register pipe renderers for addon cables", e);
        }
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (NIFluids.Entry entry : NIFluids.ALL) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "block/ni_fluid_still");
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "block/ni_fluid_flow");
                }

                @Override
                public int getTintColor() {
                    return entry.tint;
                }
            }, entry.type.get());
        }
    }

    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(NIBlocks.RESONANCE_ATTUNER_MENU.get(), ResonanceAttunerScreen::new);
        event.register(NIBlocks.SUPER_ASSEMBLER_MENU.get(), SuperAssemblerScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        NestedInfinity.LOGGER.info("HELLO FROM CLIENT SETUP");
        NestedInfinity.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    /** The algae cultivator explains its dish-repeat time penalty on its item tooltip. */
    @SubscribeEvent
    static void onItemTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(algaeCultivatorItem)) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.mi_nested_infinity.algae_cultivator.repeat.1")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.mi_nested_infinity.algae_cultivator.repeat.2")
                .withStyle(ChatFormatting.GRAY));
    }

    private static final Item algaeCultivatorItem = BuiltInRegistries.ITEM.get(
            ResourceLocation.fromNamespaceAndPath("modern_industrialization", "algae_cultivator"));
}
