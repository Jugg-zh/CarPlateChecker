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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String FILE_NAME = "temp.jpg";
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int PLATE_NUMBER_REQUEST = 4;
    //--------new fileds;
    public static final CarplateMapper mCarplateMapper = new CarplateMapper();
    private static boolean finishedDetectionTask;
    private static String plateNumber;
    private static Button[] buttons;
    private static String slotNumber;

//    private EditText editPlate, slotNum;
//    private String shift;
//    private TextView msg; //textview to show whether the car plate is registered or not
////    private ImageButton camButton, okButton, uploadButton;
////    private Button previous, next; //buttons for changing floorplan images
//    private ViewFlipper flipper;
//    private TextView plateNum;
//    private ImageView mImage;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referencing the buttons to their respective ids
        buttons = new Button[10];
        setUpButtons(buttons);
        Map<String, Map<String,String>> map = new HashMap<>();
        map = new Gson().fromJson(getString(R.string.map), new TypeToken<Map<String, Map<String,String>>>(){}.getType());
//        System.out.println("--------------------------------------------------------");
//        System.out.println(map.get("2A").get("1"));
//        System.out.println("--------------------------------------------------------");
//        System.out.println("--------------------------------------------------------");
//        System.out.println(map.get("2B").get("2"));
//        System.out.println("--------------------------------------------------------");
//        map.get("2A").put("2","5");
//        String json = new Gson().toJson(map,new TypeToken<Map<String, Map<String,String>>>(){}.getType());
//        System.out.println("--------------------------------------------------------");
//        System.out.println(json);
//        System.out.println("--------------------------------------------------------");
        setSlots(6);
        //the button for confirming the floor number and pole number
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String floorNumber = "2A";
                String poleNumber = "2";
                // map to number of slots
                int numberOfSlots = 5;
//                int numberOfSlots = Integer.parseInt(map.get(floorNumber).get(poleNumber));
                setSlots(numberOfSlots);
            }
        });
//        buttons[1].setOnClickListener(view -> startGalleryChooser());
//        okButton = findViewById(R.id.ok);
//        uploadButton = findViewById(R.id.uploadButton);
//        previous = findViewById(R.id.previous);
//        next = findViewById((R.id.next));

        //Referencing the declared variables to their respective UI components
//        editPlate = findViewById(R.id.editPlate);
//        slotNum = findViewById(R.id.slotNum);
//        msg = findViewById(R.id.unregisteredMsg);
//        flipper = findViewById(R.id.flipper);

        //creating array of floorplan images and fitting each image into the ViewFlipper
//        int images[] = {R.drawable.floor2aone, R.drawable.floor2atwo, R.drawable.floor2bone, R.drawable.floor2btwo, R.drawable.floor3a, R.drawable.floor3bone, R.drawable.floor3btwo, R.drawable.floor4a, R.drawable.floor4bone, R.drawable.floor4btwo};
//        for(int image: images){
//            flipperImages(image);
//        }
//        previous.setOnClickListener(view -> {
//            flipper.showPrevious();
//        });
//
//        next.setOnClickListener(view -> {
//            flipper.showNext();
//        });

        //Setting action on clicking the camera button
//        camButton.setOnClickListener(view -> System.out.println("fuck"));
//        camButton.setOnClickListener(view -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder
//                    .setMessage(R.string.dialog_select_prompt)
//                    .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
//                    .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
//            builder.create().show();
//            msg.setText("");
//        });
//
//        //Recording slot on clicking the ok button
//        okButton.setOnClickListener(view -> {
//            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
//            String[] time = timeStamp.toString().split(" ");
//            String[] hour = time[1].split(":");
//            if (Integer.parseInt(hour[0]) <= 12){
//                shift = "Morning";
//            }else {
//                shift = "Evening";
//            }
//            //Returning value of recordSlot to check (returns a string; "null" if car plate is not associated to a room
//            // otherwise the associated room number)
//            String check = mCarplateMapper.recordSlot(getModifiedTextFromGuard(), timeStamp, shift, getEnteredSlotNum());
//            if (check == null){
//                msg.setText("The plate is not registered!");
//            }else{
//                msg.setTextColor(Color.parseColor("#008000"));
//                msg.setText("The plate is registered with room no." + check);
//            }
//
//            Toast.makeText(getApplicationContext(),"Information added successfully!", Toast.LENGTH_LONG).show();
//            editPlate.setText("");
//            slotNum.setText("");
//        });
//
//        //downloading the recorded car information into a file
//        uploadButton.setOnClickListener(view -> {
//            writeTofile();
//            Toast.makeText(getApplicationContext(),"File downloaded successfully", Toast.LENGTH_LONG).show();
//        });
//
//        // reading the json file and convert the json file into objects
//        readFromFile();
    }

