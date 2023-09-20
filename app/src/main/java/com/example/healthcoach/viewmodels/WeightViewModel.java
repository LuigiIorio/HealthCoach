package com.example.healthcoach.viewmodels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;



public class WeightViewModel extends AndroidViewModel {

    private Context context;
    private MutableLiveData<String> weightError = new MutableLiveData<>();
    private MutableLiveData<Boolean> weightSuccess = new MutableLiveData<>();
    private MutableLiveData<Float> latestWeight = new MutableLiveData<>();
    public static final int REQUEST_OAUTH_REQUEST_CODE = 1004;
    private DataSource weightDataSource;

    public WeightViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
        this.weightDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_WEIGHT)
                .setType(DataSource.TYPE_RAW)
                .build();
    }


    public LiveData<String> getWeightError() {
        return weightError;
    }

    public LiveData<Boolean> getWeightSuccess() {
        return weightSuccess;
    }

    public LiveData<Float> getLatestWeight() {
        return latestWeight;
    }

    public void validateAndSubmitWeight(Context context, String weightInput, long startTime, long endTime) {
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
                    insertWeightDataOrSignInIfNeeded(context, weight, startTime, endTime);
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
    public void insertWeightDataOrSignInIfNeeded(Context context, float weightValue, long startTime, long endTime) {
        if (!isUserSignedIn(context)) {
            promptSignIn(context);
        } else if (!hasFitnessPermission(context)) {
            requestFitnessPermission(context);
        } else {
            insertWeightData(weightValue, startTime, endTime);
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


    public void insertWeightData(float weightValue, long startTime, long endTime) {
        Log.d("WeightViewModel", "Attempting to insert weight data");
        DataPoint weightDataPoint = DataPoint.create(weightDataSource)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

        weightDataPoint.getValue(Field.FIELD_WEIGHT).setFloat(weightValue);

        DataSet weightDataSet = DataSet.create(weightDataSource);
        weightDataSet.add(weightDataPoint);

        DataUpdateRequest request = new DataUpdateRequest.Builder()
                .setDataSet(weightDataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .updateData(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("WeightViewModel", "Successfully inserted weight data");
                        weightSuccess.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("WeightViewModel", "Failed to insert weight data", e);
                        weightError.setValue("Failed to insert weight data");
                    }
                });
    }


    public void fetchLatestWeight(long startTime, long endTime) {
        Log.d("WeightViewModel", "Attempting to fetch latest weight");
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Log.d("WeightViewModel", "Successfully fetched latest weight");
                        DataSet dataSet = dataReadResponse.getDataSet(DataType.TYPE_WEIGHT);
                        if (!dataSet.getDataPoints().isEmpty()) {
                            DataPoint lastDataPoint = dataSet.getDataPoints().get(0);
                            float lastWeight = lastDataPoint.getValue(Field.FIELD_WEIGHT).asFloat();
                            latestWeight.setValue(lastWeight);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("WeightViewModel", "Failed to fetch latest weight data", e);
                        weightError.setValue("Failed to fetch latest weight data");
                    }
                });
    }


}
