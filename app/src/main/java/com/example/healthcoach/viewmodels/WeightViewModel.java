package com.example.healthcoach.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.concurrent.TimeUnit;



public class WeightViewModel extends ViewModel {
    private MutableLiveData<String> weightError = new MutableLiveData<>();
    private MutableLiveData<Boolean> weightSuccess = new MutableLiveData<>();

    public LiveData<String> getWeightError() {
        return weightError;
    }

    public LiveData<Boolean> getWeightSuccess() {
        return weightSuccess;
    }


    public void validateAndSubmitWeight(Context context, String weightInput) {
        if (!weightInput.isEmpty()) {
            try {
                int weight = Integer.parseInt(weightInput);
                if (weight < 30 || weight > 130) {
                    weightError.setValue("Please enter a weight between 30 and 130.");
                } else {
                    insertWeightData(context, (float) weight);
                    weightSuccess.setValue(true);
                }
            } catch (NumberFormatException e) {
                weightError.setValue("Please enter a valid weight.");
            }
        } else {
            weightError.setValue("Weight field cannot be empty.");
        }
    }


    public void insertWeightData(Context context, float weightValue) {
        // Fetch the last signed-in account
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        if (googleSignInAccount == null) {
            Log.e("Weight", "No account signed in.");
            return;
        }

        // Define a data source (you can adjust the app package name and name accordingly)
        DataSource dataSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_WEIGHT)
                .setAppPackageName(context)
                .setStreamName("Weight measurements")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Use HistoryClient to insert data
        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);

        // Define a data set with weight data points
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(System.currentTimeMillis(), System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(weightValue);
        dataSet.add(dataPoint);

        // Insert the data into Google Fit
        historyClient.insertData(dataSet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Weight", "Weight data successfully inserted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Weight", "Failed to insert weight data", e);
                    }
                });
    }



}
