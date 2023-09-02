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

public class ActivitySegment {

    private static final String ACTIVITY_SEGMENT_DATA_TYPE = "com.google.activity.segment";

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    public ActivitySegment(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for activity segments
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("ActivitySegmentRecorderStream") // Unique stream name
                .build();

        // Subscribe to the data source
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ActivitySegment", "Activity segment recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ActivitySegment", "Failed to start activity segment recording", e);
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
                        Log.d("ActivitySegment", "Activity segment recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ActivitySegment", "Failed to stop activity segment recording", e);
                    }
                });
    }
}
