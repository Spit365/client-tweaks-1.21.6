package net.spit365.lulasmod.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.spit365.lulasmod.manager.ConfigManager;

import java.util.LinkedList;
import java.util.List;


public class SporeFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
     public SporeFeatureRenderer (FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
          super(context);
          sporeFeatureRenderers.add(this);
     }

     private static final List<SporeFeatureRenderer> sporeFeatureRenderers = new LinkedList<>();
     private int sporesCounter = 0;

     @Override
     public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
          ClientWorld clientWorld = MinecraftClient.getInstance().world;
          ClientPlayerEntity player = MinecraftClient.getInstance().player;
          if (
               ConfigManager.read(ConfigManager.ConfigFile.SPORES).toList().contains(state.name) &&
               !state.invisible &&
               clientWorld != null &&
               player != null &&
               sporesCounter <= 0
          ) {
               Random random = player.getRandom();
               for (int i = 0; i < random.nextBetweenExclusive(2, 4); i++)
                    clientWorld.addParticleClient(
                         ParticleTypes.SPORE_BLOSSOM_AIR,
                         player.getX(),
                         player.getY() + 1,
                         player.getZ(),
                         random.nextGaussian() * 1.0E-6F,
                         random.nextGaussian() * 1.0E-4F,
                         random.nextGaussian() * 1.0E-6F);
               sporesCounter = random.nextBetweenExclusive(30, 60);
          }
     }

     public static void decreaseSporesCounter(){
          sporeFeatureRenderers.forEach(sporeFeatureRenderer -> sporeFeatureRenderer.sporesCounter--);
     }
}
