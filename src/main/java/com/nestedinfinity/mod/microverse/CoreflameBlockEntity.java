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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A coreflame's single slot: it accepts only the singularity of its own
 * kind (the block decides the kind — see {@link MicroverseBlocks#coreflameIndex}).
 * Hoppers may insert from any side and extract from the bottom.
 */
public class CoreflameBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private ItemStack singularity = ItemStack.EMPTY;

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

    /** Puts a returned singularity back into the slot. */
    public void returnSingularity(ItemStack stack) {
        singularity = stack;
        setChanged();
        sync();
    }

    public ItemStack getSingularity() {
        return singularity;
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
        return new int[] {0};
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
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return singularity.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return singularity;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        int taken = Math.min(amount, singularity.getCount());
        ItemStack result = singularity.copyWithCount(taken);
        singularity = singularity.copyWithCount(singularity.getCount() - taken);
        if (singularity.isEmpty()) {
            singularity = ItemStack.EMPTY;
        }
        setChanged();
        sync();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = singularity;
        singularity = ItemStack.EMPTY;
        setChanged();
        sync();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        singularity = stack;
        setChanged();
        sync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(expectedSingularity().getItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        singularity = ItemStack.EMPTY;
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
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        singularity = ItemStack.parseOptional(registries, tag.getCompound("singularity"));
    }

    // -- client sync (the BER shows the hovering octahedron only while filled) ----

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
