package com.csit321mf03aproject.beescooters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.adapters.VaultedPaymentMethodsAdapter;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.HashMap;
import java.util.Map;

public class Payment_Screen extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    final String API_GET_TOKEN = "http://beescooters.net/admin/main.php";
    final String API_CHECK_OUT = "http://beescooters.net/admin/checkout.php";

    String token, amount;
    String tripTime; //testing
    HashMap<String, String> paramHash;
    float payment_amount;
    Button btn_pay;
    TextView payment_text;
    LinearLayout group_waiting;
    ConstraintLayout group_payment;
    SharedPreferences preferences;
    private String userID;
    private String scooterID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_screen);

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = preferences.getString("userID","ERROR getting User ID");

        //retrieving trip time from Riding screen
        Bundle trip_time = getIntent().getExtras();
        if(trip_time == null) {
            payment_amount = 0;
        } else {
            tripTime = String.valueOf(trip_time.getInt("TRIP_TIME"));
            Log.d("TRIP", ""+tripTime);

            scooterID = trip_time.getString("SCOOTER_ID");
            Log.d("SCOOTER_IDs", ""+scooterID);
            //payment_amount = Integer.valueOf(tripTime) / 100;
            payment_amount = (Float.valueOf(tripTime) / 100f);
            //set payment cost plan here later
        }

        group_payment = findViewById(R.id.payment_group);
        group_waiting = findViewById(R.id.waiting_group);

        btn_pay = (Button)findViewById(R.id.btn_pay);
        payment_text = findViewById(R.id.txt_amount);
        payment_text.setText(String.valueOf(payment_amount) + " AUD");
        //edt_amount = (EditText)findViewById(R.id.edt_amount);

        new getToken().execute();

        //Event
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPayment();
            }
        });
    }

    private void submitPayment() {
        //this codes displays the diff payment methods that the user can use to pay
        //https://github.com/braintree/braintree-android-drop-in
        DropInRequest dropInRequest = new DropInRequest().clientToken(token).vaultManager(true);
        startActivityForResult(dropInRequest.getIntent(Payment_Screen.this), REQUEST_CODE);


       /* DropInResult.fetchDropInResult(this, token, new DropInResult.DropInResultListener() {
            @Override
            public void onError(Exception exception) {
                // an error occurred
            }

            @Override
            public void onResult(DropInResult result) {
                if (result.getPaymentMethodType() != null) {
                    // use the icon and name to show in your UI
                    int icon = result.getPaymentMethodType().getDrawable();
                    int name = result.getPaymentMethodType().getLocalizedName();

                    PaymentMethodType paymentMethodType = result.getPaymentMethodType();
                    if (paymentMethodType == PaymentMethodType.ANDROID_PAY || paymentMethodType == PaymentMethodType.GOOGLE_PAYMENT) {
                        // The last payment method the user used was Android Pay or Google Pay.
                        // The Android/Google Pay flow will need to be performed by the
                        // user again at the time of checkout.
                    } else {
                        // Use the payment method show in your UI and charge the user
                        // at the time of checkout.
                        PaymentMethodNonce paymentMethod = result.getPaymentMethodNonce();
                    }
                } else {
                    Log.d("BEE_ERROR", "I'm inside here");

                    // there was no existing payment method
                }
            }
        });*/

    }



    //Ctrl + O


    //https://github.com/braintree/braintree-android-drop-in
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
                //user entered value

               //if (!edt_amount.getText().toString().isEmpty())
               //{
                   //assigned user entered values to variables
                   amount = Float.toString(payment_amount);
                   paramHash = new HashMap<>();
                   paramHash.put("amount", amount);
                   paramHash.put("nonce", strNonce);
                   paramHash.put("userID", userID);
                   paramHash.put("scooterID", scooterID);
                   paramHash.put("tripTime", tripTime);
                   sendPayments();
               //}
                //users did not enter a valid amount
               //else
               //{
               //    Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
               //}
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


    //https://github.com/braintree/braintree-android-drop-in

    //function to send payments to my server
    private void sendPayments() {
        RequestQueue queue = Volley.newRequestQueue(Payment_Screen.this);
        Log.d("BEE_LOG", "Testing1");
        //send request to checkout.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECK_OUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("BEE_LOG", response.toString());
                        //received approval
                        if (response.toString().contains("Successful")) {
                            Toast.makeText(Payment_Screen.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();

                            //return back to main screen
                            Intent intent = new Intent(Payment_Screen.this, MainScreen.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Payment_Screen.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("BEE_LOG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    private class getToken extends AsyncTask{

        ProgressDialog mDialog;

        //Ctrl+O

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog (Payment_Screen.this,android.R.style.Theme_DeviceDefault_Dialog);
            mDialog.setCancelable(false);
            mDialog.setMessage("Please wait");
            mDialog.show();
        }

        @Override
        protected Object doInBackground (Object[] objects){
            HttpClient client = new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Hide Group Waiting
                            group_waiting.setVisibility(View.GONE);
                            //Show Group Payment
                            group_payment.setVisibility(View.VISIBLE);

                            //set token
                            token = responseBody;
                        }
                    });
                }

                //couldn't connect to main.php to get token
                @Override
                public void failure(Exception exception) {
                    Log.d("BEE_ERROR", exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mDialog.dismiss();
        }
    }
}
