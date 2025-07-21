package net.spit365.clienttweaks.custom.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;

public class BloodCoveredModel<T extends EntityRenderState> extends EntityModel<T> {
	public BloodCoveredModel(ModelPart root) {super(root);}

	public static final Identifier TEXTURE = Identifier.of(ClientTweaks.MOD_ID, "textures/entity/blood_feature.png");

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-4f, -12f, -2f, 4f, 12f, 4f, new Dilation(0.5f)), ModelTransform.origin(0f, 24f, 0f));
		modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 16).cuboid(0f, -12f, -2f, 4f, 12f, 4f, new Dilation(0.5f)), ModelTransform.origin(0f, 24f, 0f));
		return TexturedModelData.of(modelData, 32, 32);
	}
}