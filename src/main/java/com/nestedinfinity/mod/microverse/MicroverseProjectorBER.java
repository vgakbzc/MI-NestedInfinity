package com.nestedinfinity.mod.microverse;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;

/**
 * The projected universe: a starfield cube (the vanilla end-portal render
 * type, position-only format) centered three and a half blocks above the
 * controller while a run is active, tumbling around the X and Z axes at
 * different rates. It scales in over the first two seconds and collapses
 * over the final two (spec doc section 7).
 */
public class MicroverseProjectorBER implements BlockEntityRenderer<MicroverseProjectorBlockEntity> {
    /** Center of the cube, in blocks above the controller's origin. */
    private static final double CENTER_Y = 3.5;
    /** Half edge of the cube (the old sphere's radius was 2.0). */
    private static final float HALF = 1.8F;

    /**
     * Tumble rates with exact wrap: 0.225 deg/tick over 1600 ticks is one
     * whole X turn every 80 s, 0.15 deg/tick over 2400 ticks one Z turn every
     * 120 s — the modulo never glitches and the angle never grows into float
     * imprecision.
     */
    private static final int X_PERIOD = 1600;
    private static final float X_DEG_PER_TICK = 0.225F;
    private static final int Z_PERIOD = 2400;
    private static final float Z_DEG_PER_TICK = 0.15F;

    /** The eight cube corners, x/y/z each -1 or +1, bit pattern xyz. */
    private static final float[][] CORNERS = new float[8][];
    /** The six faces as corner indices. */
    private static final int[][] FACES = {
            {0, 3, 2, 1}, // -Z
            {4, 5, 6, 7}, // +Z
            {0, 1, 5, 4}, // -Y
            {3, 7, 6, 2}, // +Y
            {1, 2, 6, 5}, // +X
            {0, 4, 7, 3}, // -X
    };

    static {
        for (int i = 0; i < 8; i++) {
            CORNERS[i] = new float[] {(i & 1) == 0 ? -1 : 1, (i & 2) == 0 ? -1 : 1, (i & 4) == 0 ? -1 : 1};
        }
    }

    public MicroverseProjectorBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MicroverseProjectorBlockEntity be, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (be.getLevel() == null || !be.isRunning() || be.getTotalDuration() <= 0) {
            return;
        }
        float total = be.getTotalDuration();
        float remaining = be.getRemaining();
        float half = HALF;
        // scale in over the first 40 ticks, out over the last 40
        if (total - remaining < 40.0F) {
            half *= (total - remaining) / 40.0F;
        } else if (remaining < 40.0F) {
            half *= remaining / 40.0F;
        }

        long time = be.getLevel().getGameTime();
        float angleX = (time % X_PERIOD + partialTick) * X_DEG_PER_TICK;
        float angleZ = (time % Z_PERIOD + partialTick) * Z_DEG_PER_TICK;

        poseStack.pushPose();
        poseStack.translate(0.5, CENTER_Y, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(angleX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(angleZ));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.endPortal());
        Matrix4f pose = poseStack.last().pose();
        for (int[] face : FACES) {
            // each quad twice, once per winding — the starfield shader's face
            // culling state is not ours to rely on (vanilla's EndPortalRenderer
            // does the same)
            for (int i = 0; i < 4; i++) {
                vertex(consumer, pose, CORNERS[face[i]], half);
            }
            for (int i = 3; i >= 0; i--) {
                vertex(consumer, pose, CORNERS[face[i]], half);
            }
        }
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, float[] v, float half) {
        consumer.addVertex(pose, v[0] * half, v[1] * half, v[2] * half);
    }

    @Override
    public boolean shouldRenderOffScreen(MicroverseProjectorBlockEntity be) {
        return be.isRunning();
    }

    @Override
    public int getViewDistance() {
        return 192;
    }
}
