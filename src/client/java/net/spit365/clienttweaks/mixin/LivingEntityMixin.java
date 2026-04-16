package net.spit365.clienttweaks.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.spit365.clienttweaks.util.ModUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "addDeathParticles", at = @At("HEAD"), cancellable = true)
	public void addDeathParticles(CallbackInfo ci){
		if (((LivingEntity) (Object) this) instanceof PlayerEntity player) {
			World world = player.getEntityWorld();
			if (world != null) {
                ModUtil.summonBleed(player.getEntityPos(), world);
                ci.cancel();
			}
		}
	}
}
