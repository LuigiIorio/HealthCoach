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


    /**
     * Adds a new water intake record via the Hydration repository.
     * The LiveData object is updated with the new total water intake.
     *
     * @param intake The amount of water intake.
     * @param startTime The time when the water was consumed.
     * @param endTime The end time for the water intake event.
     */
    public void addWater(float intake, long startTime, long endTime) {
        repository.insertWaterIntake(intake, startTime, endTime);
        totalWaterIntake.setValue(totalWaterIntake.getValue() + intake);
    }

    public void initialize(Context context) {
        this.repository = new Hydration(context);  // Initialize it here
    }


    /**
     * Fetches the hydration data for a specific time range via the Hydration repository.
     * Aggregates the total water intake and updates the LiveData object.
     *
     * @param startTime The start time for the range.
     * @param endTime The end time for the range.
     */
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
