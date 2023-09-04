package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivityViewModel extends ViewModel {

    public static final int RC_SIGN_IN = 9001;

    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public MainActivityViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }



    public void signInWithGoogle(Context context) {
        // If mGoogleSignInClient is not already initialized, do so
        if (mGoogleSignInClient == null) {
            initializeGoogleSignInClient(context);
        }

        // Ensure we're not already signed in, to force re-authentication and permission request
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener((Activity) context, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                });
    }

    private void initializeGoogleSignInClient(Context context) {
        // Specify fitness options for hydration data
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
                .build();

        // Configure Google Sign-In to request hydration data
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("99729341904-qlls8u6lhkf63fc4n68s2dvt9mnncpg2.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(Fitness.SCOPE_NUTRITION_READ_WRITE)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                FitnessOptions fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
                        .build();

                if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                    // The user granted Google Fit permissions
                    loginResult.setValue(LoginResult.SUCCESS);
                } else {
                    // The user didn't grant Google Fit permissions
                    loginResult.setValue(LoginResult.FAILURE);
                }
            } else {
                // Account was null
                loginResult.setValue(LoginResult.FAILURE);
            }
        } catch (ApiException e) {
            e.printStackTrace();
            Log.e("GoogleSignIn", "Google sign in failed", e);
            loginResult.setValue(LoginResult.FAILURE);
        }
    }


    public void loginWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginResult.setValue(LoginResult.SUCCESS);
                    } else {
                        // You can retrieve the exception and show a more specific error message to the user
                        loginResult.setValue(LoginResult.FAILURE);
                    }
                });
    }

    public enum LoginResult {
        SUCCESS, FAILURE
    }
}
