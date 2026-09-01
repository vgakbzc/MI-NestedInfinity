package com.nestedinfinity.mod.fluids;
import com.nestedinfinity.mod.NestedInfinity;

import java.util.List;
import java.util.stream.Stream;
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

    // Bacteria program: 12 algae cultures, one per 30-degree hue step on the color wheel
    // (red, orange, yellow, lime, green, teal, cyan, azure, blue, purple, magenta, pink).
    // Names are Greek/Latin color roots + "-phyta", the standard algae-division suffix;
    // see com.nestedinfinity.mod.items.algae.NIAlgae for the wheel metadata and tools/gen_algae_assets.py for asset generation.
    public static final Entry ERYTHROPHYTA = register("erythrophyta", 0xFFF53D3D); // red, Gk. erythros
    public static final Entry AURANTIOPHYTA = register("aurantiophyta", 0xFFF5993D); // orange, Lat. aurantium
    public static final Entry XANTHOPHYTA = register("xanthophyta", 0xFFF5F53D); // yellow, Gk. xanthos
    public static final Entry PRASINOPHYTA = register("prasinophyta", 0xFF99F53D); // lime, Gk. prasinos "leek-green"
    public static final Entry CHLOROPHYTA = register("chlorophyta", 0xFF3DF53D); // green, Gk. chloros
    public static final Entry GLAUCOPHYTA = register("glaucophyta", 0xFF3DF599); // teal, Gk. glaukos "blue-green"
    public static final Entry CYANOPHYTA = register("cyanophyta", 0xFF3DF5F5); // cyan, Gk. kyanos
    public static final Entry AZUREOPHYTA = register("azurophyta", 0xFF3D99F5); // azure, Lat. azureus "sky blue"
    public static final Entry CAERULEOPHYTA = register("caeruleophyta", 0xFF3D3DF5); // blue, Lat. caeruleus "deep blue"
    public static final Entry PURPUREOPHYTA = register("purpureophyta", 0xFF993DF5); // purple, Lat. purpureus
    public static final Entry MAGENTOPHYTA = register("magentophyta", 0xFFF53DF5); // magenta, modern (1859)
    public static final Entry RHODOPHYTA = register("rhodophyta", 0xFFF53D99); // pink, Gk. rhodon "rose"

    // Biochemistry program: platform chemicals for the amino-acid and agar routes,
    // plus the agar extraction stream and the finished cultivator medium.
    public static final Entry AMMONIA = register("ammonia", 0xFFE8E0B0); // Haber-Bosch
    public static final Entry CARBON_MONOXIDE = register("carbon_monoxide", 0xFFD8D8D8); // steam reforming
    public static final Entry METHANOL = register("methanol", 0xFFD0E8E0); // CO hydrogenation
    public static final Entry ACETIC_ACID = register("acetic_acid", 0xFFE8F0D0); // Monsanto carbonylation
    public static final Entry FORMALDEHYDE = register("formaldehyde", 0xFFE0F0E8); // silver-catalyzed oxidation
    public static final Entry HYDROGEN_CYANIDE = register("hydrogen_cyanide", 0xFFC8E0D8); // Andrussow
    public static final Entry ACETALDEHYDE = register("acetaldehyde", 0xFFF0E8C8); // Wacker oxidation
    public static final Entry ISOBUTYRALDEHYDE = register("isobutyraldehyde", 0xFFF0E0C0); // propene hydroformylation
    public static final Entry ISOVALERALDEHYDE = register("isovaleraldehyde", 0xFFF0D8B0); // homologation
    public static final Entry METHYL_BUTANAL = register("methyl_butanal", 0xFFF0DCC0); // 2-methylbutanal
    public static final Entry PHENYLACETALDEHYDE = register("phenylacetaldehyde", 0xFFE0D8F0); // styrene oxidation
    public static final Entry CRUDE_AGAR_SOLUTION = register("crude_agar_solution", 0xFFB89060); // hot extraction
    public static final Entry CLARIFIED_AGAR_SOLUTION = register("clarified_agar_solution", 0xFFE8D8A8); // filtered
    public static final Entry NUTRIENT_AGAR = register("nutrient_agar", 0xFFC8A868); // the cultivator medium
    public static final Entry SULFUR_DIOXIDE = register("sulfur_dioxide", 0xFFDCE0C0); // cinnabar/barite roasting offgas
    public static final Entry ADVANCED_RUBBER = register("advanced_rubber", 0xFF4A3830); // vulcanized SBR
    public static final Entry CHLOROMETHANE = register("chloromethane", 0xFFD0E8DC); // methanol hydrochlorination
    public static final Entry DIMETHYLDICHLOROSILANE = register("dimethyldichlorosilane", 0xFFE0D4C4); // Mueller-Rochow product
    public static final Entry LIQUID_SILICONE_RUBBER = register("liquid_silicone_rubber", 0xFFF0E0E2); // hydrolyzed PDMS
    public static final Entry SUPERCHARGED_NAQUADAH_SOLUTION = register("supercharged_naquadah_solution", 0xFF8CE8D8); // dissolved supercharged naquadah
    public static final Entry M_XYLENE = register("m_xylene", 0xFFE8E0C8); // meta-xylene, toluene-disproportionation cut
    public static final Entry NITROBENZENE = register("nitrobenzene", 0xFFE8E0A8); // pale-yellow nitration product
    public static final Entry POLYBENZIMIDAZOLE = register("polybenzimidazole", 0xFFC8943C); // molten PBI, amber like Celazole
    public static final Entry MUTAGEN = register("mutagen", 0xFFB254C4); // agar laced with charged naquadah
    public static final Entry METHYL_CYANOACETATE = register("methyl_cyanoacetate", 0xFFE8ECE8); // esterified cyanoacetic acid
    public static final Entry CYANOACRYLATE_GLUE = register("cyanoacrylate_glue", 0xFFE4DCC0); // cracked monomer, the strong glue

    // Resonant circuit program: superheavy separation cascade, fusion-born
    // alloys, polyimide / fluoroelastomer / superconductor chemistry and the
    // tuning feedstocks. Mirror of tools/gen_algae_assets.py (RESONANT_FLUIDS).
    public static final Entry RADON = register("radon", 0xFFC8B8E8); // noble gas, xenon fusion product
    public static final Entry SUPERHEAVY_FISSION_SOLUTION = register("superheavy_fission_solution", 0xFF8C58C8); // radon-cracked supercharged naquadah
    public static final Entry VALENCE_ADJUSTED_FEED = register("valence_adjusted_feed", 0xFFB068D8); // Pu(IV)/Np(VI) nitrate feed
    public static final Entry TBP_ORGANIC_PHASE = register("tbp_organic_phase", 0xFFE8D8A8); // loaded PUREX extractant
    public static final Entry HLR_RAFFINATE = register("hlr_raffinate", 0xFFB85838); // high-level PUREX raffinate
    public static final Entry URANIUM_LIQUOR = register("uranium_liquor", 0xFFC8E858); // purified uranyl nitrate
    public static final Entry PLUTONIUM_LIQUOR = register("plutonium_liquor", 0xFF8898B8); // stripped Pu(III)
    public static final Entry NEPTUNIUM_LIQUOR = register("neptunium_liquor", 0xFF68C8A8); // valence-split Np
    public static final Entry URANIUM_NEPTUNIUM_LIQUOR = register("uranium_neptunium_liquor", 0xFFB8D888); // U/Np co-stripped feed
    public static final Entry TRUEX_ORGANIC = register("truex_organic", 0xFFE8C888); // CMPO minor-actinide phase
    public static final Entry MINOR_ACTINIDE_LIQUOR = register("minor_actinide_liquor", 0xFFC87858); // stripped minor actinides
    public static final Entry EARLY_ACTINIDE_GROUP = register("early_actinide_group", 0xFFD88858); // Am/Cm/Bk/Cf/Es elution group
    public static final Entry LATE_ACTINIDE_GROUP = register("late_actinide_group", 0xFFD8A858); // Fm/Md/No/Lr elution group
    public static final Entry SUPERHEAVY_VAPOR = register("superheavy_vapor", 0xFFD8C8F0); // volatile oxychloride/oxide carrier gas
    public static final Entry CN_CONDENSATE = register("cn_condensate", 0xFFA8C8E8); // condensed copernicium
    public static final Entry RG_LIQUOR = register("rg_liquor", 0xFFE8C858); // roentgenium thioether complex
    public static final Entry TELLURIC_ACID = register("telluric_acid", 0xFFE8E0B8); // Te(VI) oxidizer, O->Te congener
    public static final Entry AQUA_REGIA = register("aqua_regia", 0xFFE8C840); // HNO3 + HCl, royal water
    public static final Entry MOLTEN_GOLD = register("molten_gold", 0xFFE8B830);
    public static final Entry MOLTEN_SILVER = register("molten_silver", 0xFFD8D8E0);
    public static final Entry MOLTEN_ROENTGENIUM = register("molten_roentgenium", 0xFFD8A830);
    public static final Entry MOLTEN_COPERNICIUM = register("molten_copernicium", 0xFF98B8D8);
    public static final Entry MOLTEN_ADAMANTIUM = register("molten_adamantium", 0xFFB83838); // gold-group fusion alloy
    public static final Entry MOLTEN_MITHRIL = register("molten_mithril", 0xFFB8D8E8); // silver-amalgam fusion alloy
    public static final Entry MOLTEN_TRINIUM = register("molten_trinium", 0xFF9BC8D8); // adamantium + mithril fusion
    public static final Entry RESONANT_MOTHER_LIQUOR = register("resonant_mother_liquor", 0xFF54C4C4); // mutagen + ender eye
    public static final Entry POLYAMIC_ACID = register("polyamic_acid", 0xFFD8C060); // PMDA/ODA polycondensate
    public static final Entry CONDUCTIVE_EPOXY = register("conductive_epoxy", 0xFFB8B8C0); // silver-filled epoxy
    public static final Entry VINYLIDENE_FLUORIDE = register("vinylidene_fluoride", 0xFFD0E8E0); // R-22 pyrolysis product
    public static final Entry HEXAFLUOROPROPYLENE = register("hexafluoropropylene", 0xFFC8E0D0); // propylene fluorination
    public static final Entry CHLOROFORM = register("chloroform", 0xFFD8E8E8); // methane chlorination
    public static final Entry REFRIGERANT_22 = register("refrigerant_22", 0xFFC8E8E8); // CHClF2, VF2 precursor

    // Remaining noble gases of the glow-tube program (helium is MI's own
    // modern_industrialization:helium). Each gem's tube is filled with the gas
    // whose discharge color lies nearest the gem's color: neon red-orange,
    // argon lavender, krypton ice-blue (xenon and radon already registered).
    public static final Entry NEON = register("neon", 0xFFFF5F42);
    public static final Entry ARGON = register("argon", 0xFFAA8CFF);
    public static final Entry KRYPTON = register("krypton", 0xFF96C8FF);

    public static final List<Entry> ALGAE = List.of(ERYTHROPHYTA, AURANTIOPHYTA, XANTHOPHYTA, PRASINOPHYTA,
            CHLOROPHYTA, GLAUCOPHYTA, CYANOPHYTA, AZUREOPHYTA, CAERULEOPHYTA, PURPUREOPHYTA, MAGENTOPHYTA, RHODOPHYTA);

    public static final List<Entry> BIO = List.of(AMMONIA, CARBON_MONOXIDE, METHANOL, ACETIC_ACID, FORMALDEHYDE,
            HYDROGEN_CYANIDE, ACETALDEHYDE, ISOBUTYRALDEHYDE, ISOVALERALDEHYDE, METHYL_BUTANAL, PHENYLACETALDEHYDE,
            CRUDE_AGAR_SOLUTION, CLARIFIED_AGAR_SOLUTION, NUTRIENT_AGAR, SULFUR_DIOXIDE, ADVANCED_RUBBER,
            CHLOROMETHANE, DIMETHYLDICHLOROSILANE, LIQUID_SILICONE_RUBBER, SUPERCHARGED_NAQUADAH_SOLUTION,
            M_XYLENE, NITROBENZENE, POLYBENZIMIDAZOLE, MUTAGEN, METHYL_CYANOACETATE, CYANOACRYLATE_GLUE);

    public static final List<Entry> RESONANT = List.of(RADON, SUPERHEAVY_FISSION_SOLUTION, VALENCE_ADJUSTED_FEED,
            TBP_ORGANIC_PHASE, HLR_RAFFINATE, URANIUM_LIQUOR, PLUTONIUM_LIQUOR, NEPTUNIUM_LIQUOR,
            URANIUM_NEPTUNIUM_LIQUOR, TRUEX_ORGANIC,
            MINOR_ACTINIDE_LIQUOR, EARLY_ACTINIDE_GROUP, LATE_ACTINIDE_GROUP, SUPERHEAVY_VAPOR, CN_CONDENSATE,
            RG_LIQUOR, TELLURIC_ACID, AQUA_REGIA, MOLTEN_GOLD, MOLTEN_SILVER, MOLTEN_ROENTGENIUM, MOLTEN_COPERNICIUM,
            MOLTEN_ADAMANTIUM, MOLTEN_MITHRIL, MOLTEN_TRINIUM, RESONANT_MOTHER_LIQUOR, POLYAMIC_ACID,
            CONDUCTIVE_EPOXY, VINYLIDENE_FLUORIDE, HEXAFLUOROPROPYLENE, CHLOROFORM, REFRIGERANT_22);

    public static final List<Entry> ALL = Stream.concat(List.of(XENON, FLUORINE, HYDROFLUORIC_ACID, XENON_HEXAFUORIDE,
            INERT_NAQUADAH_SOLUTION, NEUTRON_ACTIVATED_NAQUADAH_SOLUTION, NITRIC_ACID, MONAZITE_HEAVY_RESIDUE_SOLUTION,
            ULTRAHEAVY_ELEMENT_MIXTURE, LIQUID_GLASS, BRINE, CUMENE, CUMENE_HYDROPEROXIDE, PHENOL, ACETONE, PHENOL_TAR,
            CRUDE_ALLYL_CHLORIDE, ALLYL_CHLORIDE, CHLORINATED_WASTE, HYPOCHLOROUS_ACID, DICHLOROHYDRIN,
            CRUDE_EPICHLOROHYDRIN, EPICHLOROHYDRIN, CRUDE_BISPHENOL_A, BISPHENOL_A, CHLOROHYDRIN_ETHER,
            CRUDE_EPOXY_RESIN, EPOXY_RESIN, ANTIMONY_PENTAFLUORIDE, FLUOROANTIMONIC_ACID).stream(),
            Stream.concat(ALGAE.stream(), Stream.concat(BIO.stream(), RESONANT.stream()))).toList();

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

    public static void init() {}

    private NIFluids() {}
}
