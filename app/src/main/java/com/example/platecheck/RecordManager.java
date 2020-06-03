package com.example.platecheck;

import android.Manifest;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RecordManager {
    private CarplateMapper mCarplateMapper;
    private String plateNumber;
    private String slotNumber;
    private Map<String, Map<String,String>> floorAndPoleToSlotsMapper;

    public RecordManager(String json) {
        mCarplateMapper = new CarplateMapper();
        floorAndPoleToSlotsMapper = new Gson().fromJson(json, new TypeToken<Map<String, Map<String, String>>>() {}.getType());
        plateNumber = null;
        slotNumber = null;
    }

    public int getNumberOfSlots (String floorNumber, String poleNumber) {
        return Integer.parseInt(floorAndPoleToSlotsMapper.get(floorNumber).get(poleNumber));
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

    public void setSlotNumber (String floorNumber, String poleNumber, int slot) {
        this.slotNumber = floorNumber + "-" + poleNumber + "-" + slot;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String recordSlot () {

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        String[] time = timeStamp.toString().split(" ");
        String[] hour = time[1].split(":");
        String shift = null;
        if (Integer.parseInt(hour[0]) <= 12) {
            shift = "Morning";
        } else {
            shift = "Evening";
        }
        return mCarplateMapper.recordSlot(this.plateNumber, timeStamp, shift, this.slotNumber);
    }

    public void writeTofile(MainActivity activity) {
        if(Utility.requestPermission(activity, 200, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            mCarplateMapper.syncToFile();
        }
    }

    public void readFromFile(MainActivity activity) {
        if(Utility.requestPermission(activity,200,Manifest.permission.READ_EXTERNAL_STORAGE)){
            mCarplateMapper.syncFromDesktop();
        }
    }

    public void printContent(MainActivity activity) {
        Map<String, CarplateMapper.PlateInfomation> map = mCarplateMapper.getMap();
        for (String s : map.keySet()) {
            System.out.println(map.get(s).toString());
        }
    }

    public List<String> getFloorNumberList() {
        List<String> res = new ArrayList<>(floorAndPoleToSlotsMapper.keySet());
        Collections.sort(res);
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getPoleNumberList(String floorNumber) {
        List<String> res = new ArrayList<String>(floorAndPoleToSlotsMapper.getOrDefault(floorNumber, new HashMap<String,String>()).keySet());
        Collections.sort(res, (a,b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));
        assert res != null;
        return res;
    }

}
