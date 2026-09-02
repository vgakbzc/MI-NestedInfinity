package com.nestedinfinity.mod.microverse;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * The catalyzer GUI: seed singularity and catalyst stack in on the left,
 * singularities out on the right. Three data ints keep the screen's progress
 * bar, ritual status line and the twelve ritual lights live.
 */
public class SingularityCatalyzerMenu extends AbstractContainerMenu {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_RITUAL = 1;
    public static final int DATA_READY_MASK = 2;
    private static final int DATA_SIZE = 3;

    private final SingularityCatalyzerBlockEntity blockEntity;
    private final ContainerData data;

    /** Client side: the buffer carries the block position. */
    public SingularityCatalyzerMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolve(playerInventory, buf.readBlockPos()), new SimpleContainerData(DATA_SIZE));
    }

    public SingularityCatalyzerMenu(int id, Inventory playerInventory, SingularityCatalyzerBlockEntity be) {
        this(id, playerInventory, be, new CatalyzerData(be));
    }

    private SingularityCatalyzerMenu(int id, Inventory playerInventory, SingularityCatalyzerBlockEntity be,
            ContainerData data) {
        super(MicroverseBlocks.CATALYZER_MENU.get(), id);
        this.blockEntity = be;
        this.data = data;
        addDataSlots(data);

        addSlot(new Slot(be, SingularityCatalyzerBlockEntity.SEED_SLOT, 36, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return SingularityCatalyzerBlockEntity.isSeed(stack);
            }
        });
        addSlot(new Slot(be, SingularityCatalyzerBlockEntity.CATALYST_SLOT, 54, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return SingularityCatalyzerBlockEntity.kindOf(stack) >= 0;
            }
        });
        addSlot(new Slot(be, SingularityCatalyzerBlockEntity.OUTPUT_SLOT, 104, 35) {
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

    private static SingularityCatalyzerBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof SingularityCatalyzerBlockEntity catalyzer) {
            return catalyzer;
        }
        return new SingularityCatalyzerBlockEntity(pos, playerInventory.player.level().getBlockState(pos));
    }

    public int data(int index) {
        return data.get(index);
    }

    public SingularityCatalyzerBlockEntity getBlockEntity() {
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
        if (index < 3) {
            // machine slots shift-click to the inventory
            if (!moveItemStackTo(stack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
        } else if (SingularityCatalyzerBlockEntity.isSeed(stack)) {
            if (!moveItemStackTo(stack, SingularityCatalyzerBlockEntity.SEED_SLOT,
                    SingularityCatalyzerBlockEntity.SEED_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (SingularityCatalyzerBlockEntity.kindOf(stack) >= 0) {
            if (!moveItemStackTo(stack, SingularityCatalyzerBlockEntity.CATALYST_SLOT,
                    SingularityCatalyzerBlockEntity.CATALYST_SLOT + 1, false)) {
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

    /** Live server-side readouts, polled by the vanilla data-slot sync. */
    private static final class CatalyzerData implements ContainerData {
        private final SingularityCatalyzerBlockEntity be;

        private CatalyzerData(SingularityCatalyzerBlockEntity be) {
            this.be = be;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> be.getProgress();
                case DATA_RITUAL -> be.getRitualState();
                case DATA_READY_MASK -> be.getReadyMask();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    }
}
