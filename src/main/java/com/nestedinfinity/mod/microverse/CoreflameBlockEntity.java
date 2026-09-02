package com.nestedinfinity.mod.microverse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A coreflame's two slots: slot 0 accepts only the singularity of its own
 * kind (the block decides the kind — see
 * {@link MicroverseBlocks#coreflameIndex}); slot 1 is the take-only return
 * slot where the projector drops a singularity that survived its universe.
 * Hoppers may insert into slot 0 from any side and extract either slot.
 */
public class CoreflameBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int RETURN_SLOT = 1;

    private ItemStack singularity = ItemStack.EMPTY;
    private ItemStack returned = ItemStack.EMPTY;

    public CoreflameBlockEntity(BlockPos pos, BlockState state) {
        super(MicroverseBlocks.COREFLAME_TYPE.get(), pos, state);
    }

    /** The singularity item this flame accepts, or null if the block is not a coreflame. */
    public ItemStack expectedSingularity() {
        if (level == null) {
            return ItemStack.EMPTY;
        }
        int index = MicroverseBlocks.coreflameIndex(getBlockState().getBlock());
        if (index < 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(MicroverseItems.SINGULARITIES.get(index).item().get());
    }

    public boolean isFilled() {
        return singularity.is(expectedSingularity().getItem());
    }

    /** Empties the slot (the projector consumes the singularity on start). */
    public void consumeSingularity() {
        singularity = ItemStack.EMPTY;
        setChanged();
        sync();
    }

    /**
     * A returned singularity lands in the return slot (merged when possible,
     * overflow drops above the flame).
     */
    public void returnSingularity(ItemStack stack) {
        if (returned.isEmpty()) {
            returned = stack;
        } else if (ItemStack.isSameItemSameComponents(returned, stack)) {
            int add = Math.min(stack.getMaxStackSize() - returned.getCount(), stack.getCount());
            returned = returned.copyWithCount(returned.getCount() + add);
            stack = stack.copyWithCount(stack.getCount() - add);
        }
        if (!stack.isEmpty() && level != null && !level.isClientSide()) {
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, stack));
        }
        setChanged();
        sync();
    }

    public ItemStack getSingularity() {
        return singularity;
    }

    public ItemStack getReturned() {
        return returned;
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    private Component getName() {
        int index = level == null ? -1 : MicroverseBlocks.coreflameIndex(getBlockState().getBlock());
        String key = index < 0 ? "coreflame" : MicroverseItems.SINGULARITIES.get(index).blockSuffix();
        return Component.translatable("block.mi_nested_infinity.coreflame_" + key);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CoreflameMenu(id, playerInventory, this);
    }

    // -- WorldlyContainer --------------------------------------------------------

    @Override
    public int[] getSlotsForFace(Direction face) {
        return new int[] {INPUT_SLOT, RETURN_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return singularity.isEmpty() && returned.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == RETURN_SLOT ? returned : singularity;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        int taken = Math.min(amount, stack.getCount());
        ItemStack result = stack.copyWithCount(taken);
        setItem(slot, stack.copyWithCount(stack.getCount() - taken));
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == RETURN_SLOT) {
            returned = stack;
        } else {
            singularity = stack;
        }
        if (singularity != null && singularity.isEmpty()) {
            singularity = ItemStack.EMPTY;
        }
        if (returned != null && returned.isEmpty()) {
            returned = ItemStack.EMPTY;
        }
        setChanged();
        sync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == INPUT_SLOT && stack.is(expectedSingularity().getItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        singularity = ItemStack.EMPTY;
        returned = ItemStack.EMPTY;
        setChanged();
        sync();
    }

    // -- persistence --------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!singularity.isEmpty()) {
            tag.put("singularity", singularity.save(registries));
        }
        if (!returned.isEmpty()) {
            tag.put("returned", returned.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        singularity = ItemStack.parseOptional(registries, tag.getCompound("singularity"));
        returned = ItemStack.parseOptional(registries, tag.getCompound("returned"));
    }

    // -- client sync (the BER shows the hovering octahedron while filled, or
    //    while the projector above keeps the ring lit) -------------------------

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (!singularity.isEmpty()) {
            tag.put("singularity", singularity.save(registries));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        singularity = ItemStack.parseOptional(registries, tag.getCompound("singularity"));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sync() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }
}
