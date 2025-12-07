package net.spit365.clienttweaks.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record SummonBleedS2CPacket(double x, double y, double z) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<SummonBleedS2CPacket> ID = new Id<>(Identifier.of("client-tweaks", "summon_blood"));
    public static final PacketCodec<ByteBuf, SummonBleedS2CPacket> CODEC = PacketCodecs.codec(Vec3d.CODEC).xmap(vec3d -> new SummonBleedS2CPacket(vec3d.x, vec3d.y, vec3d.z), summonBleedS2CPacket -> new Vec3d(summonBleedS2CPacket.x, summonBleedS2CPacket.y, summonBleedS2CPacket.z));

    public Vec3d getPos(){
        return new Vec3d(x, y, z);
    }
}