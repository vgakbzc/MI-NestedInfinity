package com.nestedinfinity.mod.microverse;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The singularity catalyzer: a standalone no-energy machine that condenses
 * universe matter back into singularities (see
 * {@link SingularityCatalyzerBlockEntity}). Right-click opens its GUI.
 */
public class SingularityCatalyzerBlock extends BaseEntityBlock {
    public static final MapCodec<SingularityCatalyzerBlock> CODEC = simpleCodec(SingularityCatalyzerBlock::new);

    public SingularityCatalyzerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SingularityCatalyzerBlockEntity(pos, state);
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
        return createTickerHelper(type, MicroverseBlocks.CATALYZER_TYPE.get(),
                (serverLevel, pos, blockState, be) -> be.serverTick());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof SingularityCatalyzerBlockEntity catalyzer) {
            if (!level.isClientSide()) {
                player.openMenu(catalyzer, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    // justice and whimsy are armed by their neighbors changing (a flame snuffed,
    // a gold block spirited away) — the block entity tracks what it saw
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
            BlockPos neighborPos, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof SingularityCatalyzerBlockEntity catalyzer) {
            catalyzer.onNeighborChanged(neighborPos);
        }
    }

    // fury is armed by an arrow striking the machine
    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide() && projectile instanceof AbstractArrow
                && level.getBlockEntity(hit.getBlockPos()) instanceof SingularityCatalyzerBlockEntity catalyzer) {
            catalyzer.prime();
        }
    }
}
