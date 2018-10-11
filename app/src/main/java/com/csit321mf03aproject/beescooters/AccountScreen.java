package com.csit321mf03aproject.beescooters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountScreen extends AppCompatActivity {

    private static final String URL = "http://beescooters.net/admin/getUserData.php";
    RequestQueue requestQueue;
    HashMap<String, String> paramHash;
    SharedPreferences preferences;
    private String userID;

    TextView txtFullName;
    TextView txtEmail;
    TextView txtUsername;
    TextView txtAddress;
    TextView txtMemberSince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.user_account_screen);

        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        txtAddress = findViewById(R.id.txtAddress);
        txtMemberSince = findViewById(R.id.txtMemberSince);

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = preferences.getString("userID","ERROR getting User ID");
        Log.d("USER_ID", ""+userID);

        getUserData();

    }

    private void getUserData ()
    {
        paramHash = new HashMap<>();
        paramHash.put("userID", userID);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving data...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(AccountScreen.this);

        //send request to checkout.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            progressDialog.dismiss();

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i<jsonArray.length(); i++) {

                                JSONObject userObject = jsonArray.getJSONObject(i);
                                txtEmail.setText(userObject.getString("email"));
                                txtUsername.setText(userObject.getString("username"));
                                String fullName = userObject.getString("userGivenName") + " " + userObject.getString("userFamilyName");
                                txtFullName.setText(fullName);
                                txtAddress.setText(userObject.getString("address"));
                                txtMemberSince.setText(userObject.getString("registerDate"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AccountScreen.this, "Error Retrieving Data from Database! Try Again Later.", Toast.LENGTH_SHORT).show();
                Log.d("BEE_LOGS4", error.toString());
            }
        })
        {
            // Ctrl + O

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // if somehow paramHash didint contain the arguments taken from dropinrequest
                if (paramHash == null)
                {
                    return null;
                }

                //moving over all data from HashMap paramHash to Map params
                Map<String, String> params = new HashMap<>();
                for (String key:paramHash.keySet())
                {
                    params.put(key,paramHash.get(key));
                    Log.d("BEE_LOGS5", "here");
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
        Log.d("BEE_LOG", stringRequest.toString());
    }
}
