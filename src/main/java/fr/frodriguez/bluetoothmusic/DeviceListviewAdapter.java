package fr.frodriguez.bluetoothmusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Florian on 11/10/2017.
 */
@SuppressWarnings("WeakerAccess")
public class DeviceListviewAdapter extends ArrayAdapter<BTDevice> {

    public DeviceListviewAdapter(Context context, List<BTDevice> BTDevices) {
        super(context, 0, BTDevices);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_device, parent, false);
        }

        DeviceViewHolder viewHolder = (DeviceViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new DeviceViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.mac = (TextView) convertView.findViewById(R.id.mac);
            convertView.setTag(viewHolder);
        }

        // Get the BTDevice from the list
        final BTDevice btDevice = getItem(position);
        if(btDevice != null) {
            // Populate the row
            viewHolder.name.setText(btDevice.name);
            viewHolder.mac.setText(btDevice.mac);
            viewHolder.icon.setImageResource(btDevice.icon);
            setBackgroundColor(convertView, btDevice.watched);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btDevice.watched = !btDevice.watched;

                    saveWatchedState(btDevice);

                    setBackgroundColor(v, btDevice.watched);
                    if(btDevice.watched) {
                        Toast.makeText(getContext(), "Watching for " + btDevice.name + " connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return convertView;
    }

    private void saveWatchedState(BTDevice btDevice) {
        SharedPreferences sp = getContext().getSharedPreferences("sharedpref", Context.MODE_PRIVATE); //TODO static value
        SharedPreferences.Editor spe = sp.edit();
        // Save new device
        spe.putString(btDevice.mac, btDevice.watched ? btDevice.name : null); //null == spe.remove(key)
        spe.apply();
    }


    private void setBackgroundColor(View view, boolean selected) {
        if(selected) {
            view.setBackgroundColor(Color.rgb(200, 255, 200)); //TODO static value
        } else {
            view.setBackgroundColor(Color.rgb(252, 252, 252)); //TODO static value
        }
    }


    private class DeviceViewHolder {
        ImageView icon;
        TextView name;
        TextView mac;
    }
}
