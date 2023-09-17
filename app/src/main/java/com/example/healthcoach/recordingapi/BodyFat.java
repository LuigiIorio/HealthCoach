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

    public void insertBodyFatData(Context context, float bodyFatPercentage, OnSuccessListener<Void> onSuccessListener) {
        DataSource bodyFatSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet bodyFatDataSet = DataSet.create(bodyFatSource);
        DataPoint bodyFatPoint = bodyFatDataSet.createDataPoint()
                .setTimeInterval(System.currentTimeMillis(), System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        bodyFatPoint.getValue(Field.FIELD_PERCENTAGE).setFloat(bodyFatPercentage);
        bodyFatDataSet.add(bodyFatPoint);

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(bodyFatDataSet)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> {
                    Log.e("BodyFat", "Failed to insert body fat data", e);
                });
    }



    public void readBodyFatData(Context context, GoogleSignInAccount googleSignInAccount, long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_BODY_FAT_PERCENTAGE, DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        historyClient.readData(readRequest)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> {
                    Log.e("BodyFat", "Failed to read body fat data", e);
                });
    }
}
