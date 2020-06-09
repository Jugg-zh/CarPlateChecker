package com.example.platecheck;

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

    public RecordManager(String json, MainActivity activity) {

        mCarplateMapper = new CarplateMapper(activity);
        floorAndPoleToSlotsMapper = new Gson().fromJson(json, new TypeToken<Map<String, Map<String, String>>>() {}.getType());
        plateNumber = null;
        slotNumber = null;
    }

    /**
     *
     * @param floorNumber
     * @param poleNumber
     * @return how many parking slots are in this position
     */
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

    /**
     *
     * @return the roomNumber which this carPlate belongs to, "N/A" is returned if it has no been
     * registered
     */
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

    /**
     *
     * @return Write the content to Internal storage, return true if it's written successfully, false
     * otherwise.
     */
    public boolean writeToFile() {
        return mCarplateMapper.syncToFile();
    }

    /**
     * Read the mapping from Internal storage, PlateNumber -> RoomNumber
     */
    public void readFromFile() {
        mCarplateMapper.syncFromDesktop();
    }

    /**
     *
     * @return A List of Strings represent FloorNumber, ex: [2A,2B]
     */
    public List<String> getFloorNumberList() {
        List<String> res = new ArrayList<>(floorAndPoleToSlotsMapper.keySet());
        Collections.sort(res);
        return res;
    }

    /**
     *
     * @param floorNumber
     * @return A list of Poles in the "floorNumber", ex: 2A -> [1,2,3,5,6]
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getPoleNumberList(String floorNumber) {
        List<String> res = new ArrayList<String>(floorAndPoleToSlotsMapper.getOrDefault(floorNumber, new HashMap<String,String>()).keySet());
        Collections.sort(res, (a,b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));
        assert res != null;
        return res;
    }

    /**
     * update the Internal map(file) using the json string
     * @param json
     */
    public void updateFile (String json) {
        mCarplateMapper.updateTable(json);

    }

    /**
     *
     * @return upload the file to the database via Node server
     */
    public String uploadFile () {
        return mCarplateMapper.uploadFile();
    }

}
