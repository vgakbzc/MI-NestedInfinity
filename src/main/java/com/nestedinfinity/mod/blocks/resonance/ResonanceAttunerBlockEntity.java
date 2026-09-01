package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.resonance.NINotes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The resonance attuner's brain: a two-slot note processor implementing the
 * Q8 tuning mechanic. With a tuning block ({@link TuningBlock}) placed
 * directly above, every note that enters the input slot makes the machine
 * output one note of color {@code register × note} (quaternion product, see
 * {@link NINotes#times}) and then adopt the inserted note's color as the new
 * register state. Deterministic: no randomness, no wear; the only friction is
 * planning the sequence, since the register changes with every note.
 *
 * <p>Interacts with vanilla hoppers through {@link WorldlyContainer}: notes go
 * in from any top/side face (input slot), products come out of the bottom
 * (output slot). Players can also right-click the machine with a note in hand.
 * The machine idles — without consuming anything — when the tuning block is
 * missing or the output slot cannot accept the product (standard
 * "output full" behavior; the input note stays put).
 */
public class ResonanceAttunerBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private ItemStack input = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;

    public ResonanceAttunerBlockEntity(BlockPos pos, BlockState state) {
        super(NIBlocks.RESONANCE_ATTUNER_TYPE.get(), pos, state);
    }

    // -- the Q8 step ------------------------------------------------------------

    /**
     * Attempts one tuning step for the given note. Returns the produced note
     * when the craft happened, null otherwise (no tuning block above, or the
     * output slot holds a different note / is full).
     */
    public NINotes tryProcess(NINotes note) {
        if (level == null || level.isClientSide) {
            return null;
        }
        BlockState above = level.getBlockState(worldPosition.above());
        if (!above.is(NIBlocks.TUNING_BLOCK.get())) {
            return null;
        }
        NINotes product = TuningBlock.color(above).times(note);
        ItemStack produced = new ItemStack(product.item.get());
        if (!output.isEmpty()
                && (!ItemStack.isSameItemSameComponents(output, produced)
                        || output.getCount() >= output.getMaxStackSize())) {
            return null;
        }
        output = output.isEmpty() ? produced : output.copyWithCount(output.getCount() + 1);
        level.setBlock(worldPosition.above(), TuningBlock.colored(above, note), Block.UPDATE_ALL);
        setChanged();
        return product;
    }

    /** Consumes one note from the input slot after a successful {@link #tryProcess}. */
    public void consumeInput() {
        input = input.copyWithCount(input.getCount() - 1);
        if (input.isEmpty()) {
            input = ItemStack.EMPTY;
        }
        setChanged();
    }

    public ItemStack getInputStack() {
        return input;
    }

    public ItemStack getOutputStack() {
        return output;
    }

    // -- server tick: hoppers/automation path -----------------------------------

    public static void tick(ResonanceAttunerBlockEntity be) {
        if (be.level == null || be.level.isClientSide || be.input.isEmpty()) {
            return;
        }
        NINotes note = NINotes.byItem(be.input.getItem());
        if (note != null && be.tryProcess(note) != null) {
            be.consumeInput();
        }
    }

    // -- WorldlyContainer: input slot 0 (notes only, top/side), output slot 1 (bottom)

    @Override
    public int[] getSlotsForFace(Direction face) {
        return face == Direction.DOWN ? new int[] { OUTPUT_SLOT } : new int[] { INPUT_SLOT };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return slot == INPUT_SLOT && NINotes.byItem(stack.getItem()) != null;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return input.isEmpty() && output.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == INPUT_SLOT ? input : output;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = slot == INPUT_SLOT ? input : output;
        int taken = Math.min(amount, stack.getCount());
        ItemStack result = stack.copyWithCount(taken);
        stack = stack.copyWithCount(stack.getCount() - taken);
        if (slot == INPUT_SLOT) {
            input = stack.isEmpty() ? ItemStack.EMPTY : stack;
        } else {
            output = stack.isEmpty() ? ItemStack.EMPTY : stack;
        }
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == INPUT_SLOT) {
            input = ItemStack.EMPTY;
        } else {
            output = ItemStack.EMPTY;
        }
        setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            input = stack;
        } else {
            output = stack;
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        input = ItemStack.EMPTY;
        output = ItemStack.EMPTY;
        setChanged();
    }

    // -- persistence

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // ItemStack.save throws on empty stacks, so only persist filled slots
        if (!input.isEmpty()) {
            tag.put("input", input.save(registries));
        }
        if (!output.isEmpty()) {
            tag.put("output", output.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        input = ItemStack.parseOptional(registries, tag.getCompound("input"));
        output = ItemStack.parseOptional(registries, tag.getCompound("output"));
    }
}
