package com.nestedinfinity.mod.items;

import com.nestedinfinity.mod.NestedInfinity;

import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Optical-circuit program (the tier above resonant): four stronger material
 * families (FFKM perfluoroelastomer, PEEK, semiconductor-grade chemicals, DUV
 * photoresist + UV optical adhesive), the new element chains (Ge/Nb/Li/Er/Eu/
 * Ce/Ru/Be/GST/fused silica), the photonic parts, the DUV lithography
 * process stream, the HNIW implosion explosives, the neutronium program and
 * the optical processing units. All chains live in NIRecipeProvider's
 * optical*Chain methods (see docs/RECIPE_COVERAGE.md section 10).
 */
public final class NIOpticalItems {

    // -- FFKM perfluoroelastomer (Kalrez): TFE/PMVE terpolymer ---------------
    public static final DeferredItem<Item> R22_PYROLYSIS_CATALYST = register("r22_pyrolysis_catalyst"); // AlF3/CaF2
    public static final DeferredItem<Item> DCP_PEROXIDE = register("dcp_peroxide"); // peroxide cure
    public static final DeferredItem<Item> TAIC_COAGENT = register("taic_coagent"); // triallyl co-curing agent
    public static final DeferredItem<Item> PERFLUORO_CURE_SITE_MONOMER = register("perfluoro_cure_site_monomer");
    public static final DeferredItem<Item> FFKM_GUM = register("ffkm_gum");
    public static final DeferredItem<Item> FFKM_SHEET = register("ffkm_sheet");

    // -- PEEK polyetheretherketone -------------------------------------------
    public static final DeferredItem<Item> P_FLUOROBENZOYL_CHLORIDE = register("p_fluorobenzoyl_chloride");
    public static final DeferredItem<Item> DIFLUOROBENZOPHENONE = register("difluorobenzophenone");
    public static final DeferredItem<Item> DIPHENYL_SULFONE = register("diphenyl_sulfone"); // polymerization solvent
    public static final DeferredItem<Item> POTASSIUM_CARBONATE = register("potassium_carbonate"); // K2CO3 base
    public static final DeferredItem<Item> POTASSIUM_CHLORIDE = register("potassium_chloride"); // brine potash cut
    public static final DeferredItem<Item> HYDROQUINONE = register("hydroquinone");
    public static final DeferredItem<Item> PEEK_POWDER = register("peek_powder");
    public static final DeferredItem<Item> PEEK_PLATE = register("peek_plate");
    public static final DeferredItem<Item> PEEK_INSULATOR_SHEET = register("peek_insulator_sheet");

    // -- semiconductor-grade chemicals (AO hydrogen peroxide loop) ------------
    public static final DeferredItem<Item> ETHYLANTHRAQUINONE = register("ethylanthraquinone"); // EAQ carrier
    public static final DeferredItem<Item> UREA = register("urea"); // nitrous-acid scavenger

    // -- DUV photoresist + UV optical adhesive --------------------------------
    public static final DeferredItem<Item> POLYHYDROXYSTYRENE_RESIN = register("polyhydroxystyrene_resin"); // KrF
    public static final DeferredItem<Item> ALICYCLIC_ACRYLATE_RESIN = register("alicyclic_acrylate_resin"); // ArF
    public static final DeferredItem<Item> TRIPHENYLSULFONIUM_PAG = register("triphenylsulfonium_pag"); // acid generator
    public static final DeferredItem<Item> UV_PHOTOINITIATOR = register("uv_photoinitiator");

    // -- HNIW / CL-20 explosive chain (two-stage hydrogenolysis, e-polymorph) --
    public static final DeferredItem<Item> HBIW_CRUDE = register("hbiw_crude");
    public static final DeferredItem<Item> HBIW_CRYSTAL = register("hbiw_crystal");
    public static final DeferredItem<Item> TADBIW = register("tadbiw");
    public static final DeferredItem<Item> TAIW = register("taiw");
    public static final DeferredItem<Item> HNIW_CRUDE = register("hniw_crude");
    public static final DeferredItem<Item> HNIW_POWDER = register("hniw_powder"); // epsilon crystal form
    public static final DeferredItem<Item> DINITROGEN_PENTOXIDE = register("dinitrogen_pentoxide"); // N2O5
    public static final DeferredItem<Item> SODIUM_AZIDE = register("sodium_azide");
    public static final DeferredItem<Item> LEAD_AZIDE_DETONATOR = register("lead_azide_detonator");
    public static final DeferredItem<Item> HNIW_IMPLOSION_LENS = register("hniw_implosion_lens");

