package com.example.healthcoach.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.interfaces.HeartRateRepository;
import com.example.healthcoach.recordingapi.HeartRateBPM;

import java.util.Random;


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
        Random random = new Random();
        float randomBpm = random.nextInt(31) + 50; // Generates a random value between 50 and 80
        heartRateLiveData.setValue(randomBpm);
    }


    public LiveData<Float> getHeartRate() {
        return heartRateLiveData;
    }
}
