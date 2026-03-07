package net.spit365.clienttweaks.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.config.BoxOutlineConfig;
import net.spit365.clienttweaks.util.BoxContext;
import net.spit365.clienttweaks.util.EdgeCalculator;
import org.joml.Matrix4f;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class BoxOutlineRenderer {
    private static Set<ColoredEdge> state = new HashSet<>();

    public static void setState(Set<BoxContext> newState) {
        state = EdgeCalculator.getColoredEdges(newState);
    }

    public static void init() {
        WorldRenderEvents.LAST.register(BoxOutlineRenderer::render);
    }

    private static void render(WorldRenderContext ctx) {
        Set<ColoredEdge> permanentState = BoxOutlineConfig.getCachedEdges();
        if (state.isEmpty() && permanentState.isEmpty()) return;

        var camera = ctx.camera();
        Vec3d cam = camera.getPos();

        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        VertexConsumerProvider vcp = ctx.consumers();
        if (vcp == null) return;
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getLines());
        Matrix4f mat = matrices.peek().getPositionMatrix();

        for (ColoredEdge coloredEdge : permanentState) renderEdge(mat, vc, coloredEdge.edge, coloredEdge.r, coloredEdge.g, coloredEdge.b, coloredEdge.a);
        for (ColoredEdge coloredEdge : state)          renderEdge(mat, vc, coloredEdge.edge, coloredEdge.r, coloredEdge.g, coloredEdge.b, coloredEdge.a);

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

}