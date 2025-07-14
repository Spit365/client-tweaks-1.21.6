package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Glowing extends SkillIssue.Toggleable{
     public Glowing() {super(false);}

     @Override public KeyBinding key() {return GLOWING_KEY;}
     private static final KeyBinding GLOWING_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
             "key.client-tweaks.toggle_glowing",
             InputUtil.Type.KEYSYM,
             GLFW.GLFW_KEY_KP_ENTER,
             "key.categories.client-tweaks"
     ));
}
