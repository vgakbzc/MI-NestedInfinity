package com.nestedinfinity.mod.items;
import com.nestedinfinity.mod.NestedInfinity;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Special items that do not belong to a material group.
 * Material parts (dust/ingot/plate/rod etc.) are registered in bulk via {@link com.nestedinfinity.mod.material.NIMaterials}.
 */
public final class NIItems {
    public static final DeferredItem<Item> NAQUADAH_COMPUTING_UNIT = register("naquadah_computing_unit");
    public static final DeferredItem<Item> NEUTRON_SOURCE = register("neutron_source");
    public static final DeferredItem<Item> HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH = register("high_purity_monocrystalline_naquadah");

    public static final DeferredItem<Item> MONAZITE_RESIDUE = register("monazite_residue");
    public static final DeferredItem<Item> HEAVY_ELEMENT_RESIDUE_OXIDE = register("heavy_element_residue_oxide");
    public static final DeferredItem<Item> HEAVY_ELEMENT_RESIDUE_DUST = register("heavy_element_residue_dust");
    public static final DeferredItem<Item> PLATINIZED_ULTRAHEAVY_RESIDUE_DUST = register("platinized_ultraheavy_residue_dust");

    // Nichrome coil production chain
    public static final DeferredItem<Item> MICA_DUST = register("mica_dust");
    public static final DeferredItem<Item> MICA_INSULATOR_SHEET = register("mica_insulator_sheet");
    public static final DeferredItem<Item> PLASTIC_MICA_MIXTURE = register("plastic_mica_mixture");

    // Silicone rubber chain (TPV coil insulation); the cured block is a placeable block (see NIBlocks)
    public static final DeferredItem<Item> SILICONE_RUBBER_SHEET = register("silicone_rubber_sheet");
    public static final DeferredItem<Item> SILICONE_MICA_INSULATOR_SHEET = register("silicone_mica_insulator_sheet");

    // Epoxy resin production chain
    public static final DeferredItem<Item> EPOXY_PLATE = register("epoxy_plate");
    public static final DeferredItem<Item> ION_EXCHANGE_RESIN = register("ion_exchange_resin");
    public static final DeferredItem<Item> PLATINUM_WIRE_MESH = register("platinum_wire_mesh");
    public static final DeferredItem<Item> ION_EXCHANGE_CATALYST = register("ion_exchange_catalyst");

    // Bacteria program: rhodophyta processing (agar), the 16 most common amino acids and protein
    // (the chain bootstraps from kelp-extracted low-purity algae before pure cultures exist)
    public static final DeferredItem<Item> LOW_PURITY_ALGAE = register("low_purity_algae");
    public static final DeferredItem<Item> WET_RHODOPHYTA = register("wet_rhodophyta");
    public static final DeferredItem<Item> DESULFATED_RHODOPHYTA = register("desulfated_rhodophyta");
    public static final DeferredItem<Item> WASHED_RHODOPHYTA = register("washed_rhodophyta");
    public static final DeferredItem<Item> BLEACHED_RHODOPHYTA = register("bleached_rhodophyta");
    public static final DeferredItem<Item> RHODOPHYTA_RESIDUE = register("rhodophyta_residue");
    public static final DeferredItem<Item> FROZEN_AGAR_GEL = register("frozen_agar_gel");
    public static final DeferredItem<Item> AGAR_GEL = register("agar_gel");
    public static final DeferredItem<Item> AGAR = register("agar");

