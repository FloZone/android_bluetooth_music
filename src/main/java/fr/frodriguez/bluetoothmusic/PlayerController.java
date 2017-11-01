package fr.frodriguez.bluetoothmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;

/**
 * By FloZone on 01/11/2017.
 */
//TODO faire un service
@SuppressWarnings("WeakerAccess")
public final class PlayerController {

    public final static String GOOGLE_PLAYER  = "com.google.android.music";
    public final static String SAMSUNG_PLAYER = "com.sec.android.app.music";
    public final static String SPOTIFY_PLAYER = "com.spotify.music";


    public static void startSpotifyPlayer(Context context) {
        Log.d("FLZ", "try to start spotify");

        // Test start spotify. Only work when spotify service is running (even if not playing)
        // Start activity then call startPlayer
        Intent spotify = new Intent("com.spotify.mobile.android.ui.widget.NEXT");
        spotify.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(spotify);
    }

    public static void startPlayer(Context context, String playerPackage) {
        Log.d("FLZ", "try to start " + playerPackage + " player");

        Intent playIntent = new Intent(Intent.ACTION_MEDIA_BUTTON)
                .setPackage(playerPackage)
                .putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);

        playIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);
    }

}
