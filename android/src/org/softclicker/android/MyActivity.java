package org.softclicker.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /** Load student information from the localstorage */
        Spinner studentSelector = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.student_list, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        studentSelector.setAdapter(adapter);

        /** Load APNs */
        WifiManager mainWifiObj;
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiScanReceiver wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

        ListView apnList = (ListView) findViewById(R.id.apnListView);
        ArrayAdapter<ScanResult> apnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiScanList);
        apnList.setAdapter(apnAdapter);
    }

    public void navigateToListner(View view) {
        Intent i = new Intent(this, ListnerActivity.class);
        startActivity(i);
    }
}

class WifiScanReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
    }
}
