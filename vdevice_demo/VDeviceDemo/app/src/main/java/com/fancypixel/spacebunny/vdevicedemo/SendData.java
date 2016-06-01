package com.fancypixel.spacebunny.vdevicedemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.spacebunny.SpaceBunny;
import io.spacebunny.vdevicedemo.R;

public class SendData extends AppCompatActivity implements SensorEventListener {

    private String code = "";
    private SpaceBunny.Client spaceBunny = null;
    private Handler handler;
    private SensorManager mSensorManager;
    private boolean trim = true;
    private ArrayList<String> sensor_data_list = new ArrayList<>();
    private long timestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        code = getIntent().getStringExtra(Constants.CODE_EXTRA_INTENT);
        if (code.equals(""))
            finish();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        handler = new Handler();

        findViewById(R.id.action_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Thread trimThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        trim = !trim;
                        if (trim)
                            Thread.sleep(500, 0);
                        else
                            Thread.sleep(100, 0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        trimThread.setDaemon(true);
        trimThread.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    spaceBunny = new SpaceBunny.Client(Constants.DEVICE_KEY);
                    spaceBunny.connect(new SpaceBunny.OnConnectedListener() {
                        @Override
                        public void onConnected() throws SpaceBunny.ConnectionException {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showUI();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    try {
                        spaceBunny.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        Toast.makeText(SendData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }
        }.start();

    }

    public void showUI() {
        ((RippleBackground) findViewById(R.id.ripple_background)).startRippleAnimation();

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
        try {
            spaceBunny.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (!trim) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        JSONObject data = new JSONObject();
                        data.put(Constants.JSON_KEY_ID, code);
                        data.put(Constants.JSON_KEY_TIME, Calendar.getInstance().getTimeInMillis());
                        data.put(Constants.JSON_KEY_DEVICE_NAME, Build.MODEL);
                        data.put(Constants.JSON_KEY_DEVICE_TYPE, Build.DEVICE);

                        JSONObject sensor_data = new JSONObject();
                        sensor_data.put(Constants.JSON_KEY_SENSOR_NAME, event.sensor.getName());
                        sensor_data.put(Constants.JSON_KEY_SENSOR_VALUE_X, event.values[0]);
                        sensor_data.put(Constants.JSON_KEY_SENSOR_VALUE_Y, event.values[1]);
                        sensor_data.put(Constants.JSON_KEY_SENSOR_VALUE_Z, event.values[2]);

                        data.put(Constants.JSON_KEY_SENSOR, sensor_data);

                        spaceBunny.publish("device_data", data.toString(), null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onBackPressed();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
