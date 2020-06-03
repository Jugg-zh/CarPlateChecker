package com.example.platecheck;

import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

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
            sb.append("timeStamp : " + timeStamp + " ");
            sb.append("shift : " + shift + " ");
            sb.append("\n");
            return sb.toString();
        }
    }

    protected Map<String, PlateInfomation> carPlateNumberToPlateInformation;

    public CarplateMapper(){
        carPlateNumberToPlateInformation = new HashMap<>();
    }

    public CarplateMapper(Map<String, PlateInfomation> map){
        carPlateNumberToPlateInformation = map;
    }


    /**
     * Create a folder and file if it does not exist, if it exists, then read the contents
     * and store them in the HashMap
     */
    public void syncFromDesktop(){
        //setting up the path
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "table.json";
        File dir = new File(baseDir + "/Carplate/");
        dir.mkdir();
        File dir2 = new File(dir.getAbsolutePath() + "/FromDesktop/");
        dir2.mkdir();
        File f = new File(dir2.getAbsolutePath() + "/"+ fileName);
        //convert json file back to objects
        try {
            Gson gson = new Gson();
            FileInputStream fileInputStream = new FileInputStream(f);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));

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
        PlateInfomation currentPlateInfo = carPlateNumberToPlateInformation.getOrDefault(carPlate, new PlateInfomation(carPlate, null));
        currentPlateInfo.setSlotNumber(slotNumber);
        currentPlateInfo.setTimeStamp(timestamp);
        currentPlateInfo.setShift(shift);
        if(!carPlateNumberToPlateInformation.containsKey(carPlate)){
            carPlateNumberToPlateInformation.put(carPlate, currentPlateInfo);
        }

        return currentPlateInfo.getRoomNumber();
    }

    /**
     * create the folder and file if it does not exist, otherwise store the object in the json file to the files
     */
    public void syncToFile() {

        //setting up the file path
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "table.json";
        File dir = new File(baseDir + "/Carplate/");
        dir.mkdir();
        File dir2 = new File(dir.getAbsolutePath() + "/ToDesktop/");
        dir2.mkdir();
        File f = new File(dir2.getAbsolutePath() + "/"+ fileName);

        // convert the map into a list which stores all the values in the map,
        //and then convert this list into a json file.
        try {
            Gson gson = new Gson();
            FileOutputStream outputStream = new FileOutputStream(f);
            ArrayList<PlateInfomation> plateList = new ArrayList<>();
            for(String plate : carPlateNumberToPlateInformation.keySet()){
                plateList.add(carPlateNumberToPlateInformation.get(plate));
            }
            Type listType = new TypeToken<List<PlateInfomation>>() {}.getType();
            String json = gson.toJson(plateList,listType);
            outputStream.write(json.getBytes());
            System.out.println(json);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String,PlateInfomation> getMap() {
        return this.carPlateNumberToPlateInformation;
    }
}
