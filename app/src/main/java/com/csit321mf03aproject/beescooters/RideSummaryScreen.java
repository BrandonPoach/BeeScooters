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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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

    //Dont allow user to press back button to Riding Screen
    @Override
    public void onBackPressed() {
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

    //function that will update creditBalance, scooter status and insert into transaction table
    private void updateCreditBalance ()
    {
        paramHash = new HashMap<>();
        paramHash.put("userID", userID);
        paramHash.put("newBalance", String.valueOf(newBalance));
        paramHash.put("amount", String.valueOf(payment_amount));
        paramHash.put("scooterID", scooterID);
        paramHash.put("tripTime", tripTime);
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
