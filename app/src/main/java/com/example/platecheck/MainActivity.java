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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private Spinner floorNum, poleNum;
    private TextView promptText;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordManager = new RecordManager(Utility.getJsonString(this), this);
        recordManager.readFromFile();

        setUpButtons(buttons);
        setSlots(-1, false);
        floorNum = findViewById(R.id.floorNum);
        poleNum = findViewById(R.id.poleNum);
        promptText = findViewById(R.id.promptText);
        ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, recordManager.getFloorNumberList());
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorNum.setAdapter(floorAdapter);
        floorNum.setOnItemSelectedListener(this);

        final Button submitButton = findViewById(R.id.submitButton);
        // once the submit button is clicked, write the map to the file.
        submitButton.setOnClickListener((View view) -> {
            boolean successed = false;
            while (!successed) {
                successed = recordManager.writeToFile();
            }
            Toast.makeText(getApplicationContext(), "File Saved!", Toast.LENGTH_LONG).show();

        });

        final Button downloadButton = findViewById(R.id.downloadButton);
        // if download Button is clicked, download the map from the server
        downloadButton.setOnClickListener( (View view) -> {
                HttpGetRequest request = new HttpGetRequest(MainActivity.this);
                request.execute();
        });
        final Button uploadButton = findViewById(R.id.uploadButton);
        // if upload button is clicked, upload the records to the server.
        uploadButton.setOnClickListener( (View view) -> {
                HttpPostRequest request = new HttpPostRequest(MainActivity.this);
                request.execute();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String floorNumberString = this.floorNum.getSelectedItem().toString();
        List<String> poleList = recordManager.getPoleNumberList(floorNumberString);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, poleList);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.notifyDataSetChanged();
        poleNum.setAdapter(dataAdapter2);
        poleNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSlots(0, true);
                setPromptText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * initializing the buttons.
     * @param buttons
     */
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

    /**
     * Set how many buttons(slots) are visible to the users, the buttons are numbered from 1-10
     * @param numberOfSlots
     * @param selected
     */
    public void setSlots(int numberOfSlots, boolean selected) {
        if (selected) {
            String floorNumber = this.floorNum.getSelectedItem().toString();
            String poleNumber = this.poleNum.getSelectedItem().toString();
            numberOfSlots = recordManager.getNumberOfSlots(floorNumber,poleNumber);
        }

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


    /**
     * update the file which is in Internal storage using the json String
     * @param json String format queried from the server
     */
    public void updateFile(String json) {
        recordManager.updateFile(json);
        recordManager.readFromFile();
        Toast.makeText(getApplicationContext(),
                "Download Successfully!", Toast.LENGTH_LONG).show();
    }

    public String uploadFile() {
        return recordManager.uploadFile();
    }

    /**
     * set the text filed
     */
    public void setPromptText() {
        String floorNumber = this.floorNum.getSelectedItem().toString();
        String poleNumber = this.poleNum.getSelectedItem().toString();
        String prompt = "\t\t\tFloor " + floorNumber + "\t\tPole " + poleNumber;
        promptText.setText(prompt);
        promptText.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    /**
     * Check which button(slot) is clicked to generate the slot Number and set the slot number
     * in RecordManager
     * @param v
     */
    @Override
    public void onClick(View v) {
        for (int i = 0; i < buttons.length; i++) {
            if (v.getId() == buttons[i].getId()) {
                buttons[i].setBackgroundColor(getResources().getColor(R.color.green));
                buttons[i].setClickable(false);
                recordManager.setSlotNumber(floorNum.getSelectedItem().toString(),
                        poleNum.getSelectedItem().toString(), i+1);
            }
        }
        //can switch to use camera
        startGalleryChooser();
        Toast.makeText(getApplicationContext(), "Loading....", Toast.LENGTH_LONG).show();
    }

    /**
     * switch to another activity once the user clicked any button(slot)
     */
    public void switchToRecordActivity() {
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        intent.putExtra("CAR_PLATE",  recordManager.getPlateNumber());
        intent.putExtra("SLOT_NUMBER", recordManager.getSlotNumber());
        startActivityForResult(intent,PLATE_NUMBER_REQUEST);
    }

    public void startGalleryChooser() {
        if (Utility.requestPermission(this,
                GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
            Uri photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", getCameraFile());
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
            Uri photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
        else if (requestCode == PLATE_NUMBER_REQUEST && resultCode == RESULT_OK) {
            recordManager.setPlateNumber(data.getStringExtra("PLATE_NUMBER"));
            String roomNumber = recordManager.recordSlot();
            Toast.makeText(getApplicationContext(), Optional.ofNullable(roomNumber).orElse("N/A"), Toast.LENGTH_LONG).show();
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


    /**
     *prepare the image
     * @param uri the target image url
     */
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

    /**
     * create a AsyncTask to use the GCP service to extract the plateNumber from the image
     * @param bitmap the target image
     */
    private void callCloudVision(final Bitmap bitmap) {
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> plateDetectionTask =  new PlateDetectionTask(this,
                    Utility.prepareAnnotationRequest(bitmap,this));
            plateDetectionTask.execute();

        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

}
