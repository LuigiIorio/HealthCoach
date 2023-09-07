package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DistanceDeltaViewModel extends ViewModel {

    private final MutableLiveData<Float> totalDistance = new MutableLiveData<>(0f);

    public LiveData<Float> getTotalDistance() {
        return totalDistance;
    }

    public void queryTodayDistance(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    float sum = 0;
                    for (DataSet dataSet : dataReadResponse.getDataSets()) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                sum += dp.getValue(field).asFloat();
                            }
                        }
                    }
                    totalDistance.setValue(sum);
                })
                .addOnFailureListener(e -> Log.e("DistanceDeltaViewModel", "Failed to read data", e));
    }


}

