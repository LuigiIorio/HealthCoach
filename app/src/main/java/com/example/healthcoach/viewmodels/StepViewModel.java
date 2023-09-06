package com.example.healthcoach.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StepViewModel extends ViewModel {
    private MutableLiveData<Integer> steps;

    public MutableLiveData<Integer> getSteps() {
        if (steps == null) {
            steps = new MutableLiveData<Integer>();
        }
        return steps;
    }

    public void setSteps(int stepCount) {
        steps.setValue(stepCount);
    }
}
