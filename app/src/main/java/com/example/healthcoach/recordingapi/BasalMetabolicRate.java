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

public class BasalMetabolicRate {

    private static final String BMR_DATA_TYPE = "com.google.basal_metabolic_rate";

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    public BasalMetabolicRate(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for basal metabolic rate
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("BasalMetabolicRateRecorderStream") // Unique stream name
                .build();

        // Subscribe to the data source
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("BasalMetabolicRate", "Basal metabolic rate recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("BasalMetabolicRate", "Failed to start basal metabolic rate recording", e);
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
                        Log.d("BasalMetabolicRate", "Basal metabolic rate recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("BasalMetabolicRate", "Failed to stop basal metabolic rate recording", e);
                    }
                });
    }
}
