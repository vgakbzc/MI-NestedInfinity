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

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static void init() {}

    private NIItems() {}
}
