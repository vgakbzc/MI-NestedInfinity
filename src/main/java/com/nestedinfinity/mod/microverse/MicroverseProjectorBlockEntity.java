package com.nestedinfinity.mod.microverse;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.blockentities.hatches.ItemHatch;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import aztech.modern_industrialization.util.Simulation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
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
 * The projector's brain (spec doc sections 4-5, 8). The full state machine:
 *
 * <ul>
 * <li>Idle: revalidate the structure every second; auto-start when the
 * structure is complete, the heart slot holds a Heart of a Nonexistent
 * World and all twelve coreflames hold their singularities. Starting
 * consumes the heart and one singularity of every kind (all twelve).</li>
 * <li>Running: 2G EU/t. One matter item flows out to the item output
 * hatches every ten productive seconds; a full output side pauses the
 * production and decays the countdown at half speed until there is room
 * again. Missing energy destabilizes the universe: the countdown then
 * decays at ten times the normal speed instead of pausing. Giant matter
 * balls are auto-drawn one at a time from the structure's item input
 * hatches: the n-th ball spent adds 0.5/n of the base time.</li>
 * <li>Finish: the collapse bonus pays tier-squared matter items (plus any
 * pending whole item) into the output hatches, overflow drops above the
 * controller, and each of the twelve burned singularities returns
 * independently with probability max(0, 95% - 5% * extensions), into its
 * coreflame's return slot.</li>
 * <li>Collapse: if the structure breaks mid-run everything is lost — the
 * heart and singularities were already consumed, the spent balls and the
 * accrued matter go with the collapsing universe.</li>
 * </ul>
 */
