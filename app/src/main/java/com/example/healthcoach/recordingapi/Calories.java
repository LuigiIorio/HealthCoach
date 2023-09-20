package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;



public class Calories {
    private Context context;
    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    private float totalCalories;


    /**
     * Initializes the Calories object and sets up Google Fit recording for calories expended.
     * Configures the DataSource and subscribes to Google Fit's RecordingClient for tracking calories.
     *
     * @param context The application's context.
     * @param account The GoogleSignInAccount for authentication.
     */
    public Calories(Context context, GoogleSignInAccount account) {
        this.context = context;
        this.googleSignInAccount = account;
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
                .build();

        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("CaloriesExpendedRecorderStream")
                .build();

        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(aVoid -> Log.d("Calories", "Calories expended recording started"))
                .addOnFailureListener(e -> Log.e("Calories", "Failed to start calories expended recording", e));
    }



    /**
     * Reads the calories data from Google Fit for a specified time range.
     * Aggregates the data by day and returns the results through an OnSuccessListener.
     *
     * @param startTime The start time of the range in milliseconds.
     * @param endTime The end time of the range in milliseconds.
     * @param onSuccessListener The listener to be called upon successful retrieval of the calories data.
     */

    public void readCaloriesData(long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> Log.e("Calories", "Failed to read calories data for the range", e));
    }
    public Calories(float totalCalories) {
        this.totalCalories = totalCalories;
    }
    public float getTotalCalories() {
        return totalCalories;
    }

}
