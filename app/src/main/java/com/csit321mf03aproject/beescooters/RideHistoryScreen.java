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
    String tempDate = "", tempDate2 = "";
    boolean doOnce = true;

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

                            if (doOnce)
                            {
                                tempDate2 = transactionObject.getString("transDate");
                                tempDate = tempDate2;
                                doOnce = false;
                            }

                            else
                                tempDate = transactionObject.getString("transDate");

                            //if previous date is different from current object date means need to create new date header
                            if (tempDate.equals(tempDate2))//(transactionObject.getString("transDate")))
                            {
                                ListItems listItems = new ListItems();
                                listItems.setObject(item);
                                listItems.setTAG(null);
                                lItems.add(listItems);

                                if (i == response.length()-1)
                                {
                                    ListItems listItem0 = new ListItems();
                                    //setTag getting value and setObject getting null means this will be a inflated date header
                                    listItem0.setTAG((tempDate2));
                                    listItem0.setObject(null);
                                    lItems.add(listItem0);
                                }
                            }
                            //current object date is the same as previous object date so can just add it under the same header
                            else if (!tempDate.equals(tempDate2))
                            {
                                ListItems listItem0 = new ListItems();
                                //setTag getting value and setObject getting null means this will be a inflated date header
                                listItem0.setTAG((tempDate2));
                                listItem0.setObject(null);
                                lItems.add(listItem0);
                                tempDate2 = tempDate;
                                ListItems listItem = new ListItems();
                                //setTag getting value and setObject getting null means this will be a inflated date header
                                listItem.setObject(item);
                                listItem.setTAG(null);
                                lItems.add(listItem);
                            }
                        }
                    }

                    List<ListItems> newList;
                    newList = reverseList(lItems);
                    mAdapter = new MyAdapter (newList, getApplicationContext());
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

    public static List<ListItems> reverseList (List<ListItems> unreversedList)
    {
        List <ListItems> tempList = new ArrayList<>();
        for (int i = unreversedList.size()-1; i>=0; i--)
        {
            tempList.add(unreversedList.get(i));
        }

        return tempList;
    }

}
