package top.gregtao.concerto.enums;

import top.gregtao.concerto.api.SimpleStringIdentifiable;
import top.gregtao.concerto.common.ConcertoCommon;

public enum OrderType implements SimpleStringIdentifiable {
    NORMAL,
    RANDOM,
    REVERSED,
    LOOP;

    public String getName() {
        return ConcertoCommon.getPlatform().translate("concerto.order." + this.asString());
    }
}
