package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class BodyFat {

    private Context context;
    private GoogleSignInAccount googleSignInAccount;

    public BodyFat(Context context, GoogleSignInAccount account) {
        this.context = context;
        this.googleSignInAccount = account;
    }

    public void insertBodyFatData(Context context, float bodyFatPercentage, long startTime, long endTime, OnSuccessListener<Void> onSuccessListener) {
        DataSource bodyFatSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet bodyFatDataSet = DataSet.create(bodyFatSource);
        DataPoint bodyFatPoint = bodyFatDataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        bodyFatPoint.getValue(Field.FIELD_PERCENTAGE).setFloat(bodyFatPercentage);
        bodyFatDataSet.add(bodyFatPoint);

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(bodyFatDataSet)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> {
                    Log.e("BodyFat", "Failed to insert body fat data", e);
                });
    }


    public void readLatestBodyFatForDay(Context context, GoogleSignInAccount googleSignInAccount, long startTime, long endTime, OnSuccessListener<Float> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    float latestBodyFat = 0;
                    long latestTime = 0;

                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            long pointEndTime = point.getEndTime(TimeUnit.MILLISECONDS);
                            float bodyFatValue = point.getValue(Field.FIELD_PERCENTAGE).asFloat();

                            if (pointEndTime > latestTime) {
                                latestTime = pointEndTime;
                                latestBodyFat = bodyFatValue;
                            }
                        }
                    }

                    Log.d("readLatestBodyFatForDay", "Latest body fat percentage: " + latestBodyFat);
                    onSuccessListener.onSuccess(latestBodyFat);
                })
                .addOnFailureListener(e -> {
                    Log.e("readLatestBodyFatForDay", "Failed to read body fat data", e);
                });
    }


    public void readBodyFatData(Context context, GoogleSignInAccount googleSignInAccount, long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    Log.d("BodyFat", "Data Read Success");
                    onSuccessListener.onSuccess(dataReadResponse);
                })
                .addOnFailureListener(e -> {
                    Log.e("BodyFat", "Failed to read body fat data", e);
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("BodyFat", "Task not successful. Exiting.");
                    }
                });
    }




}
