package com.example.smokare;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private static final String TAG = "MainActivity";
    private DataClient dataClient;
    private int count = 0;
    private Handler handler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
        handler = new Handler();
        mRunnable = new Runnable(){
            @Override
            public void run(){
                increaseCounter();
                handler.postDelayed(mRunnable,1000);
            }
        };

        mRunnable.run();
    }


    public void increaseCounter() {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/count");
        putDataMapRequest.getDataMap().putInt("count", count++);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();

        //Task<DataItem> dataItemTask = dataClient.putDataItem(request);
        Task<DataItem> dataItemTask =
                Wearable.getDataClient(getApplicationContext()).putDataItem(request);
        mTextView.setText("count :"+ putDataMapRequest.getDataMap().getInt("count"));

//        try {
//            // Block on a task and get the result synchronously (because this is on a background
//            // thread).
//            DataItem dataItem = Tasks.await(dataItemTask);
//
//        } catch (ExecutionException exception) {
//            Log.e(TAG, "Task failed: " + exception);
//
//        } catch (InterruptedException exception) {
//            Log.e(TAG, "Interrupt occurred: " + exception);
//        }
    }
}
