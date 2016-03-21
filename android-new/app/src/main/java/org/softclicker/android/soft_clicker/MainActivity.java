package org.softclicker.android.soft_clicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.softclicker.android.soft_clicker.apn.ApnAdapter;
import org.softclicker.android.soft_clicker.apn.ApnData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager mainWifiObj;
    private ArrayList<ApnData> apnArray;
    private ApnAdapter apnAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Load student information from the localstorage */
        Spinner studentSelector = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.student_list, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        studentSelector.setAdapter(adapter);

        /** Load APNs */
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (!mainWifiObj.isWifiEnabled()) {
            mainWifiObj.setWifiEnabled(true);
        }

        MyWifiReceiver broadcastReceiver = new MyWifiReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        mainWifiObj.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainWifiObj.setWifiEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh_menu:
                mainWifiObj.startScan();
                return true;
            default:
                return false;
        }
    }

    public void navigateToListner(View view) {
        Intent i = new Intent(this, ListnerActivity.class);
        startActivity(i);
    }

    public void setData() {
        WifiScanReceiver wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

        apnArray = new ArrayList<>();
        for (int i = 0; i < wifiScanList.size(); i++) {
            apnArray.add(new ApnData(wifiScanList.get(i).SSID, wifiScanList.get(i).BSSID));
        }
        apnAdapter = new ApnAdapter(this, apnArray);
        ListView listView = (ListView) findViewById(R.id.apnListView);
        listView.setAdapter(apnAdapter);
    }
}

class WifiScanReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
    }
}