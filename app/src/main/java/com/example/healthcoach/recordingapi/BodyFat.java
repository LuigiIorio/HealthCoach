package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
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



public class BodyFat {

    public BodyFat() {
        // Default constructor code
    }

    public BodyFat(FragmentActivity activity) {
        // Constructor with FragmentActivity
    }



    public void insertBodyFatData(Context context, float bodyFatPercentage) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account == null) {
            Log.w("BodyFat", "Not signed in, skipping data insert");
            return;
        }

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

        Fitness.getHistoryClient(context, account)
                .insertData(bodyFatDataSet)
                .addOnSuccessListener(data -> Log.d("BodyFat", "Successfully inserted body fat data"))
                .addOnFailureListener(e -> Log.e("BodyFat", "Failed to insert body fat data", e));
    }

    public void readBodyFatData(Context context, GoogleSignInAccount googleSignInAccount, long startTime, long endTime, OnSuccessListener<DataReadResponse> listener) {
        Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
                .addOnSuccessListener(listener)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure here
                    }
                });
    }
}
