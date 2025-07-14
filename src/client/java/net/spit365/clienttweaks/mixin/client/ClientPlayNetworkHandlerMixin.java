package net.spit365.clienttweaks.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.spit365.clienttweaks.mod.ModParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
     @Shadow private ClientWorld world;

     @Inject(method = "onDeathMessage", at = @At("TAIL"))
     public void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci){
          if (world != null && world.getEntityById(packet.playerId()) instanceof PlayerEntity player) for (int i = 0; i < world.random.nextInt(3) + 3 ; i++)
			world.addParticleClient(ModParticles.BLOOD, player.getX(), player.getY() +1, player.getZ(), 1, 0, 1);
     }
}
