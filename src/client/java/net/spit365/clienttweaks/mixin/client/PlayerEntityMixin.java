package net.spit365.clienttweaks.mixin.client;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.spit365.clienttweaks.skillissue.SkillIssue.range;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
      @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
      public void getEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
           if (range.isRange) cir.setReturnValue(10d);
      }
}
