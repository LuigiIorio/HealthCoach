package com.example.healthcoach.activities;

import static com.example.healthcoach.viewmodels.LoginActivityViewModel.RC_SIGN_IN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private Button googleLoginButton;

    private static final int REQUEST_CODE_GOOGLE_FIT_PERMISSIONS = 1;

    private LoginActivityViewModel viewModel;

    private ActivityResultLauncher<Intent> googleSignInResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this.getApplication());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(LoginActivityViewModel.class);

        if(viewModel.checkSignInStatus(this))
            viewModel.checkDatabaseValues(this);

        viewModel.getUserLiveData().observe(this, firebaseUser -> {

            if(firebaseUser != null) {

                viewModel.checkDatabaseValues(this);

            }

        });

        inizialiseUI();

        setupListeners();

    }

    public void requestGoogleFitPermission() {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                this,
                REQUEST_CODE_GOOGLE_FIT_PERMISSIONS,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GOOGLE_FIT_PERMISSIONS) {
            if (resultCode == Activity.RESULT_OK) {
                // Permission granted
            } else {
                // Permission not granted
            }
            return;
        }

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = viewModel.getGoogleAuthCredential(account.getIdToken());

                viewModel.signInWithGoogle(credential, task1 -> {
                    if (task1.isSuccessful()) {
                        // Login with Google succeeded
                        if (viewModel.isUserLoggedIn()) {
                            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                            viewModel.firebaseAuthViaGoogle(account.getIdToken(), this);

                            // Request Google Fit permissions here
                            requestGoogleFitPermission();
                        }
                    } else {
                        // Handle login failure with Google
                        Toast.makeText(this, "Not Logged In", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                // Handle login error with Google
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
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

        TextView forgotPasswordButton = findViewById(R.id.forgetPassword);
        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        googleLoginButton.setOnClickListener(view -> {
            signInWithGoogle();
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

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
