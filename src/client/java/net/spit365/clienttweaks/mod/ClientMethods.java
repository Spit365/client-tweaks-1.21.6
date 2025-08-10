package net.spit365.clienttweaks.mod;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class ClientMethods {
     public static void applyPartTransform(MatrixStack matrices, ModelPart part) {
          matrices.translate(part.originX / 16f, part.originY / 16f, part.originZ / 16f);
          if (part.roll != 0f) matrices.multiply(RotationAxis.POSITIVE_Z.rotation(part.roll));
          if (part.yaw != 0f) matrices.multiply(RotationAxis.POSITIVE_Y.rotation(part.yaw));
          if (part.pitch != 0f) matrices.multiply(RotationAxis.POSITIVE_X.rotation(part.pitch));
          matrices.translate(-part.originX / 16f, -part.originY / 16f, -part.originZ / 16f);
     }
}
