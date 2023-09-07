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

public class DistanceDelta {

    private GoogleSignInAccount googleSignInAccount;
    private DataSource distanceDeltaDataSource;

    public DistanceDelta(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        distanceDeltaDataSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setStreamName("distanceDeltaStream")
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(context.getPackageName())
                .build();

        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        recordingClient.subscribe(distanceDeltaDataSource)
                .addOnSuccessListener(aVoid -> Log.d("DistanceDelta", "Subscribed to distance delta"))
                .addOnFailureListener(e -> Log.e("DistanceDelta", "Failed to subscribe to distance delta", e));
    }

    public void stopRecording(Context context) {
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
        recordingClient.unsubscribe(distanceDeltaDataSource)
                .addOnSuccessListener(aVoid -> Log.d("DistanceDelta", "Unsubscribed from distance delta"))
                .addOnFailureListener(e -> Log.e("DistanceDelta", "Failed to unsubscribe from distance delta", e));
    }
}