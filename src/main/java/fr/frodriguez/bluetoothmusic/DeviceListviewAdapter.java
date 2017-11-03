package fr.frodriguez.bluetoothmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

/**
 * Created by Florian on 11/10/2017.
 */

/**
 * The listview containing all bluetooth devices.
 * Touch a bluetooth device to select the player to start when it will connect.
 */
@SuppressWarnings("WeakerAccess")
public class DeviceListviewAdapter extends ArrayAdapter<BTDevice> implements AdapterView.OnItemClickListener {

    private AlertDialog dialog;


    public DeviceListviewAdapter(Context context, List<BTDevice> btDevices) {
        super(context, 0, btDevices);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_icon_two_text, parent, false);
        }

        IconTwoTextViewHolder viewHolder = (IconTwoTextViewHolder) convertView.getTag();
        if(viewHolder == null) {
            viewHolder = new IconTwoTextViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(viewHolder);
        }

        // Get the BTDevice from the list
        final BTDevice btDevice = getItem(position);
        if(btDevice != null) {
            // Populate the row
            viewHolder.title.setText(btDevice.name);
            viewHolder.subtitle.setText(btDevice.mac);
            viewHolder.icon.setImageResource(btDevice.icon);

            if(btDevice.player != null) { // watched
                convertView.setBackgroundColor(Color.rgb(200, 255, 200)); //TODO static value
            } else { // unwatched
                convertView.setBackgroundColor(Color.rgb(252, 252, 252)); //TODO static value
            }
        }

        return convertView;
    }


    private void onBTDeviceClick(@NonNull View btView, @NonNull BTDevice btDevice) {
        // The device was "watched", unwatch it
        if(btDevice.player != null) {
            btDevice.player = null;
            Utils.saveWatchedState(getContext(), btDevice);
            btView.setBackgroundColor(Color.rgb(252, 252, 252)); //TODO static value
        }

        else {
            // TODO inflate a layout from xml file
            // Create the listview of available players
            ListView playerListview = new ListView(getContext());
            PlayerListviewAdapter adapter = new PlayerListviewAdapter(btDevice, btView, getContext(), Utils.PLAYER_LIST);
            playerListview.setAdapter(adapter);
            playerListview.setOnItemClickListener(adapter);

            // Create the dialog containing the listview
            dialog = new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setTitle("Select a player")
                    .setView(playerListview)
                    .create();
            dialog.show();

        }
    }

    // Click on a device = select the player to starts
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BTDevice btDevice = getItem(position);
        if(btDevice != null) {
            onBTDeviceClick(view, btDevice);
        }
    }

    public void dismissDialog() {
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private class IconTwoTextViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;
    }








    public class PlayerListviewAdapter extends ArrayAdapter<Player> implements AdapterView.OnItemClickListener {

        private BTDevice btDevice;
        private View btView;


        public PlayerListviewAdapter(BTDevice btDevice, View btView, Context context, List<Player> players) {
            super(context, 0, players);
            this.btDevice = btDevice;
            this.btView = btView;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_icon_two_text, parent, false);
            }

            SecondIconTwoTextViewHolder viewHolder = (SecondIconTwoTextViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new SecondIconTwoTextViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
                convertView.setTag(viewHolder);
            }

            // Get the Player from the list
            final Player player = getItem(position);
            if(player != null) {
                // Populate the row
                viewHolder.title.setText(player.name);
                viewHolder.subtitle.setText(player.packageName);
                viewHolder.icon.setImageDrawable(player.icon);
            }

            return convertView;
        }

        // Click on a player = associate this player to this device
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Player player = getItem(position);
            if(player != null) {
                btDevice.player = player.packageName;
                Utils.saveWatchedState(getContext(), btDevice);
                btView.setBackgroundColor(Color.rgb(200, 255, 200)); //TODO static value
                Toast.makeText(getContext(), "Watching for " + btDevice.name + " connection", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        }

        private class SecondIconTwoTextViewHolder {
            ImageView icon;
            TextView title;
            TextView subtitle;
        }
    }
}
