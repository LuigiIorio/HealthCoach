package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.viewmodels.MainActivityViewModel;
import com.example.healthcoach.viewmodels.StepViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;



public class StepCountDelta {
    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;



    public StepCountDelta(Context context, GoogleSignInAccount account, FragmentActivity fragmentActivity) {
        this.googleSignInAccount = account;

        if (googleSignInAccount == null) {
            MainActivityViewModel viewModel = new ViewModelProvider(fragmentActivity).get(MainActivityViewModel.class);
            viewModel.checkSignInStatus(context);
        }

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        // Create a Fitness recording client
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        // Create a DataSource for steps
        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_RAW)
                .setStreamName("StepCountDeltaStream")
                .build();
    }

    public void startRecording(Context context) {
        // Subscribe to the data source
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
        recordingClient.subscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("StepCountDelta", "Step count recording started");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("StepCountDelta", "Failed to start step count recording", e);
                    }
                });
    }

    public void stopRecording(Context context) {
        // Unsubscribe from the data source
        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
        recordingClient.unsubscribe(dataSource)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("StepCountDelta", "Step count recording stopped");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("StepCountDelta", "Failed to stop step count recording", e);
                    }
                });
    }

    public void readTodaySteps(Context context, FragmentActivity activity) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 86400000;  // 24 hours in milliseconds

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        int totalSteps = 0;
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                for (Field field : dataPoint.getDataType().getFields()) {
                                    int steps = dataPoint.getValue(field).asInt();
                                    totalSteps += steps;
                                }
                            }
                        }
                        // Update ViewModel
                        StepViewModel stepViewModel = new ViewModelProvider(activity).get(StepViewModel.class);
                        stepViewModel.setSteps(totalSteps);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("StepCountDelta", "Failed to read step count", e);
                    }
                });
    }
}
