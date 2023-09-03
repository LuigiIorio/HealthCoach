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
import com.google.android.gms.common.api.ApiException;
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
        if (mGoogleSignInClient == null) {
// Configure Google Sign-In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("99729341904-on4htetl569e2ehr9n5rkprfsjcqouqr.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                // You can use account data to further authenticate with your backend or Firebase.
                // If using Firebase, you'd firebaseAuthWithGoogle(account.getIdToken());
                loginResult.setValue(LoginResult.SUCCESS);
            } else {
                // This indicates a configuration issue or refusal from the user.
                loginResult.setValue(LoginResult.FAILURE);
            }
        } catch (ApiException e) {
            // The GoogleSignInAccount object returned null, meaning the user did not sign in successfully.
            e.printStackTrace();

            // Logging the exact error can provide clarity
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
