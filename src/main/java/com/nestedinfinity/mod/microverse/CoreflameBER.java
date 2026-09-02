package com.nestedinfinity.mod.microverse;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * A filled coreflame keeps its captured singularity on display: a regular
 * octahedron in the flame's own color, hovering above the 0.6-block-tall
 * brazier and slowly spinning. Drawn on the vanilla white sprite with
 * forced full brightness so even the dark flames (shadow, evernight) stay
 * readable.
 */
public class CoreflameBER implements BlockEntityRenderer<CoreflameBlockEntity> {
    private static final ResourceLocation WHITE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    /** Center height above the brazier top (0.6) plus a hover gap, and the octahedron radius. */
    private static final double CENTER_Y = 0.95;
    private static final float RADIUS = 0.22F;

    private static final float[][] VERTS = {
            {1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {0, -1, 0}
    };
    private static final int[][] FACES = {
            {0, 1, 2}, {0, 2, 3}, {0, 3, 4}, {0, 4, 1},
            {5, 2, 1}, {5, 3, 2}, {5, 4, 3}, {5, 1, 4}
    };

    public CoreflameBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CoreflameBlockEntity be, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (be.getLevel() == null || !be.isFilled()) {
            return;
        }
        int index = MicroverseBlocks.coreflameIndex(be.getBlockState().getBlock());
        if (index < 0) {
            return;
        }
        int color = MicroverseItems.SINGULARITIES.get(index).color();
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;

        // 12000 ticks * 3 deg/tick is a whole number of turns, so the modulo
        // wrap is seamless and the angle never grows into float imprecision.
        float angle = (be.getLevel().getGameTime() % 12000 + partialTick) * 3.0F;

        poseStack.pushPose();
        poseStack.translate(0.5, CENTER_Y, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(WHITE));
        PoseStack.Pose pose = poseStack.last();
        for (int[] face : FACES) {
            // a regular octahedron's face normal is the sum of its three vertices
            float nx = VERTS[face[0]][0] + VERTS[face[1]][0] + VERTS[face[2]][0];
            float ny = VERTS[face[0]][1] + VERTS[face[1]][1] + VERTS[face[2]][1];
            float nz = VERTS[face[0]][2] + VERTS[face[1]][2] + VERTS[face[2]][2];
            float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            for (int i : face) {
                consumer.addVertex(pose.pose(), VERTS[i][0] * RADIUS, VERTS[i][1] * RADIUS, VERTS[i][2] * RADIUS)
                        .setColor(r, g, b, 255)
                        .setUv(0.5F, 0.5F)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(pose, nx / len, ny / len, nz / len);
            }
        }
        poseStack.popPose();
    }
}
