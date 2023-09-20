package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.activities.HomeActivity;
import com.example.healthcoach.activities.SignUpInformationActivity;
import com.example.healthcoach.models.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SignUpViewModel extends ViewModel {

    private MutableLiveData<UserProfile> user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    public SignUpViewModel() {
        user = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    public LiveData<UserProfile> getUser() {

        if(user.getValue() == null) {
            retriveUserInformation();
        }

        return user;
    }

    /**
     * Fetches the user information from Firebase and updates the LiveData object.
     * Reads user information from the Firebase Realtime Database and sets it in the UserProfile object.
     */

    public void retriveUserInformation() {

        UserProfile profile = new UserProfile();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(firebaseUser.getUid()).child("password");

        profile.setMail(firebaseUser.getEmail());
        profile.setUid(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile.setPassword(snapshot.getValue(String.class));
                user.setValue(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Updates the user profile information and triggers the upload of the profile image to Firebase Storage.
     * Also navigates to the HomeActivity once the upload is successful.
     *
     * @param profile  The UserProfile object containing user details.
     * @param activity The current activity context.
     */

    public void setUser(UserProfile profile, Activity activity) {
        if(profile.getImage() == null || profile.getImage().isEmpty()) {
            Toast.makeText(activity, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersReference = firebaseDatabase.getReference("users");
        StorageReference imageReference = firebaseStorage.getReference("users");

        String uid = user.getValue().getUid();

        user.setValue(profile);

        usersReference.child(user.getValue().getUid()).setValue(user.getValue());

        StorageReference userImageRef = imageReference.child(uid + ".jpg");

        Uri imageUri = Uri.parse(user.getValue().getImage());

        userImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        user.getValue().setImage(imageUrl);
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    });
                })
                .addOnFailureListener(e -> {
                    setUser(profile, activity);
                });
    }

    /**
     * Creates a new user account with the given email and password via Firebase Authentication.
     * Upon successful creation, the user's profile is stored in Firebase Realtime Database and the user is redirected to SignUpInformationActivity.
     *
     * @param email    The email address for the new user.
     * @param password The password for the new user.
     * @param activity The current activity context.
     */

    public void createUser(String email, String password, Activity activity) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        UserProfile profile = new UserProfile();
                        profile.setMail(email);
                        profile.setPassword(password);
                        profile.setUid(firebaseAuth.getUid());
                        user.setValue(profile);

                        DatabaseReference usersReference = firebaseDatabase.getReference("users");

                        usersReference.child(user.getValue().getUid()).setValue(user.getValue())
                                .addOnCompleteListener(task1 -> {
                                    Intent intent = new Intent(activity, SignUpInformationActivity.class);
                                    activity.startActivity(intent);
                                    activity.finish();
                                });
                    } else {
                        String errorMessage = "Registration failed.";
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "The email address is already in use.";
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            FirebaseAuthInvalidCredentialsException e = (FirebaseAuthInvalidCredentialsException) exception;
                            String errorCode = e.getErrorCode();
                            if ("ERROR_INVALID_EMAIL".equals(errorCode)) {
                                errorMessage = "The email address is badly formatted.";
                            } else if ("ERROR_WEAK_PASSWORD".equals(errorCode)) {
                                errorMessage = "Password should be at least 6 characters.";
                            }
                        }
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}