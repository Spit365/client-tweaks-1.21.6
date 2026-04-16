package net.spit365.clienttweaks.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.config.BoxOutlineConfig;
import net.spit365.clienttweaks.util.EdgeCalculator;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
import java.util.Set;

import static net.minecraft.client.gl.RenderPipelines.GLOBALS_SNIPPET;
import static net.minecraft.client.gl.RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET;
import static net.minecraft.client.render.RenderPhase.ITEM_ENTITY_TARGET;
import static net.minecraft.client.render.RenderPhase.VIEW_OFFSET_Z_LAYERING;

@Environment(EnvType.CLIENT)
public final class BoxOutlineRenderer {

    public static final RenderLayer.MultiPhase LINES = RenderLayer.of(
        "lines_boxoutlines",
        1536,
        RenderPipelines.register(
            RenderPipeline.builder(TRANSFORMS_PROJECTION_FOG_SNIPPET, GLOBALS_SNIPPET)
                .withVertexShader("core/rendertype_lines_boxoutlines")
                .withFragmentShader("core/rendertype_lines")
                .withBlend(BlendFunction.TRANSLUCENT)
                .withCull(false)
                .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)
                .withLocation("pipeline/lines").build()),
        RenderLayer.MultiPhaseParameters.builder()
            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
            .layering(VIEW_OFFSET_Z_LAYERING)
            .target(ITEM_ENTITY_TARGET)
            .build(false)
    );

    public static void render(Camera camera, MatrixStack matrices, VertexConsumerProvider vcp) {
        Set<ColoredEdge> permanentState = BoxOutlineConfig.getCachedEdges();
        if (BoxOutlineConfig.state.isEmpty() && permanentState.isEmpty()) return;

        Vec3d cam = camera.getPos();

        if (matrices == null) return;
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        if (vcp == null) return;
        VertexConsumer vc = vcp.getBuffer(LINES);
        Matrix4f mat = matrices.peek().getPositionMatrix();

        for (ColoredEdge coloredEdge : permanentState)         renderEdge(mat, vc, coloredEdge.edge, coloredEdge.r, coloredEdge.g, coloredEdge.b, coloredEdge.a);
        for (ColoredEdge coloredEdge : BoxOutlineConfig.state) renderEdge(mat, vc, coloredEdge.edge, coloredEdge.r, coloredEdge.g, coloredEdge.b, coloredEdge.a);

        matrices.pop();
        if (vcp instanceof VertexConsumerProvider.Immediate immediate) immediate.draw();
    }

    private static void renderEdge(Matrix4f mat, VertexConsumer vc, EdgeCalculator.Edge edge, float r, float g, float b, float a) {
        double len = edge.length();
        if (len <= 1e-12) return;
        double dt = 0.25 / len;
        for (double t = 0; t + dt <= 1d + 1e-12; t += dt) {
            Vec3d p0 = edge.at(t);
            Vec3d p1 = edge.at(Math.min(t + dt, 1));
            vc.vertex(mat, (float) p0.x, (float) p0.y, (float) p0.z).color(r, g, b, a).normal(0, 0, 0);
            vc.vertex(mat, (float) p1.x, (float) p1.y, (float) p1.z).color(r, g, b, a).normal(0, 0, 0);
        }
    }

    public record ColoredEdge(EdgeCalculator.Edge edge, float r, float g, float b, float a)  {}

    public static void init() {}
}
