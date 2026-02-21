package net.spit365.clienttweaks.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.spit365.clienttweaks.packet.BoxStateS2CPacket;
import net.spit365.clienttweaks.renderer.BoxOutlineRenderer;

public class ClientPackets {
    public static void init(){
        if (ClientNetworkingImpl.PLAY.getHandlers().containsKey(BoxStateS2CPacket.ID.id()))
            ClientPlayNetworking.registerGlobalReceiver(BoxStateS2CPacket.ID, (boxRenderS2CPacket, context) -> BoxOutlineRenderer.setState(boxRenderS2CPacket.boxContexts()));
    }
}
