package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.packet.BoxStateS2CPacket;
import net.spit365.clienttweaks.renderer.BoxOutlineRenderer;

public class ClientPackets {
    public static void init(){
        try {
            ClientPlayNetworking.registerGlobalReceiver(BoxStateS2CPacket.ID, (boxRenderS2CPacket, context) -> BoxOutlineRenderer.setState(boxRenderS2CPacket.boxContexts()));
            ClientTweaks.LOGGER.info("Server side box outline interface found!");
        } catch (Exception ignored) {}
    }
}