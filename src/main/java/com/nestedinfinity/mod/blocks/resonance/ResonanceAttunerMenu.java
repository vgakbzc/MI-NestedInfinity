package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.items.resonance.NINotes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The attuner's two-slot GUI: notes go into the input slot (the server tick
 * runs the Q8 step, same as the hopper path) and the products come out of the
 * output slot. The data slot carries the tuning block's current color
 * (-1 = no tuning block above) so the screen can highlight the register.
 */
public class ResonanceAttunerMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private final ResonanceAttunerBlockEntity blockEntity;
    private final ContainerData data;

    /** Client side: the buffer carries the block position (see the block's openMenu). */
    public ResonanceAttunerMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolveBlockEntity(playerInventory, buf.readBlockPos()), new SimpleContainerData(1));
    }

    /** Server side. */
    public ResonanceAttunerMenu(int id, Inventory playerInventory, ResonanceAttunerBlockEntity be) {
        this(id, playerInventory, be, new RegisterColorData(be));
    }

    private ResonanceAttunerMenu(int id, Inventory playerInventory, ResonanceAttunerBlockEntity be,
            ContainerData data) {
        super(com.nestedinfinity.mod.blocks.NIBlocks.RESONANCE_ATTUNER_MENU.get(), id);
        this.blockEntity = be;
        this.data = data;
        addDataSlots(data);

        ItemLike[] noteItems = NINotes.ALL.stream().map(n -> (ItemLike) n.item.get()).toArray(ItemLike[]::new);
        Ingredient notes = Ingredient.of(noteItems);
        addSlot(new Slot(be, INPUT_SLOT, 53, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return notes.test(stack);
            }
        });
        addSlot(new Slot(be, OUTPUT_SLOT, 107, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 98 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 156));
        }
    }

    private static ResonanceAttunerBlockEntity resolveBlockEntity(Inventory playerInventory, BlockPos pos) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof ResonanceAttunerBlockEntity attuner) {
            return attuner;
        }
        return new ResonanceAttunerBlockEntity(pos, playerInventory.player.level().getBlockState(pos));
    }

    /** The tuning block's color index, or -1 when there is no tuning block above. */
    public int registerColor() {
        return data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == OUTPUT_SLOT) {
            if (!moveItemStackTo(stack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index == INPUT_SLOT) {
            if (!moveItemStackTo(stack, 2, 38, false)) {
                return ItemStack.EMPTY;
            }
        } else if (NINotes.byItem(stack.getItem()) != null) {
            if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
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

    private static final class RegisterColorData implements ContainerData {
        private final ResonanceAttunerBlockEntity be;

        private RegisterColorData(ResonanceAttunerBlockEntity be) {
            this.be = be;
        }

        @Override
        public int get(int index) {
            return be.registerColor();
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
