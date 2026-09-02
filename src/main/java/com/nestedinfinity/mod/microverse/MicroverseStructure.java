package com.nestedinfinity.mod.microverse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * The projector's 7x3x7 pattern, centered on the controller (spec doc
 * section 2). The shape is mirror- and rotation-symmetric, so the
 * controller's facing is irrelevant. Layer rows are indexed 0..6 north to
 * south; each row is a column range inside the 7-wide grid.
 */
public final class MicroverseStructure {
    /** Hexagon layer rows (layers 1 and 3): first and last column per row, inclusive. */
    private static final int[][] HEX_ROWS = {
            {2, 4}, {1, 5}, {0, 6}, {0, 6}, {0, 6}, {1, 5}, {2, 4}
    };

    /** The 12 coreflame positions on layer 2, as {row, col}. */
    public static final int[][] COREFLAME_POS = {
            {0, 2}, {0, 3}, {0, 4},
            {2, 0}, {2, 6}, {3, 0}, {3, 6}, {4, 0}, {4, 6},
            {6, 2}, {6, 3}, {6, 4}
    };

    /** The 4 time dilation unit positions on layer 2. */
    public static final int[][] TDU_POS = {
            {1, 1}, {1, 5}, {5, 1}, {5, 5}
    };

    /** Layer-3 pillar cells directly above the TDUs: forced machine casing, no hatches. */
    public static final int[][] PILLAR_POS = TDU_POS;

    private static boolean inHex(int row, int col) {
        int[] range = HEX_ROWS[row];
        return col >= range[0] && col <= range[1];
    }

    /** Is this block an acceptable stand-in for a casing (casing itself, or an MI hatch)? */
    public static boolean isCasingLike(BlockState state) {
        if (state.is(MicroverseBlocks.NEUTRONIUM_MACHINE_CASING.get())) {
            return true;
        }
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getNamespace().equals("modern_industrialization") && id.getPath().endsWith("_hatch");
    }

    /** Only the neutronium machine casing itself, for the four forced positions. */
    public static boolean isForcedCasing(BlockState state) {
        return state.is(MicroverseBlocks.NEUTRONIUM_MACHINE_CASING.get());
    }

