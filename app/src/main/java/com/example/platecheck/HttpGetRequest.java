package com.example.platecheck;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class HttpGetRequest extends AsyncTask<Void, Void, String> {
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private MainActivity activity;
    private static String SERVER = "http://10.0.2.2:5000/rooms/save";
    public HttpGetRequest(MainActivity activity) {
        this.activity = activity;
    }


    @Override
    protected String doInBackground(Void... params) {
        String result;
        String inputLine;

        try {
            // connect to the server
            URL myUrl = new URL(SERVER);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            // get the string from the input stream
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
            connection.disconnect();

        } catch(IOException e) {
            e.printStackTrace();
            result = "[]";
        }
        return result;
    }

    protected void onPostExecute(String result){
        activity.updateFile(result);
    }
}
