package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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


public class MainActivityViewModel extends AndroidViewModel {

    public static final int RC_SIGN_IN = 9001;
    private final Application application;

    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void signInWithGoogle(Context context) {
        if (mGoogleSignInClient == null) {
            initializeGoogleSignInClient(context);
        }

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener((Activity) context, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                });
    }

    private void initializeGoogleSignInClient(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("99729341904-qlls8u6lhkf63fc4n68s2dvt9mnncpg2.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(Fitness.SCOPE_NUTRITION_READ_WRITE, Fitness.SCOPE_BODY_READ_WRITE)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Intent getGoogleSignInIntent() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("99729341904-qlls8u6lhkf63fc4n68s2dvt9mnncpg2.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(application, gso);
        return googleSignInClient.getSignInIntent();
    }

    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                FitnessOptions fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                        .build();

                if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                    loginResult.setValue(LoginResult.SUCCESS);
                } else {
                    loginResult.setValue(LoginResult.FAILURE);
                }
            } else {
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
                        loginResult.setValue(LoginResult.FAILURE);
                    }
                });
    }

    public enum LoginResult {
        SUCCESS, FAILURE
    }
}
