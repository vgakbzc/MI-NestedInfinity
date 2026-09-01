package com.nestedinfinity.mod.mixin;

import aztech.modern_industrialization.machines.blockentities.ReplicatorMachineBlockEntity;
import com.nestedinfinity.mod.NestedInfinity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables the MI Replicator: every replication step is cancelled at its head
 * and reports failure, so the machine idles forever without consuming UU
 * matter or emitting items. The block entity itself stays alive (GUI, redstone
 * control and auto-extraction keep working), it just never makes progress.
 */
@Mixin(ReplicatorMachineBlockEntity.class)
public class ReplicatorMachineMixin {

    @Unique
    private static boolean ni$replicatorDisabledLogged = false;

    @Inject(method = "replicationStep(Z)Z", at = @At("HEAD"), cancellable = true)
    private void ni$neverReplicate(CallbackInfoReturnable<Boolean> cir) {
        if (!ni$replicatorDisabledLogged) {
            ni$replicatorDisabledLogged = true;
            NestedInfinity.LOGGER.info("MI Replicator disabled by Nested Infinity");
        }
        cir.setReturnValue(false);
    }
}
