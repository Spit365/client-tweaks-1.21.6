package net.spit365.clienttweaks.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class TailModel<T extends EntityRenderState> extends EntityModel<T> {
	public TailModel(ModelPart root) {super(root);}

	public static ModelPart getModel(boolean isSneaking) {
		ModelData modelData = new ModelData();
		ModelPartData origin = modelData.getRoot().addChild("origin", ModelPartBuilder.create(), ModelTransform.of(0f, 10f, 2f, isSneaking? 1 : 0, 0, (float) Math.sin(System.currentTimeMillis() / (isSneaking? 100d : 500d)) / 3f));

		ModelPartData tail = origin.addChild("tail", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		tail.addChild("t1", ModelPartBuilder.create().uv(2, 0).cuboid(-2.0F, -0.621F, 0.15F, 4.0F, 2.25F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		tail.addChild("t2", ModelPartBuilder.create().uv(1, 3).cuboid(-2.25F, -1.5F, -1.125F, 4.5F, 3.0F, 2.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.2345F, 1.985F, -0.7854F, 0.0F, 0.0F));
		tail.addChild("t3", ModelPartBuilder.create().uv(-1, 6).cuboid(-2.5F, -1.0F, -1.75F, 5.0F, 2.0F, 3.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.44F, 2.5926F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t4", ModelPartBuilder.create().uv(-3, 8).cuboid(-2.75F, 2.704F, 0.675F, 5.5F, 3.55F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		tail.addChild("t5", ModelPartBuilder.create().uv(-5, 11).cuboid(-2.95F, -0.875F, -2.25F, 5.9F, 1.75F, 4.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.0893F, 3.019F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t6", ModelPartBuilder.create().uv(-4, 14).cuboid(-3.0F, -1.25F, -2.125F, 6.0F, 2.5F, 4.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.1401F, 3.6621F, 0.7854F, 0.0F, 0.0F));
		tail.addChild("t7", ModelPartBuilder.create().uv(0, 16).cuboid(-2.75F, -2.125F, -1.25F, 5.5F, 4.25F, 2.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.0417F, 5.0115F, -0.3927F, 0.0F, 0.0F));
		tail.addChild("t8", ModelPartBuilder.create().uv(1, 19).cuboid(-2.5F, 6.233F, 5.353F, 5.0F, 4.25F, 2.5F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		tail.addChild("t9", ModelPartBuilder.create().uv(2, 22).cuboid(-2.25F, -2.125F, -0.875F, 4.5F, 4.25F, 1.75F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.1852F, 7.8478F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t10", ModelPartBuilder.create().uv(3, 24).cuboid(-2.0F, -1.875F, -0.625F, 4.0F, 3.75F, 1.25F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.6112F, 9.2337F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t11", ModelPartBuilder.create().uv(3, 27).cuboid(-1.75F, -1.625F, -0.5F, 3.5F, 3.25F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.1806F, 10.273F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t12", ModelPartBuilder.create().uv(5, 29).cuboid(-1.25F, -1.125F, -0.5F, 2.5F, 2.25F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.798F, 11.1969F, 0.3927F, 0.0F, 0.0F));
		tail.addChild("t13", ModelPartBuilder.create().uv(6, 30).cuboid(-0.75F, -0.625F, -0.25F, 1.0F, 1.25F, 0.5F, new Dilation(0.0F)), ModelTransform.of(0.25F, 6.5109F, 11.8898F, 0.3927F, 0.0F, 0.0F));

		ModelPartData fins = origin.addChild("fins", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 14.0F, -2.0F));
		fins.addChild("f1", ModelPartBuilder.create().uv(6, 14).cuboid(-1.0F, -2.5F, -1.5F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.75F, 4.75F, -0.3927F, 0.0F, 0.0F));
		fins.addChild("f2", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.25F, 16.75F, 0.7854F, 0.0F, 0.0F));
		fins.addChild("f3", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -9.75F, 17.75F, -0.48F, 0.0F, 0.0F));
		fins.addChild("f4", ModelPartBuilder.create().uv(12, 0).cuboid(0.5F, 1.25F, 8.25F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -6.5F, 8.75F, 0.3927F, 0.0F, 0.0F));
		fins.addChild("f5", ModelPartBuilder.create().uv(0, 23).cuboid(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.5F, 16.5F, -0.7854F, 0.0F, 0.0F));
		fins.addChild("f6", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.5F, 15.25F, -0.7854F, 0.0F, 0.0F));
		fins.addChild("f7", ModelPartBuilder.create().uv(0, 9).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.75F, 15.25F, -0.7854F, 0.0F, 0.0F));
		fins.addChild("f8", ModelPartBuilder.create().uv(0, 25).cuboid(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.25F, 15.0F, -0.7854F, 0.0F, 0.0F));
		fins.addChild("f9", ModelPartBuilder.create().uv(0, 29).cuboid(-0.5F, -1.5F, 2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.5F, 14.75F, -0.7854F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 16, 32).createModel();
	}
}