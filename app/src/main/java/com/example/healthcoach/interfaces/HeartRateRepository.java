package com.example.healthcoach.interfaces;

import android.content.Context;

import com.example.healthcoach.recordingapi.HeartRateBPM;

public interface HeartRateRepository {

    void getLatestHeartRate(Context context, HeartRateBPM.OnHeartRateFetchListener listener);

}
