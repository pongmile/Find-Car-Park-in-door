package com.pongmile.mycarpark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by pongmile on 28-Aug-18.
 */

public class WifiBroadcastReceiver extends MainActivity {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive()");
            Toast.makeText(getApplicationContext(), "Scan complete!", Toast.LENGTH_SHORT).show();

            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok) {
                Log.d(TAG, "scan OK");

                //StringBuffer buffer = new StringBuffer();
                List<ScanResult> list = wifiManager.getScanResults();

                Toast.makeText(getApplicationContext(), Integer.toString(list.size()), Toast.LENGTH_SHORT).show();

                for (ScanResult scanResult : list) {
                    //buffer.append(scanResult);
                    textView.append(scanResult.SSID.toString() + " ");
                    textView.append(String.valueOf(scanResult.level));
                    textView.append("\n");
                }
            } else
                Log.d(TAG, "scan not OK");
        }


}
