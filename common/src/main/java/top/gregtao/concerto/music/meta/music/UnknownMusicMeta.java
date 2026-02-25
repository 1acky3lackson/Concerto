package top.gregtao.concerto.music.meta.music;

import top.gregtao.concerto.util.CommonTextUtil;

public class UnknownMusicMeta extends TimelessMusicMetaData {

    public UnknownMusicMeta(String source) {
        super(CommonTextUtil.getTranslatable("concerto.unknown"), CommonTextUtil.getTranslatable("concerto.unknown"), source);
    }
}
