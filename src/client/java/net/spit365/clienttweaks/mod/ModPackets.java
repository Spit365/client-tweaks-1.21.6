package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.spit365.clienttweaks.packet.SummonBleedS2CPacket;

public class ModPackets {
    public static void init(){
        PayloadTypeRegistry.playS2C().register(SummonBleedS2CPacket.ID, SummonBleedS2CPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SummonBleedS2CPacket.ID, (summonBleedS2CPacket, context) -> ClientMethods.summonBleed(summonBleedS2CPacket.getPos(), context.client().world));
    }
}
