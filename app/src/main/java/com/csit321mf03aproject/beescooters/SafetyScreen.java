package com.csit321mf03aproject.beescooters;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//just a static info screen
public class SafetyScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safety_screen);

        getSupportActionBar().setTitle("Safety");
    }
}
