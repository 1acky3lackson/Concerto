package top.gregtao.concerto.player;

import top.gregtao.concerto.enums.OrderType;
import top.gregtao.concerto.music.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerState {
    private final List<Music> musicList;
    private final int currentIndex;
    private final OrderType orderType;

    public MusicPlayerState(List<Music> musicList, int currentIndex, OrderType orderType) {
        this.musicList = musicList;
        this.currentIndex = currentIndex;
        this.orderType = orderType;
    }

    public MusicPlayerState() {
        this(new ArrayList<>(), -1, OrderType.NORMAL);
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
