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
import com.example.healthcoach.models.UserProfile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UidIdentifier;
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
                        activity.startActivity(intent, user.getValue().toBundle());
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
     * @param context
     */
    public void createUser(String email, String password, Context context) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfile profile = new UserProfile();
                        profile.setMail(email);
                        profile.setPassword(password);
                        profile.setUid(firebaseAuth.getUid());
                        user.setValue(profile);
                    } else {
                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT);
                    }
                });
    }

}