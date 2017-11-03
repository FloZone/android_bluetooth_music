package fr.frodriguez.bluetoothmusic;

import android.graphics.drawable.Drawable;

/**
 * By FloZone on 02/11/2017.
 */

/**
 * Represent a music player, for the listview
 */
public class Player {
    Drawable icon;
    String name;
    String packageName;

    public Player(Drawable icon, String name, String packageName) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
    }
}
