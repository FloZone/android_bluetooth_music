package fr.frodriguez.bluetoothmusic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothClass.Device.Major;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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


// TODO: constraint layout for activity_main & listview_one_icon_two_text
// TODO: respect of Google UI guidelines (paddings/margins)
// TODO: Get permissions at runtime
// TODO: javadoc
// TODO: disable landscape

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listView) ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // Initialize the list of available players
        Utils.initPlayerList(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        populateListview();
    }

    @OnClick(R.id.btnRefresh)
    public void refresh() {
        populateListview();
    }


    // Get list of bluetooth device and display them in the listview
    private void populateListview() {
        Log.d("FLZ", "get bt devices");

        // Get paired bluetooth devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If disabled, can't see paired BTDevices
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_LONG).show();
            return;
        }
        Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices == null) {
            Toast.makeText(this, "Error getting paired bluetooth devices", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert into list of BTDevice
        List<BTDevice> btDevices = Utils.convertBluetoothDevices(this, pairedDevices);

        // Populate the listview
        DeviceListviewAdapter adapter = new DeviceListviewAdapter(this, btDevices);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }



}
