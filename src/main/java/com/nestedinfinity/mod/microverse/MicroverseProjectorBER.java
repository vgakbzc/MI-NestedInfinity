package com.nestedinfinity.mod.microverse;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;

/**
 * The projected universe: a radius-2 sphere centered three blocks above the
 * controller while a run is active, drawn with the vanilla end-portal
 * render type (the animated starfield shader — position-only format).
 * It scales in over the first two seconds and collapses over the final two
 * (spec doc section 7).
 */
public class MicroverseProjectorBER implements BlockEntityRenderer<MicroverseProjectorBlockEntity> {
    /** Center of the sphere, in blocks above the controller's origin. */
    private static final double CENTER_Y = 3.5;

    public MicroverseProjectorBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MicroverseProjectorBlockEntity be, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!be.isRunning() || be.getTotalDuration() <= 0) {
            return;
        }
        float total = be.getTotalDuration();
        float remaining = be.getRemaining();
        float radius = 2.0F;
        // scale in over the first 40 ticks, out over the last 40
        if (total - remaining < 40.0F) {
            radius *= (total - remaining) / 40.0F;
        } else if (remaining < 40.0F) {
            radius *= remaining / 40.0F;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, CENTER_Y, 0.5);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.endPortal());
        var pose = poseStack.last().pose();
        for (int[] tri : TRIANGLES) {
            // both windings: the starfield shader's face culling state is not ours to rely on
            for (int i = 0; i < 3; i++) {
                emit(consumer, pose, VERTICES[tri[i]], radius);
            }
            for (int i = 2; i >= 0; i--) {
                emit(consumer, pose, VERTICES[tri[i]], radius);
            }
        }
        poseStack.popPose();
    }

    private static void emit(VertexConsumer consumer, org.joml.Matrix4f pose, float[] v, float radius) {
        consumer.addVertex(pose, v[0] * radius, v[1] * radius, v[2] * radius);
    }

    @Override
    public boolean shouldRenderOffScreen(MicroverseProjectorBlockEntity be) {
        return be.isRunning();
    }

    @Override
    public int getViewDistance() {
        return 192;
    }

    // -- a coarse sphere: eight octahedron octants, midpoint-subdivided ------------

    private static final int STEPS = 4;
    private static final int OCTANT_VERTS = (STEPS + 1) * (STEPS + 2) / 2;
    private static final float[][] OCTANT_DIRS = {
            {0, 1, 0}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {0, -1, 0}
    };
    private static final int[][] OCTANTS = {
            {0, 1, 2}, {0, 2, 3}, {0, 3, 4}, {0, 4, 1},
            {5, 2, 1}, {5, 3, 2}, {5, 4, 3}, {5, 1, 4}
    };

    // Must be declared after the lattice constants above: Java runs static
    // initializers in textual order, and these two read OCTANTS/OCTANT_DIRS.
    /** Subdivided-octahedron vertices of a unit sphere (mirrored octants). */
    private static final float[][] VERTICES = buildSphere();
    /** Triangle indices into {@link #VERTICES}. */
    private static final int[][] TRIANGLES = buildTriangles();

    private static float[][] buildSphere() {
        float[][] verts = new float[OCTANTS.length * OCTANT_VERTS][];
        for (int oct = 0; oct < OCTANTS.length; oct++) {
            float[] a = OCTANT_DIRS[OCTANTS[oct][0]];
            float[] b = OCTANT_DIRS[OCTANTS[oct][1]];
            float[] c = OCTANT_DIRS[OCTANTS[oct][2]];
            int n = 0;
            for (int i = 0; i <= STEPS; i++) {
                for (int j = 0; j + i <= STEPS; j++) {
                    float k = STEPS - i - j;
                    float x = a[0] * i + b[0] * j + c[0] * k;
                    float y = a[1] * i + b[1] * j + c[1] * k;
                    float z = a[2] * i + b[2] * j + c[2] * k;
                    float len = (float) Math.sqrt(x * x + y * y + z * z);
                    verts[oct * OCTANT_VERTS + n++] = new float[] {x / len, y / len, z / len};
                }
            }
        }
        return verts;
    }

    /**
     * Triangles of one octant's triangular lattice, offset per octant. Row r
     * has STEPS+1-r vertices starting at rowStart; the next row starts
     * STEPS+1-r later and is one vertex shorter, so the downward triangle of
     * the last cell of each row does not exist.
     */
    private static int[][] buildTriangles() {
        java.util.List<int[]> tris = new java.util.ArrayList<>();
        int rowStart = 0;
        for (int row = 0; row < STEPS; row++) {
            int next = rowStart + (STEPS + 1 - row);
            for (int col = 0; col + row < STEPS; col++) {
                tris.add(new int[] {rowStart + col, rowStart + col + 1, next + col});
                if (col + 2 + row <= STEPS) {
                    tris.add(new int[] {rowStart + col + 1, next + col, next + col + 1});
                }
            }
            rowStart = next;
        }
        int[][] one = tris.toArray(new int[0][]);
        int[][] all = new int[OCTANTS.length * one.length][];
        for (int oct = 0; oct < OCTANTS.length; oct++) {
            for (int t = 0; t < one.length; t++) {
                int[] tri = one[t];
                all[oct * one.length + t] = new int[] {
                        oct * OCTANT_VERTS + tri[0], oct * OCTANT_VERTS + tri[1], oct * OCTANT_VERTS + tri[2]};
            }
        }
        return all;
    }
}
