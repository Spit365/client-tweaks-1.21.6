package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Range extends SkillIssue.Toggleable{
     public Range() {super(false);}

     @Override public KeyBinding key() {return RANGE_KEY;}
     private static final KeyBinding RANGE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
             "key.client-tweaks.toggle_range",
             InputUtil.Type.KEYSYM,
             GLFW.GLFW_KEY_APOSTROPHE,
             "key.categories.client-tweaks"
     ));
}
