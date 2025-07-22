package net.spit365.clienttweaks.custom.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class TailModel<T extends EntityRenderState> extends EntityModel<T> {
	public TailModel(ModelPart root) {super(root);}

	public static ModelPart getModel(boolean isSneaking) {
		ModelData modelData = new ModelData();
     	ModelPartData tail = modelData.getRoot().addChild("tail", ModelPartBuilder.create(), ModelTransform.of(0f, 10f, 2f, isSneaking? 1 : 0, 0, (float) Math.sin(System.currentTimeMillis() / (isSneaking? 100d : 500d)) / 3f));

		tail.addChild("t1", ModelPartBuilder.create().uv(2, 0).cuboid(-2f, -1.125f, -1f, 4f, 2.25f, 2f, new Dilation(0f)), ModelTransform.origin(0f, 0.504f, 1.25f));
		tail.addChild("t2", ModelPartBuilder.create().uv(1, 3).cuboid(-2.25f, -1.5f, -1.125f, 4.5f, 3f, 2.25f, new Dilation(0f)), ModelTransform.of(0f, 1.2345f, 1.985f, -0.7854f, 0f, 0f));
		tail.addChild("t3", ModelPartBuilder.create().uv(-1, 6).cuboid(-2.5f, -1f, -1.75f, 5f, 2f, 3.5f, new Dilation(0f)), ModelTransform.of(0f, 2.44f, 2.5926f, 0.3927f, 0f, 0f));
		tail.addChild("t4", ModelPartBuilder.create().uv(-3, 8).cuboid(-2.75f, -1.775f, -2f, 5.5f, 3.55f, 4f, new Dilation(0f)), ModelTransform.origin(0f, 4.479f, 2.675f));
		tail.addChild("t5", ModelPartBuilder.create().uv(-5, 11).cuboid(-2.95f, -0.875f, -2.25f, 5.9f, 1.75f, 4.5f, new Dilation(0f)), ModelTransform.of(0f, 6.0893f, 3.019f, 0.3927f, 0f, 0f));
		tail.addChild("t6", ModelPartBuilder.create().uv(-4, 14).cuboid(-3f, -1.25f, -2.125f, 6f, 2.5f, 4.25f, new Dilation(0f)), ModelTransform.of(0f, 7.1401f, 3.6621f, 0.7854f, 0f, 0f));
		tail.addChild("t7", ModelPartBuilder.create().uv(0, 16).cuboid(-2.75f, -2.125f, -1.25f, 5.5f, 4.25f, 2.5f, new Dilation(0f)), ModelTransform.of(0f, 8.0417f, 5.0115f, -0.3927f, 0f, 0f));
		tail.addChild("t8", ModelPartBuilder.create().uv(1, 19).cuboid(-2.5f, -2.125f, -1.25f, 5f, 4.25f, 2.5f, new Dilation(0f)), ModelTransform.origin(0f, 8.358f, 6.603f));
		tail.addChild("t9", ModelPartBuilder.create().uv(2, 22).cuboid(-2.25f, -2.125f, -0.875f, 4.5f, 4.25f, 1.75f, new Dilation(0f)), ModelTransform.of(0f, 8.1852f, 7.8478f, 0.3927f, 0f, 0f));
		tail.addChild("t10", ModelPartBuilder.create().uv(3, 24).cuboid(-2f, -1.875f, -0.625f, 4f, 3.75f, 1.25f, new Dilation(0f)), ModelTransform.of(0f, 7.6112f, 9.2337f, 0.3927f, 0f, 0f));
		tail.addChild("t11", ModelPartBuilder.create().uv(3, 27).cuboid(-1.75f, -1.625f, -0.5f, 3.5f, 3.25f, 1f, new Dilation(0f)), ModelTransform.of(0f, 7.1806f, 10.273f, 0.3927f, 0f, 0f));
     	tail.addChild("t12", ModelPartBuilder.create().uv(5, 29).cuboid(-1.25f, -1.125f, -0.5f, 2.5f, 2.25f, 1f, new Dilation(0f)), ModelTransform.of(0f, 6.798f, 11.1969f, 0.3927f, 0f, 0f));
     	tail.addChild("t13", ModelPartBuilder.create().uv(6, 30).cuboid(-0.75f, -0.625f, -0.25f, 1.5f, 1.25f, 0.5f, new Dilation(0f)), ModelTransform.of(0f, 6.5109f, 11.8898f, 0.3927f, 0f, 0f));

		return TexturedModelData.of(modelData, 16, 32).createModel();
	}
}