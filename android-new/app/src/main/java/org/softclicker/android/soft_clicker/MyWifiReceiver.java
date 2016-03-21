package org.softclicker.android.soft_clicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MyWifiReceiver extends BroadcastReceiver {
    final MainActivity activity;
    public MyWifiReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            activity.setData();
        }
    }
}
