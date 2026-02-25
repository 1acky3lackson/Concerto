package top.gregtao.concerto.common.platform;

import org.slf4j.Logger;

import java.nio.file.Path;

public interface Platform {
    Logger getLogger();
    String translate(String key, Object... args);
    Path getConfigDir();
    Path getCacheDir();
    int getNeteaseMusicQuality();
}
