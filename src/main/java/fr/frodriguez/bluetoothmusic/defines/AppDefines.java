package fr.frodriguez.bluetoothmusic.defines;

import java.util.HashMap;

/**
 * By FloZone on 04/11/2017.
 */

public final class AppDefines {

    public final static int START_METHOD_NOTSET = -1;
    public final static int START_METHOD_KEYEVENT = 0;
    public final static int START_METHOD_WITHUI = 1;

    public final static int START_PLAYER_DELAY = 1000 * 5;

    public final static String INTENT_DISABLE_BLUETOOTH = "frodriguez.bluetoothmusic.intent.disable_bluetooth";

    public final static HashMap<String, Integer> SUPPORTED_PLAYERS = new HashMap<>(5);
    static {
        SUPPORTED_PLAYERS.put("com.google.android.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.sec.android.app.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.spotify.music", START_METHOD_WITHUI);
        SUPPORTED_PLAYERS.put("com.maxmpz.audioplayer", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.sonyericsson.music", START_METHOD_KEYEVENT);
    }

}
