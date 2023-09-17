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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;

public class Distance {

    private GoogleSignInAccount googleSignInAccount;
    private DataSource distanceDeltaDataSource;

    public Distance(Context context, GoogleSignInAccount account) {
        this.googleSignInAccount = account;
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        distanceDeltaDataSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setStreamName("distanceDeltaStream")
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(context.getPackageName())
                .build();

        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        recordingClient.subscribe(distanceDeltaDataSource)
                .addOnSuccessListener(aVoid -> Log.d("Distance", "Subscribed to distance delta"))
                .addOnFailureListener(e -> Log.e("Distance", "Failed to subscribe to distance delta", e));
    }


    public void readDistanceData(Context context, long startTime, long endTime, OnSuccessListener<DataReadResponse> listener) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_DISTANCE_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(listener)
                .addOnFailureListener(e -> Log.e("Distance", "Failed to read data", e));
    }




    public void stopRecording(Context context) {
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
        recordingClient.unsubscribe(distanceDeltaDataSource)
                .addOnSuccessListener(aVoid -> Log.d("Distance", "Unsubscribed from distance delta"))
                .addOnFailureListener(e -> Log.e("Distance", "Failed to unsubscribe from distance delta", e));
    }
}