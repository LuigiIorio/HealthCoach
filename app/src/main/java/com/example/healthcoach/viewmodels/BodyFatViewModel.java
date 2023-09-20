package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthcoach.recordingapi.BodyFat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class BodyFatViewModel extends ViewModel {

    private BodyFat bodyFat;
    private final MutableLiveData<Float> bodyFatData = new MutableLiveData<>(0f);

    /**
     * Fetches the latest body fat data for a specified date from Google Fit.
     * Updates the LiveData object bodyFatData upon successful retrieval.
     *
     * @param selectedDate The date for which to fetch body fat data.
     */

    public void fetchBodyFatData(Date selectedDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis();

        bodyFat.getLatestBodyFatForDay(startTime, endTime, new OnSuccessListener<Float>() {
            @Override
            public void onSuccess(Float latestBodyFat) {
                bodyFatData.postValue(latestBodyFat);
            }
        });
    }


    public MutableLiveData<Float> getBodyFatData() {
        return bodyFatData;
    }

    /**
     * Inserts body fat data into Google Fit and updates the LiveData object.
     * Logs success or failure of the insertion operation.
     *
     * @param bodyFatPercentage Body fat percentage to insert.
     * @param startTime The start time of the record in milliseconds.
     * @param endTime The end time of the record in milliseconds.
     */

    public void insertBodyFat(float bodyFatPercentage, long startTime, long endTime) {
        boolean success = bodyFat.insertBodyFat(bodyFatPercentage, startTime, endTime);
        if (success) {
            Log.d("BodyFat", "Body fat data inserted successfully");
            bodyFatData.setValue(bodyFatData.getValue() + bodyFatPercentage);
        } else {
            Log.e("BodyFat", "Failed to insert body fat data");
        }
    }

    public void initialize(Context context) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        this.bodyFat = new BodyFat(context, account);  // Initialize it here
    }

}
