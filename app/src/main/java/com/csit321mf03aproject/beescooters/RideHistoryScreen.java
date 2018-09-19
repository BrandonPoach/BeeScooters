package com.csit321mf03aproject.beescooters;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private List <TransactionItem> transactionItem;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_history_screen);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        transactionItem = new ArrayList<>();

        getRecyclerViewData();

    }

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
                        TransactionItem item = new TransactionItem(
                                transactionObject.getString("transDate"),
                                transactionObject.getString("tripTime"),
                                transactionObject.getString("amount")
                        );
                        transactionItem.add(item);
                    }

                    mAdapter = new MyAdapter (transactionItem, getApplicationContext());
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
