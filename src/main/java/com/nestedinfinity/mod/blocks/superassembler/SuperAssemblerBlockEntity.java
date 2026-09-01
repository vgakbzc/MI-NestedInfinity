package com.nestedinfinity.mod.blocks.superassembler;

import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.NIItems;
import com.nestedinfinity.mod.items.gems.NIGems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The super assembler's brain: a 10x10 grid of glow-tube slots plus one
 * output slot. Whenever the grid holds at least one of EACH of the hundred
 * glow-tube colors, one of every tube is consumed and an optical qubit
 * component lands in the output — the whole gem program funnels through this
 * single craft. No energy, no randomness; the cost is the collection itself.
 *
 * <p>Slots 0..99 are the grid (glow tubes only), slot 100 the output.
 * Automation is deliberately out of scope: this machine is about the player
 * laying out the rainbow by hand (or via shift-clicks).
 */
public class SuperAssemblerBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int GRID_SIZE = NIGems.ALL.size(); // 100
    public static final int OUTPUT_SLOT = GRID_SIZE;

    private final NonNullList<ItemStack> items = NonNullList.withSize(GRID_SIZE + 1, ItemStack.EMPTY);

    public SuperAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(NIBlocks.SUPER_ASSEMBLER_TYPE.get(), pos, state);
    }

    // -- the grand craft --------------------------------------------------------

    public static void tick(SuperAssemblerBlockEntity be) {
        if (be.level == null || be.level.isClientSide) {
            return;
        }
        ItemStack produced = new ItemStack(NIItems.OPTICAL_QUBIT_COMPONENT.get());
        ItemStack output = be.items.get(OUTPUT_SLOT);
        if (!output.isEmpty()
                && (!ItemStack.isSameItemSameComponents(output, produced) || output.getCount() >= output.getMaxStackSize())) {
            return;
        }
        // one of every glow-tube color must sit in the grid
        Item[] tubeItems = new Item[GRID_SIZE];
        for (ItemStack stack : be.items.subList(0, GRID_SIZE)) {
            if (stack.isEmpty()) {
                continue;
            }
            NIGems.Gem gem = NIGems.byTubeItem(stack.getItem());
            if (gem == null) {
                return; // foreign item in the grid
            }
            tubeItems[NIGems.ALL.indexOf(gem)] = stack.getItem();
        }
        for (Item tube : tubeItems) {
            if (tube == null) {
                return; // at least one color is missing
            }
        }
        // consume one of each, then produce
        for (ItemStack stack : be.items.subList(0, GRID_SIZE)) {
            if (NIGems.byTubeItem(stack.getItem()) != null) {
                stack.shrink(1);
            }
        }
        be.items.set(OUTPUT_SLOT, output.isEmpty() ? produced
                : output.copyWithCount(output.getCount() + 1));
        be.setChanged();
    }

    // -- MenuProvider ------------------------------------------------------------

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mi_nested_infinity.super_assembler");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SuperAssemblerMenu(id, playerInventory, this);
    }

    // -- Container ----------------------------------------------------------------

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack taken = ContainerHelper.removeItem(items, slot, amount);
        if (!taken.isEmpty()) {
            setChanged();
        }
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    // -- persistence ---------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putInt("Slot", i);
                slotTag.put("Item", stack.save(registries));
                list.add(slotTag);
            }
        }
        tag.put("Items", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag slotTag = list.getCompound(i);
            int slot = slotTag.getInt("Slot");
            if (slot >= 0 && slot < items.size()) {
                ItemStack stack = ItemStack.parseOptional(registries, slotTag.getCompound("Item"));
                items.set(slot, stack);
            }
        }
    }
}
