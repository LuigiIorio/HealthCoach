package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class HeartPoints {

    private static final String HEART_POINTS_DATA_TYPE = "com.google.heart_points";

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    public HeartPoints(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for heart points
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_HEART_POINTS)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("HeartPointsRecorderStream") // Unique stream name
                .build();

        // Subscribe to the data source
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("HeartPoints", "Heart points recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("HeartPoints", "Failed to start heart points recording", e);
                    }
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
                        Log.d("HeartPoints", "Heart points recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("HeartPoints", "Failed to stop heart points recording", e);
                    }
                });
    }
}
