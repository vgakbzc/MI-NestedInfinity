package com.nestedinfinity.mod.microverse;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.nestedinfinity.mod.NestedInfinity;

/** One slot over a flame-tinted background; the title says which flame this is. */
public class CoreflameScreen extends AbstractContainerScreen<CoreflameMenu> {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/coreflame.png");

    private final ResourceLocation texture;

    public CoreflameScreen(CoreflameMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
        int flame = MicroverseBlocks.coreflameIndex(menu.getBlockEntity().getBlockState().getBlock());
        this.texture = flame >= 0
                ? ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID,
                        "textures/gui/coreflame_" + MicroverseItems.SINGULARITIES.get(flame).blockSuffix() + ".png")
                : DEFAULT_TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
