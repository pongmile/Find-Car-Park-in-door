package com.pongmile.mycarpark;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.*;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pongmile.model.Dot;
import com.pongmile.mycarpark.Login;
import com.pongmile.view.Dotview;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Dot.OnDotChangedListener {

    private static final String TAG = "WiFiDemo";
    private Handler handler = new Handler();

    WifiManager wifiManager;
    WifiBroadcastReceiver wifiReceiver;


    public List<User> list = new ArrayList<>();
    public ArrayList<Double> wifi_cal = new ArrayList<>();
    public String wifi1 = "0";
    public String wifi2 = "0";
    public String wifi3 = "0";
    public String wifi4 = "0";
    public String ssid1 = "";
    public String ssid2 = "";
    public String ssid3 = "";
    public String ssid4 = "";
    public String show_ssid1 = "";
    public String show_ssid2 = "";
    public String show_ssid3 = "";
    public String show_ssid4 = "";
    public int valueSsid1 = 0;
    public int valueSsid2 = 0;
    public int valueSsid3 = 0;
    public int valueSsid4 = 0;
    public Double where_me = 0.0;
    public int centerX;
    public int centerY;
    public String floor_int;
    public int floor_se;
    public double cal;
    public TextView test_text;
    private String[] list_floor;
    private TypedArray images_floor;
    private ImageView imageShowScreen;
    private Dotview dotview;
    TextView show_rssi;
    TextView pin_rssi;
    TextView test_calculate;
    Button btn;
    EditText license_p;
    CountDownTimer cdt;
    TextView tvTimer;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference wifi_in_database = myRef.child("wifi");
    DatabaseReference saveWifi = myRef.child("save_wifi");
    DatabaseReference mlicense = myRef.child("license");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        test_calculate = findViewById(R.id.list);


        images_floor = getResources().obtainTypedArray(R.array.floor_image);
        list_floor = getResources().getStringArray(R.array.floor_arrays);
        imageShowScreen = findViewById(R.id.imageView);
        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> spinner_floor = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, list_floor);
        spinner_floor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_floor);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageShowScreen.setImageResource(images_floor.getResourceId(spinner.getSelectedItemPosition(), -1));
                switch (position) {
                    case 0:
                        floor_int = "floor1";
                        break;
                    case 1:
                        floor_int = "floor2";
                        break;
                    case 2:
                        floor_int = "floor3";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

        show_rssi = findViewById(R.id.rssi_wifi);
        show_rssi.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        btn = findViewById(R.id.send);
        btn.setOnClickListener(this);

        //Instantiate broadcast receiver
        show_rssi.setText("");
        wifiReceiver = new WifiBroadcastReceiver();

        handler.postDelayed(runnable, 1000);

        //Register the receiver
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        pin_rssi = findViewById(R.id.rssi_sr);
        test_text = findViewById(R.id.floor_s);
        test_calculate = findViewById(R.id.list);

        saveWifi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //select box
                for (int i = 1; i < 5; i++) {
                    String currentString = dataSnapshot.child(floor_int).child("wifi_num"+i).getValue(String.class);
                    String[] separated = currentString.split(" ");
                    if (i == 1){
                        show_ssid1 = separated[0];
                        show_ssid2 = separated[2];
                        show_ssid3 = separated[4];
                        show_ssid4 = separated[6];
                        ssid1 = show_ssid1; //Best_wifi
                        ssid2 = show_ssid2;
                        ssid3 = show_ssid3;
                        ssid4 = show_ssid4;
                        valueSsid1 = Integer.valueOf(separated[1]);
                        valueSsid2 = Integer.valueOf(separated[3]);
                        valueSsid3 = Integer.valueOf(separated[5]);
                        valueSsid4 = Integer.valueOf(separated[7]);
                        double cal_sq = (((valueSsid1 - Integer.valueOf(wifi1)) ^ 2) + ((valueSsid2 - Integer.valueOf(wifi2)) ^ 2) + ((valueSsid3 - Integer.valueOf(wifi3)) ^ 2) + ((valueSsid4 - Integer.valueOf(wifi4)) ^ 2));
                        cal = Math.sqrt(cal_sq);
                        wifi_cal.add(cal_sq);
                    }
                    else if (i == 2){
                        show_ssid1 = separated[0];
                        show_ssid2 = separated[2];
                        show_ssid3 = separated[4];
                        show_ssid4 = separated[6];
                        ssid1 = show_ssid1; //Best_wifi
                        ssid2 = show_ssid2;
                        ssid3 = show_ssid3;
                        ssid4 = show_ssid4;
                        valueSsid1 = Integer.valueOf(separated[1]);
                        valueSsid2 = Integer.valueOf(separated[3]);
                        valueSsid3 = Integer.valueOf(separated[5]);
                        valueSsid4 = Integer.valueOf(separated[7]);
                        double cal_sq = (((valueSsid1 - Integer.valueOf(wifi1)) ^ 2) + ((valueSsid2 - Integer.valueOf(wifi2)) ^ 2) + ((valueSsid3 - Integer.valueOf(wifi3)) ^ 2) + ((valueSsid4 - Integer.valueOf(wifi4)) ^ 2));
                        cal = Math.sqrt(cal_sq);
                        wifi_cal.add(cal_sq);
                    }
                    else if (i == 3){
                        show_ssid1 = separated[0];
                        show_ssid2 = separated[2];
                        show_ssid3 = separated[4];
                        show_ssid4 = separated[6];
                        ssid1 = show_ssid1; //Best_wifi
                        ssid2 = show_ssid2;
                        ssid3 = show_ssid3;
                        ssid4 = show_ssid4;
                        valueSsid1 = Integer.valueOf(separated[1]);
                        valueSsid2 = Integer.valueOf(separated[3]);
                        valueSsid3 = Integer.valueOf(separated[5]);
                        valueSsid4 = Integer.valueOf(separated[7]);
                        double cal_sq = (((valueSsid1 - Integer.valueOf(wifi1)) ^ 2) + ((valueSsid2 - Integer.valueOf(wifi2)) ^ 2) + ((valueSsid3 - Integer.valueOf(wifi3)) ^ 2) + ((valueSsid4 - Integer.valueOf(wifi4)) ^ 2));
                        cal = Math.sqrt(cal_sq);
                        wifi_cal.add(cal_sq);
                    }
                    else if (i == 4){
                        show_ssid1 = separated[0];
                        show_ssid2 = separated[2];
                        show_ssid3 = separated[4];
                        show_ssid4 = separated[6];
                        ssid1 = show_ssid1; //Best_wifi
                        ssid2 = show_ssid2;
                        ssid3 = show_ssid3;
                        ssid4 = show_ssid4;
                        valueSsid1 = Integer.valueOf(separated[1]);
                        valueSsid2 = Integer.valueOf(separated[3]);
                        valueSsid3 = Integer.valueOf(separated[5]);
                        valueSsid4 = Integer.valueOf(separated[7]);

                        double cal_sq = (((valueSsid1 - Integer.valueOf(wifi1)) ^ 2) + ((valueSsid2 - Integer.valueOf(wifi2)) ^ 2) + ((valueSsid3 - Integer.valueOf(wifi3)) ^ 2) + ((valueSsid4 - Integer.valueOf(wifi4)) ^ 2));
                        cal = Math.sqrt(cal_sq);
                        wifi_cal.add(cal_sq);

                    }
                    valueSsid1 = 0;
                    valueSsid2 = 0;
                    valueSsid3 = 0;
                    valueSsid4 = 0;
                    /*if (cal >= -30 && cal < 30) {
                        centerX = 100;
                        centerY = 200;
                    }*/
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    wifi1 = ds.child(ssid1).getValue(String.class);
                    wifi2 = ds.child(ssid2).getValue(String.class);
                    wifi3 = ds.child(ssid3).getValue(String.class);
                    wifi4 = ds.child(ssid4).getValue(String.class);
                    pin_rssi.setText(show_ssid1+ " " + valueSsid1 + "\n" + show_ssid2+ " " + valueSsid2 + "\n" + show_ssid3+ " " + valueSsid3 + "\n" + show_ssid4+ " " + valueSsid4);

                    User user = ds.getValue(User.class);
                    list.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        tvTimer = (TextView) findViewById(R.id.tvTimer);


        dotview = (Dotview) findViewById(R.id.dotView);
        //dotview.setOnTouchListener((View.OnTouchListener) this);


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
    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive()");

            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok) {
                Log.d(TAG, "scan OK");
                show_rssi.setText("");
                //StringBuffer buffer = new StringBuffer();
                List<ScanResult> list = wifiManager.getScanResults();
                Toast.makeText(getApplicationContext(), Integer.toString(list.size()), Toast.LENGTH_SHORT).show();
                for (ScanResult scanResult : list) {
                    //delete .
                    String currentString = scanResult.SSID;
                    String[] separated = currentString.split("\\.");
                    //buffer.append(scanResult);
                    show_rssi.append(separated[0] + " ");
                    show_rssi.append(scanResult.BSSID.toString() + " ");
                    show_rssi.append(String.valueOf(scanResult.level));
                    show_rssi.append("\n");
                    wifi_in_database.child(scanResult.BSSID).setValue(String.valueOf(scanResult.level));
                    // saveWifi.child(separated[0]+" "+scanResult.BSSID).setValue(separated[0]+" "+scanResult.BSSID+" "+scanResult.level);
                }
            } else
                Log.d(TAG, "scan not OK");
        }

    }

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

        if (view.getId() == R.id.send) {
            Log.d(TAG, "onCreate() wifi.startScan()");

            //if (!wifiManager.isWifiEnabled())
            //    wifiManager.setWifiEnabled(true);

            //test_calculate.setText(wifi1);

            wifiManager.startScan();
            startTimer();

            license_p = findViewById(R.id.license_plate);
            String result = license_p.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentString = user.getEmail();
            String[] separated = currentString.split("\\.");
            mlicense.child(separated[0]).setValue(result);
            double bef = -9999999;
            for (int a = 0; a < wifi_cal.size(); a++){
                double wifi_cal_sq = wifi_cal.get(a);
                if(wifi_cal_sq >= bef) {
                    where_me =  wifi_cal_sq;
                    bef = wifi_cal_sq;
                    test_calculate.setText(String.valueOf(wifi_cal_sq));
                }
            }
            for (int a = 0; a < wifi_cal.size(); a++){
                centerX = 100;
                centerY = 200;
            }
            new Dot(centerX, centerY, 30, randomColor(), this);




        }
        if (view.getId() == R.id.receive) {
            stopTimer();
            Log.d(TAG, "onCreate()");
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            wifiManager.startScan();
            handler.postDelayed(this, 1000);
        }
    };

    public void startTimer() {
        cdt = new CountDownTimer(9000, 50) {
            @Override
            public void onTick(long l) {
                tvTimer.setText("0");
                String strTime = String.format("%.1f", (double) l / 1000);
                tvTimer.setText(String.valueOf(strTime));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0");
            }
        }.start();
    }

    public void stopTimer() {
        tvTimer.setText("0");
    }

    private int randomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public void onDotChanged(Dot dot) {

        dotview.addDot(dot);
        dotview.invalidate();
    }


}
