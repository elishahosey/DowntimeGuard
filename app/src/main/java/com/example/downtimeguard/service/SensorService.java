package com.example.downtimeguard.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.*;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float z = event.values[2];
        if (Math.abs(z) > 9) {
            AppBlocker.blockApps(getApplicationContext());
        }
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
