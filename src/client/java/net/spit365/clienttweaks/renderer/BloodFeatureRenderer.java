package net.spit365.clienttweaks.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.spit365.clienttweaks.model.BloodCoveredModel;
import net.spit365.clienttweaks.particle.BloodParticle;
import net.spit365.clienttweaks.util.ModUtil;

public class BloodFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public BloodFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && BloodParticle.isTouchingBlood(player.getPos())) {
			VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BloodCoveredModel.TEXTURE));
			int overlay = LivingEntityRenderer.getOverlay(state, 0f);
			PlayerEntityModel playerModel = getContextModel();

			matrices.push();
			ModUtil.applyPartTransform(matrices, playerModel.rightLeg);
			BloodCoveredModel.MODEL.getChild("right").render(matrices, buffer, light, overlay);
			matrices.pop();

			matrices.push();
			ModUtil.applyPartTransform(matrices, playerModel.leftLeg);
			BloodCoveredModel.MODEL.getChild("left").render(matrices, buffer, light, overlay);
			matrices.pop();
		}
	}
}
