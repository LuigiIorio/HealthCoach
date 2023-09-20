package com.example.healthcoach.viewmodels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.recordingapi.StepCount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class StepViewModel extends ViewModel {

    private MutableLiveData<Integer> steps = new MutableLiveData<>();

    public MutableLiveData<Integer> getSteps() {
        return steps;
    }

    public void setSteps(int stepsCount) {
        steps.postValue(stepsCount);
    }

    public void startRecordingSteps(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (googleSignInAccount != null) {
            RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);
            recordingClient.subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("StepViewModel", "Successfully subscribed to recording steps");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("StepViewModel", "Failed to subscribe to recording steps", e);
                    });
        } else {
            Log.e("StepViewModel", "User is not signed in");
        }
    }



    public void stopRecordingSteps(Context context) {
        GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        RecordingClient recordingClient = Fitness.getRecordingClient(context, googleSignInAccount);

        recordingClient.unsubscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(aVoid -> {
                    // Successfully unsubscribed to recording steps
                })
                .addOnFailureListener(e -> {
                    // Handle failure here
                });
    }

    public void readStepsForRange(long startTime, long endTime, Context context) {
        StepCount stepCount = new StepCount(context, GoogleSignIn.getLastSignedInAccount(context), (FragmentActivity) context);
        stepCount.readStepsForRange(startTime, endTime, new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {
                int totalSteps = 0;
                if (dataReadResponse.getBuckets().size() > 0) {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        DataSet dataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                        for (DataPoint point : dataSet.getDataPoints()) {
                            for (Field field : point.getDataType().getFields()) {
                                int steps = point.getValue(field).asInt();
                                totalSteps += steps;
                            }
                        }
                    }
                }
                setSteps(totalSteps);
            }
        });
    }



}
