package com.example.healthcoach.viewmodels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;



public class StepViewModel extends ViewModel {
    private MutableLiveData<Integer> steps;

    private static final String TAG = "StepViewModel";

    public MutableLiveData<Integer> getSteps() {
        if (steps == null) {
            steps = new MutableLiveData<Integer>();
        }
        return steps;
    }

    public void setSteps(int stepCount) {
        steps.setValue(stepCount);
    }

    public void fetchDailySteps(Context context) {
        Log.i(TAG, "Fetching daily steps");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account == null) {
            Log.w(TAG, "GoogleSignInAccount is null in fetchDailySteps");
            return;
        }

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        Fitness.getHistoryClient(context, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        if (!dataSet.isEmpty()) {
                            int totalSteps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                            steps.setValue(totalSteps);
                            Log.i(TAG, "Fetched steps: " + totalSteps);
                        } else {
                            steps.setValue(0);
                            Log.i(TAG, "No steps data found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem getting steps.", e);
                    }
                });
    }



}
