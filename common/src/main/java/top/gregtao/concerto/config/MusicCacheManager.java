package top.gregtao.concerto.config;

import com.google.gson.JsonObject;
import top.gregtao.concerto.api.CacheableMusic;
import top.gregtao.concerto.api.MusicJsonParsers;
import top.gregtao.concerto.api.MusicSourceNotFoundException;
import top.gregtao.concerto.util.HashUtil;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class MusicCacheManager extends CacheManager {

    public static final MusicCacheManager INSTANCE = new MusicCacheManager();

    protected MusicCacheManager() {
        super("musics");
    }

    protected MusicCacheManager(int maxSize) {
        super("musics", maxSize);
    }

    public File getChild(CacheableMusic music) {
        JsonObject json = MusicJsonParsers.to(music.getMusic(), false);
        if (json == null) return null;
        String filename = HashUtil.md5(json.toString()) + "." + music.getSuffix();
        return this.exists(filename) ? super.getChild(filename) : null;
    }

    public void addMusic(CacheableMusic music) throws MusicSourceNotFoundException, IOException, UnsupportedAudioFileException {
        JsonObject json = MusicJsonParsers.to(music.getMusic(), false);
        if (json == null) return;
        this.addFile(HashUtil.md5(json.toString()) + "." + music.getSuffix(), music.getMusic().getMusicSource());
    }

    public void addMusic(CacheableMusic music, String oldSuffix) throws UnsupportedAudioFileException, IOException, InterruptedException {
        JsonObject json = MusicJsonParsers.to(music.getMusic(), false);
        if (json == null) return;
        String md5 = HashUtil.md5(json.toString());
        String filename = md5 + "." + oldSuffix;

        // Converting logic was commented out in original code, so I'll keep it simple for now or throw exception as original did
        // Or adapt it if ffmpeg is available? For now, just throw as original did at the end.

        this.addFile(filename, music.getMusic().getMusicSource());
        throw new UnsupportedAudioFileException("Conversion not supported in common yet");
    }
}
