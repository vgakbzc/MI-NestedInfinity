package com.nestedinfinity.mod.mixin;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.nestedinfinity.mod.blocks.NIMachines;
import com.nestedinfinity.mod.logic.AlgaeCultivatorLogic;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Algae cultivator behavior (see AlgaeCultivatorLogic):
 *
 * <ul>
 *   <li>putItemOutputs — dish recipes (every output a petri dish) replace MI's
 *       independent per-entry rolls: the cultivator draws exactly TWO outcomes from
 *       the recipe's probability pool, any other machine (the chemical reactor's
 *       wild isolation) draws at most ONE; other cultivator recipes (the mutagenic
 *       bombardment) keep MI's standard rolls;</li>
 *   <li>takeItemInputs — records the petri dishes a craft consumes in the machine's
 *       rolling 20-craft history and stages the repeat penalty (4^n, n&gt;4 another 10x);</li>
 *   <li>updateActiveRecipe — applies the staged penalty to the craft's total energy
 *       (equivalently its duration) right after the crafter stores it.</li>
 * </ul>
 */
@Mixin(CrafterComponent.class)
public abstract class CrafterComponentMixin {
    @Shadow
    private CrafterComponent.Inventory inventory;

    @Shadow
    private RecipeHolder<MachineRecipe> activeRecipe;

    @Shadow
    private long recipeEnergy;

    @Inject(method = "putItemOutputs", at = @At("HEAD"), cancellable = true)
    private void ni$drawExactlyTwoFromPool(MachineRecipe recipe, boolean simulate, boolean isStarting,
            CallbackInfoReturnable<Boolean> cir) {
        if (AlgaeCultivatorLogic.isDishPool(recipe)) {
            if (recipe.getType() == NIMachines.ALGAE_CULTIVATOR) {
                cir.setReturnValue(
                        AlgaeCultivatorLogic.putTwoDraws(this.inventory.getItemOutputs(), recipe, simulate));
            } else {
                cir.setReturnValue(
                        AlgaeCultivatorLogic.putSingleDraw(this.inventory.getItemOutputs(), recipe, simulate));
            }
        }
    }

    @Inject(method = "takeItemInputs(Laztech/modern_industrialization/machines/recipe/MachineRecipe;Z)Z",
            at = @At("HEAD"))
    private void ni$recordDishesUsed(MachineRecipe recipe, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (!simulate) {
            AlgaeCultivatorLogic.onInputsConsumed((CrafterComponent) (Object) this, recipe, this.inventory);
        }
    }

    @Inject(method = "updateActiveRecipe",
            at = @At(value = "INVOKE",
                    target = "Laztech/modern_industrialization/machines/components/CrafterComponent;getRecipeMaxEu(JJI)J"))
    private void ni$applyDishRepeatPenalty(CallbackInfoReturnable<Boolean> cir) {
        if (this.activeRecipe != null && this.activeRecipe.value().getType() == NIMachines.ALGAE_CULTIVATOR) {
            this.recipeEnergy *= AlgaeCultivatorLogic.takePendingPenalty();
        }
    }
}
