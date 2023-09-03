package com.example.healthcoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcoach.fitnessactivity.ActivityTypes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class FragmentScreen2 extends Fragment {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private FitnessOptions fitnessOptions;
    private Spinner activitySpinner;
    private Button submitButton;
    private TextView journalTextView;
    private Button retrieveHydrationDataButton;
    private TextView hydrationTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);

        fitnessOptions = FitnessOptions.builder()
                .addDataType(com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();

        activitySpinner = view.findViewById(R.id.activitySpinner);
        submitButton = view.findViewById(R.id.submitButton);
        journalTextView = view.findViewById(R.id.journalTextView);
        retrieveHydrationDataButton = view.findViewById(R.id.retrieveHydrationDataButton);
        hydrationTextView = view.findViewById(R.id.hydrationTextView);

        retrieveHydrationDataButton.setOnClickListener(v -> retrieveHydrationData());

        submitButton.setOnClickListener(v -> {
            String selectedActivity = activitySpinner.getSelectedItem().toString();
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());
            if (googleSignInAccount != null) {
                long endTime = System.currentTimeMillis();
                long startTime = endTime - TimeUnit.MINUTES.toMillis(30);
                Session session = new Session.Builder()
                        .setName("MyActivitySession")
                        .setIdentifier("activitySegment" + startTime)
                        .setActivity(selectedActivity)
                        .setStartTime(startTime, TimeUnit.MILLISECONDS)
                        .setEndTime(endTime, TimeUnit.MILLISECONDS)
                        .build();
                SessionInsertRequest sessionInsertRequest = new SessionInsertRequest.Builder()
                        .setSession(session)
                        .build();
                Fitness.getSessionsClient(requireContext(), googleSignInAccount)
                        .insertSession(sessionInsertRequest)
                        .addOnSuccessListener(sessionID -> {
                            updateJournal(selectedActivity);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (googleSignInAccount != null && !GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            requestGoogleFitPermissions();
        } else {
            readActivityData();
        }

        return view;
    }

    private void requestGoogleFitPermissions() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (googleSignInAccount != null) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    googleSignInAccount,
                    fitnessOptions);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                readActivityData();
            }
        }
    }

    private void updateJournal(String activity) {
        String existingJournalText = journalTextView.getText().toString();
        String newJournalText = existingJournalText + "\n" + activity;
        journalTextView.setText(newJournalText);
    }

    private void readActivityData() {
        Calendar calendar = Calendar.getInstance();
        long endTimestamp = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTimestamp = calendar.getTimeInMillis();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTimestamp, endTimestamp, TimeUnit.MILLISECONDS)
                .read(com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT)
                .build();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (googleSignInAccount != null) {
            Fitness.getHistoryClient(requireContext(), googleSignInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(dataReadResponse -> {
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (com.google.android.gms.fitness.data.DataPoint dataPoint : dataSet.getDataPoints()) {
                                long startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
                                long endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
                                int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();
                                String activityName = ActivityTypes.getActivityName(activityType);
                                updateJournal(activityName);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error Reading Activity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void retrieveHydrationData() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (googleSignInAccount != null) {
            // Updated the data source for hydration data
            DataSource hydrationDataSource = new DataSource.Builder()
                    .setAppPackageName(requireContext().getPackageName())
                    .setDataType(com.google.android.gms.fitness.data.DataType.TYPE_HYDRATION)
                    .setType(DataSource.TYPE_RAW)
                    .build();

            // Updated the time range for hydration data retrieval
            long endTime = System.currentTimeMillis();
            long startTime = endTime - TimeUnit.DAYS.toMillis(1);

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .read(hydrationDataSource)
                    .build();

            Fitness.getHistoryClient(requireContext(), googleSignInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(dataReadResponse -> {
                        float totalHydrationVolume = 0f;
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (com.google.android.gms.fitness.data.DataPoint dataPoint : dataSet.getDataPoints()) {
                                float hydrationVolume = dataPoint.getValue(Field.FIELD_VOLUME).asFloat();
                                totalHydrationVolume += hydrationVolume;
                            }
                        }
                        updateHydrationTextView(totalHydrationVolume);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error Reading Hydration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateHydrationTextView(float hydrationVolume) {
        String hydrationText = "Total Hydration Volume: " + hydrationVolume + " ml";
        hydrationTextView.setText(hydrationText);
    }
}
