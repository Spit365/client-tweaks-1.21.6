package net.spit365.clienttweaks.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.spit365.clienttweaks.custom.entity.renderer.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
     public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {super(ctx, model, shadowRadius);}

     @Inject(at = @At("TAIL"), method = "<init>")
     public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
          addFeature(new TailFeatureRenderer(this));
          addFeature(new ParticleFeatureRenderer(this));
          addFeature(new EarsFeatureRenderer(this));
          addFeature(new BloodFeatureRenderer(this));
          addFeature(new ItemFeatureRenderer(this));
     }
}
