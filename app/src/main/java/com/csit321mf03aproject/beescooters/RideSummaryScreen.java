package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RideSummaryScreen extends AppCompatActivity {
    TextView txtTimeElapsed;
    TextView txtAmount;
    TextView txtBalanceBefore;
    TextView txtBalanceAfter;
    Button buttonMainMenu;
    SharedPreferences preferences;
    Double currentBalance, newBalance, payment_amount;
    int rounded_up;
    String tripTime, scooterID, userID;
    final String URL = "http://beescooters.net/admin/deductCredit.php";
    HashMap<String, String> paramHash;
    private SharedPreferences.Editor editor;
    Result result = new Result();
    Double lat = 0.0, lng = 0.0;
    String access_token;

    //Dont allow user to press back button to Riding Screen
    //Goes to Main Menu instead
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_summary_screen);

        //Change Action Bar Title
        getSupportActionBar().setTitle("Ride Summary");
        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = preferences.getString("userID", "ERROR getting User ID");

        txtTimeElapsed = findViewById(R.id.txtTimeElapsed);
        //txtDistanceTravelled = findViewById(R.id.txtDistanceTravelled);
        txtAmount = findViewById(R.id.txtAmount);
        txtBalanceBefore = findViewById(R.id.txtBalanceBefore);
        txtBalanceAfter = findViewById(R.id.txtBalanceAfter);
        buttonMainMenu = findViewById(R.id.buttonMainMenu);
        payment_amount = 1.0;
        access_token = preferences.getString("access_token", null);

        //get data passed from Riding Screen
        Bundle trip_time = getIntent().getExtras();
        if(trip_time == null) {
            rounded_up = 0;
        } else {
            payment_amount = 1.0;
            rounded_up = 1;
            tripTime = String.valueOf(trip_time.getInt("TRIP_TIME"));
            Log.d("TRIP", ""+tripTime);
            scooterID = trip_time.getString("SCOOTER_ID");
            Log.d("SCOOTER_IDs", ""+scooterID);
            //payment_amount = Integer.valueOf(tripTime) / 100;
            rounded_up = (trip_time.getInt("TRIP_TIME") / 60);
            payment_amount += rounded_up * 0.10;
            //set payment cost plan here later
        }

        currentBalance = Double.valueOf(preferences.getString("creditBalance", "ERROR getting credit balance"));
        newBalance = currentBalance - payment_amount;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        editor = preferences.edit();
        editor.putString("creditBalance", String.valueOf(df.format(newBalance)));
        editor.commit();

        txtTimeElapsed.setText("Time Elapsed: " + tripTime+ " seconds");
        txtAmount.setText("Amount: $" + payment_amount);
        txtBalanceBefore.setText("Balance Before Ride: $" + currentBalance);
        txtBalanceAfter.setText("Balance After Ride: $" + df.format(newBalance));


        if (scooterID.equals("scooter003"))
        {
            result = getLatestLocation();
        }

        else
            updateCreditBalance();

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Main Menu
                Intent intent  = new Intent (RideSummaryScreen.this, MainScreen.class);
                startActivity(intent);
            }
        });
    }

    public static String MD5 (String toHash)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(toHash.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Result getLatestLocation()
    {
        String access_token2 = access_token;
        Log.d("ACCESS_TOKEN", "at" + access_token);
        String imeis = "359857081129744";

        Result res = new Result();

        if (access_token2 == null)
            return res;

        String app_secret = "3b6b744be34947288b4c6b3109212a80";
        String method = "jimi.device.location.get";
        String app_key = "8FB345B8693CCD0003C1DCAEFEA1FF4B";
        String sign_method = "md5";
        String v = "1.0";
        String format = "json";

        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        String timestamp = gmtDateFormat.format(new Date());

        String sign = MD5(app_secret + "access_token" + access_token2 + "app_key" + app_key + "format" + format + "imeis" + imeis + "method" +
                method + "sign_method" + sign_method + "timestamp" + timestamp + "v" + v + app_secret).toUpperCase();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://open.10000track.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JIMIAPI service = retrofit.create(JIMIAPI.class);
        service.coordinateResult(method, timestamp, app_key, sign, sign_method, v, format, imeis, access_token2).enqueue(new Callback<imeiLocation>() {
            @Override
            public void onResponse(Call<imeiLocation> call, retrofit2.Response<imeiLocation> response) {
                Log.d("ACCESS_TOKEN", "Code : " + response.code() + " | Message : " + response.message());
                if (response.body() != null)
                {
                    Log.d("ACCESS_TOKEN", "Latitude" + response.body().result[0].getLat()+ "Longitude" + response.body().result[0].getLng());
                    lat=response.body().result[0].getLat();
                    lng=response.body().result[0].getLng();
                    updateCreditBalance();
                }

            }

            @Override
            public void onFailure(Call<imeiLocation> call, Throwable t) {
                lat = -34.406437;
                lng = 150.879486;
                Log.d("ACCESS_TOKEN", ""+t.getMessage());
            }
        });


        return res;
    }

    //function that will update creditBalance, scooter status and insert into transaction table
    private void updateCreditBalance ()
    {
        paramHash = new HashMap<>();
        paramHash.put("userID", userID);
        paramHash.put("newBalance", String.valueOf(newBalance));
        paramHash.put("amount", String.valueOf(payment_amount));
        paramHash.put("scooterID", scooterID);
        paramHash.put("tripTime", tripTime);
        Log.d("ACCESS_TOKEN", "Lat" + lat+ "Lng" + lng);
        paramHash.put("lat", lat.toString());
        paramHash.put("lng", lng.toString());

        RequestQueue queue = Volley.newRequestQueue(RideSummaryScreen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("NO_ERROR", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_LOGS10", error.toString());
                error.printStackTrace();
            }
        })
        {
            // Ctrl + O

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (paramHash == null)
                {
                    return null;
                }

                //moving over all data from HashMap paramHash to Map params
                Map<String, String> params = new HashMap<>();
                for (String key:paramHash.keySet())
                {
                    params.put(key,paramHash.get(key));
                }
                return params;
            }

            // Ctrl + O

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        //add request to queue
        queue.add(stringRequest);
        Log.d("BEE_LOGS", stringRequest.toString());
    }
}
