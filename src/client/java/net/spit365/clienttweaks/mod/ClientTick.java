package net.spit365.clienttweaks.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.spit365.clienttweaks.renderer.ParticleFeatureRenderer;

@Environment(EnvType.CLIENT)
public class ClientTick {
     public static void init(){
          ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.world != null && client.player != null) ParticleFeatureRenderer.tick();
          });
     }
}
