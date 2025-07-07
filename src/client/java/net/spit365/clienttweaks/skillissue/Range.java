package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Range extends SkillIssue{
     public boolean isRange = false;

     private static KeyBinding RANGE_KEY;
     @Override public KeyBinding key() {return RANGE_KEY;}
     @Override public void register() {
          RANGE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                  "key.client-tweaks.toggle_range",
                  InputUtil.Type.KEYSYM,
                  GLFW.GLFW_KEY_APOSTROPHE,
                  "key.categories.client-tweaks"
          ));
     }
     @Override
     public void onKeyPressed(MinecraftClient client) {
          isRange = !isRange;
     }
}
