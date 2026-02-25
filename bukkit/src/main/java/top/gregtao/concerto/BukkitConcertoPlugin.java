package top.gregtao.concerto;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.gregtao.concerto.command.BukkitConcertoCommand;
import top.gregtao.concerto.common.ConcertoCommon;
import top.gregtao.concerto.network.CommonConcertoPayload;
import top.gregtao.concerto.network.ConcertoNetworking;
import top.gregtao.concerto.network.BukkitMusicNetworkHandler;
import top.gregtao.concerto.platform.BukkitPlatform;

public class BukkitConcertoPlugin extends JavaPlugin implements Listener {

    public static BukkitConcertoPlugin INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        ConcertoCommon.init(new BukkitPlatform());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "concerto:string");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "concerto:string", new BukkitMusicNetworkHandler());

        getCommand("concerto").setExecutor(new BukkitConcertoCommand());
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Concerto Bukkit Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Concerto Bukkit Plugin has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String payloadString = ConcertoNetworking.HANDSHAKE_STRING + "CallJoin:" + event.getPlayer().getName();
        CommonConcertoPayload payload = new CommonConcertoPayload(CommonConcertoPayload.Channel.HANDSHAKE, payloadString);
        BukkitMusicNetworkHandler.sendPayload(event.getPlayer(), payload);
    }
}
