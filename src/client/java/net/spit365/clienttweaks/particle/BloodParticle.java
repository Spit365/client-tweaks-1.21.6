package net.spit365.clienttweaks.particle;

import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.*;

public class BloodParticle extends BillboardParticle {
	private static final LinkedList<BloodParticle> bloodParticles = new LinkedList<>();
	public static final float ANGLE = (float) sin(Math.toRadians(-90));

	protected BloodParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
          super(world, x, y, z, 0, 0, 0, spriteProvider.getSprite(world.random));
          this.velocityMultiplier = 2f - 1f / bloodParticles.size();
          this.gravityStrength = 5f;
          this.maxAge = 36000;
          this.scale = 1f;
          bloodParticles.add(this);
     }

     @Override
     public void tick() {
          super.tick();
          if (!this.isAlive()) bloodParticles.remove(this);
          this.setPos(roundPixels(this.x), roundPixels(this.y), roundPixels(this.z));
     }

     private double roundPixels(double i) {return ((int) (i * 16)) / 16d;}

	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress) {
		float halfSize = this.getSize(tickProgress) / 2.0f;

		float minU = this.getMinU();
		float maxU = this.getMaxU();
		float minV = this.getMinV();
		float maxV = this.getMaxV();

		Vec3d camPos = camera.getPos();

        Predicate<BloodParticle> nearBlood = isNearBlood(new Vec3d(this.x, this.y, this.z));
		List<BloodParticle> list = bloodParticles.stream().filter(nearBlood).toList();
		float px = (float) (MathHelper.lerp(tickProgress, lastX, x) - camPos.getX());
		float py = (float) (MathHelper.lerp(tickProgress, lastY, y) - camPos.getY() + 0.001 * (list.indexOf(this) + 1));
		float pz = (float) (MathHelper.lerp(tickProgress, lastZ, z) - camPos.getZ());

		int light = getBrightness(tickProgress);

		int color = ((int)(this.alpha * 255) << 24) |
			((int)(this.red * 255) << 16) |
			((int)(this.green * 255) << 8) |
			((int)(this.blue * 255));

		submittable.render(
			this.getRenderType(),
			px, py, pz,
			1, 0, 0, -1,
			halfSize * 2,
			minU, maxU,
			minV, maxV,
			color,
			light
		);
	}

	public static @NotNull ParticleFactory<SimpleParticleType> getBloodParticleFactory(SpriteProvider spriteProvider) {
		return (parameters, world, x, y, z, velocityX, velocityY, velocityZ, random) -> new BloodParticle(world, x, y, z, spriteProvider);
	}

     public static boolean isTouchingBlood(Vec3d pos){
          return bloodParticles.stream().anyMatch(isNearBlood(pos));
     }

	 private static Predicate<BloodParticle> isNearBlood(Vec3d pos){
		 return bloodParticle -> pos.squaredDistanceTo(bloodParticle.x, bloodParticle.y, bloodParticle.z)  < 0.25;
	 }

	@Override
	protected RenderType getRenderType() {
		 return RenderType.PARTICLE_ATLAS_OPAQUE;
	}
}
