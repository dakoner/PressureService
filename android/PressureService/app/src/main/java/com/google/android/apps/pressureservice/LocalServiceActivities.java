package com.google.android.apps.pressureservice;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocalServiceActivities {
    /**
     * <p>Example of explicitly starting and stopping the local service.
     * This demonstrates the implementation of a service that runs in the same
     * process as the rest of the application, which is explicitly started and stopped
     * as desired.</p>
     *
     * <p>Note that this is implemented as an inner class only keep the sample
     * all together; typically this code would appear in some separate class.
     */
    public static class Controller extends Activity {
        // TODO(dek): use Calendar to measure 5-minute intervals to upload
        private boolean mDone = false;
        private PressureSensorEventListener mPSEL;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.local_service_controller);

            mPSEL = new PressureSensorEventListener(this);
        }

        public void UpdatePressure(float pressure) {
            TextView view = (TextView)findViewById(R.id.pressure);
            String pressureString = Float.toString(pressure);
            view.setText(pressureString);
            if (!mDone) {
                //Start MyIntentService
                Intent intent = new Intent(this, UploadIntentService.class);
                intent.putExtra(UploadIntentService.EXTRA_KEY_IN, pressureString);
                startService(intent);
                mDone = true;
            }
        }
    }
}
