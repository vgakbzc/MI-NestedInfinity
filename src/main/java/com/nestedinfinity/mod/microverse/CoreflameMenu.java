package com.nestedinfinity.mod.microverse;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/** The coreflame's GUI: singularity in on the left, returned singularity out on the right. */
public class CoreflameMenu extends AbstractContainerMenu {
    private final CoreflameBlockEntity blockEntity;

    /** Client side: the buffer carries the block position (see the block's openMenu). */
    public CoreflameMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolve(playerInventory, buf.readBlockPos()));
    }

    public CoreflameMenu(int id, Inventory playerInventory, CoreflameBlockEntity be) {
        super(MicroverseBlocks.COREFLAME_MENU.get(), id);
        this.blockEntity = be;
        addSlot(new Slot(be, CoreflameBlockEntity.INPUT_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return be.canPlaceItem(CoreflameBlockEntity.INPUT_SLOT, stack);
            }
        });
        addSlot(new Slot(be, CoreflameBlockEntity.RETURN_SLOT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private static CoreflameBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof CoreflameBlockEntity flame) {
            return flame;
        }
        return new CoreflameBlockEntity(pos, playerInventory.player.level().getBlockState(pos));
    }

    public CoreflameBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < 2) {
            // machine slots (input or return) shift-click to the inventory
            if (!moveItemStackTo(stack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.canPlaceItem(CoreflameBlockEntity.INPUT_SLOT, stack)) {
            if (!moveItemStackTo(stack, CoreflameBlockEntity.INPUT_SLOT,
                    CoreflameBlockEntity.INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(Vec3.atCenterOf(blockEntity.getBlockPos())) <= 64.0;
    }
}
