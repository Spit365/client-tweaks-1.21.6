package net.spit365.clienttweaks.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.util.ModUtil;
import org.joml.Quaternionf;

import java.util.Arrays;

public class ItemFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private ClientWorld world;

    public ItemFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
        this.world = MinecraftClient.getInstance().world;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (world == null) world = MinecraftClient.getInstance().world;
        JSONObject[] loadedCustomCosmetics = CosmeticsConfig.getLoadedCustomCosmetics();
        Arrays.stream(loadedCustomCosmetics)
            .filter(jsonObject ->
                CosmeticsConfig.getEnabledCosmetic(
                    (String) jsonObject.get("name")
                ).containsKey(ModUtil.getName(state)))
            .forEach(cosmetic -> {
                Identifier item = Identifier.of((String) cosmetic.get("item"));
                ItemStack stack = new ItemStack(Registries.ITEM.get(item));
                matrices.push();
                if (cosmetic.get("matrix_operations") instanceof JSONArray matrixOperations)
                    setupMatrices(matrices, matrixOperations);
                BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
                ItemModel model = bakedModelManager.getItemModel(item);
                ItemRenderState itemRenderState = new ItemRenderState();

                model.update(
                    itemRenderState,
                    stack,
                    new ItemModelManager(bakedModelManager),
                    ItemDisplayContext.GROUND,
                    world,
                    null,
                    world == null ? 0 : world.getRandom().nextInt()
                );

                itemRenderState.render(
                    matrices,
                    queue,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    0
                );

                matrices.pop();
            });
    }

    private static void setupMatrices(MatrixStack matrices, JSONArray matrixOperations) {
        matrixOperations.stream().map(JSONObject.class::cast).forEach(matrixOperation -> {
            switch ((String) matrixOperation.get("type")){
                case "push" -> matrices.push();
                case "pop" -> matrices.pop();
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
                case "multiply" -> matrices.multiply(new Quaternionf(
                    dtf(matrixOperation.get("x")),
                    dtf(matrixOperation.get("y")),
                    dtf(matrixOperation.get("z")),
                    dtf(matrixOperation.get("w"))
                ));
                case "rotate" -> matrices.multiply(new Quaternionf().rotationAxis(
                    dtf(matrixOperation.get("degrees")),
                    dtf(matrixOperation.get("x")),
                    dtf(matrixOperation.get("y")),
                    dtf(matrixOperation.get("z"))
                ));
                default -> throw new RuntimeException("Unsupported matrix operation in one of the loaded, equipped, rendered cosmetics");
            }
        });
    }

    private static float dtf(Object input) {
        return ((Double) input).floatValue();
    }
}
