package top.gregtao.concerto.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import top.gregtao.concerto.BukkitConcertoPlugin;
import top.gregtao.concerto.common.ConcertoCommon;
import top.gregtao.concerto.network.CommonConcertoPayload;
import top.gregtao.concerto.network.MusicDataPacket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BukkitMusicNetworkHandler implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("concerto:string")) return;

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            // Read VarInt length (we can ignore it if we just read the rest as string,
            // but standard MC string has length prefix. PacketByteBuf.writeString writes VarInt length)

            int length = readVarInt(in);
            if (length > message.length) return; // Basic check

            byte[] stringBytes = new byte[length];
            in.readFully(stringBytes);
            String data = new String(stringBytes, StandardCharsets.UTF_8);

            if (data.isEmpty()) return;

            CommonConcertoPayload.Channel packetChannel = CommonConcertoPayload.Channel.getById(data.charAt(0));
            String payloadString = data.substring(1);

            CommonConcertoPayload payload = new CommonConcertoPayload(packetChannel, payloadString);

            handlePacket(player, payload);

        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Error reading plugin message", e);
        }
    }

    private void handlePacket(Player player, CommonConcertoPayload payload) {
        switch (payload.channel) {
            case MUSIC_DATA -> handleMusicData(player, payload);
            // Implement other handlers as needed
        }
    }

    private void handleMusicData(Player player, CommonConcertoPayload payload) {
        try {
            MusicDataPacket packet = MusicDataPacket.fromPacket(payload, false);
            if (packet != null && packet.music != null) {
                // Handle music request (e.g. broadcast to all if to="@a")
                if ("@a".equals(packet.to)) {
                    broadcastMusic(player, packet);
                } else {
                    Player target = Bukkit.getPlayer(packet.to);
                    if (target != null) {
                        sendMusicTo(target, packet, player.getName());
                    }
                }
            }
        } catch (Exception e) {
            ConcertoCommon.getPlatform().getLogger().warn("Invalid music packet from " + player.getName());
        }
    }

    private void broadcastMusic(Player sender, MusicDataPacket packet) {
        // Here we construct a packet to send to all players
        // We need to set 'from' to sender's name and isS2C = true

        // MusicDataPacket from common is immutable in structure but fields are public?
        // Yes, public fields.
        packet.from = sender.getName();
        packet.isS2C = true;

        CommonConcertoPayload outPayload = packet.toPacket(); // This creates payload with current fields

        for (Player p : Bukkit.getOnlinePlayers()) {
            sendPayload(p, outPayload);
        }

        sender.sendMessage("Broadcasted music: " + packet.music.getMeta().title());
    }

    private void sendMusicTo(Player target, MusicDataPacket packet, String senderName) {
        packet.from = senderName;
        packet.isS2C = true;
        CommonConcertoPayload outPayload = packet.toPacket();
        sendPayload(target, outPayload);
    }

    public static void sendPayload(Player player, CommonConcertoPayload payload) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String data = payload.channel.id + payload.string;
        writeVarInt(out, data.getBytes(StandardCharsets.UTF_8).length);
        out.write(data.getBytes(StandardCharsets.UTF_8));
        player.sendPluginMessage(BukkitConcertoPlugin.INSTANCE, "concerto:string", out.toByteArray());
    }

    // Helper to read VarInt
    private int readVarInt(DataInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    private static void writeVarInt(ByteArrayDataOutput out, int value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            // Note: >>> is unsigned right shift
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            out.writeByte(temp);
        } while (value != 0);
    }
}
