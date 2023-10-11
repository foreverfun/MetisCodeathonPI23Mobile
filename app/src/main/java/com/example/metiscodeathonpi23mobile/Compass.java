package com.example.metiscodeathonpi23mobile;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetometer;

    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private boolean accelerometerSet = false;
    private boolean magnetometerSet = false;

    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private final String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};

    public float azimuthRads;
    public float azimuth;
    public String direction;

    public Compass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            accelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            magnetometerSet = true;
        }

        if (accelerometerSet && magnetometerSet) {
            // given the accelerometer and magnetometer vectors, calculate the rotationMatrix
            // that transforms from device space to world space
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);

            // get the orientation of the device in world space
            SensorManager.getOrientation(rotationMatrix, orientation);

            // orientation[0] is the azimuth in radians, which is the rotation around the Z (up) axis
            // measuring the angle between magnetic north Y-axis - thus, 0 is north. This calculation
            // converts from radians to degrees and normalizes it from 0-360
            azimuthRads = orientation[0];
            azimuth = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            direction = directions[Math.round(azimuth / 45) % 8];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
