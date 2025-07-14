package net.spit365.clienttweaks.mod;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.skillissue.SkillIssue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class ClientMethods {
     public static @Nullable LivingEntity selectNearestEntity(Vec3d pos, ClientWorld world, Entity... exceptions) {
          List<LivingEntity> sortedEntities = getSortedEntities(pos, world, exceptions);
          if (!sortedEntities.isEmpty()) {
               SkillIssue.targetIndex = Math.clamp(SkillIssue.targetIndex, 0, sortedEntities.size() -1);
               return sortedEntities.get(SkillIssue.targetIndex);
          } else return null;
     }

     public static @NotNull List<LivingEntity> getSortedEntities(Vec3d pos, ClientWorld world, Entity... exceptions) {
          return StreamSupport.stream(world.getEntities().spliterator(), false)
                  .filter(entity ->
                          entity instanceof LivingEntity &&
                          !Arrays.asList(exceptions).contains(entity) &&
                          entity.getPos().squaredDistanceTo(pos) <= 1000)
                  .map(entity -> (LivingEntity) entity)
                  .sorted(Comparator.comparingDouble(entity -> entity.getPos().squaredDistanceTo(pos)))
                  .toList();
     }
}
