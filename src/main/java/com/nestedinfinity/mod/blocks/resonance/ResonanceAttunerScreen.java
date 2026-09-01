package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.items.resonance.NINotes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The attuner screen: two slots over a shared background, plus a strip of the
 * eight Q8 colors where the tuning block's current register is highlighted.
 */
public class ResonanceAttunerScreen extends AbstractContainerScreen<ResonanceAttunerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/resonance_attuner.png");

    private static final int SWATCH_X = 18;
    private static final int SWATCH_Y = 58;
    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_STEP = 18;
    private static final int REGISTER_TEXT_Y = 76;

    public ResonanceAttunerScreen(ResonanceAttunerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 172;
        this.inventoryLabelY = 86;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int register = menu.registerColor();
        for (NINotes note : NINotes.ALL) {
            int x = leftPos + SWATCH_X + note.ordinal() * SWATCH_STEP;
            int y = topPos + SWATCH_Y;
            boolean active = note.ordinal() == register;
            int alpha = register < 0 ? 0x50 : (active ? 0xE0 : 0x60);
            graphics.fill(x, y, x + SWATCH_SIZE, y + SWATCH_SIZE, (alpha << 24) | note.tint);
            if (active) {
                graphics.renderOutline(x - 1, y - 1, SWATCH_SIZE + 2, SWATCH_SIZE + 2, 0xFFFFFFFF);
            }
            if (mouseX >= x && mouseX < x + SWATCH_SIZE && mouseY >= y && mouseY < y + SWATCH_SIZE) {
                graphics.renderTooltip(font, Component.translatable("color.mi_nested_infinity." + note.colorName()),
                        mouseX, mouseY);
            }
        }
        int textColor = register >= 0 ? 0x404040 : 0x703030;
        String label = register >= 0
                ? Component.translatable("container.mi_nested_infinity.resonance_attuner.register",
                        Component.translatable("color.mi_nested_infinity." + NINotes.ALL.get(register).colorName()))
                        .getString()
                : Component.translatable("container.mi_nested_infinity.resonance_attuner.no_register").getString();
        graphics.drawString(font, label, leftPos + (imageWidth - font.width(label)) / 2, topPos + REGISTER_TEXT_Y,
                textColor, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
