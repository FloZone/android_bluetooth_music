package fr.frodriguez.bluetoothmusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * By FloZone on 04/11/2017.
 *
 * The listview containing music players.
 */

@SuppressWarnings("WeakerAccess")
public class PlayerListviewAdapter extends ArrayAdapter<Player> {

    public PlayerListviewAdapter(Context context, List<Player> players) {
        super(context, 0, players);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_icon_two_text, parent, false);
        }

        SecondIconTwoTextViewHolder viewHolder = (SecondIconTwoTextViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SecondIconTwoTextViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(viewHolder);
        }

        // Get the Player from the list
        final Player player = getItem(position);
        if (player != null) {
            // Populate the row
            viewHolder.title.setText(player.name);
            viewHolder.subtitle.setText(player.packageName);
            viewHolder.icon.setImageDrawable(player.icon);
        }

        return convertView;
    }

    private class SecondIconTwoTextViewHolder {
        ImageView icon;
        TextView title;
        TextView subtitle;
    }

}
