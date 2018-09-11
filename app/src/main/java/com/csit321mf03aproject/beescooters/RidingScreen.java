package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class RidingScreen extends AppCompatActivity {

    private Button startLockButton;
    private Chronometer chronometer;
    private boolean timerState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riding_screen);

        startLockButton = findViewById(R.id.start_lock_button);
        chronometer = findViewById(R.id.chronometer);
        //chronometer.setFormat("Time Elapsed: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        startLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clicking START
                if (!timerState)
                {
                    startLockButton.setText("LOCK");
                    startTimer();   //call function to start the timer
                }

                else    //clicking LOCK
                {
                    stopTimer();    //call function to stop timer
                }

            }
        });
    }

    public void startTimer()
    {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        timerState = true;
    }

    public void stopTimer()
    {
        chronometer.stop();
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = elapsedMillis/1000;

        int elapsedSeconds = (int)elapsedMillis;
        Toast.makeText(RidingScreen.this, "Elapsed seconds: " + elapsedSeconds,
                Toast.LENGTH_SHORT).show();


        //goto payment screen and pass the time elapsed
        Intent intent  = new Intent (this, CCInfoScreen.class);
        intent.putExtra("PAYMENT_VALUE", elapsedSeconds);
        startActivity(intent);
    }
}
