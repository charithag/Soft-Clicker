package com.softclicker.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.softclicker.android.apn.ApnAdapter;
import com.softclicker.android.apn.ApnData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 3/18/2016.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;

    private WifiManager wifiManager;
    private SharedPreferences sharedPref;
    private WifiConfiguration wifiConfig;
    private List<ScanResult> wifiScanList;
    private String ssid = "";
    private boolean isAssociating = false;

    private ProgressDialog progressDialog;
    private TextView statusLabel;
    private ListView listView;
    private AlertDialog apSelectDialog;

    private BroadcastReceiver wifiScanBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                wifiScanList = wifiManager.getScanResults();
                String _ssid = sharedPref.getString(Constants.SSID, "");
                for (ScanResult sr : wifiScanList) {
                    if (_ssid.equals(sr.SSID)) {
                        unregisterReceiver(wifiScanBroadcastReceiver);
                        ssid = _ssid;
                        connectWithAP(ssid, sharedPref.getString(Constants.PASSWORD, null));
                        return;
                    }
                }
                if (!isAssociating) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (!apSelectDialog.isShowing()) {
                        apSelectDialog.show();
                    }
                    ArrayList<ApnData> apnArray = new ArrayList<>();
                    for (int i = 0; i < wifiScanList.size(); i++) {
                        apnArray.add(new ApnData(wifiScanList.get(i).SSID, wifiScanList.get(i).BSSID));
                    }
                    ApnAdapter apnAdapter = new ApnAdapter(getBaseContext(), apnArray);
                    listView.setAdapter(apnAdapter);
                }
            }
        }
    };

    private BroadcastReceiver wifiConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null && info.isConnected() && ssid.equals(wifiManager.getConnectionInfo().getSSID().replace("\"", ""))) {
                    unregisterReceiver(wifiConnectionReceiver);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString(Constants.SSID, ssid);
                    prefEditor.putString(Constants.PASSWORD, wifiConfig.preSharedKey);
                    prefEditor.apply();
                    apSelectDialog.dismiss();
                    listenToServerBroadcast();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        statusLabel = (TextView) findViewById(R.id.class_status_label);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);

        LayoutInflater apListLayoutInflater = LayoutInflater.from(MainActivity.this);
        @SuppressLint("InflateParams")
        View promptsView = apListLayoutInflater.inflate(R.layout.select_ap, null);
        AlertDialog.Builder apListBuilder = new AlertDialog.Builder(
                MainActivity.this);
        apListBuilder.setView(promptsView);
        apSelectDialog = apListBuilder.create();

        listView = (ListView) promptsView.findViewById(R.id.apnListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult selectedSSID = wifiScanList.get(position);

                if (!selectedSSID.capabilities.contains("WPA2") && !selectedSSID.capabilities.contains("WPA") && !selectedSSID.capabilities.contains("WEP")) {
                    connectWithAP(selectedSSID.SSID, null);
                    return;
                }

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                @SuppressLint("InflateParams")
                View promptsView = li.inflate(R.layout.apn_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.apnPwPromptText);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        connectWithAP(selectedSSID.SSID, userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // answerGroup
        RadioGroup answerGroup = (RadioGroup) findViewById(R.id.answerGroup);
        answerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedOption = ((RadioButton) findViewById(checkedId)).getText().toString();
                Toast.makeText(MainActivity.this, selectedOption, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(this, "Connecting",
                "Searching for WiFi Access Points.", true);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(getBaseContext(), "WiFi Enabled!", Toast.LENGTH_LONG).show();
        } else if (wifiInfo != null && sharedPref.getString(Constants.SSID, "").equals(wifiInfo.getSSID().replace("\"", ""))) {
            listenToServerBroadcast();
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanBroadcastReceiver, intentFilter);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionReceiver, intentFilter);
        wifiManager.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(wifiScanBroadcastReceiver);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            unregisterReceiver(wifiConnectionReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect_wifi:
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.remove(Constants.SSID);
                prefEditor.remove(Constants.PASSWORD);
                prefEditor.apply();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(wifiScanBroadcastReceiver, intentFilter);
                intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                registerReceiver(wifiConnectionReceiver, intentFilter);
                wifiManager.startScan();
                apSelectDialog.show();
                return true;
            case R.id.menu_select_user:
                selectUser();
            default:
                return false;
        }
    }

    private void selectUser() {
        LayoutInflater studentListLayoutInflater = LayoutInflater.from(MainActivity.this);
        @SuppressLint("InflateParams")
        View promptsView = studentListLayoutInflater.inflate(R.layout.select_user, null);
        AlertDialog.Builder studentSelectBuilder = new AlertDialog.Builder(MainActivity.this);
        studentSelectBuilder.setView(promptsView);
        AlertDialog studentSelectDialog = studentSelectBuilder.create();

        ListView studentListView = (ListView) promptsView.findViewById(R.id.studentListView);
        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // load current student ids


        studentSelectDialog.show();

        // add user panel
        Button buttonAddUser = (Button) promptsView.findViewById(R.id.buttonAddUser);
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater studentListLayoutInflater = LayoutInflater.from(MainActivity.this);
                @SuppressLint("InflateParams")
                View promptsView = studentListLayoutInflater.inflate(R.layout.add_user, null);

                AlertDialog.Builder studentAddBuilder = new AlertDialog.Builder(MainActivity.this);
                studentAddBuilder.setView(promptsView);
                AlertDialog studentAddDialog = studentAddBuilder.create();

                studentAddDialog.show();
            }
        });
    }

    private void connectWithAP(String ssid, String password) {
        try {
            unregisterReceiver(wifiScanBroadcastReceiver);
        } catch (IllegalArgumentException ignored) {
        }
        isAssociating = true;
        if (apSelectDialog != null && apSelectDialog.isShowing()) {
            apSelectDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(this, "Connecting",
                "Connecting to SoftClicker Access Point.", true);
        this.ssid = ssid;
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        if (password != null && !password.equals("")) {
            wifiConfig.preSharedKey = String.format("\"%s\"", password);
        }
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(netId, true);
        wifiManager.reassociate();
    }

    private void listenToServerBroadcast() {
        String ssid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        statusLabel.setText("Your are connected to " + ssid);
        progressDialog.dismiss();
        isAssociating = false;
    }
}