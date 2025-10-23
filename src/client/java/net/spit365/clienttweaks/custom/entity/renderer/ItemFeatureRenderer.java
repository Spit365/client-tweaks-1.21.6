package net.spit365.clienttweaks.custom.entity.renderer;

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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
        Arrays.stream(CosmeticsConfig.loadedCustomCosmetics).filter(jsonObject -> CosmeticsConfig.getEnabledCosmetics().containsKey((String) jsonObject.get("name"))).forEach(cosmetic -> {
			ClientTweaks.LOGGER.info("rendering cosmetic: name = {}, id = {}", cosmetic.get("name"), cosmetic.get("id"));
			if (cosmetic.get("matrix_operations") instanceof JSONArray matrixOperations)
                matrixOperations.stream().map(JSONObject.class::cast).forEach(matrixOperation -> {
					ClientTweaks.LOGGER.info("applying matrix operation: {}", matrixOperation);
                    switch ((String) matrixOperation.get("type")){
                        case "push" -> matrices.push();
                        case "pop" -> matrices.pop();
                        case "translate" -> matrices.translate(
                            (float) matrixOperation.get("x"),
                            (float) matrixOperation.get("y"),
                            (float) matrixOperation.get("z")
                        );
                        case "scale" -> matrices.scale(
                            (float) matrixOperation.get("x"),
                            (float) matrixOperation.get("y"),
                            (float) matrixOperation.get("z")
                        );
                        case "multiply" -> matrices.multiply(new Quaternionf(
                            (float) matrixOperation.get("x"),
                            (float) matrixOperation.get("y"),
                            (float) matrixOperation.get("z"),
                            (float) matrixOperation.get("w")
                        ));
                        case "rotate" -> matrices.multiply(new Quaternionf().rotationAxis(
                            (float) matrixOperation.get("degrees"),
                            (float) matrixOperation.get("x"),
                            (float) matrixOperation.get("y"),
                            (float) matrixOperation.get("z")
                        ));
                        default -> throw new RuntimeException("Unsupported matrix operation in one of the loaded, equipped, rendered cosmetics");
                    }
                });
			ClientTweaks.LOGGER.info("matrices ready: {}", matrices.peek());
            itemRenderer.renderItem(
                new ItemStack(Registries.ITEM.get(Identifier.of((String) cosmetic.get("id")))),
                ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                world,
                (world == null? Random.create() : world.random).nextInt()
            );
        });
    }
}
