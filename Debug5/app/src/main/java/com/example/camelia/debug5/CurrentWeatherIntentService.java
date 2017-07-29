package com.example.camelia.debug5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class CurrentWeatherIntentService extends IntentService{
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    int dayOfMonth, month, minute, hour, startHour;

    public CurrentWeatherIntentService() {
        super("CurrentWeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //System.out.println("INTENT STARTED!!!");

        Calendar calendar = GregorianCalendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        startHour = this.getResources().getInteger(R.integer.startHour);

        //System.out.println("am facut calendarul");
        String weatherData = "";
        try {
            weatherData = new getURLData().execute(URL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        JSONObject obj = null;
        try {
            obj = new JSONObject(weatherData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("am luat json object-ul");

        double currentTemp = 25; //initialized because I had problems with String.valueOf() (variable not declared)
        try {
            currentTemp = obj.getJSONObject("main").getDouble("temp");
            //System.out.println("CURRENT TEMP= " + currentTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String currentDateId = String.valueOf(dayOfMonth) + String.valueOf(month); //we use it to overwrite yesterday's currentWeather file (see writeToFile method)
        currentTemp = currentTemp - 273.15; //in celsius
        //System.out.println("inainte de a scrie in fisier");
        String fileName = "";
        if (hour <= 8) fileName = "nightCurrentWeather.txt";
        else if (hour >= 9) fileName = "dayCurrentWeather.txt";

        String fileContent = null;
        try {
            fileContent = readFromFile(fileName, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileContent == null) writeToFile(fileName, currentDateId + " " + String.valueOf(currentTemp), this, false, ""); //we converted kelvin to celsius
        else {
            //we'll obtain just the first word of the string because we want to avoid the case that a temperature contains the currentDateId. this wold leave to wrong results.
            String arr[] = fileContent.split(" ", 2);
            String currentDate = arr[0];
            writeToFile(fileName, currentDateId + " " + String.valueOf(currentTemp), this, true, currentDate); //we converted kelvin to celsius
        }
        //System.out.println("pusca?");
        System.out.println("s-a terminat intentul");
    }

    private void writeToFile(String fileName, String data, Context context, boolean isContent, String currentDate) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));;
            String currentDateId = String.valueOf(dayOfMonth) + String.valueOf(month);
            if (!isContent || !currentDate.contains(currentDateId)) {
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            }

            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
/*
    private void writeToFile(String data, Context context) throws IOException {
        String currentDateId = String.valueOf(dayOfMonth) + String.valueOf(month);
        String fileName = "";

        if (hour <= 8) fileName = "nightCurrentWeather.txt";
        else if (hour >= 9) fileName = "dayCurrentWeather.txt";

        String fileContent = readFromFile(fileName, this);
        System.out.println("AM PUTUT CITI DIN FISIERUL NIGHT/DAY CURRENT WEATHER");

        //we'll obtain just the first word of the string because we want to avoid the case that a temperature contains the currentDateId. this wold leave to wrong results.
        String arr[] = fileContent.split(" ", 2);
        String currentDate = arr[0];
        System.out.println("CURRENT DATE IS: " + currentDate);
        System.out.println("CURRENT DATE ID IS: " + currentDateId);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            System.out.println("Reusim sa cream outputstreamwriter MODE APPEND");
            if (!currentDate.contains(currentDateId)) {
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                System.out.println("OUTPUT STREAM WRITER MODE PRIVATE");
            }

            outputStreamWriter.write(" " + data + '\n');
            System.out.println("AM REUSIT SA SCRIEM");
            outputStreamWriter.close();
            System.out.println("AM INCHIS OUTPUTSTREAMWRITER");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    } */

    private String readFromFile(String filename, Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            //Log.e("login activity", "File not found: " + e.toString());
            File yourFile = new File(filename);
            yourFile.createNewFile(); // if file already exists will do nothing
            readFromFile(filename, context);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
