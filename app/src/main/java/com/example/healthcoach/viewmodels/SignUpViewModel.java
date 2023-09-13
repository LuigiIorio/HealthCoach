package com.example.healthcoach.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.activities.HomeActivity;
import com.example.healthcoach.activities.SignUpInformationActivity;
import com.example.healthcoach.models.UserProfile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
            UserProfile profile = new UserProfile();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            profile.setMail(firebaseUser.getEmail());
            profile.setUid(firebaseUser.getUid());

            this.user.setValue(profile);
        }

        return user;
    }

    public void setUser(UserProfile profile, Activity activity) {

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
     * Crea uno user su Firebase e ne salva i dati nella LiveData user
     *
     * @param email
     * @param password
     * @param activity
     */
    public void createUser(String email, String password, Activity activity) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfile profile = new UserProfile();
                        profile.setMail(email);
                        profile.setPassword(password);
                        profile.setUid(firebaseAuth.getUid());
                        user.setValue(profile);

                        Intent intent = new Intent(activity, SignUpInformationActivity.class);
                        activity.startActivity(intent, new Bundle(profile.toBundle()));
                        activity.finish();

                    } else {
                        // La registrazione ha fallito. Gestisci l'errore.
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            // Indirizzo email già in uso.
                            // Mostra un messaggio Toast all'utente.
                            Toast.makeText(activity, "The email address is already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}