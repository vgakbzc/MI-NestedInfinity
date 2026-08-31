package com.nestedinfinity.mod.datagen;

import com.nestedinfinity.mod.NIBlocks;
import com.nestedinfinity.mod.NIItems;
import com.nestedinfinity.mod.NIMaterials;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * Collects recipes into {@link NIRecipes} and writes them out.
 * Material standard recipes come from NIMaterial; the special production
 * chain recipes live here.
 */
public final class NIRecipeProvider implements DataProvider {
    private final PackOutput output;

    public NIRecipeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.runAsync(() -> {
            NIRecipes r = new NIRecipes(output);
            NIMaterials.Materials.values().forEach(m -> m.tags(r));
            chainRecipes(r);
            r.writeAll(cachedOutput);
        });
    }

    @Override
    public String getName() {
        return "Nested Infinity Recipes";
    }

    private void chainRecipes(NIRecipes r) {
        niquadahChain(r);
        uraniumTriplatinumChain(r);
        nichromeChain(r);
        nichromeCoilChain(r);
        epoxyChain(r);
        circuitChain(r);
    }

    /**
     * Propylene-route epoxy resin program, 20 unit operations:
     * brine chlor-alkali -> allyl chloride -> epichlorohydrin, cumene -> phenol/acetone -> BPA,
     * BPA+ECH etherification/ring closure -> DGEBA epoxy resin, curing and cutting.
     * Realistic catalysts (H2SO4) and byproducts (HCl, propane-free benzene recycle, salt, water, tar) included.
     */
    private void epoxyChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // -- Brine / chlor-alkali feedstock --
        // 1. Brine: salt + water
        r.machine("mixer", 8, 120)
                .tagIn("c:dusts/salt", 2)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "brine", 1000)
                .save("epoxy/brine");
        // 2. Chlor-alkali electrolysis: brine -> NaOH + chlorine + hydrogen
        r.machine("electrolyzer", 16, 300)
                .fluidIn(ni + "brine", 1000)
                .fluidOut(mi + "sodium_hydroxide", 900)
                .fluidOut(mi + "chlorine", 600)
                .fluidOut(mi + "hydrogen", 300)
                .save("epoxy/chlor_alkali");

        // -- Propylene -> allyl chloride -> epichlorohydrin --
        // 3. High-temperature chlorination of propylene
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "propene", 1000)
                .fluidIn(mi + "chlorine", 1000)
                .fluidOut(ni + "crude_allyl_chloride", 1000)
                .fluidOut(mi + "hydrochloric_acid", 850)
                .save("epoxy/allyl_chloride_chlorination");
        // 4. Allyl chloride purification
        r.machine("distillery", 16, 200)
                .fluidIn(ni + "crude_allyl_chloride", 1000)
                .fluidOut(ni + "allyl_chloride", 850)
                .fluidOut(ni + "chlorinated_waste", 150)
                .save("epoxy/allyl_chloride_purification");
        // 5. Chlorine-water contact: Cl2 + H2O -> HOCl + HCl
        r.machine("chemical_reactor", 16, 120)
                .fluidIn(mi + "chlorine", 500)
                .fluidIn("minecraft:water", 500)
                .fluidOut(ni + "hypochlorous_acid", 500)
                .fluidOut(mi + "hydrochloric_acid", 500)
                .save("epoxy/hypochlorous_acid");
        // 6. Chlorohydrination
        r.machine("chemical_reactor", 32, 160)
                .fluidIn(ni + "allyl_chloride", 850)
                .fluidIn(ni + "hypochlorous_acid", 850)
                .fluidOut(ni + "dichlorohydrin", 850)
                .save("epoxy/dichlorohydrin");
        // 7. Dehydrochlorination ring closure
        r.machine("chemical_reactor", 32, 160)
                .fluidIn(ni + "dichlorohydrin", 850)
                .fluidIn(mi + "sodium_hydroxide", 900)
                .fluidOut(ni + "crude_epichlorohydrin", 1000)
                .fluidOut("minecraft:water", 400)
                .itemOut("modern_industrialization:salt_dust", 3)
                .save("epoxy/epichlorohydrin_ring_closure");
        // 8. Crude ECH purification (ion exchange, consumes platinum-coated catalyst)
        r.machine("ion_exchange", 16, 160)
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1)
                .fluidIn(ni + "crude_epichlorohydrin", 1000)
                .fluidOut(ni + "epichlorohydrin", 850)
                .fluidOut("minecraft:water", 150)
                .save("epoxy/epichlorohydrin_purification");
        // 20. Waste stripping recovery (allyl chloride from chlorinated waste)
        r.machine("distillery", 8, 160)
                .fluidIn(ni + "chlorinated_waste", 150)
                .fluidOut(ni + "allyl_chloride", 30)
                .fluidOut("minecraft:water", 120)
                .save("epoxy/chlorinated_waste_stripping");

        // -- Ion exchange resin catalyst line (sulfonated styrene resins) --
        // Route A: direct sulfonation of styrene
        r.machine("chemical_reactor", 32, 240)
                .fluidIn(mi + "styrene", 1000)
                .fluidIn(mi + "sulfuric_acid", 1000)
                .itemOut(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 4)
                .save("epoxy/ion_exchange_resin_styrene");
        // Route B: sulfonation of styrene-butadiene rubber (copolymer, sturdier beads)
        r.machine("chemical_reactor", 32, 240)
                .fluidIn(mi + "styrene_butadiene_rubber", 500)
                .fluidIn(mi + "sulfuric_acid", 1000)
                .itemOut(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 4)
                .save("epoxy/ion_exchange_resin_sbr");
        // Platinum wire mesh: wire + plates + PVC (MI platinum has no rod part)
        r.machine("assembler", 8, 200)
                .itemIn(mi + "platinum_wire", 8)
                .itemIn(mi + "platinum_plate", 2)
                .fluidIn(mi + "polyvinyl_chloride", 36)
                .itemOut(NIItems.PLATINUM_WIRE_MESH.getId().toString(), 1)
                .save("epoxy/platinum_wire_mesh");
        // Catalyst: resin stirred with platinum mesh -> 32 catalysts
        r.machine("mixer", 16, 200)
                .itemIn(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 1)
                .itemIn(NIItems.PLATINUM_WIRE_MESH.getId().toString(), 1)
                .itemOut(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 32)
                .save("epoxy/ion_exchange_catalyst");
        // -- Cumene process -> phenol + acetone --
        // 9. Friedel-Crafts alkylation
        r.machine("chemical_reactor", 32, 240)
                .fluidIn(mi + "benzene", 600)
                .fluidIn(mi + "propene", 400)
                .fluidIn(mi + "sulfuric_acid", 25)
                .fluidOut(ni + "cumene", 950)
                .fluidOut(mi + "benzene", 100)
                .save("epoxy/cumene");
        // 10. Air oxidation
        r.machine("chemical_reactor", 32, 300)
                .fluidIn(ni + "cumene", 950)
                .fluidIn(mi + "oxygen", 950)
                .fluidOut(ni + "cumene_hydroperoxide", 950)
                .save("epoxy/cumene_oxidation");
        // 11. Acid cleavage
        r.machine("chemical_reactor", 32, 240)
                .fluidIn(ni + "cumene_hydroperoxide", 950)
                .fluidIn(mi + "sulfuric_acid", 50)
                .fluidOut(ni + "phenol", 1150)
                .fluidOut(ni + "acetone", 550)
                .fluidOut(ni + "phenol_tar", 90)
                .save("epoxy/phenol_acetone_cleavage");
        // 12. Phenol tar recovery (ion exchange adsorption)
        r.machine("ion_exchange", 8, 120)
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1)
                .fluidIn(ni + "phenol_tar", 90)
                .fluidOut(ni + "phenol", 50)
                .save("epoxy/phenol_tar_recovery");

        // -- Bisphenol A --
        // 13. Condensation, catalyzed by sulfonated ion exchange resin (modern process)
        r.machine("chemical_reactor", 64, 300)
                .fluidIn(ni + "phenol", 2000)
                .fluidIn(ni + "acetone", 1000)
                .itemIn(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 1)
                .fluidOut(ni + "crude_bisphenol_a", 1300)
                .fluidOut("minecraft:water", 650)
                .save("epoxy/bisphenol_a_condensation");
        // 14. BPA purification (ion exchange)
        r.machine("ion_exchange", 16, 240)
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1)
                .fluidIn(ni + "crude_bisphenol_a", 1300)
                .fluidOut(ni + "bisphenol_a", 1200)
                .save("epoxy/bisphenol_a_purification");

        // -- DGEBA epoxy resin --
        // 15. Etherification with excess epichlorohydrin
        r.machine("chemical_reactor", 64, 300)
                .fluidIn(ni + "bisphenol_a", 1000)
                .fluidIn(ni + "epichlorohydrin", 2000)
                .fluidOut(ni + "chlorohydrin_ether", 1100)
                .save("epoxy/chlorohydrin_ether");
        // 16. Ring closure
        r.machine("chemical_reactor", 64, 300)
                .fluidIn(ni + "chlorohydrin_ether", 1100)
                .fluidIn(mi + "sodium_hydroxide", 1100)
                .fluidOut(ni + "crude_epoxy_resin", 1250)
                .fluidOut("minecraft:water", 550)
                .itemOut("modern_industrialization:salt_dust", 5)
                .save("epoxy/epoxy_ring_closure");
        // 17. Purification (ion exchange, ECH absorbed into stream)
        r.machine("ion_exchange", 32, 240)
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1)
                .fluidIn(ni + "crude_epoxy_resin", 1250)
                .fluidOut(ni + "epoxy_resin", 1200)
                .fluidOut(ni + "epichlorohydrin", 250)
                .save("epoxy/epoxy_purification");

        // -- Curing --
        // 18. Vacuum freezer: epoxy resin + titanium frame -> cured epoxy resin block
        r.machine("vacuum_freezer", 32, 400)
                .fluidIn(ni + "epoxy_resin", 1296)
                .itemIn(mi + "solid_titanium_machine_casing", 1)
                .itemOut(NIBlocks.CURED_EPOXY_RESIN_BLOCK_ITEM.getId().toString(), 1)
                .save("epoxy/cured_epoxy_resin_block");
        // 19. Cutting machine: cured block -> 16 epoxy plates
        r.machine("cutting_machine", 16, 300)
                .itemIn(NIBlocks.CURED_EPOXY_RESIN_BLOCK_ITEM.getId().toString(), 1)
                .fluidIn(mi + "lubricant", 233)
                .itemOut(NIItems.EPOXY_PLATE.getId().toString(), 16)
                .save("epoxy/epoxy_plate");

        // -- Fluoroantimonic acid (HF + SbF5), realistic synthesis --
        // 20. Antimony pentafluoride: 2 Sb + 5 F2 -> 2 SbF5
        r.machine("chemical_reactor", 64, 200)
                .tagIn("c:dusts/antimony", 2)
                .fluidIn(ni + "fluorine", 5000)
                .fluidOut(ni + "antimony_pentafluoride", 1000)
                .save("epoxy/antimony_pentafluoride");
        // 21. Fluoroantimonic acid: HF + SbF5 -> H2F[SbF6]
        r.machine("chemical_reactor", 64, 100)
                .fluidIn(ni + "hydrofluoric_acid", 1000)
                .fluidIn(ni + "antimony_pentafluoride", 1000)
                .fluidOut(ni + "fluoroantimonic_acid", 1000)
                .save("epoxy/fluoroantimonic_acid");

        // -- Crystal circuit board assembly (1M EU/t, 1000s) --
        r.machine("assembler", 1_000_000, 20_000)
                .itemIn(NIItems.EPOXY_PLATE.getId().toString(), 3)
                .itemIn(mi + "superconductor_wire", 48)
                .itemIn(mi + "plutonium_battery", 16)
                .itemIn(mi + "quantum_circuit_board", 1)
                .itemIn(NIMaterials.Materials.get("naquadah").id("plate"), 4)
                .fluidIn(mi + "helium_3", 100)
                .fluidIn(ni + "fluoroantimonic_acid", 10)
                .itemOut(ni + "crystal_circuit_board", 1)
                .save("electric_age/circuit/assembler/crystal_circuit_board");
    }

    private void nichromeCoilChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // Mica dust: silicon + aluminum + salt + oxygen
        r.machine("chemical_reactor", 8, 300)
                .tagIn("c:dusts/silicon", 4)
                .tagIn("c:dusts/aluminum", 1)
                .tagIn("c:dusts/salt", 1)
                .fluidIn(mi + "oxygen", 2000)
                .itemOut(NIItems.MICA_DUST.getId().toString(), 10)
                .save("mica/mica_dust");

        // Plastic mica mixture: mica dust + PVC
        r.machine("mixer", 8, 300)
                .itemIn(NIItems.MICA_DUST.getId().toString(), 1)
                .fluidIn(mi + "polyvinyl_chloride", 144)
                .itemOut(NIItems.PLASTIC_MICA_MIXTURE.getId().toString(), 1)
                .save("mica/plastic_mica_mixture");

        // Compress 9 mixtures into a plastic mica block
        r.machine("compressor", 2, 400)
                .itemIn(NIItems.PLASTIC_MICA_MIXTURE.getId().toString(), 9)
                .itemOut(NIBlocks.PLASTIC_MICA_BLOCK_ITEM.getId().toString(), 1)
                .save("mica/plastic_mica_block");

        // Cut the block into insulator sheets
        r.machine("cutting_machine", 2, 400)
                .itemIn(NIBlocks.PLASTIC_MICA_BLOCK_ITEM.getId().toString(), 1)
                .fluidIn(mi + "lubricant", 144)
                .itemOut(NIItems.MICA_INSULATOR_SHEET.getId().toString(), 9)
                .save("mica/insulator_sheet");

        // Nichrome coil assembly
        r.machine("assembler", 8, 200)
                .itemIn(NIMaterials.Materials.get("nichrome").cableId(), 16)
                .itemIn(NIItems.MICA_INSULATOR_SHEET.getId().toString(), 3)
                .itemIn(mi + "diode", 2)
                .itemOut(ni + "nichrome_coil", 1)
                .save("nichrome/coil");

        // Magma crucible: melt a glass block into liquid glass
        r.machine("magma_crucible", 8, 100)
                .itemIn("minecraft:glass", 1)
                .fluidOut(ni + "liquid_glass", 144)
                .save("magma_crucible/glass");
    }

    private void niquadahChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // Xenon: fusion of deuterium + helium-3
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(mi + "deuterium", 1000)
                .fluidIn(mi + "helium_3", 1000)
                .fluidOut(ni + "xenon", 100)
                .fluidOut(mi + "hydrogen", 500)
                .save("naquadah/xenon");

        // Nitric acid
        r.machine("chemical_reactor", 128_000, 400)
                .fluidIn(mi + "nitrogen", 1000)
                .fluidIn(mi + "oxygen", 2500)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "nitric_acid", 2000)
                .save("naquadah/nitric_acid");

        // Monazite nitric acid soak
        r.machine("mixer", 256_000, 600)
                .tagIn("c:dusts/monazite", 4)
                .fluidIn(ni + "nitric_acid", 2000)
                .itemOut(ni + "monazite_residue", 2)
                .fluidOut(ni + "monazite_heavy_residue_solution", 2000)
                .save("naquadah/monazite_soak");

        // Neutralize into residue oxide
        r.machine("chemical_reactor", 128_000, 400)
                .fluidIn(ni + "monazite_heavy_residue_solution", 1000)
                .fluidIn(mi + "sodium_hydroxide", 500)
                .itemOut(ni + "heavy_element_residue_oxide", 1)
                .save("naquadah/neutralize");

        // Hydrogen reduction
        r.machine("blast_furnace", 512, 2_000)
                .itemIn(ni + "heavy_element_residue_oxide", 1)
                .fluidIn(mi + "hydrogen", 500)
                .itemOut(ni + "heavy_element_residue_dust", 1)
                .save("naquadah/heavy_element_residue_dust");

        // Platinum-bearing roast
        r.machine("chemical_reactor", 256_000, 800)
                .itemIn(ni + "heavy_element_residue_dust", 1)
                .fluidIn(mi + "platinum_sulfuric_solution", 500)
                .itemOut(ni + "platinized_ultraheavy_residue_dust", 1)
                .save("naquadah/platinum_roast");

        // UU matter separation: platinum dust + liquid ultraheavy element mixture
        r.machine("mixer", 256_000, 800)
                .itemIn(ni + "platinized_ultraheavy_residue_dust", 1)
                .fluidIn(mi + "uu_matter", 200)
                .itemOut(mi + "platinum_dust", 1)
                .fluidOut(ni + "ultraheavy_element_mixture", 1000)
                .save("naquadah/uu_separation");

        // Chlorine fusion into inert naquadah solution
        r.machine("fusion_reactor", 16_000, 1_200)
                .fluidIn(ni + "ultraheavy_element_mixture", 1000)
                .fluidIn(mi + "chlorine", 1000)
                .fluidOut(ni + "inert_naquadah_solution", 1000)
                .fluidOut(mi + "argon", 250)
                .save("naquadah/inert_naquadah_solution");

        // Neutron source
        r.machine("assembler", 512_000, 1_200)
                .itemIn(mi + "he_mox_fuel_rod_quad", 1)
                .itemIn(mi + "cooling_cell", 8)
                .tagIn("c:plates/iridium", 32)
                .itemOut(NIItems.NEUTRON_SOURCE.getId().toString(), 32)
                .save("naquadah/neutron_source");

        // Neutron activation
        r.machine("mixer", 256_000, 600)
                .itemIn(NIItems.NEUTRON_SOURCE.getId().toString(), 1)
                .fluidIn(ni + "inert_naquadah_solution", 8000)
                .fluidIn(ni + "xenon_hexafluoride", 8000)
                .fluidOut(ni + "neutron_activated_naquadah_solution", 8000)
                .save("naquadah/neutron_activated_naquadah_solution");

        // Centrifuge into naquadah dust
        r.machine("centrifuge", 512_000, 1_000)
                .fluidIn(ni + "neutron_activated_naquadah_solution", 125)
                .itemOut(NIMaterials.Materials.get("naquadah").id("dust"), 1)
                .fluidOut(ni + "xenon", 20)
                .save("naquadah/naquadah_dust");

        // Fluorination chain (HF is OUR fluid: mi_nested_infinity:hydrofluoric_acid)
        r.machine("chemical_reactor", 128_000, 400)
                .fluidIn(ni + "xenon", 1000)
                .fluidIn(ni + "fluorine", 3000)
                .fluidOut(ni + "xenon_hexafluoride", 1000)
                .save("naquadah/xenon_hexafluoride");
        r.machine("electrolyzer", 128_000, 400)
                .fluidIn(ni + "hydrofluoric_acid", 2000)
                .fluidOut(ni + "fluorine", 1000)
                .fluidOut(mi + "hydrogen", 1000)
                .save("naquadah/fluorine");
        r.machine("chemical_reactor", 128_000, 400)
                .itemIn("minecraft:glowstone_dust", 2)
                .fluidIn(mi + "sulfuric_acid", 2000)
                .fluidOut(ni + "hydrofluoric_acid", 2000)
                .save("naquadah/hydrofluoric_acid");

        // High-purity monocrystalline naquadah (32 ingots + uranium triplatinum + cryofluid)
        r.machine("blast_furnace", 512, 4_000)
                .itemIn(NIMaterials.NAQUADAH.id("ingot"), 32)
                .itemIn(NIMaterials.URANIUM_TRIPLATINUM.id("ingot"), 3)
                .fluidIn(mi + "cryofluid", 1000)
                .itemOut(NIItems.HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH.getId().toString(), 21)
                .save("naquadah/high_purity_monocrystalline_naquadah");

        // Computing unit
        r.machine("assembler", 1_000_000, 12_000)
                .itemIn(mi + "random_access_memory", 8)
                .itemIn(mi + "arithmetic_logic_unit", 4)
                .itemIn(mi + "memory_management_unit", 2)
                .itemIn(NIItems.HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH.getId().toString(), 1)
                .itemIn(mi + "processing_unit", 1)
                .fluidIn(mi + "acrylic_glue", 37)
                .itemOut(NIItems.NAQUADAH_COMPUTING_UNIT.getId().toString(), 1)
                .save("naquadah/computing_unit");
    }

    private void uraniumTriplatinumChain(NIRecipes r) {
        r.machine("mixer", 1_000_000, 20_000)
                .tagIn("c:dusts/platinum", 3)
                .tagIn("c:dusts/uranium", 1)
                .itemOut(NIMaterials.URANIUM_TRIPLATINUM.id("dust"), 4)
                .save("uranium_triplatinum/blend");
    }

    private void nichromeChain(NIRecipes r) {
        // Nichrome blend: 4 nickel + 1 chromium -> 5 nichrome dust (267s @ 128k EU/t)
        r.machine("mixer", 128_000, 5_340)
                .tagIn("c:dusts/nickel", 4)
                .tagIn("c:dusts/chromium", 1)
                .itemOut(NIMaterials.Materials.get("nichrome").id("dust"), 5)
                .save("nichrome/blend");
    }

    private void circuitChain(NIRecipes r) {
        r.machine("assembler", 1_000_000, 20_000)
                .itemIn("modern_industrialization:quantum_circuit", 4)
                .itemIn(NIItems.NAQUADAH_COMPUTING_UNIT.getId().toString(), 1)
                .itemIn("modern_industrialization:cooling_cell", 8)
                .itemIn("mi_nested_infinity:crystal_circuit_board", 1)
                .itemOut("mi_nested_infinity:crystal_circuit", 1)
                .save("electric_age/circuit/assembler/crystal_circuit");
    }
}
