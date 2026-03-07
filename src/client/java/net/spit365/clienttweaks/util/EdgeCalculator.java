package net.spit365.clienttweaks.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.renderer.BoxOutlineRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Environment(EnvType.CLIENT)
public final class EdgeCalculator {
    public static @NotNull Set<BoxOutlineRenderer.ColoredEdge> getColoredEdges(Set<BoxContext> newState) {
        Int2ObjectOpenHashMap<List<Box>> byColor = new Int2ObjectOpenHashMap<>();
        Set<BoxOutlineRenderer.ColoredEdge> result = new HashSet<>();
        for (BoxContext bc : newState) byColor.computeIfAbsent(bc.color(), k -> new ArrayList<>()).add(bc.box());
        for (Int2ObjectMap.Entry<List<Box>> entry : byColor.int2ObjectEntrySet()) {
            int color = entry.getIntKey();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = (color >>> 24) != 0 ? ((color >>> 24) & 0xFF) / 255f : 1.0f;

            for (Edge visibleEdge : getVisibleEdges(entry.getValue().toArray(Box[]::new))) {
                result.add(new BoxOutlineRenderer.ColoredEdge(visibleEdge, r, g, b, a));
            }
        }
        return result;
    }

    public static Set<Edge> getVisibleEdges(Box... boxes) {
        double[] X = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minX, b.maxX)).distinct().sorted().toArray();
        double[] Y = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minY, b.maxY)).distinct().sorted().toArray();
        double[] Z = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minZ, b.maxZ)).distinct().sorted().toArray();

        int nx = X.length - 1;
        int ny = Y.length - 1;
        int nz = Z.length - 1;
        boolean[][][] solid = new boolean[nx][ny][nz];

        for (int i = 0; i < nx; i++)
            for (int j = 0; j < ny; j++)
                for (int k = 0; k < nz; k++)
                    for (Box b : boxes)
                        if (boxContainsBox(b, X[i], Y[j], Z[k], X[i + 1], Y[j + 1], Z[k + 1])) {
                            solid[i][j][k] = true;
                            break;
                        }

        Map<PlaneKey, List<Rect>> faces = new HashMap<>();

        for (int i = 0; i < nx; i++)
            for (int j = 0; j < ny; j++)
                for (int k = 0; k < nz; k++) {
                    if (!solid[i][j][k]) continue;
                    for (int[] d : DIRS) {
                        int ni = i + d[0], nj = j + d[1], nk = k + d[2];
                        if (
                            ni >= 0 &&
                                nj >= 0 &&
                                nk >= 0 &&
                                ni < nx &&
                                nj < ny &&
                                nk < nz &&
                                solid[ni][nj][nk]
                        ) continue;
                        double x0 = X[i], x1 = X[i + 1];
                        double y0 = Y[j], y1 = Y[j + 1];
                        double z0 = Z[k], z1 = Z[k + 1];

                        if (d[0] != 0)
                            faces.computeIfAbsent(new PlaneKey(Direction.Axis.X, d[0] < 0 ? x0 : x1), k2 -> new ArrayList<>()).add(new Rect(y0, z0, y1, z1));
                        else if (d[1] != 0)
                            faces.computeIfAbsent(new PlaneKey(Direction.Axis.Y, d[1] < 0 ? y0 : y1), k2 -> new ArrayList<>()).add(new Rect(x0, z0, x1, z1));
                        else
                            faces.computeIfAbsent(new PlaneKey(Direction.Axis.Z, d[2] < 0 ? z0 : z1), k2 -> new ArrayList<>()).add(new Rect(x0, y0, x1, y1));

                    }
                }

        Set<Edge> result = new HashSet<>();

        for (Map.Entry<PlaneKey, List<Rect>> entry : faces.entrySet()) {
            PlaneKey key = entry.getKey();
            List<Rect> rects = entry.getValue();

            Set<Segment> outline = computeOutline(rects);

            for (Segment s : outline) {
                switch (key.axis) {
                    case X -> addEdge(result, key.coord, s.u0, s.v0, key.coord, s.u1, s.v1);
                    case Y -> addEdge(result, s.u0, key.coord, s.v0, s.u1, key.coord, s.v1);
                    case Z -> addEdge(result, s.u0, s.v0, key.coord, s.u1, s.v1, key.coord);
                }
            }
        }

        return result;
    }

    private static final int[][] DIRS = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};

    private static boolean boxContainsBox(Box box, double x0, double y0, double z0, double x1, double y1, double z1) {
        return box.minX <= x0 && box.maxX >= x1 && box.minY <= y0 && box.maxY >= y1 && box.minZ <= z0 && box.maxZ >= z1;
    }

    private static void addEdge(Set<Edge> set, double x1, double y1, double z1, double x2, double y2, double z2) {
        Vec3d a = new Vec3d(x1, y1, z1);
        Vec3d b = new Vec3d(x2, y2, z2);

        int compare;
        if (a.x != b.x) compare = Double.compare(a.x, b.x);
        else if (a.y != b.y) compare = Double.compare(a.y, b.y);
        else compare = Double.compare(a.z, b.z);

        set.add(compare <= 0 ? new Edge(a, b) : new Edge(b, a));
    }

    private static Set<Segment> computeOutline(List<Rect> rects) {
        Map<Segment, Integer> edges = new HashMap<>();

        for (Rect r : rects) {
            edges.merge(new Segment(r.u0, r.v0, r.u1, r.v0), 1, Integer::sum);
            edges.merge(new Segment(r.u1, r.v0, r.u1, r.v1), 1, Integer::sum);
            edges.merge(new Segment(r.u1, r.v1, r.u0, r.v1), 1, Integer::sum);
            edges.merge(new Segment(r.u0, r.v1, r.u0, r.v0), 1, Integer::sum);
        }

        return edges.entrySet().stream().filter(e -> e.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private record PlaneKey(Direction.Axis axis, double coord) {
    }

    private record Rect(double u0, double v0, double u1, double v1) {
    }

    private record Segment(double u0, double v0, double u1, double v1) {
        public Segment {
            if (u0 > u1 || (u0 == u1 && v0 > v1)) {
                double tempU = u0;
                double tempV = v0;
                u0 = u1;
                v0 = v1;
                u1 = tempU;
                v1 = tempV;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Segment(double a1, double b1, double c1, double d1))) return false;
            return (u0 == a1 && v0 == b1 && u1 == c1 && v1 == d1) || (u0 == c1 && v0 == d1 && u1 == a1 && v1 == b1);
        }

        @Override
        public int hashCode() {
            return Double.hashCode(u0 + v0 + u1 + v1);
        }
    }

    public record Edge(Vec3d a, Vec3d b) {
        public Vec3d at(double t) {
            return a.lerp(b, t);
        }

        public double length() {
            return a.distanceTo(b);
        }
    }
}
