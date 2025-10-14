package net.spit365.clienttweaks.custom.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.custom.entity.model.TailModel;
import net.spit365.clienttweaks.manager.ConfigManager;
import net.spit365.clienttweaks.manager.CosmeticManager;
import net.spit365.clienttweaks.mod.ClientMethods;

import java.util.stream.StreamSupport;

@Environment(EnvType.CLIENT)
public class TailFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> implements ConfigManager.DefaultedJsonReader{
	public TailFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		JSONObject category = CosmeticManager.getEnabledCosmetic("tail");
		if (isDev(state.name) || (category.containsKey(state.name)) && !state.invisible) {
			matrices.push();
			if (state.isInSneakingPose) matrices.translate(0f,  0.2f, 0.1f);
			ClientMethods.applyPartTransform(matrices, getContextModel().body);
			TailModel.getModel(state.isInSneakingPose).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(Identifier.of(stringOption((JSONObject) category.get(state.name), "texture")))), light, LivingEntityRenderer.getOverlay(state, 0f));
			matrices.pop();
		}
	}

	@Override
	public Object defaults(String value) {
		if (value.equals("texture")) return ClientTweaks.MOD_ID + ":textures/entity/tail_feature.png";
		return null;
	}

	private static boolean isDev(String name){
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		String uuid = "dade7574-9d71-4704-b951-9f319a7232e2";
		if (player != null && name.equals(player.getName().getString())) return uuid.equals(player.getUuidAsString());
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world != null) return !StreamSupport.stream(world.getEntities().spliterator(), false)
			.filter(entity -> entity.isPlayer() && name.equals(entity.getName().getString()) && uuid.equals(entity.getUuidAsString()))
			.toList().isEmpty();
		return false;
	}
}
