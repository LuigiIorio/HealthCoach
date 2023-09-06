package com.example.healthcoach.viewmodels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;



public class WeightViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> weightError = new MutableLiveData<>();
    private MutableLiveData<Boolean> weightSuccess = new MutableLiveData<>();

    public static final int REQUEST_OAUTH_REQUEST_CODE = 1004;

    public WeightViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<String> getWeightError() {
        return weightError;
    }

    public LiveData<Boolean> getWeightSuccess() {
        return weightSuccess;
    }

    public void validateAndSubmitWeight(Context context, String weightInput) {
        if (!weightInput.isEmpty()) {
            try {
                float weight = Float.parseFloat(weightInput);
                if (weight < 30 || weight > 300) {
                    weightError.setValue("Please enter a weight between 30 and 300 kg.");
                } else {
                    if (!isUserSignedIn(context)) {
                        promptSignIn(context);
                        return;
                    }
                    insertWeightDataOrSignInIfNeeded(context, weight);
                    weightSuccess.setValue(true);
                }
            } catch (NumberFormatException e) {
                weightError.setValue("Please enter a valid weight.");
            }
        } else {
            weightError.setValue("Weight field cannot be empty.");
        }
    }

    private boolean isUserSignedIn(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        return googleSignInAccount != null;
    }

    private void promptSignIn(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                (Activity) context,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
        );
    }

    public void insertWeightDataOrSignInIfNeeded(Context context, float weightValue) {
        if (!isUserSignedIn(context)) {
            promptSignIn(context);
        } else if (!hasFitnessPermission(context)) {
            requestFitnessPermission(context);
        } else {
            insertWeightData(context, weightValue);
        }
    }

    private boolean hasFitnessPermission(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        return GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions);
    }

    private void requestFitnessPermission(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                (Activity) context,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
        );
    }

    public void insertWeightData(Context context, float weightInKg) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        if (GoogleSignIn.hasPermissions(lastSignedInAccount, fitnessOptions)) {
            DataSource weightSource = new DataSource.Builder()
                    .setDataType(DataType.TYPE_WEIGHT)
                    .setAppPackageName(context.getPackageName())
                    .setStreamName("user weight")
                    .setType(DataSource.TYPE_RAW)
                    .build();

            DataPoint weightPoint = DataPoint.builder(weightSource)
                    .setField(Field.FIELD_WEIGHT, weightInKg)
                    .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .build();

            DataSet dataSet = DataSet.builder(weightSource)
                    .add(weightPoint)
                    .build();

            Fitness.getHistoryClient(context, lastSignedInAccount)
                    .insertData(dataSet)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Weight data inserted!"))
                    .addOnFailureListener(e -> Log.e(TAG, "There was a problem inserting the weight data.", e));
        } else {
            Log.e(TAG, "Permission for weight is not granted.");
        }
    }

    public void fetchLatestWeight(long startTime, long endTime, OnSuccessListener<DataReadResponse> onSuccessListener) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .enableServerQueries()
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to read weight data.", e);
                });
    }


}
