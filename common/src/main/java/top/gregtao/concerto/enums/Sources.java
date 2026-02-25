package top.gregtao.concerto.enums;

import top.gregtao.concerto.api.SimpleStringIdentifiable;
import top.gregtao.concerto.common.ConcertoCommon;

public enum Sources implements SimpleStringIdentifiable {
    LOCAL_FILE,
    INTERNET,
    NETEASE_CLOUD,
    QQ_MUSIC,
    KUGOU_MUSIC,
    BILIBILI,
    SHARED
    ;

    public String getName() {
        return ConcertoCommon.getPlatform().translate(this.getKey("source"));
    }

    public String getKey(String main) {
        return "concerto." + main + "." + this.asString();
    }

    public static String getI18nString(String source) {
        return ConcertoCommon.getPlatform().translate("concerto.source." + source);
    }
}
