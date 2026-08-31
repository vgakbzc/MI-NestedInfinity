package com.nestedinfinity.mod.mixin;

import aztech.modern_industrialization.machines.components.UpgradeComponent;
import com.nestedinfinity.mod.NIUpgrades;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UpgradeComponent.class)
public abstract class UpgradeComponentMixin {
    @Inject(method = "getExtraEu", at = @At("HEAD"), cancellable = true)
    private static void ni$hardcodedOverclock(ItemLike item, CallbackInfoReturnable<Long> cir) {
        long value = NIUpgrades.extraEu(item.asItem());
        if (value > 0) {
            cir.setReturnValue(value);
        }
    }
}
