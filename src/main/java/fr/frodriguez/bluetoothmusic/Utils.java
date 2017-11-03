package fr.frodriguez.bluetoothmusic;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * By FloZone on 03/11/2017.
 */

public final class Utils {

    /**
     * Convert API BluetoothDevice list to a displayable BTDevice list
     */
    //TODO add devices in sharedPref but not int paired devices
    @NonNull
    public static List<BTDevice> convertBluetoothDevices(@NonNull Context context, @NonNull Set<BluetoothDevice> pairedBluetoothDevices) {
        SharedPreferences sp = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE); //TODO static value

        List<BTDevice> btDevices = new ArrayList<>();
        for (BluetoothDevice pairedDevice : pairedBluetoothDevices) {
            BTDevice btDevice = new BTDevice(pairedDevice.getName(), pairedDevice.getAddress());
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

            btDevice.player = sp.getString(btDevice.mac, null);

            btDevices.add(btDevice);
        }
        return btDevices;
    }




    public final static int START_METHOD_KEYEVENT = 0;
    public final static int START_METHOD_STARTAPP_KEYEVENT = 1;

    public final static HashMap<String, Integer> PLAYERS_PACKAGES = new HashMap<>(3);
    static {
        PLAYERS_PACKAGES.put("com.google.android.music",  START_METHOD_KEYEVENT);
        PLAYERS_PACKAGES.put("com.sec.android.app.music", START_METHOD_KEYEVENT);
        PLAYERS_PACKAGES.put("com.spotify.music",         START_METHOD_STARTAPP_KEYEVENT);
    }


    public static List<Player> PLAYER_LIST = new ArrayList<>();
    public static void initPlayerList(Context context) {
        Log.d("FLZ", "initPlayerList");
        PackageManager packageManager= context.getApplicationContext().getPackageManager();

        PLAYER_LIST.clear();
        for(Map.Entry<String, Integer> player : PLAYERS_PACKAGES.entrySet()) {
            try {
                ApplicationInfo info = packageManager.getApplicationInfo(player.getKey(), PackageManager.GET_META_DATA);

                String name = (String) packageManager.getApplicationLabel(info);
                Drawable icon = packageManager.getApplicationIcon(info);

                PLAYER_LIST.add(new Player(icon, name, player.getKey()));
            } catch (Exception e) {
                // Package not found
            }
        }
    }

    public static void startPlayerWithUI(Context context, String packageName) {
        Log.d("FLZ", "try to start " + packageName + " player with UI");

        // StartIntentForPackage puis startPlayer();
        //Intent spotify = new Intent("com.spotify.mobile.android.ui.widget.NEXT");
        //spotify.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        //context.sendBroadcast(spotify);
    }

    public static void startPlayer(Context context, String packageName) {
        Log.d("FLZ", "try to start " + packageName + " player");
        if(packageName == null) return;

        Intent playIntent = new Intent(Intent.ACTION_MEDIA_BUTTON)
                .setPackage(packageName)
                .putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);

        playIntent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
        context.sendOrderedBroadcast(playIntent, null);
    }

    /**
     * Save the device into shared pref with the player to start when it will connects
     * or remove the device from shared pref
     */
    public static void saveWatchedState(Context context, BTDevice btDevice) {
        SharedPreferences sp = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE); //TODO static value
        SharedPreferences.Editor spe = sp.edit();
        // Save new device
        spe.putString(btDevice.mac, btDevice.player); //null == spe.remove(key)
        spe.apply();
    }
}
