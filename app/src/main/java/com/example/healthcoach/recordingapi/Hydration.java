package com.example.healthcoach.recordingapi;

import android.content.Context;
import android.util.Log;

import com.example.healthcoach.interfaces.WaterIntakeRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Hydration implements WaterIntakeRepository {

    private GoogleSignInAccount googleSignInAccount;
    private DataSource dataSource;
    private Context context;
    private HistoryClient historyClient;

    public Hydration(Context context) {
        this.context = context;

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        dataSource = new DataSource.Builder()
                .setAppPackageName(context.getPackageName())
                .setDataType(DataType.TYPE_HYDRATION)
                .setType(DataSource.TYPE_RAW)
                .build();

        historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
    }

    @Override
    public void insertWaterIntake(float waterIntake) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        DataPoint dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_VOLUME, waterIntake)
                .setTimeInterval(cal.getTimeInMillis(), cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();

        DataSet dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build();

        historyClient.insertData(dataSet)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Hydration", "Hydration data inserted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Hydration", "Failed to insert hydration data", e);
                });
    }
}
