package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<TransactionItem> transactionItemList;
    private Context context;

    public MyAdapter(List<TransactionItem> transactionItemList, Context context)
    {
        this.transactionItemList = transactionItemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewtype)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionItem transactionItem = transactionItemList.get(position);
        holder.textViewDate.setText(transactionItem.getDate());
        holder.textViewTripTime.setText("Trip Duration: " + transactionItem.getTripTime() + " seconds");
        holder.textViewAmount.setText("Amount: " + transactionItem.getAmount() + " AUD");
    }

    @Override
    public int getItemCount() {
        return transactionItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewDate, textViewTripTime, textViewAmount;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTripTime = itemView.findViewById(R.id.textViewTripTime);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
        }
    }


}
