package com.example.healthcoach.fragments;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        // Initialize ViewModels
        userProfileViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        // Set up Gender Spinner
        Spinner genderSpinner = view.findViewById(R.id.genderSpinner);
        String[] genderArray = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genderArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Observe and set Gender
        userProfileViewModel.getGender().observe(getViewLifecycleOwner(), gender -> {
            int position = genderAdapter.getPosition(gender);
            if (position != -1) {
                genderSpinner.setSelection(position);
            }
        });

        // Gender Spinner Selection Listener
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userProfileViewModel.setGender(genderArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });

        // Set up Age NumberPicker
        NumberPicker ageNumberPicker = view.findViewById(R.id.ageNumberPicker);
        ageNumberPicker.setMinValue(18);
        ageNumberPicker.setMaxValue(100);

        // Observe and set Age
        userProfileViewModel.getAge().observe(getViewLifecycleOwner(), ageNumberPicker::setValue);
        ageNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> userProfileViewModel.setAge(newVal));

        // Weight Input field initialization
        weightEditText = view.findViewById(R.id.weightEditText);

        // Button for inserting weight data to Google Fit
        Button insertToGoogleFitButton = view.findViewById(R.id.insertToGoogleFitButton);
        insertToGoogleFitButton.setOnClickListener(v -> {
            String weightString = weightEditText.getText().toString().trim();
            if(!weightString.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightString);
                    weightViewModel.insertWeightData(getContext(), weight); // Assuming this method is part of WeightViewModel to insert weight to Google Fit
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Weight field cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

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
}
