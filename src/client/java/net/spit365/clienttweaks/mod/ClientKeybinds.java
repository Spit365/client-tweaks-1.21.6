package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.spit365.clienttweaks.skillissue.SkillIssue;

public class ClientKeybinds {

     public static void init(){
          ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
               if (SkillIssue.lockOn.key().wasPressed()) SkillIssue.lockOn.onKeyPressed(minecraftClient);
               if (SkillIssue.teleport.key().wasPressed()) SkillIssue.teleport.onKeyPressed(minecraftClient);
               if (SkillIssue.range.key().wasPressed()) SkillIssue.range.onKeyPressed(minecraftClient);
          });
     }
}
