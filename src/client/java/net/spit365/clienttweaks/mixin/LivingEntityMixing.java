package net.spit365.clienttweaks.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.spit365.clienttweaks.mod.ClientMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixing {
	@Inject(method = "addDeathParticles", at = @At("HEAD"), cancellable = true)
	public void addDeathParticles(CallbackInfo ci){
		if (((LivingEntity) (Object) this) instanceof PlayerEntity player) {
			World world = player.getWorld();
			if (world != null) {
                ClientMethods.summonBleed(player.getPos(), world);
                ci.cancel();
			}
		}
	}
}
