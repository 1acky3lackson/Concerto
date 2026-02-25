package top.gregtao.concerto.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ConcertoPayload implements CustomPayload {

    public static final Id<ConcertoPayload> ID = new Id<>(Identifier.of("concerto", "string"));
    public String string;
    public CommonConcertoPayload.Channel channel;

    public ConcertoPayload(CommonConcertoPayload.Channel channel, String s) {
        this.channel = channel;
        this.string = s;
    }

    public ConcertoPayload(CommonConcertoPayload payload) {
        this(payload.channel, payload.string);
    }

    public CommonConcertoPayload toCommon() {
        return new CommonConcertoPayload(this.channel, this.string);
    }

    public static final PacketCodec<PacketByteBuf, ConcertoPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, ConcertoPayload value) {
            buf.writeString(value.channel.id + value.string, Integer.MAX_VALUE);
        }

        @Override
        public ConcertoPayload decode(PacketByteBuf buf) {
            String s = buf.readString(Integer.MAX_VALUE);
            CommonConcertoPayload.Channel channel1 = CommonConcertoPayload.Channel.getById(s.charAt(0));
            return new ConcertoPayload(channel1, s.substring(1));
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
