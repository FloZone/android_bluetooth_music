package fr.frodriguez.bluetoothmusic.defines;

import android.graphics.Color;

import java.util.HashMap;

/**
 * By FloZone on 04/11/2017.
 */

public final class AppDefines {

    public final static int START_METHOD_KEYEVENT = 0;
    public final static int START_METHOD_STARTAPP = 1;

    public final static HashMap<String, Integer> SUPPORTED_PLAYERS = new HashMap<>(3);

    static {
        SUPPORTED_PLAYERS.put("com.google.android.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.sec.android.app.music", START_METHOD_KEYEVENT);
        SUPPORTED_PLAYERS.put("com.spotify.music", START_METHOD_STARTAPP);
    }

    public static final int COLOR_LIGHT_GREEN = Color.rgb(200, 255, 200);
    public static final int COLOR_LIGHT_GRAY = Color.rgb(252, 252, 252);

}
