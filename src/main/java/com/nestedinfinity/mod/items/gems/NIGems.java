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
            {"ruby", "210,25,45"}, {"garnet", "150,25,35"}, {"spinel", "230,85,105"},
            {"carnelian", "195,75,50"}, {"bloodstone", "140,60,55"}, {"cuprite", "185,80,70"},
            {"crocoite", "235,75,65"}, {"eudialyte", "185,80,90"}, {"rhodochrosite", "235,120,140"},
            // pinks
            {"morganite", "250,180,200"}, {"kunzite", "245,185,225"}, {"rose_quartz", "235,150,165"},
            {"rhodonite", "225,135,155"}, {"thulite", "215,125,150"}, {"poudretteite", "255,155,185"},
            // oranges
            {"sunstone", "250,160,80"}, {"amber", "255,185,60"}, {"fire_opal", "255,120,60"},
            {"hessonite", "215,120,60"}, {"vanadinite", "230,125,70"}, {"sphalerite", "195,140,65"},
            {"imperial_topaz", "250,140,50"}, {"citrine", "240,195,75"},
            // yellows
            {"topaz", "235,205,80"}, {"heliodor", "240,225,110"}, {"sulfur", "245,230,70"},
            {"sphene", "230,190,85"}, {"zircon", "215,185,120"}, {"scapolite", "235,215,150"},
            {"tiger_eye", "200,160,60"}, {"cassiterite", "175,150,95"},
            // greens
            {"emerald", "60,200,110"}, {"peridot", "150,210,60"}, {"jade", "0,165,110"},
            {"malachite", "35,185,120"}, {"chrysoprase", "140,205,115"}, {"aventurine", "60,170,115"},
            {"diopside", "70,180,110"}, {"serpentine", "105,165,85"}, {"prasiolite", "170,220,140"},
            {"prehnite", "175,215,150"}, {"variscite", "110,195,150"}, {"brazilianite", "190,220,105"},
            {"epidote", "120,175,75"}, {"demantoid", "85,190,90"},
            // teals and cyans
            {"aquamarine", "110,220,205"}, {"turquoise", "60,215,195"}, {"chrysocolla", "55,200,200"},
            {"larimar", "100,205,230"}, {"apatite", "95,195,210"}, {"fluorite", "150,225,235"},
            {"amazonite", "110,210,190"}, {"hemimorphite", "160,215,225"}, {"alexandrite", "95,180,160"},
            // azures and blues
            {"sapphire", "35,75,190"}, {"azurite", "45,110,195"}, {"lapis", "35,95,235"},
            {"benitoite", "75,145,240"}, {"kyanite", "65,115,205"}, {"iolite", "105,125,195"},
            {"sodalite", "65,95,175"}, {"tanzanite", "80,90,225"}, {"lazulite", "55,105,215"},
            {"celestine", "150,190,235"}, {"grandidierite", "95,175,215"}, {"jeremejevite", "140,195,235"},
            // purples
            {"amethyst", "150,100,210"}, {"charoite", "145,105,190"}, {"sugilite", "125,70,150"},
            {"taaffeite", "205,155,225"}, {"lepidolite", "185,150,205"}, {"purpurite", "130,95,160"},
            {"axinite", "150,125,185"}, {"afghanite", "110,95,205"}, {"stichtite", "155,120,190"},
            // magentas
            {"rubellite", "215,55,160"}, {"cobaltoan_calcite", "235,110,195"},
            {"pezzottaite", "245,95,185"}, {"bixbite", "210,35,90"},
            // earths
            {"jasper", "150,90,70"}, {"unakite", "170,145,105"}, {"smoky_quartz", "130,115,105"},
            {"sinhalite", "190,185,130"}, {"aragonite", "240,225,195"}, {"dolomite", "232,210,205"},
            {"staurolite", "150,105,80"}, {"chromite", "95,80,75"}, {"painite", "200,95,90"},
            // grays, blacks, whites, metallics
            {"onyx", "40,40,46"}, {"hematite", "140,110,105"}, {"magnetite", "85,85,95"},
            {"galena", "120,125,135"}, {"pyrite", "205,175,60"}, {"marcasite", "190,195,205"},
            {"moonstone", "225,228,240"}, {"opal", "205,215,230"}, {"pearl", "245,240,235"},
            {"labradorite", "85,140,180"}, {"rutile", "185,155,100"}, {"molybdenite", "110,115,120"},
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
