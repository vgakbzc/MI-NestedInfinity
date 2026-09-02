package com.nestedinfinity.mod.compat;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.items.resonance.NINotes;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * One "Resonance Attuner" page per note: the note's full column of the Q8
 * Cayley table, one row per register color — [register square] [note] →
 * [two new-register squares: the note's color and the 50% drift color] [the
 * product note]. Colored squares stand for the tuning block's register state
 * (tooltips spell them out); notes render as the real items, so the paths to
 * the machine-only colors read as pictures instead of a text wall.
 */
public class NIAttuningRecipe implements EmiRecipe {
    /** Cell size, kept identical to EMI item slots so rows line up. */
    private static final int CELL = 18;
    private static final int TOP = 4;
    private static final int ROW_HEIGHT = 20;
    private static final int REG_X = 0;
    private static final int NOTE_X = 22;
    private static final int ARROW_X = 46;
    private static final int BECOMES_X = 76;
    private static final int DRIFTS_X = 96;
    private static final int OUT_X = 120;
    private static final int WIDTH = OUT_X + CELL + 6;

    private final NINotes note;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    NIAttuningRecipe(NINotes note) {
        this.note = note;
        this.inputs = List.of(EmiStack.of(note.item.get()));
        // Left multiplication by a fixed note permutes Q8, so the products
        // over all eight registers are exactly the eight distinct notes.
        List<EmiStack> outs = new ArrayList<>();
        for (NINotes register : NINotes.values()) {
            outs.add(EmiStack.of(register.times(note).item.get()));
        }
        this.outputs = outs;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NIEmiPlugin.ATTUNING;
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "attuning/note_" + note.colorName());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return TOP + NINotes.GROUP_SIZE * ROW_HEIGHT + 4;
    }

    @Override
    public boolean supportsRecipeTree() {
        // The register squares are block states, not ingredients; this page
        // documents the machine, it is not a craftable path.
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int row = 0; row < NINotes.GROUP_SIZE; row++) {
            NINotes register = NINotes.values()[row];
            int y = TOP + row * ROW_HEIGHT;
            addColorSquare(widgets, REG_X, y, register,
                    Component.translatable("container.mi_nested_infinity.resonance_attuner.register", colorName(register)));
            widgets.addSlot(EmiStack.of(note.item.get()), NOTE_X, y);
            widgets.addTexture(EmiTexture.FULL_ARROW, ARROW_X, y + 1);
            addColorSquare(widgets, BECOMES_X, y, note,
                    Component.translatable("emi.mi_nested_infinity.attuning.becomes", colorName(note)));
            addColorSquare(widgets, DRIFTS_X, y, note.next(),
                    Component.translatable("emi.mi_nested_infinity.attuning.drifts", colorName(note.next())));
            widgets.addSlot(EmiStack.of(register.times(note).item.get()), OUT_X, y);
        }
    }

    private static void addColorSquare(WidgetHolder widgets, int x, int y, NINotes color, Component tooltip) {
        // The pose is pre-translated to the widget origin, so fills are relative.
        widgets.addDrawable(x, y, CELL, CELL, (graphics, mouseX, mouseY, delta) -> {
            graphics.fill(0, 0, CELL, CELL, 0xFF33333B);
            graphics.fill(2, 2, CELL - 2, CELL - 2, 0xFF000000 | color.tint);
        });
        widgets.addTooltipText(List.of(tooltip), x, y, CELL, CELL);
    }

    private static Component colorName(NINotes note) {
        return Component.translatable("color.mi_nested_infinity." + note.colorName());
    }
}
