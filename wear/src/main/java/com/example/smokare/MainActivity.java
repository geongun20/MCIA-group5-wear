package com.example.smokare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    public static final String TAG = "TestFileActivity";



    private TextView mTextView;
    private static final String TAG2 = "MainActivity";
    private DataClient dataClient;
    private int count = 0;
    private Handler handler;
    private Runnable mRunnable;

    int dragvalue = 1;
    int dragvalue2 = 1;
    int dragcount = 0;
    int dragcount2 = 0;
    int numdrags = 0;
    int numcig = 0;
    Float azimut;
    Float pitch;

    File file;
    File file_cig;

    String outstr="";
    String outstr_cig="";
    Scanner sc;


    private SensorManager mSensorManager;
    Sensor accelerometer;
    Float roll;
    Sensor magnetometer;

    TextView t1;



    private boolean writeFile(File file , byte[] file_content){
        boolean result;
        FileOutputStream fos;
        if(file!=null&&file.exists()&&file_content!=null){
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    private static String readAllBytesJava7(String filePath)
    {
        String content = "";

        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return content;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Enables Always-on
        setAmbientEnabled();



        t1 = findViewById(R.id.text);//shoudbemodified

        File dir = getExternalFilesDir(null);

        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(getExternalFilesDir(null),"output.txt");
            if(file!=null&&!file.exists()){
                Log.i( TAG2 , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG2, "파일생성 여부 = " + isSuccess);
                }
            }else{
                Log.i( TAG2 , "file.exists" );
            }
        }
        boolean isSuccess_cig = false;
        if(dir.isDirectory()){
            file_cig = new File(getExternalFilesDir(null),"cig.txt");
            if(file_cig!=null&&!file_cig.exists()){
                Log.i( TAG2 , "!file_cig.exists" );
                try {
                    isSuccess = file_cig.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG2, "파일생성 여부 = " + isSuccess_cig);
                }
            }else{
                Log.i( TAG2 , "file_cig.exists" );
            }
        }

        outstr_cig = readAllBytesJava7( getExternalFilesDir(null)+"cig.txt" );





        handler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (dragvalue == 1) {
                    dragcount++;

                } else {
                    if (dragcount>20&&dragcount<100) {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        outstr=(sdf.format(timestamp)+"\n")+outstr;
                        writeFile(file,outstr.getBytes());

                        try {
                            sc = new Scanner(file);
                            int count = 1;
                            String line=sc.nextLine();
                            int hour= Integer.parseInt(line.substring(11,13));
                            int minute = Integer.parseInt(line.substring(14,16))+hour*60;
                            int second = Integer.parseInt(line.substring(17,19))+minute*60;
                            for (;sc.hasNextLine();) {
                                line = sc.nextLine();
                                System.out.println(line+"\n");
                                if(line.equals("asd")){
                                    System.out.println("debug");
                                    break;
                                }
                                int hour2= Integer.parseInt(line.substring(11,13));
                                int minute2 = Integer.parseInt(line.substring(14,16))+hour2*60;
                                int second2 = Integer.parseInt(line.substring(17,19))+minute2*60;
                                System.out.println("second "+second+" Second2 "+second2+"count "+count);
                                if(second<second2+240)count++;

                                else break;
                            }
                            if(count>=8){
                                putDataToPhone();
                                numcig++;
                                outstr=("asd\n")+outstr;
                                writeFile(file,outstr.getBytes());
                            }
                        }catch(FileNotFoundException e){

                        }

                    }
                    dragcount = 0;
                }
                //  lightercount++;
                //System.out.println("dragcount:"+dragcount+" dragvalue:"+dragvalue);
                //System.out.println("numcig:"+numcig);


                t1.setText("dragcount:"+dragcount+"\ndragvalue:"+dragvalue+"\nnumcig:"+numcig);
                //Toast.makeText(getApplicationContext(), "dragcount:"+dragcount+" dragvalue:"+dragvalue+"numdrag:"+numdrags, Toast.LENGTH_LONG).show();

                handler.postDelayed(mRunnable, 100);// move this inside the run method
            }
        };
        mRunnable.run();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }


    public void putDataToPhone() {


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        outstr_cig=(sdf.format(timestamp)+"\n")+outstr_cig;
        writeFile(file_cig,outstr_cig.getBytes());


        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/count");


        putDataMapRequest.getDataMap().putString("count", outstr_cig);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        request.setUrgent();

        //Task<DataItem> dataItemTask = dataClient.putDataItem(request);
        Task<DataItem> dataItemTask =
                Wearable.getDataClient(getApplicationContext()).putDataItem(request);

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

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] mGravity;
    float[] mGeomagnetic;
    int i=0;

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                pitch = orientation[1];
                roll = orientation[2];

                if ((pitch <= (-0.2) && pitch >= (-1.2)) && (roll >= 0.3 && roll <= 2.2)) {
                    dragvalue = 1;
                }else {
                    dragvalue = 0;
                }

                // Log.d("ANGLE", "\t"+pitch+"\t"+roll);
            }
        }
        // mCustomDrawableView.invalidate();

    }



}
