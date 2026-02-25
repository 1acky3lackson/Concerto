package top.gregtao.concerto.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricConcertoNetworking {

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ConcertoPayload.ID, ConcertoPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ConcertoPayload.ID, ConcertoPayload.CODEC);
    }
}
