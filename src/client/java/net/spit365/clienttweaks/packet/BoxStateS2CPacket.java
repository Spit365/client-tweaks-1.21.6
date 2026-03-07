package net.spit365.clienttweaks.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.spit365.clienttweaks.ClientTweaks;
import net.spit365.clienttweaks.util.BoxContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//register this packet for a server-controlled state
public record BoxStateS2CPacket(Set<BoxContext> boxContexts) implements CustomPayload {

    public static final Id<BoxStateS2CPacket> ID = new Id<>(Identifier.of(ClientTweaks.MOD_ID, "box_state"));

    public static final Codec<Box> BOX_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.DOUBLE.fieldOf("minX").forGetter(b -> b.minX),
        Codec.DOUBLE.fieldOf("minY").forGetter(b -> b.minY),
        Codec.DOUBLE.fieldOf("minZ").forGetter(b -> b.minZ),
        Codec.DOUBLE.fieldOf("maxX").forGetter(b -> b.maxX),
        Codec.DOUBLE.fieldOf("maxY").forGetter(b -> b.maxY),
        Codec.DOUBLE.fieldOf("maxZ").forGetter(b -> b.maxZ)
    ).apply(inst, Box::new));

    public static final Codec<BoxContext> BOX_CONTEXT_CODEC =
        RecordCodecBuilder.create(inst -> inst.group(
            BOX_CODEC.fieldOf("box").forGetter(BoxContext::box),
            Codec.INT.fieldOf("color").forGetter(BoxContext::color)
        ).apply(inst, BoxContext::new));

    public static final PacketCodec<ByteBuf, BoxStateS2CPacket> CODEC =
        PacketCodecs.codec(BOX_CONTEXT_CODEC.listOf()).xmap(boxContexts -> new BoxStateS2CPacket(new HashSet<>(boxContexts)), boxStateS2CPacket -> new ArrayList<>(boxStateS2CPacket.boxContexts));

    @Override
    public Id<BoxStateS2CPacket> getId() {
        return ID;
    }
}
