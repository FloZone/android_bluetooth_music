package fr.frodriguez.bluetoothmusic;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.frodriguez.bluetoothmusic.defines.AppDefines;

/**
 * By FloZone on 03/11/2017.
 */

@SuppressWarnings("WeakerAccess")
public final class AppEngine {

    /**
     * Convert API BluetoothDevice list to a displayable BTDevice list
     */
    //TODO add devices that are in sharedPref but not int paired devices
    @NonNull
    public static List<BTDevice> convertBluetoothDevices(@NonNull Context context, @NonNull Set<BluetoothDevice> pairedBluetoothDevices) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        List<BTDevice> btDevices = new ArrayList<>();
        for (BluetoothDevice pairedDevice : pairedBluetoothDevices) {
            BTDevice btDevice = new BTDevice(pairedDevice.getName(), pairedDevice.getAddress());
            // Get the icon
            int icon;
            switch (pairedDevice.getBluetoothClass().getMajorDeviceClass()) {
                case BluetoothClass.Device.Major.AUDIO_VIDEO:
                    icon = R.drawable.ic_headset_black_48dp;
                    break;
                case BluetoothClass.Device.Major.COMPUTER:
                    icon = R.drawable.ic_laptop_chromebook_black_48dp;
                    break;
                case BluetoothClass.Device.Major.PHONE:
                    icon = R.drawable.ic_phone_android_black_48dp;
                    break;
                case BluetoothClass.Device.Major.WEARABLE:
                    icon = R.drawable.ic_watch_black_48dp;
                    break;
                default:
                    icon = R.drawable.ic_devices_other_black_48dp;
                    break;
            }
            btDevice.icon = icon;

            // Get the music player for this device if it is watched
            btDevice.player = sp.getString(btDevice.mac, null);

            btDevices.add(btDevice);
        }
        return btDevices;
    }


    /**
     * Initialize the list of available music players
     */
    public static List<Player> PLAYER_LIST = new ArrayList<>();
    public static void initPlayerList(@NonNull Context context) {
        PLAYER_LIST.clear();

        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        // For each supported music player
        for (Map.Entry<String, Integer> player : AppDefines.SUPPORTED_PLAYERS.entrySet()) {
            try {
                // Check if it is installed and get its info
                ApplicationInfo info = packageManager.getApplicationInfo(player.getKey(), PackageManager.GET_META_DATA);
                String name = (String) packageManager.getApplicationLabel(info);
                Drawable icon = packageManager.getApplicationIcon(info);
                PLAYER_LIST.add(new Player(icon, name, player.getKey()));
            }
            // Player not installed
            catch (Exception ignored) {
            }
        }
    }

    /**
     * Starts the given music player with the correct method
     */
    public static void startPlayer(@NonNull Context context, @Nullable String packageName) {
        if (packageName == null) return;

        int startMethod = AppDefines.SUPPORTED_PLAYERS.get(packageName);
        switch (startMethod) {
            case AppDefines.START_METHOD_KEYEVENT:
                AppEngine.startPlayerKeyevent(context, packageName);
                break;

            case AppDefines.START_METHOD_STARTAPP:
                AppEngine.startPlayerWithUI(context, packageName);
                break;
        }
    }

    /**
     * Starts the given music player playback by sending a PLAY key event
     */
    public static void startPlayerKeyevent(@NonNull Context context, @NonNull String packageName) {
        Intent playIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        playIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        playIntent.setPackage(packageName);
        // Key down
        playIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);
        // Key up
        playIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);
    }

    /**
     * Starts the given music player application and then, a PLAY key event
     */
    public static void startPlayerWithUI(@NonNull final Context context, @NonNull final String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent appIntent = pm.getLaunchIntentForPackage(packageName);

        // Package is not installed
        if(appIntent == null) {
            return;
        }
        // Start the given app
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(appIntent);

        // Let enough time for the app to start, then send PLAY key event
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlayerKeyevent(context, packageName);
            }
        }, 1000 * 5);
    }

    /**
     * Saves the music player associated to the bluetooth device or remove the device from shared
     * preferences
     */
    public static void saveWatchedState(@NonNull Context context, @NonNull BTDevice btDevice) {
        SharedPreferences.Editor spe = PreferenceManager.getDefaultSharedPreferences(context).edit();
        // If btDevice.player == null, equivalent to spe.remove(key)
        spe.putString(btDevice.mac, btDevice.player);
        spe.apply();
    }

}
