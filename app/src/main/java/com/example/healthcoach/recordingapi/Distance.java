package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class Distance {
    private Context context;
    private GoogleSignInAccount account;
    private DataSource distanceDeltaDataSource;

    public Distance(Context context, GoogleSignInAccount account) {
        this.context = context;
        this.account = account;

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        distanceDeltaDataSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setStreamName("distanceDeltaStream")
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(context.getPackageName())
                .build();

        RecordingClient recordingClient = Fitness.getRecordingClient(context, account);

        recordingClient.subscribe(distanceDeltaDataSource)
                .addOnSuccessListener(aVoid -> Log.d("Distance", "Subscribed to distance delta"))
                .addOnFailureListener(e -> Log.e("Distance", "Failed to subscribe to distance delta", e));
    }

    public void readDistanceData(long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, account);

        // Check if today is the day we're interested in
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayStartTime = cal.getTimeInMillis();

        if (startTime == todayStartTime) {
            // Adjust the endTime to current time for today
            endTime = System.currentTimeMillis();
        }

        if (endTime > startTime) {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .enableServerQueries()
                    .build();

            historyClient.readData(readRequest)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Distance", "Failed to read distance data for the range", e);
                        }
                    });
        } else {
            Log.e("Distance", "Invalid time range specified");
        }
    }
}
