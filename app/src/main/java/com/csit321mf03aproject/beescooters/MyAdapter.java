package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//Adapter class that creates new views for the recycler list in Ride History
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItems> lItems;
    private Context context;
    private int itemType;

    public MyAdapter(List<ListItems> transactionItemList, Context context) {
        //get the list from RideHistoryScreen
        this.lItems = transactionItemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the views

        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.date_header, parent, false);

            return new ViewHolder(v, viewType);

        } else if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_item, parent, false);
            return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set the data

        ListItems tempLItems = lItems.get(position);

        //for date header
        if (itemType == 0)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat s2 = new SimpleDateFormat("EEE dd MMM yyyy");
            
            try {
                holder.textDateHeader.setText(s2.format(simpleDateFormat.parse(tempLItems.getTag())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //for normal records card view
        else if (itemType == 1)
        {
            int seconds = Integer.parseInt(tempLItems.getObject().getTripTime());

            int minutes = seconds / 60;
            seconds = seconds - (minutes*60);

            if (minutes > 0)
                holder.textViewTripTime.setText(minutes + " minutes " + seconds + " seconds");

            else
                holder.textViewTripTime.setText(seconds + " seconds");

            holder.textViewAmount.setText(tempLItems.getObject().getAmount() + " AUD");
        }
    }

    @Override
    public int getItemCount() {
        return lItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView textViewTripTime, textViewAmount, textDateHeader;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            //link the view to variable

            if (viewType == 0)
            {
                textDateHeader = itemView.findViewById(R.id.dateHeader);
            }

            else if (viewType == 1) {

                textViewTripTime = itemView.findViewById(R.id.textViewTripTime);
                textViewAmount = itemView.findViewById(R.id.textViewAmount);

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        ListItems tListItems = lItems.get(position);

        if(tListItems.getObject() == null)
        {
            //if object is null means create date header layout
            itemType = 0;
            return 0;
        }

        else
        {
            //itemType = 1 means create cardview record
            itemType = 1;
            return 1;
        }
    }

}
