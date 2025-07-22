package net.spit365.clienttweaks.custom.entity.renderer;

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
import net.spit365.clienttweaks.custom.entity.model.EarsModel;
import net.spit365.clienttweaks.manager.ConfigManager;
import net.spit365.clienttweaks.mod.ClientMethods;

@Environment(EnvType.CLIENT)
public class EarsFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public EarsFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		if (ConfigManager.read(ConfigManager.file(), "EARS").containsKey(state.name) && !state.invisible) {
          	matrices.push();
			ClientMethods.applyPartTransform(matrices, getContextModel().head);
			if (state.isInSneakingPose) matrices.translate(0f, 0.25f, 0f);
			EarsModel.getTexturedModelData().createModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(EarsModel.TEXTURE)), light, LivingEntityRenderer.getOverlay(state, 0f));
			matrices.pop();
		}
	}
}
