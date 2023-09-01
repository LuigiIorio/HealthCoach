package com.example.healthcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;


public class FragmentScreen1 extends Fragment {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private GoogleSignInAccount googleSignInAccount;

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

        // Initialize GoogleSignInAccount
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HYDRATION)
                .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions);

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
                        totalWaterIntake = waterIntake + totalWaterIntake;
                    }

                    // Update the water intake text view
                    waterIntakeTextView.setText(String.valueOf(totalWaterIntake) + " ml");
                } else {
                    // Handle the case where googleSignInAccount is null
                }
            }
        });

        // Set retain instance
        setRetainInstance(true);

        if (savedInstanceState != null) {
            // Restore the total water intake from the saved instance state
            totalWaterIntake = savedInstanceState.getFloat("totalWaterIntake");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the water intake text view to the total water intake
        waterIntakeTextView.setText(String.valueOf(totalWaterIntake) + " ml");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the total water intake to the saved instance state
        outState.putFloat("totalWaterIntake", totalWaterIntake);
    }
}
