package com.nestedinfinity.mod.compat;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.items.NIItems;
import com.nestedinfinity.mod.items.gems.NIGems;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

/**
 * The super assembler's grand recipe as an EMI page: the 10x10 grid of all
 * hundred glow tubes flowing into the optical qubit component — a picture of
 * exactly what the machine's in-world grid expects (one of every color).
 */
public class NIOpticalQubitRecipe implements EmiRecipe {
    private static final int GRID = 10;
    private static final int CELL = 18;
    private static final int ARROW_X = GRID * CELL + 8;
    private static final int OUT_X = ARROW_X + EmiTexture.FULL_ARROW.width + 6;

    private final List<EmiStack> inputs;

    NIOpticalQubitRecipe() {
        List<EmiStack> tubes = new ArrayList<>();
        for (NIGems.Gem gem : NIGems.ALL) {
            tubes.add(EmiStack.of(gem.tube().get()));
        }
        this.inputs = tubes;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NIEmiPlugin.SUPER_ASSEMBLING;
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID,
                "super_assembling/optical_qubit_component");
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.copyOf(inputs);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(NIItems.OPTICAL_QUBIT_COMPONENT.get()));
    }

    @Override
    public int getDisplayWidth() {
        return OUT_X + CELL;
    }

    @Override
    public int getDisplayHeight() {
        return GRID * CELL;
    }

    @Override
    public boolean supportsRecipeTree() {
        // a hundred inputs at once; the tree resolver would be meaningless
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int row = 0; row < GRID; row++) {
            for (int col = 0; col < GRID; col++) {
                widgets.addSlot(inputs.get(row * GRID + col), col * CELL, row * CELL);
            }
        }
        widgets.addTexture(EmiTexture.FULL_ARROW, ARROW_X, GRID * CELL / 2 - EmiTexture.FULL_ARROW.height / 2);
        widgets.addSlot(getOutputs().get(0), OUT_X, GRID * CELL / 2 - CELL / 2);
    }
}
