package com.csit321mf03aproject.beescooters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.HashMap;
import java.util.Map;

public class AddCreditScreen extends AppCompatActivity {

    TextView txtCurrentAmount;
    RadioGroup rgBalance;
    Button addCredit;
    private SharedPreferences.Editor editor;
    SharedPreferences preferences;
    String balance, amount, token, userID, customerID, firstName, lastName;
    Double dBalance, dAmount, newBalance;
    HashMap<String, String> paramHash;
    private static final int REQUEST_CODE = 1234;
    final String API_GET_TOKEN = "http://beescooters.net/admin/main.php";
    final String API_CHECK_OUT = "http://beescooters.net/admin/addCredit.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_credit_screen);

        getSupportActionBar().setTitle("Add Credit");

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = preferences.getString("userID", "ERROR getting User ID");
        balance = preferences.getString("creditBalance", "ERROR getting User balance");
        customerID = preferences.getString("customerID", "ERROR getting Customer ID");
        firstName = preferences.getString("userGivenName", "ERROR getting user given name");
        lastName = preferences.getString("userFamilyName", "ERROR getting user family name");
        dBalance = Double.valueOf(balance);

        editor = preferences.edit();

        txtCurrentAmount = findViewById(R.id.txtCurrentAmount);
        txtCurrentAmount.setText("$" + balance);

        rgBalance = findViewById(R.id.rgBalance);
        rgBalance.clearCheck();
        //txtAfterAmount = findViewById(R.id.txtAfterAmount);

        //radio button amount selections
        rgBalance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.rbBalance5:
                        dAmount = 5.0;
                        break;

                    case R.id.rbBalance10:
                        dAmount = 10.0;
                        break;

                    case R.id.rbBalance20:
                        dAmount = 20.0;
                        break;

                    case R.id.rbBalance30:
                        dAmount = 30.0;
                        break;

                    case R.id.rbBalance50:
                        dAmount = 50.0;
                        break;

                }

                amount = String.valueOf(dAmount);
                newBalance = dBalance + dAmount;
                txtCurrentAmount.setText("$" + (newBalance));
            }
        });

        getToken();

        addCredit = findViewById(R.id.button_addCredit);
        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });
    }

    //display Braintree Drop In Requests using client token
    private void submitPayment() {
        //this codes displays the diff payment methods that the user can use to pay
        //https://github.com/braintree/braintree-android-drop-in
        DropInRequest dropInRequest = new DropInRequest().clientToken(token).vaultManager(true);
        startActivityForResult(dropInRequest.getIntent(AddCreditScreen.this), REQUEST_CODE);
    }

    //handle the response from DopInRequest
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();   //receive nonce from Braintree server
                String strNonce = nonce.getNonce();  //nonce from Braintree server

                paramHash = new HashMap<>();
                paramHash.put("userID", userID);
                paramHash.put("newBalance", String.valueOf(newBalance));
                paramHash.put("amount", String.valueOf(amount));
                paramHash.put("nonce", strNonce);

                sendCredits();
            }

            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "User Cancel", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("BEE_ERROR", error.toString());
            }
        }
    }

    private void sendCredits() {
        RequestQueue queue = Volley.newRequestQueue(AddCreditScreen.this);

        //send request to addCredit.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECK_OUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //received approval
                        if (response.toString().contains("Successful")) {
                            Log.d("RESPONSE_BEES", ""+response);
                            String[] serverResponse = response.split("[,]");
                            String newBalance = serverResponse[1];
                            editor.putString("creditBalance", String.valueOf(newBalance));
                            editor.commit();
                            Toast.makeText(AddCreditScreen.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();

                            //return back to main screen
                            Intent intent = new Intent(AddCreditScreen.this, MainScreen.class);
                            startActivity(intent);

                        } else {
                            Log.d("RESPONSE_BEE", ""+response);
                            Toast.makeText(AddCreditScreen.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_LOGS10", error.toString());
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

    //function to get client token generated from our server
    private void getToken()
    {
        paramHash = new HashMap<>();
        paramHash.put("userID", userID);
        paramHash.put("customerID", customerID);
        paramHash.put("firstName", firstName);
        paramHash.put("lastName", lastName);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving Token from Server...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(AddCreditScreen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_GET_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("I_AM_HERE", response);
                //user does not customer ID used for saving previous payment info
                if (customerID.equals("null"))
                {
                    //will receive customer id from our server that will saved in Shared Preferences
                    String[] serverResponse = response.split("[,]");
                    String customerID = serverResponse[0];
                    token = serverResponse[1];
                    editor.putString("customerID", customerID);
                    editor.commit();
                }

                else
                {
                    //client already have Customer ID so so just receive token from server
                    token = response;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AddCreditScreen.this, "Error Retrieving Data from Database! Try Again Later.", Toast.LENGTH_SHORT).show();
                Log.d("BEE_LOGS12", error.toString());
                error.printStackTrace();
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
        Log.d("BEE_LOG10", stringRequest.toString());
    }

}
