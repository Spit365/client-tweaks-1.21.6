package net.spit365.clienttweaks.skillissue;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class SkillIssue {
     public SkillIssue(){skillIssues.add(this);}

     private static final List<SkillIssue> skillIssues = new LinkedList<>();

     protected @Nullable LivingEntity selectNearestEntity(Vec3d pos, ClientWorld world, Entity... exceptions) {
          LivingEntity closestEntity = null;
          for (Entity entity : world.getEntities()) {
               if (
                       !Arrays.asList(exceptions).contains(entity) &&
                               entity.getPos().squaredDistanceTo(pos) < 1000 &&
                               entity instanceof LivingEntity livingEntity && (
                               closestEntity == null ||
                                       closestEntity.getPos().squaredDistanceTo(pos) > entity.getPos().squaredDistanceTo(pos))
               ) closestEntity = livingEntity;
          }
          return closestEntity;
     }
     public abstract KeyBinding key();
     public abstract void onKeyPressed(MinecraftClient client);
     public abstract void register();

     public static LockOn lockOn = new LockOn();
     public static Teleport teleport = new Teleport();
     public static Range range = new Range();

     public static void init(){
          skillIssues.forEach(SkillIssue::register);
     }
}
