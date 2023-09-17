package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.recordingapi.Hydration;


public class HydrationViewModel extends ViewModel {
    private final MutableLiveData<Float> totalWaterIntake = new MutableLiveData<>(0f);
    private Hydration repository;  // Changed the type to Hydration

    public HydrationViewModel() {}  // Empty constructor

    public HydrationViewModel(Hydration repository) {
        this.repository = repository;  // Changed the type to Hydration
    }

    public void setRepository(Hydration repository) {  // Changed the type to Hydration
        this.repository = repository;
    }

    public LiveData<Float> getTotalWaterIntake() {
        return totalWaterIntake;
    }

    public void addWater(float intake, long startTime, long endTime) {
        totalWaterIntake.setValue(totalWaterIntake.getValue() + intake);
        if (repository != null) {
            repository.insertWaterIntake(intake, startTime, endTime); // Insert into data source directly from ViewModel
        }
    }



}
