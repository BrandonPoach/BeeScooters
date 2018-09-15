package com.csit321mf03aproject.beescooters;

import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Chronometer;

public class ChronometerJobService extends JobService {

    private boolean jobCancelled = false;
    //private Chronometer chronometer;

    @Override
    public boolean onStartJob(JobParameters params) {
        //chronometer = findViewById (R.id.chronometer);
        runBackgroundChronometer(params);
        return true;
    }

    private void runBackgroundChronometer (final JobParameters params)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //chronometer.setFormat("MM:SS");
                //chronometer.setBase(SystemClock.elapsedRealtime());
                //chronometer.start();

            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //chronometer.stop();
        return true;
    }
}
