package com.nestedinfinity.mod.microverse;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.BlockHitResult;
import com.mojang.serialization.MapCodec;

/**
 * One of the twelve coreflame kinds. Each kind accepts exactly its own
 * singularity (see {@link CoreflameBlockEntity}); the projector requires
 * all twelve kinds, each holding its singularity, before a projection can
 * start. Breaking the block drops the singularity with it.
 */
public class CoreflameBlock extends BaseEntityBlock {
    public static final MapCodec<CoreflameBlock> CODEC = simpleCodec(CoreflameBlock::new);

    public CoreflameBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoreflameBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof CoreflameBlockEntity flame) {
            if (!level.isClientSide()) {
                player.openMenu(flame, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    /** Drops the contained singularity so it is never lost to a pickaxe. */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CoreflameBlockEntity flame) {
            Containers.dropContents(level, pos, flame);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
