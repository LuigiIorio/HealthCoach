package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthcoach.recordingapi.BodyFat;
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
    private final MutableLiveData<Float> bodyFatData = new MutableLiveData<>(0f); // Replace totalBodyFat with bodyFatData

    public BodyFatViewModel() {
        this.bodyFat = new BodyFat();
    }


    public void fetchBodyFatData(Context context, GoogleSignInAccount googleSignInAccount, Date selectedDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        long startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis();

        bodyFat.readBodyFatData(context, googleSignInAccount, startTime, endTime, new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                float latestBodyFat = 0;
                long latestTime = 0;
                for (DataSet dataSet : dataReadResponse.getDataSets()) {
                    for (DataPoint point : dataSet.getDataPoints()) {
                        for (Field field : point.getDataType().getFields()) {
                            float bodyFatValue = point.getValue(field).asFloat();
                            long endTime = point.getEndTime(TimeUnit.MILLISECONDS);
                            if (endTime > latestTime) {
                                latestTime = endTime;
                                latestBodyFat = bodyFatValue;
                            }
                        }
                    }
                }
                bodyFatData.postValue(latestBodyFat);
            }
        });
    }

    public void insertBodyFat(Context context, float bodyFatPercentage) {
        bodyFat.insertBodyFatData(context, bodyFatPercentage, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("BodyFat", "Body fat data inserted successfully");
                bodyFatData.setValue(bodyFatData.getValue() + bodyFatPercentage);
            }
        });
    }



    public MutableLiveData<Float> getBodyFatData() {
        return bodyFatData;
    }
}
