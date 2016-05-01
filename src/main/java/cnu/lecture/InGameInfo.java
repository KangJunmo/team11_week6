package cnu.lecture;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tchi on 2016. 4. 25..
 */
public class InGameInfo {
    public static class Observer {
        @Getter @Setter
        private String encryptionKey;
    }

    public static class Participant {
        @Getter @Setter
        private String summonerName;
    }

    @Getter @Setter
    private String platformId;

    @Getter @Setter
    private Observer observers;

    @Getter @Setter
    private Participant[] participants;
}
