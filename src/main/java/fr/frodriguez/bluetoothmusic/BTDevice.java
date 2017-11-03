package fr.frodriguez.bluetoothmusic;

/**
 * Created by Florian on 11/10/2017.
 */

/**
 * Represent a bluetooth device, for the listview
 */
class BTDevice {
    int icon;
    String name;
    String mac;
    String player;

    public BTDevice(String name, String mac) {
        this.name = name;
        this.mac = mac;
        this.icon = R.drawable.ic_devices_other_black_48dp;
        this.player = null;
    }
}
