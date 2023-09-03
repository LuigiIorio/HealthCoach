package com.example.healthcoach.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.SignUpViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        EditText signupEmailEditText = findViewById(R.id.signupEmailEditText);
        EditText signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        Button signupConfirmButton = findViewById(R.id.signupConfirmButton);

        signupConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();

                signUpViewModel.createUser(email, password);

                // Observing changes in userId to handle the UI reactions.
                signUpViewModel.getUserId().observe(SignUpActivity.this, userId -> {
                    if (userId != null) {
                        // Registration successful. Navigate or show success.
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
