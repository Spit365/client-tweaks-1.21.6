package net.spit365.clienttweaks.custom.particle;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;

public class BloodParticle extends SpriteBillboardParticle {
     private static LinkedList<BloodParticle> bloodParticles = new LinkedList<>();

     protected BloodParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
          super(world, x, y, z, 0, 0, 0);
          this.setSprite(spriteProvider.getSprite(world.random));
          this.velocityMultiplier = world.random.nextFloat() * 3f;
          this.gravityStrength = 10.0F;
          this.maxAge = 200;
               //36000;
          this.scale = 1.2F;
          bloodParticles.add(this);
     }

     @Override
     public ParticleTextureSheet getType() {
          return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
     }

     @Override
     public void tick() {
          super.tick();
          if (!this.isAlive()) bloodParticles.remove(this);
     }

     @Override
     public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
		float halfSize = this.getSize(tickProgress) / 2.0f;

          float minU = this.getMinU();
          float maxU = this.getMaxU();
          float minV = this.getMinV();
          float maxV = this.getMaxV();

          Vec3d camPos = camera.getPos();

          double priority = 0.001 * (bloodParticles.indexOf(this) +1);

          float px = (float)(MathHelper.lerp(tickProgress, lastX, x) - camPos.getX());
          float py = (float)(MathHelper.lerp(tickProgress, lastY, y) - camPos.getY() + priority);
          float pz = (float)(MathHelper.lerp(tickProgress, lastZ, z) - camPos.getZ());

          int light = getBrightness(tickProgress);

          vertexConsumer.vertex(px - halfSize, py, pz - halfSize).texture(minU, minV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px - halfSize, py, pz + halfSize).texture(minU, maxV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px + halfSize, py, pz + halfSize).texture(maxU, maxV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px + halfSize, py, pz - halfSize).texture(maxU, minV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
     }


     public static class BloodParticleFactory implements ParticleFactory<SimpleParticleType> {
          private final SpriteProvider spriteProvider;

          public BloodParticleFactory(SpriteProvider spriteProvider) {
               this.spriteProvider = spriteProvider;
          }

          @Override
          public BloodParticle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
               return new BloodParticle(world, x, y, z, spriteProvider);
          }
     }
}
