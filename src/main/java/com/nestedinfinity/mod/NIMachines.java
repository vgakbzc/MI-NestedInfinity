package com.nestedinfinity.mod;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.machines.guicomponents.EnergyBar;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import aztech.modern_industrialization.machines.guicomponents.RecipeEfficiencyBar;
import aztech.modern_industrialization.machines.init.MachineRegistrationHelper;
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.minecraft.resources.ResourceLocation;

/**
 * Machines registered through MI's public machine API.
 * The magma crucible: one item input, one fluid output.
 */
public final class NIMachines {
    public static final String MAGMA_CRUCIBLE_PATH = "magma_crucible";
    public static final MachineRecipeType MAGMA_CRUCIBLE = createCrucibleType();

    public static final String ION_EXCHANGE_PATH = "ion_exchange";
    public static final MachineRecipeType ION_EXCHANGE = createIonExchangeType();

    /**
     * MIMachineRecipeTypes.create(String) builds a type that rejects every ingredient kind;
     * the two-arg overload that configures capabilities is private, so we construct the
     * types ourselves and register them into MI's recipe serializer/type registers.
     */
    private static MachineRecipeType createCrucibleType() {
        MachineRecipeType type = new MachineRecipeType(
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", MAGMA_CRUCIBLE_PATH))
                .withItemInputs()
                .withFluidOutputs();
        MIRegistries.RECIPE_SERIALIZERS.register(MAGMA_CRUCIBLE_PATH, () -> type);
        MIRegistries.RECIPE_TYPES.register(MAGMA_CRUCIBLE_PATH, () -> type);
        return type;
    }

    private static MachineRecipeType createIonExchangeType() {
        MachineRecipeType type = new MachineRecipeType(
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", ION_EXCHANGE_PATH))
                .withItemInputs()
                .withItemOutputs()
                .withFluidInputs()
                .withFluidOutputs();
        MIRegistries.RECIPE_SERIALIZERS.register(ION_EXCHANGE_PATH, () -> type);
        MIRegistries.RECIPE_TYPES.register(ION_EXCHANGE_PATH, () -> type);
        return type;
    }

    static void init() {
        // (itemInputs, itemOutputs, fluidInputs, fluidOutputs), GUI params default,
        // standard bars, slot layouts, electric-only tier, trailing values as MI's distillery.
        // Also auto-registers the recipe viewer (EMI/REI) category for the machine.
        SingleBlockCraftingMachines.registerMachineTiers(
                "Magma Crucible",
                MAGMA_CRUCIBLE_PATH,
                MAGMA_CRUCIBLE,
                1, 0, 0, 1,
                builder -> {},
                new ProgressBar.Params(77, 33, "arrow"),
                new RecipeEfficiencyBar.Params(38, 62),
                new EnergyBar.Params(18, 30),
                itemInputs -> itemInputs.addSlot(56, 35),
                // supplies positions for item outputs + fluid inputs + fluid outputs (1 fluid out here)
                itemOutputs -> itemOutputs.addSlot(102, 35),
                true, false, false,
                4, 16);

        // Ion exchange machine: consumes ion exchange resin (item) to purify fluid streams.
        // item in: resin; fluid in: crude stream; fluid out: purified + byproduct.
        SingleBlockCraftingMachines.registerMachineTiers(
                "Ion Exchange Machine",
                ION_EXCHANGE_PATH,
                ION_EXCHANGE,
                1, 0, 1, 2,
                builder -> {},
                new ProgressBar.Params(77, 33, "arrow"),
                new RecipeEfficiencyBar.Params(38, 62),
                new EnergyBar.Params(18, 30),
                itemInputs -> itemInputs.addSlot(56, 35),
                // position layout: fluid input tank on the LEFT with the item input,
                // fluid output tanks on the RIGHT (inputs left, outputs right)
                itemOutputs -> itemOutputs.addSlot(30, 27).addSlots(139, 27, 1, 2),
                true, false, false,
                4, 16);
    }

    public static void clientInit() {
        MachineRegistrationHelper.addMachineModel(MAGMA_CRUCIBLE_PATH, MAGMA_CRUCIBLE_PATH, MachineCasings.get("lv"), true, false, false);
        MachineRegistrationHelper.addMachineModel(ION_EXCHANGE_PATH, ION_EXCHANGE_PATH, MachineCasings.get("lv"), true, false, false);
    }

    private NIMachines() {}
}
