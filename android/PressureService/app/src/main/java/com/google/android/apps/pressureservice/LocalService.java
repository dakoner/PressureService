package com.google.android.apps.pressureservice;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service {
    private NotificationManager mNM;
    private PressureSensorEventListener mPsel;
    final private Timer mTimer = new Timer();


    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    AlarmManager mAlarm;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        Context ctx = this.getApplicationContext();

        mPsel = new PressureSensorEventListener(ctx);
        final Intent intent = new Intent(this, LocalService.class);
        final PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        final Calendar cal = Calendar.getInstance();

        mAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+ 20 * 1000, pintent);


        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                float reading = mPsel.getSensorReading();
                // Tell the user we stopped.
                //Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
                mTimer.cancel();
                //mAlarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+ 20 * 1000, pintent);
                stopSelf();
            }
        }, 5 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to stop running 5 seconds after starting, so return sticky (so we're not immediately killed)
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.

        mNM.cancel(NOTIFICATION);
        mNM = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
// RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        Context ctx = this.getApplicationContext();
        Resources res = ctx.getResources();

        Notification notification = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.stat_sample)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.stat_sample))
                .setContentTitle("Update on local service")
                .setContentText(text)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}