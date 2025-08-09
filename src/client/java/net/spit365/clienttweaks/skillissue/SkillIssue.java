package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.manager.KeyAction;
import net.spit365.clienttweaks.mod.ClientMethods;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.util.math.MathHelper.lerp;
import static net.minecraft.util.math.MathHelper.lerpAngleDegrees;

public class SkillIssue {
     public static int targetIndex = 0;
     public static final KeyAction<Void> NEXT_TARGET_KEY = KeyAction.register(Identifier.of(ClientTweaks.MOD_ID, "next_target"), GLFW.GLFW_KEY_RIGHT, input -> targetIndex++);
     public static final KeyAction<Void> PREVIOUS_TARGET_KEY = KeyAction.register(Identifier.of(ClientTweaks.MOD_ID, "previous_target"), GLFW.GLFW_KEY_LEFT, input -> targetIndex--);
     public static final KeyAction<Void> RESET_TARGET_KEY = KeyAction.register(Identifier.of(ClientTweaks.MOD_ID, "reset_target"), GLFW.GLFW_KEY_DOWN, input -> targetIndex = 0);
     public static final Toggleable lockOn = new Toggleable("lock_on", GLFW.GLFW_KEY_Q);
     public static final Toggleable glowing = new Toggleable("glowing", GLFW.GLFW_KEY_KP_ENTER);

     public static class Toggleable {
          private final KeyAction<MinecraftClient> keyAction;
          private boolean state = false;
          public Toggleable(String id, int key) {
               this.keyAction = KeyAction.register(
                    Identifier.of(ClientTweaks.MOD_ID, "toggle_" + id), key, this::toggle);
          }
          private void toggle(MinecraftClient client) {
               state = !state;
               client.inGameHud.setOverlayMessage(
                    Text.literal(keyAction.key().getTranslationKey() + " has been toggled " + (state ? "§aon" : "§coff")), false);
          }
          public KeyAction<MinecraftClient> key() {return keyAction;}
          public boolean isOn() {return state;}
     }
     public static void tick(MinecraftClient client, ClientPlayerEntity player) {
          if (client.world != null && player != null && lockOn.isOn()) {
               LivingEntity closestEntity = ClientMethods.selectNearestEntity(player.getEyePos(), client.world, player);
               if (closestEntity != null){
                    Vec3d targetVec = closestEntity.getEyePos().subtract(player.getEyePos()).normalize();

                    float rootDistance = (float) Math.sqrt(player.getEyePos().distanceTo(closestEntity.getEyePos()));
                    float smoothing = rootDistance == 0? 1f : Math.min(1f, 1f / rootDistance);
                    player.rotate(
                         lerpAngleDegrees(smoothing, player.getYaw(), (float) Math.toDegrees(Math.atan2(-targetVec.x, targetVec.z))),
                         lerp(smoothing, player.getPitch(), (float) Math.toDegrees(-Math.asin(targetVec.y)))
                    );
               }
          }
     }
     public static void init(){
          ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
               lockOn.key().tick(minecraftClient);
               glowing.key().tick(minecraftClient);
               NEXT_TARGET_KEY.tick(null);
               PREVIOUS_TARGET_KEY.tick(null);
               RESET_TARGET_KEY.tick(null);
          });
     }
}
