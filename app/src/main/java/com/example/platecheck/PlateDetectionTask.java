package com.example.platecheck;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

class PlateDetectionTask extends AsyncTask<Object, Void, String> {
    private static final String TAG = MainActivity.class.getSimpleName();

    protected final WeakReference<MainActivity> mActivityWeakReference;
    public Vision.Images.Annotate mRequest;

    PlateDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
        mActivityWeakReference = new WeakReference<>(activity);
        mRequest = annotate;

    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d(TAG, "created Cloud Vision request object, sending request");
            BatchAnnotateImagesResponse response = mRequest.execute();
            return Utility.convertResponseToString(response);

        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return "Cloud Vision API request failed. Check logs for details.";
    }

    protected void onPostExecute(String result) {
        MainActivity activity = mActivityWeakReference.get();
        if (activity != null && !activity.isFinishing()) {
            activity.setPlateNumber(result);
            activity.switchToRecordActivity();
        }
    }
}