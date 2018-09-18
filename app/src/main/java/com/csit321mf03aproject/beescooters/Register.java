package com.csit321mf03aproject.beescooters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register  extends AppCompatActivity {
    EditText etUsername, etFirstName, etLastName, etEmail, etAddress, etPassword;
    String username, firstName, lastName, email, address, password;
    Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);
        etUsername = findViewById(R.id.etNewUsername);
        etFirstName = findViewById(R.id.etNewFirstName);
        etLastName = findViewById(R.id.etNewLastName);
        etEmail = findViewById(R.id.etNewEmail);
        etAddress = findViewById(R.id.etNewAddress);
        etPassword = findViewById(R.id.etNewPassword);
        btnRegister = findViewById(R.id.btnNewRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                email = etEmail.getText().toString();
                address = etAddress.getText().toString();
                password = etPassword.getText().toString();
                String task = "register";
                BackgroundTask backgroundTask = new BackgroundTask(Register.this);
                backgroundTask.execute(task,username, firstName, lastName, email, address, password);
                finish();
            }
        });


    }
}

