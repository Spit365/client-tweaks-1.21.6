package net.spit365.clienttweaks.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.spit365.clienttweaks.model.EarsModel;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.util.ModUtil;

@Environment(EnvType.CLIENT)
public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> {
	public EarsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (CosmeticsConfig.getEnabledCosmetic("ears").containsKey(entity.getName().getString()) && !entity.isInvisible()) {
			matrices.push();
			ModUtil.applyPartTransform(matrices, getContextModel().head);
			if (entity.isSneaking()) matrices.translate(0f, 0.25f, 0f);
			EarsModel.getTexturedModelData().createModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(EarsModel.TEXTURE)), light, LivingEntityRenderer.getOverlay(entity, 0f));
			matrices.pop();
		}
	}
}