    public static final DeferredItem<Item> GLYCINE = register("glycine");
    public static final DeferredItem<Item> ALANINE = register("alanine");
    public static final DeferredItem<Item> VALINE = register("valine");
    public static final DeferredItem<Item> LEUCINE = register("leucine");
    public static final DeferredItem<Item> ISOLEUCINE = register("isoleucine");
    public static final DeferredItem<Item> SERINE = register("serine");
    public static final DeferredItem<Item> THREONINE = register("threonine");
    public static final DeferredItem<Item> ASPARTIC_ACID = register("aspartic_acid");
    public static final DeferredItem<Item> GLUTAMIC_ACID = register("glutamic_acid");
    public static final DeferredItem<Item> LYSINE = register("lysine");
    public static final DeferredItem<Item> ARGININE = register("arginine");
    public static final DeferredItem<Item> PROLINE = register("proline");
    public static final DeferredItem<Item> PHENYLALANINE = register("phenylalanine");
    public static final DeferredItem<Item> TYROSINE = register("tyrosine");
    public static final DeferredItem<Item> ASPARAGINE = register("asparagine");
    public static final DeferredItem<Item> GLUTAMINE = register("glutamine");
    public static final DeferredItem<Item> PROTEIN = register("protein");

    // Amino-acid route intermediates
    public static final DeferredItem<Item> ORNITHINE = register("ornithine");
    public static final DeferredItem<Item> CYANAMIDE = register("cyanamide");
    public static final DeferredItem<Item> CHLOROACETIC_ACID = register("chloroacetic_acid");
    public static final DeferredItem<Item> EPSILON_AMINOCAPROIC_ACID = register("epsilon_aminocaproic_acid");
    public static final DeferredItem<Item> FUMARIC_ACID = register("fumaric_acid");
    public static final DeferredItem<Item> MALEIC_ANHYDRIDE = register("maleic_anhydride");
    public static final DeferredItem<Item> ALPHA_KETOGLUTARIC_ACID = register("alpha_ketoglutaric_acid");
    public static final DeferredItem<Item> GLYCOLALDEHYDE = register("glycolaldehyde");

    // Lime for the nutrient agar medium (calcination + slaking);
    // quicklime itself is a placeable block (see NIBlocks)
    public static final DeferredItem<Item> SLAKED_LIME = register("slaked_lime");

    // Chain catalysts (returned by their recipes, all craftable upstream) and byproducts
    public static final DeferredItem<Item> HABER_IRON_CATALYST = register("haber_iron_catalyst");
    public static final DeferredItem<Item> REFORMING_NICKEL_CATALYST = register("reforming_nickel_catalyst");
    public static final DeferredItem<Item> METHANOL_SYNTHESIS_CATALYST = register("methanol_synthesis_catalyst");
    public static final DeferredItem<Item> SILVER_GAUZE_CATALYST = register("silver_gauze_catalyst");
    public static final DeferredItem<Item> PLATINUM_GAUZE_CATALYST = register("platinum_gauze_catalyst");
    public static final DeferredItem<Item> IRIDIUM_CARBONYLATION_CATALYST = register("iridium_carbonylation_catalyst");
    public static final DeferredItem<Item> HYDROFORMYLATION_CATALYST = register("hydroformylation_catalyst");
    public static final DeferredItem<Item> COPPER_CHLORIDE_CATALYST = register("copper_chloride_catalyst");
    public static final DeferredItem<Item> IMMOBILIZED_ENZYME = register("immobilized_enzyme");
    public static final DeferredItem<Item> AMMONIUM_CHLORIDE = register("ammonium_chloride");
    public static final DeferredItem<Item> CALCIUM_CHLORIDE = register("calcium_chloride");
    public static final DeferredItem<Item> SODIUM_SULFATE = register("sodium_sulfate");

    // Strecker intermediates: alpha-aminonitriles before acid hydrolysis
    public static final DeferredItem<Item> ALANINE_AMINONITRILE = register("alanine_aminonitrile");
    public static final DeferredItem<Item> VALINE_AMINONITRILE = register("valine_aminonitrile");
    public static final DeferredItem<Item> LEUCINE_AMINONITRILE = register("leucine_aminonitrile");
    public static final DeferredItem<Item> ISOLEUCINE_AMINONITRILE = register("isoleucine_aminonitrile");
    public static final DeferredItem<Item> SERINE_AMINONITRILE = register("serine_aminonitrile");
    public static final DeferredItem<Item> PHENYLALANINE_AMINONITRILE = register("phenylalanine_aminonitrile");

