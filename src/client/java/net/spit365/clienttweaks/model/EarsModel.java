package net.spit365.clienttweaks.model;

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
        	ModelPartData ears = modelData.getRoot().addChild("ears", ModelPartBuilder.create(), ModelTransform.origin(0f, 0f, 0f));

		ears.addChild("right", ModelPartBuilder.create().uv(0, 5).cuboid(0f, -4f, -0.4f, 3f, 4f, 0f, new Dilation(0f)).uv(0, 0).cuboid(0f, -4f, -0.5F, 3f, 4f, 0f, new Dilation(0f)), ModelTransform.of(-4f, -7f, 0.5f, 0.1745f, 0f, -0.2618f));
		ears.addChild("left", ModelPartBuilder.create().uv(0, 5).cuboid(-3f, -4f, -0.4f, 3f, 4f, 0f, new Dilation(0f)).uv(0, 0).cuboid(-3f, -4f, -0.5F, 3f, 4f, 0f, new Dilation(0f)), ModelTransform.of(4f, -7f, 0.5f, 0.1745f, 0f, 0.2618f));

		return TexturedModelData.of(modelData, 16, 16);
	}
}