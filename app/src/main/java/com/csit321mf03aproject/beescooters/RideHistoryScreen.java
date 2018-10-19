package com.csit321mf03aproject.beescooters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

public class RideHistoryScreen extends AppCompatActivity {

    private static final String URL = "http://beescooters.net/admin/getTransactions.php";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List <ListItems> lItems;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    private String userID;
    String tempDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_history_screen);

        getSupportActionBar().setTitle("Ride History");
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        lItems = new ArrayList<>();
        sharedPreferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "Error Getting User ID!");

        getRecyclerViewData();

    }

    //function to get transaction history data from database
    private void getRecyclerViewData ()
    {
        requestQueue = Volley.newRequestQueue(this);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving data...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    progressDialog.dismiss();
                    for (int i = 0; i<response.length(); i++)
                    {
                        JSONObject transactionObject = response.getJSONObject(i);
                        Log.d("TEMP", ""+userID);
                        //only get ID of current user
                        if (transactionObject.getString("userID").equals(userID))
                        {
                            TransactionItem item = new TransactionItem(
                                    transactionObject.getString("transDate"),
                                    transactionObject.getString("tripTime"),
                                    transactionObject.getString("amount"));
                            Log.d("TEMP2", ""+transactionObject.getString("userID"));

                            //if previous date is different from current object date means need to create new date header
                            if (!tempDate.equals(transactionObject.getString("transDate")))
                            {
                                tempDate = item.getDate();
                                ListItems listItem = new ListItems();
                                //setTag getting value and setObject getting null means this will be a inflated date header
                                listItem.setTAG((tempDate));
                                listItem.setObject(null);
                                lItems.add(listItem);

                                //setTag getting null and setObject getting object means this will be an inflated cardview
                                ListItems listItem2 = new ListItems();
                                listItem2.setObject(item);
                                listItem2.setTAG(null);
                                lItems.add(listItem2);

                                Log.d("RH1", ""+tempDate);
                                Log.d("RH3", ""+transactionObject.getString("transDate"));
                            }
                            //current object date is the same as previous object date so can just add it under the same header
                            else
                            {
                                tempDate = item.getDate();
                                ListItems listItems = new ListItems();
                                listItems.setObject(item);
                                listItems.setTAG(null);
                                lItems.add(listItems);
                                Log.d("RH2", "RH2");
                            }

                        }
                    }

                    mAdapter = new MyAdapter (lItems, getApplicationContext());
                    mRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    Log.d("BEE_ERROR2", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_ERROR3", error.toString());
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }


}
