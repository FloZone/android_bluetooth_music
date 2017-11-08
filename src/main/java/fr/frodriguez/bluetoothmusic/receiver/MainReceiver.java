package fr.frodriguez.bluetoothmusic.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import fr.frodriguez.bluetoothmusic.AppEngine;

/**
 * By FloZone on 06/10/2017.
 */
//TODO settings: stop playback/app on bt disconnection
//TODO settings: with ui/without ui (= for all 'other packages')
//TODO settings: btn reset to clear shared pref
//TODO settings: display version + licences in html + how it works
//TODO add devices that are in sharedPref but not in paired devices. really necessary ? if in shared pref, it should be in paired devices. If not, it will not auto connects
//TODO all strings in res/strings.xml
public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        // If the app is not "enabled"
        if(!AppEngine.isWatcherEnabled(context)) return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // When a bluetooth device connects
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            // Get the device object
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it is watched
            if (sp.contains(device.getAddress())) {
                Toast.makeText(context, device.getName() + " is now connected :)", Toast.LENGTH_LONG).show();

                // Get the music player to start and start it
                String packageName = sp.getString(device.getAddress(), null);
                AppEngine.startPlayer(context, packageName);
            }
        }

        // When a bluetooth device disconnects
        else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            // Get the device object
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it is watched
            if (sp.contains(device.getAddress())) {
                Toast.makeText(context, device.getName() + " is now disconnected :(", Toast.LENGTH_LONG).show();

                // Stop the music playback ?
            }
        }
    }

}
