package com.nestedinfinity.mod.microverse;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A time dilation unit tier block (tiers 1..9). The tier lives in the block
 * instance, mirroring the coil tier pattern; the four units in a projector
 * structure must all be the same tier (see {@link MicroverseStructure}).
 */
public class TimeDilationUnitBlock extends Block {
    private static final Map<Block, Integer> TIERS = new HashMap<>();

    private final int tier;

    public TimeDilationUnitBlock(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
        TIERS.put(this, tier);
    }

    /** The tier of this block (1..9), or 0 if it is not a time dilation unit. */
    public static int tierOf(Block block) {
        return TIERS.getOrDefault(block, 0);
    }
}
