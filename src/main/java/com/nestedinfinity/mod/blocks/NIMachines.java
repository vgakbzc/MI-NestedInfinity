package com.nestedinfinity.mod.blocks;

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

    public static final String ALGAE_CULTIVATOR_PATH = "algae_cultivator";
    public static final MachineRecipeType ALGAE_CULTIVATOR = createAlgaeCultivatorType();

    public static final String SUPER_MIXER_PATH = "super_mixer";
    public static final MachineRecipeType SUPER_MIXER = createSuperMixerType();

    public static final String SUPER_ASSEMBLER_PATH = "super_assembler";
    public static final MachineRecipeType SUPER_ASSEMBLER = createSuperAssemblerType();

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

    private static MachineRecipeType createAlgaeCultivatorType() {
        MachineRecipeType type = new MachineRecipeType(
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", ALGAE_CULTIVATOR_PATH))
                .withItemInputs()
                .withItemOutputs()
                .withFluidInputs();
        MIRegistries.RECIPE_SERIALIZERS.register(ALGAE_CULTIVATOR_PATH, () -> type);
        MIRegistries.RECIPE_TYPES.register(ALGAE_CULTIVATOR_PATH, () -> type);
        return type;
    }

    private static MachineRecipeType createSuperMixerType() {
        MachineRecipeType type = new MachineRecipeType(
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", SUPER_MIXER_PATH))
                .withItemInputs()
                .withItemOutputs()
                .withFluidInputs()
                .withFluidOutputs();
        MIRegistries.RECIPE_SERIALIZERS.register(SUPER_MIXER_PATH, () -> type);
        MIRegistries.RECIPE_TYPES.register(SUPER_MIXER_PATH, () -> type);
        return type;
    }

    private static MachineRecipeType createSuperAssemblerType() {
        MachineRecipeType type = new MachineRecipeType(
                ResourceLocation.fromNamespaceAndPath("modern_industrialization", SUPER_ASSEMBLER_PATH))
                .withItemInputs()
                .withItemOutputs();
        MIRegistries.RECIPE_SERIALIZERS.register(SUPER_ASSEMBLER_PATH, () -> type);
        MIRegistries.RECIPE_TYPES.register(SUPER_ASSEMBLER_PATH, () -> type);
        return type;
    }

    public static void init() {
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

        // Algae cultivator: two petri dishes + 50mB nutrient agar -> two dishes drawn from
        // the recipe's probability pool (see CrafterComponentMixin). 16 output slots so
        // every outcome of the largest pool can sit in the machine at once.
        // First slot consumer: ALL item slots in order — the two dish inputs, then the
        // sixteen product outputs. Second consumer: fluid tank position (nutrient agar).
        SingleBlockCraftingMachines.registerMachineTiers(
                "Algae Cultivator",
                ALGAE_CULTIVATOR_PATH,
                ALGAE_CULTIVATOR,
                2, 16, 1, 0,
                builder -> {
                    builder.backgroundHeight(184);
                    builder.playerInventoryY = 102;
                },
                new ProgressBar.Params(77, 33, "arrow"),
                new RecipeEfficiencyBar.Params(38, 62),
                new EnergyBar.Params(18, 30),
                itemSlots -> itemSlots.addSlots(48, 35, 1, 2).addSlots(102, 17, 4, 4),
                fluidTanks -> fluidTanks.addSlot(24, 71),
                true, false, false,
                4, 16);

        // Super mixer: sixteen item input slots for bulk blending (the 16 amino
        // acids -> protein, with the peptide-condensation water as byproduct).
        // 4x4 input grid on the left, item output plus fluid byproduct tank on the right.
        SingleBlockCraftingMachines.registerMachineTiers(
                "Super Mixer",
                SUPER_MIXER_PATH,
                SUPER_MIXER,
                16, 1, 0, 1,
                builder -> {
                    builder.backgroundHeight(184);
                    builder.playerInventoryY = 102;
                },
                new ProgressBar.Params(104, 44, "arrow"),
                new RecipeEfficiencyBar.Params(38, 62),
                new EnergyBar.Params(18, 30),
                itemSlots -> itemSlots.addSlots(32, 17, 4, 4).addSlot(128, 44),
                fluidTanks -> fluidTanks.addSlot(128, 71),
                true, false, false,
                4, 16);

        // Super assembler: the optical finale — all hundred glow tubes in ONE
        // 10x10 grid recipe crafting the optical qubit component. MI's machine
        // background sheet hard-caps the panel at 176x260, so the grid uses a
        // 15px slot pitch instead of the standard 18px (a 1px sprite overlap
        // between neighbours), the progress arrow and output slot live in the
        // otherwise empty title strip, and the result fits without any slot
        // falling off the panel.
        SingleBlockCraftingMachines.registerMachineTiers(
                "Super Assembler",
                SUPER_ASSEMBLER_PATH,
                SUPER_ASSEMBLER,
                100, 1, 0, 0,
                builder -> {
                    builder.backgroundHeight(260);
                    builder.playerInventoryY = 182;
                },
                new ProgressBar.Params(100, 0, "arrow"),
                new RecipeEfficiencyBar.Params(44, 171),
                new EnergyBar.Params(4, 30),
                itemSlots -> {
                    for (int row = 0; row < 10; row++) {
                        for (int col = 0; col < 10; col++) {
                            itemSlots.addSlot(22 + col * 15, 18 + row * 15);
                        }
                    }
                    itemSlots.addSlot(132, 0);
                },
                fluidTanks -> {},
                true, false, false,
                4, 16);
    }

    public static void clientInit() {
        MachineRegistrationHelper.addMachineModel(MAGMA_CRUCIBLE_PATH, MAGMA_CRUCIBLE_PATH, MachineCasings.get("lv"), true, false, false);
        MachineRegistrationHelper.addMachineModel(ION_EXCHANGE_PATH, ION_EXCHANGE_PATH, MachineCasings.get("lv"), true, false, false);
        MachineRegistrationHelper.addMachineModel(ALGAE_CULTIVATOR_PATH, ALGAE_CULTIVATOR_PATH, MachineCasings.get("lv"), true, false, false);
        MachineRegistrationHelper.addMachineModel(SUPER_MIXER_PATH, SUPER_MIXER_PATH, MachineCasings.get("lv"), true, false, false);
        MachineRegistrationHelper.addMachineModel(SUPER_ASSEMBLER_PATH, SUPER_ASSEMBLER_PATH, MachineCasings.get("lv"), true, false, false);
    }

    private NIMachines() {}
}