    // Advanced superconductor cable chain
    public static final DeferredItem<Item> VANADIUM_DUST = register("vanadium_dust");
    public static final DeferredItem<Item> SODIUM_VANADATE = register("sodium_vanadate");
    public static final DeferredItem<Item> CINNABAR_DUST = register("cinnabar_dust");
    public static final DeferredItem<Item> BARITE_DUST = register("barite_dust");
    public static final DeferredItem<Item> MERCURY_OXIDE_DUST = register("mercury_oxide_dust");
    public static final DeferredItem<Item> BARIUM_OXIDE_DUST = register("barium_oxide_dust");
    public static final DeferredItem<Item> MERCURY_BARIUM_TITANIUM_COPPER_OXIDE = register("mercury_barium_titanium_copper_oxide");
    public static final DeferredItem<Item> SUPERCONDUCTOR_SUBSTRATE = register("superconductor_substrate");
    public static final DeferredItem<Item> SUPERCONDUCTOR_SUBSTRATE_WIRE = register("superconductor_substrate_wire");

    // Wetware circuit program: mutagenesis feedstock and the bio-replacements
    // for MI's processing-unit parts (RAM / MMU / ALU). 硅岩 = naquadah, taken
    // straight from the mod's naquadah material chain; the mutagen itself is a
    // fluid (see NIFluids.MUTAGEN).
    public static final DeferredItem<Item> SUPERCHARGED_NAQUADAH = register("supercharged_naquadah");
    public static final DeferredItem<Item> BIO_RANDOM_ACCESS_MEMORY = register("bio_random_access_memory");
    public static final DeferredItem<Item> BIO_MEMORY_MANAGEMENT_UNIT = register("bio_memory_management_unit");
    public static final DeferredItem<Item> BIO_ARITHMETIC_LOGIC_UNIT = register("bio_arithmetic_logic_unit");

    // Elite pump tier feeding the wetware circuit board's cryo loop
    public static final DeferredItem<Item> ELITE_MOTOR = register("elite_motor");
    public static final DeferredItem<Item> ELITE_PUMP = register("elite_pump");

    // Wetware circuit board program: p-toluenesulfonic acid, the Celazole PBI route
    // (benzidine -> DAB, m-xylene -> diphenyl isophthalate) and the board chassis
    public static final DeferredItem<Item> NAQUADAH_FRAME = register("naquadah_frame");
    public static final DeferredItem<Item> POLYBENZIMIDAZOLE_PLATE = register("polybenzimidazole_plate");
    public static final DeferredItem<Item> P_TOLUENESULFONIC_ACID = register("p_toluenesulfonic_acid");
    public static final DeferredItem<Item> ISOPHTHALIC_ACID = register("isophthalic_acid");
    public static final DeferredItem<Item> DIPHENYL_ISOPHTHALATE = register("diphenyl_isophthalate");
    public static final DeferredItem<Item> BENZIDINE = register("benzidine");
    public static final DeferredItem<Item> DINITROBENZIDINE = register("dinitrobenzidine");
    public static final DeferredItem<Item> DIAMINOBENZIDINE = register("diaminobenzidine");

    // Cyanoacrylate adhesive program: the "stronger glue" (vs MI's acrylic glue),
    // NaCN -> cyanoacetic acid -> methyl cyanoacetate -> polymer -> cracked monomer
    public static final DeferredItem<Item> SODIUM_CYANIDE = register("sodium_cyanide");
    public static final DeferredItem<Item> CYANOACETIC_ACID = register("cyanoacetic_acid");
    public static final DeferredItem<Item> POLY_METHYL_CYANOACRYLATE = register("poly_methyl_cyanoacrylate");

    // Resonant circuit program -------------------------------------------------
    // The eight Q8 tuning notes live in items/resonance/NINotes; the tuning block
    // and the resonance attuner machine in blocks/resonance. Everything below is
    // datagen'd in NIRecipeProvider.resonantChain (see docs/RECIPE_COVERAGE.md).

