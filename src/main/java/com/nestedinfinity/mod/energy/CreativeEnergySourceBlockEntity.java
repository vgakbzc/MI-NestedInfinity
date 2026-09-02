package com.nestedinfinity.mod.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;

/**
 * Holds nothing: the storage is a stateless, never-draining
 * {@link MIEnergyStorage} shared by every side. The server tick actively
 * pushes {@link Long#MAX_VALUE} EU into each adjacent storage so consumers
 * are fed even when they do not pull.
 */
public class CreativeEnergySourceBlockEntity extends BlockEntity {
    /** Extract-only and infinite: whatever is asked for, through any face. */
    static final MIEnergyStorage STORAGE = new MIEnergyStorage() {
        @Override
        public long receive(long max, boolean simulate) {
            return 0;
        }

        @Override
        public long extract(long max, boolean simulate) {
            return max;
        }

        @Override
        public long getAmount() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        public boolean canConnect(CableTier cableTier) {
            return true;
        }
    };

    public CreativeEnergySourceBlockEntity(BlockPos pos, BlockState state) {
        super(CreativeEnergySource.TYPE.get(), pos, state);
    }

    /** Flood every adjacent EU storage with as much as it will take. */
    static void push(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            MIEnergyStorage target = level.getCapability(EnergyApi.SIDED, pos.relative(dir), dir.getOpposite());
            if (target != null) {
                target.receive(Long.MAX_VALUE, false);
            }
        }
    }
}
