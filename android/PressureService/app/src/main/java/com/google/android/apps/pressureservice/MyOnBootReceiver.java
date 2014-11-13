package com.google.android.apps.pressureservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        Intent intent2 = new Intent(context, LocalService.class);
        context.startService(intent2);
    }
}