    // Individually purified superheavy elements of the periodic table's last row
    // (Ac through Cn). U and Pu reuse MI's own items; these dusts are produced by
    // the multi-cycle separation cascade and held as a strategic reserve for the
    // future circuit tiers (optical, EMI, ...).
    public static final DeferredItem<Item> ACTINIUM_DUST = register("actinium_dust");
    public static final DeferredItem<Item> PROTACTINIUM_DUST = register("protactinium_dust");
    public static final DeferredItem<Item> THORIUM_DUST = register("thorium_dust");
    public static final DeferredItem<Item> NEPTUNIUM_DUST = register("neptunium_dust");
    public static final DeferredItem<Item> AMERICIUM_DUST = register("americium_dust");
    public static final DeferredItem<Item> CURIUM_DUST = register("curium_dust");
    public static final DeferredItem<Item> BERKELIUM_DUST = register("berkelium_dust");
    public static final DeferredItem<Item> CALIFORNIUM_DUST = register("californium_dust");
    public static final DeferredItem<Item> EINSTEINIUM_DUST = register("einsteinium_dust");
    public static final DeferredItem<Item> FERMIUM_DUST = register("fermium_dust");
    public static final DeferredItem<Item> MENDELEVIUM_DUST = register("mendelevium_dust");
    public static final DeferredItem<Item> NOBELIUM_DUST = register("nobelium_dust");
    public static final DeferredItem<Item> LAWRENCIUM_DUST = register("lawrencium_dust");
    public static final DeferredItem<Item> RUTHERFORDIUM_DUST = register("rutherfordium_dust");
    public static final DeferredItem<Item> DUBNIUM_DUST = register("dubnium_dust");
    public static final DeferredItem<Item> SEABORGIUM_DUST = register("seaborgium_dust");
    public static final DeferredItem<Item> BOHRIUM_DUST = register("bohrium_dust");
    public static final DeferredItem<Item> HASSIUM_DUST = register("hassium_dust");
    public static final DeferredItem<Item> MEITNERIUM_DUST = register("meitnerium_dust");
    public static final DeferredItem<Item> DARMSTADTIUM_DUST = register("darmstadtium_dust");
    public static final DeferredItem<Item> ROENTGENIUM_DUST = register("roentgenium_dust");
    public static final DeferredItem<Item> COPERNICIUM_DUST = register("copernicium_dust");

    // Separation-cascade catalysts and reagents (probability-consumed where they
    // act as catalysts; MI item inputs carry the consumption probability).
    public static final DeferredItem<Item> TRIBUTYL_PHOSPHATE = register("tributyl_phosphate"); // PUREX extractant
    public static final DeferredItem<Item> CMPO_EXTRACTANT = register("cmpo_extractant"); // TRUEX extractant
    public static final DeferredItem<Item> DTPA_COMPLEXANT = register("dtpa_complexant"); // TALSPEAK actinide selectant
    public static final DeferredItem<Item> ALPHA_HIBA_ELUANT = register("alpha_hiba_eluant"); // cation-exchange eluant
    public static final DeferredItem<Item> SODIUM_NITRITE = register("sodium_nitrite"); // valence adjustment
    public static final DeferredItem<Item> SODIUM_CHLORATE = register("sodium_chlorate"); // Bk(IV) oxidation
    public static final DeferredItem<Item> HYDRAZINE = register("hydrazine"); // Pu stripping reductant
    public static final DeferredItem<Item> GOLD_FOIL = register("gold_foil"); // Cn surface trap
    public static final DeferredItem<Item> TELLURIUM_DUST = register("tellurium_dust"); // fission byproduct, telluric acid feed

    // Light naquide (trinium_dinaquide coil component) and the fusion-born alloys
    public static final DeferredItem<Item> CRUDE_NAQUIDE_POWDER = register("crude_naquide_powder");
    public static final DeferredItem<Item> NAQUIDE = register("naquide");
    public static final DeferredItem<Item> ADAMANTIUM_INGOT = register("adamantium_ingot");
    public static final DeferredItem<Item> MITHRIL_INGOT = register("mithril_ingot");

