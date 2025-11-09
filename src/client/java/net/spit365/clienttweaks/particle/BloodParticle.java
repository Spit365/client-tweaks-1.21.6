package net.spit365.clienttweaks.particle;

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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BloodParticle extends SpriteBillboardParticle {
     private static final LinkedList<BloodParticle> bloodParticles = new LinkedList<>();

     protected BloodParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
          super(world, x, y, z, 0, 0, 0);
          this.setSprite(spriteProvider.getSprite(world.random));
          this.velocityMultiplier = Math.min(world.random.nextFloat() * bloodParticles.size() / 2, 2) + 2f;
          this.gravityStrength = 10f;
          this.maxAge = 36000;
          this.scale = 1f;
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
          this.setPos(roundPixels(this.x), roundPixels(this.y), roundPixels(this.z));
     }

     private double roundPixels(double i) {return ((int) (i * 16)) / 16d;}

     @Override
     public void render(VertexConsumer vertexConsumer, Camera camera, float tickProgress) {
		float halfSize = this.getSize(tickProgress) / 2.0f;

          float minU = this.getMinU();
          float maxU = this.getMaxU();
          float minV = this.getMinV();
          float maxV = this.getMaxV();

          Vec3d camPos = camera.getPos();

		float px = (float)(MathHelper.lerp(tickProgress, lastX, x) - camPos.getX());
          float py = (float)(MathHelper.lerp(tickProgress, lastY, y) - camPos.getY() + 0.001 * Math.toIntExact(bloodParticles.stream().filter(bloodParticle -> Math.abs(bloodParticle.age - this.age) <= 1).count() +1));
          float pz = (float)(MathHelper.lerp(tickProgress, lastZ, z) - camPos.getZ());

          int light = getBrightness(tickProgress);

          vertexConsumer.vertex(px - halfSize, py, pz - halfSize).texture(minU, minV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px - halfSize, py, pz + halfSize).texture(minU, maxV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px + halfSize, py, pz + halfSize).texture(maxU, maxV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
          vertexConsumer.vertex(px + halfSize, py, pz - halfSize).texture(maxU, minV).color(red, green, blue, alpha).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0);
     }

	public static @NotNull ParticleFactory<SimpleParticleType> getBloodParticleFactory(SpriteProvider spriteProvider) {
		return (SimpleParticleType type, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) -> new BloodParticle(world, x, y, z, spriteProvider);
	}

     public static boolean isTouchingBlood(Vec3d pos){
          return bloodParticles.stream().anyMatch(bloodParticle -> pos.squaredDistanceTo(bloodParticle.x, bloodParticle.y, bloodParticle.z) < 0.25);
     }
}
