package com.csit321mf03aproject.beescooters;
//DATE or DATA
public class ListItems {
    private String TAG;
    TransactionItem TransactionObject;


    public String getTag() { return TAG; }
    public TransactionItem getObject() { return TransactionObject; }

    public void setTAG (String date) { TAG = date; }
    public void setObject (TransactionItem tObject) { TransactionObject = tObject; }

}
