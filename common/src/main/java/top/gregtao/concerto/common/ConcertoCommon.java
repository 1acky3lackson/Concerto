package top.gregtao.concerto.common;

import top.gregtao.concerto.common.platform.Platform;

public class ConcertoCommon {
    private static Platform platform;

    public static void init(Platform p) {
        platform = p;
    }

    public static Platform getPlatform() {
        if (platform == null) {
            throw new IllegalStateException("Platform not initialized");
        }
        return platform;
    }
}
