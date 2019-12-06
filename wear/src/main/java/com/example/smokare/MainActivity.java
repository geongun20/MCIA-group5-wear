package com.example.smokare;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import android.app.Service;


public class MainActivity extends WearableActivity {

    TextView t1;
    private WearService mService;
    int count;
    int dragcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Enables Always-on
        setAmbientEnabled();
        t1 = findViewById(R.id.text);
        Intent serviceIntent = new Intent(this, WearService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        ContextCompat.startForegroundService(this, serviceIntent);

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            WearService.WearServiceBinder binder = (WearService.WearServiceBinder) service;
            mService = binder.getService();

            count = mService.getCount();
            dragcount = mService.dragcount;

            t1.setText("dragcount:"+dragcount+" count: "+count);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


}
