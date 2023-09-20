package com.example.healthcoach.models;

public class GoogleFitDailyData {
    private int steps;
    private float calories;
    private float hydration;

    public GoogleFitDailyData(int steps, float calories, float hydration) {
        this.steps = steps;
        this.calories = calories;
        this.hydration = hydration;
    }

    public GoogleFitDailyData() {}

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getHydration() {
        return hydration;
    }

    public void setHydration(float hydration) {
        this.hydration = hydration;
    }
}

