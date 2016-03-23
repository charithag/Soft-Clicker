package com.softclicker.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class MyWifiReceiver extends BroadcastReceiver {
    final MainActivity activity;

    public MyWifiReceiver() {
        activity = null;
    }

    public MyWifiReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (activity != null && action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            activity.setData();
        }
    }
}
