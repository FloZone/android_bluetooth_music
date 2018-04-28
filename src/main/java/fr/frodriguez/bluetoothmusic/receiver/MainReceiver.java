package fr.frodriguez.bluetoothmusic.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import fr.frodriguez.bluetoothmusic.AppEngine;
import fr.frodriguez.bluetoothmusic.R;
import fr.frodriguez.bluetoothmusic.defines.AppDefines;
import fr.frodriguez.bluetoothmusic.defines.Preferences;
import fr.frodriguez.library.utils.AppUtils;

/**
 * By FloZone on 06/10/2017.
 */

public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        // If the app is not "enabled"
        if(!AppEngine.isAppEnabled(context)) return;

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // When a bluetooth device connects
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {

            // Get the device object
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it is watched
            if (sp.contains(device.getAddress())) {
                // Cancel the alarm which disable the bluetooth
                AppEngine.cancelDisableBluetoothAlarm(context);

                Toast.makeText(context,
                        context.getResources().getString(R.string.toast_is_connected, device.getName()),
                        Toast.LENGTH_LONG).show();

                // Get the music player to start and start it after few seconds
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String packageName = sp.getString(device.getAddress(), null);
                        AppEngine.startPlayer(context, packageName);
                    }
                }, 1000 * 3);
            }
        }

        // When a bluetooth device disconnects
        else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            // Get the device object
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it is watched
            if (sp.contains(device.getAddress())) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.toast_is_disconnected, device.getName()),
                        Toast.LENGTH_LONG).show();

                // If "stop playback' option is enabled
                if(sp.getBoolean(Preferences.KEY_STOP_PLAYBACK, Preferences.KEY_STOP_PLAYBACK_DEFAULT)) {
                    String packageName = sp.getString(device.getAddress(), null);
                    if(packageName != null) AppEngine.stopPlayerKeyevent(context, packageName);
                }

                // If "disable bluetooth" option is enabled
                if(sp.getBoolean(Preferences.KEY_DISABLE_BT, Preferences.KEY_DISABLE_BT_DEFAULT)) {
                    // Trigger an alarm in Xsec which will disable bluetooth
                    AppEngine.scheduleDisableBluetoothAlarm(context);
                }
            }
        }

        // Disable bluetooth intent
        else if (intent.getAction().equals(AppDefines.INTENT_DISABLE_BLUETOOTH)) {
            AppUtils.switchBluetooth(false);
        }
    }

}
