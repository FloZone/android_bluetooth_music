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
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.frodriguez.bluetoothmusic.defines.AppDefines;
import fr.frodriguez.bluetoothmusic.defines.Preferences;
import fr.frodriguez.library.utils.AppUtils;

/**
 * By FloZone on 03/11/2017.
 */
@SuppressWarnings("WeakerAccess")
public final class AppEngine {

    /**
     * Return whether the app watch for bluetooth device connection or not
     */
    public static boolean isAppEnabled(@NonNull Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // If the main switch is disabled, return false
        if(!sp.getBoolean(Preferences.KEY_ENABLED, Preferences.ENABLED_DEFAULT)) {
            return false;
        }

        // If the battery level is watched, check it
        else if(sp.getBoolean(Preferences.KEY_WATCH_BATTERY, Preferences.KEY_WATCH_BATTERY_DEFAULT)) {
            int watchedBatteryLevel = Integer.valueOf(sp.getString(Preferences.KEY_BATTERY_LEVEL, Preferences.KEY_BATTERY_LEVEL_DEFAULT));
            int deviceBatteryLevel = AppUtils.getBatteryLevel(context);
            if(deviceBatteryLevel <= watchedBatteryLevel) return false;
        }

        return true;
    }

    /**
     * Convert API BluetoothDevice list to a displayable BTDevice list
     */
    @NonNull
    public static List<BTDevice> convertBluetoothDevices(@NonNull Context context, @NonNull Set<BluetoothDevice> pairedBluetoothDevices) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        PackageManager packageManager = context.getApplicationContext().getPackageManager();

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

            // Get the player icon if the device is watched
            if(btDevice.player != null) {
                try {
                    ApplicationInfo info = packageManager.getApplicationInfo(btDevice.player, PackageManager.GET_META_DATA);
                    btDevice.playerIcon = packageManager.getApplicationIcon(info);
                } catch (Exception ignored) {
                    // Exception thrown if the player app no longer exists, so unwatch the device
                    btDevice.player = null;
                    btDevice.playerIcon = null;
                    sp.edit().remove(btDevice.mac).apply();
                }
            }

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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int startMethod;
        // If the player is not supported,
        // or if "start with UI" option if enabled,
        // start the player with UI (captain obvious)
        if(!AppDefines.SUPPORTED_PLAYERS.containsKey(packageName)
            || sp.getBoolean(Preferences.KEY_START_UI, Preferences.KEY_START_UI_DEFAULT)) {
            startMethod = AppDefines.START_METHOD_WITHUI;
        }
        // Else, set the known start method for this player
        else {
            startMethod = AppDefines.SUPPORTED_PLAYERS.get(packageName);
        }
        switch (startMethod) {
            case AppDefines.START_METHOD_KEYEVENT:
                AppEngine.startPlayerKeyevent(context, packageName);
                break;

            case AppDefines.START_METHOD_WITHUI:
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
