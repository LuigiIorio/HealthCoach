package com.example.healthcoach.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.healthcoach.R;
import com.example.healthcoach.viewmodels.HomeViewModel;
import com.example.healthcoach.viewmodels.WeightViewModel;



public class FragmentScreen4 extends Fragment {

    private HomeViewModel userProfileViewModel;
    private WeightViewModel weightViewModel;
    private EditText weightEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        // Initialize ViewModels
        userProfileViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        // Setup Gender Spinner
        setupGenderSpinner(view);

        // Setup Age NumberPicker
        setupAgeNumberPicker(view);

        // Weight Input field initialization
        weightEditText = view.findViewById(R.id.weightEditText);

        // Button for inserting weight data to Google Fit
        setupWeightInsertButton(view);

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

        return view;
    }

    private void setupGenderSpinner(View view) {
        Spinner genderSpinner = view.findViewById(R.id.genderSpinner);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Male", "Female"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        userProfileViewModel.getGender().observe(getViewLifecycleOwner(), gender -> {
            int position = genderAdapter.getPosition(gender);
            if (position != -1) {
                genderSpinner.setSelection(position);
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userProfileViewModel.setGender(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void setupAgeNumberPicker(View view) {
        NumberPicker ageNumberPicker = view.findViewById(R.id.ageNumberPicker);
        ageNumberPicker.setMinValue(18);
        ageNumberPicker.setMaxValue(100);

        userProfileViewModel.getAge().observe(getViewLifecycleOwner(), ageNumberPicker::setValue);

        ageNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> userProfileViewModel.setAge(newVal));
    }

    private void setupWeightInsertButton(View view) {
        Button insertToGoogleFitButton = view.findViewById(R.id.insertToGoogleFitButton);
        insertToGoogleFitButton.setOnClickListener(v -> {
            String weightString = weightEditText.getText().toString().trim();
            weightViewModel.validateAndSubmitWeight(getContext(), weightString);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }
}
