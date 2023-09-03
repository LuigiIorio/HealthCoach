package com.example.healthcoach.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpViewModel extends ViewModel {

    private MutableLiveData<String> userId;
    private FirebaseAuth firebaseAuth;

    public SignUpViewModel() {
        userId = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userId.setValue(firebaseAuth.getCurrentUser().getUid());
                    } else {
                        // Note: You shouldn't use Toast here since ViewModel shouldn't know about UI components
                        // Instead, you might want to use some kind of LiveData or state representation to notify the view (Activity/Fragment) about the error
                    }
                });
    }
}
