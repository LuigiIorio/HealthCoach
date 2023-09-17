package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;


public class Calories {

    private static final String CALORIES_EXPENDED_DATA_TYPE = "com.google.calories.expended";

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    public Calories(Context context, GoogleSignInAccount account) {
        this.googleSignInAccount = account;
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build();

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for calories expended
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("CaloriesExpendedRecorderStream") // Unique stream name
                .build();

        // Subscribe to the data source
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Calories", "Calories expended recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Calories", "Failed to start calories expended recording", e);
                    }
                });
    }


    public void readCaloriesData(Context context, GoogleSignInAccount googleSignInAccount, long startTime, long endTime, OnSuccessListener<DataReadResponse> listener) {
        Log.d("Calories", "Start Time: " + startTime + ", End Time: " + endTime);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    Log.d("Calories", "Successfully read data.");
                    listener.onSuccess(response);
                })
                .addOnFailureListener(e -> {
                    Log.e("Calories", "Failed to read data", e);
                    Log.e("Calories", "Error Message: " + e.getMessage());
                });
    }





    public void stopRecording(Context context) {
        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Unsubscribe from the data source
        recordingClient.unsubscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Calories", "Calories expended recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Calories", "Failed to stop calories expended recording", e);
                    }
                });
    }
}
