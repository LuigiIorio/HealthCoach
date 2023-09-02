package com.example.healthcoach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthcoach.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class FragmentScreen1 extends Fragment {

    // Declare the Google Fit components
    private GoogleSignInAccount googleSignInAccount;
    private HistoryClient historyClient;

    private EditText waterIntakeEditText;
    private Button addWaterIntakeButton;
    private TextView waterIntakeTextView;
    private float totalWaterIntake;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen1, container, false);

        waterIntakeEditText = view.findViewById(R.id.waterIntakeEditText);
        addWaterIntakeButton = view.findViewById(R.id.addWaterIntakeButton);
        waterIntakeTextView = view.findViewById(R.id.waterIntakeTextView);

        // Initialize GoogleSignInAccount and HistoryClient
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_WRITE)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions);
        historyClient = Fitness.getHistoryClient(getActivity(), googleSignInAccount);

        // Set retain instance
        setRetainInstance(true);

        if (savedInstanceState != null) {
            // Restore the totalWaterIntake from the savedInstanceState
            totalWaterIntake = savedInstanceState.getFloat("totalWaterIntake");

            // Update the water intake text view with the restored value
            waterIntakeTextView.setText(String.valueOf(totalWaterIntake) + " ml");
        } else {
            // If savedInstanceState is null, initialize totalWaterIntake to 0
            totalWaterIntake = 0;
        }

        addWaterIntakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if googleSignInAccount is not null
                if (googleSignInAccount != null) {
                    // Get the water intake amount from the edit text
                    String waterIntakeString = waterIntakeEditText.getText().toString();

                    // If the water intake string is empty, do nothing
                    if (waterIntakeString.isEmpty()) {
                        return;
                    }

                    // Try to convert the water intake string to a float
                    float waterIntake;
                    try {
                        waterIntake = Float.parseFloat(waterIntakeString);
                    } catch (NumberFormatException e) {
                        // The water intake string is not a float, show an error message
                        waterIntakeTextView.setText("Please enter a valid number");
                        return;
                    }

                    // Update the total water intake
                    synchronized (this) {
                        totalWaterIntake += waterIntake;
                    }

                    // Update the water intake text view
                    waterIntakeTextView.setText(String.valueOf(totalWaterIntake) + " ml");

                    // Insert the water intake data into Google Fit
                    insertWaterIntakeData(waterIntake);

                } else {
                    // Handle the case where googleSignInAccount is null
                }
            }
        });

        return view;
    }

    // Insert water intake data into Google Fit
    private void insertWaterIntakeData(float waterIntake) {
        // Check if the user is signed in
        if (googleSignInAccount != null) {
            // Create a data source for hydration
            DataSource dataSource = new DataSource.Builder()
                    .setAppPackageName(getActivity())
                    .setDataType(DataType.TYPE_HYDRATION)
                    .setType(DataSource.TYPE_RAW)
                    .build();

            // Create a DataPoint for water intake
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());

            DataPoint dataPoint = DataPoint.builder(dataSource)
                    .setField(Field.FIELD_VOLUME, waterIntake) // Field.FIELD_VOLUME represents hydration volume
                    .setTimeInterval(cal.getTimeInMillis(), cal.getTimeInMillis(), TimeUnit.MILLISECONDS)
                    .build();

            // Create a DataSet and add the DataPoint
            DataSet dataSet = DataSet.builder(dataSource)
                    .add(dataPoint)
                    .build();

            // Insert water intake data into Google Fit
            historyClient.insertData(dataSet)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Handle success
                            Log.d("FragmentScreen1", "Water intake data inserted successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Log.e("FragmentScreen1", "Failed to insert water intake data", e);
                            // You can display an error message to the user here
                        }
                    });

        } else {
            // The user is not signed in, prompt them to sign in
            // TODO: Prompt the user to sign in
        }
    }

    // Save the totalWaterIntake value in savedInstanceState
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("totalWaterIntake", totalWaterIntake);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore the total water intake from the saved instance state
            totalWaterIntake = savedInstanceState.getFloat("totalWaterIntake");

            // Update the water intake text view
            waterIntakeTextView.setText(String.valueOf(totalWaterIntake) + " ml");
        }
    }
}