    // -- neutronium program: decay neutrons, implosion, degenerate matter ------
    // (beryllium itself is MI's own material — c:dusts/beryllium)
    public static final DeferredItem<Item> BERYLLIUM_REFLECTOR = register("beryllium_reflector");
    public static final DeferredItem<Item> CALIFORNIUM_INITIATOR = register("californium_initiator");
    public static final DeferredItem<Item> FISSION_FRAGMENTS = register("fission_fragments");
    public static final DeferredItem<Item> GIANT_MATTER_BALL = register("giant_matter_ball");

    // -- optical superconducting alloy (Ru/Am/Nt/Naquadah/Eu/graphene) ---------
    public static final DeferredItem<Item> OPTICAL_ALLOY_MIXTURE = register("optical_alloy_mixture");
    public static final DeferredItem<Item> OPTICAL_SUPERCONDUCTOR_INGOT = register("optical_superconductor_ingot");
    public static final DeferredItem<Item> OPTICAL_SUPERCONDUCTOR_WIRE = register("optical_superconductor_wire");

    // -- new element chains ----------------------------------------------------
    public static final DeferredItem<Item> ZINC_FLUE_DUST = register("zinc_flue_dust"); // Ge carrier
    public static final DeferredItem<Item> GERMANIUM_DIOXIDE = register("germanium_dioxide");
    public static final DeferredItem<Item> GERMANIUM_INGOT = register("germanium_ingot");
    public static final DeferredItem<Item> GERMANIUM_WAFER = register("germanium_wafer");
    public static final DeferredItem<Item> COLTAN_CONCENTRATE = register("coltan_concentrate");
    public static final DeferredItem<Item> NIOBIUM_PENTOXIDE = register("niobium_pentoxide");
    public static final DeferredItem<Item> NIOBIUM_INGOT = register("niobium_ingot");
    public static final DeferredItem<Item> LITHIUM_CARBONATE = register("lithium_carbonate");
    public static final DeferredItem<Item> ERBIUM_OXIDE = register("erbium_oxide");
    public static final DeferredItem<Item> EUROPIUM_DUST = register("europium_dust");
    public static final DeferredItem<Item> CERIUM_OXIDE = register("cerium_oxide");
    public static final DeferredItem<Item> RUTHENIUM_DIOXIDE = register("ruthenium_dioxide");
    public static final DeferredItem<Item> RUTHENIUM_DUST = register("ruthenium_dust");
    public static final DeferredItem<Item> GST_TARGET = register("gst_target"); // Ge2Sb2Te5 sputter target
    public static final DeferredItem<Item> GST_MEMORY_CELL = register("gst_memory_cell"); // phase-change cell

    // -- optics and photonics ---------------------------------------------------
    public static final DeferredItem<Item> FUSED_SILICA_INGOT = register("fused_silica_ingot");
    public static final DeferredItem<Item> FUSED_SILICA_PLATE = register("fused_silica_plate");
    public static final DeferredItem<Item> FIBER_PREFORM = register("fiber_preform");
    public static final DeferredItem<Item> ERBIUM_DOPED_FIBER = register("erbium_doped_fiber");
    public static final DeferredItem<Item> LITHIUM_NIOBATE_WAFER = register("lithium_niobate_wafer");
    public static final DeferredItem<Item> ELECTROOPTIC_MODULATOR = register("electrooptic_modulator");
    public static final DeferredItem<Item> LASER_DIODE = register("laser_diode");
    public static final DeferredItem<Item> SOLID_STATE_LASER = register("solid_state_laser");
    public static final DeferredItem<Item> EXCIMER_LASER = register("excimer_laser");
    public static final DeferredItem<Item> CAF2_LENS_ARRAY = register("caf2_lens_array");
    public static final DeferredItem<Item> OPTICAL_BENCH = register("optical_bench");
    public static final DeferredItem<Item> PHOTOMASK_BLANK = register("photomask_blank");
    public static final DeferredItem<Item> PHOTOMASK = register("photomask");
    public static final DeferredItem<Item> OPTICAL_TRANSCEIVER = register("optical_transceiver");
    public static final DeferredItem<Item> SINGLE_PHOTON_DETECTOR = register("single_photon_detector"); // SNSPD
    public static final DeferredItem<Item> OPTICAL_WAVEGUIDE = register("optical_waveguide");

