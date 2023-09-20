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

    public void queryTodayCalories(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
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
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)  // Added this line for bucketing
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
                    totalCalories.setValue(new Calories(sum));
                })
                .addOnFailureListener(e -> Log.e("CaloriesViewModel", "Failed to read data", e));
    }

    public void fetchCaloriesData(Context context, GoogleSignInAccount googleSignInAccount, Date selectedDate) {
        long startTime = selectedDate.getTime();
        long endTime = startTime + 24 * 60 * 60 * 1000;

        Calories caloriesInstance = new Calories(context, googleSignInAccount);
        caloriesInstance.readCaloriesData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                float sum = 0;
                for (DataSet dataSet : dataReadResponse.getDataSets()) {
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




    public void fetchCalories(Context context) {
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(dataSet -> {
                    float totalCaloriesValue = dataSet.isEmpty() ? 0 : dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                    totalCalories.setValue(new Calories(totalCaloriesValue));
                })
                .addOnFailureListener(e -> totalCalories.setValue(new Calories(0f)));
    }




}
