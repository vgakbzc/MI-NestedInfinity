package com.nestedinfinity.mod.datagen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.NIItems;
import com.nestedinfinity.mod.items.algae.NIAlgae;
import com.nestedinfinity.mod.items.algae.NIPetriDishes;
import com.nestedinfinity.mod.items.gems.NIGems;
import com.nestedinfinity.mod.material.NIMaterial;
import com.nestedinfinity.mod.material.NIMaterials;

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
        superconductorChain(r);
        siliconeChain(r);
        pbiChain(r);
        glueChain(r);
        bioCircuitChain(r);
        epoxyChain(r);
        circuitChain(r);
        resonantSeparationChain(r);
        resonantFusionChain(r);
        resonantPolyimideChain(r);
        resonantFluoroChain(r);
        resonantTuningChain(r);
        resonantCircuitChain(r);
        bioChain(r);
        cultivationRecipes(r);
        wildIsolation(r);
        opticalGemChain(r);
    }

    /**
     * Biochemistry program feeding the algae cultivator:
     * platform chemicals -> 16 amino-acid synthesis routes -> protein,
     * the real red-algae agar extraction route, and the nutrient agar medium
     * (protein + agar + slaked lime, following real nutrient-agar composition).
     */
    private void bioChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // -- chain catalysts (all craftable upstream): used recipes consume them with a
        //    small probability instead of returning them --
        // promoted iron (Fe + Al2O3 + CaO), the real Haber-Bosch catalyst
        r.machine("mixer", 16, 200)
                .tagIn("c:dusts/iron", 3)
                .tagIn("c:dusts/aluminum", 1)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1)
                .itemOut(NIItems.HABER_IRON_CATALYST.getId().toString(), 5)
                .save("bio/cat_haber_iron");
        // nickel on alumina, the steam-reforming catalyst
        r.machine("mixer", 16, 200)
                .tagIn("c:dusts/nickel", 3)
                .tagIn("c:dusts/aluminum", 2)
                .itemOut(NIItems.REFORMING_NICKEL_CATALYST.getId().toString(), 4)
                .save("bio/cat_reforming_nickel");
        // copper-based low-temperature methanol catalyst (Cu/ZnO/Al2O3 family)
        r.machine("mixer", 16, 200)
                .tagIn("c:dusts/copper", 4)
                .tagIn("c:dusts/aluminum", 1)
                .itemOut(NIItems.METHANOL_SYNTHESIS_CATALYST.getId().toString(), 4)
                .save("bio/cat_methanol");
        // woven silver gauze for the silver formaldehyde process
        r.machine("assembler", 16, 200)
                .itemIn(mi + "silver_wire", 4)
                .itemIn(mi + "silver_plate", 1)
                .itemOut(NIItems.SILVER_GAUZE_CATALYST.getId().toString(), 2)
                .save("bio/cat_silver_gauze");
        // platinum gauze for Andrussow HCN (the historic nets were pure platinum)
        r.machine("assembler", 8, 200)
                .itemIn(mi + "platinum_wire", 4)
                .itemIn(mi + "platinum_plate", 1)
                .itemOut(NIItems.PLATINUM_GAUZE_CATALYST.getId().toString(), 2)
                .save("bio/cat_platinum_gauze");
        // iridium carbonyl, precursor of the Cativa carbonylation system
        r.machine("chemical_reactor", 32, 200)
                .tagIn("c:dusts/iridium", 1)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .itemOut(NIItems.IRIDIUM_CARBONYLATION_CATALYST.getId().toString(), 2)
                .save("bio/cat_iridium_carbonyl");
        // platinum carbonyl hydride, the oxo catalyst (platinum standing in for rhodium)
        r.machine("chemical_reactor", 32, 200)
                .tagIn("c:dusts/platinum", 1)
                .fluidIn(ni + "carbon_monoxide", 1500)
                .fluidIn(mi + "hydrogen", 500)
                .itemOut(NIItems.HYDROFORMYLATION_CATALYST.getId().toString(), 2)
                .save("bio/cat_hydroformylation");
        // cupric chloride, the co-catalyst of the Wacker system
        r.machine("chemical_reactor", 16, 150)
                .tagIn("c:dusts/copper", 1)
                .fluidIn(mi + "chlorine", 1000)
                .itemOut(NIItems.COPPER_CHLORIDE_CATALYST.getId().toString(), 4)
                .save("bio/cat_copper_chloride");
        // enzymes immobilized on ion-exchange resin beads (grown on a sugar broth)
        r.machine("mixer", 16, 300)
                .itemIn(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 1)
                .itemIn("minecraft:sugar", 4)
                .fluidIn("minecraft:water", 250)
                .itemOut(NIItems.IMMOBILIZED_ENZYME.getId().toString(), 4)
                .save("bio/cat_immobilized_enzyme");
        // byproduct recycle: NH4Cl + Ca(OH)2 -> NH3 + CaCl2 (ammonia recovery)
        r.machine("chemical_reactor", 16, 200)
                .itemIn(NIItems.AMMONIUM_CHLORIDE.getId().toString(), 2)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1)
                .fluidOut(ni + "ammonia", 1000)
                .fluidOut("minecraft:water", 500)
                .itemOut(NIItems.CALCIUM_CHLORIDE.getId().toString(), 1)
                .save("bio/ammonium_chloride_recycle");

        // -- platform chemicals (catalysts are only consumed with a small probability,
        //    5-25% per craft depending on how precious they are) --
        // Haber-Bosch: N2 + 3 H2 -> 2 NH3
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "nitrogen", 1000)
                .fluidIn(mi + "hydrogen", 3000)
                .itemIn(NIItems.HABER_IRON_CATALYST.getId().toString(), 1, 0.15)
                .fluidOut(ni + "ammonia", 2000)
                .save("bio/ammonia");
        // steam reforming: CH4 + H2O -> CO + 3 H2
        r.machine("chemical_reactor", 64, 200)
                .fluidIn(mi + "methane", 1000)
                .fluidIn("minecraft:water", 1000)
                .itemIn(NIItems.REFORMING_NICKEL_CATALYST.getId().toString(), 1, 0.15)
                .fluidOut(ni + "carbon_monoxide", 1000)
                .fluidOut(mi + "hydrogen", 3000)
                .save("bio/carbon_monoxide");
        // methanol synthesis: CO + 2 H2 -> CH3OH
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .fluidIn(mi + "hydrogen", 2000)
                .itemIn(NIItems.METHANOL_SYNTHESIS_CATALYST.getId().toString(), 1, 0.12)
                .fluidOut(ni + "methanol", 1000)
                .save("bio/methanol");
        // formaldehyde (silver catalyst): CH3OH + 1/2 O2 -> HCHO + H2O
        r.machine("chemical_reactor", 16, 150)
                .fluidIn(ni + "methanol", 1000)
                .fluidIn(mi + "oxygen", 500)
                .itemIn(NIItems.SILVER_GAUZE_CATALYST.getId().toString(), 1, 0.08)
                .fluidOut(ni + "formaldehyde", 1000)
                .fluidOut("minecraft:water", 500)
                .save("bio/formaldehyde");
        // Andrussow: CH4 + NH3 + 1.5 O2 -> HCN + 3 H2O over the platinum gauze
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "methane", 250)
                .fluidIn(ni + "ammonia", 250)
                .fluidIn(mi + "oxygen", 375)
                .itemIn(NIItems.PLATINUM_GAUZE_CATALYST.getId().toString(), 1, 0.05)
                .fluidOut(ni + "hydrogen_cyanide", 250)
                .fluidOut("minecraft:water", 750)
                .save("bio/hydrogen_cyanide");
        // Cativa carbonylation: CH3OH + CO -> CH3COOH
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(ni + "methanol", 1000)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .itemIn(NIItems.IRIDIUM_CARBONYLATION_CATALYST.getId().toString(), 1, 0.05)
                .fluidOut(ni + "acetic_acid", 1000)
                .save("bio/acetic_acid");
        // Wacker oxidation: C2H4 + 1/2 O2 -> CH3CHO
        r.machine("chemical_reactor", 16, 150)
                .fluidIn(mi + "ethylene", 1000)
                .fluidIn(mi + "oxygen", 500)
                .itemIn(NIItems.COPPER_CHLORIDE_CATALYST.getId().toString(), 1, 0.20)
                .fluidOut(ni + "acetaldehyde", 1000)
                .save("bio/acetaldehyde");
        // hydroformylation: C3H6 + CO + H2 -> isobutyraldehyde
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "propene", 1000)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .fluidIn(mi + "hydrogen", 1000)
                .itemIn(NIItems.HYDROFORMYLATION_CATALYST.getId().toString(), 1, 0.08)
                .fluidOut(ni + "isobutyraldehyde", 1000)
                .save("bio/isobutyraldehyde");
        // one-carbon homologation: isobutyraldehyde + HCHO + H2 -> isovaleraldehyde
        // (base-catalyzed aldol condensation over cheap slaked lime)
        r.machine("chemical_reactor", 32, 250)
                .fluidIn(ni + "isobutyraldehyde", 1000)
                .fluidIn(ni + "formaldehyde", 1000)
                .fluidIn(mi + "hydrogen", 1000)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1, 0.25)
                .fluidOut(ni + "isovaleraldehyde", 1000)
                .fluidOut("minecraft:water", 500)
                .save("bio/isovaleraldehyde");
        // tandem ethylene dimerization + hydroformylation -> 2-methylbutanal
        r.machine("chemical_reactor", 64, 250)
                .fluidIn(mi + "ethylene", 2000)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .fluidIn(mi + "hydrogen", 1000)
                .itemIn(NIItems.HYDROFORMYLATION_CATALYST.getId().toString(), 1, 0.08)
                .fluidOut(ni + "methyl_butanal", 1000)
                .save("bio/methyl_butanal");
        // anti-Markov oxidation of styrene -> phenylacetaldehyde
        // (epoxidation over silver, then oxide rearrangement)
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "styrene", 1000)
                .fluidIn(mi + "oxygen", 500)
                .itemIn(NIItems.SILVER_GAUZE_CATALYST.getId().toString(), 1, 0.08)
                .fluidOut(ni + "phenylacetaldehyde", 1000)
                .save("bio/phenylacetaldehyde");
        // formose reaction: 2 HCHO -> glycolaldehyde (real catalyst: calcium hydroxide)
        r.machine("chemical_reactor", 16, 200)
                .fluidIn(ni + "formaldehyde", 2000)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1, 0.25)
                .itemOut(NIItems.GLYCOLALDEHYDE.getId().toString(), 1)
                .save("bio/glycolaldehyde");
        // cyanamide: HCN + NH3 -> H2NCN + H2 (gas-phase route)
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(ni + "hydrogen_cyanide", 1000)
                .fluidIn(ni + "ammonia", 1000)
                .itemOut(NIItems.CYANAMIDE.getId().toString(), 2)
                .fluidOut(mi + "hydrogen", 500)
                .save("bio/cyanamide");

        // -- lime: calcination then slaking --
        r.machine("chemical_reactor", 16, 200)
                .itemIn("minecraft:calcite", 1)
                .itemOut(NIBlocks.QUICKLIME_ITEM.getId().toString(), 1)
                .save("bio/quicklime");
        r.machine("mixer", 8, 100)
                .itemIn(NIBlocks.QUICKLIME_ITEM.getId().toString(), 1)
                .fluidIn("minecraft:water", 125)
                .itemOut(NIItems.SLAKED_LIME.getId().toString(), 2)
                .save("bio/slaked_lime");

        // -- agar: the real Gelidium/Gracilaria extraction route, step by step --
        // 0a. bootstrap: vanilla kelp extracted into low-purity algae (the strain fluids
        //     have no source until the algae program is running, so the agar chain needs
        //     an entry raw material everyone can reach)
        r.machine("chemical_reactor", 16, 200)
                .itemIn("minecraft:kelp", 4)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.LOW_PURITY_ALGAE.getId().toString(), 2)
                .save("bio/low_purity_algae");
        // 0b. initial washing substitute: the crude extract carries sand and sea salts,
        //     so the yield is half of the pure-culture washing below
        r.machine("chemical_reactor", 8, 160)
                .itemIn(NIItems.LOW_PURITY_ALGAE.getId().toString(), 2)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.WET_RHODOPHYTA.getId().toString(), 1)
                .itemOut(mi + "salt_dust", 1)
                .save("bio/agar_washing_crude");
        // 1. washing the raw weed (sand and sea salts out)
        r.machine("chemical_reactor", 8, 160)
                .fluidIn(ni + "rhodophyta", 1000)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.WET_RHODOPHYTA.getId().toString(), 2)
                .itemOut(mi + "salt_dust", 1)
                .save("bio/agar_washing");
        // 2. alkaline pretreatment (de-sulfation, ~90 deg C, raises gel strength);
        //    the cleaved sulfate leaves as sodium sulfate
        r.machine("chemical_reactor", 64, 600)
                .itemIn(NIItems.WET_RHODOPHYTA.getId().toString(), 2)
                .fluidIn(mi + "sodium_hydroxide", 500)
                .itemOut(NIItems.DESULFATED_RHODOPHYTA.getId().toString(), 2)
                .itemOut(NIItems.SODIUM_SULFATE.getId().toString(), 1)
                .save("bio/agar_alkali_pretreatment");
        // 3. washing to neutral pH
        r.machine("mixer", 8, 160)
                .itemIn(NIItems.DESULFATED_RHODOPHYTA.getId().toString(), 2)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.WASHED_RHODOPHYTA.getId().toString(), 2)
                .fluidOut("minecraft:water", 500)
                .save("bio/agar_neutral_wash");
        // 4. bleaching
        r.machine("chemical_reactor", 16, 200)
                .itemIn(NIItems.WASHED_RHODOPHYTA.getId().toString(), 2)
                .fluidIn(ni + "hypochlorous_acid", 250)
                .itemOut(NIItems.BLEACHED_RHODOPHYTA.getId().toString(), 2)
                .save("bio/agar_bleaching");
        // 5. hot water extraction of the agar (95-100 deg C)
        r.machine("chemical_reactor", 64, 400)
                .itemIn(NIItems.BLEACHED_RHODOPHYTA.getId().toString(), 2)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "crude_agar_solution", 1000)
                .itemOut(NIItems.RHODOPHYTA_RESIDUE.getId().toString(), 1)
                .save("bio/agar_hot_extraction");
        // 6. hot filtration (centrifuge separation of suspended debris)
        r.machine("centrifuge", 16, 200)
                .fluidIn(ni + "crude_agar_solution", 1000)
                .fluidOut(ni + "clarified_agar_solution", 900)
                .itemOut(NIItems.RHODOPHYTA_RESIDUE.getId().toString(), 1)
                .save("bio/agar_hot_filtration");
        // 7. freeze-gelation (freezing/thawing purifies the gel)
        r.machine("vacuum_freezer", 32, 400)
                .fluidIn(ni + "clarified_agar_solution", 1000)
                .itemOut(NIItems.FROZEN_AGAR_GEL.getId().toString(), 1)
                .save("bio/agar_freeze_gelation");
        // 8. thawing and washing out soluble impurities
        r.machine("chemical_reactor", 8, 200)
                .itemIn(NIItems.FROZEN_AGAR_GEL.getId().toString(), 1)
                .fluidIn("minecraft:water", 250)
                .itemOut(NIItems.AGAR_GEL.getId().toString(), 1)
                .save("bio/agar_thaw_wash");
        // 9. drying into agar flakes
        r.machine("chemical_reactor", 8, 300)
                .itemIn(NIItems.AGAR_GEL.getId().toString(), 2)
                .itemOut(NIItems.AGAR.getId().toString(), 1)
                .save("bio/agar_drying");

        // -- the 16 most common amino acids, one synthesis route each --
        // Glycine: chloroacetic acid + 2 NH3 -> glycine + NH4Cl (ammonolysis).
        // Chloroacetic acid itself: acetic acid + Cl2 -> ClCH2COOH + HCl.
        r.machine("chemical_reactor", 16, 200)
                .fluidIn(ni + "acetic_acid", 1000)
                .fluidIn(mi + "chlorine", 1000)
                .itemOut(NIItems.CHLOROACETIC_ACID.getId().toString(), 1)
                .fluidOut(mi + "hydrochloric_acid", 1000)
                .save("bio/chloroacetic_acid");
        r.machine("chemical_reactor", 16, 200)
                .itemIn(NIItems.CHLOROACETIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 2000)
                .itemOut(NIItems.GLYCINE.getId().toString(), 1)
                .itemOut(NIItems.AMMONIUM_CHLORIDE.getId().toString(), 1)
                .save("bio/glycine");
        // Alanine / valine / leucine / isoleucine / serine: two-step Strecker synthesis.
        // Step 1: aldehyde + NH3 + HCN -> alpha-aminonitrile.
        // Step 2: acid hydrolysis -> amino acid + ammonium chloride (the byproduct,
        //         recycled to ammonia by the lime recipe above).
        streckerRoute(r, ni + "acetaldehyde", "alanine",
                NIItems.ALANINE_AMINONITRILE, NIItems.ALANINE);
        streckerRoute(r, ni + "isobutyraldehyde", "valine",
                NIItems.VALINE_AMINONITRILE, NIItems.VALINE);
        streckerRoute(r, ni + "isovaleraldehyde", "leucine",
                NIItems.LEUCINE_AMINONITRILE, NIItems.LEUCINE);
        streckerRoute(r, ni + "methyl_butanal", "isoleucine",
                NIItems.ISOLEUCINE_AMINONITRILE, NIItems.ISOLEUCINE);
        // phenylalanine via Strecker of phenylacetaldehyde (itself from styrene oxidation)
        streckerRoute(r, ni + "phenylacetaldehyde", "phenylalanine",
                NIItems.PHENYLALANINE_AMINONITRILE, NIItems.PHENYLALANINE);
        // serine via Strecker of glycolaldehyde (formose reaction from formaldehyde)
        r.machine("chemical_reactor", 32, 200)
                .itemIn(NIItems.GLYCOLALDEHYDE.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 1000)
                .fluidIn(ni + "hydrogen_cyanide", 1000)
                .itemOut(NIItems.SERINE_AMINONITRILE.getId().toString(), 2)
                .save("bio/serine_aminonitrile");
        r.machine("chemical_reactor", 32, 240)
                .itemIn(NIItems.SERINE_AMINONITRILE.getId().toString(), 2)
                .fluidIn(mi + "hydrochloric_acid", 2000)
                .fluidIn("minecraft:water", 2000)
                .itemOut(NIItems.SERINE.getId().toString(), 2)
                .itemOut(NIItems.AMMONIUM_CHLORIDE.getId().toString(), 2)
                .save("bio/serine");
        // Threonine: threonine aldolase route — glycine + acetaldehyde condense,
        // the immobilized enzyme is returned.
        r.machine("chemical_reactor", 32, 240)
                .itemIn(NIItems.GLYCINE.getId().toString(), 1)
                .fluidIn(ni + "acetaldehyde", 500)
                .itemIn(NIItems.IMMOBILIZED_ENZYME.getId().toString(), 1, 0.10)
                .itemOut(NIItems.THREONINE.getId().toString(), 1)
                .save("bio/threonine");
        // Aspartic acid: amination of fumaric acid (aspartase route, immobilized enzyme returned).
        // Fumaric acid via benzene oxidation to maleic anhydride, hydrolysis, isomerization.
        r.machine("chemical_reactor", 64, 300)
                .fluidIn(mi + "benzene", 1000)
                .fluidIn(mi + "oxygen", 2250)
                .itemOut(NIItems.MALEIC_ANHYDRIDE.getId().toString(), 2)
                .fluidOut("minecraft:water", 500)
                .save("bio/maleic_anhydride");
        r.machine("chemical_reactor", 16, 150)
                .itemIn(NIItems.MALEIC_ANHYDRIDE.getId().toString(), 1)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.FUMARIC_ACID.getId().toString(), 1)
                .save("bio/fumaric_acid");
        r.machine("chemical_reactor", 32, 200)
                .itemIn(NIItems.FUMARIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 500)
                .fluidIn("minecraft:water", 500)
                .itemIn(NIItems.IMMOBILIZED_ENZYME.getId().toString(), 1, 0.10)
                .itemOut(NIItems.ASPARTIC_ACID.getId().toString(), 1)
                .save("bio/aspartic_acid");
        // Glutamic acid: reductive amination of alpha-ketoglutaric acid (TCA intermediate,
        // built by carboxylation of fumaric acid).
        r.machine("chemical_reactor", 64, 250)
                .itemIn(NIItems.FUMARIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "carbon_monoxide", 1000)
                .fluidIn("minecraft:water", 500)
                .itemOut(NIItems.ALPHA_KETOGLUTARIC_ACID.getId().toString(), 1)
                .save("bio/alpha_ketoglutaric_acid");
        r.machine("chemical_reactor", 32, 200)
                .itemIn(NIItems.ALPHA_KETOGLUTARIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 500)
                .fluidIn(mi + "hydrogen", 500)
                .itemOut(NIItems.GLUTAMIC_ACID.getId().toString(), 1)
                .save("bio/glutamic_acid");
        // Lysine: caprolactam hydrolysis to epsilon-aminocaproic acid, then alpha-amination.
        r.machine("chemical_reactor", 32, 250)
                .fluidIn(mi + "caprolactam", 1000)
                .fluidIn("minecraft:water", 1000)
                .itemOut(NIItems.EPSILON_AMINOCAPROIC_ACID.getId().toString(), 2)
                .save("bio/epsilon_aminocaproic_acid");
        r.machine("chemical_reactor", 64, 300)
                .itemIn(NIItems.EPSILON_AMINOCAPROIC_ACID.getId().toString(), 2)
                .fluidIn(ni + "ammonia", 1000)
                .itemOut(NIItems.LYSINE.getId().toString(), 1)
                .save("bio/lysine");
        // Ornithine: reduction of glutamic acid side chain (mimics the Glu -> Orn route).
        r.machine("chemical_reactor", 32, 250)
                .itemIn(NIItems.GLUTAMIC_ACID.getId().toString(), 1)
                .fluidIn(mi + "hydrogen", 1000)
                .itemOut(NIItems.ORNITHINE.getId().toString(), 1)
                .save("bio/ornithine");
        // Arginine: ornithine + cyanamide (real carbamoylation route).
        r.machine("chemical_reactor", 32, 200)
                .itemIn(NIItems.ORNITHINE.getId().toString(), 1)
                .itemIn(NIItems.CYANAMIDE.getId().toString(), 1)
                .itemOut(NIItems.ARGININE.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("bio/arginine");
        // Proline: cyclizing reduction of glutamic acid.
        r.machine("chemical_reactor", 32, 250)
                .itemIn(NIItems.GLUTAMIC_ACID.getId().toString(), 1)
                .fluidIn(mi + "hydrogen", 2000)
                .itemOut(NIItems.PROLINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("bio/proline");
        // Tyrosine: ring hydroxylation of phenylalanine.
        r.machine("chemical_reactor", 32, 200)
                .itemIn(NIItems.PHENYLALANINE.getId().toString(), 1)
                .fluidIn(mi + "oxygen", 250)
                .itemOut(NIItems.TYROSINE.getId().toString(), 1)
                .save("bio/tyrosine");
        // Asparagine / glutamine: amidation of the parent acid.
        r.machine("chemical_reactor", 16, 200)
                .itemIn(NIItems.ASPARTIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 500)
                .itemOut(NIItems.ASPARAGINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("bio/asparagine");
        r.machine("chemical_reactor", 16, 200)
                .itemIn(NIItems.GLUTAMIC_ACID.getId().toString(), 1)
                .fluidIn(ni + "ammonia", 500)
                .itemOut(NIItems.GLUTAMINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("bio/glutamine");

        // -- protein: peptide condensation of exactly one of each amino acid --
        r.tag(ni + "amino_acids",
                NIItems.GLYCINE.getId().toString(), NIItems.ALANINE.getId().toString(),
                NIItems.VALINE.getId().toString(), NIItems.LEUCINE.getId().toString(),
                NIItems.ISOLEUCINE.getId().toString(), NIItems.SERINE.getId().toString(),
                NIItems.THREONINE.getId().toString(), NIItems.ASPARTIC_ACID.getId().toString(),
                NIItems.GLUTAMIC_ACID.getId().toString(), NIItems.LYSINE.getId().toString(),
                NIItems.ARGININE.getId().toString(), NIItems.PROLINE.getId().toString(),
                NIItems.PHENYLALANINE.getId().toString(), NIItems.TYROSINE.getId().toString(),
                NIItems.ASPARAGINE.getId().toString(), NIItems.GLUTAMINE.getId().toString());
        // 1 GRF/t for 325 s in the 16-slot super mixer; each peptide bond
        // releases one water of condensation as the byproduct
        r.machine("super_mixer", 1_000_000_000, 6500)
                .itemIn(NIItems.GLYCINE.getId().toString(), 1)
                .itemIn(NIItems.ALANINE.getId().toString(), 1)
                .itemIn(NIItems.VALINE.getId().toString(), 1)
                .itemIn(NIItems.LEUCINE.getId().toString(), 1)
                .itemIn(NIItems.ISOLEUCINE.getId().toString(), 1)
                .itemIn(NIItems.SERINE.getId().toString(), 1)
                .itemIn(NIItems.THREONINE.getId().toString(), 1)
                .itemIn(NIItems.ASPARTIC_ACID.getId().toString(), 1)
                .itemIn(NIItems.GLUTAMIC_ACID.getId().toString(), 1)
                .itemIn(NIItems.LYSINE.getId().toString(), 1)
                .itemIn(NIItems.ARGININE.getId().toString(), 1)
                .itemIn(NIItems.PROLINE.getId().toString(), 1)
                .itemIn(NIItems.PHENYLALANINE.getId().toString(), 1)
                .itemIn(NIItems.TYROSINE.getId().toString(), 1)
                .itemIn(NIItems.ASPARAGINE.getId().toString(), 1)
                .itemIn(NIItems.GLUTAMINE.getId().toString(), 1)
                .itemOut(NIItems.PROTEIN.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("bio/protein");

        // -- nutrient agar medium: protein + agar + slaked lime + water --
        r.machine("mixer", 16, 200)
                .itemIn(NIItems.PROTEIN.getId().toString(), 1)
                .itemIn(NIItems.AGAR.getId().toString(), 1)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1)
                .fluidIn("minecraft:water", 750)
                .fluidOut(ni + "nutrient_agar", 1000)
                .save("bio/nutrient_agar");
    }

    /**
     * Two-step Strecker synthesis: aldehyde + NH3 + HCN -> alpha-aminonitrile,
     * then HCl hydrolysis -> amino acid + ammonium chloride (recycled to ammonia).
     */
    private void streckerRoute(NIRecipes r, String aldehyde, String path,
            net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.Item> nitrile,
            net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.Item> acid) {
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(aldehyde, 1000)
                .fluidIn("mi_nested_infinity:ammonia", 1000)
                .fluidIn("mi_nested_infinity:hydrogen_cyanide", 1000)
                .itemOut(nitrile.getId().toString(), 2)
                .save("bio/" + path + "_aminonitrile");
        r.machine("chemical_reactor", 32, 240)
                .itemIn(nitrile.getId().toString(), 2)
                .fluidIn("modern_industrialization:hydrochloric_acid", 2000)
                .fluidIn("minecraft:water", 2000)
                .itemOut(acid.getId().toString(), 2)
                .itemOut(NIItems.AMMONIUM_CHLORIDE.getId().toString(), 2)
                .save("bio/" + path);
    }

    /**
     * Algae cultivator: one recipe per unordered pair of distinct petri dishes. The medium
     * is 50mB of nutrient agar; each finished craft draws TWO dishes independently from the
     * recipe's probability pool (see CrafterComponentMixin), the outputs recombining the two
     * strain sets by XOR and resolving wheel conflicts to a stable dish (see {@link #outcomes}).
     * Identical inputs XOR to the empty set, so those pairs have no recipe.
     */
    private void cultivationRecipes(NIRecipes r) {
        String ni = "mi_nested_infinity:";

        Map<Integer, String> dishByMask = new HashMap<>();
        for (NIPetriDishes.PetriDish dish : NIPetriDishes.ALL) {
            int mask = 0;
            for (NIAlgae alga : dish.algae()) {
                mask |= 1 << alga.ordinal();
            }
            dishByMask.put(mask, dish.item().getId().toString());
        }

        for (int i = 0; i < NIPetriDishes.ALL.size(); i++) {
            NIPetriDishes.PetriDish first = NIPetriDishes.ALL.get(i);
            for (int j = i + 1; j < NIPetriDishes.ALL.size(); j++) {
                NIPetriDishes.PetriDish second = NIPetriDishes.ALL.get(j);
                NIRecipes.MachineBuilder builder = r.machine("algae_cultivator", 8, 200)
                        .itemIn(first.item().getId().toString(), 1)
                        .itemIn(second.item().getId().toString(), 1)
                        .fluidIn(ni + "nutrient_agar", 50);
                var outcomes = outcomes(dishMask(first) ^ dishMask(second));
                if (outcomes.size() == 1) {
                    // both slots deterministically produce the same dish
                    var entry = outcomes.entrySet().iterator().next();
                    builder.itemOut(dishId(dishByMask, entry.getKey()), 2);
                } else {
                    for (var entry : outcomes.entrySet()) {
                        builder.itemOut(dishId(dishByMask, entry.getKey()), 1, entry.getValue());
                    }
                }
                builder.save("cultivation/" + NIPetriDishes.word(first.algae())
                        + "__" + NIPetriDishes.word(second.algae()));
            }
        }
    }

    /**
     * Wild isolation, the seed recipe of the whole algae program: dirt stirred into
     * an agar plate sometimes carries a wild culture. Candidates are exactly the
     * {@link NIPetriDishes#WILD_ISOLATES} — two-strain dishes whose colors sit 3 or
     * 5 steps apart on the wheel — and one craft yields at most ONE dish (single-draw
     * semantics, see CrafterComponentMixin): 1% per candidate = 24% overall, uniform.
     */
    private void wildIsolation(NIRecipes r) {
        NIRecipes.MachineBuilder builder = r.machine("chemical_reactor", 16, 200)
                .itemIn("minecraft:dirt", 1)
                .itemIn(NIItems.AGAR.getId().toString(), 1);
        for (NIPetriDishes.PetriDish dish : NIPetriDishes.WILD_ISOLATES) {
            builder.itemOut(dish.item().getId().toString(), 1, 0.01);
        }
        builder.save("cultivation/wild_isolation");
    }

    private static int dishMask(NIPetriDishes.PetriDish dish) {
        int mask = 0;
        for (NIAlgae alga : dish.algae()) {
            mask |= 1 << alga.ordinal();
        }
        return mask;
    }

    private static String dishId(Map<Integer, String> dishByMask, int mask) {
        String id = dishByMask.get(mask);
        if (id == null) {
            throw new IllegalStateException("Cultivation outcome is not a registered dish: " + mask);
        }
        return id;
    }

    /** Memoized outcome distribution of ONE product slot for a strain-set XOR, by wheel mask. */
    private static final Map<Integer, Map<Integer, Double>> OUTCOME_CACHE = new HashMap<>();

    private static Map<Integer, Double> outcomes(int mask) {
        Map<Integer, Double> cached = OUTCOME_CACHE.get(mask);
        if (cached != null) {
            return cached;
        }
        Map<Integer, Double> result;
        int adjacent = lowestPair(mask, 1);
        if (adjacent >= 0) {
            // Rule 1: adjacent strains — the slot keeps one of the pair at random (50/50)
            int base = mask & ~(1 << adjacent) & ~(1 << ((adjacent + 1) % NIAlgae.WHEEL_SIZE));
            result = mix(outcomes(base | (1 << adjacent)), 0.5, outcomes(base | (1 << ((adjacent + 1) % NIAlgae.WHEEL_SIZE))), 0.5);
        } else {
            int near = lowestPair(mask, 2);
            if (near >= 0) {
                // Rule 2: distance-2 strains — collapse onto the middle strain
                int other = (near + 2) % NIAlgae.WHEEL_SIZE;
                int collapsed = (mask & ~(1 << near) & ~(1 << other)) | (1 << ((near + 1) % NIAlgae.WHEEL_SIZE));
                result = outcomes(collapsed);
            } else {
                result = Map.of(mask, 1.0);
            }
        }
        OUTCOME_CACHE.put(mask, Map.copyOf(result));
        return result;
    }

    /** Lowest wheel index i with both i and (i + gap) mod wheel present in the mask, or -1. */
    private static int lowestPair(int mask, int gap) {
        for (int i = 0; i < NIAlgae.WHEEL_SIZE; i++) {
            if ((mask & (1 << i)) != 0 && (mask & (1 << ((i + gap) % NIAlgae.WHEEL_SIZE))) != 0) {
                return i;
            }
        }
        return -1;
    }

    private static Map<Integer, Double> mix(Map<Integer, Double> a, double pa,
            Map<Integer, Double> b, double pb) {
        Map<Integer, Double> out = new TreeMap<>();
        a.forEach((k, v) -> out.merge(k, v * pa, Double::sum));
        b.forEach((k, v) -> out.merge(k, v * pb, Double::sum));
        return out;
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
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1, 0.10)
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
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1, 0.10)
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
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1, 0.10)
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
                .itemIn(NIItems.ION_EXCHANGE_CATALYST.getId().toString(), 1, 0.10)
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

    /**
     * Advanced superconductor cable program: a mercury-barium-titanium-copper oxide
     * (HgBaTiCuO) cuprate fired in the electric blast furnace on the TPV coil tier,
     * drawn into substrate wire, and assembled — together with a TPV coil tier built
     * like the nichrome coil — into a 2^33 EU/t cable four times MI's own
     * superconductor cable.
     */
    private void superconductorChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        NIMaterial tpv = NIMaterials.Materials.get("tpv");

        // -- vanadium: salt-roast of the titanium stream (how V is really recovered),
        //    then aluminothermic reduction of the sodium vanadate --
        r.machine("chemical_reactor", 128_000, 400)
                .tagIn("c:dusts/titanium", 3)
                .tagIn("c:dusts/salt", 2)
                .fluidIn(mi + "oxygen", 3000)
                .itemOut(NIItems.SODIUM_VANADATE.getId().toString(), 2)
                .save("superconductor/sodium_vanadate");
        r.machine("chemical_reactor", 128_000, 400)
                .itemIn(NIItems.SODIUM_VANADATE.getId().toString(), 2)
                .tagIn("c:dusts/aluminum", 3)
                .itemOut(NIItems.VANADIUM_DUST.getId().toString(), 2)
                .save("superconductor/vanadium");

        // -- TPV (titanium-platinum-vanadium) alloy, blended like nichrome --
        r.machine("mixer", 512_000, 8_000)
                .tagIn("c:dusts/titanium", 2)
                .tagIn("c:dusts/platinum", 1)
                .itemIn(NIItems.VANADIUM_DUST.getId().toString(), 1)
                .itemOut(tpv.id("dust"), 4)
                .save("superconductor/tpv_blend");

        // -- TPV cable: the standard wire+casing assembly, plus a large pump backing
        //    the cryo loop (25% chance to be worn out per cable); the auto-generated
        //    assembler/cable recipe is canceled in NIMaterials --
        r.machine("assembler", 8, 200)
                .itemIn(tpv.id("wire"), 1)
                .itemIn(NIItems.MICA_INSULATOR_SHEET.getId().toString(), 1)
                .itemIn(mi + "large_pump", 1, 0.25)
                .fluidIn(mi + "styrene_butadiene_rubber", 12)
                .fluidIn(ni + "liquid_glass", 72)
                .itemOut(tpv.cableId(), 1)
                .save("superconductor/tpv_cable");

        // -- TPV coil: built like the nichrome coil but insulated with silicone-
        //    rubber-bonded mica and controlled by a quantum circuit --
        r.machine("assembler", 8, 200)
                .itemIn(tpv.cableId(), 16)
                .itemIn(NIItems.SILICONE_MICA_INSULATOR_SHEET.getId().toString(), 6)
                .itemIn(mi + "diode", 8)
                .itemIn(mi + "quantum_circuit", 1)
                .itemOut(ni + "tpv_coil", 1)
                .save("superconductor/tpv_coil");

        // -- heavy-mineral fractionation of the ultraheavy element mixture:
        //    cinnabar (HgS) and barite (BaSO4) are exactly the dense minerals a
        //    centrifuge would concentrate --
        r.machine("centrifuge", 512_000, 600)
                .fluidIn(ni + "ultraheavy_element_mixture", 1000)
                .itemOut(NIItems.CINNABAR_DUST.getId().toString(), 1)
                .itemOut(NIItems.BARITE_DUST.getId().toString(), 1)
                .save("superconductor/heavy_minerals");

        // -- mercury(II) oxide: roasting cinnabar in air (HgS + O2 -> HgO + SO2) --
        r.machine("chemical_reactor", 128_000, 300)
                .itemIn(NIItems.CINNABAR_DUST.getId().toString(), 2)
                .fluidIn(mi + "oxygen", 1500)
                .itemOut(NIItems.MERCURY_OXIDE_DUST.getId().toString(), 2)
                .fluidOut(ni + "sulfur_dioxide", 1000)
                .save("superconductor/mercury_oxide");

        // -- barium oxide: thermal decomposition of barite (BaSO4 -> BaO + SO2) --
        r.machine("chemical_reactor", 128_000, 400)
                .itemIn(NIItems.BARITE_DUST.getId().toString(), 1)
                .fluidIn(mi + "oxygen", 1000)
                .itemOut(NIItems.BARIUM_OXIDE_DUST.getId().toString(), 1)
                .fluidOut(ni + "sulfur_dioxide", 1000)
                .save("superconductor/barium_oxide");

        // -- cuprate precursor: coprecipitated and mixed oxide powders, the way real
        //    mercury cuprates are prepared --
        r.machine("mixer", 512_000, 400)
                .itemIn(NIItems.MERCURY_OXIDE_DUST.getId().toString(), 1)
                .itemIn(NIItems.BARIUM_OXIDE_DUST.getId().toString(), 1)
                .tagIn("c:dusts/titanium", 1)
                .tagIn("c:dusts/copper", 1)
                .fluidIn(mi + "oxygen", 500)
                .itemOut(NIItems.MERCURY_BARIUM_TITANIUM_COPPER_OXIDE.getId().toString(), 4)
                .save("superconductor/hbtco_mix");

        // -- EBF firing on the TPV coil tier (recipe eu 4096 = tpv coil; lower coils
        //    are locked out by MI's coil-tier gate): oxygen-atmosphere sintering into
        //    the superconductor substrate --
        r.machine("blast_furnace", (int) com.nestedinfinity.mod.blocks.NICoils.TIERS.get(1).eu(), 600)
                .itemIn(NIItems.MERCURY_BARIUM_TITANIUM_COPPER_OXIDE.getId().toString(), 1)
                .fluidIn(mi + "oxygen", 250)
                .itemOut(NIItems.SUPERCONDUCTOR_SUBSTRATE.getId().toString(), 1)
                .save("superconductor/substrate");

        // -- drawn into substrate wire --
        r.machine("wiremill", 128_000, 300)
                .itemIn(NIItems.SUPERCONDUCTOR_SUBSTRATE.getId().toString(), 1)
                .itemOut(NIItems.SUPERCONDUCTOR_SUBSTRATE_WIRE.getId().toString(), 2)
                .save("superconductor/substrate_wire");

        // -- advanced rubber: vulcanized, carbon-black reinforced SBR --
        r.machine("chemical_reactor", 128_000, 300)
                .fluidIn(mi + "styrene_butadiene_rubber", 1000)
                .tagIn("c:dusts/sulfur", 1)
                .tagIn("c:dusts/coal", 1)
                .fluidOut(ni + "advanced_rubber", 1000)
                .save("superconductor/advanced_rubber");

        // -- the cable itself: 1M EU/t for 300 s in the assembler. Liquid helium
        //    quenches the cuprate, the advanced pump circulates it (and a plain
        //    large pump backs the loop, 25% chance to be worn out per cable), and
        //    a vanilla MI superconductor seeds the lattice --
        r.machine("assembler", 1_000_000, 6_000)
                .itemIn(NIItems.SUPERCONDUCTOR_SUBSTRATE_WIRE.getId().toString(), 4)
                .itemIn(NIItems.MICA_INSULATOR_SHEET.getId().toString(), 2)
                .itemIn(mi + "large_advanced_pump", 1)
                .itemIn(mi + "large_pump", 1, 0.25)
                .itemIn(mi + "superconductor_ingot", 1)
                .fluidIn(mi + "helium", 100)
                .fluidIn(ni + "advanced_rubber", 72)
                .itemOut(NIMaterials.Materials.get("advanced_superconductor").cableId(), 1)
                .save("superconductor/advanced_superconductor_cable");
    }

    /**
     * Silicone rubber program, following the real organosilicon route:
     * methanol + HCl -> chloromethane; Müller-Rochow direct process
     * (silicon + chloromethane over a copper catalyst) -> dimethyldichlorosilane;
     * hydrolysis/condensation -> liquid silicone rubber (PDMS), releasing HCl that
     * loops back into chloromethane production; peroxide/sulfur cure -> block;
     * cut into sheets; sheets laminate mica into the TPV coil's insulation.
     */
    private void siliconeChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // -- chloromethane: hydrochlorination of methanol (CH3OH + HCl -> CH3Cl + H2O) --
        r.machine("chemical_reactor", 32_000, 200)
                .fluidIn(ni + "methanol", 1000)
                .fluidIn(mi + "hydrochloric_acid", 1000)
                .fluidOut(ni + "chloromethane", 1000)
                .fluidOut("minecraft:water", 1000)
                .save("silicone/chloromethane");

        // -- Müller-Rochow direct process: silicon + chloromethane over a copper
        //    catalyst at ~300 °C gives dimethyldichlorosilane (Me2SiCl2) --
        r.machine("chemical_reactor", 128_000, 400)
                .tagIn("c:dusts/silicon", 1)
                .fluidIn(ni + "chloromethane", 2000)
                .tagIn("c:dusts/copper", 1, 0.20)        // copper catalyst, 20% wear
                .fluidOut(ni + "dimethyldichlorosilane", 1000)
                .save("silicone/dimethyldichlorosilane");

        // -- hydrolysis & condensation: Me2SiCl2 + 2 H2O -> PDMS backbone + 2 HCl;
        //    the released HCl loops back into chloromethane production --
        r.machine("chemical_reactor", 128_000, 300)
                .fluidIn(ni + "dimethyldichlorosilane", 1000)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "liquid_silicone_rubber", 1000)
                .fluidOut(mi + "hydrochloric_acid", 2000)
                .save("silicone/liquid_silicone_rubber");

        // -- cure: peroxide/sulfur crosslinking of LSR into a solid block --
        r.machine("chemical_reactor", 128_000, 300)
                .tagIn("c:dusts/sulfur", 1)
                .fluidIn(ni + "liquid_silicone_rubber", 1296)
                .itemOut(NIBlocks.SILICONE_RUBBER_BLOCK_ITEM.getId().toString(), 1)
                .save("silicone/silicone_rubber_block");

        // -- sliced into thin sheets on the cutting machine --
        r.machine("cutting_machine", 8, 200)
                .itemIn(NIBlocks.SILICONE_RUBBER_BLOCK_ITEM.getId().toString(), 1)
                .fluidIn(mi + "lubricant", 114)
                .itemOut(NIItems.SILICONE_RUBBER_SHEET.getId().toString(), 4)
                .save("silicone/silicone_rubber_sheet");

        // -- silicone-bonded mica: the TPV coil's high-temperature insulation --
        r.machine("assembler", 8, 200)
                .itemIn(NIItems.MICA_INSULATOR_SHEET.getId().toString(), 2)
                .itemIn(NIItems.SILICONE_RUBBER_SHEET.getId().toString(), 1)
                .itemOut(NIItems.SILICONE_MICA_INSULATOR_SHEET.getId().toString(), 1)
                .save("silicone/silicone_mica_insulator_sheet");
    }

    /**
     * Wetware circuit board raw materials, all real chemistry:
     * p-toluenesulfonic acid by para-sulfonation of toluene; polybenzimidazole
     * (Celazole) by the classic diphenyl-isophthalate melt polycondensation —
     * 3,3'-diaminobenzidine via the benzidine route (benzene &rarr; nitrobenzene &rarr;
     * benzidine &rarr; 3,3'-dinitrobenzidine &rarr; DAB), diphenyl isophthalate via
     * m-xylene (toluene disproportionation &rarr; Mid-Century oxidation &rarr;
     * esterification with phenol, which the polycondensation then gives back);
     * PBI cured and cut into plates exactly like the epoxy plate route.
     */
    private void pbiChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // -- p-toluenesulfonic acid: para sulfonation of toluene in sulfuric acid,
        //    crystallized as the monohydrate (the o-isomer is recycled) --
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "toluene", 1000)
                .fluidIn(mi + "sulfuric_acid", 1000)
                .itemOut(NIItems.P_TOLUENESULFONIC_ACID.getId().toString(), 2)
                .fluidOut("minecraft:water", 1000)
                .save("pbi/p_toluenesulfonic_acid");

        // -- 3,3'-diaminobenzidine via the benzidine route --
        // benzene -> nitrobenzene (mixed-acid nitration; the sulfuric acid is the
        // dehydrating medium and leaves the reactor unchanged)
        r.machine("chemical_reactor", 32, 200)
                .fluidIn(mi + "benzene", 1000)
                .fluidIn(ni + "nitric_acid", 1000)
                .fluidIn(mi + "sulfuric_acid", 1000)
                .fluidOut(ni + "nitrobenzene", 1000)
                .fluidOut("minecraft:water", 1000)
                .fluidOut(mi + "sulfuric_acid", 1000)
                .save("pbi/nitrobenzene");
        // 2 nitrobenzene + 5 H2 -> hydrazobenzene -> benzidine (catalytic reduction
        // followed by the benzidine rearrangement)
        r.machine("chemical_reactor", 64, 300)
                .fluidIn(ni + "nitrobenzene", 2000)
                .fluidIn(mi + "hydrogen", 5000)
                .itemOut(NIItems.BENZIDINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 4000)
                .save("pbi/benzidine");
        // benzidine -> 3,3'-dinitrobenzidine (mixed-acid dinitration seats the
        // nitro groups at the free 3,3' positions)
        r.machine("chemical_reactor", 32, 300)
                .itemIn(NIItems.BENZIDINE.getId().toString(), 1)
                .fluidIn(ni + "nitric_acid", 2000)
                .fluidIn(mi + "sulfuric_acid", 2000)
                .itemOut(NIItems.DINITROBENZIDINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 2000)
                .fluidOut(mi + "sulfuric_acid", 2000)
                .save("pbi/dinitrobenzidine");
        // 3,3'-dinitrobenzidine + 6 H2 -> 3,3'-diaminobenzidine
        r.machine("chemical_reactor", 64, 300)
                .itemIn(NIItems.DINITROBENZIDINE.getId().toString(), 1)
                .fluidIn(mi + "hydrogen", 6000)
                .itemOut(NIItems.DIAMINOBENZIDINE.getId().toString(), 1)
                .fluidOut("minecraft:water", 4000)
                .save("pbi/diaminobenzidine");

        // -- diphenyl isophthalate via m-xylene --
        // toluene disproportionation (Tatoray-type transalkylation): 2 toluene ->
        // benzene + xylenes; the meta cut is taken (the equilibrium-major isomer)
        r.machine("chemical_reactor", 64, 400)
                .fluidIn(mi + "toluene", 2000)
                .fluidOut(mi + "benzene", 1000)
                .fluidOut(ni + "m_xylene", 1000)
                .save("pbi/m_xylene");
        // Amoco Mid-Century oxidation: m-xylene + 3 O2 -> isophthalic acid
        // (Co-Mn-Br catalyst; manganese dust stands in, sparingly consumed)
        r.machine("chemical_reactor", 64, 400)
                .fluidIn(ni + "m_xylene", 1000)
                .fluidIn(mi + "oxygen", 3000)
                .tagIn("c:dusts/manganese", 1, 0.15)
                .itemOut(NIItems.ISOPHTHALIC_ACID.getId().toString(), 2)
                .fluidOut("minecraft:water", 1000)
                .save("pbi/isophthalic_acid");
        // esterification of the acid with phenol -> diphenyl isophthalate
        r.machine("chemical_reactor", 32, 300)
                .itemIn(NIItems.ISOPHTHALIC_ACID.getId().toString(), 2)
                .fluidIn(ni + "phenol", 2000)
                .itemOut(NIItems.DIPHENYL_ISOPHTHALATE.getId().toString(), 2)
                .fluidOut("minecraft:water", 1000)
                .save("pbi/diphenyl_isophthalate");

        // -- Celazole melt polycondensation: DAB + diphenyl isophthalate -> PBI,
        //    the ester phenol distilling off (prepolymer ~200 C, solid-state
        //    finish ~400 C) --
        r.machine("chemical_reactor", 64_000, 400)
                .itemIn(NIItems.DIAMINOBENZIDINE.getId().toString(), 2)
                .itemIn(NIItems.DIPHENYL_ISOPHTHALATE.getId().toString(), 2)
                .fluidOut(ni + "polybenzimidazole", 1296)
                .fluidOut(ni + "phenol", 4000)
                .save("pbi/polybenzimidazole");

        // -- curing and cutting, mirroring the epoxy plate route exactly --
        r.machine("vacuum_freezer", 32, 400)
                .fluidIn(ni + "polybenzimidazole", 1296)
                .itemIn(mi + "solid_titanium_machine_casing", 1)
                .itemOut(NIBlocks.POLYBENZIMIDAZOLE_BLOCK_ITEM.getId().toString(), 1)
                .save("pbi/polybenzimidazole_block");
        r.machine("cutting_machine", 16, 300)
                .itemIn(NIBlocks.POLYBENZIMIDAZOLE_BLOCK_ITEM.getId().toString(), 1)
                .fluidIn(mi + "lubricant", 233)
                .itemOut(NIItems.POLYBENZIMIDAZOLE_PLATE.getId().toString(), 16)
                .save("pbi/polybenzimidazole_plate");

        // -- the naquadah chassis the board is built on: rods and plates bonded
        //    with cyanoacrylate (see glueChain), the stronger glue --
        r.machine("assembler", 8, 200)
                .itemIn(NIMaterials.Materials.get("naquadah").id("rod"), 4)
                .itemIn(NIMaterials.Materials.get("naquadah").id("plate"), 4)
                .fluidIn(ni + "cyanoacrylate_glue", 100)
                .itemOut(NIItems.NAQUADAH_FRAME.getId().toString(), 1)
                .save("pbi/naquadah_frame");
    }

    /**
     * Cyanoacrylate adhesive program — the "stronger glue", one tier above MI's
     * acrylic glue (real cyanoacrylates bond at roughly twice the tensile
     * strength). Following the real industrial route (see e.g. patent
     * EP0714887A1): neutralization of HCN to sodium cyanide, SN2 cyanation of
     * chloroacetic acid to cyanoacetic acid, TsOH-catalyzed esterification with
     * methanol, Knoevenagel condensation with formaldehyde straight to the
     * polymer (the monomer polymerizes as fast as it forms), and finally
     * SO2-inhibited thermal depolymerization back to the reactive monomer —
     * which is the finished adhesive.
     */
    private void glueChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // NaOH + HCN -> NaCN + H2O
        r.machine("chemical_reactor", 16, 200)
                .fluidIn(mi + "sodium_hydroxide", 1000)
                .fluidIn(ni + "hydrogen_cyanide", 1000)
                .itemOut(NIItems.SODIUM_CYANIDE.getId().toString(), 4)
                .fluidOut("minecraft:water", 1000)
                .save("glue/sodium_cyanide");
        // chloroacetic acid + NaCN -> cyanoacetic acid + NaCl (SN2 cyanation)
        r.machine("chemical_reactor", 32, 240)
                .itemIn(NIItems.CHLOROACETIC_ACID.getId().toString(), 1)
                .itemIn(NIItems.SODIUM_CYANIDE.getId().toString(), 1)
                .itemOut(NIItems.CYANOACETIC_ACID.getId().toString(), 1)
                .itemOut(mi + "salt_dust", 1)
                .save("glue/cyanoacetic_acid");
        // esterification with methanol over p-toluenesulfonic acid
        r.machine("chemical_reactor", 32, 300)
                .itemIn(NIItems.CYANOACETIC_ACID.getId().toString(), 2)
                .fluidIn(ni + "methanol", 1000)
                .itemIn(NIItems.P_TOLUENESULFONIC_ACID.getId().toString(), 1, 0.10)
                .fluidOut(ni + "methyl_cyanoacetate", 1000)
                .fluidOut("minecraft:water", 1000)
                .save("glue/methyl_cyanoacetate");
        // Knoevenagel condensation with formaldehyde -> poly(methyl cyanoacrylate)
        // (cheap base catalysis; water of condensation leaves)
        r.machine("chemical_reactor", 32, 300)
                .fluidIn(ni + "methyl_cyanoacetate", 1000)
                .fluidIn(ni + "formaldehyde", 1000)
                .itemIn(NIItems.SLAKED_LIME.getId().toString(), 1, 0.25)
                .itemOut(NIItems.POLY_METHYL_CYANOACRYLATE.getId().toString(), 2)
                .fluidOut("minecraft:water", 1000)
                .save("glue/poly_methyl_cyanoacrylate");
        // thermal depolymerization: the SO2 inhibitor keeps the cracked monomer
        // from repolymerizing; the distilled monomer is the finished adhesive
        r.machine("chemical_reactor", 64, 400)
                .itemIn(NIItems.POLY_METHYL_CYANOACRYLATE.getId().toString(), 2)
                .fluidIn(ni + "sulfur_dioxide", 100)
                .fluidOut(ni + "cyanoacrylate_glue", 1000)
                .save("glue/cyanoacrylate_glue");
    }

    /**
     * Wetware circuit program: naquadah (硅岩, from the mod's own naquadah chain)
     * bombarded in the algae cultivator becomes supercharged naquadah — but only a
     * COLD culture can seed the mutation: the dish must contain a cold strain
     * (teal / cyan / deep blue) and none of the warm ones (red / orange / pink),
     * see the {@code cold_petri_dishes} tag. Both inputs are only consumed half
     * the time and the product appears 25% of the time. Dissolved and blended
     * into agar it yields the mutagen fluid that rewrites ONE four-strain petri
     * dish — grafted onto four of MI's own parts — into the bio-replacements
     * (RAM / MMU / ALU). The wetware circuit assembles like the crystal circuit,
     * one tier up, on an elite-pump cryo loop.
     */
    private void bioCircuitChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";

        // -- cold cultures only: teal (glaucophyta) / cyan (cyanophyta) / deep blue
        //    (caeruleophyta) present, and no red / orange / pink at all --
        EnumSet<NIAlgae> cold = EnumSet.of(NIAlgae.GLAUCOPHYTA, NIAlgae.CYANOPHYTA, NIAlgae.CAERULEOPHYTA);
        EnumSet<NIAlgae> warm = EnumSet.of(NIAlgae.ERYTHROPHYTA, NIAlgae.AURANTIOPHYTA, NIAlgae.RHODOPHYTA);
        String[] coldDishes = NIPetriDishes.ALL.stream()
                .filter(d -> d.algae().stream().anyMatch(cold::contains))
                .filter(d -> d.algae().stream().noneMatch(warm::contains))
                .map(d -> d.item().getId().toString())
                .toArray(String[]::new);
        r.tag(ni + "cold_petri_dishes", coldDishes);

        // -- mutagenic bombardment in the cultivator: cold dish + naquadah;
        //    each input is only consumed 50% of the time, the charged naquadah
        //    appears 25% of the time (and the dish counts into the machine's
        //    repeat penalty) --
        r.machine("algae_cultivator", 8, 1000)
                .tagIn(ni + "cold_petri_dishes", 1, 0.5)
                .itemIn(NIMaterials.Materials.get("naquadah").id("ingot"), 1, 0.5)
                .itemOut(NIItems.SUPERCHARGED_NAQUADAH.getId().toString(), 1, 0.25)
                .save("wetware/supercharged_naquadah");

        // -- dissolution of the charged naquadah --
        r.machine("chemical_reactor", 32_000, 200)
                .itemIn(NIItems.SUPERCHARGED_NAQUADAH.getId().toString(), 1)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "supercharged_naquadah_solution", 1000)
                .save("wetware/supercharged_naquadah_solution");

        // -- mutagen (a fluid): agar laced with the charged solution --
        r.machine("chemical_reactor", 64_000, 400)
                .itemIn(NIItems.AGAR.getId().toString(), 2)
                .fluidIn(ni + "supercharged_naquadah_solution", 500)
                .fluidOut(ni + "mutagen", 500)
                .save("wetware/mutagen");

        // -- the bio-parts: ONE four-strain dish each, rewritten by the mutagen
        //    and grafted onto four of MI's own parts (100/200/300 mB of mutagen
        //    so the three recipes stay distinguishable) --
        var bioParts = List.of(
                NIItems.BIO_RANDOM_ACCESS_MEMORY, NIItems.BIO_MEMORY_MANAGEMENT_UNIT,
                NIItems.BIO_ARITHMETIC_LOGIC_UNIT);
        var miParts = List.of(
                mi + "random_access_memory", mi + "memory_management_unit",
                mi + "arithmetic_logic_unit");
        for (int i = 0; i < bioParts.size(); i++) {
            r.machine("assembler", 512_000, 2_000)
                    .itemIn(NIPetriDishes.FOUR_STRAIN.get(i).item().getId().toString(), 1)
                    .itemIn(miParts.get(i), 4)
                    .fluidIn(ni + "mutagen", (i + 1) * 100)
                    .itemOut(bioParts.get(i).getId().toString(), 1)
                    .save("wetware/" + bioParts.get(i).getId().getPath());
        }

        // -- the elite cryo loop: nine advanced motors on a naquadah shaft make
        //    the elite motor; three advanced pumps around it make the elite pump --
        r.machine("assembler", 512_000, 4_000)
                .itemIn(mi + "large_advanced_motor", 9)
                .itemIn(NIMaterials.Materials.get("naquadah").id("rod"), 4)
                .itemIn(mi + "quantum_circuit", 2)
                .fluidIn(mi + "lubricant", 1000)
                .itemOut(NIItems.ELITE_MOTOR.getId().toString(), 1)
                .save("wetware/elite_motor");
        r.machine("assembler", 1_000_000, 4_000)
                .itemIn(mi + "large_advanced_pump", 3)
                .itemIn(NIItems.ELITE_MOTOR.getId().toString(), 1)
                .itemIn(NIMaterials.Materials.get("naquadah").id("rotor"), 6)
                .itemIn(mi + "quantum_circuit", 4)
                .itemOut(NIItems.ELITE_PUMP.getId().toString(), 1)
                .save("wetware/elite_pump");

        // -- wetware circuit board: the crystal board recipe rebuilt for biology —
        //    PBI plates (aerospace-grade Celazole, see pbiChain) glued onto a
        //    naquadah frame with cyanoacrylate (see glueChain), p-toluenesulfonic
        //    acid AND fluoroantimonic acid etching, advanced-superconductor
        //    wiring, mutagen doping, and an elite pump driving the cryo loop --
        r.machine("assembler", 1_000_000, 20_000)
                .itemIn(NIItems.NAQUADAH_FRAME.getId().toString(), 1)
                .itemIn(NIItems.POLYBENZIMIDAZOLE_PLATE.getId().toString(), 3)
                .itemIn(NIMaterials.Materials.get("advanced_superconductor").cableId(), 12)
                .itemIn(mi + "plutonium_battery", 16)
                .itemIn(ni + "crystal_circuit_board", 1)
                .itemIn(NIMaterials.Materials.get("naquadah").id("plate"), 4)
                .itemIn(NIItems.P_TOLUENESULFONIC_ACID.getId().toString(), 2)
                .itemIn(NIItems.ELITE_PUMP.getId().toString(), 1, 0.10)
                .fluidIn(ni + "mutagen", 400)
                .fluidIn(mi + "helium_3", 100)
                .fluidIn(ni + "fluoroantimonic_acid", 10)
                .itemOut(ni + "wetware_circuit_board", 1)
                .save("electric_age/circuit/assembler/wetware_circuit_board");

        // -- the wetware circuit: processing-unit layout (4 circuits + 2 RAM + 1 MMU +
        //    1 ALU + board), one tier above the crystal circuit --
        r.machine("assembler", 1_000_000, 20_000)
                .itemIn(ni + "crystal_circuit", 4)
                .itemIn(NIItems.BIO_RANDOM_ACCESS_MEMORY.getId().toString(), 2)
                .itemIn(NIItems.BIO_MEMORY_MANAGEMENT_UNIT.getId().toString(), 1)
                .itemIn(NIItems.BIO_ARITHMETIC_LOGIC_UNIT.getId().toString(), 1)
                .itemIn(ni + "wetware_circuit_board", 1)
                .itemOut(ni + "wetware_circuit", 1)
                .save("electric_age/circuit/assembler/wetware_circuit");
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

    /**
     * Resonant circuit program, part 1: the all-element multi-cycle separation
     * cascade. Radon (fusion of xenon and oxygen) cracks supercharged naquadah
     * into a superheavy fission solution, and four real radiochemistry cycles
     * carve the periodic table's entire last row out of it — PUREX
     * (U/Pu/Np/Th), TRUEX + TALSPEAK (Am..Lr with alpha-HIBA elution ladders),
     * light-actinide carrier chemistry (Ac/La coprecipitation, Pa manganese
     * adsorption), and transactinide single-atom chemistry (Rf..Cn: fluoride
     * anion exchange, oxychloride volatility, gold-foil trapping).
     *
     * Every catalyst is consumed with a small independent probability; product
     * probabilities are the chemical yields. Separation steps run at the 2G
     * EU/t / 4000 s gate, reagent syntheses at 2G EU/t / 1000 s, and the two
     * fusion feeds at MI's 16k EU/t fusion precedent.
     */
    private void resonantSeparationChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000; // the 2G EU/t gate of the resonant program
        int T = 80_000;         // 4000 s per separation step
        int RT = 20_000;        // 1000 s for reagent syntheses

        // -- feeds ------------------------------------------------------------------

        // Radon: fusion of xenon with an oxygen jacket (real Rn sits below Xe;
        // unreacted oxygen recycles out of the torch).
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(ni + "xenon", 1000)
                .fluidIn(mi + "oxygen", 3000)
                .fluidOut(ni + "radon", 200)
                .fluidOut(mi + "oxygen", 1000)
                .save("resonant/radon_fusion");

        // Superheavy cracking: radon irradiation cracks supercharged naquadah
        // solution into the fission solution everything below feeds on.
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(ni + "supercharged_naquadah_solution", 1000)
                .fluidIn(ni + "radon", 100)
                .fluidOut(ni + "superheavy_fission_solution", 1000)
                .fluidOut(ni + "inert_naquadah_solution", 800)
                .save("resonant/superheavy_cracking");

        // Backup solid feed: the naquadah line's heavy element residue digested
        // with radon-spiked nitric acid (for when monazite is the bottleneck).
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.HEAVY_ELEMENT_RESIDUE_DUST.getId().toString(), 4)
                .fluidIn(ni + "radon", 100)
                .fluidIn(ni + "nitric_acid", 1000)
                .fluidOut(ni + "superheavy_fission_solution", 1000)
                .save("resonant/residue_dissolution");

        // -- Cycle A: PUREX (U / Pu / Np / Th) ---------------------------------------

        // A1. Valence adjustment to Pu(IV)/Np(VI) nitrate with catalytic nitrite.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_fission_solution", 1000)
                .itemIn(NIItems.SODIUM_NITRITE.getId().toString(), 1, 0.25)
                .fluidOut(ni + "valence_adjusted_feed", 1000)
                .save("resonant/purex_valence_adjustment");

        // A2. TBP extraction: U/Pu/Np load into the organic phase; everything
        // else stays in the high-level raffinate (Cycle B/C feed). Noble fission
        // gas and a tellurium fraction ride along as byproducts.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "valence_adjusted_feed", 1000)
                .itemIn(NIItems.TRIBUTYL_PHOSPHATE.getId().toString(), 1, 0.10)
                .fluidOut(ni + "tbp_organic_phase", 900)
                .fluidOut(ni + "hlr_raffinate", 1000)
                .fluidOut(ni + "xenon", 20)
                .itemOut(NIItems.TELLURIUM_DUST.getId().toString(), 1, 0.10)
                .save("resonant/purex_tbp_extraction");

        // A3. Hydrazine strip: Pu(III) drops out of the organic phase while
        // U/Np co-strip into one liquor.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "tbp_organic_phase", 1000)
                .itemIn(NIItems.HYDRAZINE.getId().toString(), 1, 0.10)
                .fluidOut(ni + "plutonium_liquor", 400)
                .fluidOut(ni + "uranium_neptunium_liquor", 600)
                .save("resonant/purex_hydrazine_strip");

        // A4. Nitric acid valence split of the U/Np liquor.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "uranium_neptunium_liquor", 1000)
                .fluidIn(ni + "nitric_acid", 1000)
                .fluidOut(ni + "uranium_liquor", 850)
                .fluidOut(ni + "neptunium_liquor", 150)
                .fluidOut("minecraft:water", 1000)
                .save("resonant/purex_un_split");

        // A5-A7. Denitration + hydrogen reduction to the metals.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "uranium_liquor", 1000)
                .fluidIn(mi + "hydrogen", 2000)
                .itemOut(mi + "le_uranium_dust", 1, 0.90)
                .fluidOut("minecraft:water", 2000)
                .save("resonant/purex_uranium_reduction");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "plutonium_liquor", 1000)
                .fluidIn(mi + "hydrogen", 2000)
                .itemOut(mi + "plutonium_dust", 1, 0.85)
                .fluidOut("minecraft:water", 2000)
                .save("resonant/purex_plutonium_reduction");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "neptunium_liquor", 1000)
                .fluidIn(mi + "hydrogen", 2000)
                .itemOut(NIItems.NEPTUNIUM_DUST.getId().toString(), 1, 0.85)
                .fluidOut("minecraft:water", 2000)
                .save("resonant/purex_neptunium_reduction");

        // A8. Thorough scrub of the co-extracted thorium from the organic phase.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "tbp_organic_phase", 500)
                .fluidIn(ni + "nitric_acid", 500)
                .itemOut(NIItems.THORIUM_DUST.getId().toString(), 1, 0.90)
                .fluidOut("minecraft:water", 1000)
                .save("resonant/purex_thorium_scrub");

        // -- Cycle B: TRUEX + TALSPEAK (Am..Lr) --------------------------------------

        // B1. CMPO co-extraction of the minor actinides; the lanthanide aqueous
        // phase loops back into the monazite heavy-residue stream.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "hlr_raffinate", 1000)
                .itemIn(NIItems.CMPO_EXTRACTANT.getId().toString(), 1, 0.05)
                .fluidOut(ni + "truex_organic", 800)
                .fluidOut(ni + "monazite_heavy_residue_solution", 200)
                .save("resonant/truex_cmpo_extraction");

        // B2. Nitric acid strip of the loaded TRUEX phase (TBP solvent recycle).
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "truex_organic", 1000)
                .fluidIn(ni + "nitric_acid", 1000)
                .fluidOut(ni + "minor_actinide_liquor", 900)
                .fluidOut(ni + "tbp_organic_phase", 100)
                .save("resonant/truex_nitric_strip");

        // B3. TALSPEAK split: DTPA in a lactic buffer separates the early
        // (Am/Cm/Bk/Cf/Es) from the late (Fm/Md/No/Lr) actinide groups.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "minor_actinide_liquor", 1000)
                .itemIn(NIItems.DTPA_COMPLEXANT.getId().toString(), 1, 0.10)
                .fluidIn("minecraft:water", 1000)
                .fluidOut(ni + "early_actinide_group", 600)
                .fluidOut(ni + "late_actinide_group", 400)
                .save("resonant/talspeak_group_split");

        // B4. Bk(IV) chlorate oxidation precipitates berkelium out of the early
        // group (the real Bk/Cf separation); a tellurium fraction comes along.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "early_actinide_group", 1000)
                .itemIn(NIItems.SODIUM_CHLORATE.getId().toString(), 1, 0.10)
                .itemOut(NIItems.BERKELIUM_DUST.getId().toString(), 1, 0.80)
                .itemOut(NIItems.TELLURIUM_DUST.getId().toString(), 1, 0.15)
                .fluidOut(ni + "early_actinide_group", 700)
                .save("resonant/berkelium_oxidation");

        // B5. Cation-exchange alpha-HIBA elution ladder over the early group:
        // the elution volume picks the element (Am elutes first, Es last).
        String[] early = {"americium", "curium", "californium", "einsteinium"};
        for (int i = 0; i < early.length; i++) {
            r.machine("chemical_reactor", EU, T)
                    .fluidIn(ni + "early_actinide_group", 1000)
                    .itemIn(NIItems.ALPHA_HIBA_ELUANT.getId().toString(), 1, 0.10)
                    .fluidIn("minecraft:water", 100 * (i + 1))
                    .itemOut(itemId(early[i] + "_dust"), 1, 0.80)
                    .fluidOut(ni + "early_actinide_group", 800)
                    .save("resonant/elution_early_" + early[i]);
        }

        // B6. Same ladder over the late group (Fm first ... Lr last; No comes
        // off two columns early because it reduces to +2 - real nobelium).
        String[] late = {"fermium", "mendelevium", "nobelium", "lawrencium"};
        for (int i = 0; i < late.length; i++) {
            r.machine("chemical_reactor", EU, T)
                    .fluidIn(ni + "late_actinide_group", 1000)
                    .itemIn(NIItems.ALPHA_HIBA_ELUANT.getId().toString(), 1, 0.10)
                    .fluidIn("minecraft:water", 100 * (i + 1))
                    .itemOut(itemId(late[i] + "_dust"), 1, 0.70)
                    .fluidOut(ni + "late_actinide_group", 800)
                    .save("resonant/elution_late_" + late[i]);
        }

        // -- Cycle C: light actinides (Ac / Pa) --------------------------------------

        // C1. Actinium rides a lanthanum (monazite) carrier into the carbonate
        // precipitate - the real 225Ac carrier process.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "hlr_raffinate", 1000)
                .tagIn("c:dusts/monazite", 2, 0.50)
                .fluidIn(mi + "sodium_hydroxide", 500)
                .itemOut(NIItems.ACTINIUM_DUST.getId().toString(), 1, 0.75)
                .fluidOut(ni + "monazite_heavy_residue_solution", 800)
                .save("resonant/actinium_carrier");

        // C2. Protactinium adsorbs on manganese dioxide, eluted with HF.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_fission_solution", 1000)
                .tagIn("c:dusts/manganese", 1)
                .fluidIn(ni + "hydrofluoric_acid", 200)
                .itemOut(NIItems.PROTACTINIUM_DUST.getId().toString(), 1, 0.75)
                .fluidOut(ni + "inert_naquadah_solution", 900)
                .save("resonant/protactinium_adsorption");

        // -- Cycle D: transactinide single-atom chemistry (Rf..Cn) ------------------

        // D1. Fluoride anion-exchange: Rf and Db come off at different HF strengths.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_fission_solution", 1000)
                .fluidIn(ni + "hydrofluoric_acid", 100)
                .itemOut(NIItems.RUTHERFORDIUM_DUST.getId().toString(), 1, 0.60)
                .fluidOut(ni + "superheavy_fission_solution", 900)
                .save("resonant/rutherfordium_fluoride");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_fission_solution", 1000)
                .fluidIn(ni + "hydrofluoric_acid", 200)
                .itemOut(NIItems.DUBNIUM_DUST.getId().toString(), 1, 0.60)
                .fluidOut(ni + "superheavy_fission_solution", 900)
                .save("resonant/dubnium_fluoride");

        // D2. Sg forms a volatile oxychloride (SgO2Cl2, the SeO2 congener route
        // with tellurium standing in for selenium) and distills into the trap.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_fission_solution", 1000)
                .fluidIn(mi + "chlorine", 1000)
                .itemIn(NIItems.TELLURIUM_DUST.getId().toString(), 1, 0.25)
                .fluidOut(ni + "superheavy_vapor", 300)
                .fluidOut(ni + "inert_naquadah_solution", 700)
                .save("resonant/seaborgium_volatilization");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 400)
                .itemOut(NIItems.SEABORGIUM_DUST.getId().toString(), 1, 0.50)
                .fluidOut(mi + "chlorine", 300)
                .save("resonant/seaborgium_condensation");

        // D3. Bh oxychloride (BhO3Cl) fraction of the vapor, cracked with HCl.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .fluidIn(mi + "hydrochloric_acid", 200)
                .itemOut(NIItems.BOHRIUM_DUST.getId().toString(), 1, 0.50)
                .fluidOut(mi + "chlorine", 100)
                .save("resonant/bohrium_oxychloride");

        // D4. Hs forms a volatile oxide (HsO4; the telluric-acid congener route).
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .fluidIn(ni + "telluric_acid", 200)
                .itemOut(NIItems.HASSIUM_DUST.getId().toString(), 1, 0.55)
                .fluidOut(mi + "oxygen", 200)
                .save("resonant/hassium_oxide");

        // D5. Cn condenses onto a gold-foil surface trap (the real experiment
        // design); gentle heating desorbs the metal.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .itemIn(NIItems.GOLD_FOIL.getId().toString(), 1, 0.05)
                .fluidIn(mi + "helium_3", 100)
                .fluidOut(ni + "cn_condensate", 100)
                .fluidOut(ni + "superheavy_vapor", 100)
                .save("resonant/copernicium_gold_trap");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "cn_condensate", 100)
                .itemOut(NIItems.COPERNICIUM_DUST.getId().toString(), 1, 0.65)
                .save("resonant/copernicium_desorption");

        // D6. Mt / Ds yield to stepped aqua-regia leaching of the vapor.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .fluidIn(ni + "aqua_regia", 100)
                .itemOut(NIItems.MEITNERIUM_DUST.getId().toString(), 1, 0.55)
                .fluidOut(mi + "chlorine", 100)
                .save("resonant/meitnerium_leach");
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .fluidIn(ni + "aqua_regia", 200)
                .itemOut(NIItems.DARMSTADTIUM_DUST.getId().toString(), 1, 0.55)
                .fluidOut(mi + "chlorine", 100)
                .save("resonant/darmstadtium_leach");

        // D7. Rg (gold's congener) complexes with thioethers and reduces clean.
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "superheavy_vapor", 300)
                .fluidIn(ni + "sulfur_dioxide", 200)
                .fluidIn(mi + "hydrogen", 200)
                .itemOut(NIItems.ROENTGENIUM_DUST.getId().toString(), 1, 0.60)
                .fluidOut("minecraft:water", 200)
                .save("resonant/roentgenium_thioether");

        // -- reagents -----------------------------------------------------------------

        // TBP: hydrodeoxygenated isobutyraldehyde butyl chains esterified onto
        // a monazite (phosphate mineral) backbone; the residue solution loops
        // back into the monazite heavy-residue stream.
        r.machine("chemical_reactor", EU, RT)
                .tagIn("c:dusts/monazite", 1)
                .fluidIn(ni + "isobutyraldehyde", 1500)
                .fluidIn(mi + "chlorine", 1000)
                .fluidIn(mi + "hydrogen", 6000)
                .itemOut(NIItems.TRIBUTYL_PHOSPHATE.getId().toString(), 4)
                .fluidOut(ni + "monazite_heavy_residue_solution", 500)
                .fluidOut("minecraft:water", 3000)
                .save("resonant/reagent_tbp");

        // CMPO: a TBP phenyl-carbamoyl extension (the TRUEX molecule).
        r.machine("chemical_reactor", EU, RT)
                .itemIn(NIItems.TRIBUTYL_PHOSPHATE.getId().toString(), 1)
                .fluidIn(ni + "phenol", 200)
                .fluidIn(ni + "ammonia", 500)
                .fluidIn(mi + "chlorine", 500)
                .itemOut(NIItems.CMPO_EXTRACTANT.getId().toString(), 2)
                .fluidOut("minecraft:water", 500)
                .save("resonant/reagent_cmpo");

        // DTPA: cyanamide homologation in hydrochloric ammonia.
        r.machine("chemical_reactor", EU, RT)
                .itemIn(NIItems.CYANAMIDE.getId().toString(), 2)
                .fluidIn(ni + "ammonia", 1000)
                .fluidIn(mi + "hydrochloric_acid", 500)
                .itemOut(NIItems.DTPA_COMPLEXANT.getId().toString(), 2)
                .fluidOut("minecraft:water", 1000)
                .save("resonant/reagent_dtpa");

        // alpha-HIBA: alpha-hydroxyisobutyric acid from isobutyraldehyde.
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "isobutyraldehyde", 500)
                .fluidIn(mi + "hydrochloric_acid", 200)
                .fluidIn(mi + "oxygen", 500)
                .itemOut(NIItems.ALPHA_HIBA_ELUANT.getId().toString(), 2)
                .fluidOut("minecraft:water", 300)
                .save("resonant/reagent_alpha_hiba");

        // Hydrazine: Raschig-style chloramine ammonia route.
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "ammonia", 2000)
                .fluidIn(mi + "chlorine", 1000)
                .itemOut(NIItems.HYDRAZINE.getId().toString(), 2)
                .itemOut(NIItems.AMMONIUM_CHLORIDE.getId().toString(), 2)
                .fluidOut("minecraft:water", 500)
                .save("resonant/reagent_hydrazine");

        // Sodium nitrite / chlorate from caustic soda (fluid form in MI).
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(mi + "sodium_hydroxide", 1000)
                .fluidIn(ni + "nitric_acid", 500)
                .itemOut(NIItems.SODIUM_NITRITE.getId().toString(), 3)
                .fluidOut("minecraft:water", 1000)
                .save("resonant/reagent_sodium_nitrite");
        r.machine("electrolyzer", EU, RT)
                .fluidIn("minecraft:water", 1000)
                .fluidIn(mi + "chlorine", 1000)
                .fluidIn(mi + "sodium_hydroxide", 1000)
                .itemOut(NIItems.SODIUM_CHLORATE.getId().toString(), 3)
                .fluidOut(mi + "hydrogen", 500)
                .save("resonant/reagent_sodium_chlorate");

        // Telluric acid: the Te(VI) oxidizer for the Hs route.
        r.machine("chemical_reactor", EU, RT)
                .itemIn(NIItems.TELLURIUM_DUST.getId().toString(), 1)
                .fluidIn(ni + "nitric_acid", 1000)
                .fluidIn(mi + "oxygen", 1000)
                .fluidOut(ni + "telluric_acid", 500)
                .fluidOut("minecraft:water", 500)
                .save("resonant/reagent_telluric_acid");

        // Aqua regia: one part nitric to three parts hydrochloric.
        r.machine("mixer", EU, RT)
                .fluidIn(ni + "nitric_acid", 250)
                .fluidIn(mi + "hydrochloric_acid", 750)
                .fluidOut(ni + "aqua_regia", 1000)
                .save("resonant/reagent_aqua_regia");

        // Gold foil: beaten from ingots for the copernicium surface trap.
        r.machine("compressor", EU, RT)
                .tagIn("c:ingots/gold", 1)
                .itemOut(NIItems.GOLD_FOIL.getId().toString(), 4)
                .save("resonant/reagent_gold_foil");
    }

    /** mi_nested_infinity item id shorthand for the elution ladders. */
    private static String itemId(String name) {
        return "mi_nested_infinity:" + name;
    }

    /**
     * Resonant circuit program, part 2: naquide (轻硅岩) derivation and the
     * fusion trinity. Gold/silver and the single-atom transactinides Rg/Cn
     * melt in the crucible, fuse into adamantium (Au+Rg) and mithril (Ag+Cn),
     * re-fuse into trinium, and a helium-3 quench casts the ingots. Trinium
     * has no EBF route on purpose (skipEbfRecipes): fusion is the only way in.
     */
    private void resonantFusionChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000;
        int T = 80_000;

        // -- naquide: the light fraction spun out of inert naquadah solution,
        //    reduced with hydrogen to the crystal (coil dielectric feed) --
        r.machine("centrifuge", EU, T)
                .fluidIn(ni + "inert_naquadah_solution", 1000)
                .itemOut(NIItems.CRUDE_NAQUIDE_POWDER.getId().toString(), 1, 0.75)
                .fluidOut("minecraft:water", 900)
                .save("resonant/naquide_centrifuge");
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.CRUDE_NAQUIDE_POWDER.getId().toString(), 2)
                .fluidIn(mi + "hydrogen", 1000)
                .itemOut(NIItems.NAQUIDE.getId().toString(), 1, 0.90)
                .fluidOut("minecraft:water", 500)
                .save("resonant/naquide_reduction");

        // -- crucible melts: the fusion torch only drinks liquids --
        r.machine("magma_crucible", EU, T)
                .itemIn(mi + "gold_dust", 1)
                .fluidOut(ni + "molten_gold", 1000)
                .save("resonant/melt_gold");
        r.machine("magma_crucible", EU, T)
                .itemIn(mi + "silver_dust", 1)
                .fluidOut(ni + "molten_silver", 1000)
                .save("resonant/melt_silver");
        r.machine("magma_crucible", EU, T)
                .itemIn(NIItems.ROENTGENIUM_DUST.getId().toString(), 1)
                .fluidOut(ni + "molten_roentgenium", 1000)
                .save("resonant/melt_roentgenium");
        r.machine("magma_crucible", EU, T)
                .itemIn(NIItems.COPERNICIUM_DUST.getId().toString(), 1)
                .fluidOut(ni + "molten_copernicium", 1000)
                .save("resonant/melt_copernicium");

        // -- the fusion trinity (16k EU/t per MI's fusion precedent) --
        // adamantium = gold + roentgenium (Rg is gold's own congener)
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(ni + "molten_gold", 900)
                .fluidIn(ni + "molten_roentgenium", 100)
                .fluidOut(ni + "molten_adamantium", 1000)
                .save("resonant/fusion_adamantium");
        // mithril = silver + copernicium (Cn, the mercury-like volatile)
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(ni + "molten_silver", 900)
                .fluidIn(ni + "molten_copernicium", 100)
                .fluidOut(ni + "molten_mithril", 1000)
                .save("resonant/fusion_mithril");
        // trinium = the two alloys re-fused
        r.machine("fusion_reactor", 16_000, 2_400)
                .fluidIn(ni + "molten_adamantium", 500)
                .fluidIn(ni + "molten_mithril", 500)
                .fluidOut(ni + "molten_trinium", 1000)
                .save("resonant/fusion_trinium");

        // -- helium-3 quenches cast the fusion-born ingots --
        r.machine("vacuum_freezer", EU, T)
                .fluidIn(ni + "molten_adamantium", 1000)
                .fluidIn(mi + "helium_3", 200)
                .itemOut(NIItems.ADAMANTIUM_INGOT.getId().toString(), 1)
                .save("resonant/quench_adamantium");
        r.machine("vacuum_freezer", EU, T)
                .fluidIn(ni + "molten_mithril", 1000)
                .fluidIn(mi + "helium_3", 200)
                .itemOut(NIItems.MITHRIL_INGOT.getId().toString(), 1)
                .save("resonant/quench_mithril");
        r.machine("vacuum_freezer", EU, T)
                .fluidIn(ni + "molten_trinium", 1000)
                .fluidIn(mi + "helium_3", 200)
                .itemOut("modern_industrialization:trinium_ingot", 1)
                .save("resonant/quench_trinium");

        // -- alloy plates, laminated into the resonant circuit (MI's ingot -> plate
        //    compressor convention: eu 2, 200t, 1:1) --
        r.machine("compressor", 2, 200)
                .itemIn(NIItems.ADAMANTIUM_INGOT.getId().toString(), 1)
                .itemOut(NIItems.ADAMANTIUM_PLATE.getId().toString(), 1)
                .save("resonant/adamantium_plate");
        r.machine("compressor", 2, 200)
                .itemIn(NIItems.MITHRIL_INGOT.getId().toString(), 1)
                .itemOut(NIItems.MITHRIL_PLATE.getId().toString(), 1)
                .save("resonant/mithril_plate");

        // -- the coil alloy: trinium + 2 naquide -> trinium_dinaquadide dust; the
        //    EBF cast (dust -> hot ingot, TPV coil gate) is auto-generated by the
        //    material registration --
        r.machine("mixer", EU, T)
                .itemIn("modern_industrialization:trinium_dust", 1)
                .itemIn(NIItems.NAQUIDE.getId().toString(), 2)
                .itemOut("modern_industrialization:trinium_dinaquadide_dust", 3)
                .save("resonant/trinium_dinaquadide_blend");
    }

    /**
     * Resonant circuit program, part 3: the polyimide program (real Kapton
     * chemistry) and the conductive silver epoxy. Durene -> nitric oxidation
     * (vanadium catalyst) -> pyromellitic acid -> PMDA; nitrobenzene ->
     * p-nitrochlorobenzene -> dinitrodiphenyl ether -> ODA; PMDA + ODA ->
     * polyamic acid -> imidization -> PI. The silver epoxy cures with an
     * aromatic diamine hardener (the ODA monomer doubles as the curer).
     */
    private void resonantPolyimideChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000;
        int T = 80_000;
        int RT = 20_000;

        // durene: chloromethane methylation of m-xylene (Friedel-Crafts)
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "m_xylene", 500)
                .fluidIn(ni + "chloromethane", 500)
                .itemOut(NIItems.DURENE.getId().toString(), 1)
                .fluidOut(mi + "hydrochloric_acid", 500)
                .save("resonant/pi_durene");

        // pyromellitic acid: full-side-chain nitric oxidation on vanadium
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.DURENE.getId().toString(), 1)
                .fluidIn(ni + "nitric_acid", 2000)
                .tagIn("c:dusts/vanadium", 1, 0.10)
                .itemOut(NIItems.PYROMELLITIC_ACID.getId().toString(), 1, 0.90)
                .fluidOut("minecraft:water", 1500)
                .fluidOut(mi + "nitrogen", 500)
                .save("resonant/pi_pyromellitic_acid");

        // PMDA: dehydration of the tetracid to the dianhydride
        r.machine("chemical_reactor", EU, RT)
                .itemIn(NIItems.PYROMELLITIC_ACID.getId().toString(), 2)
                .itemOut(NIItems.PYROMELLITIC_DIANHYDRIDE.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("resonant/pi_pmda");

        // p-nitrochlorobenzene: para chlorination of nitrobenzene
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "nitrobenzene", 500)
                .fluidIn(mi + "chlorine", 500)
                .itemOut(NIItems.P_NITROCHLOROBENZENE.getId().toString(), 1, 0.80)
                .fluidOut(mi + "hydrochloric_acid", 500)
                .save("resonant/pi_p_nitrochlorobenzene");

        // dinitrodiphenyl ether: Ullmann etherification on copper
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.P_NITROCHLOROBENZENE.getId().toString(), 2)
                .fluidIn(mi + "oxygen", 500)
                .tagIn("c:dusts/copper", 1, 0.15)
                .itemOut(NIItems.DINITRODIPHENYL_ETHER.getId().toString(), 1)
                .fluidOut(mi + "chlorine", 500)
                .save("resonant/pi_dinitrodiphenyl_ether");

        // ODA: hydrogenation of the dinitro ether to the diamine
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.DINITRODIPHENYL_ETHER.getId().toString(), 1)
                .fluidIn(mi + "hydrogen", 4000)
                .itemOut(NIItems.DIAMINODIPHENYL_ETHER.getId().toString(), 1)
                .fluidOut("minecraft:water", 3000)
                .save("resonant/pi_oda");

        // polyamic acid: PMDA + ODA polycondensation in the reactor
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.PYROMELLITIC_DIANHYDRIDE.getId().toString(), 1)
                .itemIn(NIItems.DIAMINODIPHENYL_ETHER.getId().toString(), 1)
                .fluidOut(ni + "polyamic_acid", 1000)
                .save("resonant/pi_polyamic_acid");

        // imidization: cyclize the prepolymer, water boils off
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "polyamic_acid", 1000)
                .itemOut(NIItems.POLYIMIDE_DUST.getId().toString(), 1)
                .fluidOut("minecraft:water", 500)
                .save("resonant/pi_imidization");

        // the amber plate
        r.machine("compressor", EU, RT)
                .itemIn(NIItems.POLYIMIDE_DUST.getId().toString(), 4)
                .itemOut(NIItems.POLYIMIDE_PLATE.getId().toString(), 1)
                .save("resonant/pi_plate");

        // conductive silver epoxy: silver flakes in epoxy resin, cured by the
        // aromatic diamine (ODA hardener, consumed as a catalyst)
        r.machine("chemical_reactor", EU, RT)
                .tagIn("c:dusts/silver", 2)
                .fluidIn(ni + "epoxy_resin", 500)
                .itemIn(NIItems.DIAMINODIPHENYL_ETHER.getId().toString(), 1, 0.25)
                .fluidOut(ni + "conductive_epoxy", 500)
                .save("resonant/conductive_epoxy");
    }

    /**
     * Resonant circuit program, part 4: the fluoroelastomer (FKM) gasket
     * stock and the resonant YBCO superconductor tape / 2^36 EU/t cable.
     * FKM: chloromethane -> chloroform -> R-22 -> vinylylidene fluoride,
     * propene -> hexafluoropropylene, persulfate-initiated copolymerization.
     * YBCO: the monazite heavy-residue stream yields yttrium (with a PGM
     * residue byproduct that feeds back into the aqua-regia transactinide
     * leach), oxide charge mixing, sapphire substrates grown on the trinium
     * coil, and RF sputtering onto the tape.
     */
    private void resonantFluoroChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000;
        int T = 80_000;
        int RT = 20_000;

        // -- FKM (vinylidene fluoride / hexafluoropropylene copolymer) --------

        // chloroform: exhaustive chlorination of chloromethane
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "chloromethane", 500)
                .fluidIn(mi + "chlorine", 1000)
                .fluidOut(ni + "chloroform", 500)
                .fluidOut(mi + "hydrochloric_acid", 1000)
                .save("resonant/fkm_chloroform");

        // R-22: chlorine-fluorine exchange on chloroform with HF
        r.machine("chemical_reactor", EU, RT)
                .fluidIn(ni + "chloroform", 500)
                .fluidIn(ni + "hydrofluoric_acid", 1000)
                .fluidOut(ni + "refrigerant_22", 500)
                .fluidOut(mi + "hydrochloric_acid", 500)
                .save("resonant/fkm_refrigerant_22");

        // VDF monomer: high-temperature R-22 pyrolysis
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "refrigerant_22", 1000)
                .fluidOut(ni + "vinylidene_fluoride", 800)
                .fluidOut(mi + "hydrogen", 200)
                .fluidOut(mi + "hydrochloric_acid", 200)
                .save("resonant/fkm_vdf");

        // HFP comonomer: full fluorination of propene
        r.machine("chemical_reactor", EU, T)
                .fluidIn(mi + "propene", 500)
                .fluidIn(ni + "fluorine", 1500)
                .fluidOut(ni + "hexafluoropropylene", 400)
                .fluidOut(mi + "hydrogen", 1000)
                .save("resonant/fkm_hfp");

        // FKM: persulfate-initiated emulsion copolymerization, cured straight
        // into gasket sheet stock
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "vinylidene_fluoride", 600)
                .fluidIn(ni + "hexafluoropropylene", 200)
                .itemIn(NIItems.SODIUM_SULFATE.getId().toString(), 1, 0.10)
                .itemOut(NIItems.FLUOROELASTOMER_SHEET.getId().toString(), 2)
                .save("resonant/fkm_sheet");

        // -- resonant superconductor ------------------------------------------

        // yttrium from the monazite heavy-rare-earth stream on ion exchange
        // resin; the platinum-group residue feeds the aqua-regia leach below
        r.machine("chemical_reactor", EU, T)
                .fluidIn(ni + "monazite_heavy_residue_solution", 1000)
                .itemIn(NIItems.ION_EXCHANGE_RESIN.getId().toString(), 1, 0.50)
                .itemOut(NIItems.YTTRIUM_OXIDE.getId().toString(), 2, 0.90)
                .itemOut(NIItems.PGM_RESIDUE.getId().toString(), 1, 0.50)
                .fluidOut("minecraft:water", 1000)
                .save("resonant/ybco_yttrium");

        // the PGM residue: aqua-regia dissolution of the noble matrix carrying
        // the late transactinides (alternative superheavy vapor feed)
        r.machine("chemical_reactor", EU, T)
                .itemIn(NIItems.PGM_RESIDUE.getId().toString(), 2)
                .fluidIn(ni + "aqua_regia", 500)
                .fluidOut(ni + "superheavy_vapor", 200)
                .save("resonant/pgm_residue_leach");

        // cupric oxide: copper roasted in air
        r.machine("chemical_reactor", EU, RT)
                .tagIn("c:dusts/copper", 1)
                .fluidIn(mi + "oxygen", 1000)
                .itemOut(NIItems.CUPRIC_OXIDE.getId().toString(), 2)
                .save("resonant/ybco_cupric_oxide");

        // YBCO oxide charge: Y2O3 + 2 BaO + 3 CuO, mixed like the Hg cuprates
        r.machine("mixer", EU, RT)
                .itemIn(NIItems.YTTRIUM_OXIDE.getId().toString(), 1)
                .itemIn(NIItems.BARIUM_OXIDE_DUST.getId().toString(), 2)
                .itemIn(NIItems.CUPRIC_OXIDE.getId().toString(), 3)
                .fluidIn(mi + "oxygen", 500)
                .itemOut(NIItems.YBCO_TARGET.getId().toString(), 4)
                .save("resonant/ybco_mix");

        // sapphire substrates: corundum grown on the trinium coil tier
        // (recipe eu 32768 = the trinium_dinaquide coil; lower coils locked out)
        r.machine("blast_furnace", (int) com.nestedinfinity.mod.blocks.NICoils.TIERS.get(2).eu(), 600)
                .tagIn("c:dusts/aluminum", 2)
                .fluidIn(mi + "oxygen", 250)
                .itemOut(NIItems.SAPPHIRE_SUBSTRATE.getId().toString(), 2)
                .save("resonant/ybco_sapphire");

        // RF sputtering: the target wears out with 75% chance per deposition
        // (real sputter target lifetimes); argon carries the plasma
        r.machine("assembler", EU, T)
                .itemIn(NIItems.YBCO_TARGET.getId().toString(), 1, 0.75)
                .itemIn(NIItems.SAPPHIRE_SUBSTRATE.getId().toString(), 2)
                .fluidIn(mi + "argon", 500)
                .fluidIn(mi + "oxygen", 250)
                .itemOut(NIItems.RESONANT_SUPERCONDUCTOR_TAPE.getId().toString(), 2)
                .save("resonant/ybco_sputter");

        // the 2^36 EU/t cable: eight tapes laminated on resonite wire with the
        // conductive silver epoxy, sealed in liquid glass
        r.machine("assembler", EU, T)
                .itemIn(NIItems.RESONANT_SUPERCONDUCTOR_TAPE.getId().toString(), 8)
                .itemIn("modern_industrialization:resonite_wire", 4)
                .fluidIn(ni + "conductive_epoxy", 500)
                .fluidIn(ni + "liquid_glass", 144)
                .itemOut("modern_industrialization:resonant_superconductor_cable", 1)
                .save("resonant/resonant_superconductor_cable");
    }

    /**
     * Resonant circuit program, part 5: resonite, the piezoelectric quartz
     * side, the resonant mother liquor, the player-craftable Q8 notes
     * (white directly, red/yellow/blue on a mother-liquor ladder; the other
     * four colors only ever come out of the attuner), and the tuning block /
     * resonance attuner themselves. Resonite dust blends in the mixer; its
     * dust -&gt; hot ingot EBF recipe is auto-generated at the trinium coil
     * tier (32768) by the material system.
     */
    private void resonantTuningChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000;
        int T = 80_000;
        int RT = 20_000;

        // resonite: the ender-eye alloy (ender resonance + naquadah lattice +
        // polyimide binder); hot ingot follows on the trinium coil (auto EBF)
        r.machine("mixer", EU, RT)
                .itemIn("minecraft:ender_eye", 4)
                .itemIn("modern_industrialization:naquadah_dust", 2)
                .itemIn(NIItems.POLYIMIDE_DUST.getId().toString(), 1)
                .itemOut("modern_industrialization:resonite_dust", 4)
                .save("resonant/resonite_blend");

        // resonite cable: PI insulation, FKM jacket, silver-epoxy bonded
        // (the auto assembler/cable recipe is canceled in NIMaterials)
        r.machine("assembler", EU, RT)
                .itemIn("modern_industrialization:resonite_wire", 1)
                .itemIn(NIItems.POLYIMIDE_PLATE.getId().toString(), 1)
                .itemIn(NIItems.FLUOROELASTOMER_SHEET.getId().toString(), 1)
                .fluidIn(ni + "conductive_epoxy", 250)
                .fluidIn(ni + "liquid_glass", 72)
                .itemOut("modern_industrialization:resonite_cable", 1)
                .save("resonant/resonite_cable");

        // piezo wafers: quartz blanks cut along the resonant axis
        r.machine("cutting_machine", EU, RT)
                .itemIn("minecraft:quartz", 4)
                .fluidIn(mi + "lubricant", 233)
                .itemOut(NIItems.PIEZO_WAFER.getId().toString(), 8)
                .save("resonant/piezo_wafer");

        // lead titanate: the perovskite piezoceramic (MI has no zirconium,
        // so real PbTiO3 instead of PZT)
        r.machine("mixer", EU, RT)
                .tagIn("c:dusts/lead", 1)
                .tagIn("c:dusts/titanium", 1)
                .fluidIn(mi + "oxygen", 1500)
                .itemOut(NIItems.LEAD_TITANATE_DUST.getId().toString(), 3)
                .save("resonant/lead_titanate");

        // the sintered ceramic plate
        r.machine("compressor", EU, RT)
                .itemIn(NIItems.LEAD_TITANATE_DUST.getId().toString(), 4)
                .itemOut(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 1)
                .save("resonant/lead_titanate_plate");

        // quartz oscillator: wafer on a titanate header
        r.machine("assembler", EU, RT)
                .itemIn(NIItems.PIEZO_WAFER.getId().toString(), 2)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 1)
                .itemIn("modern_industrialization:resonite_dust", 1)
                .itemOut(NIItems.QUARTZ_OSCILLATOR.getId().toString(), 2)
                .save("resonant/quartz_oscillator");

        // SAW resonator: interdigital combs on a titanate substrate
        r.machine("assembler", EU, RT)
                .itemIn(NIItems.PIEZO_WAFER.getId().toString(), 4)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 2)
                .fluidIn(ni + "conductive_epoxy", 100)
                .itemOut(NIItems.SAW_RESONATOR.getId().toString(), 2)
                .save("resonant/saw_resonator");

        // the resonant mother liquor: ender eyes dissolved in mutagen —
        // the growth feed for every craftable note
        r.machine("chemical_reactor", EU, T)
                .itemIn("minecraft:ender_eye", 1)
                .fluidIn(ni + "mutagen", 800)
                .fluidIn("minecraft:water", 200)
                .fluidOut(ni + "resonant_mother_liquor", 1000)
                .save("resonant/mother_liquor");

        // the white note (the group identity): a crystal circuit reading a
        // monocrystalline naquadah reference
        r.machine("assembler", EU, T)
                .itemIn(ni + "crystal_circuit", 1)
                .itemIn(NIItems.HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH.getId().toString(), 1)
                .itemIn(NIItems.QUARTZ_OSCILLATOR.getId().toString(), 1)
                .fluidIn(ni + "resonant_mother_liquor", 100)
                .itemOut("mi_nested_infinity:note_white", 1)
                .save("resonant/note_white");

        // red / yellow / blue on the mother-liquor ladder; the complementary
        // colors (green/cyan/purple/black) exist only as attuner products
        String[] primaries = {"red", "yellow", "blue"};
        for (int i = 0; i < primaries.length; i++) {
            r.machine("assembler", EU, T)
                    .itemIn(NIItems.QUARTZ_OSCILLATOR.getId().toString(), 1)
                    .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 1)
                    .itemIn("modern_industrialization:resonite_plate", 1)
                    .fluidIn(ni + "resonant_mother_liquor", 100 * (i + 1))
                    .itemOut("mi_nested_infinity:note_" + primaries[i], 1)
                    .save("resonant/note_" + primaries[i]);
        }

        // the tuning block: an eight-state Q8 register around a white note seed
        // (inverse-color pair, each consumed with 50% probability)
        r.machine("assembler", EU, T)
                .itemIn(ni + "crystal_circuit", 1)
                .itemIn(NIItems.HIGH_PURITY_MONOCRYSTALLINE_NAQUADAH.getId().toString(), 2)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 2)
                .itemIn("modern_industrialization:resonite_plate", 1)
                .itemIn("mi_nested_infinity:note_white", 1, 0.5)
                .itemIn("mi_nested_infinity:note_black", 1, 0.5)
                .itemOut("mi_nested_infinity:tuning_block", 1)
                .save("resonant/tuning_block");

        // the resonance attuner: the machine itself, wetware-brained, wired
        // with the resonant superconductor and potted in silver epoxy
        r.machine("assembler", EU, T)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 2)
                .itemIn("modern_industrialization:resonite_plate", 2)
                .itemIn("modern_industrialization:resonant_superconductor_cable", 4)
                .itemIn(ni + "wetware_circuit", 1)
                .fluidIn(ni + "conductive_epoxy", 250)
                .itemOut("mi_nested_infinity:resonance_attuner", 1)
                .save("resonant/resonance_attuner");
    }

    /**
     * Resonant circuit program, part 6: the resonant processing units (each
     * one consumes an inverse-color pair of Q8 notes at 50% probability each
     * - green/blue RAM, cyan/red MMU, purple/yellow ALU, mirroring the bio
     * parts), the signature components (the black/white saser, the resonance
     * chamber, the phase-locked loop), the trinium_dinaquide coil, and the two
     * finals: the board and the circuit, both at the 8000 s climax of the
     * program.
     */
    private void resonantCircuitChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        String mi = "modern_industrialization:";
        int EU = 2_000_000_000;
        int T = 80_000;
        int FT = 160_000; // the 8000 s finals

        // -- resonant processing units on the note ladder (100/200/300 mB) ----
        // each consumes an inverse-color pair of notes (green/blue, cyan/red,
        // purple/yellow), both colors at 50% consumption probability

        r.machine("assembler", EU, T)
                .itemIn("mi_nested_infinity:note_green", 2, 0.5)
                .itemIn("mi_nested_infinity:note_blue", 2, 0.5)
                .itemIn(NIItems.BIO_RANDOM_ACCESS_MEMORY.getId().toString(), 1)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 1)
                .itemIn("modern_industrialization:resonite_plate", 1)
                .fluidIn(ni + "resonant_mother_liquor", 100)
                .itemOut(NIItems.RESONANT_RANDOM_ACCESS_MEMORY.getId().toString(), 1)
                .save("resonant/resonant_ram");
        r.machine("assembler", EU, T)
                .itemIn("mi_nested_infinity:note_cyan", 1, 0.5)
                .itemIn("mi_nested_infinity:note_red", 1, 0.5)
                .itemIn(NIItems.BIO_MEMORY_MANAGEMENT_UNIT.getId().toString(), 1)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 1)
                .itemIn("modern_industrialization:resonite_plate", 2)
                .fluidIn(ni + "resonant_mother_liquor", 200)
                .itemOut(NIItems.RESONANT_MEMORY_MANAGEMENT_UNIT.getId().toString(), 1)
                .save("resonant/resonant_mmu");
        r.machine("assembler", EU, T)
                .itemIn("mi_nested_infinity:note_purple", 1, 0.5)
                .itemIn("mi_nested_infinity:note_yellow", 1, 0.5)
                .itemIn(NIItems.BIO_ARITHMETIC_LOGIC_UNIT.getId().toString(), 1)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 2)
                .itemIn("modern_industrialization:resonite_plate", 2)
                .fluidIn(ni + "resonant_mother_liquor", 300)
                .itemOut(NIItems.RESONANT_ARITHMETIC_LOGIC_UNIT.getId().toString(), 1)
                .save("resonant/resonant_alu");

        // -- signature components ------------------------------------------------

        // the saser: coherent phonons, the sound laser (a black/white note pair
        // trapped between two SAW resonators, pumped by an elite pump; both
        // colors 50% consumption)
        r.machine("assembler", EU, T)
                .itemIn(NIItems.SAW_RESONATOR.getId().toString(), 2)
                .itemIn("mi_nested_infinity:note_black", 1, 0.5)
                .itemIn("mi_nested_infinity:note_white", 1, 0.5)
                .itemIn(NIItems.ELITE_PUMP.getId().toString(), 1)
                .fluidIn(ni + "conductive_epoxy", 100)
                .itemOut(NIItems.SASER.getId().toString(), 1)
                .save("resonant/saser");

        // the resonance chamber: a titanate/resonite cavity held at the black
        // note, superconductingly wired, gasketed with FKM (black/white pair,
        // both colors 50% consumption)
        r.machine("assembler", EU, T)
                .itemIn(NIItems.LEAD_TITANATE_PLATE.getId().toString(), 6)
                .itemIn("modern_industrialization:resonite_plate", 6)
                .itemIn("mi_nested_infinity:note_black", 2, 0.5)
                .itemIn("mi_nested_infinity:note_white", 2, 0.5)
                .itemIn("modern_industrialization:resonant_superconductor_cable", 8)
                .itemIn(NIItems.FLUOROELASTOMER_SHEET.getId().toString(), 2)
                .itemOut(NIItems.RESONANCE_CHAMBER.getId().toString(), 1)
                .save("resonant/resonance_chamber");

        // the phase-locked loop: twin oscillators on resonite wire
        r.machine("assembler", EU, T)
                .itemIn(NIItems.QUARTZ_OSCILLATOR.getId().toString(), 2)
                .itemIn("modern_industrialization:resonite_wire", 8)
                .itemIn(NIItems.FLUOROELASTOMER_SHEET.getId().toString(), 1)
                .fluidIn(ni + "conductive_epoxy", 100)
                .itemOut(NIItems.PHASE_LOCKED_LOOP.getId().toString(), 1)
                .save("resonant/phase_locked_loop");

        // -- the trinium_dinaquide coil: plates of the coil alloy itself (EBF-cast
        //    on the TPV coil tier, see the material registration) --
        r.machine("assembler", EU, T)
                .itemIn("modern_industrialization:trinium_dinaquadide_plate", 2)
                .itemIn(NIItems.SILICONE_MICA_INSULATOR_SHEET.getId().toString(), 6)
                .itemIn(ni + "wetware_circuit", 2)
                .itemOut(ni + "trinium_dinaquadide_coil", 1)
                .save("resonant/trinium_dinaquadide_coil");

        // -- the finals ----------------------------------------------------------

        // the resonant board: the wetware board re-laminated in polyimide
        // around the three signature components (all nine assembler item inputs)
        r.machine("assembler", EU, FT)
                .itemIn(ni + "wetware_circuit_board", 1)
                .itemIn(NIItems.POLYIMIDE_PLATE.getId().toString(), 4)
                .itemIn("modern_industrialization:resonant_superconductor_cable", 12)
                .itemIn(NIItems.SASER.getId().toString(), 1)
                .itemIn(NIItems.RESONANCE_CHAMBER.getId().toString(), 1)
                .itemIn(NIItems.PHASE_LOCKED_LOOP.getId().toString(), 1)
                .itemIn("modern_industrialization:resonite_plate", 6)
                .itemIn(mi + "plutonium_battery", 16)
                .itemIn(NIItems.ELITE_PUMP.getId().toString(), 2)
                .fluidIn(ni + "resonant_mother_liquor", 100)
                .fluidIn(mi + "helium_3", 500)
                .fluidIn(ni + "mutagen", 100)
                .itemOut(ni + "resonant_circuit_board", 1)
                .save("electric_age/circuit/assembler/resonant_circuit_board");

        // the resonant circuit: processing-unit layout one tier above wetware,
        // cased in the two fusion-born alloy plates
        r.machine("assembler", EU, FT)
                .itemIn(ni + "wetware_circuit", 4)
                .itemIn(NIItems.RESONANT_RANDOM_ACCESS_MEMORY.getId().toString(), 2)
                .itemIn(NIItems.RESONANT_MEMORY_MANAGEMENT_UNIT.getId().toString(), 1)
                .itemIn(NIItems.RESONANT_ARITHMETIC_LOGIC_UNIT.getId().toString(), 1)
                .itemIn(ni + "resonant_circuit_board", 1)
                .itemIn(NIItems.ADAMANTIUM_PLATE.getId().toString(), 8)
                .itemIn(NIItems.MITHRIL_PLATE.getId().toString(), 8)
                .itemOut(ni + "resonant_circuit", 1)
                .save("electric_age/circuit/assembler/resonant_circuit");
    }

    // -- optical program: the hundred-gem collection ---------------------------

    /**
     * The optical program: a hundred real gemstones, each grown in the algae
     * cultivator from glass, the petri dish of the gem's hue and the noble gas
     * of its glow color (probabilistic output, like a chemical yield). Nine
     * gems compress into a storage block, the cutting machine slices that
     * block into nine plates, and each plate becomes one glow tube in the
     * assembler (transuranic RTG battery, crystal diode, two graphene
     * electrodes, its noble gas, molten trinium). The hundred distinct tubes
     * finally merge in the super assembler's 10x10 grid into the optical
     * qubit component - that last craft lives in
     * {@code SuperAssemblerBlockEntity}, beyond the JSON recipe system.
     */
    private void opticalGemChain(NIRecipes r) {
        String ni = "mi_nested_infinity:";
        int EU = 2_000_000_000;
        int T = 80_000;

        // the transuranic RTG battery: three so-far-unused heavy elements in a
        // naquadah casing (Am/Cm are real radioisotope-battery fuels, Np-237
        // breeds battery-grade Pu-238)
        r.machine("assembler", EU, T)
                .itemIn("modern_industrialization:naquadah_plate", 16)
                .itemIn(ni + "americium_dust", 24)
                .itemIn(ni + "curium_dust", 16)
                .itemIn(ni + "neptunium_dust", 12)
                .itemOut(ni + "transuranic_battery", 1)
                .save("optical/transuranic_battery");
        // crystal diode and graphene electrodes, the tube's active parts
        r.machine("assembler", EU, T)
                .itemIn("modern_industrialization:silicon_wafer", 2)
                .itemIn("modern_industrialization:copper_plate", 1)
                .itemOut(ni + "crystal_diode", 1)
                .save("optical/crystal_diode");
        r.machine("assembler", EU, T)
                .itemIn("modern_industrialization:carbon_dust", 4)
                .itemIn("modern_industrialization:copper_plate", 1)
                .itemOut(ni + "graphene_electrode", 2)
                .save("optical/graphene_electrode");

        // the graphene chemical route: Brodie-style nitric-acid intercalation
        // oxidizes graphite into graphene oxide, hydrazine strips the oxygen
        // back down to graphene, and the packer presses the flakes into rods
        r.machine("chemical_reactor", EU, T)
                .itemIn("modern_industrialization:carbon_dust", 2)
                .fluidIn(ni + "nitric_acid", 250)
                .itemOut(ni + "graphene_oxide", 4)
                .save("optical/graphene_oxide");
        r.machine("chemical_reactor", EU, T)
                .itemIn(ni + "graphene_oxide", 2)
                .itemIn(ni + "hydrazine", 1)
                .itemOut(ni + "graphene", 2)
                .save("optical/graphene");
        r.machine("packer", EU, T)
                .itemIn(ni + "graphene", 4)
                .itemOut(ni + "graphene_rod", 1)
                .save("optical/graphene_rod");

        // xenon condensed to the liquid phase in the vacuum freezer: the tubes
        // are flooded with 16 buckets of it each
        r.machine("vacuum_freezer", 4096, 400)
                .fluidIn(ni + "xenon", 1000)
                .fluidOut(ni + "liquid_xenon", 1000)
                .save("optical/liquid_xenon");

        // gems are grown in groups: every gem sharing one petri dish AND one
        // noble gas shares a single cultivator recipe whose outputs all roll
        // independently, probabilities scaled so the expected yield per craft
        // (half a gem) is unchanged
        record CultivationGroup(String dish, String gas, List<NIGems.Gem> gems) {}
        Map<String, CultivationGroup> groups = new LinkedHashMap<>();
        for (NIGems.Gem gem : NIGems.ALL) {
            String gas = gasFor(gem.rgb());
            String dish = dishFor(gem.rgb());
            groups.computeIfAbsent(dish + "|" + gas,
                    key -> new CultivationGroup(dish, gas, new ArrayList<>())).gems().add(gem);
        }
        for (CultivationGroup group : groups.values()) {
            double probability = 0.5 / group.gems().size();
            NIRecipes.MachineBuilder builder = r.machine("algae_cultivator", 8, 200)
                    .itemIn("minecraft:glass", 1)
                    .itemIn(group.dish(), 1)
                    .fluidIn(group.gas(), 50);
            for (NIGems.Gem gem : group.gems()) {
                builder.itemOut(NIGems.gemId(gem), 1, probability);
            }
            builder.save("optical/gem_" + group.gems().get(0).name());
        }
        for (NIGems.Gem gem : NIGems.ALL) {
            r.machine("compressor", 2, 200)
                    .itemIn(NIGems.gemId(gem), 9)
                    .itemOut(NIGems.blockId(gem), 1)
                    .save("optical/block_" + gem.name());
            r.machine("cutting_machine", 2, 200)
                    .itemIn(NIGems.blockId(gem), 1)
                    .fluidIn("modern_industrialization:lubricant", 1)
                    .itemOut(NIGems.plateId(gem), 9)
                    .save("optical/plate_" + gem.name());
            r.machine("assembler", EU, T)
                    .itemIn(NIGems.plateId(gem), 1)
                    .itemIn(ni + "transuranic_battery", 1)
                    .itemIn(ni + "crystal_diode", 1)
                    .itemIn(ni + "graphene_electrode", 2)
                    .itemIn(ni + "graphene_rod", 4)
                    .fluidIn(gasFor(gem.rgb()), 100)
                    .fluidIn(ni + "liquid_xenon", 16_000)
                    .fluidIn(ni + "molten_trinium", 50)
                    .itemOut(NIGems.tubeId(gem), 1)
                    .save("optical/tube_" + gem.name());
        }
    }

    /** Discharge colors of the six noble gases (helium peach, neon red-orange,
     * argon lavender, krypton ice-blue, xenon and radon per their fluids). */
    private static final int[][] NOBLE_GLOWS = {
            {255, 200, 130}, {255, 95, 66}, {170, 140, 255}, {150, 200, 255}, {127, 184, 196}, {200, 184, 232},
    };
    private static final String[] NOBLE_FLUIDS = {
            "modern_industrialization:helium",
            "mi_nested_infinity:neon",
            "mi_nested_infinity:argon",
            "mi_nested_infinity:krypton",
            "mi_nested_infinity:xenon",
            "mi_nested_infinity:radon",
    };
    /** Canonical colors of the twelve algae-wheel slots (ordinal order);
     * the mixture of a multi-strain dish is the average of its members. */
    private static final int[][] WHEEL_COLORS = {
            {220, 45, 55}, {240, 140, 45}, {240, 220, 80}, {160, 215, 80}, {60, 200, 110}, {75, 200, 165},
            {80, 215, 215}, {95, 165, 230}, {55, 95, 210}, {145, 95, 200}, {200, 65, 170}, {240, 145, 175},
    };

    /**
     * The noble gas whose discharge color lies nearest the given rgb. Hue
     * dominates the metric (weighted circular hue distance plus saturation
     * and value deltas), which spreads the six gases across the wheel
     * instead of letting xenon's central glow swallow the whole blue side;
     * near-achromatic gems fall back to plain RGB distance where hue is
     * meaningless.
     */
    private static String gasFor(int rgb) {
        float[] hsv = toHsv(rgb);
        if (hsv[1] < 0.12f) {
            return NOBLE_FLUIDS[nearest(rgb, NOBLE_GLOWS)];
        }
        int best = 0;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < NOBLE_GLOWS.length; i++) {
            float[] glow = toHsv((NOBLE_GLOWS[i][0] << 16) | (NOBLE_GLOWS[i][1] << 8) | NOBLE_GLOWS[i][2]);
            double hueDelta = Math.abs(hsv[0] - glow[0]);
            hueDelta = Math.min(hueDelta, 1 - hueDelta);
            double distance = 24 * hueDelta * hueDelta
                    + 0.25 * ((hsv[1] - glow[1]) * (hsv[1] - glow[1])
                    + (hsv[2] - glow[2]) * (hsv[2] - glow[2]));
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return NOBLE_FLUIDS[best];
    }

    /** {hue turns 0..1, saturation, value} of a packed rgb. */
    private static float[] toHsv(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        float delta = max - min;
        float hue;
        if (delta == 0) {
            hue = 0;
        } else if (max == red) {
            hue = ((green - blue) / delta) % 6;
        } else if (max == green) {
            hue = (blue - red) / delta + 2;
        } else {
            hue = (red - green) / delta + 4;
        }
        if (hue < 0) {
            hue += 6;
        }
        return new float[] {hue / 6, max == 0 ? 0 : delta / max, max / 255f};
    }

    /**
     * The petri dish a gem color grows on: of all 97 dishes, the one whose
     * culture color — the average of the member wheel colors, i.e. the exact
     * wheel color for a single strain — lies nearest. Vivid gems naturally
     * land on their single-strain dish, while muted stones (jaspers, smoky
     * quartz, grays) match the muddy mixtures only a multi-strain culture
     * can produce, so part of the collection sits on blended dishes.
     */
    private static String dishFor(int rgb) {
        NIPetriDishes.PetriDish best = null;
        long bestDistance = Long.MAX_VALUE;
        for (NIPetriDishes.PetriDish dish : NIPetriDishes.ALL) {
            long red = 0;
            long green = 0;
            long blue = 0;
            for (NIAlgae alga : dish.algae()) {
                int[] color = WHEEL_COLORS[alga.ordinal()];
                red += color[0];
                green += color[1];
                blue += color[2];
            }
            int size = dish.algae().size();
            long dr = (rgb >> 16 & 0xFF) - red / size;
            long dg = (rgb >> 8 & 0xFF) - green / size;
            long db = (rgb & 0xFF) - blue / size;
            long distance = dr * dr + dg * dg + db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = dish;
            }
        }
        return best.item().getId().toString();
    }

    private static int nearest(int rgb, int[][] palette) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        int best = 0;
        long bestDistance = Long.MAX_VALUE;
        for (int i = 0; i < palette.length; i++) {
            long dr = red - palette[i][0];
            long dg = green - palette[i][1];
            long db = blue - palette[i][2];
            long distance = dr * dr + dg * dg + db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }
}
