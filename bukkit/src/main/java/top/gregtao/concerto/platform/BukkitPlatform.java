package top.gregtao.concerto.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.gregtao.concerto.BukkitConcertoPlugin;
import top.gregtao.concerto.api.CacheableMusic;
import top.gregtao.concerto.common.platform.Platform;
import top.gregtao.concerto.music.Music;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class BukkitPlatform implements Platform {
    private static final Logger LOGGER = LoggerFactory.getLogger("Concerto");
    private final ResourceBundle bundle;

    public BukkitPlatform() {
        // Simple localization for Bukkit, falling back to en_us or built-in strings
        // Ideally we should load lang files. For now let's assume English or implement basic loading.
        // Or just return key if not found.
        this.bundle = ResourceBundle.getBundle("assets/concerto/lang/en_us", Locale.US, this.getClass().getClassLoader());
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String translate(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return key; // Fallback
        }
    }

    @Override
    public java.nio.file.Path getConfigDir() {
        return BukkitConcertoPlugin.INSTANCE.getDataFolder().toPath();
    }

    @Override
    public java.nio.file.Path getCacheDir() {
        return BukkitConcertoPlugin.INSTANCE.getDataFolder().toPath().resolve("cache");
    }

    @Override
    public int getNeteaseMusicQuality() {
        return 0; // Standard quality by default
    }
}
