package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class LoggingIn extends AppCompatActivity {
    TextView name,email, userid;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged_in);

        email = (TextView) findViewById(R.id.textEmail);
        name = (TextView) findViewById(R.id.textName);
        userid = findViewById(R.id.textUserID);
        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);

        String mName = preferences.getString("name","ERROR getting name");
        String mEmail = preferences.getString("email","ERROR getting email");
        String mUserID = preferences.getString("userID","ERROR getting User ID");
        name.setText(mName);
        email.setText(mEmail);
        userid.setText(mUserID);
        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
