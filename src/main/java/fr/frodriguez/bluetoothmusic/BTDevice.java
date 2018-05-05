package fr.frodriguez.bluetoothmusic;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fr.frodriguez.bluetoothmusic.defines.AppDefines;

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
    int startMethod;
    Drawable playerIcon;

    public BTDevice(@NonNull String name, @NonNull String mac) {
        this.name = name;
        this.mac = mac;
        this.icon = R.drawable.ic_devices_other_darkgrey_48dp;
        this.player = null;
        this.startMethod = AppDefines.START_METHOD_NOTSET;
        this.playerIcon = null;
    }
}
