package net.spit365.clienttweaks.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.spit365.clienttweaks.config.CosmeticsConfig;
import net.spit365.clienttweaks.model.EarsModel;
import net.spit365.clienttweaks.util.ModUtil;

@Environment(EnvType.CLIENT)
public class EarsFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	public EarsFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {super(context);}

	@Override
	public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		if (CosmeticsConfig.getEnabledCosmetic("ears").containsKey(ModUtil.getName(state)) && !state.invisible) {
			matrices.push();
			ModUtil.applyPartTransform(matrices, getContextModel().head);
			if (state.isInSneakingPose) matrices.translate(0f, 0.25f, 0f);
			queue.submitModelPart(
				EarsModel.MODEL,
				matrices,
				RenderLayer.getArmorCutoutNoCull(EarsModel.TEXTURE),
				light,
				LivingEntityRenderer.getOverlay(state, 0f),
				null
			);
			matrices.pop();
		}
	}
}
