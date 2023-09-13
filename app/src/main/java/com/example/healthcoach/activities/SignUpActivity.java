package com.example.healthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.SignUpViewModel;



public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;

    private EditText emailText, passwordText, confirmPasswordText;
    private Button confirm;
    private TextView alreadyRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        inizialiseUI();
        setupListeners();

    }

    private void inizialiseUI() {

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        confirmPasswordText = findViewById(R.id.confirmPasswordText);
        confirm = findViewById(R.id.signupConfirmButton);
        alreadyRegistered = findViewById(R.id.signInTextView);

    }

    private void setupListeners() {

        confirm.setOnClickListener(view -> {

            String email, password, confirmPassword;
            email = emailText.getText().toString().trim();
            password = passwordText.getText().toString();
            confirmPassword = confirmPasswordText.getText().toString();

            if(password.equals(confirmPassword)) {

                if(!email.isEmpty()) {
                    signUpViewModel.createUser(email, password, this);

                } else
                    Toast.makeText(this, "Please insert mail", Toast.LENGTH_SHORT).show();

            }

            else {

                Toast.makeText(this, "Password must be the same", Toast.LENGTH_SHORT).show();

            }

        });

        alreadyRegistered.setOnClickListener(view -> {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        });

    }

}
