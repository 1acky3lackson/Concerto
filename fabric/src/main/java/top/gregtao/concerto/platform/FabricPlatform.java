package top.gregtao.concerto.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.gregtao.concerto.common.platform.Platform;
import top.gregtao.concerto.config.ClientConfig;

import java.nio.file.Path;

public class FabricPlatform implements Platform {
    private static final Logger LOGGER = LoggerFactory.getLogger("Concerto");

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String translate(String key, Object... args) {
        return Text.translatable(key, args).getString();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("Concerto");
    }

    @Override
    public Path getCacheDir() {
        return FabricLoader.getInstance().getGameDir().resolve("Concerto/cache");
    }

    @Override
    public int getNeteaseMusicQuality() {
        return ClientConfig.INSTANCE.options.neteaseMusicQuality.getRank();
    }
}
