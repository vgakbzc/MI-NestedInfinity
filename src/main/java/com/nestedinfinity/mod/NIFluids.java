package com.nestedinfinity.mod;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class NIFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, NestedInfinity.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, NestedInfinity.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, NestedInfinity.MODID);

    public static final Entry XENON = register("xenon", 0xFF7FB8C4);
    public static final Entry FLUORINE = register("fluorine", 0xFFD6C546);
    public static final Entry HYDROFLUORIC_ACID = register("hydrofluoric_acid", 0xFFB8D8A8);
    public static final Entry XENON_HEXAFUORIDE = register("xenon_hexafluoride", 0xFFC9A0E8);
    public static final Entry INERT_NAQUADAH_SOLUTION = register("inert_naquadah_solution", 0xFF8FA8A0);
    public static final Entry NEUTRON_ACTIVATED_NAQUADAH_SOLUTION = register("neutron_activated_naquadah_solution", 0xFF6FE86F);
    public static final Entry NITRIC_ACID = register("nitric_acid", 0xFFE8D890);
    public static final Entry MONAZITE_HEAVY_RESIDUE_SOLUTION = register("monazite_heavy_residue_solution", 0xFFB09868);
    public static final Entry ULTRAHEAVY_ELEMENT_MIXTURE = register("ultraheavy_element_mixture", 0xFF78E8B0);
    public static final Entry LIQUID_GLASS = register("liquid_glass", 0xFFC8E4EC);

    // Epoxy resin program, propylene route (cumene + epichlorohydrin)
    public static final Entry BRINE = register("brine", 0xFFB8C8D0);
    public static final Entry CUMENE = register("cumene", 0xFFE8E0C8);
    public static final Entry CUMENE_HYDROPEROXIDE = register("cumene_hydroperoxide", 0xFFE8D890);
    public static final Entry PHENOL = register("phenol", 0xFFE8C8C0);
    public static final Entry ACETONE = register("acetone", 0xFFE8F0F0);
    public static final Entry PHENOL_TAR = register("phenol_tar", 0xFF4A3828);
    public static final Entry CRUDE_ALLYL_CHLORIDE = register("crude_allyl_chloride", 0xFFD8D8A0);
    public static final Entry ALLYL_CHLORIDE = register("allyl_chloride", 0xFFDCE8D0);
    public static final Entry CHLORINATED_WASTE = register("chlorinated_waste", 0xFF7A8250);
    public static final Entry HYPOCHLOROUS_ACID = register("hypochlorous_acid", 0xFFD8E8A0);
    public static final Entry DICHLOROHYDRIN = register("dichlorohydrin", 0xFFE0E8D8);
    public static final Entry CRUDE_EPICHLOROHYDRIN = register("crude_epichlorohydrin", 0xFFD8E8C8);
    public static final Entry EPICHLOROHYDRIN = register("epichlorohydrin", 0xFFE4F0DC);
    public static final Entry CRUDE_BISPHENOL_A = register("crude_bisphenol_a", 0xFFD8C098);
    public static final Entry BISPHENOL_A = register("bisphenol_a", 0xFFE8D8B0);
    public static final Entry CHLOROHYDRIN_ETHER = register("chlorohydrin_ether", 0xFFE0ECD4);
    public static final Entry CRUDE_EPOXY_RESIN = register("crude_epoxy_resin", 0xFFC89840);
    public static final Entry EPOXY_RESIN = register("epoxy_resin", 0xFFD8A838);

    // Fluoroantimonic acid (HF + SbF5), catalyst for crystal circuit board assembly
    public static final Entry ANTIMONY_PENTAFLUORIDE = register("antimony_pentafluoride", 0xFFD0E0E8);
    public static final Entry FLUOROANTIMONIC_ACID = register("fluoroantimonic_acid", 0xFFB8D8E8);

    public static final List<Entry> ALL = List.of(XENON, FLUORINE, HYDROFLUORIC_ACID, XENON_HEXAFUORIDE, INERT_NAQUADAH_SOLUTION, NEUTRON_ACTIVATED_NAQUADAH_SOLUTION, NITRIC_ACID, MONAZITE_HEAVY_RESIDUE_SOLUTION, ULTRAHEAVY_ELEMENT_MIXTURE, LIQUID_GLASS, BRINE, CUMENE, CUMENE_HYDROPEROXIDE, PHENOL, ACETONE, PHENOL_TAR, CRUDE_ALLYL_CHLORIDE, ALLYL_CHLORIDE, CHLORINATED_WASTE, HYPOCHLOROUS_ACID, DICHLOROHYDRIN, CRUDE_EPICHLOROHYDRIN, EPICHLOROHYDRIN, CRUDE_BISPHENOL_A, BISPHENOL_A, CHLOROHYDRIN_ETHER, CRUDE_EPOXY_RESIN, EPOXY_RESIN, ANTIMONY_PENTAFLUORIDE, FLUOROANTIMONIC_ACID);

    private static Entry register(String name, int tint) {
        return new Entry(name, tint);
    }

    public static final class Entry {
        public final DeferredHolder<FluidType, FluidType> type;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
        public final DeferredHolder<Block, LiquidBlock> block;
        public final DeferredItem<BucketItem> bucket;
        public final int tint;

        private Entry(String name, int tint) {
            this.tint = tint;
            this.type = FLUID_TYPES.register(name,
                    () -> new FluidType(FluidType.Properties.create().density(1000).viscosity(1000).descriptionId("fluid." + NestedInfinity.MODID + "." + name)));
            this.source = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(properties()));
            this.flowing = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(properties()));
            this.block = BLOCKS.register(name, () -> new LiquidBlock(this.source.get(),
                    BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable()));
            this.bucket = NestedInfinity.ITEMS.register(name + "_bucket",
                    () -> new BucketItem(this.source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        }

        private BaseFlowingFluid.Properties properties() {
            return new BaseFlowingFluid.Properties(this.type, this.source, this.flowing)
                    .bucket(this.bucket)
                    .block(this.block)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2)
                    .tickRate(5);
        }
    }

    static void init() {}

    private NIFluids() {}
}
