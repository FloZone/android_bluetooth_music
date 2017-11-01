package fr.frodriguez.bluetoothmusic;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Florian on 06/10/2017.
 */

public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE); //TODO static value

        if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(sp.contains(device.getAddress())) {
                Log.d("FLZ", device.getName() + " is now connected :)");
                Toast.makeText(context, device.getName() + " is now connected :)", Toast.LENGTH_LONG).show();

                PlayerController.startPlayer(context, PlayerController.GOOGLE_PLAYER);
            }
        }

        else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(sp.contains(device.getAddress())) {
                Toast.makeText(context, device.getName() + " is now disconnected :(", Toast.LENGTH_LONG).show();
                Log.d("FLZ", device.getName() + " is now disconnected :(");
            }
        }
    }


}
