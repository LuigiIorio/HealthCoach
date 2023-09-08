package com.example.healthcoach.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthcoach.recordingapi.BodyFat;

public class BodyFatViewModel extends ViewModel {

    private final MutableLiveData<Float> totalBodyFat = new MutableLiveData<>(0f);
    private BodyFat bodyFatRepo;

    public BodyFatViewModel() {
        bodyFatRepo = new BodyFat();
    }

    public void insertBodyFat(Context context, float bodyFatPercentage) {
        bodyFatRepo.insertBodyFatData(context, bodyFatPercentage);
        totalBodyFat.setValue(totalBodyFat.getValue() + bodyFatPercentage);
    }

    public MutableLiveData<Float> getTotalBodyFat() {
        return totalBodyFat;
    }
}
