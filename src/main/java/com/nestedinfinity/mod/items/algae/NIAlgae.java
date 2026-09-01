package com.nestedinfinity.mod.items.algae;
import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.fluids.NIFluids;

/**
 * The 12 color-wheel algae, one per 30-degree hue step: red, orange, yellow, lime, green,
 * teal, cyan, azure, blue, purple, magenta, pink (in ordinal == wheel order).
 *
 * <p>Each strain is named after its color with a Greek/Latin root plus the algae-division
 * suffix {@code -phyta} (several of them, e.g. Cyanophyta or Rhodophyta, are real taxa).
 * Concatenating the roots names the compound petri dishes ({@link NIPetriDishes}), e.g.
 * red + cyan = Erythrocyanophyta.
 *
 * <p>This table is mirrored by {@code tools/gen_algae_assets.py}, which generates the
 * models, textures and lang entries from the same roots and tints.
 */
public enum NIAlgae {
    ERYTHROPHYTA("erythro", NIFluids.ERYTHROPHYTA),
    AURANTIOPHYTA("aurantio", NIFluids.AURANTIOPHYTA),
    XANTHOPHYTA("xantho", NIFluids.XANTHOPHYTA),
    PRASINOPHYTA("prasino", NIFluids.PRASINOPHYTA),
    CHLOROPHYTA("chloro", NIFluids.CHLOROPHYTA),
    GLAUCOPHYTA("glauco", NIFluids.GLAUCOPHYTA),
    CYANOPHYTA("cyano", NIFluids.CYANOPHYTA),
    AZUREOPHYTA("azuro", NIFluids.AZUREOPHYTA),
    CAERULEOPHYTA("caeruleo", NIFluids.CAERULEOPHYTA),
    PURPUREOPHYTA("purpureo", NIFluids.PURPUREOPHYTA),
    MAGENTOPHYTA("magento", NIFluids.MAGENTOPHYTA),
    RHODOPHYTA("rhodo", NIFluids.RHODOPHYTA);

    /** Number of strains on the color wheel; ordinal distance is wheel distance. */
    public static final int WHEEL_SIZE = values().length;

    /** Minimum circular wheel distance between two strains sharing a petri dish. */
    public static final int MIN_SEPARATION = 3;

    private final String root;
    private final NIFluids.Entry fluid;

    NIAlgae(String root, NIFluids.Entry fluid) {
        this.root = root;
        this.fluid = fluid;
    }

    /** Compound root; the standalone taxon name is {@code root() + "phyta"}. */
    public String root() {
        return root;
    }

    public String taxon() {
        return root + "phyta";
    }

    public NIFluids.Entry fluid() {
        return fluid;
    }

    /** Translation key of the strain's fluid display name. */
    public String fluidKey() {
        return "fluid." + NestedInfinity.MODID + "." + taxon();
    }
}
