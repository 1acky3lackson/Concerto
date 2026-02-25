package top.gregtao.concerto.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.gregtao.concerto.api.*;
import top.gregtao.concerto.enums.Sources;
import top.gregtao.concerto.http.HttpURLInputStream;
import top.gregtao.concerto.http.netease.NeteaseCloudApiClient;
import top.gregtao.concerto.music.lyrics.DefaultFormatLyrics;
import top.gregtao.concerto.music.lyrics.Lyrics;
import top.gregtao.concerto.music.meta.music.BasicMusicMetaData;
import top.gregtao.concerto.music.meta.music.MusicMetaData;
import top.gregtao.concerto.music.meta.music.UnknownMusicMeta;
import top.gregtao.concerto.util.FileUtil;
import top.gregtao.concerto.util.Pair;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NeteaseCloudMusic extends Music implements CacheableMusic, DynamicPath, Likeable {
    private final String id;
    private final Level level;
    private String rawPath, rawLyrics, rawSubLyrics, format;

    public NeteaseCloudMusic(String id, Level level) {
        this.id = id;
        this.level = level;
    }

    public NeteaseCloudMusic(JsonObject object, Level level) {
        this.id = object.get("id").getAsString();
        this.level = level;
        this.setMusicMeta(parseMetaData(object));
    }

    @Override
    public InputStream getMusicSource() throws MusicSourceNotFoundException {
        try {
            return FileUtil.createBuffered(new HttpURLInputStream(URI.create(this.getRawPath()).toURL(), this::getRawPath));
        } catch (Exception e) {
            throw new MusicSourceNotFoundException(e);
        }
    }

    @Override
    public String getLink() {
        return "https://music.163.com/song?id=" + this.getId();
    }

    public String getRawPath() {
        try {
            JsonObject object = NeteaseCloudApiClient.INSTANCE.getMusicLink(this.id, this.level)
                    .getAsJsonArray("data").get(0).getAsJsonObject();
            this.rawPath = object.get("url").getAsString();
            this.rawPath = this.rawPath.isEmpty() ? null : this.rawPath;
            if (this.rawPath != null) {
                this.format = FileUtil.getSuffix(URI.create(this.rawPath).getPath());
            }
        } catch (Exception e) {
            this.rawPath = null;
            this.format = null;
        }
        return this.rawPath;
    }

    @Override
    public String getLastRawPath() {
        if (this.rawPath == null) return this.getRawPath();
        return this.rawPath;
    }

    @Override
    public String updateRawPath() {
        return this.getRawPath();
    }

    @Override
    public String getLastSuffix() {
        if (this.format == null) return this.getRawPath();
        return this.format;
    }

    @Override
    public String getLastLyrics() {
        if (this.rawLyrics == null) this.getLyrics();
        return this.rawLyrics;
    }

    @Override
    public String getLastSubLyrics() {
        return this.rawSubLyrics;
    }

    @Override
    public Pair<Lyrics, Lyrics> getLyrics() {
        try {
            Pair<String, String> pair = NeteaseCloudApiClient.INSTANCE.getLyrics(this.id);
            this.rawLyrics = pair.getFirst();
            this.rawSubLyrics = pair.getSecond();
            Lyrics lyrics1 = new DefaultFormatLyrics().load(pair.getFirst());
            Lyrics lyrics2 = new DefaultFormatLyrics().load(pair.getSecond());
            return Pair.of(lyrics1.isEmpty() ? null : lyrics1, lyrics2.isEmpty() ? null : lyrics2);
        } catch (Exception e) {
            return null;
        }
    }

    public static MusicMetaData parseMetaData(JsonObject object) {
        String name = object.get("name").getAsString();
        long duration = object.get("dt").getAsLong();
        JsonArray authors = object.getAsJsonArray("ar");
        List<String> authorList = new ArrayList<>();
        authors.forEach(element -> {
            JsonElement nameElement = element.getAsJsonObject().get("name");
            if (!nameElement.isJsonNull()) authorList.add(nameElement.getAsString());
        });
        JsonObject album = object.getAsJsonObject("al");
        String headPic = "";
        if (!album.isJsonNull()) {
            JsonElement element = album.get("picUrl");
            if (element != null && !element.isJsonNull()) headPic = element.getAsString();
        }
        return new BasicMusicMetaData(String.join(", ", authorList), name,
                Sources.NETEASE_CLOUD.getName(), duration, headPic);
    }

    @Override
    public void load() {
        try {
            JsonObject object = NeteaseCloudApiClient.INSTANCE.getMusicDetail(this.id)
                    .getAsJsonArray("songs").get(0).getAsJsonObject();
            this.setMusicMeta(parseMetaData(object));
        } catch (Exception e) {
            this.setMusicMeta(new UnknownMusicMeta(Sources.NETEASE_CLOUD.getName()));
        }
        super.load();
    }

    @Override
    public JsonParser<Music> getJsonParser() {
        return MusicJsonParsers.NETEASE_CLOUD;
    }

    public String getId() {
        return this.id;
    }

    public Level getLevel() {
        return this.level;
    }

    @Override
    public String getSuffix() {
        return this.getLastSuffix();
    }

    @Override
    public Music getMusic() {
        return this;
    }

    @Override
    public boolean likeIt() {
        return NeteaseCloudApiClient.LOCAL_USER.likeMusic(this);
    }

    @Override
    public boolean dislikeIt() {
        return NeteaseCloudApiClient.LOCAL_USER.dislikeMusic(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NeteaseCloudMusic music) && music.level == this.level && music.id.equals(this.id);
    }

    public enum Level implements SimpleStringIdentifiable {
        STANDARD(0, "standard"),
        HIGHER(1, "higher"),
        EXHIGH(2, "exhigh"),
        LOSSLESS(3, "lossless"),
        HIRES(4, "hires"),
        JY_STANDARD(5, "jymaster"),
        JY_HIGHER(6, "jyeffect"),
        SKY(7, "sky"),
        JY_MASTER(8, "jymaster");

        private final int rank;
        private final String key;

        Level(int rank, String key) {
            this.rank = rank;
            this.key = key;
        }

        public int getRank() {
            return rank;
        }

        public String getKey() {
            return key;
        }

        public String asString() {
            return this.key;
        }

        public static Level byRank(int rank) {
            for (Level level : values()) {
                if (level.rank == rank) return level;
            }
            return STANDARD;
        }
    }
}
