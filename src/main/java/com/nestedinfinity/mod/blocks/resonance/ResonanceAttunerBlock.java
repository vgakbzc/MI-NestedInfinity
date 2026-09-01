package com.nestedinfinity.mod.blocks.resonance;

import com.nestedinfinity.mod.blocks.NIBlocks;
import com.nestedinfinity.mod.items.resonance.NINotes;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    // Direct hand processing: one note in, one product back into the player's
    // inventory. Hopper automation goes through the block entity's input slot
    // instead (see tick), so both paths share the exact same Q8 step.
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof ResonanceAttunerBlockEntity attuner)) {
            return InteractionResult.PASS;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = player.getItemInHand(hand);
            NINotes note = NINotes.byItem(held.getItem());
            if (note == null) {
                continue;
            }
            NINotes product = attuner.tryProcess(note);
            if (product != null) {
                held.shrink(1);
                ItemStack produced = new ItemStack(product.item.get());
                if (!player.getInventory().add(produced)) {
                    player.drop(produced, false);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
