package com.example.healthcoach.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.recordingapi.Hydration;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;


public class HydrationViewModel extends ViewModel {
    private final MutableLiveData<Float> totalWaterIntake = new MutableLiveData<>(0f);
    private Hydration repository;

    public void setRepository(Hydration repository) {
        this.repository = repository;
    }

    public LiveData<Float> getTotalWaterIntake() {
        return totalWaterIntake;
    }

    public void addWater(float intake, long startTime, long endTime) {
        repository.insertWaterIntake(intake, startTime, endTime);
        totalWaterIntake.setValue(totalWaterIntake.getValue() + intake);
    }

    public void initialize(Context context) {
        this.repository = new Hydration(context);  // Initialize it here
    }


    public void fetchHydrationData(long startTime, long endTime) {
        repository.readHydrationData(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                float totalHydration = 0;
                for (DataSet dataSet : dataReadResponse.getDataSets()) {
                    for (DataPoint point : dataSet.getDataPoints()) {
                        for (Field field : point.getDataType().getFields()) {
                            float hydrationValue = point.getValue(field).asFloat();
                            totalHydration += hydrationValue;
                        }
                    }
                }
                totalWaterIntake.postValue(totalHydration);
            }
        });
    }
}
