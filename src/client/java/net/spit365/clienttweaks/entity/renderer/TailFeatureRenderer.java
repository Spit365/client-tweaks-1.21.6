package net.spit365.clienttweaks.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.spit365.clienttweaks.entity.model.TailModel;
import net.spit365.clienttweaks.manager.ConfigManager;

@Environment(EnvType.CLIENT)
public class TailFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public TailFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		if (ConfigManager.read(ConfigManager.file(), "TAILED").containsKey(state.name) && !state.invisible) {
			if (state.positionOffset != null) matrices.translate(state.positionOffset.getX(), state.positionOffset.getY(), state.positionOffset.getZ());
			TailModel.getTexturedModelData().createModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TailModel.TEXTURE)), light, LivingEntityRenderer.getOverlay(state, 0f));
		}
	}

}
