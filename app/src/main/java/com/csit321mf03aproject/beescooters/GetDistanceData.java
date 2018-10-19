package com.csit321mf03aproject.beescooters;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetDistanceData extends AsyncTask<String, String, String> {
    String distanceURL;

    @Override
    protected String doInBackground(String... strings) {
        GoogleUrl googleUrl = new GoogleUrl();
        try
        {
            distanceURL = googleUrl.readUrl(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //contains step by step direction on how to get to destination in JSON
        return distanceURL;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        retrieveDistance(s);
    }

    public void retrieveDistance (String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        String distance = "";

       try
       {
           jsonObject = new JSONObject(jsonData);
           jsonArray = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
           jsonObject = jsonArray.getJSONObject(0).getJSONObject("distance");
           distance = jsonObject.getString("text");
           Log.d("JSONArray", ""+distance);
       }

       catch (JSONException e)
       {
            e.printStackTrace();
       }


       MainScreen.mDistance = distance;

    }
}
