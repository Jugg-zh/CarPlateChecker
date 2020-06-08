package com.example.platecheck;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class HttpPostRequest extends AsyncTask<Void, Void, String> {
    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private MainActivity activity;
    private static String SERVER = "http://10.0.2.2:5000/records/upload";

    public HttpPostRequest(MainActivity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        try {
            String records = activity.uploadFile();
            // convert the records into correctly formatted json format
            if (records != null && records.length() > 2) {
                records.trim();
                records  = '[' + records.substring(0, records.length()-2) + ']';
            }

            // connect to the server
            URL myUrl = new URL(SERVER);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("Content-Type","application/json");
            connection.connect();
            // write the json file to the server
            byte[] outputBytes = Optional.ofNullable(records).orElse("").getBytes("UTF-8");
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(outputBytes);
            out.flush();
            out.close();
            connection.getResponseCode();
            connection.getResponseMessage();
            connection.disconnect();
        } catch(IOException e) {
            e.printStackTrace();
            result = "error";
        }

        return result;
    }

    protected void onPostExecute(String result){
        Toast.makeText(activity.getApplicationContext(), "Upload Successfully!", Toast.LENGTH_LONG).show();
    }
}
