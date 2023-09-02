package com.example.healthcoach.viewmodel;

import com.example.healthcoach.interfaces.WaterIntakeRepository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaterIntakeViewModel extends ViewModel {
    private final MutableLiveData<Float> totalWaterIntake = new MutableLiveData<>(0f);
    private WaterIntakeRepository repository;

    public WaterIntakeViewModel(WaterIntakeRepository repository) {
        this.repository = repository;
    }

    public LiveData<Float> getTotalWaterIntake() {
        return totalWaterIntake;
    }

    public void addWater(float intake) {
        totalWaterIntake.setValue(totalWaterIntake.getValue() + intake);
        repository.insertWaterIntake(intake); // Insert into data source directly from ViewModel
    }
}
