package fr.frodriguez.bluetoothmusic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothClass.Device.Major;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


// TODO: constraint layout for activity_main & listview_device
// TODO: respect of Google UI guidelines (paddings/margins)
// TODO: Get permissions at runtime

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listView) ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        populateListview();
    }

    @OnClick(R.id.btnRefresh)
    public void refresh() {
        populateListview();
    }

    private void populateListview() {
        Log.d("FLZ", "get bt devices");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not have bluetooth", Toast.LENGTH_LONG).show();
            return;
        }
        // If disabled, can't see paired BTDevices
        if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_LONG).show();
            return;
        }
        // Get paired bluetooth devices
        Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices == null) {
            Toast.makeText(this, "Error getting paired bluetooth devices", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert into list of device
        List<BTDevice> btDevices = convertBluetoothDevices(pairedDevices);

        // Populate the listview
        DeviceListviewAdapter adapter = new DeviceListviewAdapter(this, btDevices);
        listView.setAdapter(adapter);
    }

    /**
     * Convert API BluetoothDevice list to a displayable BTDevice list
     */
    //TODO add devices in sharedPref but not int paired devices
    @NonNull
    private List<BTDevice> convertBluetoothDevices(@NonNull Set<BluetoothDevice> pairedBluetoothDevices) {
        SharedPreferences sp = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE); //TODO static value

        List<BTDevice> btDevices = new ArrayList<>();
        for (BluetoothDevice pairedDevice : pairedBluetoothDevices) {
            BTDevice btDevice = new BTDevice(pairedDevice.getName(), pairedDevice.getAddress());
            int icon;
            switch (pairedDevice.getBluetoothClass().getMajorDeviceClass()) {
                case Major.AUDIO_VIDEO:
                    icon = R.drawable.ic_headset_black_48dp;
                    break;
                case Major.COMPUTER:
                    icon = R.drawable.ic_laptop_chromebook_black_48dp;
                    break;
                case Major.PHONE:
                    icon = R.drawable.ic_phone_android_black_48dp;
                    break;
                case Major.WEARABLE:
                    icon = R.drawable.ic_watch_black_48dp;
                    break;
                default:
                    icon = R.drawable.ic_devices_other_black_48dp;
                    break;
            }
            btDevice.icon = icon;

            btDevice.watched = sp.contains(btDevice.mac);

            btDevices.add(btDevice);
        }
        return btDevices;
    }



    @OnClick(R.id.btnTest1)
    public void test1() {
        PlayerController.startPlayer(this, PlayerController.GOOGLE_PLAYER);
    }

    @OnClick(R.id.btnTest2)
    public void test2() {
        PlayerController.startSpotifyPlayer(this);
    }
}
