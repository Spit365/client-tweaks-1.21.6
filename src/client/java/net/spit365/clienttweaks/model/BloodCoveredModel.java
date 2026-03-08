package net.spit365.clienttweaks.model;

import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;

public class BloodCoveredModel extends BipedEntityModel<AbstractClientPlayerEntity> {
	public BloodCoveredModel(ModelPart root) {super(root);}

	public static final Identifier TEXTURE = Identifier.of(ClientTweaks.MOD_ID, "textures/entity/blood_feature.png");

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-4f, -12f, -2f, 4f, 12f, 4f, new Dilation(0.5f)), ModelTransform.pivot(0f, 24f, 0f));
		modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 16).cuboid(0f, -12f, -2f, 4f, 12f, 4f, new Dilation(0.5f)), ModelTransform.pivot(0f, 24f, 0f));
		return TexturedModelData.of(modelData, 32, 32);
	}
}