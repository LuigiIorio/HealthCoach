package com.example.healthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;




public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private Button googleLoginButton;


    private LoginActivityViewModel viewModel;

    private ActivityResultLauncher<Intent> googleSignInResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(LoginActivityViewModel.class);

        if(viewModel.checkSignInStatus(this)) {

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }

        inizialiseUI();

        setupListeners();

    }

    private void inizialiseUI() {

        emailEditText = findViewById(R.id.emailText);
        passwordEditText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        googleLoginButton = findViewById(R.id.googleLoginButton);

    }

    public void setupListeners() {

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.loginWithEmailAndPassword(email, password);
        });

        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        googleSignInResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> viewModel.handleGoogleSignInResult(LoginActivity.this, result.getData())
        );

        googleLoginButton.setOnClickListener(v -> {
            Intent signInIntent = viewModel.getGoogleSignInIntent();
            googleSignInResultLauncher.launch(signInIntent);
        });

        TextView forgotPasswordButton = findViewById(R.id.forgetPassword);
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        viewModel.getLoginResult().observe(this, loginResult -> {

            if (loginResult == LoginActivityViewModel.LoginResult.SUCCESS) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);

            } else if (loginResult == LoginActivityViewModel.LoginResult.FAILURE) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }

        });

    }
}
