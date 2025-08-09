package net.spit365.clienttweaks.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.spit365.clienttweaks.mod.ClientMethods;
import net.spit365.clienttweaks.skillissue.SkillIssue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
     @Shadow @Nullable public ClientPlayerEntity player;
     @Shadow @Nullable public ClientWorld world;

     @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
     public void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir){
          if (SkillIssue.glowing.isOn()) cir.setReturnValue(true);
          else if (SkillIssue.glowing.isOn() && player != null && world != null)
               cir.setReturnValue(entity.isGlowing() || entity.equals(ClientMethods.selectNearestEntity(player.getEyePos(), world, player)));
     }
}
