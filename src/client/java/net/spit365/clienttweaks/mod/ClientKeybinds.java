package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.spit365.clienttweaks.skillissue.SkillIssue;

public class ClientKeybinds {

     public static void init(){
          ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
               SkillIssue.keybindLogic(minecraftClient);
               if (SkillIssue.NEXT_TARGET_KEY.wasPressed()) SkillIssue.targetIndex++;
               if (SkillIssue.PREVIOUS_TARGET_KEY.wasPressed()) SkillIssue.targetIndex--;
               if (SkillIssue.RESET_TARGET_KEY.wasPressed()) SkillIssue.targetIndex = 0;
          });
     }
}
