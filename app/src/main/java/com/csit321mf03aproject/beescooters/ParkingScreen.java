package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//just a normal static information screen
public class ParkingScreen extends AppCompatActivity{

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_screen);

        getSupportActionBar().setTitle("Safety");
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEndingTripScreen();
            }
        });
    }

    public void openEndingTripScreen(){

        Intent intent = new Intent(this, EndingTripScreen.class);
        startActivity(intent);

    }
}