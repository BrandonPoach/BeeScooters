package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//just a normal static information screen
public class HowToRideScreen extends AppCompatActivity{

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoride_screen);

        getSupportActionBar().setTitle("How To Ride");
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSafety2Screen();
            }
        });
    }

    public void openSafety2Screen(){
        Intent intent = new Intent(this, Safety2Screen.class);
        startActivity(intent);
    }
}