    // Piezoelectric quartz side (real PbTiO3 ceramics; MI has no zirconium)
    public static final DeferredItem<Item> LEAD_TITANATE_DUST = register("lead_titanate_dust");
    public static final DeferredItem<Item> LEAD_TITANATE_PLATE = register("lead_titanate_plate");
    public static final DeferredItem<Item> PIEZO_WAFER = register("piezo_wafer");
    public static final DeferredItem<Item> QUARTZ_OSCILLATOR = register("quartz_oscillator");
    public static final DeferredItem<Item> SAW_RESONATOR = register("saw_resonator");

    // Polyimide program (real Kapton chemistry): durene oxidation -> PMDA,
    // nitrobenzene chlorination/etherification/reduction -> ODA, polyamic acid
    // imidization -> PI
    public static final DeferredItem<Item> DURENE = register("durene");
    public static final DeferredItem<Item> PYROMELLITIC_ACID = register("pyromellitic_acid");
    public static final DeferredItem<Item> PYROMELLITIC_DIANHYDRIDE = register("pyromellitic_dianhydride");
    public static final DeferredItem<Item> P_NITROCHLOROBENZENE = register("p_nitrochlorobenzene");
    public static final DeferredItem<Item> DINITRODIPHENYL_ETHER = register("dinitrodiphenyl_ether");
    public static final DeferredItem<Item> DIAMINODIPHENYL_ETHER = register("diaminodiphenyl_ether");
    public static final DeferredItem<Item> POLYIMIDE_DUST = register("polyimide_dust");
    public static final DeferredItem<Item> POLYIMIDE_PLATE = register("polyimide_plate");

    // Fluoroelastomer (FKM) gasket stock for the resonance chamber
    public static final DeferredItem<Item> FLUOROELASTOMER_SHEET = register("fluoroelastomer_sheet");

    // Resonant superconductor: monazite-residue yttrium -> YBCO target ->
    // sputtered tape -> 2^36 EU/t cable (material registered cable-only)
    public static final DeferredItem<Item> YTTRIUM_OXIDE = register("yttrium_oxide");
    public static final DeferredItem<Item> CUPRIC_OXIDE = register("cupric_oxide");
    public static final DeferredItem<Item> YBCO_TARGET = register("ybco_target");
    public static final DeferredItem<Item> SAPPHIRE_SUBSTRATE = register("sapphire_substrate");
    public static final DeferredItem<Item> RESONANT_SUPERCONDUCTOR_TAPE = register("resonant_superconductor_tape");

    // Processing-unit replacements one tier above the bio parts (each consumes a
    // machine-only Q8 note color: green RAM, cyan MMU, purple ALU)
    public static final DeferredItem<Item> RESONANT_RANDOM_ACCESS_MEMORY = register("resonant_random_access_memory");
    public static final DeferredItem<Item> RESONANT_MEMORY_MANAGEMENT_UNIT = register("resonant_memory_management_unit");
    public static final DeferredItem<Item> RESONANT_ARITHMETIC_LOGIC_UNIT = register("resonant_arithmetic_logic_unit");

    // Signature one-off components (the black-note saser, the resonance chamber,
    // the phase-locked loop) consumed by the coil and the board
    public static final DeferredItem<Item> SASER = register("saser");
    public static final DeferredItem<Item> RESONANCE_CHAMBER = register("resonance_chamber");
    public static final DeferredItem<Item> PHASE_LOCKED_LOOP = register("phase_locked_loop");

    // Platinum-group residue of the bromine-free aqua-regia cycle (Rf/Db/Sg/Bh/Mt/Ds feed)
    public static final DeferredItem<Item> PGM_RESIDUE = register("pgm_residue");

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static void init() {}

    private NIItems() {}
}
