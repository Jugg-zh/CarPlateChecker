package com.example.platecheck;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String FILE_NAME = "temp.jpg";
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int PLATE_NUMBER_REQUEST = 4;
    private static final Button[] buttons = new Button[10];
    private RecordManager recordManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordManager = new RecordManager(Utility.getJsonString(this));
        setUpButtons(buttons);
        setSlots(6);
        //the button for confirming the floor number and pole number
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String floorNumber = "2A";
                String poleNumber = "1";
                // map to number of slots
                setSlots(recordManager.getNumberOfSlots(floorNumber,poleNumber));
            }
        });
    }

    public void setUpButtons(Button[] buttons) {
        for (int i = 0; i < 10; i++) {
            String buttonID = "button" + (i+1);
            int resID = getResources().getIdentifier(buttonID, "id",
                    "com.example.platecheck");
            buttons[i] = (Button) findViewById(resID);
            buttons[i].setOnClickListener(this);
            buttons[i].setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }

    public void setSlots(int numberOfSlots) {
        for (int i = 0; i < buttons.length; i++) {
            if (i < numberOfSlots) {
                buttons[i].setVisibility(View.VISIBLE);
                buttons[i].setClickable(true);
                buttons[i].setBackgroundColor(getResources().getColor(R.color.gray));
            }
            else {
                buttons[i].setVisibility(View.GONE);
            }
        }
    }

    public void setPlateNumber(String plate) {
        recordManager.setPlateNumber(plate);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < buttons.length; i++) {
            if (v.getId() == buttons[i].getId()) {
                buttons[i].setBackgroundColor(getResources().getColor(R.color.green));
                buttons[i].setClickable(false);
                recordManager.setSlotNumber("2A", "1", i+1);
            }
        }
        //can switch to use camera
        startGalleryChooser();

    }

    public void switchToRecordActivity() {
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        intent.putExtra("CAR_PLATE",  recordManager.getPlateNumber());
        intent.putExtra("SLOT_NUMBER", recordManager.getSlotNumber());
        startActivityForResult(intent,PLATE_NUMBER_REQUEST);
    }

    public void startGalleryChooser() {
        if (Utility.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (Utility.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        }
        else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
        else if (requestCode == PLATE_NUMBER_REQUEST && resultCode == RESULT_OK) {
            recordManager.setPlateNumber(data.getStringExtra("PLATE_NUMBER"));
            String roomNumber = recordManager.recordSlot();
            Toast.makeText(getApplicationContext(), Optional.ofNullable(roomNumber).orElse("N/A"), Toast.LENGTH_LONG).show();
            recordManager.printContent(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (Utility.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (Utility.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }



    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap = Utility.scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                callCloudVision(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> plateDetectionTask =  new Utility.PlateDetectionTask(this, Utility.prepareAnnotationRequest(bitmap,this));
            plateDetectionTask.execute();

        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

}
