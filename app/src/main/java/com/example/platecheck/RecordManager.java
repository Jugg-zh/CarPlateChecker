package com.example.platecheck;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.Map;

public class RecordManager {
    private CarplateMapper mCarplateMapper;
    private String plateNumber;
    private String slotNumber;
    private Map<String, Map<String,String>> floorAndPoleToSlotsMapper;

    public RecordManager(Context context) {
        mCarplateMapper = new CarplateMapper();

        floorAndPoleToSlotsMapper = new Gson().fromJson(context.getString(R.string.map),
                new TypeToken<Map<String, Map<String, String>>>() {}.getType());
        plateNumber = null;
        slotNumber = null;
    }

    public String getNumberOfSlots (String floorNumber, String poleNumber) {
        return floorAndPoleToSlotsMapper.get(floorNumber).get(poleNumber);
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public String getSlotNumber() {
        return this.slotNumber;
    }

    public void setPlateNumber (String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setSlotNumber (String slotNumber) {
        this.slotNumber = slotNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordSlot () {

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        String[] time = timeStamp.toString().split(" ");
        String[] hour = time[1].split(":");
        String shift = null;
        if (Integer.parseInt(hour[0]) <= 12) {
            shift = "Morning";
        } else {
            shift = "Evening";
        }
        mCarplateMapper.recordSlot(this.plateNumber, timeStamp, shift, this.slotNumber);
    }

    private void writeTofile(MainActivity activity) {
        if(Utility.requestPermission(activity, 200, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            mCarplateMapper.syncToFile();
        }
    }

    private void readFromFile(MainActivity activity) {
        if(Utility.requestPermission(activity,200,Manifest.permission.READ_EXTERNAL_STORAGE)){
            mCarplateMapper.syncFromDesktop();
        }
    }
}
