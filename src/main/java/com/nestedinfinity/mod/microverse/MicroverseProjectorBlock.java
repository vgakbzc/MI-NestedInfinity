package com.nestedinfinity.mod.microverse;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import com.mojang.serialization.MapCodec;

/**
 * The projector controller: the center block of layer 3. Right-click opens
 * the machine GUI; the server tick runs the whole projection state machine
 * (see {@link MicroverseProjectorBlockEntity}). The RUNNING property swaps
 * the front overlay texture while a universe exists.
 */
public class MicroverseProjectorBlock extends BaseEntityBlock {
    public static final MapCodec<MicroverseProjectorBlock> CODEC = simpleCodec(MicroverseProjectorBlock::new);
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    public MicroverseProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUNNING);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MicroverseProjectorBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, MicroverseBlocks.PROJECTOR_TYPE.get(),
                (serverLevel, pos, blockState, be) -> MicroverseProjectorBlockEntity.tick(be));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof MicroverseProjectorBlockEntity projector) {
            if (!level.isClientSide()) {
                player.openMenu(projector, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
