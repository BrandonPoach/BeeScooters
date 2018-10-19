package com.csit321mf03aproject.beescooters;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//class to send directions URL to Google and retrieve result
public class GoogleUrl {

    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            //open connection with URL
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            //read response from URL
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);

            }

            data = stringBuffer.toString();
            Log.d("downloadUrl", data);

            bufferedReader.close();

        }

        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if(inputStream != null)
                inputStream.close();
            urlConnection.disconnect();
        }

        Log.d("data downloaded",data);
        return data;
    }
}
