package net.spit365.clienttweaks.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.spit365.clienttweaks.model.BloodCoveredModel;
import net.spit365.clienttweaks.particle.BloodParticle;
import net.spit365.clienttweaks.util.ModUtil;

public class BloodFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> {
	public BloodFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && BloodParticle.isTouchingBlood(player.getPos())) {
			VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BloodCoveredModel.TEXTURE));
			int overlay = LivingEntityRenderer.getOverlay(entity, 0f);
			BipedEntityModel<AbstractClientPlayerEntity> playerModel = getContextModel();
			ModelPart bloodModel = BloodCoveredModel.getTexturedModelData().createModel();

			matrices.push();
			ModUtil.applyPartTransform(matrices, playerModel.rightLeg);
			bloodModel.getChild("right").render(matrices, buffer, light, overlay);
			matrices.pop();

			matrices.push();
			ModUtil.applyPartTransform(matrices, playerModel.leftLeg);
			bloodModel.getChild("left").render(matrices, buffer, light, overlay);
			matrices.pop();
		}
	}
}
