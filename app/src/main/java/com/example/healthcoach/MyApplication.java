package com.example.healthcoach;

import android.app.Application;
import androidx.room.Room;

public class MyApplication extends Application {

    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "my-database-name")
                .build();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
