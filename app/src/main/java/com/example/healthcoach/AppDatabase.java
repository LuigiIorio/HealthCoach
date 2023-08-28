package com.example.healthcoach;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false) // Add exportSchema = false to prevent warnings
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    // Add a static reference to the database instance
    private static AppDatabase instance;

    // Method to get the database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "healthcoach_db")
                    .fallbackToDestructiveMigration() // This migrates by destroying the old database
                    .build();
        }
        return instance;
    }
}