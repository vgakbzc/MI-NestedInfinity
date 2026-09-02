package com.nestedinfinity.mod.items.gems;

import com.nestedinfinity.mod.NestedInfinity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The hundred-gem collection of the optical program: one item family per real
 * gemstone, each with its own characteristic color (mirrored by
 * {@code tools/gen_resonant_assets.py}, which paints the textures and lang
 * from the same table). Every gem runs through the same chain: the algae
 * cultivator grows it (glass + the petri dish of its hue + the noble gas of
 * its glow color, probabilistic output like a chemical yield), the compressor
 * squeezes nine gems into a storage block, the cutting machine slices that
 * block into nine plates, and the assembler builds one glow tube per color.
 *
 * <p>The 100 distinct glow tubes finally merge in the super assembler's
 * 10x10 grid into the optical qubit component.
 */
public final class NIGems {

    /** One gemstone family; rgb drives the petri-dish/noble-gas mapping in the recipe provider. */
    public record Gem(String name, int rgb,
            DeferredItem<Item> gem, DeferredBlock<Block> block, DeferredItem<BlockItem> blockItem,
            DeferredItem<Item> plate, DeferredItem<Item> tube) {}

    public static final List<Gem> ALL = buildAll();

    private static List<Gem> buildAll() {
        String[][] table = {
            // reds
            {"ruby", "171,48,61"}, {"garnet", "102,20,27"}, {"spinel", "128,25,40"},
            {"carnelian", "120,48,34"}, {"bloodstone", "191,62,53"}, {"cuprite", "171,46,34"},
            {"crocoite", "135,23,16"}, {"eudialyte", "158,19,32"}, {"rhodochrosite", "188,23,51"},
            // pinks
            {"morganite", "189,38,81"}, {"kunzite", "140,39,106"}, {"rose_quartz", "128,25,66"},
            {"rhodonite", "140,0,31"}, {"thulite", "69,20,33"}, {"poudretteite", "150,18,58"},
            // oranges
            {"sunstone", "158,85,19"}, {"amber", "171,127,48"}, {"fire_opal", "158,79,44"},
            {"hessonite", "189,96,38"}, {"vanadinite", "135,58,16"}, {"sphalerite", "161,106,31"},
            {"imperial_topaz", "120,73,34"}, {"citrine", "176,134,21"},
            // yellows
            {"topaz", "126,128,25"}, {"heliodor", "189,173,53"}, {"sulfur", "176,162,21"},
            {"sphene", "135,109,38"}, {"zircon", "191,148,53"}, {"scapolite", "107,85,13"},
            {"tiger_eye", "219,169,44"}, {"cassiterite", "135,98,16"},
            // greens
            {"emerald", "20,102,50"}, {"peridot", "76,117,14"}, {"jade", "16,135,96"},
            {"malachite", "49,176,121"}, {"chrysoprase", "68,161,31"}, {"aventurine", "33,165,99"},
            {"diopside", "39,140,76"}, {"serpentine", "87,189,53"}, {"prasiolite", "63,140,17"},
            {"prehnite", "57,102,29"}, {"variscite", "15,128,68"}, {"brazilianite", "128,158,44"},
            {"epidote", "96,171,34"}, {"demantoid", "19,158,26"},
            // teals and cyans
            {"aquamarine", "38,189,168"}, {"turquoise", "34,120,109"}, {"chrysocolla", "17,140,140"},
            {"larimar", "20,86,102"}, {"apatite", "53,171,189"}, {"fluorite", "31,146,161"},
            {"amazonite", "12,99,82"}, {"hemimorphite", "23,166,191"}, {"alexandrite", "49,176,146"},
            // azures and blues
            {"sapphire", "39,65,140"}, {"azurite", "29,60,102"}, {"lapis", "23,74,191"},
            {"benitoite", "15,60,122"}, {"kyanite", "31,78,161"}, {"iolite", "17,46,146"},
            {"sodalite", "16,35,84"}, {"tanzanite", "23,30,117"}, {"lazulite", "57,103,204"},
            {"celestine", "38,108,189"}, {"grandidierite", "44,120,158"}, {"jeremejevite", "16,85,135"},
            // purples
            {"amethyst", "96,34,171"}, {"charoite", "74,34,120"}, {"sugilite", "115,19,158"},
            {"taaffeite", "105,26,135"}, {"lepidolite", "126,48,171"}, {"purpurite", "50,16,80"},
            {"axinite", "57,14,117"}, {"afghanite", "60,44,158"}, {"stichtite", "79,17,140"},
            // magentas
            {"rubellite", "135,16,95"}, {"cobaltoan_calcite", "176,49,136"},
            {"pezzottaite", "171,34,116"}, {"bixbite", "102,29,52"},
            // earths
            {"jasper", "212,167,153"}, {"unakite", "135,114,81"}, {"smoky_quartz", "100,80,66"},
            {"sinhalite", "204,199,147"}, {"aragonite", "173,150,104"}, {"dolomite", "153,111,101"},
            {"staurolite", "115,94,82"}, {"chromite", "176,123,105"}, {"painite", "204,138,135"},
            // grays, blacks, whites, metallics
            {"onyx", "32,32,36"}, {"hematite", "139,127,125"}, {"magnetite", "55,55,62"},
            {"galena", "102,106,113"}, {"pyrite", "158,149,44"}, {"marcasite", "153,157,165"},
            {"moonstone", "203,206,216"}, {"opal", "172,179,191"}, {"pearl", "242,237,232"},
            {"labradorite", "19,99,158"}, {"rutile", "84,60,16"}, {"molybdenite", "80,84,87"},
        };
        List<Gem> gems = new ArrayList<>(table.length);
        for (String[] row : table) {
            String name = row[0];
            String[] parts = row[1].split(",");
            int rgb = (Integer.parseInt(parts[0].strip()) << 16)
                    | (Integer.parseInt(parts[1].strip()) << 8)
                    | Integer.parseInt(parts[2].strip());
            DeferredItem<Item> gem = NestedInfinity.ITEMS.registerSimpleItem("gem_" + name, new Item.Properties());
            DeferredBlock<Block> block = NestedInfinity.BLOCKS.register(name + "_block",
                    () -> new Block(BlockBehaviour.Properties.of().strength(5.0F, 6.0F)));
            DeferredItem<BlockItem> blockItem = NestedInfinity.ITEMS.registerSimpleBlockItem(name + "_block", block);
            DeferredItem<Item> plate = NestedInfinity.ITEMS.registerSimpleItem(name + "_plate", new Item.Properties());
            DeferredItem<Item> tube = NestedInfinity.ITEMS.registerSimpleItem(name + "_glow_tube", new Item.Properties());
            gems.add(new Gem(name, rgb, gem, block, blockItem, plate, tube));
        }
        return List.copyOf(gems);
    }

    /** Full item id of the gem's raw gem item, e.g. {@code mi_nested_infinity:gem_ruby}. */
    public static String gemId(Gem gem) {
        return NestedInfinity.MODID + ":gem_" + gem.name();
    }

    public static String blockId(Gem gem) {
        return NestedInfinity.MODID + ":" + gem.name() + "_block";
    }

    public static String plateId(Gem gem) {
        return NestedInfinity.MODID + ":" + gem.name() + "_plate";
    }

    public static String tubeId(Gem gem) {
        return NestedInfinity.MODID + ":" + gem.name() + "_glow_tube";
    }

    /** The gem whose glow tube this item is, or null. */
    public static Gem byTubeItem(Item item) {
        for (Gem gem : ALL) {
            if (gem.tube().get() == item) {
                return gem;
            }
        }
        return null;
    }

    public static void init() {
        if (ALL.size() != 100) {
            throw new IllegalStateException("Expected exactly 100 gems, found " + ALL.size());
        }
    }

    private NIGems() {}
}
