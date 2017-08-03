package com.example.camelia.debug6;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    Button commitB;
    Boolean answered;
    int startHour, hour, minute;
    ProgressBar pB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commitB = (Button) findViewById(R.id.commit);
        pB = (ProgressBar) findViewById(R.id.progressBar);

        //int unixCurrentTime = (int) (System.currentTimeMillis() / 1000L);
        //int unixStartTime = unixCurrentTime - 12*60*60;
        //System.out.println("unixStartTime: " + String.valueOf(unixStartTime));
        //System.out.println("unixCurrentTime: " + String.valueOf(unixCurrentTime));

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(this,WeatherReceiver.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (this, 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            //System.out.println("WEATHER PENDING INTENT TO SEND");
            weatherPendingIntent.send(this, 0, weatherIntent);
            //System.out.println("WEATHER PENDING INTENT SENT");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        AlarmManager alarmManager1 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 3 * 60 * 60, weatherPendingIntent); //TODO put frequency of currentWeather data (current every 3h)

        Calendar calendar = GregorianCalendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);


        startHour = this.getResources().getInteger(R.integer.startHour);

        String answeredContent = null;
        try {
            answeredContent = readFromFile(getString(R.string.answeredFile), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (answeredContent == null) answered = false;
        else answered = Boolean.valueOf(answeredContent); //when the user opens the aplication for the first time after the star hour --> answer == null
        System.out.println("ANSWERed" + answered);
        if (hour < startHour) {
            writeToFile(getString(R.string.answeredFile), String.valueOf(false), this);
            //we cancel the notification
            NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(123);
        } else {
            if (!answered) {

                //WE LAUNCH THE NOTIFICATION
                Intent notifyIntent = new Intent(this,MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000 * startHour * 60 * 60,
                        1000 * 24 * 60 * 60, pendingIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            if (fileName == getString(R.string.answeredFile)) outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String fileName, Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

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
            Log.e("login activity", "File not found: " + e.toString());
            //File yourFile = new File(fileName);
            //yourFile.createNewFile(); // if file already exists will do nothing
            //readFromFile(fileName, context);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
 /*
    public void commit(View view) {
        if (!answered) {
            Calendar calendar = GregorianCalendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);

            if (isNetworkAvailable()) {
                if (hour >= startHour) {
                    int howWasYourDay = pB.getProgress();
                    System.out.println("how was my day------------> " + howWasYourDay);
                    Intent intent = new Intent(this, ResponseActivity.class);
                    intent.putExtra("how", String.valueOf(howWasYourDay));
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Commit available at " + startHour + "h", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Answer committed today!", Toast.LENGTH_SHORT);
            toast.show();
        }
    } */

    public void commit(View view) {
        if (isNetworkAvailable()) {
            int howWasYourDay = pB.getProgress();
            System.out.println("how was my day------------> " + howWasYourDay);
            Intent intent = new Intent(this, ResponseActivity.class);
            intent.putExtra("how", String.valueOf(howWasYourDay));
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void incrementProgressBar (View view) {
        pB.incrementProgressBy(5);
    }

    public void decrementProgressBar (View view) {
        pB.incrementProgressBy(-5);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}