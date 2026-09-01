package com.nestedinfinity.mod.blocks.superassembler;

import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.gems.NIGems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * The super assembler's GUI: the 10x10 glow-tube grid, the output slot beside
 * it, and the player inventory below. Glow tubes shift-clicked from the
 * inventory land in the grid automatically, so filling the rainbow is 100
 * shift-clicks once the tubes are collected.
 */
public class SuperAssemblerMenu extends AbstractContainerMenu {
    public static final int GRID_X = 8;
    public static final int GRID_Y = 18;
    public static final int OUTPUT_X = 214;
    public static final int OUTPUT_Y = 99;
    public static final int INV_Y = 216;
    public static final int HOTBAR_Y = 278;

    private final SuperAssemblerBlockEntity blockEntity;

    /** Client side: the buffer carries the block position (see the block's openMenu). */
    public SuperAssemblerMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolveBlockEntity(playerInventory, buf.readBlockPos()));
    }

    /** Server side. */
    public SuperAssemblerMenu(int id, Inventory playerInventory, SuperAssemblerBlockEntity be) {
        super(NIBlocks.SUPER_ASSEMBLER_MENU.get(), id);
        this.blockEntity = be;

        int side = (int) Math.sqrt(SuperAssemblerBlockEntity.GRID_SIZE); // 10
        for (int row = 0; row < side; row++) {
            for (int col = 0; col < side; col++) {
                addSlot(new Slot(be, row * side + col, GRID_X + col * 18, GRID_Y + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return NIGems.byTubeItem(stack.getItem()) != null;
                    }
                });
            }
        }
        addSlot(new Slot(be, SuperAssemblerBlockEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 41 + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 41 + col * 18, HOTBAR_Y));
        }
    }

    private static SuperAssemblerBlockEntity resolveBlockEntity(Inventory playerInventory, BlockPos pos) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof SuperAssemblerBlockEntity assembler) {
            return assembler;
        }
        return new SuperAssemblerBlockEntity(pos, playerInventory.player.level().getBlockState(pos));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int gridSize = SuperAssemblerBlockEntity.GRID_SIZE;
        if (index == gridSize) {
            // output -> player inventory
            if (!moveItemStackTo(stack, gridSize + 1, gridSize + 37, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index > gridSize) {
            // player inventory -> grid (glow tubes only)
            if (NIGems.byTubeItem(stack.getItem()) != null) {
                if (!moveItemStackTo(stack, 0, gridSize, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            // grid -> player inventory
            if (!moveItemStackTo(stack, gridSize + 1, gridSize + 37, false)) {
                return ItemStack.EMPTY;
            }
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
