package com.example.healthcoach.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetButton;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        // Observe toast messages
        viewModel.getToastMessage().observe(this, message -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());

        // Observe close activity event
        viewModel.getCloseActivityEvent().observe(this, close -> {
            if (close) {
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Enter your registered email", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.resetPassword(email);
                }
            }
        });
    }
}
