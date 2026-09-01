package com.nestedinfinity.mod.compat;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.resonance.NINotes;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

/**
 * The "Resonance Attuner" EMI category, one visual page per note (see
 * {@link NIAttuningRecipe}). The attuner block is registered as the
 * category's workstation, so the pages surface from the machine itself as
 * well as from every note — and the machine-only colors (green, cyan,
 * purple, black) visibly have no other recipe source.
 */
@EmiEntrypoint
public class NIEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory ATTUNING = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "resonance_attuner"),
            EmiStack.of(NIBlocks.RESONANCE_ATTUNER.get()));

    public static final EmiRecipeCategory SUPER_ASSEMBLING = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "super_assembler"),
            EmiStack.of(NIBlocks.SUPER_ASSEMBLER.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ATTUNING);
        registry.addWorkstation(ATTUNING, EmiStack.of(NIBlocks.RESONANCE_ATTUNER.get()));
        for (NINotes note : NINotes.ALL) {
            registry.addRecipe(new NIAttuningRecipe(note));
        }
        // the super assembler's hundred-tube finale (see NIOpticalQubitRecipe)
        registry.addCategory(SUPER_ASSEMBLING);
        registry.addWorkstation(SUPER_ASSEMBLING, EmiStack.of(NIBlocks.SUPER_ASSEMBLER.get()));
        registry.addRecipe(new NIOpticalQubitRecipe());
    }
}
