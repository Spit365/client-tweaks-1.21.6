package net.spit365.clienttweaks.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.clienttweaks.mod.ModParticles;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ModUtil {
    public static void applyPartTransform(MatrixStack matrices, ModelPart part) {
        matrices.translate(part.pivotX / 16.0F, part.pivotY / 16.0F, part.pivotZ / 16.0F);
        if (part.roll != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Z.rotation(part.roll));
        if (part.yaw != 0.0F) matrices.multiply(RotationAxis.POSITIVE_Y.rotation(part.yaw));
        if (part.pitch != 0.0F) matrices.multiply(RotationAxis.POSITIVE_X.rotation(part.pitch));
        matrices.translate(-part.pivotX / 16.0F, -part.pivotY / 16.0F, -part.pivotZ / 16.0F);
    }

    public static void summonBleed(Vec3d entity, World world) {
        if (world instanceof ClientWorld)
            for (int i = 0; i < world.random.nextInt(4) + 6; i++)
                world.addParticle(ModParticles.BLOOD, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 1);
    }

    public static <T> HashSet<T> makeMutable(Set<T> list) {
        return list == null ? new HashSet<>() : new HashSet<>(list);
    }
}