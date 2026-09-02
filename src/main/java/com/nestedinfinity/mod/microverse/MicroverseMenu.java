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
 * The projector GUI: heart in, giant matter balls in, universe matter out.
 * Ten data ints keep the screen's readouts and the twelve flame lights
 * live: structure validity, TDU tier, flame bitmask, running flag,
 * remaining/total ticks, accrued matter, extensions, ball cost and the
 * singularity return chance. Button id 0 is the "extend" action.
 */
public class MicroverseMenu extends AbstractContainerMenu {
    public static final int EXTEND_BUTTON = 0;

    private static final int DATA_SIZE = 10;
    public static final int DATA_OK = 0;
    public static final int DATA_TIER = 1;
    public static final int DATA_FLAME_MASK = 2;
    public static final int DATA_RUNNING = 3;
    public static final int DATA_REMAINING = 4;
    public static final int DATA_TOTAL = 5;
    public static final int DATA_ACCRUED = 6;
    public static final int DATA_EXTENSIONS = 7;
    public static final int DATA_BALL_COST = 8;
    public static final int DATA_RETURN_CHANCE = 9;

    private final MicroverseProjectorBlockEntity blockEntity;
    private final ContainerData data;

    /** Client side: the buffer carries the block position. */
    public MicroverseMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolve(playerInventory, buf.readBlockPos()), new SimpleContainerData(DATA_SIZE));
    }

    public MicroverseMenu(int id, Inventory playerInventory, MicroverseProjectorBlockEntity be) {
        this(id, playerInventory, be, new ProjectorData(be));
    }

    private MicroverseMenu(int id, Inventory playerInventory, MicroverseProjectorBlockEntity be, ContainerData data) {
        super(MicroverseBlocks.PROJECTOR_MENU.get(), id);
        this.blockEntity = be;
        this.data = data;
        addDataSlots(data);

        addSlot(new Slot(be, MicroverseProjectorBlockEntity.HEART_SLOT, 8, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return be.canPlaceItem(MicroverseProjectorBlockEntity.HEART_SLOT, stack);
            }

            @Override
            public boolean isActive() {
                return !be.isRunning();
            }
        });
        addSlot(new Slot(be, MicroverseProjectorBlockEntity.BALL_SLOT, 8, 58) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return be.canPlaceItem(MicroverseProjectorBlockEntity.BALL_SLOT, stack);
            }
        });
        addSlot(new Slot(be, MicroverseProjectorBlockEntity.OUTPUT_SLOT, 151, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 102 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 160));
        }
    }

    private static MicroverseProjectorBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof MicroverseProjectorBlockEntity projector) {
            return projector;
        }
        return new MicroverseProjectorBlockEntity(pos, playerInventory.player.level().getBlockState(pos));
    }

    public MicroverseProjectorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int data(int index) {
        return data.get(index);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == EXTEND_BUTTON && blockEntity.tryExtend()) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == MicroverseProjectorBlockEntity.OUTPUT_SLOT) {
            if (!moveItemStackTo(stack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 3) {
            if (!moveItemStackTo(stack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (blockEntity.canPlaceItem(MicroverseProjectorBlockEntity.HEART_SLOT, stack)) {
                if (!moveItemStackTo(stack, MicroverseProjectorBlockEntity.HEART_SLOT,
                        MicroverseProjectorBlockEntity.HEART_SLOT + 1, false)) {
                    if (blockEntity.canPlaceItem(MicroverseProjectorBlockEntity.BALL_SLOT, stack)) {
                        if (!moveItemStackTo(stack, MicroverseProjectorBlockEntity.BALL_SLOT,
                                MicroverseProjectorBlockEntity.BALL_SLOT + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (blockEntity.canPlaceItem(MicroverseProjectorBlockEntity.BALL_SLOT, stack)) {
                if (!moveItemStackTo(stack, MicroverseProjectorBlockEntity.BALL_SLOT,
                        MicroverseProjectorBlockEntity.BALL_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    /** Live server-side readouts, polled by the vanilla data-slot sync. */
    private static final class ProjectorData implements ContainerData {
        private final MicroverseProjectorBlockEntity be;

        private ProjectorData(MicroverseProjectorBlockEntity be) {
            this.be = be;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_OK -> be.isStructureOk() ? 1 : 0;
                case DATA_TIER -> be.isRunning() ? be.getTier() : be.structureTier();
                case DATA_FLAME_MASK -> be.flameMask();
                case DATA_RUNNING -> be.isRunning() ? 1 : 0;
                case DATA_REMAINING -> be.getRemaining();
                case DATA_TOTAL -> be.getTotalDuration();
                case DATA_ACCRUED -> be.getAccruedMatter();
                case DATA_EXTENSIONS -> be.getExtensions();
                case DATA_BALL_COST -> be.getBallCost();
                case DATA_RETURN_CHANCE -> be.getReturnChance();
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
