package com.example.healthcoach.viewmodels;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.concurrent.TimeUnit;

public class HeightViewModel extends ViewModel {
    private MutableLiveData<String> heightError = new MutableLiveData<>();
    private MutableLiveData<Boolean> heightSuccess = new MutableLiveData<>();

    public static final int REQUEST_OAUTH_REQUEST_CODE = 1003;

    public LiveData<String> getHeightError() {
        return heightError;
    }

    public LiveData<Boolean> getHeightSuccess() {
        return heightSuccess;
    }

    public void validateAndSubmitHeight(Context context, String heightInput) {
        if (!heightInput.isEmpty()) {
            try {
                float height = Float.parseFloat(heightInput);
                if (height < 50 || height > 250) {
                    heightError.setValue("Please enter a height between 50 and 250 cm.");
                } else {
                    if (!isUserSignedIn(context)) {
                        promptSignIn(context);
                        return;
                    }
                    insertHeightDataOrSignInIfNeeded(context, height);
                    heightSuccess.setValue(true);
                }
            } catch (NumberFormatException e) {
                heightError.setValue("Please enter a valid height.");
            }
        } else {
            heightError.setValue("Height field cannot be empty.");
        }
    }

    private boolean isUserSignedIn(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        return googleSignInAccount != null;
    }

    private void promptSignIn(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                (Activity) context,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
        );
    }

    public void insertHeightDataOrSignInIfNeeded(Context context, float heightValue) {
        if (!isUserSignedIn(context)) {
            promptSignIn(context);
        } else if (!hasFitnessPermission(context)) {
            requestFitnessPermission(context);
        } else {
            insertHeightData(context, heightValue);
        }
    }

    private boolean hasFitnessPermission(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        return GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions);
    }

    private void requestFitnessPermission(Context context) {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignIn.requestPermissions(
                (Activity) context,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
        );
    }

    public void insertHeightData(Context context, float heightInCm) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .build();

        if (GoogleSignIn.hasPermissions(lastSignedInAccount, fitnessOptions)) {
            float heightInMeters = heightInCm / 100;

            DataSource heightSource = new DataSource.Builder()
                    .setDataType(DataType.TYPE_HEIGHT)
                    .setAppPackageName(context.getPackageName())
                    .setStreamName("user height")
                    .setType(DataSource.TYPE_RAW)
                    .build();

            DataPoint heightPoint = DataPoint.builder(heightSource)
                    .setField(Field.FIELD_HEIGHT, heightInMeters)
                    .setTimestamp(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .build();

            DataSet dataSet = DataSet.builder(heightSource)
                    .add(heightPoint)
                    .build();

            Fitness.getHistoryClient(context, lastSignedInAccount)
                    .insertData(dataSet)
                    .addOnSuccessListener(unused -> Log.d(TAG, "Height data inserted!"))
                    .addOnFailureListener(e -> Log.e(TAG, "There was a problem inserting the height data.", e));
        } else {
            Log.e(TAG, "Permission for height is not granted.");
        }
    }
}
