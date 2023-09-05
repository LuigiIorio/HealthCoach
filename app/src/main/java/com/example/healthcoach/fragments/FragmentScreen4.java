package com.example.healthcoach.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.HeightViewModel;
import com.example.healthcoach.viewmodels.WeightViewModel;


public class FragmentScreen4 extends Fragment {

    // Global variables for Weight
    private WeightViewModel weightViewModel;
    private EditText weightEditText;

    // Global variables for Height
    private HeightViewModel heightViewModel;
    private EditText heightEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        // Initialize WeightViewModel
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        // Weight Input field initialization
        weightEditText = view.findViewById(R.id.weightEditText);

        // Button for submitting weight data
        setupWeightSubmitButton(view);

        // Observe any weight errors
        weightViewModel.getWeightError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        // Observe weight submission success
        weightViewModel.getWeightSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Weight submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize HeightViewModel
        heightViewModel = new ViewModelProvider(this).get(HeightViewModel.class);

        // Height Input field initialization
        heightEditText = view.findViewById(R.id.heightEditText);

        // Button for submitting height data
        setupHeightSubmitButton(view);

        // Observe any height errors
        heightViewModel.getHeightError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        // Observe height submission success
        heightViewModel.getHeightSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Height submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupWeightSubmitButton(View view) {
        Button submitWeightButton = view.findViewById(R.id.submitWeightButton);
        submitWeightButton.setOnClickListener(v -> {
            String weightString = weightEditText.getText().toString().trim();
            weightViewModel.validateAndSubmitWeight(getContext(), weightString);
        });
    }

    private void setupHeightSubmitButton(View view) {
        Button submitHeightButton = view.findViewById(R.id.submitHeightButton);
        submitHeightButton.setOnClickListener(v -> {
            String heightString = heightEditText.getText().toString().trim();
            heightViewModel.validateAndSubmitHeight(getActivity(), heightString);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handling for weight data
        if (requestCode == WeightViewModel.REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String weightString = weightEditText.getText().toString().trim();
                if (!weightString.isEmpty()) {
                    try {
                        float weight = Float.parseFloat(weightString);
                        weightViewModel.insertWeightData(getContext(), weight);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        // Handling for height data
        if (requestCode == HeightViewModel.REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String heightString = heightEditText.getText().toString().trim();
                if (!heightString.isEmpty()) {
                    try {
                        float height = Float.parseFloat(heightString);
                        heightViewModel.insertHeightData(getContext(), height);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
