package com.example.healthcoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FragmentScreen2 extends Fragment {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private FitnessOptions fitnessOptions;
    private AutoCompleteTextView activityAutoCompleteTextView;
    private Button submitButton;
    private TextView journalTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentScreen2", "onCreateView: Inflating layout");
        View view = inflater.inflate(R.layout.fragment_screen2, container, false);

        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();

        activityAutoCompleteTextView = view.findViewById(R.id.activityAutoCompleteTextView);
        String[] activitySuggestions = getResources().getStringArray(R.array.activity_suggestions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, activitySuggestions);
        activityAutoCompleteTextView.setAdapter(adapter);

        submitButton = view.findViewById(R.id.submitButton);
        journalTextView = view.findViewById(R.id.journalTextView);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FragmentScreen2", "Submit button clicked");
                String selectedActivity = activityAutoCompleteTextView.getText().toString();
                if (!selectedActivity.isEmpty()) {
                    insertActivitySegment(selectedActivity);
                    activityAutoCompleteTextView.setText(""); // Clear the input field
                }
            }
        });

        if (googleSignInAccount != null && !GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            Log.d("FragmentScreen2", "Requesting Google Fit permissions");
            requestGoogleFitPermissions();
        } else {
            Log.d("FragmentScreen2", "Reading activity data");
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

    private void insertActivitySegment(String selectedActivity) {
        if (!selectedActivity.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long startTime = endTime - TimeUnit.MINUTES.toMillis(30); // Example: 30 minutes ago

            Session session = new Session.Builder()
                    .setName("MyActivitySession")
                    .setIdentifier("activitySegment" + startTime) // Unique identifier for session
                    .setActivity(selectedActivity)
                    .setStartTime(startTime, TimeUnit.MILLISECONDS)
                    .setEndTime(endTime, TimeUnit.MILLISECONDS)
                    .build();

            SessionInsertRequest sessionInsertRequest = new SessionInsertRequest.Builder()
                    .setSession(session)
                    .build();

            Fitness.getSessionsClient(requireContext(), Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(requireContext())))
                    .insertSession(sessionInsertRequest)
                    .addOnSuccessListener(sessionID -> {
                        updateJournal(selectedActivity);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }

    private void updateJournal(String activity) {
        String existingJournalText = journalTextView.getText().toString();
        String newJournalText = existingJournalText + "\n" + activity;
        journalTextView.setText(newJournalText);
    }

    private void readActivityData() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .build();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext());

        if (googleSignInAccount != null) {
            Fitness.getHistoryClient(requireContext(), googleSignInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(dataReadResponse -> {
                        processActivityData(dataReadResponse.getDataSets());
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }

    private void processActivityData(List<DataSet> dataSets) {
        for (DataSet dataSet : dataSets) {
            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                long startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
                long endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
                int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();

                // Here you can update your journal with the activity information
                // You might want to convert activityType to a human-readable string
            }
        }
    }
}