    /**
     * Classifies a casing-like cell: MI energy input, item input and item
     * output hatches are collected so the controller can drink EU, auto-spend
     * giant matter balls and push the universe matter out.
     */
    private static void collectHatch(BlockState state, BlockPos pos,
            List<BlockPos> energyHatches, List<BlockPos> itemInputHatches,
            List<BlockPos> itemOutputHatches) {
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals("modern_industrialization") || !id.getPath().endsWith("_hatch")) {
            return;
        }
        String path = id.getPath();
        if (path.contains("energy_input")) {
            energyHatches.add(pos.immutable());
        } else if (path.contains("item_input")) {
            itemInputHatches.add(pos.immutable());
        } else if (path.contains("item_output")) {
            itemOutputHatches.add(pos.immutable());
        }
    }

    public static final class Result {
        public final boolean valid;
        /** 1..9 when valid, 0 otherwise. */
        public final int tduTier;
        /** Bit i set == the coreflame at COREFLAME_POS[i] holds its singularity. */
        public final int flameMask;
        /** First problem found, for the GUI; null when valid. */
        public final String problem;
        /** Energy input hatches in the casing (empty when invalid). */
        public final List<BlockPos> energyHatches;
        /** Item input hatches in the casing (empty when invalid). */
        public final List<BlockPos> itemInputHatches;
        /** Item output hatches in the casing (empty when invalid). */
        public final List<BlockPos> itemOutputHatches;

        private Result(boolean valid, int tduTier, int flameMask, String problem) {
            this(valid, tduTier, flameMask, problem, List.of(), List.of(), List.of());
        }

        private Result(boolean valid, int tduTier, int flameMask, String problem,
                List<BlockPos> energyHatches, List<BlockPos> itemInputHatches,
                List<BlockPos> itemOutputHatches) {
            this.valid = valid;
            this.tduTier = tduTier;
            this.flameMask = flameMask;
            this.problem = problem;
            this.energyHatches = energyHatches;
            this.itemInputHatches = itemInputHatches;
            this.itemOutputHatches = itemOutputHatches;
        }
    }

    /**
     * Validates the full structure around the controller at {@code pos}
     * (controller = center of layer 3; layers 2 and 1 lie below it).
     */
    public static Result validate(Level level, BlockPos pos) {
        BlockPos layer2 = pos.below();
        BlockPos layer1 = pos.below(2);

        // layer 3: almost fully open — just the controller and four casing
        // pillars standing on the TDUs; every other hex cell must be air
        for (int row = 0; row < 7; row++) {
            for (int col = HEX_ROWS[row][0]; col <= HEX_ROWS[row][1]; col++) {
                if (row == 3 && col == 3) {
                    continue; // the controller itself
                }
                BlockPos p = pos.offset(col - 3, 0, row - 3);
                BlockState state = level.getBlockState(p);
                if (isPillar(row, col) ? !isForcedCasing(state) : !state.isAir()) {
                    return new Result(false, 0, 0, "layer3");
                }
            }
        }

        // layer 2: 12 coreflames + 4 TDUs + center 3x3 casing
        int tier = -1;
        List<BlockPos> energyHatches = new ArrayList<>();
        List<BlockPos> itemInputHatches = new ArrayList<>();
        List<BlockPos> itemOutputHatches = new ArrayList<>();
        for (int[] rc : TDU_POS) {
            BlockPos p = layer2.offset(rc[1] - 3, 0, rc[0] - 3);
            int t = TimeDilationUnitBlock.tierOf(level.getBlockState(p).getBlock());
            if (t == 0) {
                return new Result(false, 0, 0, "tdu_missing");
            }
            if (tier == -1) {
                tier = t;
            } else if (tier != t) {
                return new Result(false, 0, 0, "tdu_mixed");
            }
        }
        boolean[] seen = new boolean[MicroverseBlocks.COREFLAMES.size()];
        for (int[] rc : COREFLAME_POS) {
            BlockPos p = layer2.offset(rc[1] - 3, 0, rc[0] - 3);
            int index = MicroverseBlocks.coreflameIndex(level.getBlockState(p).getBlock());
            if (index < 0) {
                return new Result(false, tier, 0, "coreflame_missing");
            }
            if (seen[index]) {
                return new Result(false, tier, 0, "coreflame_duplicate");
            }
            seen[index] = true;
        }
        for (int row = 2; row <= 4; row++) {
            for (int col = 2; col <= 4; col++) {
                BlockState state = level.getBlockState(layer2.offset(col - 3, 0, row - 3));
                if (!isCasingLike(state)) {
                    return new Result(false, tier, 0, "layer2_center");
                }
                collectHatch(state, layer2.offset(col - 3, 0, row - 3),
                        energyHatches, itemInputHatches, itemOutputHatches);
            }
        }

        // layer 1: plain hexagon of casing-like cells
        for (int row = 0; row < 7; row++) {
            for (int col = HEX_ROWS[row][0]; col <= HEX_ROWS[row][1]; col++) {
                BlockState state = level.getBlockState(layer1.offset(col - 3, 0, row - 3));
                if (!isCasingLike(state)) {
                    return new Result(false, tier, 0, "layer1");
                }
                collectHatch(state, layer1.offset(col - 3, 0, row - 3),
                        energyHatches, itemInputHatches, itemOutputHatches);
            }
        }

        int mask = 0;
        for (int i = 0; i < COREFLAME_POS.length; i++) {
            int[] rc = COREFLAME_POS[i];
            BlockPos p = layer2.offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame && flame.isFilled()) {
                mask |= 1 << i;
            }
        }
        return new Result(true, tier, mask, null, energyHatches, itemInputHatches, itemOutputHatches);
    }

    private static boolean isPillar(int row, int col) {
        for (int[] rc : PILLAR_POS) {
            if (rc[0] == row && rc[1] == col) {
                return true;
            }
        }
        return false;
    }

    /** World position of a structure cell given the controller position and layer height (0..2). */
    public static BlockPos cell(BlockPos controller, int layerRow, int layerCol, int layer) {
        return controller.below(2 - layer).offset(layerCol - 3, 0, layerRow - 3);
    }

    private MicroverseStructure() {}
}
