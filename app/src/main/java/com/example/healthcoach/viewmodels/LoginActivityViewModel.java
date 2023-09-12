package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;



public class LoginActivityViewModel extends AndroidViewModel {

    public static final int RC_SIGN_IN = 9001;
    private final Application application;
    private GoogleSignInAccount mAccount;
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
        initializeGoogleSignInClient(application);  // Initialize GoogleSignInClient
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount account) {
        this.mAccount = account;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return mAccount;
    }

    public void signInWithGoogle(Context context) {
        if (mGoogleSignInClient == null) {
            initializeGoogleSignInClient(context);
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public Intent getGoogleSignInIntent() {
        if (mGoogleSignInClient == null) {
            initializeGoogleSignInClient(application);
        }
        return mGoogleSignInClient.getSignInIntent();
    }

    private void initializeGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("99729341904-qlls8u6lhkf63fc4n68s2dvt9mnncpg2.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // Modified method
    public void handleGoogleSignInResult(Activity activity, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                // Call Firebase authentication method here
                firebaseAuthWithGoogle(account);
                subscribeToSteps(activity);
            } else {
                loginResult.setValue(LoginResult.FAILURE);
            }
        } catch (ApiException e) {
            e.printStackTrace();
            loginResult.setValue(LoginResult.FAILURE);
        }
    }


    /**
     * Esegue il login qualora email e password siano corrette
     * @param email
     * @param password
     */
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

    public void subscribeToSteps(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .build();

        Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(aVoid -> Log.i("MyApp", "Subscription was successful!"))
                .addOnFailureListener(e -> Log.w("MyApp", "There was a problem subscribing", e));
    }



    public enum LoginResult {
        SUCCESS, FAILURE
    }

    // New method to handle Firebase authentication with Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase Sign in success
                        loginResult.setValue(LoginResult.SUCCESS);
                    } else {
                        // Firebase Sign in failure
                        loginResult.setValue(LoginResult.FAILURE);
                    }
                });
    }

    /**
     * Controlla che l'account sia già connesso a Google.
     * Qualora sia connesso avvia la Home Activity e chiude la Login Activity
     * @param context
     */
    public boolean checkSignInStatus(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

}
