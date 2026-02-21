package net.spit365.clienttweaks.config;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.renderer.BoxOutlineRenderer;
import net.spit365.clienttweaks.util.BoxContext;
import net.spit365.clienttweaks.util.ConfigManager;
import net.spit365.clienttweaks.util.EdgeCalculator;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoxOutlineConfig {
    public static final String BOX_OUTLINES_ID = "box_outlines";

    public static Set<BoxOutlineRenderer.ColoredEdge> getCachedEdges() {
        return cachedEdges;
    }

    private static Set<BoxOutlineRenderer.ColoredEdge> cachedEdges = EdgeCalculator.getColoredEdges(getPermanentBoxOutlineState());

    public static Set<BoxContext> getPermanentBoxOutlineState() {
        return ConfigManager.read(ConfigManager.file(), BOX_OUTLINES_ID).values().stream()
            .map(value -> {
                JSONObject serializedBox = (JSONObject) value;
                Vec3d min = new Vec3d(
                    (Double) serializedBox.get("minX"),
                    (Double) serializedBox.get("minY"),
                    (Double) serializedBox.get("minZ")
                );
                Vec3d max = new Vec3d(
                    (Double) serializedBox.get("maxX"),
                    (Double) serializedBox.get("maxY"),
                    (Double) serializedBox.get("maxZ")
                );
                return new BoxContext(
                    new Box(min, max),
                    Integer.parseInt((String) serializedBox.get("color"), 16)
                );
            })
            .collect(Collectors.toUnmodifiableSet());
    }

    public static void setPermanentBoxOutlineState(Set<BoxContext> permanentBoxOutlineState) {
        int[] index = {0};
        ConfigManager.write("box_outlines", new JSONObject(
            permanentBoxOutlineState.stream().collect(Collectors.toMap(
                boxContext -> String.valueOf(index[0]++),
                boxContext -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("color", Integer.toHexString(boxContext.color()));
                    Box box = boxContext.box();
                    jsonObject.put("minX", box.minX);
                    jsonObject.put("minY", box.minY);
                    jsonObject.put("minZ", box.minZ);
                    jsonObject.put("maxX", box.maxX);
                    jsonObject.put("maxY", box.maxY);
                    jsonObject.put("maxZ", box.maxZ);
                    return jsonObject;
                }
            ))
        ));
        updateCachedEdges(permanentBoxOutlineState);
    }

    private static void updateCachedEdges(Set<BoxContext> permanentBoxOutlineState) {
        cachedEdges = EdgeCalculator.getColoredEdges(permanentBoxOutlineState);
    }
}
