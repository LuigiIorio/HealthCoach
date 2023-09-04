package com.example.healthcoach.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.healthcoach.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private Button googleLoginButton;

    private MainActivityViewModel viewModel;

    // Moved ActivityResultLauncher initialization inside onCreate method
    private ActivityResultLauncher<Intent> googleSignInResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainActivityViewModel.class);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        googleLoginButton = findViewById(R.id.googleLoginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
                return; // Don't proceed with login
            }

            viewModel.loginWithEmailAndPassword(email, password);
        });

        signUpTextView.setOnClickListener(v -> {
            // Navigate to the sign-up activity.
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Register the ActivityResultLauncher for Google Sign In
        googleSignInResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> viewModel.handleGoogleSignInResult(result.getData())
        );

        googleLoginButton.setOnClickListener(v -> {
            Intent signInIntent = viewModel.getGoogleSignInIntent(); // Assuming your ViewModel can provide the Google SignIn Intent
            googleSignInResultLauncher.launch(signInIntent);
        });

        // New code for "Forgot Password?" button
        Button forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        forgotPasswordButton.setOnClickListener(v -> {
            // Start the ForgotPasswordActivity
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Observe login result from ViewModel
        viewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == MainActivityViewModel.LoginResult.SUCCESS) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to the HomeActivity
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            } else if (loginResult == MainActivityViewModel.LoginResult.FAILURE) {
                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Removed the onActivityResult method as it's no longer needed with the new approach
}
