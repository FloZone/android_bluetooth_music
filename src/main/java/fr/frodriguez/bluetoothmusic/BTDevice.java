package fr.frodriguez.bluetoothmusic;

/**
 * Created by Florian on 11/10/2017.
 */

class BTDevice {
    int icon;
    String name;
    String mac;
    boolean watched; //TODO au lieu de bool, enum qui représente le player à lancer

    public BTDevice(String name, String mac) {
        this.name = name;
        this.mac = mac;
        this.icon = R.drawable.ic_devices_other_black_48dp;
        this.watched = false;
    }
}
