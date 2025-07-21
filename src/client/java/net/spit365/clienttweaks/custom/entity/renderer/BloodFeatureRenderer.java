package net.spit365.clienttweaks.custom.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
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
import net.spit365.clienttweaks.custom.entity.model.BloodCoveredModel;
import net.minecraft.util.math.RotationAxis;
import net.spit365.clienttweaks.custom.particle.BloodParticle;

public class BloodFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public BloodFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && BloodParticle.isTouchingBlood(player.getPos())) {
			VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.createArmorTranslucent(BloodCoveredModel.TEXTURE));
			int overlay = LivingEntityRenderer.getOverlay(state, 0f);
			PlayerEntityModel playerModel = getContextModel();
			ModelPart bloodModel = BloodCoveredModel.getTexturedModelData().createModel();

			matrices.push();
			applyPartTransform(matrices, playerModel.rightLeg);
			bloodModel.getChild("right").render(matrices, buffer, light, overlay);
			matrices.pop();

			matrices.push();
			applyPartTransform(matrices, playerModel.leftLeg);
			bloodModel.getChild("left").render(matrices, buffer, light, overlay);
			matrices.pop();
		}
	}
	private void applyPartTransform(MatrixStack matrices, ModelPart part) {
		matrices.translate(part.originX / 16f, part.originY / 16f, part.originZ / 16f);
		if (part.roll != 0f) matrices.multiply(RotationAxis.POSITIVE_Z.rotation(part.roll));
		if (part.yaw != 0f) matrices.multiply(RotationAxis.POSITIVE_Y.rotation(part.yaw));
		if (part.pitch != 0f) matrices.multiply(RotationAxis.POSITIVE_X.rotation(part.pitch));
		matrices.translate(-part.originX / 16f, -part.originY / 16f, -part.originZ / 16f);
	}
}
