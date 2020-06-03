package com.example.platecheck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.Timestamp;
import java.util.Optional;

public class RecordActivity extends AppCompatActivity {

    private TextView plateTextField;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        recordSlot(intent);

//        //downloading the recorded car information into a file
//        uploadButton.setOnClickListener(view -> {
//            writeTofile();
//            Toast.makeText(getApplicationContext(), "File downloaded successfully", Toast.LENGTH_LONG).show();
//        });
//
//        // reading the json file and convert the json file into objects
//        readFromFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordSlot(Intent intent) {
        String carPlate = intent.getStringExtra("CAR_PLATE");
        String slotNumber = intent.getStringExtra("SLOT_NUMBER");


        //initialize buttons
        Button submitButton = findViewById(R.id.button2);
        plateTextField = findViewById(R.id.editPlateTextView);

        TextView promptTextFiled = findViewById(R.id.promptInformation),slotTextField = findViewById(R.id.slotNumberTextView);

        plateTextField.setText(carPlate);
        slotTextField.setText(slotNumber);
        //Recording slot on clicking the ok button
        submitButton.setOnClickListener(view -> {
//            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
//            String[] time = timeStamp.toString().split(" ");
//            String[] hour = time[1].split(":");
//            String shift = null;
//            if (Integer.parseInt(hour[0]) <= 12) {
//                shift = "Morning";
//            } else {
//                shift = "Evening";
//            }
            //Returning value of recordSlot to check (returns a string; "null" if car plate is not associated to a room
            // otherwise the associated room number)
//            String roomNumber = carPlateMapper.recordSlot(plateTextField.getText().toString(), timeStamp, shift, slotNumber);

            promptTextFiled.setText(Optional.ofNullable("415").orElse("Not Registered"));
            //wait to check if it is necessary
//            if (check == null) {
//                msg.setText("The plate is not registered!");
//            } else {
//                msg.setTextColor(Color.parseColor("#008000"));
//                msg.setText("The plate is registered with room no." + check);
//            }

            Toast.makeText(getApplicationContext(), "Information added successfully!", Toast.LENGTH_LONG).show();
//            editPlate.setText("");
//            slotNum.setText("");
        });
    }
    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.putExtra("message_return", "This data is returned when user click back menu in target activity.");
//        setResult(RESULT_OK, intent);

        Intent intent = new Intent();
//        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
//        String[] time = timeStamp.toString().split(" ");
//        String[] hour = time[1].split(":");
//        String shift = null;
//        if (Integer.parseInt(hour[0]) <= 12) {
//            shift = "Morning";
//        } else {
//            shift = "Evening";
//        }
//        intent.putExtra("TIME_STAMP",timeStamp);
//        intent.putExtra("SHIFT", shift);
        intent.putExtra("PLATE_NUMBER", plateTextField.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

    }

}
