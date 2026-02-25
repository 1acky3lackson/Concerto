package top.gregtao.concerto.config;

import top.gregtao.concerto.common.ConcertoCommon;
import top.gregtao.concerto.util.CommonTextUtil;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CookieFile {
    private final Path path;

    public CookieFile(String name) {
        this.path = ConcertoCommon.getPlatform().getConfigDir().resolve("cookie_" + name + ".txt");
    }

    public void write(String content) {
        try {
            if (!Files.exists(this.path.getParent())) {
                Files.createDirectories(this.path.getParent());
            }
            Files.writeString(this.path, content);
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to write cookie file: " + this.path, e);
        }
    }

    public void write(CookieManager manager) {
        try {
            StringBuilder builder = new StringBuilder();
            for (URI uri : manager.getCookieStore().getURIs()) {
                builder.append(CommonTextUtil.toBase64(uri.toString())).append(":")
                        .append(CommonTextUtil.toBase64(String.join("\n", manager.get(uri, Map.of()).get("Cookie"))))
                        .append('\n');
            }
            this.write(builder.toString());
        } catch (Exception e) {
            ConcertoCommon.getPlatform().getLogger().error("Error writing cookie", e);
        }
    }

    public String read() {
        if (!Files.exists(this.path)) {
            return "";
        }
        try {
            return Files.readString(this.path);
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to read cookie file: " + this.path, e);
            return "";
        }
    }

    public void read(CookieManager manager) {
        try {
            String baseRaw = this.read();
            if (baseRaw.isEmpty()) return;
            String[] lines = baseRaw.split("\n");
            for (String line : lines) {
                String[] args = line.split(":");
                if (args.length != 2) continue;
                URI uri = new URI(CommonTextUtil.fromBase64(args[0]));
                String raw = CommonTextUtil.fromBase64(args[1]);
                List<String> cookies = List.of(raw.split("\n"));
                manager.put(uri, Map.of("Set-Cookie", cookies));
            }
        } catch (Exception e) {
            ConcertoCommon.getPlatform().getLogger().error("Error reading cookie", e);
        }
    }

    public String readAsHeader() {
        try {
            String baseRaw = this.read();
            if (baseRaw.isEmpty()) return "";
            String[] lines = baseRaw.split("\n");
            StringBuilder result = new StringBuilder();
            for (String line : lines) {
                String[] args = line.split(":");
                if (args.length != 2) continue;
                String raw = CommonTextUtil.fromBase64(args[1]).replace("\n", "; ");
                result.append(raw);
            }
            return result.toString();
        } catch (Exception e) {
            ConcertoCommon.getPlatform().getLogger().error("Error reading cookie as header", e);
            return "";
        }
    }
}
