package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.blocks.NIBlocks;

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
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;

/**
 * The resonance attuner: a single-block machine that needs a
 * {@link TuningBlock} placed directly above it. Right-click it with a tuning
 * note in hand (or feed notes by hopper) to run the Q8 step — the note that
 * comes back is {@code register × note}, and the tuning block adopts the
 * inserted note's color. See {@link ResonanceAttunerBlockEntity}.
 */
public class ResonanceAttunerBlock extends BaseEntityBlock {
    public static final MapCodec<ResonanceAttunerBlock> CODEC = simpleCodec(ResonanceAttunerBlock::new);

    public ResonanceAttunerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceAttunerBlockEntity(pos, state);
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
        return createTickerHelper(type, NIBlocks.RESONANCE_ATTUNER_TYPE.get(),
                (serverLevel, pos, blockState, be) -> ResonanceAttunerBlockEntity.tick(be));
    }

    // Right-click opens the two-slot GUI; notes placed in the input slot are
    // processed by the server tick (the same Q8 step the hoppers go through).
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ResonanceAttunerBlockEntity attuner) {
            if (!level.isClientSide()) {
                player.openMenu(attuner, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
