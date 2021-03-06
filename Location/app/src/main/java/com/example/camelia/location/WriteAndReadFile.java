package com.example.camelia.location;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by camelia on 21/08/17.
 */

public class WriteAndReadFile {
    public void writeToInternalFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromInternalFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void writeToExternalFile(String filename, String data, Boolean append) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File(myDir, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file, append);
            byte[] strb = data.getBytes();
            for (int i = 0; i < strb.length; ++i) {
                fos.write(strb[i]);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileStreamsTest: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromExternalFile(String filename) throws IOException { //trebuie testat!!
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        //get InputStream of a file
        if (file.exists()) {
            InputStream is = new FileInputStream(file);
            String strContent = "";

                /*
                 * There are several way to convert InputStream to String. First is using
                 * BufferedReader as given below.
                 */

            //Create BufferedReader object
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sbfFileContents = new StringBuffer();
            String line = null;

            if ((line = bReader.readLine()) == null) return null; // here **
            else {
                sbfFileContents.append(line);
                //read file line by line
                while( (line = bReader.readLine()) != null){
                    sbfFileContents.append(line);
                }
            }


            //finally convert StringBuffer object to String!
            strContent = sbfFileContents.toString();

                /*
                 * Second and one liner approach is to use Scanner class. This is only supported
                 * in Java 1.5 and higher version.
                 */

            //strContent = new Scanner(is).useDelimiter("\\A").next();
            return strContent;
        }
        return null;
    }
}
