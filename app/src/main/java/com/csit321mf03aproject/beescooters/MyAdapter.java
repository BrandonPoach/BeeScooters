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

        if (itemType == 0)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat s2 = new SimpleDateFormat("EEE dd MMM yyyy");
            
            try {
                holder.textDateHeader.setText(s2.format(simpleDateFormat.parse(tempLItems.getTag())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //String formattedDate = simpleDateFormat.format(date);
            //holder.textDateHeader.setText(date.toString());

        }

        else if (itemType == 1)
        {
            holder.textViewDate.setText(tempLItems.getObject().getDate());
            holder.textViewTripTime.setText("Trip Duration: " + tempLItems.getObject().getTripTime() + " seconds");
            holder.textViewAmount.setText("Amount: " + tempLItems.getObject().getAmount() + " AUD");
        }

    }

    @Override
    public int getItemCount() {
        return lItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView textViewDate, textViewTripTime, textViewAmount, textDateHeader;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            //link the view to variable

            if (viewType == 0)
            {
                textDateHeader = itemView.findViewById(R.id.dateHeader);

            }

            else if (viewType == 1) {
                textViewDate = itemView.findViewById(R.id.textViewDate);
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
            itemType = 0;
            return 0;
        }

        else
        {
            itemType = 1;
            return 1;
        }
    }
}
