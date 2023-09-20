package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.viewmodels.LoginActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class StepCount {
    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;
    private Context context;

    public StepCount(Context context, GoogleSignInAccount account, FragmentActivity fragmentActivity) {
        this.googleSignInAccount = account;
        this.context = context;

        if (googleSignInAccount == null) {
            LoginActivityViewModel viewModel = new ViewModelProvider(fragmentActivity).get(LoginActivityViewModel.class);
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
    public void readStepsForRange(long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        // Check if today is the day we're interested in
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayStartTime = cal.getTimeInMillis();

        if (startTime == todayStartTime) {
            endTime = System.currentTimeMillis();
        }

        if (endTime > startTime) {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .enableServerQueries()
                    .build();

            historyClient.readData(readRequest)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("StepCount", "Failed to read step count for the range", e);
                        }
                    });
        } else {
            Log.e("StepCount", "Invalid time range specified");
        }
    }


}
