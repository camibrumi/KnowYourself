package yukami.weather;

import android.util.Log;

import java.io.*;
import java.net.*;
public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private static String IULIS_KEY = "&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    public String getWeatherData(String location) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            con = (HttpURLConnection) ( new URL(BASE_URL + location + IULIS_KEY)).openConnection();
            System.out.println("------------S-A FACUT OPENCONNEC");
            con.setRequestMethod("GET");
            System.out.println("con.setRequestMethod(\"GET\");");
            con.setDoInput(true);
            System.out.println("con.setDoInput(true);");

            con.setDoOutput(true);
            System.out.println("con.setDoOutput(true);");
            con.connect();
            System.out.println("con.connect();");
            //chivato
            System.out.println("-----------S-A CONNECTAT");
            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ( (line = br.readLine()) != null )
                buffer.append(line + "rn");
            if (line == null) System.out.println("-----------LINE IS NULL!!!!!!");
            else System.out.println("------------------LINE = " + line);

            is.close();
            con.disconnect();
            System.out.println("-----------------S-A DESCONNECTAT");
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

    //[I] The following code is not needed, at least not in the initial steps
    public byte[] getImage(String code) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ( is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }
}