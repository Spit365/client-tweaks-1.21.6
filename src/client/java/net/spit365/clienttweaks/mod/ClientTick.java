package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.spit365.clienttweaks.custom.entity.renderer.SporeFeatureRenderer;
import net.spit365.clienttweaks.skillissue.SkillIssue;

public class ClientTick {
     public static void init(){
          ClientTickEvents.START_CLIENT_TICK.register(client -> {
               ClientPlayerEntity player = client.player;
               if (client.world != null && player != null) {
                    SporeFeatureRenderer.tick();
               }
               SkillIssue.lockOn.tick(client);
          });
     }
}
