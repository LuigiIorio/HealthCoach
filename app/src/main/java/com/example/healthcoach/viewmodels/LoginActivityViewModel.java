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

import com.example.healthcoach.activities.HomeActivity;
import com.example.healthcoach.activities.SignUpInformationActivity;
import com.example.healthcoach.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivityViewModel extends AndroidViewModel {

    public static final int RC_SIGN_IN = 9001;
    private final Application application;
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<FirebaseUser> userLiveData;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private GoogleSignInClient googleSignInClient;


    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userLiveData = new MutableLiveData<>();
        userLiveData.setValue(mAuth.getCurrentUser());
    }

    /**
     * Attempts to sign in the user using the provided email and password.
     * Updates the LiveData object 'loginResult' to either SUCCESS or FAILURE based on the result of the operation.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
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

    /**
     * Checks if the user is already signed in with Google.
     *
     * @param context The current activity context.
     * @return true if the user is already signed in, false otherwise.
     */

    public boolean checkSignInStatus(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void signInWithGoogle(AuthCredential credential, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(onCompleteListener);
    }
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }


    public void signOut() {

        mAuth.signOut();

    }


    /**
     * Generates and returns a Firebase AuthCredential object using Google's ID token.
     *
     * @param idToken The ID token from Google.
     * @return A Firebase AuthCredential object.
     */

    public AuthCredential getGoogleAuthCredential(String idToken) {
        return GoogleAuthProvider.getCredential(idToken, null);
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }


    /**
     * Initializes Google Sign-In client with the specified sign-in options.
     *
     * @param activity The current activity.
     * @param gso Google Sign-In options.
     */

    public void signInWithGoogle(Activity activity, GoogleSignInOptions gso) {

        googleSignInClient = GoogleSignIn.getClient(activity, gso);

    }


    /**
     * Authenticates the user with Firebase using the Google Sign-In credentials.
     * Checks the database to either navigate to HomeActivity or update the user profile.
     *
     * @param idToken The ID token from Google.
     * @param activity The current activity.
     */


    public void firebaseAuthViaGoogle(String idToken, Activity activity) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {

            if(task.isSuccessful()) {

                FirebaseUser user = mAuth.getCurrentUser();

                UserProfile profile = new UserProfile();
                profile.setUid(user.getUid());
                profile.setMail(user.getEmail());
                profile.setImage(user.getPhotoUrl().toString());

                checkDatabaseValues(activity, profile);

            }

            else {

                Toast.makeText(activity, "Non Loggato", Toast.LENGTH_SHORT).show();

            }

        });

    }

    /**
     * Checks if the user exists in the database and navigates to the appropriate activity.
     *
     * @param activity The current activity.
     */


    public void checkDatabaseValues(Activity activity) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Riferimento al nodo "users" nel database

        if(mAuth.getCurrentUser() == null)
            return;

        // Cerca l'utente nel database in base al suo ID
        databaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                } else {

                    Intent intent = new Intent(activity, SignUpInformationActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestione degli errori (opzionale)
            }
        });

    }


    /**
     * Checks if the user exists in the database, updates the user profile if needed,
     * and navigates to the appropriate activity.
     *
     * @param activity The current activity.
     * @param profile The user profile.
     */

    public void checkDatabaseValues(Activity activity, UserProfile profile) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Riferimento al nodo "users" nel database

        if(mAuth.getCurrentUser() == null)
            return;


        databaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                } else {

                    databaseReference.child(profile.getUid()).setValue(profile);

                    Intent intent = new Intent(activity, SignUpInformationActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gestione degli errori (opzionale)
            }
        });

    }

    public enum LoginResult {
        SUCCESS, FAILURE
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }


}
