package org.softclicker.android.soft_clicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by User on 3/18/2016.
 */
public class ListnerActivity extends AppCompatActivity {
    public static final String SSID = "ssid";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listner);
        Bundle extras = getIntent().getExtras();
        String selectedSSID = extras.getString(SSID);
        TextView statusLabel = (TextView) findViewById(R.id.class_status_label);
        statusLabel.setText("Your are connected to " + selectedSSID);
    }
}