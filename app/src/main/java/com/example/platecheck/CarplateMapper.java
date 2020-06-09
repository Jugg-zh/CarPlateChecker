package com.example.platecheck;

import android.os.Build;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.annotation.RequiresApi;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CarplateMapper{

    class PlateInfomation{
        protected String plateNumber;
        protected String roomNumber;
        protected String slotNumber;
        protected Timestamp timeStamp;
        protected String shift;

        public PlateInfomation(String carPlate, String roomNumber){
            this.roomNumber = roomNumber;
            this.plateNumber = carPlate;
            this.timeStamp = null;
            this.slotNumber = null;
            this.shift = null;
        }

        public void setSlotNumber(String slotNumber){
            this.slotNumber = slotNumber;
        }

        public void setTimeStamp(Timestamp timeStamp){
            this.timeStamp = timeStamp;
        }

        public void setShift(String shift){
            this.shift = shift;
        }

        public String getRoomNumber(){
            return this.roomNumber;
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append("plateNumber : " + plateNumber + " ");
            sb.append("roomNumber : " + roomNumber + " ");
            sb.append("slotNumber : " + slotNumber + " ");
            sb.append("timeStamp : " + timeStamp.toString() + " GMT " + " ");
            sb.append("shift : " + shift + " ");
            sb.append("\n");
            return sb.toString();
        }
    }

    private Map<String, PlateInfomation> carPlateNumberToPlateInformation;
    private MainActivity activity;
    private final String INPUT_FILE = "table.json";
    private final String OUTPUT_FILE = "jsonFile.json";

    public CarplateMapper(MainActivity activity){
        carPlateNumberToPlateInformation = new HashMap<>();
        this.activity = activity;
    }

    /**
     * Create a folder and file if it does not exist, if it exists, then read the contents
     * and store them in the HashMap
     */
    public void syncFromDesktop(){
        //clear the previous map
        carPlateNumberToPlateInformation = new HashMap<>();
        FileInputStream fileInputStream;
        //convert json file back to objects
        try {
            fileInputStream = activity.openFileInput(INPUT_FILE);
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

            StringBuilder sb = new StringBuilder();
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
                sb.append("\n");
            }
            //parsing json file into array of the objects
            PlateInfomation[] slots = gson.fromJson(sb.toString(), PlateInfomation[].class);
            //mapping plate number to the object
            for(PlateInfomation p : slots){
                carPlateNumberToPlateInformation.put(p.plateNumber, p);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param json write the json string queried from the server to a local file, so it can be read later
     */
    public void updateTable(String json){
        //save the json string to file
        try {
            FileOutputStream outputStream = activity.openFileOutput(INPUT_FILE, activity.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param carPlate is the car plate number that needs to be recorded
     * @param timestamp indicates when the car plate number is recorded
     * @param shift indicates which round (morning or evening) is the car plate number recorded
     * @param slotNumber represents which slot is this car parked
     * @return return a roomNumber(string format) if the car plate number is belonging to a resident null otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String recordSlot(String carPlate, Timestamp timestamp, String shift, String slotNumber){
        PlateInfomation currentPlateInfo = carPlateNumberToPlateInformation.getOrDefault(carPlate, new PlateInfomation(carPlate, "N/A"));
        currentPlateInfo.setSlotNumber(slotNumber);
        currentPlateInfo.setTimeStamp(timestamp);
        currentPlateInfo.setShift(shift);

        carPlateNumberToPlateInformation.putIfAbsent(carPlate, currentPlateInfo);

        return currentPlateInfo.getRoomNumber();
    }

    /**
     * create the file if it does not exist, otherwise store the map to the OUTPUT file
     */
    public boolean syncToFile() {

        // convert the map into a list which stores all the values in the map,
        //and then convert this list into a json file.
        File outputFile = new File(activity.getFilesDir(), OUTPUT_FILE);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // the content will be appended to the file if the file exists
            FileOutputStream outputStream = activity.openFileOutput(OUTPUT_FILE, activity.MODE_APPEND);
            Gson gson = new Gson();
            ArrayList<PlateInfomation> plateList = new ArrayList<>();
            for(String plate : carPlateNumberToPlateInformation.keySet()){
                // if the car is not parked in the parking place, skip it
                if(carPlateNumberToPlateInformation.get(plate).slotNumber != null) {
                    plateList.add(carPlateNumberToPlateInformation.get(plate));
                }
            }
            Type listType = new TypeToken<List<PlateInfomation>>() {}.getType();
            String json = gson.toJson(plateList,listType);
            json = json.substring(1, json.length()-1);
            json = json + ",";
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @return the content from OUTPUT file
     */
    public String uploadFile(){
        try {
            FileInputStream fileInputStream = activity.openFileInput(OUTPUT_FILE);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));
            StringBuilder sb = new StringBuilder();
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
                sb.append("\n");
            }
            fileInputStream.close();
            br.close();
            activity.deleteFile(OUTPUT_FILE);
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
