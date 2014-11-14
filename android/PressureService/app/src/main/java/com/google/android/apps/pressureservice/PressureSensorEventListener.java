package com.google.android.apps.pressureservice;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class PressureSensorEventListener implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mPressure;
    private LocalServiceActivities.Controller mC;

    PressureSensorEventListener(LocalServiceActivities.Controller c) {
        mC = c;
        mSensorManager = (SensorManager) mC.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorManager.registerListener(this,
                mPressure,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        mC.UpdatePressure(event.values[0]);
    }


}