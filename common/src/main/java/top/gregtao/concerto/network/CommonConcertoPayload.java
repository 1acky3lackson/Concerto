package top.gregtao.concerto.network;

public class CommonConcertoPayload {
    public String string;
    public Channel channel;

    public CommonConcertoPayload(Channel channel, String s) {
        this.channel = channel;
        this.string = s;
    }

    public enum Channel {
        MUSIC_DATA('0'),
        HANDSHAKE('1'),
        AUDITION_SYNC('2'),
        MUSIC_ROOM('3'),
        PRESET_RADIOS('4'),
        MUSIC_AGENT('5');

        public static Channel getById(char id) {
            for (Channel channel1 : values()) {
                if (channel1.id == id) {
                    return channel1;
                }
            }
            return MUSIC_DATA;
        }

        public final char id;
        Channel(char id) {
            this.id = id;
        }
    }
}
