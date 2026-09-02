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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

/**
 * One of the twelve coreflame kinds. Each kind accepts exactly its own
 * singularity (see {@link CoreflameBlockEntity}); the projector requires
 * all twelve kinds, each holding its singularity, before a projection can
 * start. Breaking the block drops the singularity with it.
 */
public class CoreflameBlock extends BaseEntityBlock {
    public static final MapCodec<CoreflameBlock> CODEC = simpleCodec(CoreflameBlock::new);

    /** The brazier stands 0.6 blocks tall, matching the shortened block model. */
    private static final VoxelShape SHAPE = Shapes.create(new AABB(0, 0, 0, 1, 0.6, 1));

    public CoreflameBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
