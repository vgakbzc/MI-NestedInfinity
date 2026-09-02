package com.nestedinfinity.mod.microverse;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.blockentities.hatches.EnergyHatch;
import aztech.modern_industrialization.machines.blockentities.hatches.ItemHatch;
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
 * consumes the heart and the twelve singularities.</li>
 * <li>Running: 2G EU/t pauses nothing else — missing energy freezes the
 * countdown and the accrual. Output accrues at 2.1^(tier-1) per second of
 * existence (fixed-point micro-items). The "extend" action (GUI button)
 * spends giant matter balls (1, then doubling) for +50% base time each.</li>
 * <li>Finish: the accrued matter is output (overflow drops above the
 * controller), and each singularity returns independently with probability
 * max(0, 95% - 5% * extensions), back into its coreflame.</li>
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

    /** Base stability time in ticks: t1 = 20, then x2.1 per tier. */
    public static int baseTicks(int tier) {
        return (int) Math.round(20.0 * Math.pow(2.1, tier - 1));
    }

    /** Accrual rate in micro-items (1e-6) per tick: 2.1^(tier-1) per second. */
    public static long microPerTick(int tier) {
        return Math.round(Math.pow(2.1, tier - 1) * 50_000.0);
    }

    private ItemStack heart = ItemStack.EMPTY;
    private ItemStack balls = ItemStack.EMPTY;
    private ItemStack matterOutput = ItemStack.EMPTY;

    private long energy = 0;
    private boolean running = false;
    private int tier = 0;
    private int remaining = 0;
    private int totalDuration = 0;
    private long accruedMicro = 0;
    private int extensions = 0;
    private int ballCost = 1;
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
        return (int) (accruedMicro / 1_000_000L);
    }

    public int getExtensions() {
        return extensions;
    }

    public int getBallCost() {
        return ballCost;
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
        // consume the heart and exactly one singularity, picked at random
        // from the twelve filled flames (each run burns one coreflame)
        heart = heart.copyWithCount(heart.getCount() - 1);
        if (heart.isEmpty()) {
            heart = ItemStack.EMPTY;
        }
        pendingSingularities.clear();
        List<Integer> filled = new ArrayList<>();
        for (int i = 0; i < MicroverseStructure.COREFLAME_POS.length; i++) {
            int[] rc = MicroverseStructure.COREFLAME_POS[i];
            BlockPos p = worldPosition.below().offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame && flame.isFilled()) {
                filled.add(i);
            }
        }
        if (!filled.isEmpty()) {
            int pick = filled.get(level.random.nextInt(filled.size()));
            int[] rc = MicroverseStructure.COREFLAME_POS[pick];
            BlockPos p = worldPosition.below().offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame) {
                flame.consumeSingularity();
            }
            pendingSingularities.add(pick);
            flameMask &= ~(1 << pick);
        }
        tier = structureTier;
        remaining = totalDuration = baseTicks(tier);
        accruedMicro = 0;
        extensions = 0;
        ballCost = 1;
        running = true;
        // flameMask already reflects the one burned flame; revalidation keeps
        // it in sync with the eleven still-filled coreflames
        setRunningState(true);
        level.playSound(null, worldPosition, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.4F, 1.5F);
        setChanged();
        sync();
    }

    /**
     * The GUI's extend button: spends the current giant-matter-ball
     * requirement for +50% of the base time. Returns false when idle or
     * out of balls. The same extension also fires automatically from the
     * structure's item input hatches (see {@link #tryAutoExtend()}).
     */
    public boolean tryExtend() {
        if (!running || level == null || level.isClientSide()) {
            return false;
        }
        if (balls.isEmpty() || balls.getCount() < ballCost
                || !balls.is(com.nestedinfinity.mod.items.NIOpticalItems.GIANT_MATTER_BALL.get())) {
            return false;
        }
        balls = balls.copyWithCount(balls.getCount() - ballCost);
        if (balls.isEmpty()) {
            balls = ItemStack.EMPTY;
        }
        applyExtension();
        return true;
    }

    /** Shared extend math: +50% of the base time, the next ball costs double. */
    private void applyExtension() {
        int add = Math.max(1, baseTicks(tier) / 2);
        remaining += add;
        totalDuration += add;
        extensions++;
        ballCost = Math.min(ballCost << 1, 1 << 20);
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
     * The ball auto-feeder: once per structure pass, giant matter balls in
     * the item input hatches are detected and spent on an extension without
     * any button press. Feeding hatches is how you keep a universe alive.
     */
    private void tryAutoExtend() {
        if (!running || level == null || level.isClientSide()) {
            return;
        }
        Item ball = com.nestedinfinity.mod.items.NIOpticalItems.GIANT_MATTER_BALL.get();
        int available = 0;
        for (BlockPos p : itemInputHatches) {
            if (level.getBlockEntity(p) instanceof ItemHatch hatch) {
                for (var stack : hatch.getInventory().getItemStacks()) {
                    if (!stack.isEmpty() && stack.getResource() == ball) {
                        available += (int) stack.getAmount();
                    }
                }
            }
        }
        if (available < ballCost) {
            return;
        }
        int need = ballCost;
        for (BlockPos p : itemInputHatches) {
            if (need <= 0) {
                break;
            }
            if (level.getBlockEntity(p) instanceof ItemHatch hatch) {
                for (var stack : hatch.getInventory().getItemStacks()) {
                    if (need <= 0) {
                        break;
                    }
                    if (!stack.isEmpty() && stack.getResource() == ball) {
                        long take = Math.min(need, stack.getAmount());
                        stack.decrement(take);
                        need -= (int) take;
                    }
                }
            }
        }
        applyExtension();
    }

    private void finish() {
        running = false;
        int runTier = tier;
        tier = 0;
        int count = (int) (accruedMicro / 1_000_000L);
        accruedMicro = 0;
        ItemStack produced = new ItemStack(MicroverseItems.MATTERS.get(runTier - 1).get());
        // fill the output slot, drop the overflow on top of the controller
        int room = produced.getMaxStackSize() - matterOutput.getCount();
        if (matterOutput.isEmpty()) {
            room = produced.getMaxStackSize();
        }
        if (matterOutput.isEmpty() || ItemStack.isSameItemSameComponents(matterOutput, produced)) {
            int intoSlot = Math.min(count, room);
            matterOutput = matterOutput.isEmpty() ? produced.copyWithCount(intoSlot)
                    : matterOutput.copyWithCount(matterOutput.getCount() + intoSlot);
            count -= intoSlot;
        }
        while (count > 0) {
            int drop = Math.min(count, produced.getMaxStackSize());
            ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5, produced.copyWithCount(drop));
            level.addFreshEntity(entity);
            count -= drop;
        }
        // the singularity return rolls
        int chance = getReturnChance();
        List<Integer> returned = new ArrayList<>();
        for (int index : pendingSingularities) {
            if (level.random.nextInt(100) < chance) {
                returned.add(index);
            }
        }
        pendingSingularities.clear();
        for (int[] rc : MicroverseStructure.COREFLAME_POS) {
            BlockPos p = worldPosition.below().offset(rc[1] - 3, 0, rc[0] - 3);
            if (level.getBlockEntity(p) instanceof CoreflameBlockEntity flame
                    && flame.getSingularity().isEmpty()) {
                int index = MicroverseBlocks.coreflameIndex(level.getBlockState(p).getBlock());
                if (returned.remove((Integer) index)) {
                    flame.returnSingularity(new ItemStack(MicroverseItems.SINGULARITIES.get(index).item().get()));
                }
            }
        }
        extensions = 0;
        ballCost = 1;
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
        accruedMicro = 0;
        extensions = 0;
        ballCost = 1;
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
        if (++be.recheckCounter >= 20) {
            be.recheckCounter = 0;
            be.revalidateStructure();
            if (be.running && !be.structureOk) {
                be.collapse();
                return;
            }
            if (be.running) {
                be.tryAutoExtend();
                be.sync(); // one-second resolution is plenty for the sphere
            }
        }
        if (!be.running) {
            if (be.structureOk) {
                be.tryStart();
            }
            return;
        }
        if (be.energy < EU_PER_TICK) {
            be.pullEnergyFromHatches();
        }
        if (be.energy >= EU_PER_TICK) {
            be.energy -= EU_PER_TICK;
            be.remaining--;
            be.accruedMicro += microPerTick(be.tier);
            if (be.remaining <= 0) {
                be.finish();
            }
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
        // heart and balls in from the top/sides, matter out of the bottom
        return face == Direction.DOWN ? new int[] {OUTPUT_SLOT} : new int[] {HEART_SLOT, BALL_SLOT};
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
            case BALL_SLOT -> stack.is(com.nestedinfinity.mod.items.NIOpticalItems.GIANT_MATTER_BALL.get());
            default -> false;
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
        tag.putLong("accruedMicro", accruedMicro);
        tag.putInt("extensions", extensions);
        tag.putInt("ballCost", ballCost);
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
        accruedMicro = tag.getLong("accruedMicro");
        extensions = tag.getInt("extensions");
        ballCost = Math.max(1, tag.getInt("ballCost"));
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
