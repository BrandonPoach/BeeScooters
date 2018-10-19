package com.csit321mf03aproject.beescooters;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//just a static info screen
public class ScanCode extends AppCompatActivity{

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanqrcode);

        getSupportActionBar().setTitle("How To Ride");
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHowToRideScreen();
            }
        });
    }

    public void openHowToRideScreen(){

        Intent intent = new Intent(this, HowToRideScreen.class);
        startActivity(intent);

    }
}