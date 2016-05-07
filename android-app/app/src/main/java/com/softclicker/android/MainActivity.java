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
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickAnswer;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.transport.handler.MessageHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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
    private DatagramSocket socket;
    private MessageHandler messageHandler;
    private Handler mHandler;

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        statusLabel = (TextView) findViewById(R.id.class_status_label);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
        mHandler = new Handler();

        final RadioGroup answerGroup = (RadioGroup) findViewById(R.id.answerGroup);
        Button btnSubmit = (Button) findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedStudent = sharedPref.getString(Constants.SELECTED_STUDENT, null);
                if (selectedStudent == null) {
                    selectStudent();
                    return;
                }

                RadioButton checkedAnswer = (RadioButton) findViewById(answerGroup.getCheckedRadioButtonId());
                if (checkedAnswer == null) {
                    Toast.makeText(MainActivity.this, "Please select an answer first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String option = (String) checkedAnswer.getTag();
                SoftClickAnswer.AnswerOption answerOption = SoftClickAnswer.AnswerOption.OPTION_1;
                switch (option) {
                    case "OPTION_1":
                        answerOption = SoftClickAnswer.AnswerOption.OPTION_1;
                        break;
                    case "OPTION_2":
                        answerOption = SoftClickAnswer.AnswerOption.OPTION_2;
                        break;
                    case "OPTION_3":
                        answerOption = SoftClickAnswer.AnswerOption.OPTION_3;
                        break;
                    case "OPTION_4":
                        answerOption = SoftClickAnswer.AnswerOption.OPTION_4;
                        break;
                    case "OPTION_5":
                        answerOption = SoftClickAnswer.AnswerOption.OPTION_5;
                        break;
                }

                final SoftClickAnswer softClickAnswer = new SoftClickAnswer();
                softClickAnswer.setAnswerOption(answerOption);
                softClickAnswer.setStudentId(selectedStudent);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String serverIP = sharedPref.getString(Constants.SERVER_IP, "");
                            int port = sharedPref.getInt(Constants.SERVER_PORT, 0);
                            Socket clientSocket = new Socket(serverIP, port);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.write(messageHandler.encodeAnswer(softClickAnswer));
                            int response = clientSocket.getInputStream().read();
                            if (response == 0x06) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Answer saved", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else if (response == 0x15) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Answer rejected", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Unknown error", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            clientSocket.close();
                        } catch (IOException e) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "Unable to submit answer", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

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

        messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(), new SoftClickAnswerDAOImpl());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String selectedStudent = sharedPref.getString(Constants.SELECTED_STUDENT, null);
        TextView txtStudentId = (TextView) findViewById(R.id.student_id_label);
        if (txtStudentId != null) {
            if (selectedStudent != null) {
                txtStudentId.setText(selectedStudent);
            } else {
                txtStudentId.setText("Please add student first");
            }
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (!wifiManager.isWifiEnabled()) {
            progressDialog = ProgressDialog.show(this, "Connecting",
                    "Searching for WiFi Access Points.", true);
            wifiManager.setWifiEnabled(true);
            Toast.makeText(getBaseContext(), "WiFi Enabled!", Toast.LENGTH_LONG).show();
        } else if (wifiInfo != null
                && sharedPref.getString(Constants.SSID, "").equals(wifiInfo.getSSID().replace("\"", ""))) {
            if (!sharedPref.contains(Constants.SERVER_IP)) {
                listenToServerBroadcast();
            } else if (selectedStudent == null) {
                selectStudent();
            }
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
                selectStudent();
            default:
                return false;
        }
    }

    private void selectStudent() {
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
                    selectStudent();
                } else {
                    String selectedStudent = sharedPref.getString(Constants.SELECTED_STUDENT, null);
                    TextView studentId = (TextView) findViewById(R.id.student_id_label);
                    if (selectedStudent != null && studentId != null) {
                        studentId.setText("Welcome " + selectedStudent);
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
        final WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock(Constants.BORADCAST);
        if (!multicastLock.isHeld()) {
            multicastLock.acquire();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(this, "Connecting",
                "Searching for Soft Clicker server.", true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] recvBuf = new byte[15000];
                    if (socket == null || socket.isClosed()) {
                        socket = new DatagramSocket(Constants.BORADCAST_PORT, getBroadcastAddress());
                        socket.setBroadcast(true);
                    }
                    socket.setSoTimeout(10000);
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    Log.i("UDP", "Waiting for UDP broadcast");
                    socket.receive(packet);

                    String senderIP = packet.getAddress().getHostAddress();
                    String message = new String(packet.getData()).trim();
                    Log.i("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);

                    SoftClickBroadcast softClickBroadcast = messageHandler.decodeBroadcast(packet.getData());
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString(Constants.SERVER_IP, softClickBroadcast.getServerIP().getHostAddress());
                    prefEditor.putString(Constants.SERVER_NAME, softClickBroadcast.getServerName());
                    prefEditor.putInt(Constants.SERVER_PORT, softClickBroadcast.getPort());
                    prefEditor.apply();

                    socket.close();
                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Unable to detect soft-clicker server", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                } finally {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    isAssociating = false;
                    multicastLock.release();
                }
            }
        }).start();
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return InetAddress.getByAddress(quads);
    }

}
