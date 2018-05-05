package fr.frodriguez.bluetoothmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.frodriguez.bluetoothmusic.defines.AppDefines;
import fr.frodriguez.library.utils.StringUtils;

/**
 * By FloZone on 11/10/2017.
 *
 * The listview containing all bluetooth devices.
 * Touch a bluetooth device to select the music player to start when it will connect.
 */
@SuppressWarnings("WeakerAccess")
public class BTDeviceListviewAdapter extends ArrayAdapter<BTDevice> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private AlertDialog dialog;


    public BTDeviceListviewAdapter(@NonNull Context context, @NonNull List<BTDevice> btDevices) {
        super(context, 0, btDevices);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_two_icon_two_text, parent, false);
        }

        TwoIconTwoTextViewHolder viewHolder = (TwoIconTwoTextViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new TwoIconTwoTextViewHolder();
            viewHolder.icon1 = convertView.findViewById(R.id.icon1);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
            viewHolder.icon2 = convertView.findViewById(R.id.icon2);
            viewHolder.icon3 = convertView.findViewById(R.id.icon3);
            convertView.setTag(viewHolder);
        }

        // Get the BTDevice from the list
        final BTDevice btDevice = getItem(position);
        if (btDevice != null) {
            // Populate the row
            viewHolder.title.setText(btDevice.name);
            viewHolder.subtitle.setText(btDevice.mac);
            viewHolder.icon1.setImageResource(btDevice.icon);

            // Player icon
            if (btDevice.playerIcon != null) {
                viewHolder.icon2.setImageDrawable(btDevice.playerIcon);
            } else {
                viewHolder.icon2.setImageDrawable(null);
            }

            // Row in green if the device is watched
            if (btDevice.player != null) {
                convertView.setBackgroundResource(R.color.colorBackgroundSelected);

                // Little icon if this player will be starter with UI, only if the device is watched
                if (btDevice.startMethod == AppDefines.START_METHOD_WITHUI) {
                    viewHolder.icon3.setImageResource(R.drawable.ic_fullscreen_darkgrey_24dp);
                } else {
                    viewHolder.icon3.setImageDrawable(null);
                }
            } else {
                convertView.setBackgroundResource(R.color.colorBackground);
                viewHolder.icon3.setImageDrawable(null);
            }
        }

        return convertView;
    }

    /**
     * Display a dialog which ask the music player to start when this device will connect
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View btView, int position, long id) {
        final BTDevice btDevice = getItem(position);
        if (btDevice != null) {
            // The device was "watched", unwatch it
            if (btDevice.player != null) {
                btDevice.player = null;
                btDevice.startMethod = AppDefines.START_METHOD_NOTSET;
                AppEngine.saveWatchedState(getContext(), btDevice);
                btView.setBackgroundResource(R.color.colorBackground);
                ImageView playerIcon = btView.findViewById(R.id.icon2);
                if(playerIcon != null) {
                    playerIcon.setImageDrawable(null);
                }
                ImageView startMethodIcon = btView.findViewById(R.id.icon3);
                if(startMethodIcon != null) {
                    startMethodIcon.setImageDrawable(null);
                }
            }
            // Watch the device
            else {
                // Inflate the layout XML
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.dialog_players, null);

                // LISTVIEW
                // Create the listview of available players
                ListView playerListview = dialogView.findViewById(R.id.listView);
                final PlayerListviewAdapter adapter = new PlayerListviewAdapter(getContext(), AppEngine.PLAYER_LIST);
                playerListview.setAdapter(adapter);
                // When clicking on a player, save it for the bt device
                playerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Player player = adapter.getItem(position);
                        if (player != null) {
                            watchDevice(btDevice, player.packageName, player.icon, btView);
                        }
                    }
                });

                // EDITTEXT
                final EditText etOtherPlayer = dialogView.findViewById(R.id.etOtherPlayer);
                final ImageButton btnOtherPlayer = dialogView.findViewById(R.id.btnOtherPlayer);
                btnOtherPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the packageName
                        String packageName = etOtherPlayer.getText().toString();
                        if(StringUtils.isEmpty(packageName)) {
                            etOtherPlayer.setError(getContext().getResources().getString(R.string.package_empty));
                            return;
                        }
                        // Get the package icon
                        PackageManager packageManager = getContext().getApplicationContext().getPackageManager();
                        Drawable icon;
                        try {
                            ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                            icon = packageManager.getApplicationIcon(info);
                        } catch (Exception ignored) {
                            etOtherPlayer.setError(getContext().getResources().getString(R.string.package_not_exists));
                            return;
                        }
                        // Watch the device with the given player
                        watchDevice(btDevice, packageName, icon, btView);
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
     * Switch between "start with UI" and "start with play keyevent" when long clicking on a device which is already watched
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View btView, int position, long id) {
        final BTDevice btDevice = getItem(position);
        if (btDevice != null) {
            // The device was "watched", switch start method
            if (btDevice.player != null) {
                // Switch the current start method for this player
                if(btDevice.startMethod == AppDefines.START_METHOD_KEYEVENT) {
                    btDevice.startMethod = AppDefines.START_METHOD_WITHUI;
                } else {
                    btDevice.startMethod = AppDefines.START_METHOD_KEYEVENT;
                }
                // Save it
                AppEngine.saveWatchedState(getContext(), btDevice);

                // Display or not the "start method icon"
                ImageView startMethodIcon = btView.findViewById(R.id.icon3);
                if (btDevice.startMethod == AppDefines.START_METHOD_WITHUI) {
                    startMethodIcon.setImageResource(R.drawable.ic_fullscreen_darkgrey_24dp);
                } else {
                    startMethodIcon.setImageDrawable(null);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Watch the btDevice with the given music player
     * @param btDevice      the bluetooth device to watch
     * @param packageName   the music player package name to starts when the device connects
     * @param icon          the icon of the music player
     * @param btview        the listview row of the bluetooth device
     */
    public void watchDevice(@NonNull BTDevice btDevice, @NonNull String packageName, @Nullable Drawable icon, @NonNull View btview) {
        btDevice.player = packageName;
        btDevice.startMethod = AppEngine.getStartMethod(packageName);
        btDevice.playerIcon = icon;
        AppEngine.saveWatchedState(getContext(), btDevice);
        // Set background color
        btview.setBackgroundResource(R.color.colorBackgroundSelected);
        // Set player icon
        ImageView playerIcon = btview.findViewById(R.id.icon2);
        if(playerIcon != null) {
            playerIcon.setImageDrawable(icon);
        }
        // Display or not the "start method icon"
        ImageView startMethodIcon = btview.findViewById(R.id.icon3);
        if (btDevice.startMethod == AppDefines.START_METHOD_WITHUI) {
            startMethodIcon.setImageResource(R.drawable.ic_fullscreen_darkgrey_24dp);
        } else {
            startMethodIcon.setImageDrawable(null);
        }
        dismissDialog();
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

    private class TwoIconTwoTextViewHolder {
        ImageView icon1;
        TextView title;
        TextView subtitle;
        ImageView icon2;
        ImageView icon3;
    }

}