public class MicroverseProjectorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final long EU_PER_TICK = 2_000_000_000L;
    /** Sixty seconds of full-burn buffer, refillable from any MI cable tier. */
    public static final long ENERGY_CAPACITY = EU_PER_TICK * 60;

    public static final int HEART_SLOT = 0;
    public static final int BALL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    /**
     * Base stability time in ticks: t1 = 10s, then x10^(1/4) per tier — a
     * geometric ramp from 10s (t1) to exactly 1000s (t9).
     */
    public static int baseTicks(int tier) {
        return (int) Math.round(200.0 * Math.pow(10.0, (tier - 1) / 4.0));
    }

    /** One matter item flows out every ten productive seconds while running. */
    public static final int ITEM_INTERVAL_TICKS = 200;
    /** The collapse bonus at the end of a run: tier squared matter items. */
    public static int collapseBonus(int tier) {
        return tier * tier;
    }

    private ItemStack heart = ItemStack.EMPTY;
    private ItemStack balls = ItemStack.EMPTY;
    private ItemStack matterOutput = ItemStack.EMPTY;

    private long energy = 0;
    private boolean running = false;
    private int tier = 0;
    private int remaining = 0;
    private int totalDuration = 0;
    /** Productive ticks toward the next whole matter item. */
    private int ticksTowardItem = 0;
    /** Matter items yielded this run (the GUI's running counter). */
    private int yieldedThisRun = 0;
    private int extensions = 0;
    /** Output hatches could not take the last whole item: pause + half-speed countdown. */
    private boolean outputBlocked = false;
    /** Toggles once per tick to halve the countdown while blocked. */
    private boolean slowPhase = false;
    /** Singularity kinds swallowed at start, awaiting their return roll. */
    private final List<Integer> pendingSingularities = new ArrayList<>();

    /** Cached structure state, refreshed every second and on demand. */
    private boolean structureOk = false;
    private int structureTier = 0;
    private int flameMask = 0;
    private String structureProblem = "unchecked";
    /** Hatches found by the last structure pass (see {@link MicroverseStructure.Result}). */
    private List<BlockPos> energyHatches = List.of();
    private List<BlockPos> itemInputHatches = List.of();
    private List<BlockPos> itemOutputHatches = List.of();

    private int recheckCounter = 0;

    public MicroverseProjectorBlockEntity(BlockPos pos, BlockState state) {
        super(MicroverseBlocks.PROJECTOR_TYPE.get(), pos, state);
    }

    // -- energy -------------------------------------------------------------------

    private final MIEnergyStorage energyStorage = new MIEnergyStorage() {
        @Override
        public long receive(long max, boolean simulate) {
            long accepted = Math.min(max, ENERGY_CAPACITY - energy);
            if (!simulate) {
                energy += accepted;
                setChanged();
            }
            return accepted;
        }

        @Override
        public long extract(long max, boolean simulate) {
            return 0;
        }

        @Override
        public long getAmount() {
            return energy;
        }

        @Override
        public long getCapacity() {
            return ENERGY_CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public boolean canConnect(CableTier cableTier) {
            return true;
        }
    };

    public MIEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    // -- structure ------------------------------------------------------------------

    public void revalidateStructure() {
        if (level == null || level.isClientSide()) {
            return;
        }
        MicroverseStructure.Result result = MicroverseStructure.validate(level, worldPosition);
        boolean was = structureOk;
        structureOk = result.valid;
        structureTier = result.valid ? result.tduTier : 0;
        flameMask = result.flameMask;
        structureProblem = result.problem == null ? "" : result.problem;
        energyHatches = result.energyHatches;
        itemInputHatches = result.itemInputHatches;
        itemOutputHatches = result.itemOutputHatches;
        if (!was && result.valid || was && !result.valid) {
            sync();
        }
    }

    public boolean isStructureOk() {
        return structureOk;
    }

    public int structureTier() {
        return structureTier;
    }

    public int flameMask() {
        return flameMask;
    }

    public String structureProblem() {
        return structureProblem;
    }

    // -- run state --------------------------------------------------------------------

    public boolean isRunning() {
        return running;
    }

    public int getTier() {
        return tier;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getAccruedMatter() {
        return yieldedThisRun;
    }

    public int getExtensions() {
        return extensions;
    }

    /** True while the item output hatches cannot take the accrued matter. */
    public boolean isOutputBlocked() {
        return outputBlocked;
    }

    /** True when the buffer cannot cover this tick — outranks a blocked output. */
    public boolean isEnergyStarved() {
        return running && energy < EU_PER_TICK;
    }

    /**
     * Ticks the next spent matter ball would add: ball n extends the run by
     * 0.5/n of the tier's base time (ball 1 = +50%, ball 2 = +25%, ...).
     */
    public int getNextBallTicks() {
        if (tier <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.round(baseTicks(tier) * 0.5 / (extensions + 1)));
    }

    public int getReturnChance() {
        return Math.max(0, 95 - 5 * extensions);
    }

    private void tryStart() {
        if (!structureOk || running) {
            return;
        }
        if (heart.isEmpty() || !heart.is(MicroverseItems.HEART_OF_A_NONEXISTENT_WORLD.get())) {
            return;
        }
        if (flameMask != 0xFFF) {
            return;
        }
        // consume the heart and one singularity of every kind — all twelve
        // flames burn, and each burned singularity awaits its return roll
        heart = heart.copyWithCount(heart.getCount() - 1);
        if (heart.isEmpty()) {
            heart = ItemStack.EMPTY;
        }
        pendingSingularities.clear();
        for (int i = 0; i < MicroverseStructure.COREFLAME_POS.length; i++) {
            int[] rc = MicroverseStructure.COREFLAME_POS[i];
            BlockPos p = worldPosition.below().offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame && flame.isFilled()) {
                flame.consumeSingularity();
                pendingSingularities.add(i);
            }
        }
        flameMask = 0;
        outputBlocked = false;
        slowPhase = false;
        tier = structureTier;
        remaining = totalDuration = baseTicks(tier);
        ticksTowardItem = 0;
        yieldedThisRun = 0;
        extensions = 0;
        running = true;
        // all twelve flames burned: the mask stays 0 while running (the GUI
        // lights the whole ring for a running universe instead)
        setRunningState(true);
        level.playSound(null, worldPosition, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.4F, 1.5F);
        setChanged();
        sync();
    }

    /**
     * Shared extend math: ball n adds 0.5/n of the base time (the first ball
     * is +50%, the second +25%, and so on — each ball buys less stability).
     */
    private void applyExtension() {
        int add = getNextBallTicks();
        remaining += add;
        totalDuration += add;
        extensions++;
        setChanged();
        sync();
    }

    /**
     * Refills the internal buffer from the structure's energy input hatches
     * — MI cables feed the hatches, the controller drinks from them, the
     * same contract MI's own multiblocks use. (A creative energy source or
     * cable placed directly against the controller also works: it pushes
     * into the sided capability registered in MicroverseBlocks.)
     */
    private void pullEnergyFromHatches() {
        long want = ENERGY_CAPACITY - energy;
        for (BlockPos p : energyHatches) {
            if (want <= 0) {
                break;
            }
            if (level.getBlockEntity(p) instanceof EnergyHatch hatch) {
                long drained = hatch.getEnergyComponent().consumeEu(want, Simulation.ACT);
                if (drained > 0) {
                    energy += drained;
                    want -= drained;
                    setChanged();
                }
            }
        }
    }

    /**
     * The ball auto-feeder: once per structure pass, one giant matter ball
     * is spent on an extension, drawn from the item input hatches only.
     * Feeding hatches is how you keep a universe alive.
     */
    private void tryAutoExtend() {
        if (!running || level == null || level.isClientSide()) {
            return;
        }
        Item ball = com.nestedinfinity.mod.items.NIOpticalItems.GIANT_MATTER_BALL.get();
        if (!takeOneBallFromHatches(ball)) {
            return;
        }
        applyExtension();
    }

    /** Takes exactly one ball out of the item input hatches; false if none. */
    private boolean takeOneBallFromHatches(Item ball) {
        for (BlockPos p : itemInputHatches) {
            if (level.getBlockEntity(p) instanceof ItemHatch hatch) {
                for (var stack : hatch.getInventory().getItemStacks()) {
                    // NOTE: getResource()/getVariant() return the ItemVariant
                    // wrapper, never the Item — compare via getItem() or the
                    // == silently compares unrelated references (always false)
                    if (!stack.isEmpty() && stack.getVariant().getItem() == ball && stack.getAmount() >= 1) {
                        stack.decrement(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * One matter item every {@link #ITEM_INTERVAL_TICKS} productive ticks
     * (the increment lives in {@link #tick}); a full output side holds the
     * pending item, blocks the run (no progress, half-speed countdown) and
     * retries until room appears.
     */
    private void produceMatter() {
        if (tier <= 0) {
            return;
        }
        if (outputBlocked) {
            // retry the held item; the run resumes when it fits
            if (pushMatterToHatches(MicroverseItems.MATTERS.get(tier - 1).get(), 1) == 0) {
                outputBlocked = false;
                yieldedThisRun++;
                ticksTowardItem -= ITEM_INTERVAL_TICKS;
                setChanged();
                sync();
            }
            return;
        }
        if (ticksTowardItem < ITEM_INTERVAL_TICKS) {
            return;
        }
        if (pushMatterToHatches(MicroverseItems.MATTERS.get(tier - 1).get(), 1) == 0) {
            yieldedThisRun++;
            ticksTowardItem -= ITEM_INTERVAL_TICKS;
        } else {
            outputBlocked = true;
            setChanged();
            sync();
        }
    }

    /**
     * Pushes whole matter items into the structure's item output hatches.
     * Returns what did not fit anywhere.
     */
    private int pushMatterToHatches(Item produced, int count) {
        var variant = ItemVariant.of(produced);
        for (BlockPos p : itemOutputHatches) {
            if (count <= 0) {
                break;
            }
            if (level.getBlockEntity(p) instanceof ItemHatch hatch) {
                for (var stack : hatch.getInventory().getItemStacks()) {
                    if (count <= 0) {
                        break;
                    }
                    if (!stack.isResourceAllowedByLock(produced)) {
                        continue;
                    }
                    if (stack.isEmpty()) {
                        long room = stack.getRemainingCapacityFor(variant);
                        if (room > 0) {
                            long add = Math.min(room, count);
                            stack.setKey(variant);
                            stack.increment(add);
                            count -= (int) add;
                        }
                    } else if (stack.getVariant().getItem() == produced) {
                        long room = stack.getRemainingCapacityFor(stack.getVariant());
                        long add = Math.min(room, count);
                        if (add > 0) {
                            stack.increment(add);
                            count -= (int) add;
                        }
                    }
                }
            }
        }
        return count;
    }

    private void finish() {
        running = false;
        int runTier = tier;
        tier = 0;
        // the collapse bonus: tier squared, plus any whole item still pending
        int count = collapseBonus(runTier) + Math.max(0, ticksTowardItem / ITEM_INTERVAL_TICKS);
        ticksTowardItem = 0;
        yieldedThisRun = 0;
        Item produced = MicroverseItems.MATTERS.get(runTier - 1).get();
        // the item output hatches drink what they can; the rest drops on top
        // of the controller
        count = pushMatterToHatches(produced, count);
        // drain anything left in the internal output buffer the same way
        if (!matterOutput.isEmpty()) {
            if (matterOutput.is(produced)) {
                count += matterOutput.getCount();
            } else {
                // unrelated leftover from an older version: hand it back
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, matterOutput.copy()));
            }
            matterOutput = ItemStack.EMPTY;
        }
        while (count > 0) {
            int drop = Math.min(count, new ItemStack(produced).getMaxStackSize());
            ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5, new ItemStack(produced, drop));
            level.addFreshEntity(entity);
            count -= drop;
        }
        // the singularity return rolls: each burned flame's return slot
        int chance = getReturnChance();
        for (int index : pendingSingularities) {
            if (level.random.nextInt(100) >= chance) {
                continue;
            }
            int[] rc = MicroverseStructure.COREFLAME_POS[index];
            BlockPos p = worldPosition.below().offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame) {
                flame.returnSingularity(new ItemStack(MicroverseItems.SINGULARITIES.get(index).item().get()));
            }
        }
        pendingSingularities.clear();
        extensions = 0;
        outputBlocked = false;
        setRunningState(false);
        revalidateStructure();
        level.playSound(null, worldPosition, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 0.6F);
        setChanged();
        sync();
    }

    private void collapse() {
        running = false;
        tier = 0;
        remaining = 0;
        totalDuration = 0;
        ticksTowardItem = 0;
        yieldedThisRun = 0;
        extensions = 0;
        outputBlocked = false;
        pendingSingularities.clear();
        balls = ItemStack.EMPTY; // spent balls go with the collapsing universe
        setRunningState(false);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.POOF, worldPosition.getX() + 0.5, worldPosition.getY() + 3.5,
                    worldPosition.getZ() + 0.5, 40, 1.2, 0.8, 1.2, 0.05);
        }
        level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.8F, 1.4F);
        setChanged();
        sync();
    }

    private void setRunningState(boolean value) {
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getValue(MicroverseProjectorBlock.RUNNING) != value) {
                level.setBlock(worldPosition, state.setValue(MicroverseProjectorBlock.RUNNING, value), 3);
            }
        }
    }

    // -- ticking ------------------------------------------------------------------

    public static void tick(MicroverseProjectorBlockEntity be) {
        if (be.level == null || be.level.isClientSide()) {
            return;
        }
        // migration: balls left in the internal slot by pre-hatch versions
        // are handed back — consumption is input-hatch-only now
        if (!be.balls.isEmpty()) {
            be.level.addFreshEntity(new ItemEntity(be.level, be.worldPosition.getX() + 0.5,
                    be.worldPosition.getY() + 1.0, be.worldPosition.getZ() + 0.5, be.balls.copy()));
            be.balls = ItemStack.EMPTY;
            be.setChanged();
        }
        if (++be.recheckCounter >= 20) {
            be.recheckCounter = 0;
            be.revalidateStructure();
            if (be.running && !be.structureOk) {
                be.collapse();
                return;
            }
            if (be.running) {
                be.tryAutoExtend();
                be.sync(); // one-second resolution is plenty for the cube
            }
        }
        if (!be.running) {
            if (be.structureOk) {
                be.tryStart();
            }
            return;
        }
        // one matter item every ten productive seconds; a full output side
        // holds the pending item and halves the countdown until room appears
        be.produceMatter();
        if (be.energy < EU_PER_TICK) {
            be.pullEnergyFromHatches();
        }
        // decay priority: no energy (10x) outranks a blocked output (0.5x)
        if (be.energy >= EU_PER_TICK) {
            be.energy -= EU_PER_TICK;
            if (!be.outputBlocked) {
                be.remaining--;
                // progress ticks only while the machine actually produces
                be.ticksTowardItem++;
            } else if (be.slowPhase = !be.slowPhase) {
                be.remaining--;
            }
        } else {
            // starving the projector destabilizes the universe: ten times
            // the normal decay instead of a pause
            be.remaining -= 10;
        }
        if (be.remaining <= 0) {
            be.finish();
        }
    }

    // -- MenuProvider + WorldlyContainer -------------------------------------------

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mi_nested_infinity.microverse_projector");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MicroverseMenu(id, playerInventory, this);
    }

    @Override
    public int[] getSlotsForFace(Direction face) {
        // heart in from the top/sides, matter out of the bottom; the legacy
        // ball slot is closed to automation (hatches only)
        return face == Direction.DOWN ? new int[] {OUTPUT_SLOT} : new int[] {HEART_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return heart.isEmpty() && balls.isEmpty() && matterOutput.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case HEART_SLOT -> heart;
            case BALL_SLOT -> balls;
            default -> matterOutput;
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
            case HEART_SLOT -> heart = stack;
            case BALL_SLOT -> balls = stack;
            default -> matterOutput = stack;
        }
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case HEART_SLOT -> stack.is(MicroverseItems.HEART_OF_A_NONEXISTENT_WORLD.get()) && !running;
            default -> false; // matter balls: item input hatches only
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        heart = ItemStack.EMPTY;
        balls = ItemStack.EMPTY;
        matterOutput = ItemStack.EMPTY;
        setChanged();
    }

    // -- persistence + client sync ---------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!heart.isEmpty()) {
            tag.put("heart", heart.save(registries));
        }
        if (!balls.isEmpty()) {
            tag.put("balls", balls.save(registries));
        }
        if (!matterOutput.isEmpty()) {
            tag.put("matter", matterOutput.save(registries));
        }
        tag.putLong("energy", energy);
        tag.putBoolean("running", running);
        tag.putInt("tier", tier);
        tag.putInt("remaining", remaining);
        tag.putInt("totalDuration", totalDuration);
        tag.putInt("ticksTowardItem", ticksTowardItem);
        tag.putInt("yielded", yieldedThisRun);
        tag.putInt("extensions", extensions);
        tag.putBoolean("outputBlocked", outputBlocked);
        tag.putInt("pending", pendingSingularities.size());
        for (int i = 0; i < pendingSingularities.size(); i++) {
            tag.putByte("pending" + i, (byte) (int) pendingSingularities.get(i));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        heart = ItemStack.parseOptional(registries, tag.getCompound("heart"));
        balls = ItemStack.parseOptional(registries, tag.getCompound("balls"));
        matterOutput = ItemStack.parseOptional(registries, tag.getCompound("matter"));
        energy = tag.getLong("energy");
        running = tag.getBoolean("running");
        tier = tag.getInt("tier");
        remaining = tag.getInt("remaining");
        totalDuration = tag.getInt("totalDuration");
        ticksTowardItem = tag.getInt("ticksTowardItem");
        yieldedThisRun = tag.getInt("yielded");
        extensions = tag.getInt("extensions");
        outputBlocked = tag.getBoolean("outputBlocked");
        pendingSingularities.clear();
        for (int i = 0; i < tag.getInt("pending"); i++) {
            pendingSingularities.add((int) tag.getByte("pending" + i) & 0xFF);
        }
    }

    /** Client sync keeps the BER's sphere and the GUI's live readouts honest. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("running", running);
        tag.putInt("remaining", remaining);
        tag.putInt("totalDuration", totalDuration);
        tag.putInt("tier", tier);
        tag.putLong("energy", energy);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        running = tag.getBoolean("running");
        remaining = tag.getInt("remaining");
        totalDuration = tag.getInt("totalDuration");
        tier = tag.getInt("tier");
        energy = tag.getLong("energy");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sync() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }
}
