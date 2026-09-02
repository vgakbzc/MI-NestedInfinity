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
 * The projector screen: the three machine slots on the left rail, a strip
 * of the twelve coreflame lights across the middle, live countdown /
 * accrual / return-chance readouts, and the "extend" button with its
 * current giant-matter-ball price.
 */
public class MicroverseScreen extends AbstractContainerScreen<MicroverseMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NestedInfinity.MODID, "textures/gui/microverse_projector.png");

    private static final int LIGHT_X = 30;
    private static final int LIGHT_Y = 36;
    private static final int LIGHT_SIZE = 8;
    private static final int LIGHT_STEP = 10;

    private Button extendButton;

    public MicroverseScreen(MicroverseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 184;
        this.inventoryLabelY = 108;
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
                .bounds(leftPos + 62, topPos + 58, 104, 18)
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
            graphics.drawString(font, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.countdown",
                    String.format("%.1f", remaining / 20.0)),
                    leftPos + 30, topPos + 48, 0x404040, false);
            graphics.drawString(font, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.accrued",
                    menu.data(MicroverseMenu.DATA_ACCRUED)),
                    leftPos + 30, topPos + 80, 0x404040, false);
        } else if (ok) {
            int chance = menu.data(MicroverseMenu.DATA_RETURN_CHANCE);
            graphics.drawString(font, Component.translatable(
                    "container.mi_nested_infinity.microverse_projector.return_chance", chance),
                    leftPos + 62, topPos + 42, 0x604040, false);
        }
    }

    private static String problemKey(MicroverseMenu menu) {
        String problem = menu.getBlockEntity().structureProblem();
        return problem == null || problem.isEmpty() || "unchecked".equals(problem) ? "unchecked" : problem;
    }

    /** One hue per coreflame kind, in ring order. */
    private static int flameColor(int index) {
        float hue = index / 12.0F;
        return java.awt.Color.HSBtoRGB(hue, 0.75F, 0.95F) & 0xFFFFFF;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
