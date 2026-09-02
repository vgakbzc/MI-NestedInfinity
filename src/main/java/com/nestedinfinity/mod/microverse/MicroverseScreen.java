package com.nestedinfinity.mod.microverse;

import com.nestedinfinity.mod.NestedInfinity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The projector screen: one row with the heart slot, the strip of twelve
 * coreflame lights and the output slot, then a centered countdown, the ball
 * slot with the "extend" button beside it, and one centered info line
 * (accrued matter while running, singularity return chance when ready).
 */
public class MicroverseScreen extends AbstractContainerScreen<MicroverseMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/microverse_projector.png");

    /** The lights share their row with the heart and output slots (center y 34). */
    private static final int LIGHT_X = 29;
    private static final int LIGHT_Y = 30;
    private static final int LIGHT_SIZE = 8;
    private static final int LIGHT_STEP = 10;

    private static final int COUNTDOWN_Y = 44;
    private static final int BUTTON_X = 30;
    private static final int BUTTON_Y = 58;
    private static final int BUTTON_W = 138;
    private static final int INFO_Y = 80;

    private Button extendButton;

    public MicroverseScreen(MicroverseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 184;
    }

    @Override
    protected void init() {
        super.init();
        extendButton = addRenderableWidget(Button.builder(
                Component.translatable("container.mi_nested_infinity.microverse_projector.extend",
                        menu.data(MicroverseMenu.DATA_BALL_COST)),
                b -> {
                    if (getMinecraft().player != null) {
                        getMinecraft().gameMode.handleInventoryButtonClick(getMenu().containerId,
                                MicroverseMenu.EXTEND_BUTTON);
                    }
                })
                .bounds(leftPos + BUTTON_X, topPos + BUTTON_Y, BUTTON_W, 18)
                .build());
    }

    @Override
    public void containerTick() {
        super.containerTick();
        boolean running = menu.data(MicroverseMenu.DATA_RUNNING) == 1;
        extendButton.active = running;
        extendButton.setMessage(Component.translatable(
                "container.mi_nested_infinity.microverse_projector.extend",
                menu.data(MicroverseMenu.DATA_BALL_COST)));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // the twelve coreflame lights: lit = the flame holds its singularity
        int mask = menu.data(MicroverseMenu.DATA_FLAME_MASK);
        for (int i = 0; i < 12; i++) {
            int x = leftPos + LIGHT_X + i * LIGHT_STEP;
            int y = topPos + LIGHT_Y;
            boolean lit = (mask & (1 << i)) != 0;
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

        boolean running = menu.data(MicroverseMenu.DATA_RUNNING) == 1;
        int tier = menu.data(MicroverseMenu.DATA_TIER);
        boolean ok = menu.data(MicroverseMenu.DATA_OK) == 1;

        // status line
        Component status;
        if (running) {
            status = Component.translatable("container.mi_nested_infinity.microverse_projector.running",
                    Component.translatable("item.mi_nested_infinity."
                            + MicroverseItems.MATTERS.get(Math.max(0, tier - 1)).getId().getPath()));
        } else if (ok) {
            status = Component.translatable("container.mi_nested_infinity.microverse_projector.ready");
        } else {
            status = Component.translatable("container.mi_nested_infinity.microverse_projector.problem_"
                    + problemKey(menu));
        }
        graphics.drawString(font, status, leftPos + 8, topPos + 16,
                running ? 0x208040 : (ok ? 0x305080 : 0x803030), false);

        if (running) {
            int remaining = menu.data(MicroverseMenu.DATA_REMAINING);
            // while running, exactly the burned flame's bit is clear in the mask
            drawCentered(graphics, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.countdown",
                    String.format("%.1f", remaining / 20.0)),
                    COUNTDOWN_Y, burnedFlameColor(mask));
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

        // function hints for the machine's own slots while they are empty
        // (occupied slots get the item's tooltip from renderTooltip below)
        if (this.hoveredSlot != null && this.hoveredSlot.index < 3 && !this.hoveredSlot.hasItem()) {
            String key = switch (this.hoveredSlot.index) {
                case MicroverseProjectorBlockEntity.HEART_SLOT ->
                        "container.mi_nested_infinity.microverse_projector.slot_heart";
                case MicroverseProjectorBlockEntity.BALL_SLOT ->
                        "container.mi_nested_infinity.microverse_projector.slot_balls";
                default ->
                        "container.mi_nested_infinity.microverse_projector.slot_output";
            };
            graphics.renderTooltip(font, Component.translatable(key), mouseX, mouseY);
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

    /** Color of the one coreflame burned by the running universe (its bit is clear). */
    private static int burnedFlameColor(int mask) {
        for (int i = 0; i < 12; i++) {
            if ((mask & (1 << i)) == 0) {
                return flameColor(i);
            }
        }
        return 0x404040;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
