package com.example.miniprj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private TextView frequencyTextView;
    private MediaPlayer mediaPlayer;
    private boolean isBeepPlaying = false;
    private LineChart lineChart;
    private LineDataSet dataSet;
    private LineData lineData;
    private List<Entry> entries;
    private boolean isProcessStarted = false;
    private static final String CHANNEL_ID = "MagneticFieldChannel";
    private boolean isVibrationEnabled = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ImageView img1;
    public boolean vib=false;
    MyDatabaseHelper myDB;
    int count=0,flag=0;
    List<String> user_number=new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Detection");
        setContentView(R.layout.activity_main);

        frequencyTextView = findViewById(R.id.textView);
        img1=findViewById(R.id.left_icon1);

        lineChart = findViewById(R.id.chart);
        lineChart.setTouchEnabled(true);

        // Initialize the data set and entries list
        dataSet = new LineDataSet(new ArrayList<Entry>(), "Magnetic Field Strength");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        entries = new ArrayList<>();

        // Configure the LineChart
        lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        if (magnetometer == null) {
            // Handle the case where the device doesn't have a magnetometer
        }

        frequencyTextView.setText("Frequency: --");
        frequencyTextView.setTypeface(null, Typeface.BOLD);
        MaterialButton materialButton = findViewById(R.id.materialButton);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProcessStarted) {
                    materialButton.setText("START");
                    stopProcess();
                } else {
                    materialButton.setText("STOP");
                    startProcess();
                }
            }
        });

        ImageView toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVibrationEnabled = !isVibrationEnabled;
                if (isVibrationEnabled) {
                    toggleButton.setImageResource(R.drawable.img);
                    isVibrationEnabled=true;
                } else {
                    toggleButton.setImageResource(R.drawable.img_1);
                    isVibrationEnabled=false;
                    //vib=false;
                }
            }
        });
    }

    private void startProcess() {
        isProcessStarted = true;
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopProcess() {
        isProcessStarted = false;
        sensorManager.unregisterListener(this);
        stopBeepSound();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magnetometer) {
            float magneticFieldStrength = (float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            frequencyTextView.setText("Frequency: " + magneticFieldStrength + "µT");
            frequencyTextView.setTypeface(null, Typeface.BOLD);

            if (magneticFieldStrength > 50) {
                // Object detected within 10 cm range


                if (!isBeepPlaying) {
                    playBeepSound();
                    count++;
                    isBeepPlaying = true;
                    if (isVibrationEnabled) {
                        vibrate();
                    }
                    if(count==1||count==2){
                        showNotification();
                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
                            sendMessage();
                        }
                        else{
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},100);
                        }
                    }

                }
            } else {
                // No object detected within the specified range
                if (isBeepPlaying) {
                    stopBeepSound();
                    count=0;
                    isBeepPlaying = false;
                }
            }

            // Add new data to the live graph
            entries.add(new Entry(entries.size(), magneticFieldStrength));
            dataSet.setValues(entries);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            sendMessage();
        }
    }

    private void sendMessage() {
        String phone;
        String message="Object Detected";
        try{
            myDB = new MyDatabaseHelper(MainActivity.this);
            Cursor cursor = myDB.readAllData();
            if(cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    user_number.add(cursor.getString(2));
                }
            }
        }catch(Exception e)
        {
            Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
        for(int i=0;i<user_number.size();i++) {
            phone = user_number.get(i);
            if (!phone.equals("") && !message.equals("")) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
            }
        }
    }

    private void vibrate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Vibrator vibrator;
                if (isVibrationEnabled) {
                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(50);
                        }
                    }
                    if (isProcessStarted && isBeepPlaying) {
                        handler.postDelayed(this, 50);
                    }
                }
            }
        };
        handler.post(runnable);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    private void stopBeepSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playBeepSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void showNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Object Detected")
                .setContentText("An object has been detected within range.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}