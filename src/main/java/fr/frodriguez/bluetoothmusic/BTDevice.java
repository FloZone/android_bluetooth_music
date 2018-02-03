package fr.frodriguez.bluetoothmusic;

import android.graphics.drawable.Drawable;

/**
 * By FloZone on 11/10/2017.
 *
 * Represent a bluetooth device, for the listview
 */

@SuppressWarnings("WeakerAccess")
public class BTDevice {
    int icon;
    String name;
    String mac;
    String player;
    Drawable playerIcon;

    public BTDevice(String name, String mac) {
        this.name = name;
        this.mac = mac;
        this.icon = R.drawable.ic_devices_other_darkgrey_48dp;
        this.player = null;
        this.playerIcon = null;
    }
}
