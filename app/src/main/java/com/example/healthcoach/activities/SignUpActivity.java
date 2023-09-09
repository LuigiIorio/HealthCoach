package com.example.healthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.SignUpViewModel;



public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        EditText signupEmailEditText = findViewById(R.id.emailText);
        EditText signupPasswordEditText = findViewById(R.id.passwordText);
        Button signupConfirmButton = findViewById(R.id.signupConfirmButton);

        signupConfirmButton.setOnClickListener(view -> {
            String email = signupEmailEditText.getText().toString();
            String password = signupPasswordEditText.getText().toString();

            signUpViewModel.createUser(email, password);

            // Observing changes in userId to handle the UI reactions.
            signUpViewModel.getUserId().observe(SignUpActivity.this, userId -> {
                if (userId != null) {
                    if (userId.equals("email_in_use")) {
                        Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToNextScreen(); // Navigate to the appropriate screen
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateToNextScreen() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class); // Navigate to HomeActivity or desired activity
        startActivity(intent);
        finish(); // Close the current activity
    }
}
