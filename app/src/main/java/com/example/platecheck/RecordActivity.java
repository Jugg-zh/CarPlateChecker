package com.example.platecheck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        //initialize button
        Button submitButton = findViewById(R.id.button2);
        plateTextField = findViewById(R.id.editPlateTextView);
//        TextView promptTextFiled = findViewById(R.id.promptInformation);
        TextView slotTextField = findViewById(R.id.slotNumberTextView);
        plateTextField.setText(carPlate);
        slotTextField.setText(slotNumber);
        //Recording slot on clicking the ok button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("PLATE_NUMBER", plateTextField.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
//        submitButton.setOnClickListener(view -> {
////            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
////            String[] time = timeStamp.toString().split(" ");
////            String[] hour = time[1].split(":");
////            String shift = null;
////            if (Integer.parseInt(hour[0]) <= 12) {
////                shift = "Morning";
////            } else {
////                shift = "Evening";
////            }
//            //Returning value of recordSlot to check (returns a string; "null" if car plate is not associated to a room
//            // otherwise the associated room number)
////            String roomNumber = carPlateMapper.recordSlot(plateTextField.getText().toString(), timeStamp, shift, slotNumber);
//
//            promptTextFiled.setText(Optional.ofNullable("415").orElse("Not Registered"));
//            //wait to check if it is necessary
////            if (check == null) {
////                msg.setText("The plate is not registered!");
////            } else {
////                msg.setTextColor(Color.parseColor("#008000"));
////                msg.setText("The plate is registered with room no." + check);
////            }
//
//            Toast.makeText(getApplicationContext(), "Information added successfully!", Toast.LENGTH_LONG).show();
////            editPlate.setText("");
////            slotNum.setText("");
//        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("PLATE_NUMBER", plateTextField.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

    }

}
