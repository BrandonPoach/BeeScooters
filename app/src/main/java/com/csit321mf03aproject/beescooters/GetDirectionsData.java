package com.csit321mf03aproject.beescooters;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;
    private Polyline line;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];

        Log.d("PARSE_ERROR", " lol " + url);

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        String[] directionsList;
        DataParser parser = new DataParser();
        Log.d("PARSE_ERROR", " lol " + s);
        directionsList = parser.parseDirections(s);
        displayDirection(directionsList);
    }

    //displays the polylines
    public void displayDirection(String[] directionsList)
    {
        int count = directionsList.length;

        PolylineOptions options = new PolylineOptions();

        for(int i = 0;i<count;i++)
        {
            options.color(Color.YELLOW);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));
        }

        line = mMap.addPolyline(options);
        MainScreen.pLine = line;


    }

}
