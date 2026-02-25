package top.gregtao.concerto.config;

import top.gregtao.concerto.common.ConcertoCommon;
import top.gregtao.concerto.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheManager {

    private final Path folder;
    private long maxSize = 100L * 1024 * 1024; // 100 MB

    public CacheManager(String name) {
        this.folder = ConcertoCommon.getPlatform().getCacheDir().resolve(name);
        try {
            if (!Files.exists(this.folder)) {
                Files.createDirectories(this.folder);
            }
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to create cache directory: {}", this.folder, e);
        }
    }

    public CacheManager(String name, long maxSize) {
        this(name);
        this.maxSize = maxSize;
    }

    public static void cleanAllCache() {
        Path cacheRoot = ConcertoCommon.getPlatform().getCacheDir();

        if (!Files.exists(cacheRoot) || !Files.isDirectory(cacheRoot)) {
            return;
        }

        try (Stream<Path> walk = Files.walk(cacheRoot, 1)) {
            walk.filter(path -> !path.equals(cacheRoot)).forEach(CacheManager::deleteRecursively);
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to scan cache folder: {}", cacheRoot, e);
        }
    }

    private static void deleteRecursively(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    ConcertoCommon.getPlatform().getLogger().error("Failed to delete: {}", p, e);
                }
            });
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to traverse path: {}", path, e);
        }
    }

    public File getChild(String child) {
        return this.folder.resolve(child).toFile();
    }

    public long getTotalSize() {
        long size = 0;
        try (Stream<Path> stream = Files.list(this.folder)) {
            size = stream.filter(Files::isRegularFile)
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to calculate cache size", e);
        }
        return size;
    }

    public void removeEarliest() {
        try (Stream<Path> stream = Files.list(this.folder)) {
            List<Pair<File, Long>> files = stream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(file -> {
                        try {
                            BasicFileAttributes attributes = Files.getFileAttributeView(
                                    file.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
                            return Pair.of(file, attributes.creationTime().toMillis());
                        } catch (IOException e) {
                            return Pair.of(file, 0L);
                        }
                    })
                    .sorted(Comparator.comparingLong(Pair::getSecond))
                    .collect(Collectors.toList());

            long currentSize = getTotalSize();
            int index = 0;
            while (currentSize > this.maxSize && index < files.size()) {
                File file = files.get(index).getFirst();
                long len = file.length();
                if (file.delete()) {
                    currentSize -= len;
                } else {
                    ConcertoCommon.getPlatform().getLogger().warn("Cannot remove file {}", file.getAbsolutePath());
                }
                index++;
            }
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to list files for removal", e);
        }
    }

    public boolean exists(String filename) {
        return Files.exists(this.folder.resolve(filename));
    }

    public void addFile(String filename, InputStream inputStream) {
        Path filePath = this.folder.resolve(filename);
        try {
            if (Files.exists(filePath)) return;
            Files.copy(inputStream, filePath);
            inputStream.close();

            if (getTotalSize() > this.maxSize) {
                new Thread(this::removeEarliest).start();
            }
        } catch (IOException e) {
            ConcertoCommon.getPlatform().getLogger().error("Failed to write cache file: {}", filename, e);
        }
    }
}
