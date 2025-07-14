package net.spit365.clienttweaks.skillissue;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.mod.ClientMethods;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.util.math.MathHelper.lerp;
import static net.minecraft.util.math.MathHelper.lerpAngleDegrees;

public class LockOn extends SkillIssue.Toggleable{
     public LockOn() {super(false);}

     @Override public KeyBinding key() {return LOCK_ON_KEY;}
     private static final KeyBinding LOCK_ON_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
             "key.client-tweaks.toggle_lock_on",
             InputUtil.Type.KEYSYM,
             GLFW.GLFW_KEY_Q,
             "key.categories.client-tweaks"
     ));

     public void tick(MinecraftClient client) {
          ClientPlayerEntity player = client.player;
          if (client.world != null && player != null && this.activated) {
               LivingEntity closestEntity = ClientMethods.selectNearestEntity(player.getEyePos(), client.world, player);
               if (closestEntity != null){
                    Vec3d targetVec = closestEntity.getEyePos().subtract(player.getEyePos()).normalize();

                    float rootDistance = (float) Math.pow(player.getEyePos().distanceTo(closestEntity.getEyePos()), 0.5);
                    float smoothing = rootDistance == 0? 1f : 1f / rootDistance;
                    player.rotate(
                         lerpAngleDegrees(smoothing, player.getYaw(), (float) Math.toDegrees(Math.atan2(-targetVec.x, targetVec.z))),
                         lerp(smoothing, player.getPitch(), (float) Math.toDegrees(-Math.asin(targetVec.y)))
                    );
               }
          }
     }
}
