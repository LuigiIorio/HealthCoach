package com.example.healthcoach.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.interfaces.HeartRateRepository;
import com.example.healthcoach.recordingapi.HeartRateBPM;

public class HeartRateViewModel extends ViewModel {
    private MutableLiveData<Float> heartRateLiveData = new MutableLiveData<>();
    private HeartRateRepository heartRateRepository;
    private Context context;


    public HeartRateViewModel(HeartRateRepository repository, Context context) {
        this.heartRateRepository = repository;
        this.context = context;
        fetchLatestHeartRate();
    }

    public LiveData<Float> getHeartRateLiveData() {
        return heartRateLiveData;
    }

    public void fetchLatestHeartRate() {
        heartRateRepository.getLatestHeartRate(context, new HeartRateBPM.OnHeartRateFetchListener() {
            @Override
            public void onSuccess(float bpm) {
                heartRateLiveData.setValue(bpm);
            }

            @Override
            public void onFailure(String error) {
                // Handle the error here, perhaps with a separate LiveData to display error messages.
            }
        });
    }

    public LiveData<Float> getHeartRate() {
        return heartRateLiveData;
    }
}
