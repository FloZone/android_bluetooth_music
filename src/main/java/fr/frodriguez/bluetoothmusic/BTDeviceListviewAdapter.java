package fr.frodriguez.bluetoothmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.frodriguez.bluetoothmusic.defines.AppDefines;

/**
 * By FloZone on 11/10/2017.
 *
 * The listview containing all bluetooth devices.
 * Touch a bluetooth device to select the music player to start when it will connect.
 */
@SuppressWarnings("WeakerAccess")
public class BTDeviceListviewAdapter extends ArrayAdapter<BTDevice> implements AdapterView.OnItemClickListener {

    private AlertDialog dialog;


    public BTDeviceListviewAdapter(Context context, List<BTDevice> btDevices) {
        super(context, 0, btDevices);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_icon_two_text, parent, false);
        }

        IconTwoTextViewHolder viewHolder = (IconTwoTextViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new IconTwoTextViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(viewHolder);
        }

        // Get the BTDevice from the list
        final BTDevice btDevice = getItem(position);
        if (btDevice != null) {
            // Populate the row
            viewHolder.title.setText(btDevice.name);
            viewHolder.subtitle.setText(btDevice.mac);
            viewHolder.icon.setImageResource(btDevice.icon);

            // Row in green if the device is watched
            if (btDevice.player != null) {
                convertView.setBackgroundColor(AppDefines.COLOR_LIGHT_GREEN);
            } else {
                convertView.setBackgroundColor(AppDefines.COLOR_LIGHT_GRAY);
            }
        }

        return convertView;
    }

    /**
     * Display a dialog which ask the music player to start when this device will connect
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View btview, int position, long id) {
        final BTDevice btDevice = getItem(position);
        if (btDevice != null) {
            // The device was "watched", unwatch it
            if (btDevice.player != null) {
                btDevice.player = null;
                AppEngine.saveWatchedState(getContext(), btDevice);
                btview.setBackgroundColor(AppDefines.COLOR_LIGHT_GRAY);
            }
            // Watch the device
            else {
                // Inflate the layout XML
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.dialog_players, null);
                ListView playerListview = (ListView) dialogView.findViewById(R.id.listView);

                // Create the listview of available players
                final PlayerListviewAdapter adapter = new PlayerListviewAdapter(getContext(), AppEngine.PLAYER_LIST);
                playerListview.setAdapter(adapter);

                // When clicking on a player, save it for the bt device
                playerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Player player = adapter.getItem(position);
                        if (player != null) {
                            btDevice.player = player.packageName;
                            AppEngine.saveWatchedState(getContext(), btDevice);
                            btview.setBackgroundColor(AppDefines.COLOR_LIGHT_GREEN);
                            Toast.makeText(getContext(), "Watching for " + btDevice.name + " connection", Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }
                    }
                });

                // Create the dialog containing the listview
                dialog = new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setTitle(R.string.select_player)
                        .setView(dialogView)
                        .create();
                dialog.show();
            }
        }
    }

    /**
     * Dismiss the "select player" popup
     */
    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private class IconTwoTextViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;
    }

}