    // -- DUV lithography process stream ----------------------------------------
    public static final DeferredItem<Item> LITHO_SUBSTRATE = register("litho_substrate");
    public static final DeferredItem<Item> COATED_SUBSTRATE = register("coated_substrate");
    public static final DeferredItem<Item> EXPOSED_SUBSTRATE = register("exposed_substrate");
    public static final DeferredItem<Item> DEVELOPED_SUBSTRATE = register("developed_substrate");
    public static final DeferredItem<Item> ETCHED_SUBSTRATE = register("etched_substrate");
    public static final DeferredItem<Item> METALLIZED_WAFER = register("metallized_wafer");
    public static final DeferredItem<Item> PHOTONIC_CHIP = register("photonic_chip");

    // -- optical processing units + large elite pump/motor ---------------------
    public static final DeferredItem<Item> OPTICAL_RANDOM_ACCESS_MEMORY = register("optical_random_access_memory");
    public static final DeferredItem<Item> OPTICAL_MEMORY_MANAGEMENT_UNIT = register("optical_memory_management_unit");
    public static final DeferredItem<Item> OPTICAL_ARITHMETIC_LOGIC_UNIT = register("optical_arithmetic_logic_unit");
    public static final DeferredItem<Item> LARGE_ELITE_MOTOR = register("large_elite_motor");
    public static final DeferredItem<Item> LARGE_ELITE_PUMP = register("large_elite_pump");

    public static final List<DeferredItem<Item>> ALL = List.of(
            R22_PYROLYSIS_CATALYST, DCP_PEROXIDE, TAIC_COAGENT, PERFLUORO_CURE_SITE_MONOMER, FFKM_GUM, FFKM_SHEET,
            P_FLUOROBENZOYL_CHLORIDE, DIFLUOROBENZOPHENONE, DIPHENYL_SULFONE, POTASSIUM_CARBONATE,
            POTASSIUM_CHLORIDE, HYDROQUINONE,
            PEEK_POWDER, PEEK_PLATE, PEEK_INSULATOR_SHEET,
            ETHYLANTHRAQUINONE, UREA,
            POLYHYDROXYSTYRENE_RESIN, ALICYCLIC_ACRYLATE_RESIN, TRIPHENYLSULFONIUM_PAG, UV_PHOTOINITIATOR,
            HBIW_CRUDE, HBIW_CRYSTAL, TADBIW, TAIW, HNIW_CRUDE, HNIW_POWDER, DINITROGEN_PENTOXIDE,
            SODIUM_AZIDE, LEAD_AZIDE_DETONATOR, HNIW_IMPLOSION_LENS,
            BERYLLIUM_REFLECTOR, CALIFORNIUM_INITIATOR, FISSION_FRAGMENTS, GIANT_MATTER_BALL,
            OPTICAL_ALLOY_MIXTURE, OPTICAL_SUPERCONDUCTOR_INGOT, OPTICAL_SUPERCONDUCTOR_WIRE,
            ZINC_FLUE_DUST, GERMANIUM_DIOXIDE, GERMANIUM_INGOT, GERMANIUM_WAFER, COLTAN_CONCENTRATE,
            NIOBIUM_PENTOXIDE, NIOBIUM_INGOT, LITHIUM_CARBONATE, ERBIUM_OXIDE, EUROPIUM_DUST, CERIUM_OXIDE,
            RUTHENIUM_DIOXIDE, RUTHENIUM_DUST, GST_TARGET, GST_MEMORY_CELL,
            FUSED_SILICA_INGOT, FUSED_SILICA_PLATE, FIBER_PREFORM, ERBIUM_DOPED_FIBER, LITHIUM_NIOBATE_WAFER,
            ELECTROOPTIC_MODULATOR, LASER_DIODE, SOLID_STATE_LASER, EXCIMER_LASER, CAF2_LENS_ARRAY, OPTICAL_BENCH,
            PHOTOMASK_BLANK, PHOTOMASK, OPTICAL_TRANSCEIVER, SINGLE_PHOTON_DETECTOR, OPTICAL_WAVEGUIDE,
            LITHO_SUBSTRATE, COATED_SUBSTRATE, EXPOSED_SUBSTRATE, DEVELOPED_SUBSTRATE, ETCHED_SUBSTRATE,
            METALLIZED_WAFER, PHOTONIC_CHIP,
            OPTICAL_RANDOM_ACCESS_MEMORY, OPTICAL_MEMORY_MANAGEMENT_UNIT, OPTICAL_ARITHMETIC_LOGIC_UNIT,
            LARGE_ELITE_MOTOR, LARGE_ELITE_PUMP);

    private static DeferredItem<Item> register(String name) {
        return NestedInfinity.ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    public static void init() {}

    private NIOpticalItems() {}
}
