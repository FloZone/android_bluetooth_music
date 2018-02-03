package fr.frodriguez.bluetoothmusic.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.frodriguez.bluetoothmusic.AppEngine;
import fr.frodriguez.bluetoothmusic.BTDevice;
import fr.frodriguez.bluetoothmusic.BTDeviceListviewAdapter;
import fr.frodriguez.bluetoothmusic.R;
import fr.frodriguez.bluetoothmusic.defines.AppDefines;
import fr.frodriguez.bluetoothmusic.defines.Preferences;


/**
 * By FloZone on 06/10/2017.
 */

//TODO associate a list of app to a bt device
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.switchButton)
    SwitchButton switchButton;
    @BindView(R.id.listView)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the action bar
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        // Init switch button
        switchButton.setChecked(AppEngine.isAppEnabled(this));
        // Initialize the list of available players
        AppEngine.initPlayerList(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateListview();
    }

    // Add a menu button to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    // Handle click on the button added just above
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMenuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Populate the listview with all known bluetooth devices
     */
    private void populateListview() {
        // Get paired bluetooth devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If disabled, can't see paired BTDevices
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.toast_enable_bluetooth, Toast.LENGTH_LONG).show();
            return;
        }
        Set<android.bluetooth.BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            Toast.makeText(this, R.string.toast_error_paired_devices, Toast.LENGTH_LONG).show();
            return;
        }

        // Convert into list of BTDevice
        List<BTDevice> btDevices = AppEngine.convertBluetoothDevices(this, pairedDevices);

        // Populate the listview
        BTDeviceListviewAdapter adapter = new BTDeviceListviewAdapter(this, btDevices);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

    /**
     * On switch click
     */
    @OnClick(R.id.switchButton)
    public void onSwitchClick() {
        SharedPreferences.Editor spe = PreferenceManager.getDefaultSharedPreferences(this).edit();
        // Enable or disable the "watcher"
        spe.putBoolean(Preferences.KEY_ENABLED, switchButton.isChecked());
        spe.apply();
    }

}
