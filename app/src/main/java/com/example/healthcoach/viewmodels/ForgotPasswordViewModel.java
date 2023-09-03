package com.example.healthcoach.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordViewModel extends ViewModel {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeActivityEvent = new MutableLiveData<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getCloseActivityEvent() {
        return closeActivityEvent;
    }

    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toastMessage.setValue("Password reset email sent");
                            closeActivityEvent.setValue(true);
                        } else {
                            toastMessage.setValue("Failed to send password reset email");
                        }
                    }
                });
    }
}
