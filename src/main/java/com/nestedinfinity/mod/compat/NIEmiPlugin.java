package com.nestedinfinity.mod.compat;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.items.resonance.NINotes;

import java.util.List;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * EMI info pages for the Q8 notes: four colors are assembler-craftable, the
 * other four only ever come out of the resonance attuner, so without these
 * pages the machine-only notes would look unobtainable in the recipe viewer.
 */
@EmiEntrypoint
public class NIEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (NINotes note : NINotes.ALL) {
            List<Component> lines = List.of(
                    Component.translatable("emi.mi_nested_infinity.note." + note.colorName() + ".1"),
                    Component.translatable("emi.mi_nested_infinity.note." + note.colorName() + ".2"));
            registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(note.item.get())), lines,
                    ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, "info/note_" + note.colorName())));
        }
    }
}
