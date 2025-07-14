package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.spit365.clienttweaks.mod.ClientMethods;
import org.lwjgl.glfw.GLFW;

public class Teleport extends SkillIssue.Normal{
     @Override public KeyBinding key() {return TELEPORT_KEY;}
     private static final KeyBinding TELEPORT_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
             "key.client-tweaks.teleport",
             InputUtil.Type.KEYSYM,
             GLFW.GLFW_KEY_UNKNOWN,
             "key.categories.client-tweaks"
     ));

     @Override
     public void onKeyPressed(MinecraftClient client) {
          ClientPlayerEntity player = client.player;
          ClientWorld world = client.world;

          if (player != null && world != null) {
               LivingEntity target = ClientMethods.selectNearestEntity(player.getEyePos(), world, player);
               if (target != null) player.setPosition(target.getPos().subtract(target.getRotationVec(1).normalize().multiply(1, 0, 1)));
          }
     }
}
