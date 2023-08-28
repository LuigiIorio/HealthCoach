package com.example.healthcoach;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<UserEntity>> allUsers;
    private LiveData<UserEntity> selectedUser;
    private ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(2); // Add this line

    public UserRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        userDao = appDatabase.userDao();
        allUsers = userDao.getAllUsers();
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }

    public void insert(UserEntity user) {
        databaseWriteExecutor.execute(() -> userDao.insert(user)); // Use databaseWriteExecutor
    }

    public LiveData<UserEntity> getSelectedUser(String email) {
        selectedUser = userDao.getSelectedUser(email);
        return selectedUser;
    }

    // Add more methods as needed
}
