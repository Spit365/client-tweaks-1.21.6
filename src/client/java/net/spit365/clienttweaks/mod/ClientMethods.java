package net.spit365.clienttweaks.mod;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClientMethods {
     public static void applyPartTransform(MatrixStack matrices, ModelPart part) {
          matrices.translate(part.originX / 16f, part.originY / 16f, part.originZ / 16f);
          if (part.roll != 0f) matrices.multiply(RotationAxis.POSITIVE_Z.rotation(part.roll));
          if (part.yaw != 0f) matrices.multiply(RotationAxis.POSITIVE_Y.rotation(part.yaw));
          if (part.pitch != 0f) matrices.multiply(RotationAxis.POSITIVE_X.rotation(part.pitch));
          matrices.translate(-part.originX / 16f, -part.originY / 16f, -part.originZ / 16f);
     }

    public static void summonBleed(Vec3d entity, World world) {
        if (world instanceof ClientWorld)
            for (int i = 0; i < world.random.nextInt(4) + 6; i++)
                world.addParticleClient(ModParticles.BLOOD, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 1);
    }
}