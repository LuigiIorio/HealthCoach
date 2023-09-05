package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class Height {

    private Context context;
    private FitnessOptions fitnessOptions;
    private DataSource heightDataSource;

    public Height(Context context) {
        this.context = context;
        setupHeight();
    }

    private void setupHeight() {
        // Create fitness options with the specific DataType and access type.
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        // Ensure the user has granted the necessary permissions.
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            Log.e("Height", "Necessary permissions not granted for height data.");
            // Handle permission request here or inform the user to grant permissions
            // For now, I'll just return from the method to avoid proceeding further.
            return;
        }

        // Set up the height data source.
        heightDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_HEIGHT)
                .setType(DataSource.TYPE_RAW)
                .build();
    }


    public void insertHeightData(float heightValue) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        // Create a data point for height.
        DataPoint dataPoint = DataPoint.builder(heightDataSource)
                .setField(Field.FIELD_HEIGHT, heightValue)
                .setTimeInterval(cal.getTimeInMillis(), cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();

        // Create a data set using the data point.
        DataSet dataSet = DataSet.builder(heightDataSource)
                .add(dataPoint)
                .build();

        // Insert the height data.
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .insertData(dataSet)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Height", "Height data inserted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Height", "Failed to insert height data.", e);
                });
    }
}
