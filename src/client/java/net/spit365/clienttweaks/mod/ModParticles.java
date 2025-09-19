package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.custom.particle.BloodParticle;

public class ModParticles {
     private static SimpleParticleType register(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render){
          SimpleParticleType particle = FabricParticleTypes.simple(alwaysShow);
          Registry.register(Registries.PARTICLE_TYPE, Identifier.of(ClientTweaks.MOD_ID, name), particle);
          ParticleFactoryRegistry.getInstance().register(particle, render);
          return particle;
     }

     public static final SimpleParticleType BLOOD = register("blood", false, BloodParticle::getBloodParticleFactory);

     public static void init(){}
}
