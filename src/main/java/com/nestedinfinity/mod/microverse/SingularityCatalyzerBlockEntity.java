package com.nestedinfinity.mod.microverse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * The singularity catalyzer: a standalone no-energy machine that grows
 * singularities. Each of the twelve kinds has its own ritual condition and
 * its own catalyst — a plain Modern Industrialization singularity (the seed,
 * consumed) plus a stack of {@link #CRAFT_AMOUNT} catalyst go in, and when
 * the ritual holds the machine spends {@link #TOTAL_TICKS} returning
 * {@link #OUTPUT_AMOUNT} singularity of that kind: the seed, transmuted.
 *
 * <p>Rituals split into polled states (gold's two faces of lava, rift's log
 * and leaves, plenty's world floor, twilight's open sky at Y&ge;315, worlds'
 * top light &ge;13, stone's four base stones, evernight's rail, infinity's
 * door) and one-shot events that permanently unlock their kind on this
 * machine (shadow's nearby death, justice's snuffed adjacent flame, whimsy's
 * gold block vanishing while no player watches, fury's arrow strike) — once
 * a kind is unlocked it stays craftable here forever. State rituals and the
 * craft-start check are reevaluated every {@link #RITUAL_INTERVAL} ticks.
 */
public class SingularityCatalyzerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SEED_SLOT = 0;
    public static final int CATALYST_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int TOTAL_TICKS = 2000;
    /** Catalysts are consumed a stack at a time; potions only stack to 16. */
    public static final int CRAFT_AMOUNT = 64;
    /** One craft transmutes the seed into one singularity of the catalyst's kind. */
    public static final int OUTPUT_AMOUNT = 1;
    /** Rituals are polled every 8 ticks, not every tick. */
    public static final int RITUAL_INTERVAL = 8;

    /** Ritual kind indexes with one-shot (event) rituals. */
    public static final int KIND_SHADOW = 2;
    public static final int KIND_JUSTICE = 3;
    public static final int KIND_WHIMSY = 4;
    public static final int KIND_FURY = 8;

    /** The catalyst of each kind, in MicroverseItems.SINGULARITIES order. */
    public static final List<Item> CATALYSTS = List.of(
            Items.STRING, // gold: spun beside lava
            BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(
                    "modern_industrialization", "quantum_circuit")), // rift
            Items.ALLIUM, // shadow
            Items.BLUE_CANDLE, // justice
            Items.GOLD_BLOCK, // whimsy
            Items.POTION, // plenty: water bottles only (see kindOf)
            Items.DAYLIGHT_DETECTOR, // twilight
            Items.GLOWSTONE, // worlds
            Items.ARROW, // fury
            Items.DIRT, // stone
            Items.CLOCK, // evernight
            Items.FIREWORK_ROCKET); // infinity

    /** The seed every craft consumes and transmutes: MI's own singularity. */
    public static final Item MI_SINGULARITY = BuiltInRegistries.ITEM.get(
            ResourceLocation.fromNamespaceAndPath("modern_industrialization", "singularity"));

    /** Whether the kind's ritual is a one-shot event (primed) or a held state. */
    private static final boolean[] EVENT_RITUAL = {
            false, false, true, true, true, false, false, false, true, false, false, false};

    /** Server-side catalyzers, so death events can find the witnesses fast. */
    private static final Set<SingularityCatalyzerBlockEntity> LOADED = ConcurrentHashMap.newKeySet();

    private ItemStack seed = ItemStack.EMPTY;
    private ItemStack catalyst = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    /** What a running craft will emit; the catalyst was already burned. */
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private int progress;
    /** One-shot rituals permanently unlocked on this machine, bit per kind. */
    private int primedMask;
    /** Ritual readiness cache, refreshed every {@link #RITUAL_INTERVAL} ticks. */
    private int readyMask;
    /** Face bitmask of adjacent flames / gold blocks, to catch them vanishing. */
    private int flameMask;
    private int goldMask;
    private boolean neighborsScanned;
    /** 0 = no target, 1 = ritual not holding, 2 = ritual ready, 3 = no seed. */
    private int ritualState;

    public SingularityCatalyzerBlockEntity(BlockPos pos, BlockState state) {
        super(MicroverseBlocks.CATALYZER_TYPE.get(), pos, state);
    }

    /** Wires the death witness listener; call once from the mod constructor. */
    public static void registerEventListeners() {
        NeoForge.EVENT_BUS.addListener(SingularityCatalyzerBlockEntity::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        for (SingularityCatalyzerBlockEntity be : LOADED) {
            if (be.level == entity.level() && be.worldPosition.distSqr(entity.blockPosition()) <= 25) {
                be.prime(KIND_SHADOW);
            }
        }
    }

    /** The singularity kind index this catalyst stack targets, or -1. */
    public static int kindOf(ItemStack stack) {
        int index = CATALYSTS.indexOf(stack.getItem());
        if (index == 5 && !isWaterBottle(stack)) {
            return -1;
        }
        return index;
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    public static int craftAmount(ItemStack catalyst) {
        return Math.min(CRAFT_AMOUNT, catalyst.getMaxStackSize());
    }

    /** Permanently unlocks a one-shot ritual kind on this machine. */
    public void prime(int kind) {
        int bit = 1 << kind;
        if ((primedMask & bit) == 0) {
            primedMask |= bit;
            readyMask |= bit;
            setChanged();
        }
    }

    // -- the twelve rituals ---------------------------------------------------

    private boolean stateRitualHolds(int kind) {
        if (level == null) {
            return false;
        }
        BlockPos pos = worldPosition;
        return switch (kind) {
            case 0 -> neighborsMatching(state -> state.is(Blocks.LAVA)) >= 2; // gold
            case 1 -> hasNeighborMatching(state -> state.is(BlockTags.LOGS)) // rift
                    && hasNeighborMatching(state -> state.is(BlockTags.LEAVES));
            case 5 -> pos.getY() <= level.getMinBuildHeight() + 8; // plenty: world floor
            case 6 -> pos.getY() >= 315 && openSkyAbove(); // twilight
            case 7 -> topLight() >= 13; // worlds
            case 9 -> distinctBaseStones() >= 4; // stone
            case 10 -> hasNeighborMatching(state -> state.is(BlockTags.RAILS)); // evernight
            case 11 -> hasNeighborMatching(state -> state.is(BlockTags.DOORS)); // infinity
            default -> false;
        };
    }

    private int neighborsMatching(java.util.function.Predicate<BlockState> filter) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (filter.test(level.getBlockState(worldPosition.relative(dir)))) {
                count++;
            }
        }
        return count;
    }

    private boolean hasNeighborMatching(java.util.function.Predicate<BlockState> filter) {
        return neighborsMatching(filter) > 0;
    }

    private boolean openSkyAbove() {
        for (int y = worldPosition.getY() + 1; y <= level.getMaxBuildHeight(); y++) {
            if (!level.getBlockState(new BlockPos(worldPosition.getX(), y, worldPosition.getZ())).isAir()) {
                return false;
            }
        }
        return true;
    }

    private int topLight() {
        BlockPos above = worldPosition.above();
        int block = level.getBrightness(LightLayer.BLOCK, above);
        int sky = day() ? level.getBrightness(LightLayer.SKY, above) : 0;
        return Math.max(block, sky);
    }

    private boolean day() {
        return level.dayTime() % 24000L < 12000L;
    }

    private int distinctBaseStones() {
        Set<Block> stones = new HashSet<>();
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(worldPosition.relative(dir));
            if (state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.BASE_STONE_NETHER)) {
                stones.add(state.getBlock());
            }
        }
        return stones.size();
    }

    // -- neighbor bookkeeping (justice & whimsy watch their neighbors) -------

    /** Called by the block when a face-adjacent position changed. */
    public void onNeighborChanged(BlockPos neighborPos) {
        if (level == null || level.isClientSide()) {
            return;
        }
        for (Direction dir : Direction.values()) {
            if (!worldPosition.relative(dir).equals(neighborPos)) {
                continue;
            }
            BlockState state = level.getBlockState(neighborPos);
            int bit = 1 << dir.ordinal();
            boolean flame = isFlame(state);
            boolean gold = state.is(Blocks.GOLD_BLOCK);
            boolean wasFlame = (flameMask & bit) != 0;
            boolean wasGold = (goldMask & bit) != 0;
            flameMask = flame ? flameMask | bit : flameMask & ~bit;
            goldMask = gold ? goldMask | bit : goldMask & ~bit;
            if (wasFlame && !flame) {
                prime(KIND_JUSTICE); // the flame was snuffed or broken
            }
            if (wasGold && !gold && !playerNearby()) {
                prime(KIND_WHIMSY); // gold spirited away unseen
            }
            setChanged();
        }
    }

    private void scanNeighbors() {
        if (neighborsScanned || level == null || level.isClientSide()) {
            return;
        }
        neighborsScanned = true;
        flameMask = 0;
        goldMask = 0;
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(worldPosition.relative(dir));
            if (isFlame(state)) {
                flameMask |= 1 << dir.ordinal();
            }
            if (state.is(Blocks.GOLD_BLOCK)) {
                goldMask |= 1 << dir.ordinal();
            }
        }
    }

    private boolean playerNearby() {
        return level != null && level.players().stream().anyMatch(
                p -> p.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5) <= 25.0);
    }

    private static boolean isFlame(BlockState state) {
        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.TORCH)
                || state.is(Blocks.WALL_TORCH) || state.is(Blocks.SOUL_TORCH)
                || state.is(Blocks.SOUL_WALL_TORCH)) {
            return true;
        }
        if (state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) {
            return state.getValue(CampfireBlock.LIT);
        }
        if (state.getBlock() instanceof CandleBlock) {
            return state.getValue(CandleBlock.LIT);
        }
        if (state.getBlock() instanceof CandleCakeBlock) {
            return state.getValue(CandleCakeBlock.LIT);
        }
        return false;
    }

    // -- the craft loop -------------------------------------------------------

    public void serverTick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        scanNeighbors();
        if (level.getGameTime() % RITUAL_INTERVAL == 0) {
            refreshRituals();
        }
        int kind = kindOf(catalyst);
        ritualState = kind < 0 ? 0
                : !seed.is(MI_SINGULARITY) ? 3
                : (readyMask & (1 << kind)) != 0 ? 2 : 1;
        if (progress > 0) {
            if (++progress >= TOTAL_TICKS) {
                progress = 0;
                emitPending();
                setChanged();
                sync();
            } else {
                setChanged();
            }
            return;
        }
        if (kind < 0 || catalyst.getCount() < craftAmount(catalyst)
                || (readyMask & (1 << kind)) == 0 || !seed.is(MI_SINGULARITY)) {
            return;
        }
        ItemStack produced = new ItemStack(MicroverseItems.SINGULARITIES.get(kind).item().get(), OUTPUT_AMOUNT);
        if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, produced)
                || output.getCount() + OUTPUT_AMOUNT > output.getMaxStackSize())) {
            return;
        }
        catalyst = catalyst.copyWithCount(catalyst.getCount() - craftAmount(catalyst));
        if (catalyst.getCount() <= 0) {
            catalyst = ItemStack.EMPTY;
        }
        seed = seed.copyWithCount(seed.getCount() - 1);
        if (seed.getCount() <= 0) {
            seed = ItemStack.EMPTY;
        }
        pendingOutput = produced;
        progress = 1;
        setChanged();
        sync();
    }

    /** Recomputes the readiness bitmask (states polled, events stay latched). */
    private void refreshRituals() {
        int mask = primedMask;
        for (int kind = 0; kind < MicroverseItems.SINGULARITIES.size(); kind++) {
            if (!EVENT_RITUAL[kind] && stateRitualHolds(kind)) {
                mask |= 1 << kind;
            }
        }
        if (mask != readyMask) {
            readyMask = mask;
        }
    }

    private void emitPending() {
        if (pendingOutput.isEmpty()) {
            return;
        }
        int add = 0;
        if (output.isEmpty()) {
            add = pendingOutput.getCount();
        } else if (ItemStack.isSameItemSameComponents(output, pendingOutput)) {
            add = Math.min(pendingOutput.getCount(), output.getMaxStackSize() - output.getCount());
        }
        if (add > 0) {
            output = (output.isEmpty() ? pendingOutput : output).copyWithCount(output.getCount() + add);
        }
        if (add < pendingOutput.getCount()) {
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
                    pendingOutput.copyWithCount(pendingOutput.getCount() - add)));
        }
        pendingOutput = ItemStack.EMPTY;
    }

    // -- accessors -------------------------------------------------------------

    public ItemStack getSeed() {
        return seed;
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getProgress() {
        return progress;
    }

    /** 0 = no target, 1 = ritual not holding, 2 = ritual ready, 3 = no seed. */
    public int getRitualState() {
        return ritualState;
    }

    /** Bit per kind: ritual completed/unlocked right now (for the GUI lights). */
    public int getReadyMask() {
        return readyMask;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mi_nested_infinity.singularity_catalyzer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SingularityCatalyzerMenu(id, playerInventory, this);
    }

    // -- WorldlyContainer --------------------------------------------------------

    @Override
    public int[] getSlotsForFace(Direction face) {
        return new int[] {SEED_SLOT, CATALYST_SLOT, OUTPUT_SLOT};
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
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return seed.isEmpty() && catalyst.isEmpty() && output.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case SEED_SLOT -> seed;
            case CATALYST_SLOT -> catalyst;
            default -> output;
        };
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
        switch (slot) {
            case SEED_SLOT -> seed = stack;
            case CATALYST_SLOT -> catalyst = stack;
            default -> output = stack;
        }
        if (seed != null && seed.isEmpty()) {
            seed = ItemStack.EMPTY;
        }
        if (catalyst != null && catalyst.isEmpty()) {
            catalyst = ItemStack.EMPTY;
        }
        if (output != null && output.isEmpty()) {
            output = ItemStack.EMPTY;
        }
        setChanged();
        sync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SEED_SLOT) {
            return stack.is(MI_SINGULARITY);
        }
        return slot == CATALYST_SLOT && kindOf(stack) >= 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        seed = ItemStack.EMPTY;
        catalyst = ItemStack.EMPTY;
        output = ItemStack.EMPTY;
        setChanged();
        sync();
    }

    // -- lifecycle (the death-witness registry) -------------------------------

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            LOADED.add(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            LOADED.remove(this);
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (level != null && !level.isClientSide()) {
            LOADED.remove(this);
        }
        super.onChunkUnloaded();
    }

    // -- persistence --------------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!seed.isEmpty()) {
            tag.put("seed", seed.save(registries));
        }
        if (!catalyst.isEmpty()) {
            tag.put("catalyst", catalyst.save(registries));
        }
        if (!output.isEmpty()) {
            tag.put("output", output.save(registries));
        }
        if (!pendingOutput.isEmpty()) {
            tag.put("pending", pendingOutput.save(registries));
        }
        tag.putInt("progress", progress);
        tag.putInt("primed_mask", primedMask);
        tag.putInt("flame_mask", flameMask);
        tag.putInt("gold_mask", goldMask);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        seed = ItemStack.parseOptional(registries, tag.getCompound("seed"));
        catalyst = ItemStack.parseOptional(registries, tag.getCompound("catalyst"));
        output = ItemStack.parseOptional(registries, tag.getCompound("output"));
        pendingOutput = ItemStack.parseOptional(registries, tag.getCompound("pending"));
        progress = Math.max(0, tag.getInt("progress"));
        primedMask = tag.getInt("primed_mask");
        readyMask = primedMask; // states refresh on the next poll
        flameMask = tag.getInt("flame_mask");
        goldMask = tag.getInt("gold_mask");
        neighborsScanned = false; // rescan from the live world
    }

    // -- client sync (item slots change while the GUI or hoppers interact) -------

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (!seed.isEmpty()) {
            tag.put("seed", seed.save(registries));
        }
        if (!catalyst.isEmpty()) {
            tag.put("catalyst", catalyst.save(registries));
        }
        if (!output.isEmpty()) {
            tag.put("output", output.save(registries));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        seed = ItemStack.parseOptional(registries, tag.getCompound("seed"));
        catalyst = ItemStack.parseOptional(registries, tag.getCompound("catalyst"));
        output = ItemStack.parseOptional(registries, tag.getCompound("output"));
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
