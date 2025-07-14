package net.spit365.clienttweaks.custom.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;

public class EarsModel<T extends EntityRenderState> extends EntityModel<T> {
	public EarsModel(ModelPart root) {super(root);}

	public static final Identifier TEXTURE = Identifier.of(ClientTweaks.MOD_ID, "textures/entity/ears_feature.png");

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
        	ModelPartData ears = modelData.getRoot().addChild("ears", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ears.addChild("right", ModelPartBuilder.create().uv(0, 5).cuboid(0.0F, -4.0F, -0.4F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)).uv(0, 0).cuboid(0.0F, -4.0F, -0.5F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -7.0F, 0.5F, 0.1745F, 0.0F, -0.2618F));
		ears.addChild("left", ModelPartBuilder.create().uv(0, 5).cuboid(-3.0F, -4.0F, -0.4F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)).uv(0, 0).cuboid(-3.0F, -4.0F, -0.5F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -7.0F, 0.5F, 0.1745F, 0.0F, 0.2618F));

		return TexturedModelData.of(modelData, 16, 16);
	}
}