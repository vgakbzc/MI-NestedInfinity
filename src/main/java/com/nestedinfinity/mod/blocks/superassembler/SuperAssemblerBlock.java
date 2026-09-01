package com.nestedinfinity.mod.blocks.superassembler;

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
 * The super assembler: the optical program's grand finale machine. Right-click
 * opens a 10x10 grid — drop in one glow tube of every one of the hundred gem
 * colors and the machine assembles the optical qubit component (see
 * {@link SuperAssemblerBlockEntity}).
 */
public class SuperAssemblerBlock extends BaseEntityBlock {
    public static final MapCodec<SuperAssemblerBlock> CODEC = simpleCodec(SuperAssemblerBlock::new);

    public SuperAssemblerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuperAssemblerBlockEntity(pos, state);
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
        return createTickerHelper(type, NIBlocks.SUPER_ASSEMBLER_TYPE.get(),
                (serverLevel, pos, blockState, be) -> SuperAssemblerBlockEntity.tick(be));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof SuperAssemblerBlockEntity assembler) {
            if (!level.isClientSide()) {
                player.openMenu(assembler, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
