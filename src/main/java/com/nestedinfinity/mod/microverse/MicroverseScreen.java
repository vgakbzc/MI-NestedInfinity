package com.nestedinfinity.mod.microverse;

import com.nestedinfinity.mod.NestedInfinity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The projector screen: the heart slot beside the strip of twelve
 * coreflame lights, then a centered countdown, the time one matter ball
 * would buy, and one centered info line (accrued matter while running,
 * singularity return chance when ready). Matter balls and the universe
 * matter itself never touch this GUI — they flow through the structure's
 * item input and output hatches.
 */
public class MicroverseScreen extends AbstractContainerScreen<MicroverseMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/microverse_projector.png");

    /** Two status rows, then the slot/lights row below them. */
    private static final int STATUS_Y1 = 16;
    private static final int STATUS_Y2 = 26;

    /** The lights share their row with the heart slot (center y 46). */
    private static final int LIGHT_X = 39;
    private static final int LIGHT_Y = 42;
    private static final int LIGHT_SIZE = 8;
    private static final int LIGHT_STEP = 10;

    /** The three compact readout rows (10px pitch). */
    private static final int COUNTDOWN_Y = 58;
    private static final int BALL_TIME_Y = 68;
    private static final int INFO_Y = 78;

    public MicroverseScreen(MicroverseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 184;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        boolean running = menu.data(MicroverseMenu.DATA_RUNNING) == 1;
        boolean blocked = menu.data(MicroverseMenu.DATA_OUTPUT_BLOCKED) == 1;

        // the twelve coreflame lights: lit = the flame holds its singularity;
        // a running universe keeps the whole ring lit (all twelve burn)
        int mask = menu.data(MicroverseMenu.DATA_FLAME_MASK);
        for (int i = 0; i < 12; i++) {
            int x = leftPos + LIGHT_X + i * LIGHT_STEP;
            int y = topPos + LIGHT_Y;
            boolean lit = running || (mask & (1 << i)) != 0;
            int color = lit ? flameColor(i) : 0x2a2a2e;
            graphics.fill(x, y, x + LIGHT_SIZE, y + LIGHT_SIZE, 0xFF000000 | color);
            graphics.renderOutline(x, y, LIGHT_SIZE, LIGHT_SIZE, lit ? 0xFFFFFFFF : 0xFF555560);
            if (mouseX >= x && mouseX < x + LIGHT_SIZE && mouseY >= y && mouseY < y + LIGHT_SIZE) {
                graphics.renderTooltip(font, Component.translatable(
                        "block.mi_nested_infinity.coreflame_"
                                + MicroverseItems.SINGULARITIES.get(i).blockSuffix()),
                        mouseX, mouseY);
            }
        }

        int tier = menu.data(MicroverseMenu.DATA_TIER);
        boolean ok = menu.data(MicroverseMenu.DATA_OK) == 1;

        // status: a two-line block — state on the first row, detail on the
        // second (a blocked output outranks the plain "projecting" note)
        Component status1;
        Component status2 = null;
        int statusColor;
        if (running && blocked) {
            status1 = Component.translatable("container.mi_nested_infinity.microverse_projector.output_full");
            status2 = Component.translatable("container.mi_nested_infinity.microverse_projector.output_full_hint");
            statusColor = 0x803030;
        } else if (running) {
            status1 = Component.translatable("container.mi_nested_infinity.microverse_projector.running");
            status2 = Component.translatable("item.mi_nested_infinity."
                    + MicroverseItems.MATTERS.get(Math.max(0, tier - 1)).getId().getPath());
            statusColor = 0x208040;
        } else if (ok) {
            status1 = Component.translatable("container.mi_nested_infinity.microverse_projector.ready");
            status2 = Component.translatable("container.mi_nested_infinity.microverse_projector.ready_hint");
            statusColor = 0x305080;
        } else {
            status1 = Component.translatable("container.mi_nested_infinity.microverse_projector.problem_"
                    + problemKey(menu));
            statusColor = 0x803030;
        }
        graphics.drawString(font, status1, leftPos + 8, topPos + STATUS_Y1, statusColor, false);
        if (status2 != null) {
            graphics.drawString(font, status2, leftPos + 8, topPos + STATUS_Y2, statusColor, false);
        }

        if (running) {
            int remaining = menu.data(MicroverseMenu.DATA_REMAINING);
            drawCentered(graphics, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.countdown",
                    String.format("%.1f", remaining / 20.0)),
                    COUNTDOWN_Y, blocked ? 0x803030 : 0x404040);
            // what the next auto-fed matter ball is worth (0.5/n of base time)
            drawCentered(graphics, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.ball_time",
                    String.format("%.1f", menu.data(MicroverseMenu.DATA_NEXT_BALL_TICKS) / 20.0)),
                    BALL_TIME_Y, 0x604030);
            drawCentered(graphics, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.accrued",
                    menu.data(MicroverseMenu.DATA_ACCRUED)),
                    INFO_Y, 0x404040);
        } else if (ok) {
            drawCentered(graphics, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.return_chance",
                    menu.data(MicroverseMenu.DATA_RETURN_CHANCE)),
                    INFO_Y, 0x604040);
        }

        // a hint for the heart slot while it is empty (occupied slots get
        // the item's tooltip from renderTooltip below)
        if (this.hoveredSlot != null && this.hoveredSlot.index == 0 && !this.hoveredSlot.hasItem()) {
            graphics.renderTooltip(font, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.slot_heart"), mouseX, mouseY);
        }

        // 1.21.1's AbstractContainerScreen.render no longer draws slot
        // tooltips itself — draw them after our own labels, like vanilla's
        // ContainerScreen does
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private static String problemKey(MicroverseMenu menu) {
        String problem = menu.getBlockEntity().structureProblem();
        return problem == null || problem.isEmpty() || "unchecked".equals(problem) ? "unchecked" : problem;
    }

    private void drawCentered(GuiGraphics graphics, Component text, int y, int color) {
        graphics.drawString(font, text, leftPos + (imageWidth - font.width(text)) / 2, topPos + y, color, false);
    }

    /** The flame's own signature color (see {@link MicroverseItems.SINGULARITIES}). */
    private static int flameColor(int index) {
        return MicroverseItems.SINGULARITIES.get(index).color();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
