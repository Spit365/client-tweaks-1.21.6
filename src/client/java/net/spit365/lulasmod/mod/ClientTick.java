package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.spit365.lulasmod.entity.renderer.SporeFeatureRenderer;

public class ClientTick {
     public static void init(){
          ClientTickEvents.START_CLIENT_TICK.register(minecraftClient -> {
               if (minecraftClient.world != null && minecraftClient.player != null) SporeFeatureRenderer.decreaseSporesCounter();
          });
     }
}
