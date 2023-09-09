package com.example.healthcoach.viewmodels;



import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.healthcoach.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;


public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> welcomeMessage = new MutableLiveData<>();
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

    public HomeViewModel() {
        welcomeMessage.setValue("Welcome to HealthCoach!");
        // Optionally, initialize a default UserProfile here if necessary
    }

    // Methods related to welcomeMessage
    public LiveData<String> getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String message) {
        welcomeMessage.setValue(message);
    }

    // Methods related to UserProfile
    public LiveData<UserProfile> getUserProfile() {
        return userProfileLiveData;
    }

    public void setUserProfile(UserProfile profile) {
        userProfileLiveData.setValue(profile);
    }

    public void setGender(String gender) {
        UserProfile currentProfile = userProfileLiveData.getValue();
        if (currentProfile != null) {
            currentProfile.setGender(gender);
            userProfileLiveData.setValue(currentProfile);
        }
    }

    public void setAge(int age) {
        UserProfile currentProfile = userProfileLiveData.getValue();
        if (currentProfile != null) {
            currentProfile.setAge(age);
            userProfileLiveData.setValue(currentProfile);
        }
    }

    public LiveData<String> getGender() {
        UserProfile currentProfile = userProfileLiveData.getValue();
        if (currentProfile != null) {
            return new MutableLiveData<>(currentProfile.getGender());
        }
        return new MutableLiveData<>("");
    }

    public LiveData<Integer> getAge() {
        UserProfile currentProfile = userProfileLiveData.getValue();
        if (currentProfile != null) {
            return new MutableLiveData<>(currentProfile.getAge());
        }
        return new MutableLiveData<>(0);
    }

    public void signOut(Context context, OnCompleteListener<Void> onCompleteListener) {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(
                context,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut().addOnCompleteListener(onCompleteListener);
    }


    // You can continue adding more methods as needed for your business logic
}
