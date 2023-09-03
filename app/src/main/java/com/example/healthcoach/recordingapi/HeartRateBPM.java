package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.example.healthcoach.interfaces.HeartRateRepository;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;


public class HeartRateBPM implements HeartRateRepository {

    private GoogleSignInAccount googleSignInAccount;

    public HeartRateBPM(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);
    }

    @Override
    public void getLatestHeartRate(Context context, OnHeartRateFetchListener listener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (24 * 60 * 60 * 1000); // 24 hours ago

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(response -> {
                    float heartRate = 0;
                    for (DataSet dataSet : response.getDataSets()) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                heartRate = dp.getValue(field).asFloat();
                            }
                        }
                    }
                    listener.onSuccess(heartRate);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }

    public interface OnHeartRateFetchListener {
        void onSuccess(float bpm);
        void onFailure(String error);
    }
}
