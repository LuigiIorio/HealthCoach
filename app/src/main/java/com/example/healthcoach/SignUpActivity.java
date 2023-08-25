package com.example.healthcoach;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.healthcoach.R;


public class SignUpActivity extends AppCompatActivity {

    private EditText signupEmailEditText;
    private EditText signupPasswordEditText;
    private EditText signupConfirmPasswordEditText;
    private Button signupConfirmButton;
    private TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        signupConfirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        signupConfirmButton = findViewById(R.id.signupConfirmButton);
        signInTextView = findViewById(R.id.signInTextView);

        signupConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input from EditText fields
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String confirmPassword = signupConfirmPasswordEditText.getText().toString();

                // TODO: Add your sign-up logic here

                // Display a toast message
                Toast.makeText(SignUpActivity.this, "Sign up clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Other code and initialization
    }

    // Other methods and code
}
