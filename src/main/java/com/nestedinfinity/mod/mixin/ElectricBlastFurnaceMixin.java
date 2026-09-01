package com.nestedinfinity.mod.mixin;


import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import com.nestedinfinity.mod.NestedInfinity;
import net.minecraft.resources.ResourceLocation;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.Console;
import java.util.List;
import com.nestedinfinity.mod.blocks.NICoils;
import com.nestedinfinity.mod.blocks.NIMachines;

@Mixin(ElectricBlastFurnaceBlockEntity.class)
public class ElectricBlastFurnaceMixin {
    private static final Logger log = LoggerFactory.getLogger(ElectricBlastFurnaceMixin.class);

    @Inject(at = @At("HEAD"), method = "<clinit>")
    private static void mixinChecker(CallbackInfo ci) {
        Log.info("[MINI]: woo we are fucking mixin'");
    }

    @Inject(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            // Targets the .add(Object) call on the List
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            // ordinal = 1 means the SECOND time .add() is called in this method (Kanthal)
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void addExtendedTiers(CallbackInfo ci, List<ElectricBlastFurnaceBlockEntity.Tier> registrationTiers) {

        for (int i = 0; i < NICoils.ALL.size(); i++) {
            registrationTiers.add(
                 new ElectricBlastFurnaceBlockEntity.Tier(
                     (NICoils.ALL.get(i).getId()) ,
                     NICoils.TIERS.get(i).eu(),
                     NICoils.TIERS.get(i).coilPath()
                     )
            );
        }
        Log.info("[MINI]: registered coil count: " + registrationTiers.size());
    }
}
