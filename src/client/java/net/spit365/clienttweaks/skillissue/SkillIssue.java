package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class SkillIssue {
     public static int targetIndex = 0;

     public static KeyBinding NEXT_TARGET_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                  "key.client-tweaks.next_target",
                  InputUtil.Type.KEYSYM,
                  GLFW.GLFW_KEY_RIGHT,
                  "key.categories.client-tweaks"
     ));
     public static KeyBinding PREVIOUS_TARGET_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                  "key.client-tweaks.previous_target",
                  InputUtil.Type.KEYSYM,
                  GLFW.GLFW_KEY_LEFT,
                  "key.categories.client-tweaks"
     ));
     public static KeyBinding RESET_TARGET_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                  "key.client-tweaks.reset_target",
                  InputUtil.Type.KEYSYM,
                  GLFW.GLFW_KEY_DOWN,
                  "key.categories.client-tweaks"
     ));


     public abstract static class Normal {
          public Normal(){normals.add(this);}

          private static final List<Normal> normals = new LinkedList<>();
          abstract KeyBinding key();
          abstract void onKeyPressed(MinecraftClient client);
     }
     public abstract static class Toggleable {
          public Toggleable(Boolean activated){
               toggleables.add(this);
               this.activated = activated;
          }

          private static final List<Toggleable> toggleables = new LinkedList<>();
          abstract KeyBinding key();
          public boolean activated;
     }

     public static LockOn lockOn = new LockOn();
     public static Teleport teleport = new Teleport();
     public static Range range = new Range();
     public static Glowing glowing = new Glowing();

     public static void keybindLogic(MinecraftClient client){
          Normal.normals.forEach(normal -> {if (normal.key().wasPressed()) normal.onKeyPressed(client);});
          Toggleable.toggleables.forEach(toggleable -> {if (toggleable.key().wasPressed()) {
               toggleable.activated = !toggleable.activated;
               client.inGameHud.setOverlayMessage(Text.literal(toggleable.getClass().getSimpleName() + " has been toggled " + (toggleable.activated? "§aon" : "§coff")), false);
          }});
     }

     public static void init(){
          Normal.normals.forEach(Normal::key);
          Toggleable.toggleables.forEach(Toggleable::key);
     }
}
