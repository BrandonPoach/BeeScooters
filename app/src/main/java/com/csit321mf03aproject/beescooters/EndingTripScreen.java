package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//just a normal static information screen
public class EndingTripScreen extends AppCompatActivity{

    private SharedPreferences preferences;
    private Button button;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endingtrip_screen);

        getSupportActionBar().setTitle("Safety");
        preferences = getSharedPreferences("MYPREFS",MODE_PRIVATE);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainScreen();
            }
        });
    }

    public void openMainScreen(){
        String flag = preferences.getString("flag","0");

        if(flag.equals("register")){
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
    }
}