package com.example.healthcoach.recordingapi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.healthcoach.interfaces.WaterIntakeRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Hydration implements WaterIntakeRepository {

    public static final int REQUEST_OAUTH_REQUEST_CODE = 1001;

    private Context context;
    private GoogleSignInAccount googleSignInAccount;
    private FitnessOptions fitnessOptions;
    private DataSource hydrationDataSource;

    public Hydration(Context context) {
        this.context = context;
        setupHydration();
    }

    private void setupHydration() {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();

        refreshGoogleSignInAccount();
        setupHydrationDataSource();
    }

    public void refreshGoogleSignInAccount() {
        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);
    }

    private void setupHydrationDataSource() {
        hydrationDataSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_HYDRATION)
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(context)
                .build();
    }

    public void insertWaterIntake(float waterIntake) {
        if (!isUserSignedIn()) {
            Log.e("Hydration", "User is not signed in. Can't insert data.");
            promptSignIn();
            return;
        }

        if (!hasNecessaryPermissions()) {
            Log.e("Hydration", "No permissions to insert data. Requesting permissions.");
            requestPermissions();
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        DataPoint dataPoint = DataPoint.builder(hydrationDataSource)
                .setField(Field.FIELD_VOLUME, waterIntake)
                .setTimeInterval(cal.getTimeInMillis(), cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(hydrationDataSource)
                .add(dataPoint)
                .build();

        Fitness.getHistoryClient(context, googleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Hydration", "Hydration data inserted successfully");
                })
                .addOnFailureListener(e -> {
                    if (e instanceof ApiException && ((ApiException) e).getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                        Log.e("Hydration", "Sign-in required. Prompting user for sign-in.");
                        promptSignIn();
                    } else {
                        Log.e("Hydration", "Failed to insert hydration data: " + e.getMessage(), e);
                    }
                });
    }

    private boolean isUserSignedIn() {
        refreshGoogleSignInAccount();
        return googleSignInAccount != null;
    }

    private boolean hasNecessaryPermissions() {
        return GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions);
    }

    private void promptSignIn() {
        if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            Log.e("Hydration", "Requesting permissions for Google Fit.");
            GoogleSignIn.requestPermissions(
                    (Activity) context,
                    REQUEST_OAUTH_REQUEST_CODE,
                    googleSignInAccount,
                    fitnessOptions
            );
        }
    }

    private void requestPermissions() {
        GoogleSignIn.requestPermissions(
                (Activity) context,
                REQUEST_OAUTH_REQUEST_CODE,
                googleSignInAccount,
                fitnessOptions
        );
    }
}
