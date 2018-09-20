package com.csit321mf03aproject.beescooters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginScreen extends AppCompatActivity  {
    Button btnRegister, btnLogin;
    EditText etEmail,etPassword;
    String stringEmail,stringPassword;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        preferences = getSharedPreferences("MYPREFS",MODE_PRIVATE);

        if (preferences.getString("email", "") != ""){
            Intent intent = new Intent(this,LoggingIn.class);
            startActivity(intent);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringEmail = etEmail.getText().toString();
                stringPassword = etPassword.getText().toString();
                String task = "login";
                BackgroundTask backgroundTask = new BackgroundTask(LoginScreen.this);

                etEmail.setText("");
                etPassword.setText("");

                //execute the task
                //passes the paras to the backgroundTask (param[0],param[1],param[2])
                backgroundTask.execute(task,stringEmail,stringPassword);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this,Register.class);
                startActivity(intent);
            }
        });
    }
}

