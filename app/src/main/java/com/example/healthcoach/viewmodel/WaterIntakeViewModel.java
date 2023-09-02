package com.example.healthcoach.viewmodel;

import androidx.lifecycle.ViewModel;

public class WaterIntakeViewModel extends ViewModel {
    private float totalWaterIntake = 0;

    public float getTotalWaterIntake() {
        return totalWaterIntake;
    }

    public void addWater(float waterAmount) {
        this.totalWaterIntake += waterAmount;
    }
}
