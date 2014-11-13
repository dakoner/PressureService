package com.google.android.apps.pressureservice;

import android.app.NotificationManager;
import android.os.AsyncTask;

class UploadAsyncTask extends AsyncTask<LocalService, Integer, Long> {
    LocalService mLS;

    // these Strings / or String are / is the parameters of the task, that can be handed over via the excecute(params) method of AsyncTask
    protected Long doInBackground(LocalService... ls) {

        mLS = ls[0];
        try {
            Thread.sleep(5000);                 //1000 milliseconds is one second.

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return 42L;
    }


    // the onPostexecute method receives the return type of doInBackGround()
    protected void onPostExecute(Long result) {
        mLS.showNotification();
        // do something with the result, for example display the received Data in a ListView
        // in this case, "result" would contain the "someLong" variable returned by doInBackground();


    }
}
