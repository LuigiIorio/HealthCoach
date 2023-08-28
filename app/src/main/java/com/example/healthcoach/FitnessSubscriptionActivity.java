package com.example.healthcoach;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcoach.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FitnessSubscriptionActivity extends AppCompatActivity {

    private static final String TAG = "FitnessSubscription";
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_subscription);

        textView = findViewById(R.id.textView);

        subscribeToFitnessData();
    }

    private void subscribeToFitnessData() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build();

        Fitness.getRecordingClient(this, googleSignInAccount)
                .subscribe(dataSource)
                .addOnSuccessListener(unused -> {
                    Log.i(TAG, "Successfully subscribed!");
                    // Query for step count data and update UI
                    queryStepCountAndDisplay();
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "There was a problem subscribing.", e));
    }

    private void queryStepCountAndDisplay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Task<DataReadResponse> responseTask = Fitness.getHistoryClient(this, account)
                .readData(readRequest);

        responseTask.addOnSuccessListener(dataReadResponse -> {
            int totalSteps = 0;
            for (DataPoint dataPoint : dataReadResponse.getDataSet(DataType.TYPE_STEP_COUNT_DELTA).getDataPoints()) {
                int steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
                totalSteps += steps;
            }
            updateStepCount(totalSteps);
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read step count data", e);
        });
    }

    private void updateStepCount(int stepCount) {
        textView.setText("Recorded Steps: " + stepCount);
    }



}
