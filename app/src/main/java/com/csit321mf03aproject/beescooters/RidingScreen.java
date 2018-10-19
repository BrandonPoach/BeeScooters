package com.csit321mf03aproject.beescooters;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RidingScreen extends AppCompatActivity {

    private static final String TAG = RidingScreen.class.getSimpleName();

    private TimerService timerService;
    private boolean serviceBound;

    private Button timerButton;
    private TextView timerTextView;
    private TextView infoText;

    // Handler to update the UI every second when the timer is running
    private final Handler mUpdateTimeHandler = new UIUpdateHandler(this);

    // Message type for the handler
    private final static int MSG_UPDATE_TIME = 0;

    private static String scooterID;
    private final  String URL = "http://beescooters.net/admin/setScooterStatus.php";
    HashMap<String, String> paramHash;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riding_screen);

        getSupportActionBar().setTitle("Ride");
        timerButton = findViewById(R.id.timer_button);
        timerTextView = findViewById(R.id.timer_text_view);
        infoText = findViewById(R.id.info_text);

        Bundle scooter_id = getIntent().getExtras();
        if(scooter_id == null) {
            scooterID = "scooter001";
        } else {
            scooterID = scooter_id.getString("SCOOTER_ID");
            Log.d("scooterID", ""+scooterID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting and binding service");
        }
        Intent i = new Intent(this, TimerService.class);
        startService(i);
        bindService(i, mConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //updateUIStopRun();
        if (serviceBound) {
            // If a timer is active, foreground the service, otherwise kill the service
            if (timerService.isTimerRunning()) {
                timerService.foreground();
            }
            else {
                stopService(new Intent(this, TimerService.class));
            }

            // Unbind the service
            unbindService(mConnection);
            serviceBound = false;
        }
    }

    //when Start Timer button is clicked
    public void runButtonClick(View v) {
        if (serviceBound && !timerService.isTimerRunning()) {

            //set scooter status to Used
            changeScooterStatus ();

            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Starting timer");
            }
            timerService.startTimer();
            updateUIStartRun();
        }
        else if (serviceBound && timerService.isTimerRunning()) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Stopping timer");
            }
            timerService.stopTimer();
            //updateUIStopRun();
        }
    }

    //Update UI when timer starts
    private void updateUIStartRun() {
        mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        timerButton.setText("LOCK");
        infoText.setText("Your current Trip Time....");
    }

//    private void updateUIStopRun() {
//        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
//        timerButton.setText("START");
//    }

    //Updates timer UI which displays in H : M : S
    private void updateUITimer() {
        if (serviceBound) {
            long secs = timerService.elapsedTime();
            int h = (int) (secs/60/60);
            secs -=  (h*60*60);
            int m = (int) secs / 60;
            secs -= (m *60);
            timerTextView.setText(h + " : " + m + " : " + secs);
        }
    }

    //Callback for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service bound");
            }
            TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;
            timerService = binder.getService();
            serviceBound = true;
            // Ensure the service is not in the foreground when bound
            timerService.background();
            // Update the UI if the service is already running the timer
            if (timerService.isTimerRunning()) {
                updateUIStartRun();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service disconnect");
            }
            serviceBound = false;
        }
    };

    //When timer is running, this handler updates the UI every second to show timer progress
    static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<RidingScreen> activity;

        UIUpdateHandler(RidingScreen activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "updating time");
                }
                activity.get().updateUITimer();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            }
        }
    }

    private void changeScooterStatus ()
    {
        paramHash = new HashMap<>();
        paramHash.put("scooterID", scooterID);

        RequestQueue queue = Volley.newRequestQueue(RidingScreen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("NO_ERROR", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_LOGS10", error.toString());
                error.printStackTrace();
            }
        })
        {
            // Ctrl + O

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (paramHash == null)
                {
                    return null;
                }

                //moving over all data from HashMap paramHash to Map params
                Map<String, String> params = new HashMap<>();
                for (String key:paramHash.keySet())
                {
                    params.put(key,paramHash.get(key));
                }
                return params;
            }

            // Ctrl + O

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        //add request to queue
        queue.add(stringRequest);
        Log.d("BEE_LOGS", stringRequest.toString());
    }

    /**
     * Timer service tracks the start and end time of timer; service can be placed into the
     * foreground to prevent it being killed when the activity goes away
     */
    public static class TimerService extends Service {

        private static final String TAG = TimerService.class.getSimpleName();

        // Start and end times in milliseconds
        private long startTime, endTime;

        // Is the service tracking time?
        private boolean isTimerRunning;

        // Foreground notification id
        private static final int NOTIFICATION_ID = 1;

        // Service binder
        private final IBinder serviceBinder = new RunServiceBinder();

        public class RunServiceBinder extends Binder {
            TimerService getService() {
                return TimerService.this;
            }
        }

        @Override
        public void onCreate() {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Creating service");
            }
            startTime = 0;
            endTime = 0;
            isTimerRunning = false;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Starting service");
            }
            return Service.START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Binding service");
            }
            return serviceBinder;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Destroying service");
            }
        }

        //Starts timer
        public void startTimer() {
            startTime = System.currentTimeMillis();
            isTimerRunning = true;
        }

        //stop timer
        public void stopTimer() {
            endTime = System.currentTimeMillis();
            isTimerRunning = false;

            int elapsedSeconds = (int)elapsedTime();
            //goto ride summary screen and pass trip time and scooter id
            Intent intent  = new Intent (this, RideSummaryScreen.class);
            intent.putExtra("TRIP_TIME", elapsedSeconds);
            intent.putExtra("SCOOTER_ID", scooterID);

            startActivity(intent);
        }

        //returns boolean value on whether the timer is running
        public boolean isTimerRunning() {
            return isTimerRunning;
        }

        //return elapsed time in seconds
        public long elapsedTime() {
            // If the timer is running, the end time will be zero
            return endTime > startTime ?
                    (endTime - startTime) / 1000 :
                    (System.currentTimeMillis() - startTime) / 1000;
        }

        //Place service in foreground
        public void foreground() {
            startForeground(NOTIFICATION_ID, createNotification());
        }

        //Bring back service to Background
        public void background() {
            stopForeground(true);
        }


        //create notification for the service
        private Notification createNotification() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                    .setContentTitle("Ride Started")
                    .setContentText("Tap to return to Ride")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent resultIntent = new Intent(this, RidingScreen.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(this, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            //code to make Notification work on Android Oreo and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Timer";
                String description = "Timer Working";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("123", name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            return builder.build();
        }
    }

}



