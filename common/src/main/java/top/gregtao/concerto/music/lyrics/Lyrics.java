package top.gregtao.concerto.music.lyrics;

import top.gregtao.concerto.common.ConcertoCommon;
import top.gregtao.concerto.music.MusicTimestamp;
import top.gregtao.concerto.util.MathUtil;
import top.gregtao.concerto.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class Lyrics {

    protected ArrayList<Pair<MusicTimestamp, String>> lyricBody = new ArrayList<>();

    protected int index = 0;

    public void addLine(MusicTimestamp timestamp, String string) {
        this.lyricBody.add(Pair.of(timestamp, string));
        this.lyricBody.sort((p1, p2) -> p1.getFirst().compareTo(p2.getFirst()));
    }

    public String getCurrent(int delta) {
        if (this.index + delta < 0 || this.index + delta >= this.lyricBody.size()) return "";
        return this.lyricBody.get(this.index + delta).getSecond();
    }

    public String getCurrent() {
        return this.getCurrent(0);
    }

    public String nextLine() {
        if (this.index + 1 >= this.lyricBody.size()) return "";
        return this.lyricBody.get(this.index + 1).getSecond();
    }

    public String stayOrNext(long timestamp) {
        if (this.lyricBody.isEmpty()) return "";
        if (this.index < this.lyricBody.size() - 1 &&
                this.lyricBody.get(this.index + 1).getFirst().asMilliseconds() < timestamp) {
            this.index++;
        }
        return this.getCurrent();
    }

    public boolean isEmpty() {
        return this.lyricBody.isEmpty();
    }

    public ArrayList<Pair<MusicTimestamp, String>> getLyricBody() {
        return this.lyricBody;
    }

    public Lyrics load(String raw) {
        try {
            this.parse(raw);
        } catch (Exception e) {
            ConcertoCommon.getPlatform().getLogger().error("Error parsing lyric", e);
        }
        this.sortLines();
        return this;
    }

    private void sortLines() {
        this.lyricBody.sort((p1, p2) -> p1.getFirst().compareTo(p2.getFirst()));
    }

    public abstract void parse(String raw);

    public String startFrom(long timestamp) {
        this.index = 0;
        int size = this.lyricBody.size();
        for (int i = 0; i < size; i++) {
            MusicTimestamp ts = this.lyricBody.get(i).getFirst();
            if (ts.asMilliseconds() >= timestamp) {
                this.index = Math.max(0, i - 1);
                break;
            }
        }
        // If timestamp is past the last lyric, set index to last
        if (size > 0 && timestamp > this.lyricBody.get(size - 1).getFirst().asMilliseconds()) {
            this.index = size - 1;
        }
        return this.getCurrent();
    }
}
