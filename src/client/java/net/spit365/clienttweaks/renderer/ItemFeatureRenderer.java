package net.spit365.clienttweaks.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import org.joml.Quaternionf;

import java.util.Arrays;

public class ItemFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final ItemRenderer itemRenderer;
    private final ClientWorld world;

    public ItemFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
        MinecraftClient instance = MinecraftClient.getInstance();
        this.world = instance.world;
        this.itemRenderer = instance.getItemRenderer();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        JSONObject[] loadedCustomCosmetics = CosmeticsConfig.getLoadedCustomCosmetics();
        Arrays.stream(loadedCustomCosmetics)
            .filter(jsonObject ->
                CosmeticsConfig.getEnabledCosmetic(
                    (String) jsonObject.get("name")
                ).containsKey(state.name))
            .forEach(cosmetic -> {
                ItemStack stack = new ItemStack(Registries.ITEM.get(Identifier.of((String) cosmetic.get("item"))));
                matrices.push();
                if (cosmetic.get("matrix_operations") instanceof JSONArray matrixOperations)
                    setupMatrices(matrices, matrixOperations);
                itemRenderer.renderItem(
                    stack,
                    ItemDisplayContext.GROUND,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    world,
                    (world == null? Random.create() : world.random).nextInt()
                );
                matrices.pop();
            });
    }

    private static void setupMatrices(MatrixStack matrices, JSONArray matrixOperations) {
        matrixOperations.stream().map(JSONObject.class::cast).forEach(matrixOperation -> {
            switch ((String) matrixOperation.get("type")){
                case "translate" -> matrices.translate(
                    dtf(matrixOperation.get("x")),
                    dtf(matrixOperation.get("y")),
                    dtf(matrixOperation.get("z"))
                );
                case "scale" -> matrices.scale(
                    dtf(matrixOperation.get("x")),
                    dtf(matrixOperation.get("y")),
                    dtf(matrixOperation.get("z"))
                );
                case "rotate" -> matrices.multiply(new Quaternionf().rotationAxis(
                    (float) Math.toRadians((Double) matrixOperation.get("degrees")),
                    dtf(matrixOperation.get("x")),
                    dtf(matrixOperation.get("y")),
                    dtf(matrixOperation.get("z"))
                ));
                default -> throw new RuntimeException("Unsupported matrix operation in one of the cosmetics!");
            }
        });
    }

    private static float dtf(Object input) {
        return ((Double) input).floatValue();
    }
}
