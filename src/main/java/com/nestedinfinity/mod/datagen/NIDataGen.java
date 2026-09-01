package com.nestedinfinity.mod.datagen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import com.nestedinfinity.mod.material.NIMaterials;

/**
 * Datagen entry point: triggered by {@code gradlew runData}, output goes to src/generated/resources.
 * NIRecipeProvider writes our custom chain recipes (MI machine format);
 * the RecipeProvider runner writes MI material recipes via the material API.
 */
public final class NIDataGen {
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new NIRecipeProvider(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new RecipeProvider(generator.getPackOutput(), event.getLookupProvider()) {
            @Override
            protected void buildRecipes(RecipeOutput output) {
                NIMaterials.Materials.values().forEach(m -> m.buildRecipes(output));
            }
        });
    }

    private NIDataGen() {}
}
