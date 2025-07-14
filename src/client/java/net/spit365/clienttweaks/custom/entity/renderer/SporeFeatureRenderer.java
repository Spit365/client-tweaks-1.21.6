package net.spit365.clienttweaks.custom.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.spit365.clienttweaks.manager.ConfigManager;
import net.minidev.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class SporeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> implements ConfigManager.DefaultedJsonReader {
     public SporeFeatureRenderer (FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
          super(context);
          sporeFeatureRenderers.add(this);
          ClientPlayerEntity player = MinecraftClient.getInstance().player;
          if (player != null) random = player.getRandom();
          else random = Random.create();
     }

     private static final List<SporeFeatureRenderer> sporeFeatureRenderers = new LinkedList<>();
     private final Random random;
     private int sporesCounter = 0;

     @Override
     public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
          ClientWorld clientWorld = MinecraftClient.getInstance().world;
          ClientPlayerEntity player = MinecraftClient.getInstance().player;
          JSONObject category = ConfigManager.read(ConfigManager.file(), "SPORES");
          if (
               category.containsKey(state.name) &&
               !state.invisible &&
               clientWorld != null &&
               player != null &&
               sporesCounter <= 0
          ) {
               JSONObject options = (JSONObject) category.get(state.name);
               for (int i = 0; i < random.nextBetweenExclusive(
                       intOption(options, "minParticle"),
                       intOption(options, "maxParticle")
               ); i++)
                    clientWorld.addParticleClient(
                         (ParticleEffect) Registries.PARTICLE_TYPE.get(Identifier.of(stringOption(options, "particle"))),
                         player.getX() + doubleOption(options, "x"),
                         player.getY() + doubleOption(options, "y"),
                         player.getZ() + doubleOption(options, "z"),
                         random.nextGaussian() * floatOption(options, "vx"),
                         random.nextGaussian() * floatOption(options, "vy"),
                         random.nextGaussian() * floatOption(options, "vz"));
               sporesCounter = random.nextBetweenExclusive(
                    intOption(options, "minCooldown"),
                    intOption(options, "maxCooldown"));
          }
     }

     public static void tick(){
          sporeFeatureRenderers.forEach(sporeFeatureRenderer -> sporeFeatureRenderer.sporesCounter--);
     }

     @Override
     public Object defaults(String value) {
          return switch (value){
               case "x", "z" -> 0d;
               case "y" -> 1d;
               case "vx", "vz" -> 1.0E-6f;
               case "vy" -> 1.0E-4f;
               case "particle" -> "minecraft:crimson_spore";
               case "minParticle" -> 2;
               case "maxParticle" -> 4;
               case "minCooldown" -> 30;
               case "maxCooldown" -> 60;
               default -> null;
          };
     }
}
