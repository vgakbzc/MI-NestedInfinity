package com.nestedinfinity.mod.microverse;

import com.nestedinfinity.mod.NestedInfinity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Seed singularity and catalyst slots on the left, the singularity output on
 * the right, a progress bar filling the arrow groove between them, and one
 * status line under the arrow naming the targeted singularity kind in its
 * flame color together with the ritual state (or a seed mismatch). A
 * twelve-light strip under the title shows every ritual — lit in the flame's
 * color once completed (one-shot rituals stay lit: completing one permanently
 * unlocks that kind on this machine). Hovering the catalyst slot or a light
 * shows the kind's ritual.
 */
public class SingularityCatalyzerScreen extends AbstractContainerScreen<SingularityCatalyzerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/singularity_catalyzer.png");

    /** The light bar sits between the title and the slot row. */
    private static final int LIGHT_Y = 18;
    private static final int LIGHT_SIZE = 8;
    private static final int LIGHT_STEP = 10;
    private static final int LIGHT_X = (176 - (12 * LIGHT_STEP - (LIGHT_STEP - LIGHT_SIZE))) / 2;

    /** The progress bar fills this slice of the groove painted in the texture. */
    private static final int BAR_X = 74;
    private static final int BAR_Y = 40;
    private static final int BAR_MAX_W = 20;
    private static final int BAR_H = 8;
    private static final int STATUS_Y = 55;

    public SingularityCatalyzerScreen(SingularityCatalyzerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    // 1.21.1's AbstractContainerScreen.render no longer draws slot tooltips
    // itself — concrete screens call this after super.render, like vanilla's
    // ContainerScreen does.
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // the twelve ritual lights: flame color when completed/unlocked
        int mask = menu.data(SingularityCatalyzerMenu.DATA_READY_MASK);
        for (int i = 0; i < 12; i++) {
            int x = leftPos + LIGHT_X + i * LIGHT_STEP;
            int y = topPos + LIGHT_Y;
            boolean lit = (mask & (1 << i)) != 0;
            int color = lit ? MicroverseItems.SINGULARITIES.get(i).color() : 0x2A2A2E;
            graphics.fill(x, y, x + LIGHT_SIZE, y + LIGHT_SIZE, 0xFF000000 | color);
            graphics.renderOutline(x, y, LIGHT_SIZE, LIGHT_SIZE, lit ? 0xFFFFFFFF : 0xFF555560);
            if (mouseX >= x && mouseX < x + LIGHT_SIZE && mouseY >= y && mouseY < y + LIGHT_SIZE) {
                var kind = MicroverseItems.SINGULARITIES.get(i);
                java.util.List<Component> tooltip = java.util.List.of(
                        Component.translatable("item.mi_nested_infinity.singularity_" + kind.key()),
                        Component.translatable("container.mi_nested_infinity.singularity_catalyzer.condition."
                                + kind.key()).withStyle(net.minecraft.ChatFormatting.GRAY));
                graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            }
        }

        int progress = menu.data(SingularityCatalyzerMenu.DATA_PROGRESS);
        int width = Math.round(BAR_MAX_W * (float) progress / SingularityCatalyzerBlockEntity.TOTAL_TICKS);
        if (width > 0) {
            graphics.fill(leftPos + BAR_X, topPos + BAR_Y, leftPos + BAR_X + width,
                    topPos + BAR_Y + BAR_H, 0xFFE0E0E0);
        }

        ItemStack catalyst = menu.getBlockEntity().getCatalyst();
        int kind = SingularityCatalyzerBlockEntity.kindOf(catalyst);
        if (kind >= 0) {
            var singularity = MicroverseItems.SINGULARITIES.get(kind);
            int ritual = menu.data(SingularityCatalyzerMenu.DATA_RITUAL);
            Component line = Component.translatable("container.mi_nested_infinity.singularity_catalyzer.target",
                    Component.translatable("item.mi_nested_infinity.singularity_" + singularity.key()),
                    Component.translatable("container.mi_nested_infinity.singularity_catalyzer.ritual_"
                            + (ritual == 3 ? "seed" : ritual >= 2 ? "met" : "unmet")));
            graphics.drawString(font, line, leftPos + (imageWidth - font.width(line)) / 2, topPos + STATUS_Y,
                    singularity.color(), false);
        }

        if (this.hoveredSlot != null && this.hoveredSlot.index < 3) {
            if (this.hoveredSlot.index == SingularityCatalyzerBlockEntity.SEED_SLOT) {
                graphics.renderTooltip(font, Component.translatable(
                        "container.mi_nested_infinity.singularity_catalyzer.slot_seed"), mouseX, mouseY);
            } else if (this.hoveredSlot.index == SingularityCatalyzerBlockEntity.CATALYST_SLOT) {
                // show the hovered (or target) kind's ritual, or the slot hint when empty
                int hoveredKind = kind >= 0 ? kind : SingularityCatalyzerBlockEntity.kindOf(this.hoveredSlot.getItem());
                graphics.renderTooltip(font, hoveredKind >= 0
                        ? Component.translatable("container.mi_nested_infinity.singularity_catalyzer.condition."
                                + MicroverseItems.SINGULARITIES.get(hoveredKind).key())
                        : Component.translatable("container.mi_nested_infinity.singularity_catalyzer.slot_input"),
                        mouseX, mouseY);
            } else if (!this.hoveredSlot.hasItem()) {
                graphics.renderTooltip(font, Component.translatable(
                        "container.mi_nested_infinity.singularity_catalyzer.slot_output"), mouseX, mouseY);
            }
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
