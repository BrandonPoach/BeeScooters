package com.csit321mf03aproject.beescooters;

import java.util.Date;

public class TransactionItem {

    private String date, tripTime, amount;

    public TransactionItem (String date, String tripTime, String amount)
    {
        this.date = date;
        this.tripTime = tripTime;
        this.amount = amount;
    }

    public String getDate() { return date; }
    public String getTripTime () { return tripTime; }
    public String getAmount () { return amount; }
}
