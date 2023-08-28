package com.example.healthcoach;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity user);

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    // Add more query methods as needed
}

