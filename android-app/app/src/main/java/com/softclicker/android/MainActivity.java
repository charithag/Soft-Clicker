package com.softclicker.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
import com.softclicker.android.student.StudentAdapter;

import org.json.JSONArray;
import org.json.JSONException;

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
        obtainPermissionsToWiFiScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWiFiScan();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (!apSelectDialog.isShowing()) {
                apSelectDialog.show();
            }
        }
    }

    private void obtainPermissionsToWiFiScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            startWiFiScan();
        }
    }

    private void startWiFiScan() {
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
                obtainPermissionsToWiFiScan();
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
        View promptsView = studentListLayoutInflater.inflate(R.layout.select_student, null);
        AlertDialog.Builder studentSelectBuilder = new AlertDialog.Builder(MainActivity.this);
        studentSelectBuilder.setView(promptsView);
        final AlertDialog studentSelectDialog = studentSelectBuilder.create();

        studentSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String studentIds = sharedPref.getString(Constants.STUDENT_IDS, "[]");
                String updatedIds = sharedPref.getString(Constants.UPDATED_STUDENT_IDS, "[]");

                if (!studentIds.equals(updatedIds)) {
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString(Constants.STUDENT_IDS, updatedIds);
                    prefEditor.apply();
                    selectUser();
                } else {
                    String selectedStudent = sharedPref.getString(Constants.SELECTED_STUDENT, null);
                    TextView studentId = (TextView) findViewById(R.id.student_id_label);
                    if (selectedStudent != null && studentId != null) {
                        studentId.setText(selectedStudent);
                        Toast.makeText(MainActivity.this, selectedStudent + " is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "You need to select a student first", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final ArrayList<String> students = new ArrayList<>();
        JSONArray studentsJsonArray = new JSONArray();
        try {
            studentsJsonArray = new JSONArray(sharedPref.getString(Constants.STUDENT_IDS, "[]"));
            for (int i = 0; i < studentsJsonArray.length(); i++) {
                students.add(studentsJsonArray.getString(i));
            }
        } catch (JSONException ignored) {
        }
        ListView studentListView = (ListView) promptsView.findViewById(R.id.student_list);
        StudentAdapter studentsAdaptor = new StudentAdapter(getApplicationContext(), studentSelectDialog, students);
        studentListView.setAdapter(studentsAdaptor);

        // load current student ids
        studentSelectDialog.show();

        Button btnAddNewStudent = (Button) promptsView.findViewById(R.id.button_add_new_student);
        final JSONArray finalStudentsJsonArray = studentsJsonArray;
        btnAddNewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater studentListLayoutInflater = LayoutInflater.from(MainActivity.this);
                @SuppressLint("InflateParams")
                final View promptsView = studentListLayoutInflater.inflate(R.layout.add_student, null);

                AlertDialog.Builder studentAddBuilder = new AlertDialog.Builder(MainActivity.this);
                studentAddBuilder.setView(promptsView);
                final AlertDialog studentAddDialog = studentAddBuilder.create();

                studentAddDialog.show();

                Button btnAddStudent = (Button) promptsView.findViewById(R.id.button_add_student);
                btnAddStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText txtStudentId = (EditText) promptsView.findViewById(R.id.student_name);
                        if (txtStudentId != null) {
                            finalStudentsJsonArray.put(txtStudentId.getText().toString());
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putString(Constants.UPDATED_STUDENT_IDS, finalStudentsJsonArray.toString());
                            prefEditor.apply();
                            studentAddDialog.dismiss();
                            studentSelectDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Please add a student Id", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
