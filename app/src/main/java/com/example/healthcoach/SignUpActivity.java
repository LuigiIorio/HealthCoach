package com.example.healthcoach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupEmailEditText;
    private EditText signupPasswordEditText;
    private EditText signupConfirmPasswordEditText;
    private EditText signupAgeEditText;
    private EditText signupGenderEditText;
    private EditText signupBirthdateEditText;
    private EditText signupWeightEditText;
    private EditText signupHeightEditText;
    private Button signupConfirmButton;
    private TextView signInTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        signupConfirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        signupAgeEditText = findViewById(R.id.signupAgeEditText);
        signupGenderEditText = findViewById(R.id.signupGenderEditText);
        signupBirthdateEditText = findViewById(R.id.signupBirthdateEditText);
        signupWeightEditText = findViewById(R.id.signupWeightEditText);
        signupHeightEditText = findViewById(R.id.signupHeightEditText);
        signupConfirmButton = findViewById(R.id.signupConfirmButton);
        signInTextView = findViewById(R.id.signInTextView);

        signupConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String confirmPassword = signupConfirmPasswordEditText.getText().toString();
                String age = signupAgeEditText.getText().toString();
                String gender = signupGenderEditText.getText().toString();
                String birthdate = signupBirthdateEditText.getText().toString();
                String weight = signupWeightEditText.getText().toString();
                String height = signupHeightEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
                    return; // Don't proceed with registration
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                // Navigate to HomeActivity
                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish(); // Close the SignUpActivity
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(SignUpActivity.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
