package top.gregtao.concerto.api;

public interface SimpleStringIdentifiable {

    default String asString() {
        return this.toString().toLowerCase();
    }
}
