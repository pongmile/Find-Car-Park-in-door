package com.pongmile.mycarpark;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.*;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WiFiDemo";
    private Handler handler = new Handler();

    WifiManager wifiManager;
    WifiBroadcastReceiver wifiReceiver;

    TextView textView;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "Requesting permissions");

            //Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    123);
        } else
            Log.d(TAG, "Permissions already granted");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        textView = findViewById(R.id.rssi_wifi);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        //Instantiate broadcast receiver
        textView.setText("");
        wifiReceiver = new WifiBroadcastReceiver();

        handler.postDelayed(runnable,1000);

        //Register the receiver
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, "permission granted: " + permissions[0]);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permission denied: " + permissions[0]);
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Define class to listen to broadcasts


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        unregisterReceiver(wifiReceiver);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        //Toast.makeText(getApplicationContext(), "All Network seached !!",0).show();

        if (view.getId() == R.id.btn) {
            Log.d(TAG, "onCreate() wifi.startScan()");

            //if (!wifiManager.isWifiEnabled())
            //    wifiManager.setWifiEnabled(true);

            wifiManager.startScan();
        }
    }

    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            wifiManager.startScan();
            handler.postDelayed(this, 1000);
        }
    };

}
