package com.example.healthcoach.recordingapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class Weight {

    private static final String WEIGHT_DATA_TYPE = "com.google.weight";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;

    public Weight(Context context) {
        if (!checkAndRequestPermissions(context)) {
            return;
        }

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for weight
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_WEIGHT)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("WeightRecorderStream")
                .build();

        // Subscribe to the data source
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Weight", "Weight recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Weight", "Failed to start weight recording", e);
                    }
                });
    }

    private boolean checkAndRequestPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.BODY_SENSORS")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{"android.permission.BODY_SENSORS"},
                    PERMISSION_REQUEST_CODE
            );
            return false;
        }
        return true;
    }

    public void stopRecording(Context context) {
        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Unsubscribe from the data source
        recordingClient.unsubscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Weight", "Weight recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Weight", "Failed to stop weight recording", e);
                    }
                });
    }
}