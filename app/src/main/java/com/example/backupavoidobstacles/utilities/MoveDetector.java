package com.example.backupavoidobstacles.utilities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MoveDetector implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MoveCallback moveCallback;
    private long timestamp = 0;
    private static final int MOVE_THRESHOLD = 3; // Sensitivity threshold, adjust if necessary

    public interface MoveCallback {
        void onMoveLeft();
        void onMoveRight();
    }

    public MoveDetector(Context context, MoveCallback moveCallback) {
        this.moveCallback = moveCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void register() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - timestamp) > 100) { // 100 ms interval to check
                timestamp = currentTime;

                float x = event.values[0];
                if (x > MOVE_THRESHOLD) {
                    moveCallback.onMoveLeft();
                } else if (x < -MOVE_THRESHOLD) {
                    moveCallback.onMoveRight();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // pass
    }
}