//    public void flipperImages(int image) {
//        ImageView floorplan = new ImageView(this);
//        floorplan.setBackgroundResource(image);
//        flipper.addView(floorplan);
//    }


//    public String getModifiedTextFromGuard(){
//        String editedPlate = editPlate.getText().toString();
//        return editedPlate;
//    }
//    public String getEnteredSlotNum(){
//        String slotNumber = slotNum.getText().toString();
//        return slotNumber;
//    }

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
        plateNumber = plate;
    }

    @Override
    public void onClick(View v) {
        int position = -1;
        for (int i = 0; i < buttons.length; i++) {
            if(v.getId() == buttons[i].getId()) {
                position = i+1;
                buttons[i].setBackgroundColor(getResources().getColor(R.color.green));
                buttons[i].setClickable(false);
            }
        }
        slotNumber = getSlotNumber(position);
        startGalleryChooser();
        System.out.println("second");
//        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
//        intent.putExtra("CAR_PLATE", plateNumber);
//        intent.putExtra("SLOT_NUMBER", slotNumber);
//        startActivityForResult(intent,PLATE_NUMBER_REQUEST);
//        startActivity(intent);

    }

    public void switchToRecordActivity() {
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        intent.putExtra("CAR_PLATE", plateNumber);
        intent.putExtra("SLOT_NUMBER", slotNumber);
        startActivityForResult(intent,PLATE_NUMBER_REQUEST);
    }

    private String getSlotNumber(int position) {
        String floorNumber = "2A";
        String poleNumber = "1";
        return "2A-1-1";
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
            plateNumber = data.getStringExtra("PLATE_NUMBER");
            System.out.println(plateNumber);
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            String[] time = timeStamp.toString().split(" ");
            String[] hour = time[1].split(":");
            String shift = null;
            if (Integer.parseInt(hour[0]) <= 12) {
                shift = "Morning";
            } else {
                shift = "Evening";
            }
            mCarplateMapper.recordSlot(plateNumber, timeStamp,
                    shift, slotNumber);

            Map<String, CarplateMapper.PlateInfomation> map = mCarplateMapper.getMap();
            for (String s : map.keySet()) {
                System.out.println(map.get(s).toString());
            }
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
                Bitmap bitmap =
                        Utility.scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                callCloudVision(bitmap);
//                editPlate.setVisibility(View.VISIBLE);
//                editPlate.setText(findViewById(R.id.plateNum));

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
        }
    }


    private static class PlateDetectionTask extends AsyncTask<Object, Void, String> {
        protected final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        PlateDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        public void setRequest(Vision.Images.Annotate annotate) {
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
//            if (activity != null) {
                activity.setPlateNumber(result);
                activity.switchToRecordActivity();

//                activity.setTaskFinished();

//                System.out.println("first");
//                TextView imageDetail = activity.findViewById(R.id.plateNum);
//                imageDetail.setText("Press camera button to detect plate or press download button to save the data.");
//
//                TextView textView = activity.findViewById(R.id.textView);
//                textView.setVisibility(View.VISIBLE);
//
//                TextView textView2 = activity.findViewById(R.id.textView2);
//                textView2.setVisibility(View.VISIBLE);
//
//                EditText editPlate = activity.findViewById(R.id.editPlate);
//                editPlate.setVisibility(View.VISIBLE);
//                editPlate.setText(result);
//
//                EditText slotNum = activity.findViewById(R.id.slotNum);
//                slotNum.setVisibility(View.VISIBLE);
//
//                ImageButton ok = activity.findViewById(R.id.ok);
//                ok.setVisibility(View.VISIBLE);

//                ViewFlipper flipper = activity.findViewById(R.id.flipper);
//                flipper.setVisibility(View.VISIBLE);
//
//                Button previous = activity.findViewById((R.id.previous));
//                previous.setVisibility(View.VISIBLE);
//
//                Button next = activity.findViewById((R.id.next));
//                next.setVisibility(View.VISIBLE);

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
//        TextView plateNum = findViewById(R.id.plateNum);
//        plateNum.setText("Loading, Please wait");
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> plateDetectionTask =  new PlateDetectionTask(this, Utility.prepareAnnotationRequest(bitmap,this));
            finishedDetectionTask = false;
            plateDetectionTask.execute();

        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private void writeTofile(){
        if(Utility.requestPermission(this, 200, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            mCarplateMapper.syncToFile();
        }
    }

    private void readFromFile(){
        if(Utility.requestPermission(this,200,Manifest.permission.READ_EXTERNAL_STORAGE)){
            mCarplateMapper.syncFromDesktop();
        }
    }


}
