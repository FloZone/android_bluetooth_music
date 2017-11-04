package fr.frodriguez.bluetoothmusic.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.frodriguez.bluetoothmusic.AppEngine;
import fr.frodriguez.bluetoothmusic.BTDevice;
import fr.frodriguez.bluetoothmusic.BTDeviceListviewAdapter;
import fr.frodriguez.bluetoothmusic.R;

/**
 * By FloZone on 06/10/2017.
 */

// TODO: constraint layout for all xml
// TODO: respect of Google UI guidelines (paddings/margins)
// TODO: Get permissions at runtime

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listView)
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Initialize the list of available players
        AppEngine.initPlayerList(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListview();
    }

    /**
     * Populate the listview with all known bluetooth devices
     */
    private void populateListview() {
        // Get paired bluetooth devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If disabled, can't see paired BTDevices
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_LONG).show();
            return;
        }
        Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            Toast.makeText(this, "Error getting paired bluetooth devices", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert into list of BTDevice
        List<BTDevice> btDevices = AppEngine.convertBluetoothDevices(this, pairedDevices);

        // Populate the listview
        BTDeviceListviewAdapter adapter = new BTDeviceListviewAdapter(this, btDevices);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

}
