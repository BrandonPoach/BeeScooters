package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HowToRide2Screen extends AppCompatActivity{

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoride2_screen);

        getSupportActionBar().setTitle("How To Ride");
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanCode();
            }
        });
    }

    public void openScanCode(){

        Intent intent = new Intent(this, ScanCode.class);
        startActivity(intent);

    }
}
