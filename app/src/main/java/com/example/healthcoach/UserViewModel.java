package com.example.healthcoach;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private LiveData<UserEntity> selectedUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<UserEntity> getSelectedUser(String userEmail) {
        if (selectedUser == null) {
            selectedUser = userRepository.getSelectedUser(userEmail);
        }
        return selectedUser;
    }
}
