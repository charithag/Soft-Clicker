package org.softclicker.android.soft_clicker.apn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.softclicker.android.soft_clicker.R;

import java.util.ArrayList;

/**
 * Created by User on 3/19/2016.
 */
public class ApnAdapter extends ArrayAdapter<ApnData> {
    public ApnAdapter(Context context, ArrayList<ApnData> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ApnData data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_apn, parent, false);
        }
        // Lookup view for data population
        TextView ssid = (TextView) convertView.findViewById(R.id.ssid);
        TextView bssid = (TextView) convertView.findViewById(R.id.bssid);
        // Populate the data into the template view using the data object
        ssid.setText(data.SSID);
        bssid.setText(data.BSSID);
        // Return the completed view to render on screen
        return convertView;
    }
}
