package fr.frodriguez.bluetoothmusic.defines;

import java.util.HashMap;

/**
 * By FloZone on 04/11/2017.
 */

public final class AppDefines {

    public final static int PERMISSION_REQUEST_BLUETOOTH = 1;

    public final static String SHARED_PREF_KEY_ENABLED      = "sp_enabled";
    public final static boolean SHARED_PREF_DEFAULT_ENABLED = false;

    public final static int START_METHOD_DEFAULT  = 0;
    public final static int START_METHOD_KEYEVENT = 1;
    public final static int START_METHOD_STARTAPP = 2;

    public final static HashMap<String, Integer> SUPPORTED_PLAYERS = new HashMap<>(3);

    static {
        SUPPORTED_PLAYERS.put("com.google.android.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.sec.android.app.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.spotify.music", START_METHOD_STARTAPP);
    }

}
