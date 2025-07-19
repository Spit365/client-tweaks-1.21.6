package net.spit365.clienttweaks.custom.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.spit365.clienttweaks.custom.entity.model.TailModel;
import net.spit365.clienttweaks.manager.ConfigManager;

@Environment(EnvType.CLIENT)
public class TailFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public TailFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		if (ConfigManager.read(ConfigManager.file(), "TAILED").containsKey(state.name) && !state.invisible) {
			matrices.push();
			matrices.translate(0.0F, state.isInSneakingPose ? 0.1F : -0.1F, 0.0F);
			ModelPart part = getContextModel().body;
			matrices.translate(part.originX / 16.0F, part.originY / 16.0F, part.originZ / 16.0F);
			if (part.roll != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Z.rotation(part.roll));
			if (part.yaw != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Y.rotation(part.yaw));
			if (part.pitch != 0.0F) matrices.multiply(RotationAxis.POSITIVE_X.rotation(part.pitch));
			matrices.translate(-part.originX / 16.0F, -part.originY / 16.0F, -part.originZ / 16.0F);
			TailModel.getTexturedModelData().createModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TailModel.TEXTURE)), light, LivingEntityRenderer.getOverlay(state, 0f));
			matrices.pop();
		}
	}
}
