package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.items.resonance.NINotes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * The tuning block placed directly above a resonance attuner: an eight-state
 * "group register" whose {@code color} property holds one of the eight Q8
 * note colors (the property value equals the {@link NINotes} ordinal, white —
 * the group identity — when placed). The attuner reads this state, multiplies
 * it with the inserted note, and writes the note's own color back.
 *
 * <p>Breaking and re-placing resets the register to white; inserting a white
 * note reads the register out (output = current color) and resets it too.
 */
public class TuningBlock extends Block {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, NINotes.GROUP_SIZE - 1);

    public TuningBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(COLOR, NINotes.WHITE.ordinal()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    /** The note color this register currently holds. */
    public static NINotes color(BlockState state) {
        return NINotes.values()[state.getValue(COLOR)];
    }

    /** The same block state with the register set to {@code note}'s color. */
    public static BlockState colored(BlockState state, NINotes note) {
        return state.setValue(COLOR, note.ordinal());
    }
}
