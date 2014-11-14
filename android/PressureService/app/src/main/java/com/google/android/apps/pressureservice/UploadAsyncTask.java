package com.google.android.apps.pressureservice;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class UploadAsyncTask extends AsyncTask<LocalService, Integer, Long> {
    LocalService mLS;

    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }

    // these Strings / or String are / is the parameters of the task, that can be handed over via the excecute(params) method of AsyncTask
    protected Long doInBackground(LocalService... ls) {

        mLS = ls[0];
        Context ctx = mLS.getApplicationContext();
        PressureSensorEventListener psel = new PressureSensorEventListener(ctx);
        float reading = 0;
        while (reading == 0)
            reading = psel.getSensorReading();

        HttpClient httpClient = createHttpClient();

        String url = "https://goosci-outreach.appspot.com/weather/314159";
        HttpPost post = new HttpPost(url);
        StringEntity params;
        HttpResponse response;
        long response_code=-1;
        StringBuffer result;
        try {
            params = new StringEntity("{\"pressure\":" + reading + ",\"collected_at\":\"Thu, 13 Oct 2014 12:12:12 -0000\",\"us_units\":0}");
            Log.i("UploadAsyncTask", "Request content: " + params.getContent().read());
            post.addHeader("content-type", "application/json");
            post.setEntity(params);


            response = httpClient.execute(post);
            {
                response_code = response.getStatusLine().getStatusCode();

                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                Log.i("UploadAsyncTask", "Error: " + response_code);
                Log.i("UploadAsyncTask", "Error: " + result.toString());
            }
        }        catch (Exception ex) {
            Log.i("UploadAsyncTask", "blah");
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }


        return response_code;
    }


    // the onPostexecute method receives the return type of doInBackGround()
    protected void onPostExecute(Long result) {
        mLS.showNotification(result);
        mLS.reschedule();
        mLS.stopSelf();
    }
}
