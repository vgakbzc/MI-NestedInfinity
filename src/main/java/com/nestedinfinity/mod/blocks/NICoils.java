package com.nestedinfinity.mod.blocks;
import com.nestedinfinity.mod.NestedInfinity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public final class NICoils {

    private static final String[] coilTierName = Arrays.stream(new String[]{
        "nichrome", "tpv", "trinium_dinaquadide", "neutronium", "infinitium",
        "hypogen", "stellarium", "draconicic_prismarinium_diaquamide", "eternium", "terminium"
    }).map(s -> (s + "_coil")).toArray(String[]::new);

    public record CoilTier(String coilPath, long eu) {}

    public static final List<CoilTier> TIERS = IntStream.range(0, coilTierName.length).mapToObj(i -> {
        return new CoilTier(coilTierName[i], 1L << (i*3+9));
    }).toList();

    public static final List<Long> TIER_EUS = TIERS.stream().map(CoilTier::eu).toList();
    public static final List<Component> TIER_DISPLAY_NAMES = TIERS.stream()
        .<Component>map(t -> Component.translatable("ebf_tier.modern_industrialization." + t.coilPath()))
        .toList();

    public static final List<DeferredItem<BlockItem>> ALL = TIERS.stream().map(coilTier -> {
        return register(coilTier.coilPath(), MapColor.TERRACOTTA_BLUE);
    }).toList();

    private static DeferredItem<BlockItem> register(String name, MapColor color) {
        DeferredBlock<Block> block = NestedInfinity.BLOCKS.register(name,
                () -> new Block(BlockBehaviour.Properties.of().mapColor(color).strength(6.0F, 8.0F).requiresCorrectToolForDrops()));
        return NestedInfinity.ITEMS.registerSimpleBlockItem(name, block);
    }

    public static void init() {}

    private NICoils() {}
}
