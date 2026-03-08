package net.spit365.clienttweaks.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.spit365.clienttweaks.util.ConfigManager;
import net.minidev.json.JSONObject;
import net.spit365.clienttweaks.config.CosmeticsConfig;

import java.util.LinkedList;
import java.util.List;


public class ParticleFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> implements ConfigManager.DefaultedJsonReader {
     public ParticleFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, BipedEntityModel<AbstractClientPlayerEntity>> context) {
          super(context);
          particleFeatureRenderers.add(this);
          ClientPlayerEntity player = MinecraftClient.getInstance().player;
          if (player != null) random = player.getRandom();
          else random = Random.create();
     }

     private static final List<ParticleFeatureRenderer> particleFeatureRenderers = new LinkedList<>();
     private final Random random;
     private int particleCounter = 0;

     public static void tick(){
          if (!MinecraftClient.getInstance().isPaused()) particleFeatureRenderers.forEach(particleFeatureRenderer -> particleFeatureRenderer.particleCounter--);
     }

     @Override
     public Object defaults(String value) {
          return switch (value){
               case "x", "z" -> 0d;
               case "y" -> 1d;
               case "vx", "vz", "vy" -> 0f;
			   case "particle" -> "minecraft:crimson_spore";
               case "minParticle" -> 2;
               case "maxParticle" -> 4;
               case "minCooldown" -> 30;
               case "maxCooldown" -> 60;
               default -> null;
          };
     }

     @Override
     public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
          ClientWorld clientWorld = MinecraftClient.getInstance().world;
          ClientPlayerEntity player = MinecraftClient.getInstance().player;
          JSONObject category = CosmeticsConfig.getEnabledCosmetic("particles");
          String name = entity.getName().getString();
          if (
              category.containsKey(name) &&
                  !entity.isInvisible() &&
                  clientWorld != null &&
                  player != null &&
                  particleCounter <= 0
          ) {
               JSONObject options = (JSONObject) category.get(name);
               for (int i = 0; i < random.nextBetweenExclusive(
                   intOption(options, "minParticle"),
                   intOption(options, "maxParticle")
               ); i++)
                    clientWorld.addParticle(
                        (ParticleEffect) Registries.PARTICLE_TYPE.get(Identifier.of(stringOption(options, "particle"))),
                        player.getX() + doubleOption(options, "x"),
                        player.getY() + doubleOption(options, "y"),
                        player.getZ() + doubleOption(options, "z"),
                        random.nextGaussian() * floatOption(options, "vx"),
                        random.nextGaussian() * floatOption(options, "vy"),
                        random.nextGaussian() * floatOption(options, "vz"));
               particleCounter = random.nextBetweenExclusive(
                   intOption(options, "minCooldown"),
                   intOption(options, "maxCooldown"));
          }
     }
}
