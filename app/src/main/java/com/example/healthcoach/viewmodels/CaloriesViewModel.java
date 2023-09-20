package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.recordingapi.Calories;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
public class CaloriesViewModel extends ViewModel {
    private final MutableLiveData<Calories> totalCalories = new MutableLiveData<>(new Calories(0f));
    public LiveData<Calories> getTotalCalories() {
        return totalCalories;
    }

    public void fetchCaloriesData(Context context, GoogleSignInAccount googleSignInAccount, Date selectedDate) {
        long startTime = selectedDate.getTime();
        long endTime = startTime + 24 * 60 * 60 * 1000;

        Calories caloriesInstance = new Calories(context, googleSignInAccount);
        caloriesInstance.readCaloriesData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                float sum = 0;
                for (Bucket bucket : dataReadResponse.getBuckets()) {
                    DataSet dataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED);
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        for (Field field : dp.getDataType().getFields()) {
                            sum += dp.getValue(field).asFloat();
                        }
                    }
                }
                totalCalories.setValue(new Calories(sum));
            }
        });
    }

}
