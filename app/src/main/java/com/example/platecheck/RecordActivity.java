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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordSlot(Intent intent) {
        String carPlate = intent.getStringExtra("CAR_PLATE");
        String slotNumber = intent.getStringExtra("SLOT_NUMBER");

        //initialize button
        Button submitButton = findViewById(R.id.button2);
        plateTextField = findViewById(R.id.editPlateTextView);

        TextView slotTextField = findViewById(R.id.slotNumberTextView);
        plateTextField.setText(carPlate);
        slotTextField.setText(slotNumber);
        //Record slot by clicking the button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("PLATE_NUMBER", plateTextField.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("PLATE_NUMBER", plateTextField.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

    }

}
